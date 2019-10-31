package org.cedar.schemas.parse;

import org.cedar.schemas.avro.psi.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultParser {

  public static ParsedRecord fillInDefaults(ParsedRecord inputRecord) {
    return Optional.ofNullable(inputRecord)
        .map(ParsedRecord::newBuilder)
        .map(DefaultParser::setDefaultTitle)
        .map(DefaultParser::setDefaultFileId)
        .map(DefaultParser::setDefaultParentId)
        .map(DefaultParser::setDefaultHierarchyName)
        .map(DefaultParser::setDefaultLinks)
        .map(DefaultParser::setDefaultDataFormats)
        .map(ParsedRecord.Builder::build)
        .orElse(null);
  }

  public static Discovery buildDefaultDiscovery(ParsedRecord record) {
    ParsedRecord tempRecord = ParsedRecord.newBuilder(record).setDiscovery(null).build();
    return fillInDefaults(tempRecord).getDiscovery();
  }

  public static Discovery buildDefaultDiscovery(RecordType type, FileInformation fileInfo, Map<String, FileLocation> fileLocations,
                                                List<Relationship> relationships) {
    ParsedRecord tempRecord = ParsedRecord.newBuilder()
        .setType(type)
        .setFileInformation(fileInfo)
        .setFileLocations(fileLocations)
        .setRelationships(relationships)
        .build();
    return fillInDefaults(tempRecord).getDiscovery();
  }

  public static ParsedRecord.Builder setDefaultTitle(ParsedRecord.Builder builder) {
    if (discoveryStringMissing(builder, Discovery::getTitle)) {
      Discovery discovery = getDiscoveryBuilder(builder).setTitle(defaultTitle(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  private static String defaultTitle(ParsedRecord.Builder builder) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getFileInformation)
        .map(FileInformation::getName)
        .orElse(null);
  }

  public static ParsedRecord.Builder setDefaultFileId(ParsedRecord.Builder builder) {
    if (discoveryStringMissing(builder, Discovery::getFileIdentifier)) {
      Discovery discovery = getDiscoveryBuilder(builder).setFileIdentifier(defaultFileId(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  private static String defaultFileId(ParsedRecord.Builder builder) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getFileInformation)
        .map(FileInformation::getName)
        .orElse(null);
  }

  public static ParsedRecord.Builder setDefaultParentId(ParsedRecord.Builder builder) {
    if (discoveryStringMissing(builder, Discovery::getParentIdentifier)) {
      Discovery discovery = getDiscoveryBuilder(builder).setParentIdentifier(defaultParentId(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  private static String defaultParentId(ParsedRecord.Builder builder) {
    List<Relationship> relationships = builder.getRelationships();
    if (relationships == null || relationships.isEmpty()) {
      return null;
    }
    return relationships.stream()
        .filter(r -> r.getType() == RelationshipType.COLLECTION)
        .findFirst()
        .map(Relationship::getId)
        .orElse(null);
  }

  public static ParsedRecord.Builder setDefaultHierarchyName(ParsedRecord.Builder builder) {
    if (discoveryStringMissing(builder, Discovery::getHierarchyLevelName)) {
      Discovery discovery = getDiscoveryBuilder(builder).setHierarchyLevelName(defaultHierarchyName(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  private static String defaultHierarchyName(ParsedRecord.Builder builder) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getType)
        .map(RecordType::name)
        .map(String::toLowerCase)
        .orElse(null);
  }

  public static ParsedRecord.Builder setDefaultLinks(ParsedRecord.Builder builder) {
    List<Link> inputLinks = discoveryListEmpty(builder, Discovery::getLinks) ?
        new ArrayList<>(0) : builder.getDiscovery().getLinks();
    Map<String, Link> inputLinksByUrl = inputLinks.stream()
        .collect(Collectors.toMap(Link::getLinkUrl, Function.identity()));
    Map<String, Link> defaultLinksByUrl = defaultLinks(builder).stream()
        .collect(Collectors.toMap(Link::getLinkUrl, Function.identity()));
    // override defaults with explicit input links based on URLs
    inputLinksByUrl.forEach((k, v) -> defaultLinksByUrl.merge(k, v, (a, b) -> v));
    List<Link> mergedLinks = new ArrayList<>(defaultLinksByUrl.values());
    Discovery discovery = getDiscoveryBuilder(builder).setLinks(mergedLinks).build();
    builder.setDiscovery(discovery);
    return builder;
  }

  private static List<Link> defaultLinks(ParsedRecord.Builder builder) {
    Map<String, FileLocation> fileLocations = builder.getFileLocations();
    if (fileLocations == null || fileLocations.isEmpty()) {
      return new ArrayList<>();
    }
    return fileLocations.values().stream()
        .filter(v -> v.getType() == FileLocationType.ACCESS && !v.getRestricted() && !v.getDeleted())
        .map(v -> Link.newBuilder()
            .setLinkProtocol(v.getServiceType())
            .setLinkUrl(v.getUri())
            .setLinkFunction("download")
            .build())
        .distinct()
        .collect(Collectors.toList());
  }

  public static ParsedRecord.Builder setDefaultDataFormats(ParsedRecord.Builder builder) {
    if (discoveryListEmpty(builder, Discovery::getDataFormats)) {
      Discovery discovery = getDiscoveryBuilder(builder).setDataFormats(defaultDataFormats(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  private static List<DataFormat> defaultDataFormats(ParsedRecord.Builder builder) {
    DataFormat format = Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getFileInformation)
        .map(FileInformation::getFormat)
        .filter(it -> !it.isEmpty())
        .map(f -> DataFormat.newBuilder().setName(f).build())
        .orElse(null);
    List<DataFormat> result = new ArrayList<>(1);
    if (format != null) {
      result.add(format);
    }
    return result;
  }

  private static boolean discoveryStringMissing(ParsedRecord.Builder builder, Function<Discovery, String> discoveryStringGetter) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getDiscovery)
        .map(discoveryStringGetter)
        .map(String::isEmpty)
        .orElse(true);
  }

  private static boolean discoveryListEmpty(ParsedRecord.Builder builder, Function<Discovery, List> discoveryListGetter) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getDiscovery)
        .map(discoveryListGetter)
        .map(List::isEmpty)
        .orElse(true);
  }

  /**
   * <em>IMPORTANT!!</em>
   * <br/>
   * Apparently functions on an Avro builder which return a builder for a nested object,
   * for example {@link ParsedRecord.Builder#getDiscoveryBuilder}, are not always null-safe!
   * <br/>
   * If that nested object is nullable, and you built the {@code ParsedRecord} builder from another {@code ParsedRecord}
   * instance with {@link ParsedRecord#newBuilder(ParsedRecord)}, then it'll set {@code null} as the value
   * for the {@code Discovery} object, which will cause {@link ParsedRecord.Builder#hasDiscovery()} to return {@code true}
   * since the {@code Discovery} object was technically set (even though it was set to {@code null}).
   * <br/>
   * The result in that case is that {@link ParsedRecord.Builder#getDiscoveryBuilder()} throws a {@link NullPointerException}.
   * This function will check whether the {@code Discovery} is null and create a {@code Discovery.Builder} safely.
   *
   * @param builder A {@link ParsedRecord.Builder} instance, potentially with a discovery value set
   * @return A {@link Discovery.Builder} instance built on the existing discovery value, if present
   */
  private static Discovery.Builder getDiscoveryBuilder(ParsedRecord.Builder builder) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getDiscovery)
        .map(Discovery::newBuilder)
        .orElse(Discovery.newBuilder());
  }

}
