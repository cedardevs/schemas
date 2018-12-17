package org.cedar.schemas.avro.util

import groovy.transform.Canonical
import org.cedar.schemas.avro.psi.TemporalBounding
import org.cedar.schemas.avro.psi.TemporalBoundingAnalysis

import java.time.temporal.ChronoUnit

import static org.cedar.schemas.avro.psi.TimeRangeDescriptor.*
import static org.cedar.schemas.avro.psi.NullDescriptor.*

class TemporalTestData {

  @Canonical
  static class TemporalSitutation {
    String description
    TemporalBounding bounding
    TemporalBoundingAnalysis analysis
  }

  static Map<String, TemporalSitutation> situtations = [
      empty  : new TemporalSitutation('undefined range',
          TemporalBounding.newBuilder().build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(false)
              .setBeginIndexable(true)
              .setBeginPrecision(UNDEFINED)
              .setBeginZoneSpecified(UNDEFINED)
              .setBeginUtcDateTimeString(UNDEFINED)
              .setEndExists(false)
              .setEndIndexable(true)
              .setEndPrecision(UNDEFINED)
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString(UNDEFINED)
              .setInstantExists(false)
              .setInstantIndexable(true)
              .setInstantPrecision(UNDEFINED)
              .setInstantZoneSpecified(UNDEFINED)
              .setInstantUtcDateTimeString(UNDEFINED)
              .setRangeDescriptor(UNDEFINED)
              .setRangeBeginLTEEnd(UNDEFINED)
              .build()
      ),
      invalid: new TemporalSitutation('invalid dates',
          TemporalBounding.newBuilder().setBeginDate('not a date').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(true)
              .setBeginIndexable(false)
              .setBeginPrecision(INVALID)
              .setBeginZoneSpecified(INVALID)
              .setBeginUtcDateTimeString(INVALID)
              .setEndExists(false)
              .setEndIndexable(true)
              .setEndPrecision(UNDEFINED)
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString(UNDEFINED)
              .setInstantExists(false)
              .setInstantIndexable(true)
              .setInstantPrecision(UNDEFINED)
              .setInstantZoneSpecified(UNDEFINED)
              .setInstantUtcDateTimeString(UNDEFINED)
              .setRangeDescriptor(UNDEFINED)
              .setRangeBeginLTEEnd(UNDEFINED)
              .build()
      ),
      instantNano: new TemporalSitutation('non-paleo instant with nanos precision',
          TemporalBounding.newBuilder().setInstant('2008-04-01T00:00:00Z').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(false)
              .setBeginIndexable(true)
              .setBeginPrecision(UNDEFINED)
              .setBeginZoneSpecified(UNDEFINED)
              .setBeginUtcDateTimeString(UNDEFINED)
              .setEndExists(false)
              .setEndIndexable(true)
              .setEndPrecision(UNDEFINED)
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString(UNDEFINED)
              .setInstantExists(true)
              .setInstantIndexable(true)
              .setInstantPrecision(ChronoUnit.NANOS.toString())
              .setInstantZoneSpecified('Z')
              .setInstantUtcDateTimeString('2008-04-01T00:00:00Z')
              .setRangeDescriptor(INSTANT)
              .setRangeBeginLTEEnd(UNDEFINED)
              .build()
      ),
      instantDay: new TemporalSitutation('non-paleo instant with days precision',
          TemporalBounding.newBuilder().setInstant('1999-12-31').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(false)
              .setBeginIndexable(true)
              .setBeginPrecision(UNDEFINED)
              .setBeginZoneSpecified(UNDEFINED)
              .setBeginUtcDateTimeString(UNDEFINED)
              .setEndExists(false)
              .setEndIndexable(true)
              .setEndPrecision(UNDEFINED)
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString(UNDEFINED)
              .setInstantExists(true)
              .setInstantIndexable(true)
              .setInstantPrecision(ChronoUnit.DAYS.toString())
              .setInstantZoneSpecified(UNDEFINED)
              .setInstantUtcDateTimeString('1999-12-31T00:00:00Z')
              .setRangeDescriptor(INSTANT)
              .setRangeBeginLTEEnd(UNDEFINED)
              .build()
      ),
      instantYear: new TemporalSitutation('non-paleo instant with years precision',
          TemporalBounding.newBuilder().setInstant('1999').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(false)
              .setBeginIndexable(true)
              .setBeginPrecision(UNDEFINED)
              .setBeginZoneSpecified(UNDEFINED)
              .setBeginUtcDateTimeString(UNDEFINED)
              .setEndExists(false)
              .setEndIndexable(true)
              .setEndPrecision(UNDEFINED)
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString(UNDEFINED)
              .setInstantExists(true)
              .setInstantIndexable(true)
              .setInstantPrecision(ChronoUnit.YEARS.toString())
              .setInstantZoneSpecified(UNDEFINED)
              .setInstantUtcDateTimeString('1999-01-01T00:00:00Z')
              .setRangeDescriptor(INSTANT)
              .setRangeBeginLTEEnd(UNDEFINED)
              .build()
      ),
      instantPaleo: new TemporalSitutation('paleo instant with years precision',
          TemporalBounding.newBuilder().setInstant('-1000000000').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(false)
              .setBeginIndexable(true)
              .setBeginPrecision(UNDEFINED)
              .setBeginZoneSpecified(UNDEFINED)
              .setBeginUtcDateTimeString(UNDEFINED)
              .setEndExists(false)
              .setEndIndexable(true)
              .setEndPrecision(UNDEFINED)
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString(UNDEFINED)
              .setInstantExists(true)
              .setInstantIndexable(true)
              .setInstantPrecision(ChronoUnit.YEARS.toString())
              .setInstantZoneSpecified(UNDEFINED)
              .setInstantUtcDateTimeString('-1000000000-01-01T00:00:00Z')
              .setRangeDescriptor(INSTANT)
              .setRangeBeginLTEEnd(UNDEFINED)
              .build()
      ),
      ongoing: new TemporalSitutation('ongoing range with second precision for begin',
          TemporalBounding.newBuilder().setBeginDate('1975-06-15T12:30:00Z').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(true)
              .setBeginIndexable(true)
              .setBeginPrecision(ChronoUnit.SECONDS.toString())
              .setBeginZoneSpecified('Z')
              .setBeginUtcDateTimeString('1975-06-15T12:30:00Z')
              .setEndExists(false)
              .setEndIndexable(true)
              .setEndPrecision(UNDEFINED)
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString(UNDEFINED)
              .setInstantExists(false)
              .setInstantIndexable(true)
              .setInstantPrecision(UNDEFINED)
              .setInstantZoneSpecified(UNDEFINED)
              .setInstantUtcDateTimeString(UNDEFINED)
              .setRangeDescriptor(ONGOING)
              .setRangeBeginLTEEnd(UNDEFINED)
              .build()
      ),
      bounded: new TemporalSitutation('non-paleo bounded range with day and year precision',
          TemporalBounding.newBuilder().setBeginDate('1900-01-01').setEndDate('2009').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(true)
              .setBeginIndexable(true)
              .setBeginPrecision(ChronoUnit.DAYS.toString())
              .setBeginZoneSpecified(UNDEFINED)
              .setBeginUtcDateTimeString('1900-01-01T00:00:00Z')
              .setEndExists(true)
              .setEndIndexable(true)
              .setEndPrecision(ChronoUnit.YEARS.toString())
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString('2009-12-31T23:59:59Z')
              .setInstantExists(false)
              .setInstantIndexable(true)
              .setInstantPrecision(UNDEFINED)
              .setInstantZoneSpecified(UNDEFINED)
              .setInstantUtcDateTimeString(UNDEFINED)
              .setRangeDescriptor(BOUNDED)
              .setRangeBeginLTEEnd(true)
              .build()
      ),
      paleoBounded: new TemporalSitutation('paleo bounded range',
          TemporalBounding.newBuilder().setBeginDate('-2000000000').setEndDate('-1000000000').build(),
          TemporalBoundingAnalysis.newBuilder()
              .setBeginExists(true)
              .setBeginIndexable(false)
              .setBeginPrecision(ChronoUnit.YEARS.toString())
              .setBeginZoneSpecified(UNDEFINED)
              .setBeginUtcDateTimeString('-2000000000-01-01T00:00:00Z')
              .setEndExists(true)
              .setEndIndexable(false)
              .setEndPrecision(ChronoUnit.YEARS.toString())
              .setEndZoneSpecified(UNDEFINED)
              .setEndUtcDateTimeString('-1000000000-12-31T23:59:59Z')
              .setInstantExists(false)
              .setInstantIndexable(true)
              .setInstantPrecision(UNDEFINED)
              .setInstantZoneSpecified(UNDEFINED)
              .setInstantUtcDateTimeString(UNDEFINED)
              .setRangeDescriptor(BOUNDED)
              .setRangeBeginLTEEnd(true)
              .build()
      ),
  ]

}
