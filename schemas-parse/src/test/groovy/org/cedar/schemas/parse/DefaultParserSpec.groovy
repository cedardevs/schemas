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

class DefaultParserSpec extends Specification {

  static final String collectionId = '58559a4a-c2fc-4841-913a-8376457d45cc'
  static final String fileName = 'testFile.nc'
  static final String fileUri = 'http://public-path.to/testFile.nc'
  static final String format = 'NetCDF'
  static final protocol = 'cloud'

  static final fileInfo = FileInformation.newBuilder().setName(fileName).setFormat(format).build()
  static final Map<String, FileLocation> fileLocations = new HashMap<>()
  static final List<Relationship> relationships = new ArrayList<>()

  static final Discovery expectedDiscovery

  static {
    // Assemble Discovery Object
    def links = new ArrayList<Link>()
    links.add(Link.newBuilder().setLinkFunction('download').setLinkUrl(fileUri).setLinkProtocol(protocol).build())
    def formats = new ArrayList<DataFormat>()
    formats.add(DataFormat.newBuilder().setName(format).build())
    expectedDiscovery = Discovery.newBuilder()
        .setFileIdentifier(fileName)
        .setTitle(fileName)
        .setLinks(links)
        .setParentIdentifier(collectionId)
        .setDataFormats(formats)
        .build()

    // Populate fileLocations
    def fileLocationToLink = FileLocation.newBuilder()
        .setUri(fileUri)
        .setType(FileLocationType.ACCESS)
        .setServiceType(protocol)
        .build()
    def fileLocationNonAccess = FileLocation.newBuilder()
        .setUri('http://internal-path.only/testFile.nc')
        .setType(FileLocationType.WORKING)
        .build()
    def fileLocationRestricted = FileLocation.newBuilder()
        .setUri('https://restrict.ed/testFile.nc')
        .setType(FileLocationType.ACCESS)
        .setRestricted(true)
        .build()
    def fileLocationDeleted = FileLocation.newBuilder()
        .setUri('filePath://nysi.nyd/testFile.nc')
        .setType(FileLocationType.ACCESS)
        .setDeleted(true)
        .build()

    fileLocations.put(fileLocationToLink.getUri(), fileLocationToLink)
    fileLocations.put(fileLocationNonAccess.getUri(), fileLocationNonAccess)
    fileLocations.put(fileLocationRestricted.getUri(), fileLocationRestricted)
    fileLocations.put(fileLocationDeleted.getUri(), fileLocationDeleted)

    // Populate relationships
    def collectionRelationship = Relationship.newBuilder()
        .setType(RelationshipType.COLLECTION)
        .setId(collectionId)
        .build()
    relationships.add(collectionRelationship)
  }


  def 'adds default Discovery to ParsedRecord'() {
    given:
    def originalParsedRecord = ParsedRecord.newBuilder()
        .setType(RecordType.granule)
        .setFileInformation(fileInfo)
        .setFileLocations(fileLocations)
        .setRelationships(relationships)
        .build()

    when:
    def updatedParsedRecord = DefaultParser.addDiscoveryToParsedRecord(originalParsedRecord)

    then:
    def actualDiscovery = updatedParsedRecord.discovery
    actualDiscovery != null
    actualDiscovery.fileIdentifier == expectedDiscovery.fileIdentifier
    actualDiscovery.parentIdentifier == expectedDiscovery.parentIdentifier
    actualDiscovery.title == expectedDiscovery.title

    and:
    def actualLinks = actualDiscovery.links
    actualLinks.size() == 1
    actualLinks == expectedDiscovery.links

    and:
    def actualFormats = actualDiscovery.dataFormats
    actualFormats == expectedDiscovery.dataFormats
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
    actualDiscovery.fileIdentifier == expectedDiscovery.fileIdentifier
    actualDiscovery.parentIdentifier == expectedDiscovery.parentIdentifier
    actualDiscovery.title == expectedDiscovery.title

    and:
    def actualLinks = actualDiscovery.links
    actualLinks.size() == 1
    actualLinks == expectedDiscovery.links

    and:
    def actualFormats = actualDiscovery.dataFormats
    actualFormats == expectedDiscovery.dataFormats
  }

  def 'builds default Discovery from required parts'() {
    when:
    def actualDiscovery = DefaultParser.buildDefaultDiscovery(fileInfo, fileLocations, relationships)

    then:
    actualDiscovery.fileIdentifier == expectedDiscovery.fileIdentifier
    actualDiscovery.parentIdentifier == expectedDiscovery.parentIdentifier
    actualDiscovery.title == expectedDiscovery.title

    and:
    def actualLinks = actualDiscovery.links
    actualLinks.size() == 1
    actualLinks == expectedDiscovery.links

    and:
    def actualFormats = actualDiscovery.dataFormats
    actualFormats == expectedDiscovery.dataFormats
  }
}