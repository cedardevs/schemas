package org.cedar.schemas.avro.util

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import org.cedar.schemas.avro.psi.TemporalBounding
import org.cedar.schemas.avro.psi.TemporalBoundingAnalysis
import org.cedar.schemas.avro.psi.TimeRangeDescriptor
import org.cedar.schemas.avro.psi.ValidDescriptor

import java.time.temporal.ChronoUnit

@CompileStatic
class TemporalTestData {

  @Canonical
  static class TemporalSituation {
    String description
    TemporalBounding bounding
    TemporalBoundingAnalysis analysis

    String toString() { description }
  }

  static Map<String, TemporalSituation> situtations = [
      empty  : new TemporalSituation('undefined range',
          TemporalBounding.newBuilder().build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.UNDEFINED)
              .setBeginIndexable(true)
              .setBeginPrecision(null)
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString(null)
              .setEndDescriptor(ValidDescriptor.UNDEFINED)
              .setEndIndexable(true)
              .setEndPrecision(null)
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString(null)
              .setInstantDescriptor(ValidDescriptor.UNDEFINED)
              .setInstantIndexable(true)
              .setInstantPrecision(null)
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString(null)
              .setRangeDescriptor(TimeRangeDescriptor.UNDEFINED)
              .build()
      ),
      invalid: new TemporalSituation('invalid dates',
          TemporalBounding.newBuilder().setBeginDate('not a date').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.INVALID)
              .setBeginIndexable(false)
              .setBeginPrecision(null)
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString(null)
              .setEndDescriptor(ValidDescriptor.UNDEFINED)
              .setEndIndexable(true)
              .setEndPrecision(null)
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString(null)
              .setInstantDescriptor(ValidDescriptor.UNDEFINED)
              .setInstantIndexable(true)
              .setInstantPrecision(null)
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString(null)
              .setRangeDescriptor(TimeRangeDescriptor.INVALID)
              .build()
      ),
      backwards: new TemporalSituation('non-paleo bounded range begin greater than end',
          TemporalBounding.newBuilder().setBeginDate('2009-01-01').setEndDate('1900').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.VALID)
              .setBeginIndexable(true)
              .setBeginPrecision(ChronoUnit.DAYS.toString())
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString('2009-01-01T00:00:00Z')
              .setEndDescriptor(ValidDescriptor.VALID)
              .setEndIndexable(true)
              .setEndPrecision(ChronoUnit.YEARS.toString())
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString('1900-12-31T23:59:59Z')
              .setInstantDescriptor(ValidDescriptor.UNDEFINED)
              .setInstantIndexable(true)
              .setInstantPrecision(null)
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString(null)
              .setRangeDescriptor(TimeRangeDescriptor.BACKWARDS)
              .build()
      ),
      instantNano: new TemporalSituation('non-paleo instant with nanos precision',
          TemporalBounding.newBuilder().setInstant('2008-04-01T00:00:00Z').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.UNDEFINED)
              .setBeginIndexable(true)
              .setBeginPrecision(null)
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString(null)
              .setEndDescriptor(ValidDescriptor.UNDEFINED)
              .setEndIndexable(true)
              .setEndPrecision(null)
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString(null)
              .setInstantDescriptor(ValidDescriptor.VALID)
              .setInstantIndexable(true)
              .setInstantPrecision(ChronoUnit.NANOS.toString())
              .setInstantZoneSpecified('Z')
              .setInstantUtcDateTimeString('2008-04-01T00:00:00Z')
              .setRangeDescriptor(TimeRangeDescriptor.INSTANT)
              .build()
      ),
      instantDay: new TemporalSituation('non-paleo instant with days precision',
          TemporalBounding.newBuilder().setInstant('1999-12-31').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.UNDEFINED)
              .setBeginIndexable(true)
              .setBeginPrecision(null)
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString(null)
              .setEndDescriptor(ValidDescriptor.UNDEFINED)
              .setEndIndexable(true)
              .setEndPrecision(null)
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString(null)
              .setInstantDescriptor(ValidDescriptor.VALID)
              .setInstantIndexable(true)
              .setInstantPrecision(ChronoUnit.DAYS.toString())
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString('1999-12-31T00:00:00Z')
              .setRangeDescriptor(TimeRangeDescriptor.INSTANT)
              .build()
      ),
      instantYear: new TemporalSituation('non-paleo instant with years precision',
          TemporalBounding.newBuilder().setInstant('1999').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.UNDEFINED)
              .setBeginIndexable(true)
              .setBeginPrecision(null)
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString(null)
              .setEndDescriptor(ValidDescriptor.UNDEFINED)
              .setEndIndexable(true)
              .setEndPrecision(null)
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString(null)
              .setInstantDescriptor(ValidDescriptor.VALID)
              .setInstantIndexable(true)
              .setInstantPrecision(ChronoUnit.YEARS.toString())
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString('1999-01-01T00:00:00Z')
              .setRangeDescriptor(TimeRangeDescriptor.INSTANT)
              .build()
      ),
      instantPaleo: new TemporalSituation('paleo instant with years precision',
          TemporalBounding.newBuilder().setInstant('-1000000000').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.UNDEFINED)
              .setBeginIndexable(true)
              .setBeginPrecision(null)
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString(null)
              .setEndDescriptor(ValidDescriptor.UNDEFINED)
              .setEndIndexable(true)
              .setEndPrecision(null)
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString(null)
              .setInstantDescriptor(ValidDescriptor.VALID)
              .setInstantIndexable(false)
              .setInstantPrecision(ChronoUnit.YEARS.toString())
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString('-1000000000-01-01T00:00:00Z')
              .setRangeDescriptor(TimeRangeDescriptor.INSTANT)
              .build()
      ),
      ongoing: new TemporalSituation('ongoing range with second precision for begin',
          TemporalBounding.newBuilder().setBeginDate('1975-06-15T12:30:00Z').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.VALID)
              .setBeginIndexable(true)
              .setBeginPrecision(ChronoUnit.SECONDS.toString())
              .setBeginZoneSpecified('Z')
              .setBeginUtcDateTimeString('1975-06-15T12:30:00Z')
              .setEndDescriptor(ValidDescriptor.UNDEFINED)
              .setEndIndexable(true)
              .setEndPrecision(null)
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString(null)
              .setInstantDescriptor(ValidDescriptor.UNDEFINED)
              .setInstantIndexable(true)
              .setInstantPrecision(null)
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString(null)
              .setRangeDescriptor(TimeRangeDescriptor.ONGOING)
              .build()
      ),
      bounded: new TemporalSituation('non-paleo bounded range with day and year precision',
          TemporalBounding.newBuilder().setBeginDate('1900-01-01').setEndDate('2009').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.VALID)
              .setBeginIndexable(true)
              .setBeginPrecision(ChronoUnit.DAYS.toString())
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString('1900-01-01T00:00:00Z')
              .setEndDescriptor(ValidDescriptor.VALID)
              .setEndIndexable(true)
              .setEndPrecision(ChronoUnit.YEARS.toString())
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString('2009-12-31T23:59:59Z')
              .setInstantDescriptor(ValidDescriptor.UNDEFINED)
              .setInstantIndexable(true)
              .setInstantPrecision(null)
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString(null)
              .setRangeDescriptor(TimeRangeDescriptor.BOUNDED)
              .build()
      ),
      paleoBounded: new TemporalSituation('paleo bounded range',
          TemporalBounding.newBuilder().setBeginDate('-2000000000').setEndDate('-1000000000').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginDescriptor(ValidDescriptor.VALID)
              .setBeginIndexable(false)
              .setBeginPrecision(ChronoUnit.YEARS.toString())
              .setBeginZoneSpecified(null)
              .setBeginUtcDateTimeString('-2000000000-01-01T00:00:00Z')
              .setEndDescriptor(ValidDescriptor.VALID)
              .setEndIndexable(false)
              .setEndPrecision(ChronoUnit.YEARS.toString())
              .setEndZoneSpecified(null)
              .setEndUtcDateTimeString('-1000000000-12-31T23:59:59Z')
              .setInstantDescriptor(ValidDescriptor.UNDEFINED)
              .setInstantIndexable(true)
              .setInstantPrecision(null)
              .setInstantZoneSpecified(null)
              .setInstantUtcDateTimeString(null)
              .setRangeDescriptor(TimeRangeDescriptor.BOUNDED)
              .build()
      ),
  ]

}
