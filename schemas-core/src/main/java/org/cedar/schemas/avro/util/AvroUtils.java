package org.cedar.schemas.avro.util;

import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificFixed;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class AvroUtils {
  static private Logger log = LoggerFactory.getLogger(AvroUtils.class);

  public static Map<String, Object> avroToMap(GenericRecord record) {
    return avroToMap(record, true);
  }

  public static Map<String, Object> avroToMap(GenericRecord record, boolean recurse) {
    if (record == null) { return null; }
    log.debug("Transforming a record of type [" + record.getClass() + "] to a map, with recursive [" + recurse + "]");
    List<Schema.Field> fields = record.getSchema().getFields();
    Map<String, Object> result = new LinkedHashMap<>(fields.size());
    fields.forEach((field) -> {
      String name = field.name();
      Object value = record.get(name);
      if (recurse && value instanceof GenericRecord) {
        result.put(name, avroToMap((GenericRecord)value, recurse));
      }
      else if (recurse && value instanceof Collection) {
        result.put(name, avroCollectionToList((Collection)value, recurse));
      }
      else if (recurse && value instanceof Map) {
        LinkedHashMap<String, Object> transformedMap = new LinkedHashMap<>();
        ((Map) value).forEach((k, v) -> {
          Object mappedValue = v instanceof GenericRecord ? avroToMap((GenericRecord) v, recurse) : v;
          transformedMap.put(k.toString(), mappedValue);
        });
        result.put(name, transformedMap);
      }
      else {
        result.put(name, value);
      }
    });
    return result;
  }

  public static List<Object> avroCollectionToList(Collection<Object> collection) {
    return avroCollectionToList(collection, false);
  }

  public static List<Object> avroCollectionToList(Collection<Object> collection, boolean recurse) {
    if (collection == null) { return null; }
    log.debug("Transforming a collection of type [" + collection.getClass() + "] to a list of maps, with recursive [" + recurse + "]");
    return collection
        .stream()
        .map((it) -> it instanceof GenericRecord ? avroToMap((GenericRecord) it, recurse) : it)
        .collect(Collectors.toList());
  }

  public static String avroToJson(GenericRecord record) {
    if (record == null) { return null; }
    return GenericData.get().toString(record);
  }

  public static <T extends GenericRecord> T jsonToAvro(String json, Schema schema) throws IOException {
    if (json == null) { return null; }
    return jsonToAvro(new ByteArrayInputStream(json.getBytes()), schema);
  }

  public static <T extends GenericRecord> T jsonToAvro(InputStream json, Schema schema) throws IOException {
    if (json == null) { return null; }
    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, json);
    return new SpecificDatumReader<T>(schema).read(null, decoder);
  }

  public static <T extends GenericRecord> T jsonToAvroLenient(String json, Schema schema) throws IOException, ClassNotFoundException {
    if (json == null) { return null; }
    return jsonToAvroLenient(new ByteArrayInputStream(json.getBytes()), schema);
  }

  public static <T extends GenericRecord> T jsonToAvroLenient(InputStream json, Schema schema) throws IOException, ClassNotFoundException {
    if (json == null) { return null; }
    Map map = new ObjectMapper().readValue(json, Map.class);
    return mapToAvro(map, findAvroClass(schema.getFullName()));
  }

  public static <T extends IndexedRecord> T mapToAvro(Map input, Class<T> avroClass) {
    if (input == null) { return null; }
    log.debug("Transforming a type map to Avro type [" + avroClass + "]");
    try {
      T instance = avroClass.getDeclaredConstructor().newInstance();
      Schema schema = instance.getSchema();
      List<Schema.Field> fields = schema.getFields();
      log.debug("Schema "+schema.getName()+" has fields "+fields);
      fields.forEach(f -> {
        Object value = input.containsKey(f.name()) ? input.get(f.name()) : f.defaultVal();
        log.debug("Input "+(input.containsKey(f.name()) ? "contains" : "does NOT contain")+" field "+f.name());
        instance.put(f.pos(), coerceValueForSchema(value, f.schema()));
      });
      return instance;
    }
    catch (Exception e) {
      throw new IllegalStateException("Unable to build Avro object of type " + avroClass, e);
    }
  }

  public static Object coerceValueForSchema(Object value, Schema schema) {
    log.debug("coercing value [" + value + "] for schema [" + schema.getFullName() + "] for schema type [" +
            schema.getType() + "]");
    switch(schema.getType()) {
      case RECORD:
        if (value instanceof Map) {
          try {
            Class<? extends IndexedRecord> recordClass = findAvroClass(schema.getFullName());
            return mapToAvro((Map) value, recordClass);
          }
          catch(Exception e) {
            log.debug("Unable to build record of type " + schema.getFullName(), e);
            throw new UnsupportedOperationException("Unable to build record of type " + schema.getFullName(), e);
          }
        }
        break;

      case ENUM:
        try {
          Class<? extends Enum> enumClass = findEnum(schema.getFullName());
          if (enumClass.isAssignableFrom(value.getClass())) {
            return value;
          }
          else {
            return Enum.valueOf(enumClass, value.toString());
          }
        }
        catch (Exception e) {
          log.debug("Unable to build enum of type " + schema.getFullName(), e);
          throw new UnsupportedOperationException("Unable to build enum of type " + schema.getFullName(), e);
        }

      case ARRAY:
        if (value instanceof List) {
          Schema elementType = schema.getElementType();
          return ((List) value).stream().map(v -> coerceValueForSchema(v, elementType)).collect(Collectors.toList());
        }
        break;

      case MAP:
        if (value instanceof Map) {
          Schema valueType = schema.getValueType();
          Map mapValue = (Map) value;
          Map result = new LinkedHashMap();
          mapValue.keySet().forEach(key -> result.put(key, coerceValueForSchema(mapValue.get(key), valueType)));
          return result;
        }
        break;

      case UNION:
        log.debug("Recursively trying to coerce the value into one of these schemas types: "+schema.getTypes());
        for (Schema type : schema.getTypes()) {
          try {
            return coerceValueForSchema(value, type);
          }
          catch (Exception e) {
            log.debug("tried and failed to coerce value for specific type " + type, e);
          }
        }

      case FIXED:
        if (value instanceof byte[]) {
          try {
            Class<? extends SpecificFixed> fixedClass = findAvroFixed(schema.getFullName());
            return fixedClass.getDeclaredConstructor(byte[].class).newInstance(value);
          }
          catch (Exception e) {
            log.debug("Unable to build fixed of type " + schema.getFullName(), e);
            throw new UnsupportedOperationException("Unable to build fixed of type " + schema.getFullName(), e);
          }
        }
        break;

      case STRING:
        return value.toString();

      case BYTES:
        if (value instanceof byte[]) {
          return value;
        }
        else if (value instanceof String) {
          return ((String) value).getBytes();
        }
        break;

      case INT:
        if (value instanceof Integer) {
          return value;
        }
        else if (value instanceof BigInteger) {
          return ((BigInteger) value).intValueExact();
        }
        if (value instanceof String) {
          return Integer.parseInt((String) value);
        }
        break;

      case LONG:
        if (value instanceof Long) {
          return value;
        }
        else if (value instanceof Integer) {
          return ((Integer) value).longValue();
        }
        else if (value instanceof BigInteger) {
          return ((BigInteger) value).longValueExact();
        }
        else if (value instanceof String) {
          return Long.parseLong((String) value);
        }
        break;

      case FLOAT:
        if (value instanceof Float) {
          return value;
        }
        else if (value instanceof BigDecimal) {
          Float result = ((BigDecimal) value).floatValue();
          if (!result.equals(Float.NEGATIVE_INFINITY) && !result.equals(Float.POSITIVE_INFINITY)) {
            return result;
          }
        }
        else if (value instanceof String) {
          return Float.parseFloat((String) value);
        }
        break;

      case DOUBLE:
        if (value instanceof Double) {
          return value;
        }
        else if (value instanceof Float) {
          return ((Float) value).doubleValue();
        }
        else if (value instanceof BigDecimal) {
          Double result = ((BigDecimal) value).doubleValue();
          if (!result.equals(Double.NEGATIVE_INFINITY) && !result.equals(Double.POSITIVE_INFINITY)) {
            return result;
          }
        }
        else if (value instanceof String) {
          return Double.parseDouble((String) value);
        }
        break;

      case BOOLEAN:
        if (value instanceof Boolean) {
          return value;
        }
        else if (value instanceof String) {
          return Boolean.parseBoolean((String) value);
        }
        break;

      case NULL:
        if (value == null || value instanceof JsonProperties.Null) {
          return null;
        }
        break;
    }
    throw new UnsupportedOperationException("Unable to coerce value [" + value + "] of type ["
        + (value == null ? "null" : value.getClass()) + "] for schema [" + schema.getFullName() + "]");
  }

  public static <T extends IndexedRecord> Class<T> findAvroClass(String className) throws ClassNotFoundException {
    Class clazz = AvroUtils.class.getClassLoader().loadClass(className);
    if (IndexedRecord.class.isAssignableFrom(clazz)) {
      return clazz;
    }
    else {
      log.debug("Class " + className + " is not an Avro IndexedRecord class");
      throw new IllegalArgumentException("Class " + className + " is not an Avro IndexedRecord class");
    }
  }

  public static <T extends Enum> Class<T> findEnum(String className) throws ClassNotFoundException {
    Class clazz = AvroUtils.class.getClassLoader().loadClass(className);
    if (clazz.isEnum()) {
      return clazz;
    }
    else {
      log.debug("Class " + className + " is not an enumeration");
      throw new IllegalArgumentException("Class " + className + " is not an enumeration");
    }
  }

  public static <T extends SpecificFixed> Class<T> findAvroFixed(String className) throws ClassNotFoundException {
    Class clazz = AvroUtils.class.getClassLoader().loadClass(className);
    if (SpecificFixed.class.isAssignableFrom(clazz)) {
      return clazz;
    }
    else {
      log.debug("Class " + className + " is not a fixed");
      throw new IllegalArgumentException("Class " + className + " is not a fixed");
    }
  }

}
