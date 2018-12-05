/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.cedar.schemas.avro.psi;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class DataAccessAnalysis extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -9063057855966545042L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DataAccessAnalysis\",\"namespace\":\"org.cedar.psi.common.avro\",\"fields\":[{\"name\":\"dataAccessExists\",\"type\":\"boolean\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private boolean dataAccessExists;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public DataAccessAnalysis() {}

  /**
   * All-args constructor.
   * @param dataAccessExists The new value for dataAccessExists
   */
  public DataAccessAnalysis(Boolean dataAccessExists) {
    this.dataAccessExists = dataAccessExists;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public Object get(int field$) {
    switch (field$) {
    case 0: return dataAccessExists;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
    case 0: dataAccessExists = (Boolean)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'dataAccessExists' field.
   * @return The value of the 'dataAccessExists' field.
   */
  public Boolean getDataAccessExists() {
    return dataAccessExists;
  }


  /**
   * Creates a new DataAccessAnalysis RecordBuilder.
   * @return A new DataAccessAnalysis RecordBuilder
   */
  public static DataAccessAnalysis.Builder newBuilder() {
    return new DataAccessAnalysis.Builder();
  }

  /**
   * Creates a new DataAccessAnalysis RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new DataAccessAnalysis RecordBuilder
   */
  public static DataAccessAnalysis.Builder newBuilder(DataAccessAnalysis.Builder other) {
    return new DataAccessAnalysis.Builder(other);
  }

  /**
   * Creates a new DataAccessAnalysis RecordBuilder by copying an existing DataAccessAnalysis instance.
   * @param other The existing instance to copy.
   * @return A new DataAccessAnalysis RecordBuilder
   */
  public static DataAccessAnalysis.Builder newBuilder(DataAccessAnalysis other) {
    return new DataAccessAnalysis.Builder(other);
  }

  /**
   * RecordBuilder for DataAccessAnalysis instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<DataAccessAnalysis>
    implements org.apache.avro.data.RecordBuilder<DataAccessAnalysis> {

    private boolean dataAccessExists;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(DataAccessAnalysis.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.dataAccessExists)) {
        this.dataAccessExists = data().deepCopy(fields()[0].schema(), other.dataAccessExists);
        fieldSetFlags()[0] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing DataAccessAnalysis instance
     * @param other The existing instance to copy.
     */
    private Builder(DataAccessAnalysis other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.dataAccessExists)) {
        this.dataAccessExists = data().deepCopy(fields()[0].schema(), other.dataAccessExists);
        fieldSetFlags()[0] = true;
      }
    }

    /**
      * Gets the value of the 'dataAccessExists' field.
      * @return The value.
      */
    public Boolean getDataAccessExists() {
      return dataAccessExists;
    }

    /**
      * Sets the value of the 'dataAccessExists' field.
      * @param value The value of 'dataAccessExists'.
      * @return This builder.
      */
    public DataAccessAnalysis.Builder setDataAccessExists(boolean value) {
      validate(fields()[0], value);
      this.dataAccessExists = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'dataAccessExists' field has been set.
      * @return True if the 'dataAccessExists' field has been set, false otherwise.
      */
    public boolean hasDataAccessExists() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'dataAccessExists' field.
      * @return This builder.
      */
    public DataAccessAnalysis.Builder clearDataAccessExists() {
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    public DataAccessAnalysis build() {
      try {
        DataAccessAnalysis record = new DataAccessAnalysis();
        record.dataAccessExists = fieldSetFlags()[0] ? this.dataAccessExists : (Boolean) defaultValue(fields()[0]);
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
