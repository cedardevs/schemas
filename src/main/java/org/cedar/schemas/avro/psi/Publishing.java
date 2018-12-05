/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.cedar.schemas.avro.psi;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
/** TBD */
@org.apache.avro.specific.AvroGenerated
public class Publishing extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -1536280192658776130L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Publishing\",\"namespace\":\"org.cedar.psi.common.avro\",\"doc\":\"TBD\",\"fields\":[{\"name\":\"isPrivate\",\"type\":\"boolean\",\"default\":false},{\"name\":\"until\",\"type\":[\"null\",\"long\"],\"default\":null,\"logicalType\":\"timestamp-millis\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private boolean isPrivate;
   private Long until;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Publishing() {}

  /**
   * All-args constructor.
   * @param isPrivate The new value for isPrivate
   * @param until The new value for until
   */
  public Publishing(Boolean isPrivate, Long until) {
    this.isPrivate = isPrivate;
    this.until = until;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public Object get(int field$) {
    switch (field$) {
    case 0: return isPrivate;
    case 1: return until;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
    case 0: isPrivate = (Boolean)value$; break;
    case 1: until = (Long)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'isPrivate' field.
   * @return The value of the 'isPrivate' field.
   */
  public Boolean getIsPrivate() {
    return isPrivate;
  }


  /**
   * Gets the value of the 'until' field.
   * @return The value of the 'until' field.
   */
  public Long getUntil() {
    return until;
  }


  /**
   * Creates a new Publishing RecordBuilder.
   * @return A new Publishing RecordBuilder
   */
  public static Publishing.Builder newBuilder() {
    return new Publishing.Builder();
  }

  /**
   * Creates a new Publishing RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Publishing RecordBuilder
   */
  public static Publishing.Builder newBuilder(Publishing.Builder other) {
    return new Publishing.Builder(other);
  }

  /**
   * Creates a new Publishing RecordBuilder by copying an existing Publishing instance.
   * @param other The existing instance to copy.
   * @return A new Publishing RecordBuilder
   */
  public static Publishing.Builder newBuilder(Publishing other) {
    return new Publishing.Builder(other);
  }

  /**
   * RecordBuilder for Publishing instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Publishing>
    implements org.apache.avro.data.RecordBuilder<Publishing> {

    private boolean isPrivate;
    private Long until;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(Publishing.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.isPrivate)) {
        this.isPrivate = data().deepCopy(fields()[0].schema(), other.isPrivate);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.until)) {
        this.until = data().deepCopy(fields()[1].schema(), other.until);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Publishing instance
     * @param other The existing instance to copy.
     */
    private Builder(Publishing other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.isPrivate)) {
        this.isPrivate = data().deepCopy(fields()[0].schema(), other.isPrivate);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.until)) {
        this.until = data().deepCopy(fields()[1].schema(), other.until);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'isPrivate' field.
      * @return The value.
      */
    public Boolean getIsPrivate() {
      return isPrivate;
    }

    /**
      * Sets the value of the 'isPrivate' field.
      * @param value The value of 'isPrivate'.
      * @return This builder.
      */
    public Publishing.Builder setIsPrivate(boolean value) {
      validate(fields()[0], value);
      this.isPrivate = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'isPrivate' field has been set.
      * @return True if the 'isPrivate' field has been set, false otherwise.
      */
    public boolean hasIsPrivate() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'isPrivate' field.
      * @return This builder.
      */
    public Publishing.Builder clearIsPrivate() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'until' field.
      * @return The value.
      */
    public Long getUntil() {
      return until;
    }

    /**
      * Sets the value of the 'until' field.
      * @param value The value of 'until'.
      * @return This builder.
      */
    public Publishing.Builder setUntil(Long value) {
      validate(fields()[1], value);
      this.until = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'until' field has been set.
      * @return True if the 'until' field has been set, false otherwise.
      */
    public boolean hasUntil() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'until' field.
      * @return This builder.
      */
    public Publishing.Builder clearUntil() {
      until = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public Publishing build() {
      try {
        Publishing record = new Publishing();
        record.isPrivate = fieldSetFlags()[0] ? this.isPrivate : (Boolean) defaultValue(fields()[0]);
        record.until = fieldSetFlags()[1] ? this.until : (Long) defaultValue(fields()[1]);
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
