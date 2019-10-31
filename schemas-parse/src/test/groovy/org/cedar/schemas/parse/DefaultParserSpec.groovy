package org.cedar.schemas.parse

import org.cedar.schemas.avro.psi.AggregatedInput
import org.cedar.schemas.avro.psi.DataFormat
import org.cedar.schemas.avro.psi.Discovery
import org.cedar.schemas.avro.psi.FileInformation
import org.cedar.schemas.avro.psi.FileLocation
import org.cedar.schemas.avro.psi.FileLocationType
import org.cedar.schemas.avro.psi.Link
import org.cedar.schemas.avro.psi.ParsedRecord
import org.cedar.schemas.avro.psi.RecordType
import org.cedar.schemas.avro.psi.Relationship
import org.cedar.schemas.avro.psi.RelationshipType
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DefaultParserSpec extends Specification {

  static final String collectionId = '58559a4a-c2fc-4841-913a-8376457d45cc'
  static final String fileName = 'testFile.nc'
  static final String fileUri = 'http://public-path.to/testFile.nc'
  static final String format = 'NetCDF'
  static final String protocol = 'cloud'

  static final fileInfo = FileInformation.newBuilder().setName(fileName).setFormat(format).build()

  static final List<Relationship> relationships = [
      Relationship.newBuilder()
          .setType(RelationshipType.COLLECTION)
          .setId(collectionId)
          .build()
  ]

  static final fileLocationToLink = FileLocation.newBuilder()
      .setUri(fileUri)
      .setType(FileLocationType.ACCESS)
      .setServiceType(protocol)
      .build()
  static final fileLocationNonAccess = FileLocation.newBuilder()
      .setUri('http://internal-path.only/testFile.nc')
      .setType(FileLocationType.WORKING)
      .build()
  static final fileLocationRestricted = FileLocation.newBuilder()
      .setUri('https://restrict.ed/testFile.nc')
      .setType(FileLocationType.ACCESS)
      .setRestricted(true)
      .build()
  static final fileLocationDeleted = FileLocation.newBuilder()
      .setUri('filePath://nysi.nyd/testFile.nc')
      .setType(FileLocationType.ACCESS)
      .setDeleted(true)
      .build()
  static final Map<String, FileLocation> fileLocations = [
      (fileLocationToLink.getUri()): fileLocationToLink,
      (fileLocationNonAccess.getUri()): fileLocationNonAccess,
      (fileLocationRestricted.getUri()): fileLocationRestricted,
      (fileLocationDeleted.getUri()): fileLocationDeleted
  ]

  static final Discovery defaultDiscovery = Discovery.newBuilder()
      .setFileIdentifier(fileName)
      .setTitle(fileName)
      .setLinks([Link.newBuilder().setLinkFunction('download').setLinkUrl(fileUri).setLinkProtocol(protocol).build()])
      .setParentIdentifier(collectionId)
      .setDataFormats([DataFormat.newBuilder().setName(format).build()])
      .build()

  static final Discovery overriddenDiscovery = Discovery.newBuilder()
      .setTitle('override')
      .setFileIdentifier('override')
      .setParentIdentifier('override')
      .setHierarchyLevelName('override')
      .setLinks([Link.newBuilder().setLinkFunction('override').setLinkUrl('override').setLinkProtocol('override').build()])
      .setDataFormats([DataFormat.newBuilder().setName('override').build()])
      .build()

  static final ParsedRecord emptyRecord = ParsedRecord.newBuilder().build()
  static final ParsedRecord inputRecord = ParsedRecord.newBuilder()
      .setType(RecordType.granule)
      .setFileInformation(fileInfo)
      .setFileLocations(fileLocations)
      .setRelationships(relationships)
      .build()
  static final ParsedRecord overriddenRecord = ParsedRecord.newBuilder()
      .setType(RecordType.granule)
      .setFileInformation(fileInfo)
      .setFileLocations(fileLocations)
      .setRelationships(relationships)
      .setDiscovery(overriddenDiscovery)
      .build()


  def 'adds default Discovery to ParsedRecord'() {
    when:
    def updatedParsedRecord = DefaultParser.addDiscoveryToParsedRecord(inputRecord)

    then:
    def actualDiscovery = updatedParsedRecord.discovery
    actualDiscovery != null
    actualDiscovery.fileIdentifier == defaultDiscovery.fileIdentifier
    actualDiscovery.parentIdentifier == defaultDiscovery.parentIdentifier
    actualDiscovery.hierarchyLevelName == 'granule'
    actualDiscovery.title == defaultDiscovery.title

    and:
    def actualLinks = actualDiscovery.links
    actualLinks.size() == 1
    actualLinks[0] == defaultDiscovery.links[0]

    and:
    def actualFormats = actualDiscovery.dataFormats
    actualFormats.size() == 1
    actualFormats[0] == defaultDiscovery.dataFormats[0]
  }

  def 'fills in all default Discovery fields in a ParsedRecord'() {
    when:
    def filledInRecord = DefaultParser.fillInDefaults(inputRecord)

    then:
    def actualDiscovery = filledInRecord.discovery
    actualDiscovery != null
    actualDiscovery.fileIdentifier == defaultDiscovery.fileIdentifier
    actualDiscovery.parentIdentifier == defaultDiscovery.parentIdentifier
    actualDiscovery.hierarchyLevelName == 'granule'
    actualDiscovery.title == defaultDiscovery.title

    and:
    def actualLinks = actualDiscovery.links
    actualLinks.size() == 1
    actualLinks[0] == defaultDiscovery.links[0]

    and:
    def actualFormats = actualDiscovery.dataFormats
    actualFormats.size() == 1
    actualFormats[0] == defaultDiscovery.dataFormats[0]
  }

  def 'builds default Discovery from AggregatedInput'() {
    given:
    def aggregatedInput = AggregatedInput.newBuilder()
        .setType(RecordType.granule)
        .setFileInformation(fileInfo)
        .setFileLocations(fileLocations)
        .setRelationships(relationships)
        .build()

    when:
    def actualDiscovery = DefaultParser.buildDefaultDiscovery(aggregatedInput)

    then:
    actualDiscovery.fileIdentifier == defaultDiscovery.fileIdentifier
    actualDiscovery.parentIdentifier == defaultDiscovery.parentIdentifier
    actualDiscovery.hierarchyLevelName == 'granule'
    actualDiscovery.title == defaultDiscovery.title

    and:
    def actualLinks = actualDiscovery.links
    actualLinks.size() == 1
    actualLinks[0] == defaultDiscovery.links[0]

    and:
    def actualFormats = actualDiscovery.dataFormats
    actualFormats.size() == 1
    actualFormats[0] == defaultDiscovery.dataFormats[0]
  }

  def 'builds default Discovery from required parts'() {
    when:
    def actualDiscovery = DefaultParser.buildDefaultDiscovery(RecordType.granule, fileInfo, fileLocations, relationships)

    then:
    actualDiscovery.fileIdentifier == defaultDiscovery.fileIdentifier
    actualDiscovery.parentIdentifier == defaultDiscovery.parentIdentifier
    actualDiscovery.hierarchyLevelName == 'granule'
    actualDiscovery.title == defaultDiscovery.title

    and:
    def actualLinks = actualDiscovery.links
    actualLinks.size() == 1
    actualLinks[0] == defaultDiscovery.links[0]

    and:
    def actualFormats = actualDiscovery.dataFormats
    actualFormats.size() == 1
    actualFormats[0] == defaultDiscovery.dataFormats[0]
  }

  def 'null required parts create empty Discovery'() {
    when:
    def actualDiscovery = DefaultParser.buildDefaultDiscovery(null, null, null, null)

    then:
    actualDiscovery != null
    actualDiscovery == Discovery.newBuilder().build()
  }

  def 'fills in default value for title'() {
    when:
    def result = DefaultParser.setDefaultTitle(ParsedRecord.newBuilder(input))

    then:
    result.discovery.title == output

    where:
    input             | output
    emptyRecord       | null
    inputRecord       | fileName
    overriddenRecord  | overriddenDiscovery.title
  }

  def 'fills in default value for fileIdentifier'() {
    when:
    def result = DefaultParser.setDefaultFileId(ParsedRecord.newBuilder(input))

    then:
    result.discovery.fileIdentifier == output

    where:
    input             | output
    emptyRecord       | null
    inputRecord       | fileName
    overriddenRecord  | overriddenDiscovery.fileIdentifier
  }

  def 'fills in default value for parentIdentifier'() {
    when:
    def result = DefaultParser.setDefaultParentId(ParsedRecord.newBuilder(input))

    then:
    result.discovery.parentIdentifier == output

    where:
    input             | output
    emptyRecord       | null
    inputRecord       | collectionId
    overriddenRecord  | overriddenDiscovery.parentIdentifier
  }

  def 'fills in default value for hierarchyLevelName'() {
    when:
    def result = DefaultParser.setDefaultHierarchyName(ParsedRecord.newBuilder(input))

    then:
    result.discovery.hierarchyLevelName == output

    where:
    input             | output
    emptyRecord       | null
    inputRecord       | 'granule'
    overriddenRecord  | overriddenDiscovery.hierarchyLevelName
  }

  def 'fills in default value for links'() {
    when:
    def result = DefaultParser.setDefaultLinks(ParsedRecord.newBuilder(input))

    then:
    result.discovery.links.size() == output.size()
    if (output.size() > 0) {
      result.discovery.links.each({ assert output.contains(it) })
    }

    where:
    input             | output
    emptyRecord       | []
    inputRecord       | defaultDiscovery.links
    overriddenRecord  | overriddenDiscovery.links + defaultDiscovery.links
  }

  def 'input link with matching url overrides default link'() {
    setup:
    def matchingUrl = defaultDiscovery.links[0].linkUrl
    def inputLink = Link.newBuilder().setLinkUrl(matchingUrl).setLinkName('OVERRIDE!').build()
    def inputDiscovery = Discovery.newBuilder().setLinks([inputLink]).build()
    def customBuilder = ParsedRecord.newBuilder(inputRecord).setDiscovery(inputDiscovery)

    when:
    def result = DefaultParser.setDefaultLinks(customBuilder)

    then:
    result.discovery.links.size() == 1
    result.discovery.links[0].linkName == inputLink.linkName
  }

  def 'fills in default value for dataFormats'() {
    when:
    def result = DefaultParser.setDefaultDataFormats(ParsedRecord.newBuilder(input))

    then:
    result.discovery.dataFormats.size() == output.size()
    if (output.size() > 0) {
      result.discovery.dataFormats.each({ assert output.contains(it) })
    }

    where:
    input             | output
    emptyRecord       | []
    inputRecord       | defaultDiscovery.dataFormats
    overriddenRecord  | overriddenDiscovery.dataFormats
  }

}
