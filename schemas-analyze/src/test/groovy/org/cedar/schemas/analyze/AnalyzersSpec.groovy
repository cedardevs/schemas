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
class AnalyzersSpec extends Specification {

  final String analysisAvro = ClassLoader.systemClassLoader.getResourceAsStream('avro/psi/analysis.avsc').text

  def 'adds an analysis into a parsed record'() {
    def record = ParsedRecord.newBuilder().setType(RecordType.collection).setDiscovery(Discovery.newBuilder().build()).build()

    when:
    def result = Analyzers.addAnalysis(record)

    then:
    result instanceof ParsedRecord
    result.analysis instanceof Analysis
    result.discovery == record.discovery
  }

  def 'analyzing null discovery returns null'() {
    expect:
    Analyzers.analyze(null) == null
  }

  def 'analyzing a default discovery object returns all expected analysis'() {
    def discovery = Discovery.newBuilder().build()

    when:
    def analysis = Analyzers.analyze(discovery)

    then:
    analysis instanceof Analysis
    analysis.identification instanceof IdentificationAnalysis
    analysis.temporalBounding instanceof TemporalBoundingAnalysis
    analysis.spatialBounding instanceof SpatialBoundingAnalysis
    analysis.titles instanceof TitleAnalysis
    analysis.description instanceof DescriptionAnalysis
    analysis.thumbnail instanceof ThumbnailAnalysis
    analysis.dataAccess instanceof DataAccessAnalysis
  }

  def "All valid fields return expected response from service"() {
    given:
    def inputXml = ClassLoader.systemClassLoader.getResourceAsStream('test-iso-metadata.xml').text
    def discovery = ISOParser.parseXMLMetadataToDiscovery(inputXml)

    def expectedAnalysisMap = [
        identification  : [
            fileIdentifierExists    : true,
            fileIdentifierString    : 'gov.super.important:FILE-ID',
            doiExists               : true,
            doiString               : 'doi:10.5072/FK2TEST',
            parentIdentifierExists  : true,
            parentIdentifierString  : 'gov.super.important:PARENT-ID',
            hierarchyLevelNameExists: true,
            isGranule               : true
        ],
        temporalBounding: [
            beginDescriptor         : ValidDescriptor.VALID,
            // For why below value is not seconds, see:
            // https://docs.oracle.com/javase/8/docs/api/java/time/temporal/TemporalQueries.html#precision--
            beginPrecision          : ChronoUnit.NANOS.toString(),
            beginIndexable          : true,
            beginZoneSpecified      : 'Z',
            beginUtcDateTimeString  : '2005-05-09T00:00:00Z',
            beginDayOfYear          : 129,
            beginDayOfMonth         : 9,
            beginYear               : 2005,
            beginMonth              : 5,
            endDescriptor           : ValidDescriptor.VALID,
            endPrecision            : ChronoUnit.DAYS.toString(),
            endIndexable            : true,
            endZoneSpecified        : null,
            endUtcDateTimeString    : '2010-10-01T23:59:59.999Z',
            endDayOfYear            : 274,
            endDayOfMonth           : 1,
            endYear                 : 2010,
            endMonth                : 10,
            instantDescriptor       : ValidDescriptor.UNDEFINED,
            instantPrecision        : null,
            instantIndexable        : true,
            instantZoneSpecified    : null,
            instantUtcDateTimeString: null,
            instantEndUtcDateTimeString: null,
            instantDayOfYear        : null,
            instantDayOfMonth       : null,
            instantYear             : null,
            instantMonth            : null,
            instantEndDayOfYear     : null,
            instantEndDayOfMonth    : null,
            instantEndMonth         : null,
            rangeDescriptor         : BOUNDED,
        ],
        spatialBounding : [
            spatialBoundingExists: true,
            isValid              : true,
            validationError      : null
        ],
        titles          : [
            titleExists             : true,
            titleCharacters         : 63,
            alternateTitleExists    : true,
            alternateTitleCharacters: 51,
            titleFleschReadingEaseScore: -41.984285714285676,
            alternateTitleFleschReadingEaseScore: 42.61571428571432,
            titleFleschKincaidReadingGradeLevel: 20.854285714285712,
            alternateTitleFleschKincaidReadingGradeLevel: 9.054285714285715
        ],
        description     : [
            descriptionExists    : true,
            descriptionCharacters: 65,
            descriptionFleschReadingEaseScore: 19.100000000000023,
            descriptionFleschKincaidReadingGradeLevel: 12.831111111111113,
        ],
        thumbnail       : [
            thumbnailExists: true,
        ],
        dataAccess      : [
            dataAccessExists: true
        ]
    ]

    when:
    def analysis = Analyzers.analyze(discovery)

    then:
    AvroUtils.avroToMap(analysis.identification) == expectedAnalysisMap.identification
    AvroUtils.avroToMap(analysis.titles) == expectedAnalysisMap.titles
    AvroUtils.avroToMap(analysis.description) == expectedAnalysisMap.description
    AvroUtils.avroToMap(analysis.dataAccess) == expectedAnalysisMap.dataAccess
    AvroUtils.avroToMap(analysis.thumbnail) == expectedAnalysisMap.thumbnail
    AvroUtils.avroToMap(analysis.temporalBounding) == expectedAnalysisMap.temporalBounding
    AvroUtils.avroToMap(analysis.spatialBounding) == expectedAnalysisMap.spatialBounding
  }

