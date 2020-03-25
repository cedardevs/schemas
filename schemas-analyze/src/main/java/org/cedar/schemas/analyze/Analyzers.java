package org.cedar.schemas.analyze;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cedar.schemas.avro.psi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.List;

import static org.cedar.schemas.avro.psi.TimeRangeDescriptor.*;

public class Analyzers {
  private static final Logger log = LoggerFactory.getLogger(Analyzers.class);

  public static final DateTimeFormatter PARSE_DATE_FORMATTER = new DateTimeFormatterBuilder()
      .appendOptional(DateTimeFormatter.ISO_ZONED_DATE_TIME)  // e.g.  2010-12-30T00:00:00Z
      .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME)  // e.g.  2010-12-30T00:00:00
      .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)       // e.g.  2010-12-30
      .appendOptional(new DateTimeFormatterBuilder()
          .appendValue(ChronoField.YEAR)                      // e.g. -200
          .optionalStart()
          .appendPattern("-MM")                               // e.g. -200-10
          .optionalEnd()
          .optionalStart()
          .appendPattern("-dd")                               // e.g. -200-01-01
          .optionalEnd()
          .toFormatter())
      .toFormatter()
      .withResolverStyle(ResolverStyle.STRICT);

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
        .setTemporalBounding(analyzeTemporalBounding(discovery))
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
        .setMatchesIdentifiers((hierarchyInfo.exists && hierarchyInfo.value.equals("granule") && parentIdInfo.exists) || (hierarchyInfo.exists && !hierarchyInfo.value.equals("granule")) || !hierarchyInfo.exists)
        .build();
  }

  public static TemporalBoundingAnalysis analyzeTemporalBounding(Discovery metadata) {
    TemporalBoundingAnalysis.Builder builder = TemporalBoundingAnalysis.newBuilder();

    if (metadata != null && metadata.getTemporalBounding() != null) {
      // Gather info
      DateInfo beginInfo = new DateInfo(metadata.getTemporalBounding().getBeginDate(), true);
      DateInfo endInfo = new DateInfo(metadata.getTemporalBounding().getEndDate(), false);
      DateInfo instantInfo = new DateInfo(metadata.getTemporalBounding().getInstant(), true);
      TimeRangeDescriptor rangeDescriptor = rangeDescriptor(beginInfo, endInfo, instantInfo);

      // Build
      builder.setBeginDescriptor(beginInfo.descriptor);
      builder.setBeginPrecision(beginInfo.precision);
      builder.setBeginIndexable(beginInfo.indexable);
      builder.setBeginZoneSpecified(beginInfo.zoneSpecified);
      builder.setBeginUtcDateTimeString(beginInfo.utcDateTimeString);

      builder.setEndDescriptor(endInfo.descriptor);
      builder.setEndPrecision(endInfo.precision);
      builder.setEndIndexable(endInfo.indexable);
      builder.setEndZoneSpecified(endInfo.zoneSpecified);
      builder.setEndUtcDateTimeString(endInfo.utcDateTimeString);

      builder.setInstantDescriptor(instantInfo.descriptor);
      builder.setInstantPrecision(instantInfo.precision);
      builder.setInstantIndexable(instantInfo.indexable);
      builder.setInstantZoneSpecified(instantInfo.zoneSpecified);
      builder.setInstantUtcDateTimeString(instantInfo.utcDateTimeString);

      builder.setRangeDescriptor(rangeDescriptor);
    }

    return builder.build();
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

  static class DateInfo {
    public final ValidDescriptor descriptor;
    public final String precision;
    public final boolean indexable;
    public final String zoneSpecified;
    public final String utcDateTimeString;

    public DateInfo(String dateString, boolean start) {
      if (dateString == null || dateString.length() == 0) {
        descriptor = ValidDescriptor.UNDEFINED;
        precision = null;
        indexable = true;
        zoneSpecified = null;
        utcDateTimeString = null;
        return;
      }

      Long longDate = parseLong(dateString);
      TemporalAccessor parsedDate = parseDate(dateString);
      if (longDate != null && !indexable(longDate)) {
        descriptor = ValidDescriptor.VALID;
        precision = precision(longDate);
        indexable = indexable(longDate);
        zoneSpecified = timezone(longDate);
        utcDateTimeString = utcDateTimeString(longDate, start);
      }
      else if (parsedDate != null) {
        descriptor = ValidDescriptor.VALID;
        precision = precision(parsedDate);
        indexable = indexable(parsedDate);
        zoneSpecified = timezone(parsedDate);
        utcDateTimeString = utcDateTimeString(parsedDate, start);
      }
      else {
        descriptor = ValidDescriptor.INVALID;
        precision = null;
        indexable = false;
        zoneSpecified = null;
        utcDateTimeString = null;
      }
    }

    private static Long parseLong(String number) {
      try {
        return Long.parseLong(number);
      } catch (Exception e) {
        return null;
      }
    }

    private static TemporalAccessor parseDate(String date) {
      try {
        return PARSE_DATE_FORMATTER.parseBest(
            date,
            ZonedDateTime::from,
            LocalDateTime::from,
            LocalDate::from,
            YearMonth::from,
            Year::from);
      } catch (Exception e) {
        return null;
      }
    }
  }

  static boolean indexable(Long year) {
    // Year must be in the range [-292_275_055, 292_278_994] in order to be parsed as a date by ES (Joda time magic number). However,
    // this number is a bit arbitrary, and prone to change when ES switches to the Java time library (minimum supported year
    // being -999,999,999). We will limit the year ourselves instead to -100,000,000 -- since this is a fairly safe bet for
    // supportability across many date libraries if the utcDateTime ends up used as is by a downstream app.
    return year >= -100_000_000L;
  }

  static boolean indexable(TemporalAccessor date) {
    return true; // if it's a parsable accessor, it's indexable
  }

  static String precision(Long year) {
    return ChronoUnit.YEARS.toString();
  }

  static String precision(TemporalAccessor date) {
    if (date == null) {
      return null;
    }
    return date.query(TemporalQueries.precision()).toString();
  }

  static String timezone(Object date) {
    return date instanceof ZonedDateTime ? ((ZonedDateTime) date).getOffset().toString() : null;
  }

  static String utcDateTimeString(TemporalAccessor parsedDate, boolean start) {
    if (parsedDate == null) {
      return null;
    }

    if (parsedDate instanceof Year) {
      LocalDateTime yearDate = start ?
          ((Year) parsedDate).atMonth(1).atDay(1).atStartOfDay() :
          ((Year) parsedDate).atMonth(12).atEndOfMonth().atTime(23, 59, 59, 999000000);
      return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(yearDate.atZone(ZoneOffset.UTC));
    }
    if (parsedDate instanceof YearMonth) {
      LocalDateTime yearMonthDate = start ?
          ((YearMonth) parsedDate).atDay(1).atStartOfDay() :
          ((YearMonth) parsedDate).atEndOfMonth().atTime(23, 59, 59, 999000000);
      return DateTimeFormatter.ISO_ZONED_DATE_TIME.format((yearMonthDate.atZone(ZoneOffset.UTC)));
    }
    if (parsedDate instanceof LocalDate) {
      LocalDateTime localDate = start ?
          ((LocalDate) parsedDate).atStartOfDay() :
          ((LocalDate) parsedDate).atTime(23, 59, 59, 999000000);
      return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(localDate.atZone(ZoneOffset.UTC));
    }
    if (parsedDate instanceof LocalDateTime) {
      return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(((LocalDateTime) parsedDate).atZone(ZoneOffset.UTC));
    }
    if (parsedDate instanceof ZonedDateTime) {
      return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(((ZonedDateTime) parsedDate).withZoneSameInstant(ZoneOffset.UTC));
    }

    return null;
  }

  static String utcDateTimeString(Long year, boolean start) {
    return start ? year.toString() + "-01-01T00:00:00Z" : year.toString() + "-12-31T23:59:59.999Z";
  }

  static TimeRangeDescriptor rangeDescriptor(DateInfo beginInfo, DateInfo endInfo, DateInfo instantInfo) {
    ValidDescriptor begin = beginInfo.descriptor;
    ValidDescriptor end = endInfo.descriptor;
    ValidDescriptor instant = instantInfo.descriptor;

    if (begin == ValidDescriptor.VALID &&
        end == ValidDescriptor.VALID &&
        instant == ValidDescriptor.UNDEFINED) {
      Boolean inOrder = beginLTEEnd(beginInfo, endInfo);
      return inOrder == null ? INVALID : inOrder ? BOUNDED : BACKWARDS;
    }
    if (begin == ValidDescriptor.VALID &&
        end == ValidDescriptor.UNDEFINED &&
        instant == ValidDescriptor.UNDEFINED) {
      return ONGOING;
    }
    if (begin == ValidDescriptor.UNDEFINED &&
        end == ValidDescriptor.UNDEFINED &&
        instant == ValidDescriptor.VALID) {
      return INSTANT;
    }
    if (begin == ValidDescriptor.UNDEFINED &&
        end == ValidDescriptor.UNDEFINED &&
        instant == ValidDescriptor.UNDEFINED) {
      return UNDEFINED;
    }

    return INVALID;
  }

  static Boolean beginLTEEnd(DateInfo beginInfo, DateInfo endInfo) {
    boolean beginIndexable = beginInfo.indexable;
    boolean endIndexable = endInfo.indexable;
    boolean beginIsYears = beginInfo.precision.equals(ChronoUnit.YEARS.toString());
    boolean endIsYears = endInfo.precision.equals(ChronoUnit.YEARS.toString());

    if (beginIndexable && endIndexable) {
      // Compare actual dates with UTC string
      ZonedDateTime beginDate = ZonedDateTime.parse(beginInfo.utcDateTimeString);
      ZonedDateTime endDate = ZonedDateTime.parse(endInfo.utcDateTimeString);
      return beginDate.isBefore(endDate) || beginDate.isEqual(endDate);
    }
    else if ((beginIsYears && endIsYears) || (beginIsYears && endIndexable) || (beginIndexable && endIsYears)) {
      // Compare years only as longs; parse both as string objects since both may not be just a long.
      // Watch out for negative years...
      String beginYearText = beginInfo.utcDateTimeString.substring(0, beginInfo.utcDateTimeString.indexOf('-', 1));
      String endYearText = endInfo.utcDateTimeString.substring(0, endInfo.utcDateTimeString.indexOf('-', 1));
      Long beginYear = Long.parseLong(beginYearText);
      Long endYear = Long.parseLong(endYearText);
      return beginYear <= endYear;
    }
    else {
      // One or both has an INVALID search format that is not just due to a paleo year
      return null;
    }
  }

}
