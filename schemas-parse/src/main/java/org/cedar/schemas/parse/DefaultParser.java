package org.cedar.schemas.parse;

import org.cedar.schemas.avro.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultParser {

  public static ParsedRecord addDiscoveryToParsedRecord(ParsedRecord parsedRecord) {
    Discovery defaultDiscovery = buildDefaultDiscovery(parsedRecord.getType(), parsedRecord.getFileInformation(),
        parsedRecord.getFileLocations(), parsedRecord.getRelationships());

    ParsedRecord updatedRecord = ParsedRecord.newBuilder(parsedRecord)
        .setDiscovery(defaultDiscovery)
        .build();
    return updatedRecord;
  }

  public static Discovery buildDefaultDiscovery(AggregatedInput aggregatedInput) {
    return buildDefaultDiscovery(aggregatedInput.getType(), aggregatedInput.getFileInformation(),
        aggregatedInput.getFileLocations(), aggregatedInput.getRelationships());
  }

  public static Discovery buildDefaultDiscovery(RecordType type, FileInformation fileInfo, Map<String, FileLocation> fileLocations,
                                                List<Relationship> relationships) {
    Discovery.Builder builder = Discovery.newBuilder();

    // Set fileIdentifier and title with the file name
    String fileName = fileInfo != null ? fileInfo.getName() : null;
    builder.setFileIdentifier(fileName);
    builder.setTitle(fileName);

    // Set parentIdentifier with COLLECTION relationship id value
    String parentIdentifier;
    if(relationships != null) {
      Optional<Relationship> collectionRelationship = relationships.stream()
          .filter(r -> r.getType() == RelationshipType.COLLECTION)
          .findFirst();
      parentIdentifier = collectionRelationship.isPresent() ? collectionRelationship.get().getId() : null;
    }
    else {
      parentIdentifier = null;
    }
    builder.setParentIdentifier(parentIdentifier);

    // Set hierarchyLevelName with RecordType
    String hierarchyLevelName = type != null ? type.name().toLowerCase() : null;
    builder.setHierarchyLevelName(hierarchyLevelName);

    // Set links from file locations that are not restricted access
    // TODO In the future we may need to look at Publishing as well...
    List<Link> links = new ArrayList<>();
    if(fileLocations != null) {
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
    }
    builder.setLinks(links);

    // Set data format with known file format
    List<DataFormat> dataFormats = new ArrayList<>();
    if(fileInfo != null && fileInfo.getFormat() != null && !fileInfo.getFormat().isEmpty()) {
      dataFormats.add(DataFormat.newBuilder().setName(fileInfo.getFormat()).build());
    }
    builder.setDataFormats(dataFormats);

    return builder.build();
  }
}