  def "analyzes when links are #testCase"() {
    given:
    def record = Discovery.newBuilder().setLinks(testLinks).build()

    when:
    def dataAccessAnalysis = Analyzers.analyzeDataAccess(record)

    then:
    dataAccessAnalysis instanceof DataAccessAnalysis
    dataAccessAnalysis.dataAccessExists == expected

    where:
    testCase  | testLinks                   | expected
    'missing' | []                          | false
    'present' | [Link.newBuilder().build()] | true
  }

  def "analyzes required identifiers"() {
    given:
    def metadata = Discovery.newBuilder().setFileIdentifier('xyz').build()

    when:
    def result = Analyzers.analyzeIdentifiers(metadata)

    then:
    result instanceof IdentificationAnalysis
    result.fileIdentifierExists
    result.fileIdentifierString == 'xyz'
    !result.doiExists
    result.doiString == null
    !result.parentIdentifierExists
    result.parentIdentifierString == null
    !result.hierarchyLevelNameExists
    !result.isGranule
  }

  def "detects mismatch between metadata type and corresponding identifiers"() {
    given:
    def builder = Discovery.newBuilder()
    builder.fileIdentifier = 'xyz'
    builder.hierarchyLevelName = 'granule'
    def metadata = builder.build()

    when:
    def result = Analyzers.analyzeIdentifiers(metadata)

    then:
    result instanceof IdentificationAnalysis
    result.fileIdentifierExists
    result.fileIdentifierString == 'xyz'
    !result.doiExists
    result.doiString == null
    !result.parentIdentifierExists
    result.parentIdentifierString == null
    result.hierarchyLevelNameExists
    !result.isGranule
  }

  def 'handles analysis of #testCase strings'() {
    when:
    def result = new Analyzers.StringInfo(value)

    then:
    result instanceof Analyzers.StringInfo
    result.exists == exists
    result.characters == length
    result.readingEase == ease
    result.gradeLevel == grade

    where:
    testCase  | value  | exists | length | ease    | grade
    'missing' | null   | false  | 0      | null    | null
    'empty'   | ''     | false  | 0      | null    | null
  }

  def 'analyzes #testCase strings'() {
    when:
    def result = new Analyzers.StringInfo(value)

    then:
    result instanceof Analyzers.StringInfo
    result.exists == exists
    result.characters == length
    closeTo(ease, 0.01).matches(result.readingEase)
    closeTo(grade, 0.01).matches(result.gradeLevel)

    where:
    testCase  | value  | exists | length | ease    | grade
    'present' | 'test' | true   | 4      | 121.220 | -3.40
  }

  def "analyzes when titles are missing"() {
    given:
    def metadata = Discovery.newBuilder().build()

    when:
    def titlesAnalysis = Analyzers.analyzeTitles(metadata)

    then:
    titlesAnalysis instanceof TitleAnalysis
    !titlesAnalysis.titleExists
    titlesAnalysis.titleCharacters == 0
    !titlesAnalysis.alternateTitleExists
    titlesAnalysis.alternateTitleCharacters == 0
  }

  def "analyses when description is missing"() {
    given:
    def metadata = Discovery.newBuilder().build()

    when:
    def descriptionAnalysis = Analyzers.analyzeDescription(metadata)

    then:
    descriptionAnalysis instanceof DescriptionAnalysis
    !descriptionAnalysis.descriptionExists
    descriptionAnalysis.descriptionCharacters == 0
  }

  def "analyzes when thumbnail is #testCase"() {
    given:
    def metadata = Discovery.newBuilder().setThumbnail(value).build()

    when:
    def thumbnailAnalysis = Analyzers.analyzeThumbnail(metadata)

    then:
    thumbnailAnalysis instanceof ThumbnailAnalysis
    thumbnailAnalysis.thumbnailExists == expected

    where:
    testCase  | value                | expected
    'missing' | null                 | false
    'present' | 'thumbnailAnalysis!' | true
  }

  def "analyzes when spatial boundings are #testCase"() {
    given:
    def metadata = Discovery.newBuilder().setSpatialBounding(value).build()

    when:
    def result = Analyzers.analyzeSpatialBounding(metadata)

    then:
    result instanceof SpatialBoundingAnalysis
    result.spatialBoundingExists == expected

    where:
    testCase  | value        | expected
    'missing' | null         | false
    'present' | buildPoint() | true
  }

  static buildPoint() {
    Point.newBuilder()
        .setCoordinates([1 as Double, 2 as Double])
        .build()
  }
}
