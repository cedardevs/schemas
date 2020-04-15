package org.cedar.schemas.parse;

import org.cedar.schemas.avro.psi.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultParser {

  /**
   * Given a {@link ParsedRecord}, fill in any empty fields for which a default can be calculated. For instance
   * if the record's {@link Discovery} does not have a title, it will be set with the default, which is the the
   * {@link FileInformation#getName()} (see: {@link #defaultTitle(ParsedRecord.Builder)}).
   * <br>
   * In general, only fields which have no value will be set with their defaults. The exception is the links,
   * which will have the defaults merged into the input values, with inputs taking precedence.
   * See: {@link #setDefaultLinks(ParsedRecord.Builder)}
   *
   * @param inputRecord The input record
   * @return A new record with any missing fields with defaults filled in
   */
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

  /**
   * Builds a new {@link Discovery} object containing only default values which can be derived from the input record.
   * @param record The input record.
   * @return The default {@link Discovery} object
   */
  public static Discovery buildDefaultDiscovery(ParsedRecord record) {
    ParsedRecord tempRecord = ParsedRecord.newBuilder(record).setDiscovery(null).build();
    return fillInDefaults(tempRecord).getDiscovery();
  }

  /**
   * Builds a new {@link Discovery} object containing only default values which can be derived from the input values.
   * @param type
   * @param fileInfo
   * @param fileLocations
   * @param relationships
   * @return The default {@link Discovery} object with as many fields filled in as can be derived from the inputs.
   */
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

  /**
   * If the input builder's Discovery object does not already have a title,
   * apply {@link #defaultTitle(ParsedRecord.Builder) the default} to it.
   * @param builder The input builder
   * @return An updated builder
   */
  public static ParsedRecord.Builder setDefaultTitle(ParsedRecord.Builder builder) {
    if (discoveryStringMissing(builder, Discovery::getTitle)) {
      Discovery discovery = getDiscoveryBuilder(builder).setTitle(defaultTitle(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  /**
   * The default title is the name of the file from the {@link FileInformation}
   * @param builder The input builder
   * @return The default title
   */
  public static String defaultTitle(ParsedRecord.Builder builder) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getFileInformation)
        .map(FileInformation::getName)
        .orElse(null);
  }

  /**
   * If the input builder's Discovery object does not already have a fileIdentifier,
   * apply {@link #defaultFileId(ParsedRecord.Builder) the default} to it.
   * @param builder The input builder
   * @return An updated builder
   */
  public static ParsedRecord.Builder setDefaultFileId(ParsedRecord.Builder builder) {
    if (discoveryStringMissing(builder, Discovery::getFileIdentifier)) {
      Discovery discovery = getDiscoveryBuilder(builder).setFileIdentifier(defaultFileId(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  /**
   * The default fileIdentifier is the name of the file from the {@link FileInformation}
   * @param builder The input builder
   * @return The default fileIdentifier
   */
  public static String defaultFileId(ParsedRecord.Builder builder) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getFileInformation)
        .map(FileInformation::getName)
        .orElse(null);
  }

  /**
   * If the input builder's Discovery object does not already have a parentIdentifier,
   * apply {@link #defaultParentId(ParsedRecord.Builder) the default} to it.
   * @param builder The input builder
   * @return An updated builder
   */
  public static ParsedRecord.Builder setDefaultParentId(ParsedRecord.Builder builder) {
    if (discoveryStringMissing(builder, Discovery::getParentIdentifier)) {
      Discovery discovery = getDiscoveryBuilder(builder).setParentIdentifier(defaultParentId(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  /**
   * The default parentIdentifier is the ID from the first COLLECTION relationship in the record's relationships
   * @param builder The input builder
   * @return The default parentIdentifier
   */
  public static String defaultParentId(ParsedRecord.Builder builder) {
    List<Relationship> relationships = Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getRelationships)
        .filter(it -> !it.isEmpty())
        .orElse(Collections.emptyList());
    return relationships.stream()
        .filter(r -> r.getType() == RelationshipType.COLLECTION)
        .findFirst()
        .map(Relationship::getId)
        .orElse(null);
  }

  /**
   * If the input builder's Discovery object does not already have a hierarchyLevelName, apply {@link #defaultHierarchyName(ParsedRecord.Builder) the default} to it.
   * @param builder The input builder
   * @return An updated builder
   */
  public static ParsedRecord.Builder setDefaultHierarchyName(ParsedRecord.Builder builder) {
    if (discoveryStringMissing(builder, Discovery::getHierarchyLevelName)) {
      Discovery discovery = getDiscoveryBuilder(builder).setHierarchyLevelName(defaultHierarchyName(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  /**
   * The default hierarchyLevelName is the lower-cased name of the record's {@link RecordType}
   * @param builder The input builder
   * @return The default hierarchyLevelName
   */
  public static String defaultHierarchyName(ParsedRecord.Builder builder) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getType)
        .map(RecordType::name)
        .map(String::toLowerCase)
        .orElse(null);
  }

  /**
   * Sets the {@link Link}s on a {@link ParsedRecord} builder by merging together any existing
   * {@link Discovery#getLinks() Discovery links} with the {@link #defaultLinks(ParsedRecord.Builder) defaultLinks}
   * built from the {@link ParsedRecord#getFileLocations() fileLocations}. Any existing discovery links
   * with a URL that matches a default link will override that default in the result.
   * @param builder The input builder
   * @return An updated builder
   */
  public static ParsedRecord.Builder setDefaultLinks(ParsedRecord.Builder builder) {
    Map<String, Link> defaultLinksByUrl = defaultLinks(builder).stream()
        .collect(Collectors.toMap(Link::getLinkUrl, Function.identity()));
    // add existing discovery links, overriding defaults based on URL
    if (!discoveryListEmpty(builder, Discovery::getLinks)) {
      builder.getDiscovery().getLinks().forEach(l -> defaultLinksByUrl.put(l.getLinkUrl(), l));
    }
    Discovery discovery = getDiscoveryBuilder(builder)
        .setLinks(new ArrayList<>(defaultLinksByUrl.values()))
        .build();
    builder.setDiscovery(discovery);
    return builder;
  }

  /**
   * Builds a list of {@link Link links} from all the {@link FileLocation fileLocations} of type
   * {@link FileLocationType#ACCESS ACCESS} that are neither {@link FileLocation#getRestricted() restricted}
   * nor {@link FileLocation#getDeleted() deleted}.
   * @param builder
   * @return The list of links or an empty list if there are no acceptable fileLocations.
   */
  public static List<Link> defaultLinks(ParsedRecord.Builder builder) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getFileLocations)
        .orElse(Collections.emptyMap())
        .values()
        .stream()
        .filter(v -> v.getType() == FileLocationType.ACCESS && !v.getRestricted() && !v.getDeleted())
        .map(v -> Link.newBuilder()
            .setLinkProtocol(v.getServiceType())
            .setLinkUrl(v.getUri())
            .setLinkFunction("download")
            .build())
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * If the input builder's Discovery object does not already have any dataFormats, apply {@link #defaultDataFormats(ParsedRecord.Builder) the default} to it.
   * @param builder The input builder
   * @return An updated builder
   */
  public static ParsedRecord.Builder setDefaultDataFormats(ParsedRecord.Builder builder) {
    if (discoveryListEmpty(builder, Discovery::getDataFormats)) {
      Discovery discovery = getDiscoveryBuilder(builder).setDataFormats(defaultDataFormats(builder)).build();
      builder.setDiscovery(discovery);
    }
    return builder;
  }

  /**
   * The default list of data formats is a list of one {@link DataFormat} object with its name set to the value of
   * {@link FileInformation#getFormat()}, or an empty list if no such value exists.
   * @param builder The input builder
   * @return The list of data formats or an empty list if there is no fileLocation.format
   */
  public static List<DataFormat> defaultDataFormats(ParsedRecord.Builder builder) {
    DataFormat format = Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getFileInformation)
        .map(FileInformation::getFormat)
        .filter(it -> !it.isEmpty())
        .map(f -> DataFormat.newBuilder().setName(f).build())
        .orElse(null);
    return Stream.of(format)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Checks whether or not the record builder has a Discovery object where the given string field is present and not empty.
   * @param builder The input builder
   * @param discoveryStringGetter A function reference to a {@link Discovery} getter which returns a string
   * @return true if the given string field is missing or empty
   */
  private static boolean discoveryStringMissing(ParsedRecord.Builder builder, Function<Discovery, String> discoveryStringGetter) {
    return Optional.ofNullable(builder)
        .map(ParsedRecord.Builder::getDiscovery)
        .map(discoveryStringGetter)
        .map(String::isEmpty)
        .orElse(true);
  }

  /**
   * Checks whether or not the record builder has a Discovery object where the given List field is present and not empty.
   * @param builder The input builder
   * @param discoveryListGetter A function reference to a {@link Discovery} getter which returns a List
   * @return true if the given list field is missing or empty
   */
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
