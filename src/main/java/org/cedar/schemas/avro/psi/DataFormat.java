/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.cedar.schemas.avro.psi;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class DataFormat extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -2202261583501578560L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DataFormat\",\"namespace\":\"org.cedar.psi.common.avro\",\"fields\":[{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"version\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private String name;
   private String version;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public DataFormat() {}

  /**
   * All-args constructor.
   * @param name The new value for name
   * @param version The new value for version
   */
  public DataFormat(String name, String version) {
    this.name = name;
    this.version = version;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public Object get(int field$) {
    switch (field$) {
    case 0: return name;
    case 1: return version;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
    case 0: name = (String)value$; break;
    case 1: version = (String)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public String getName() {
    return name;
  }


  /**
   * Gets the value of the 'version' field.
   * @return The value of the 'version' field.
   */
  public String getVersion() {
    return version;
  }


  /**
   * Creates a new DataFormat RecordBuilder.
   * @return A new DataFormat RecordBuilder
   */
  public static DataFormat.Builder newBuilder() {
    return new DataFormat.Builder();
  }

  /**
   * Creates a new DataFormat RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new DataFormat RecordBuilder
   */
  public static DataFormat.Builder newBuilder(DataFormat.Builder other) {
    return new DataFormat.Builder(other);
  }

  /**
   * Creates a new DataFormat RecordBuilder by copying an existing DataFormat instance.
   * @param other The existing instance to copy.
   * @return A new DataFormat RecordBuilder
   */
  public static DataFormat.Builder newBuilder(DataFormat other) {
    return new DataFormat.Builder(other);
  }

  /**
   * RecordBuilder for DataFormat instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<DataFormat>
    implements org.apache.avro.data.RecordBuilder<DataFormat> {

    private String name;
    private String version;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(DataFormat.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.version)) {
        this.version = data().deepCopy(fields()[1].schema(), other.version);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing DataFormat instance
     * @param other The existing instance to copy.
     */
    private Builder(DataFormat other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.version)) {
        this.version = data().deepCopy(fields()[1].schema(), other.version);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public String getName() {
      return name;
    }

    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public DataFormat.Builder setName(String value) {
      validate(fields()[0], value);
      this.name = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public DataFormat.Builder clearName() {
      name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'version' field.
      * @return The value.
      */
    public String getVersion() {
      return version;
    }

    /**
      * Sets the value of the 'version' field.
      * @param value The value of 'version'.
      * @return This builder.
      */
    public DataFormat.Builder setVersion(String value) {
      validate(fields()[1], value);
      this.version = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'version' field has been set.
      * @return True if the 'version' field has been set, false otherwise.
      */
    public boolean hasVersion() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'version' field.
      * @return This builder.
      */
    public DataFormat.Builder clearVersion() {
      version = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public DataFormat build() {
      try {
        DataFormat record = new DataFormat();
        record.name = fieldSetFlags()[0] ? this.name : (String) defaultValue(fields()[0]);
        record.version = fieldSetFlags()[1] ? this.version : (String) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  private static final org.apache.avro.io.DatumWriter
    WRITER$ = new org.apache.avro.specific.SpecificDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  private static final org.apache.avro.io.DatumReader
    READER$ = new org.apache.avro.specific.SpecificDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
