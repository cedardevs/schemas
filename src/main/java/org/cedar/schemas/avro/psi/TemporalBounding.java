/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.cedar.schemas.avro.psi;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class TemporalBounding extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -260393029859333506L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TemporalBounding\",\"namespace\":\"org.cedar.psi.common.avro\",\"fields\":[{\"name\":\"beginDate\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"beginIndeterminate\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"endDate\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"endIndeterminate\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"instant\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"instantIndeterminate\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"description\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private String beginDate;
   private String beginIndeterminate;
   private String endDate;
   private String endIndeterminate;
   private String instant;
   private String instantIndeterminate;
   private String description;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public TemporalBounding() {}

  /**
   * All-args constructor.
   * @param beginDate The new value for beginDate
   * @param beginIndeterminate The new value for beginIndeterminate
   * @param endDate The new value for endDate
   * @param endIndeterminate The new value for endIndeterminate
   * @param instant The new value for instant
   * @param instantIndeterminate The new value for instantIndeterminate
   * @param description The new value for description
   */
  public TemporalBounding(String beginDate, String beginIndeterminate, String endDate, String endIndeterminate, String instant, String instantIndeterminate, String description) {
    this.beginDate = beginDate;
    this.beginIndeterminate = beginIndeterminate;
    this.endDate = endDate;
    this.endIndeterminate = endIndeterminate;
    this.instant = instant;
    this.instantIndeterminate = instantIndeterminate;
    this.description = description;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public Object get(int field$) {
    switch (field$) {
    case 0: return beginDate;
    case 1: return beginIndeterminate;
    case 2: return endDate;
    case 3: return endIndeterminate;
    case 4: return instant;
    case 5: return instantIndeterminate;
    case 6: return description;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
    case 0: beginDate = (String)value$; break;
    case 1: beginIndeterminate = (String)value$; break;
    case 2: endDate = (String)value$; break;
    case 3: endIndeterminate = (String)value$; break;
    case 4: instant = (String)value$; break;
    case 5: instantIndeterminate = (String)value$; break;
    case 6: description = (String)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'beginDate' field.
   * @return The value of the 'beginDate' field.
   */
  public String getBeginDate() {
    return beginDate;
  }


  /**
   * Gets the value of the 'beginIndeterminate' field.
   * @return The value of the 'beginIndeterminate' field.
   */
  public String getBeginIndeterminate() {
    return beginIndeterminate;
  }


  /**
   * Gets the value of the 'endDate' field.
   * @return The value of the 'endDate' field.
   */
  public String getEndDate() {
    return endDate;
  }


  /**
   * Gets the value of the 'endIndeterminate' field.
   * @return The value of the 'endIndeterminate' field.
   */
  public String getEndIndeterminate() {
    return endIndeterminate;
  }


  /**
   * Gets the value of the 'instant' field.
   * @return The value of the 'instant' field.
   */
  public String getInstant() {
    return instant;
  }


  /**
   * Gets the value of the 'instantIndeterminate' field.
   * @return The value of the 'instantIndeterminate' field.
   */
  public String getInstantIndeterminate() {
    return instantIndeterminate;
  }


  /**
   * Gets the value of the 'description' field.
   * @return The value of the 'description' field.
   */
  public String getDescription() {
    return description;
  }


  /**
   * Creates a new TemporalBounding RecordBuilder.
   * @return A new TemporalBounding RecordBuilder
   */
  public static TemporalBounding.Builder newBuilder() {
    return new TemporalBounding.Builder();
  }

  /**
   * Creates a new TemporalBounding RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new TemporalBounding RecordBuilder
   */
  public static TemporalBounding.Builder newBuilder(TemporalBounding.Builder other) {
    return new TemporalBounding.Builder(other);
  }

  /**
   * Creates a new TemporalBounding RecordBuilder by copying an existing TemporalBounding instance.
   * @param other The existing instance to copy.
   * @return A new TemporalBounding RecordBuilder
   */
  public static TemporalBounding.Builder newBuilder(TemporalBounding other) {
    return new TemporalBounding.Builder(other);
  }

  /**
   * RecordBuilder for TemporalBounding instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<TemporalBounding>
    implements org.apache.avro.data.RecordBuilder<TemporalBounding> {

    private String beginDate;
    private String beginIndeterminate;
    private String endDate;
    private String endIndeterminate;
    private String instant;
    private String instantIndeterminate;
    private String description;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(TemporalBounding.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.beginDate)) {
        this.beginDate = data().deepCopy(fields()[0].schema(), other.beginDate);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.beginIndeterminate)) {
        this.beginIndeterminate = data().deepCopy(fields()[1].schema(), other.beginIndeterminate);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.endDate)) {
        this.endDate = data().deepCopy(fields()[2].schema(), other.endDate);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.endIndeterminate)) {
        this.endIndeterminate = data().deepCopy(fields()[3].schema(), other.endIndeterminate);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.instant)) {
        this.instant = data().deepCopy(fields()[4].schema(), other.instant);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.instantIndeterminate)) {
        this.instantIndeterminate = data().deepCopy(fields()[5].schema(), other.instantIndeterminate);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.description)) {
        this.description = data().deepCopy(fields()[6].schema(), other.description);
        fieldSetFlags()[6] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing TemporalBounding instance
     * @param other The existing instance to copy.
     */
    private Builder(TemporalBounding other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.beginDate)) {
        this.beginDate = data().deepCopy(fields()[0].schema(), other.beginDate);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.beginIndeterminate)) {
        this.beginIndeterminate = data().deepCopy(fields()[1].schema(), other.beginIndeterminate);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.endDate)) {
        this.endDate = data().deepCopy(fields()[2].schema(), other.endDate);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.endIndeterminate)) {
        this.endIndeterminate = data().deepCopy(fields()[3].schema(), other.endIndeterminate);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.instant)) {
        this.instant = data().deepCopy(fields()[4].schema(), other.instant);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.instantIndeterminate)) {
        this.instantIndeterminate = data().deepCopy(fields()[5].schema(), other.instantIndeterminate);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.description)) {
        this.description = data().deepCopy(fields()[6].schema(), other.description);
        fieldSetFlags()[6] = true;
      }
    }

    /**
      * Gets the value of the 'beginDate' field.
      * @return The value.
      */
    public String getBeginDate() {
      return beginDate;
    }

    /**
      * Sets the value of the 'beginDate' field.
      * @param value The value of 'beginDate'.
      * @return This builder.
      */
    public TemporalBounding.Builder setBeginDate(String value) {
      validate(fields()[0], value);
      this.beginDate = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'beginDate' field has been set.
      * @return True if the 'beginDate' field has been set, false otherwise.
      */
    public boolean hasBeginDate() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'beginDate' field.
      * @return This builder.
      */
    public TemporalBounding.Builder clearBeginDate() {
      beginDate = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'beginIndeterminate' field.
      * @return The value.
      */
    public String getBeginIndeterminate() {
      return beginIndeterminate;
    }

    /**
      * Sets the value of the 'beginIndeterminate' field.
      * @param value The value of 'beginIndeterminate'.
      * @return This builder.
      */
    public TemporalBounding.Builder setBeginIndeterminate(String value) {
      validate(fields()[1], value);
      this.beginIndeterminate = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'beginIndeterminate' field has been set.
      * @return True if the 'beginIndeterminate' field has been set, false otherwise.
      */
    public boolean hasBeginIndeterminate() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'beginIndeterminate' field.
      * @return This builder.
      */
    public TemporalBounding.Builder clearBeginIndeterminate() {
      beginIndeterminate = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'endDate' field.
      * @return The value.
      */
    public String getEndDate() {
      return endDate;
    }

    /**
      * Sets the value of the 'endDate' field.
      * @param value The value of 'endDate'.
      * @return This builder.
      */
    public TemporalBounding.Builder setEndDate(String value) {
      validate(fields()[2], value);
      this.endDate = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'endDate' field has been set.
      * @return True if the 'endDate' field has been set, false otherwise.
      */
    public boolean hasEndDate() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'endDate' field.
      * @return This builder.
      */
    public TemporalBounding.Builder clearEndDate() {
      endDate = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'endIndeterminate' field.
      * @return The value.
      */
    public String getEndIndeterminate() {
      return endIndeterminate;
    }

    /**
      * Sets the value of the 'endIndeterminate' field.
      * @param value The value of 'endIndeterminate'.
      * @return This builder.
      */
    public TemporalBounding.Builder setEndIndeterminate(String value) {
      validate(fields()[3], value);
      this.endIndeterminate = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'endIndeterminate' field has been set.
      * @return True if the 'endIndeterminate' field has been set, false otherwise.
      */
    public boolean hasEndIndeterminate() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'endIndeterminate' field.
      * @return This builder.
      */
    public TemporalBounding.Builder clearEndIndeterminate() {
      endIndeterminate = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'instant' field.
      * @return The value.
      */
    public String getInstant() {
      return instant;
    }

    /**
      * Sets the value of the 'instant' field.
      * @param value The value of 'instant'.
      * @return This builder.
      */
    public TemporalBounding.Builder setInstant(String value) {
      validate(fields()[4], value);
      this.instant = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'instant' field has been set.
      * @return True if the 'instant' field has been set, false otherwise.
      */
    public boolean hasInstant() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'instant' field.
      * @return This builder.
      */
    public TemporalBounding.Builder clearInstant() {
      instant = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'instantIndeterminate' field.
      * @return The value.
      */
    public String getInstantIndeterminate() {
      return instantIndeterminate;
    }

    /**
      * Sets the value of the 'instantIndeterminate' field.
      * @param value The value of 'instantIndeterminate'.
      * @return This builder.
      */
    public TemporalBounding.Builder setInstantIndeterminate(String value) {
      validate(fields()[5], value);
      this.instantIndeterminate = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'instantIndeterminate' field has been set.
      * @return True if the 'instantIndeterminate' field has been set, false otherwise.
      */
    public boolean hasInstantIndeterminate() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'instantIndeterminate' field.
      * @return This builder.
      */
    public TemporalBounding.Builder clearInstantIndeterminate() {
      instantIndeterminate = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'description' field.
      * @return The value.
      */
    public String getDescription() {
      return description;
    }

    /**
      * Sets the value of the 'description' field.
      * @param value The value of 'description'.
      * @return This builder.
      */
    public TemporalBounding.Builder setDescription(String value) {
      validate(fields()[6], value);
      this.description = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'description' field has been set.
      * @return True if the 'description' field has been set, false otherwise.
      */
    public boolean hasDescription() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'description' field.
      * @return This builder.
      */
    public TemporalBounding.Builder clearDescription() {
      description = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    public TemporalBounding build() {
      try {
        TemporalBounding record = new TemporalBounding();
        record.beginDate = fieldSetFlags()[0] ? this.beginDate : (String) defaultValue(fields()[0]);
        record.beginIndeterminate = fieldSetFlags()[1] ? this.beginIndeterminate : (String) defaultValue(fields()[1]);
        record.endDate = fieldSetFlags()[2] ? this.endDate : (String) defaultValue(fields()[2]);
        record.endIndeterminate = fieldSetFlags()[3] ? this.endIndeterminate : (String) defaultValue(fields()[3]);
        record.instant = fieldSetFlags()[4] ? this.instant : (String) defaultValue(fields()[4]);
        record.instantIndeterminate = fieldSetFlags()[5] ? this.instantIndeterminate : (String) defaultValue(fields()[5]);
        record.description = fieldSetFlags()[6] ? this.description : (String) defaultValue(fields()[6]);
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
