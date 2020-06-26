package org.cedar.schemas.analyze;

import org.cedar.schemas.avro.psi.ValidDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.Year;
import java.time.YearMonth;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.cedar.schemas.avro.psi.TimeRangeDescriptor.*;

import static org.cedar.schemas.analyze.Temporal.*;

public class DateInfo implements Comparable<DateInfo> {
  private static final Logger log = LoggerFactory.getLogger(Analyzers.class);

  public final ValidDescriptor descriptor;
  public final String precision;
  public final boolean indexable;
  public final String zoneSpecified;
  public final String utcDateTimeString;
  public final String endUtcDateTimeString;
  public final Long year;
  public Integer dayOfYear; // values 1 - 366 // TODO temp remove final from these fields, just to sanity check things
  public Integer dayOfMonth; // values 1 - 31
  public Integer month; // values 1 - 12

  // special stuff for dealing with how we interpret year or month precision instants:
  public Integer endDayOfYear;
  public Integer endDayOfMonth;
  public Integer endMonth;

  public DateInfo(String dateString, boolean start) {
    if (dateString == null || dateString.length() == 0) {
      descriptor = ValidDescriptor.UNDEFINED;
      precision = null;
      indexable = true;
      zoneSpecified = null;
      utcDateTimeString = null;
      endUtcDateTimeString = null;
      year = null;
      dayOfYear = null;
      dayOfMonth = null;
      month = null;
      return;
    }

    Long longDate = parseLong(dateString);
    TemporalAccessor parsedDate = parseDate(dateString);

    Integer yearValue = extractField(parsedDate, ChronoField.YEAR);
    if (yearValue == null) {
      year = longDate;
    } else {
      year = (long) yearValue;
    }
    dayOfYear = extractField(parsedDate, ChronoField.DAY_OF_YEAR);
    dayOfMonth = extractField(parsedDate, ChronoField.DAY_OF_MONTH);
    month = extractField(parsedDate, ChronoField.MONTH_OF_YEAR);
    if (parsedDate != null && dayOfMonth == null && month != null) { // TODO or parsedDate instanceof YearMonth
      endMonth = month;
      dayOfMonth = 1;
      endDayOfMonth = ((YearMonth)parsedDate).lengthOfMonth();
      dayOfYear = ((YearMonth)parsedDate).atDay(1).getDayOfYear();
      endDayOfYear = ((YearMonth)parsedDate).atEndOfMonth().getDayOfYear();
      // endUtcDateTimeString = utcDateTimeString(((YearMonth)parsedDate).atEndOfMonth(), false);
    } else if (parsedDate != null && month == null && year != null) { // TODO or instanceof Year?
      dayOfMonth = 1;
      dayOfYear = 1;
      endDayOfYear = ((Year)parsedDate).length(); // number of days in the year, including leap years
      endDayOfMonth = 31;
      month = 1;
      endMonth = 12;

      // endUtcDateTimeString = utcDateTimeString(((Year)parsedDate).atDay(endDayOfYear), false);
    } else {
      endMonth = month;
      endDayOfMonth = dayOfMonth;
      endDayOfYear = dayOfYear;
      // endUtcDateTimeString = utcDateTimeString;
    }

    if (longDate != null && !indexable(longDate)) {
      descriptor = ValidDescriptor.VALID;
      precision = ChronoUnit.YEARS.toString();
      indexable = indexable(longDate);
      zoneSpecified = timezone(longDate);
      utcDateTimeString = utcDateTimeString(longDate, start);
      endUtcDateTimeString = utcDateTimeString(longDate, false);
    }
    else if (parsedDate != null) {
      descriptor = ValidDescriptor.VALID;
      precision = precision(parsedDate);
      indexable = true;
      zoneSpecified = timezone(parsedDate);
      utcDateTimeString = utcDateTimeString(parsedDate, start);
      endUtcDateTimeString = utcDateTimeString(parsedDate, false);
    }
    else {
      descriptor = ValidDescriptor.INVALID;
      precision = null;
      indexable = false;
      zoneSpecified = null;
      utcDateTimeString = null;
      endUtcDateTimeString = null;
    }
  }

  @Override
  public int compareTo(DateInfo o) {
    boolean thisIndexable = this.indexable;
    boolean oIndexable = o.indexable;
    boolean thisIsYears = ChronoUnit.YEARS.toString().equals(this.precision);
    boolean oIsYears = ChronoUnit.YEARS.toString().equals(o.precision);

    if (thisIndexable && oIndexable) {
      // Compare actual dates with UTC string
      ZonedDateTime thisDate = ZonedDateTime.parse(this.utcDateTimeString);
      ZonedDateTime oDate = ZonedDateTime.parse(o.utcDateTimeString);
      if (thisDate.isEqual(oDate)) {
        return 0;
      } else {
        return thisDate.isBefore(oDate) ? -1 : 1;
      }
    }
    else if ((thisIsYears && oIsYears) || (thisIsYears && oIndexable) || (thisIndexable && oIsYears)) {
      // Compare years only as longs; parse both as string objects since both may not be just a long.
      // Watch out for negative years...
      String thisYearText = this.utcDateTimeString.substring(0, this.utcDateTimeString.indexOf('-', 1));
      String oYearText = o.utcDateTimeString.substring(0, o.utcDateTimeString.indexOf('-', 1));
      Long thisYear = Long.parseLong(thisYearText);
      Long oYear = Long.parseLong(oYearText);
      if (thisYear == oYear) {
        return 0;
      } else {
        return thisYear < oYear ? -1 : 1;
      }
    }
    else {
      // One or both has an INVALID search format that is not just due to a paleo year
      throw new DateTimeException("One or both dates being compared have an INVALID format.");
    }
  }
}
