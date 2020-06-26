package org.cedar.schemas.analyze;

import org.cedar.schemas.avro.psi.Discovery;
import org.cedar.schemas.avro.psi.TemporalBoundingAnalysis;
import org.cedar.schemas.avro.psi.TimeRangeDescriptor;
import org.cedar.schemas.avro.psi.ValidDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.UnsupportedTemporalTypeException;

import static org.cedar.schemas.avro.psi.TimeRangeDescriptor.*;

public class Temporal {
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

  public static TemporalAccessor parseDate(String date) {
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

  public static Long parseLong(String number) {
    try {
      return Long.parseLong(number);
    } catch (Exception e) {
      return null;
    }
  }

  public static Integer extractField(TemporalAccessor parsedDate, ChronoField field) {
    // Safely extract date field values for final fields. Assigns value to null if a specific parsedDate (such as java.time.Year when parsing paleo dates) does not support fields.
    if (parsedDate == null) {
      return null;
    }
    try {
      return parsedDate.get(field);
    } catch (UnsupportedTemporalTypeException e) {
      return null;
    }
  }

  public static TemporalBoundingAnalysis analyzeBounding(Discovery metadata) {
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
      builder.setBeginYear(beginInfo.year);
      builder.setBeginDayOfYear(beginInfo.dayOfYear);
      builder.setBeginDayOfMonth(beginInfo.dayOfMonth);
      builder.setBeginMonth(beginInfo.month);

      builder.setEndDescriptor(endInfo.descriptor);
      builder.setEndPrecision(endInfo.precision);
      builder.setEndIndexable(endInfo.indexable);
      builder.setEndZoneSpecified(endInfo.zoneSpecified);
      builder.setEndUtcDateTimeString(endInfo.utcDateTimeString);
      builder.setEndYear(endInfo.year);
      builder.setEndDayOfYear(endInfo.dayOfYear);
      builder.setEndDayOfMonth(endInfo.dayOfMonth);
      builder.setEndMonth(endInfo.month);

      builder.setInstantDescriptor(instantInfo.descriptor);
      builder.setInstantPrecision(instantInfo.precision);
      builder.setInstantIndexable(instantInfo.indexable);
      builder.setInstantZoneSpecified(instantInfo.zoneSpecified);
      builder.setInstantUtcDateTimeString(instantInfo.utcDateTimeString);
      builder.setInstantYear(instantInfo.year);
      builder.setInstantDayOfYear(instantInfo.dayOfYear);
      builder.setInstantDayOfMonth(instantInfo.dayOfMonth);
      builder.setInstantMonth(instantInfo.month);
      builder.setInstantEndDayOfYear(instantInfo.endDayOfYear);
      builder.setInstantEndDayOfMonth(instantInfo.endDayOfMonth);
      builder.setInstantEndMonth(instantInfo.endMonth);

      builder.setRangeDescriptor(rangeDescriptor);
    }

    return builder.build();
  }

  static boolean indexable(Long year) {
    // Year must be in the range [-292_275_055, 292_278_994] in order to be parsed as a date by ES (Joda time magic number). However,
    // this number is a bit arbitrary, and prone to change when ES switches to the Java time library (minimum supported year
    // being -999,999,999). We will limit the year ourselves instead to -100,000,000 -- since this is a fairly safe bet for
    // supportability across many date libraries if the utcDateTime ends up used as is by a downstream app.
    return year >= -100_000_000L;
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

    // A time range cannot be described as an error exists with one or more dates:
    if(begin == ValidDescriptor.INVALID ||
        end == ValidDescriptor.INVALID ||
        instant == ValidDescriptor.INVALID) {
      return NOT_APPLICABLE;
    }

    // Dates are all undefined so range is undefined:
    if (begin == ValidDescriptor.UNDEFINED &&
        end == ValidDescriptor.UNDEFINED &&
        instant == ValidDescriptor.UNDEFINED) {
      return UNDEFINED;
    }
    // If begin is valid but end is undefined, this indicates an ongoing range:
    if (begin == ValidDescriptor.VALID &&
        end == ValidDescriptor.UNDEFINED &&
        instant == ValidDescriptor.UNDEFINED) {
      return ONGOING;
    }
    // Valid instant is straightforward:
    if (begin == ValidDescriptor.UNDEFINED &&
        end == ValidDescriptor.UNDEFINED &&
        instant == ValidDescriptor.VALID) {
      return INSTANT;
    }
    // Dates describe more than one valid range descriptor, which is ambiguous:
    if ( ( begin == ValidDescriptor.VALID && end == ValidDescriptor.VALID && instant == ValidDescriptor.VALID ) ||
        ( begin == ValidDescriptor.VALID && end == ValidDescriptor.UNDEFINED && instant == ValidDescriptor.VALID ) ) {
      return AMBIGUOUS;
    }

    // Begin and end dates are independently valid but based on how they compare to each other can describe very
    // different range types:
    if (begin == ValidDescriptor.VALID &&
        end == ValidDescriptor.VALID &&
        instant == ValidDescriptor.UNDEFINED) {
      try {
        int comparator = beginInfo.compareTo(endInfo);
        TimeRangeDescriptor descriptor;
        switch (comparator) {
          case -1: descriptor = BOUNDED;
                   break;
          case 0:  descriptor = INSTANT;
                   break;
          case 1:  descriptor = BACKWARDS;
                   break;
          default: descriptor = INVALID;
                   break;
        }
        return descriptor;
      } catch(DateTimeException e) {
        return INVALID;
      }
    }

    // Covers undefined begin date with valid end date which is meaningless, regardless of presence of an instant date
    return INVALID;
  }

}
