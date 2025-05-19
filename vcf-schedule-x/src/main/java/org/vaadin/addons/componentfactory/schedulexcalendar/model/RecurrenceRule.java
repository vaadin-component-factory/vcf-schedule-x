/*
 * Copyright 2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.addons.componentfactory.schedulexcalendar.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;

/**
 * Representation for the events recurrence rules.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/plugins/recurrence">Event Recurrence
 *      documentation</a>.
 * 
 */
@SuppressWarnings("serial")
public class RecurrenceRule implements Serializable {

  /**
   * FREQ supported values.
   */
  public enum Frequency {
    DAILY, WEEKLY, MONTHLY, YEARLY;
  }

  /**
   * BYDAY supported values
   */
  public enum Day {
    MO, TU, WE, TH, FR, SA, SU;
  }

  /**
   * The FREQ rule part identifies the type of recurrence rule. This rule part MUST be specified in
   * the recurrence rule.
   */
  private Frequency freq;

  /**
   * The COUNT rule part defines the number of occurrences at which to range-bound the recurrence.
   */
  private Integer count;

  /**
   * The INTERVAL rule part contains a positive integer representing at which intervals the
   * recurrence rule repeats.
   */
  private Integer interval;

  /**
   * The BYDAY rule part specifies a COMMA-separated list of days of the week; SU indicates Sunday;
   * MO indicates Monday; TU indicates Tuesday; WE indicates Wednesday; TH indicates Thursday; FR
   * indicates Friday; and SA indicates Saturday.
   * 
   * <p>
   * Compatible with {@code Frequency.DAILY} and {@code Frequency.WEEKLY}
   * </p>
   */
  private List<Day> byDay;

  /**
   * The BYMONTHDAY rule part specifies a COMMA-separated list of days of the month.
   * 
   * <p>
   * Compatible with {@code Frequency.MONTHLY}
   * </p>
   */
  private List<Integer> byMonthDay;

  private Until until;

  /**
   * The WKST rule part specifies the day on which the workweek starts. Valid values are MO, TU, WE,
   * TH, FR, SA, and SU.
   */
  private Day wkst;
  
  /**
   * Constructs a {@code RecurrenceRule} with the specified frequency value.
   * 
   * @param freq the frequency of the recurrence rule
   */
  public RecurrenceRule(Frequency freq) {
    this.freq = freq;
  }
  
  public Frequency getFreq() {
    return freq;
  }

  public void setFreq(Frequency freq) {
    this.freq = freq;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Integer getInterval() {
    return interval;
  }

  public void setInterval(Integer interval) {
    this.interval = interval;
  }

  public List<Day> getByDay() {
    return byDay;
  }

  public void setByDay(List<Day> byDay) {
    this.byDay = byDay;
  }

  public List<Integer> getByMonthDay() {
    return byMonthDay;
  }

  public void setByMonthDay(List<Integer> byMonthDay) {
    this.byMonthDay = byMonthDay;
  }

  public Until getUntil() {
    return until;
  }

  public void setUntil(Until until) {
    this.until = until;
  }

  public Day getWkst() {
    return wkst;
  }

  public void setWkst(Day wkst) {
    this.wkst = wkst;
  }

  protected String getRule() {
    StringBuilder sb = new StringBuilder("FREQ=" + this.getFreq());

    if (this.getCount() != null) {
      sb.append(";COUNT=").append(this.getCount());
    }
    if (this.getInterval() != null) {
      sb.append(";INTERVAL=").append(this.getInterval());
    }
    if (this.getByDay() != null && !this.getByDay().isEmpty()) {
      String byDay = this.getByDay().stream().map(Enum::name).collect(Collectors.joining(","));
      sb.append(";BYDAY=").append(byDay);;
    }
    if (this.getByMonthDay() != null && !this.getByMonthDay().isEmpty()) {
      String byMonthDay =
          this.getByMonthDay().stream().map(String::valueOf).collect(Collectors.joining(","));
      sb.append(";BYMONTHDAY=").append(byMonthDay);
    }
    if (this.getUntil() != null) {
      sb.append(";UNTIL=").append(this.getUntil().format());
    }
    if (this.getWkst() != null) {
      sb.append(";WKST=").append(this.getWkst());
    }

    return sb.toString();
  }

  /**
   * The UNTIL rule part defines a DATE or DATE-TIME value that bounds the recurrence rule in an
   * inclusive manner. (Floating date, for example 20240101 or date-time 20240101T120000)
   */
  public static class Until implements Serializable {

    private final LocalDate date;
    private final LocalTime time; 
    
    public Until(LocalDate date) {
      this.date = date;
      this.time = null;
    }

    public Until(LocalDate date, LocalTime time) {
      this.date = date;
      this.time = time;
    }
    
    public LocalDate getDate() {
      return date;
    }

    public LocalTime getTime() {
      return time;
    }

    /**
     * Formats to RFC-compliant string: either `YYYYMMDD` or `YYYYMMDDTHHMMSS`.
     */
    public String format() {
      DateTimeFormatter dateFmt = DateTimeFormatUtils.COMPACT_DATE_FORMATTER;
      if (time == null) {
        return date.format(dateFmt);
      } else {
        DateTimeFormatter timeFmt = DateTimeFormatUtils.COMPACT_TIME_FORMATTER;
        return date.format(dateFmt) + "T" + time.format(timeFmt);
      }
    }
  }

}
