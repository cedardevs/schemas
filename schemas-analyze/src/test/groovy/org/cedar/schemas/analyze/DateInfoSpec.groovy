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
class DateInfoSpec extends Specification {

  final String analysisAvro = ClassLoader.systemClassLoader.getResourceAsStream('avro/psi/analysis.avsc').text

  def 'extracts expanded date info from date string: #input'() {
    when:
    def result = new DateInfo(input, true) // shouldn't matter if start is true or false for these
    println(result.utcDateTimeString)

    then:
    result.year == year
    result.dayOfYear == dayOfYear
    result.dayOfMonth == dayOfMonth
    result.month == month

    where:
    input | year | dayOfYear | dayOfMonth | month
    '1999-12-31' | 1999 | 365 | 31 | 12
    '1999' | 1999 | null | null | null
    '1999-01-01T00:00:00Z' | 1999 | 1 | 1 | 1
    '-1000000000' | null | null | null | null // too big paleo number isn't parsed as a year value
    '-35000000' | -35_000_000L | null | null | null
    '2008-04-01T00:00:00Z' | 2008 | 92 | 1 | 4
    '1975-06-15T12:30:00Z' | 1975 | 166 | 15 | 6
    null | null | null | null | null
    'not a date' | null | null | null | null
    '2020-02-29' | 2020 | 60 | 29 | 2 // leap day
    '2020-12-31' | 2020 | 366 | 31 | 12 // last day of leap year
    '20191025' | 20191025 | null | null | null // string datetime from the bug
  }

  def 'extracts date info from date string: #input'() {
    when:
    def result = new DateInfo(input, start)
    println(result.utcDateTimeString)

    then:
    result.descriptor == descriptor
    result.precision == precision
    result.indexable == indexable
    result.zoneSpecified == zone
    result.utcDateTimeString == string

    where:
    input                  | start || descriptor                | precision | indexable | zone | string
    '2042-04-02T00:42:42Z' | false || ValidDescriptor.VALID     | 'Nanos'   | true      | 'Z'  | '2042-04-02T00:42:42Z'
    '2042-04-02T00:42:42'  | false || ValidDescriptor.VALID     | 'Nanos'   | true      | null | '2042-04-02T00:42:42Z'
    '2042-04-02'           | false || ValidDescriptor.VALID     | 'Days'    | true      | null | '2042-04-02T23:59:59.999Z'
    '2042-04-02'           | true  || ValidDescriptor.VALID     | 'Days'    | true      | null | '2042-04-02T00:00:00Z'
    '2042-05'              | true  || ValidDescriptor.VALID     | 'Months'  | true      | null | '2042-05-01T00:00:00Z'
    '-2042-05'             | false || ValidDescriptor.VALID     | 'Months'  | true      | null | '-2042-05-31T23:59:59.999Z'
    '2042'                 | true  || ValidDescriptor.VALID     | 'Years'   | true      | null | '2042-01-01T00:00:00Z'
    '1965'                 | false || ValidDescriptor.VALID     | 'Years'   | true      | null | '1965-12-31T23:59:59.999Z'
    '-5000'                | true  || ValidDescriptor.VALID     | 'Years'   | true      | null | '-5000-01-01T00:00:00Z'
    '-3000'                | false || ValidDescriptor.VALID     | 'Years'   | true      | null | '-3000-12-31T23:59:59.999Z'
    '-100000001'           | true  || ValidDescriptor.VALID     | 'Years'   | false     | null | '-100000001-01-01T00:00:00Z'
    '-100000002'           | false || ValidDescriptor.VALID     | 'Years'   | false     | null | '-100000002-12-31T23:59:59.999Z'
    'ABC'                  | true  || ValidDescriptor.INVALID   | null      | false     | null | null
    ''                     | true  || ValidDescriptor.UNDEFINED | null      | true      | null | null
    null                   | true  || ValidDescriptor.UNDEFINED | null      | true      | null | null
  }
}
