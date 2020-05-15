package org.cedar.schemas.analyze;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cedar.schemas.avro.psi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.cedar.schemas.avro.psi.TimeRangeDescriptor.*;
import org.cedar.schemas.analyze.DateInfo;

public class Analyzers {
  private static final Logger log = LoggerFactory.getLogger(Analyzers.class);

  public static ParsedRecord addAnalysis(ParsedRecord record) {
    if (record == null) {
      return null; // pass through
    }
    try {
      return ParsedRecord.newBuilder(record)
          .setAnalysis(analyze(record.getDiscovery()))
          .build();
    } catch (Exception e) {
      log.error("An error occurred during analysis", e);
      List<ErrorEvent> errors = record.getErrors() != null ? record.getErrors() : new ArrayList<ErrorEvent>();
      ErrorEvent error = ErrorEvent.newBuilder()
          .setTitle("Analysis failed")
          .setDetail(ExceptionUtils.getRootCauseMessage(e).trim())
          .build();
      errors.add(error);
      return ParsedRecord.newBuilder(record).setErrors(errors).build();
    }
  }

  public static Analysis analyze(Discovery discovery) {
    log.debug("Analyzing record: {}", discovery);
    if (discovery == null) {
      return null;
    }
    return Analysis.newBuilder()
        .setIdentification(analyzeIdentifiers(discovery))
        .setTemporalBounding(Temporal.analyzeBounding(discovery))
        .setSpatialBounding(analyzeSpatialBounding(discovery))
        .setTitles(analyzeTitles(discovery))
        .setDescription(analyzeDescription(discovery))
        .setThumbnail(analyzeThumbnail(discovery))
        .setDataAccess(analyzeDataAccess(discovery))
        .build();
  }

  public static IdentificationAnalysis analyzeIdentifiers(Discovery metadata) {
    if (metadata == null) {
      return null;
    }
    StringInfo fileIdInfo = new StringInfo(metadata.getFileIdentifier());
    StringInfo doiInfo = new StringInfo(metadata.getDoi());
    StringInfo parentIdInfo = new StringInfo(metadata.getParentIdentifier());
    StringInfo hierarchyInfo = new StringInfo(metadata.getHierarchyLevelName());

    return IdentificationAnalysis.newBuilder()
        .setFileIdentifierExists(fileIdInfo.exists)
        .setFileIdentifierString(fileIdInfo.value)
        .setDoiExists(doiInfo.exists)
        .setDoiString(doiInfo.value)
        .setParentIdentifierExists(parentIdInfo.exists)
        .setParentIdentifierString(parentIdInfo.value)
        .setHierarchyLevelNameExists(hierarchyInfo.exists)
        .setIsGranule(hierarchyInfo.exists && hierarchyInfo.value.equals("granule") && parentIdInfo.exists)
        .build();
  }

  public static SpatialBoundingAnalysis analyzeSpatialBounding(Discovery metadata) {
    SpatialBoundingAnalysis.Builder builder = SpatialBoundingAnalysis.newBuilder();
    if (metadata != null) {
      ValidateGeometry.Result validateGeometry = ValidateGeometry.validateGeometry(metadata);
      builder.setSpatialBoundingExists(validateGeometry.exists)
          .setIsValid(validateGeometry.isValid)
          .setValidationError(validateGeometry.error);
    }
    return builder.build();
  }

  public static TitleAnalysis analyzeTitles(Discovery metadata) {
    TitleAnalysis.Builder builder = TitleAnalysis.newBuilder();
    if (metadata != null) {
      StringInfo titleAnalysis = new StringInfo(metadata.getTitle());
      StringInfo altAnalysis = new StringInfo(metadata.getAlternateTitle());
      builder.setTitleExists(titleAnalysis.exists);
      builder.setTitleCharacters(titleAnalysis.characters);
      builder.setAlternateTitleExists(altAnalysis.exists);
      builder.setAlternateTitleCharacters(altAnalysis.characters);
      builder.setTitleFleschReadingEaseScore(titleAnalysis.readingEase);
      builder.setTitleFleschKincaidReadingGradeLevel(titleAnalysis.gradeLevel);
      builder.setAlternateTitleFleschReadingEaseScore(altAnalysis.readingEase);
      builder.setAlternateTitleFleschKincaidReadingGradeLevel(altAnalysis.gradeLevel);
    }
    return builder.build();
  }

  public static DescriptionAnalysis analyzeDescription(Discovery metadata) {
    DescriptionAnalysis.Builder builder = DescriptionAnalysis.newBuilder();
    if (metadata != null) {
      StringInfo analysis = new StringInfo(metadata.getDescription());
      builder.setDescriptionExists(analysis.exists);
      builder.setDescriptionCharacters(analysis.characters);
      builder.setDescriptionFleschReadingEaseScore(analysis.readingEase);
      builder.setDescriptionFleschKincaidReadingGradeLevel(analysis.gradeLevel);
    }
    return builder.build();
  }

  public static ThumbnailAnalysis analyzeThumbnail(Discovery metadata) {
    ThumbnailAnalysis.Builder builder = ThumbnailAnalysis.newBuilder();
    if (metadata != null) {
      builder.setThumbnailExists(metadata.getThumbnail() != null);
    }
    return builder.build();
  }

  public static DataAccessAnalysis analyzeDataAccess(Discovery metadata) {
    DataAccessAnalysis.Builder builder = DataAccessAnalysis.newBuilder();
    if (metadata != null) {
      builder.setDataAccessExists(metadata.getLinks().size() > 0);
    }
    return builder.build();
  }

  //-- Helpers

  static class StringInfo {
    public final String value;
    public final boolean exists;
    public final int characters;
    public final Double readingEase;
    public final Double gradeLevel;

    public StringInfo(String input) {
      value = input;
      exists = input != null && input.length() > 0;
      characters = exists ? input.length() : 0;
      readingEase = exists ? ReadingLevel.FleschReadingEaseScore(input) : null;
      gradeLevel = exists ? ReadingLevel.FleschKincaidReadingGradeLevel(input) : null;
    }
  }

}
