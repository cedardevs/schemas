package org.cedar.schemas.avro.util;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvroUtils {

  public static Map<String, Object> avroToMap(GenericRecord record) {
    return avroToMap(record, false);
  }

  public static Map<String, Object> avroToMap(GenericRecord record, boolean recurse) {
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
    return collection
        .stream()
        .map((it) -> it instanceof GenericRecord ? avroToMap((GenericRecord) it, recurse) : it)
        .collect(Collectors.toList());
  }

  public static String avroToJson(GenericRecord record) {
    return GenericData.get().toString(record);
  }

  public static <T extends GenericRecord> T jsonToAvro(String json, Schema schema) throws IOException {
    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, json);
    return new SpecificDatumReader<T>(schema).read(null, decoder);
  }

  public static <T extends GenericRecord> T jsonToAvro(InputStream json, Schema schema) throws IOException {
    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, json);
    return new SpecificDatumReader<T>(schema).read(null, decoder);
  }

}
