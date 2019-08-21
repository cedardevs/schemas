package org.cedar.schemas.parse;

import org.cedar.schemas.avro.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultParser {

  public static ParsedRecord addDiscoveryToParsedRecord(ParsedRecord parsedRecord) {
    Discovery defaultDiscovery = buildDefaultDiscovery(parsedRecord.getFileInformation(),
        parsedRecord.getFileLocations(), parsedRecord.getRelationships());

    ParsedRecord updatedRecord = ParsedRecord.newBuilder(parsedRecord)
        .setDiscovery(defaultDiscovery)
        .build();
    return updatedRecord;
  }

  public static Discovery buildDefaultDiscovery(AggregatedInput aggregatedInput) {
    return buildDefaultDiscovery(aggregatedInput.getFileInformation(), aggregatedInput.getFileLocations(),
        aggregatedInput.getRelationships());
  }

  public static Discovery buildDefaultDiscovery(FileInformation fileInfo, Map<String, FileLocation> fileLocations,
                                                List<Relationship> relationships) {
    Discovery.Builder builder = Discovery.newBuilder();

    // Set fileIdentifier and title with the file name
    String fileName = fileInfo.getName();
    builder.setFileIdentifier(fileName);
    builder.setTitle(fileName);

    // Set data format with known file format
    List<DataFormat> dataFormats = new ArrayList<>();
    dataFormats.add(DataFormat.newBuilder().setName(fileInfo.getFormat()).build());
    builder.setDataFormats(dataFormats);

    // Set parentIdentifier with COLLECTION relationship id value
    Optional<Relationship> collectionRelationship = relationships.stream()
        .filter(r -> r.getType() == RelationshipType.COLLECTION)
        .findFirst();
    String parentIdentifier = collectionRelationship.isPresent() ? collectionRelationship.get().getId() : null;
    builder.setParentIdentifier(parentIdentifier);

    // Set links from file locations that are not restricted access
    // TODO In the future we may need to look at Publishing as well...
    List<Link> links = new ArrayList<>();
    fileLocations.forEach( (k, v) -> {
      if(v.getType() == FileLocationType.ACCESS && !v.getRestricted() && !v.getDeleted()) {
        Link link = Link.newBuilder()
            .setLinkProtocol(v.getServiceType())
            .setLinkUrl(v.getUri())
            .setLinkFunction("download")
            .build();
        links.add(link);
      }
    });
    builder.setLinks(links);

    return builder.build();
  }
}
