package org.cedar.schemas.analyze

import org.cedar.schemas.avro.geojson.Point
import org.cedar.schemas.avro.psi.*
import org.cedar.schemas.avro.util.AvroUtils
import org.cedar.schemas.parse.ISOParser
import spock.lang.Specification
import spock.lang.Unroll

import java.time.temporal.ChronoUnit

import static org.cedar.schemas.avro.psi.TimeRangeDescriptor.*
import static spock.util.matcher.HamcrestMatchers.closeTo

@Unroll
class TemporalSpec extends Specification {

  final String analysisAvro = ClassLoader.systemClassLoader.getResourceAsStream('avro/psi/analysis.avsc').text

  def "instant #description (#instant) correctly extrapolates date range"() {
    given:
    def bounding = TemporalBounding.newBuilder()
        .setInstant(instant)
        .build()
    def discovery = Discovery.newBuilder().setTemporalBounding(bounding).build()

    when:
    def result = Temporal.analyzeBounding(discovery)

    then:
    println("????"+result)
    result.instantMonth == month
    result.instantEndMonth == endMonth
    result.instantDayOfMonth == dayOfMonth
    result.instantEndDayOfMonth == endDayOfMonth
    result.instantDayOfYear == dayOfYear
    result.instantEndDayOfYear == endDayOfYear

    where:

    description | instant | year | dayOfYear | dayOfMonth | month | endYear | endDayOfYear | endDayOfMonth | endMonth
    "instant with month precision" | '2003-02' | 2003 | 32 | 1 | 2 | 2003 | 59 | 28 | 2
    "instant on leapyear with month precision" | '2004-02' | 2004 | 32 | 1 | 2 | 2004 | 60 | 29 | 2
    "instant with day precision" | '2001-06-22' | 2001 | 173 | 22 | 6 | 2001 | 173 | 22 | 6
    "instant with day precision on leapyear" | '2020-06-22' | 2020 | 174 | 22 | 6 | 2020 | 174 | 22 | 6

  }

  def "#descriptor date range correctly identified when #situation"() {
    given:
    def bounding = TemporalBounding.newBuilder()
        .setBeginDate(begin)
        .setEndDate(end)
        .setInstant(instant)
        .build()
    def discovery = Discovery.newBuilder().setTemporalBounding(bounding).build()

    when:
    def result = Temporal.analyzeBounding(discovery)

    then:
    result.rangeDescriptor == descriptor

    where:
    descriptor     | situation                                                   | begin                  | end                    | instant
    ONGOING        | 'begin date exists but not end date'                        | '2010-01-01'           | ''                     | null
    BOUNDED        | 'begin and end date exist and are valid'                    | '2000-01-01T00:00:00Z' | '2001-01-01T00:00:00Z' | null
    UNDEFINED      | 'begin, end, and instant date all undefined'                | ''                     | ''                     | null
    INSTANT        | 'neither begin nor end date exist but valid instant does'   | ''                     | ''                     | '2001-01-01'
    INVALID        | 'end date exists but not begin date, valid instant present' | ''                     | '2010'                 | '2001-01-01'
    INVALID        | 'end date exists but not begin date, instant undefined'     | ''                     | '2010'                 | null
    BACKWARDS      | 'begin and end date exist but start after end'              | '2100-01-01T00:00:00Z' | '2002-01-01'           | null
    NOT_APPLICABLE | 'invalid instant present'                                   | ''                     | ''                     | '2001-01-32'
    NOT_APPLICABLE | 'invalid begin date present'                                | '2001-01-32'           | ''                     | ''
    NOT_APPLICABLE | 'invalid end date present'                                  | ''                     | '2001-01-32'           | ''
    AMBIGUOUS      | 'has valid begin, end, and instant'                         | '2010-01-01'           | '2001-01-01T00:00:00Z' | '2001-01-31'
    AMBIGUOUS      | 'begin date and instant exist but not end date'             | '2010-01-01'           | ''                     | '2001-01-31'
    INSTANT        | 'begin and end date equal timestamps'                       | '2000-01-01T00:00:00Z' | '2000-01-01T00:00:00Z' | null
    BOUNDED        | 'begin and end date same day but times assumed BoD and EoD' | '2010-01-01'           | '2010-01-01'           | null
  }

  def "Range descriptor is #value when #situation"() {
    given:
    def bounding = TemporalBounding.newBuilder()
        .setBeginDate(begin)
        .setEndDate(end)
        .build()
    def discovery = Discovery.newBuilder().setTemporalBounding(bounding).build()

    when:
    def result = Temporal.analyzeBounding(discovery)

    then:
    result.rangeDescriptor == value

    where:
    value          | situation                                               | begin                  | end
    BOUNDED        | 'start is valid format and before valid format end'     | '2010-01-01'           | '2011-01-01'
    BACKWARDS      | 'start is valid format and after valid format end'      | '2011-01-01T00:00:00Z' | '2001-01-01T00:00:00Z'
    BOUNDED        | 'start is paleo and before valid format end'            | '-1000000000'          | '2015'
    BOUNDED        | 'start and end both paleo and start before end'         | '-2000000000'          | '-1000000000'
    BOUNDED        | 'start valid LT end valid but years less than 4 digits' | '-900'                 | '100-01-01'
    BACKWARDS      | 'start and end both paleo and start after end'          | '-1000000000'          | '-2000000000'
    INSTANT        | 'start and end both same instant'                       | '2000-01-01T00:00:00Z' | '2000-01-01T00:00:00Z'
    ONGOING        | 'start exists but not end'                              | '2000-01-01T00:00:00Z' | ''
    INVALID        | 'start does not exist but end does'                     | ''                     | '2000-01-01T00:00:00Z'
    UNDEFINED      | 'neither start nor end exist'                           | ''                     | ''
    NOT_APPLICABLE | 'start is paleo and end is invalid'                     | '-1000000000'          | '1999-13-12'
    NOT_APPLICABLE | 'start is invalid and end is paleo'                     | '15mya'                | '-1000000000'
    NOT_APPLICABLE | 'start is valid and end is invalid'                     | '2000-01-01T00:00:00Z' | '2000-12-31T25:00:00Z'
    NOT_APPLICABLE | 'start and end both invalid'                            | '2000-01-01T00:61:00Z' | '2000-11-31T00:00:00Z'
    NOT_APPLICABLE | 'start is invalid but end is valid'                     | '2000-01-01T00:00:61Z' | '2000-01-02T00:00:00Z'
  }
}
