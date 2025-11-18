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
package org.vaadin.addons.componentfactory.schedulexcalendar.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class that provides shared {@link DateTimeFormatter} instances for consistent date and
 * time formatting across the ScheduleX implementation.
 * <p>
 * This class is not intended to be instantiated.
 */

public final class DateTimeFormatUtils {

  /**
   * Formats dates as yyyy-MM-dd, e.g., 2025-05-12.
   */
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Formats date and time as yyyy-MM-dd HH:mm, e.g., 2025-05-12 14:30.
   */
  public static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  /**
   * Formats time as HH:mm, e.g., 14:30.
   */
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Formats dates as yyyyMMdd, e.g., 20250512
   */
  public static final DateTimeFormatter COMPACT_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd");

  /**
   * Formats times as HHmmss, e.g., 153045
   */
  public static final DateTimeFormatter COMPACT_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("HHmmss");
  
  /**
   * Parses a date string into a {@link LocalDateTime}, accepting two formats:
   * <ul>
   * <li>{@code YYYY-MM-DD HH:mm} – parsed as-is</li>
   * <li>{@code YYYY-MM-DD} – parsed as start of day (00:00), or end of day (23:59:59) if
   * {@code end} is {@code true}</li>
   * </ul>
   *
   * @param date the date string to parse
   * @param end whether to treat a date-only value as the end of the day
   * @return the parsed {@code LocalDateTime}
   */
  public static LocalDateTime parseDate(String date, boolean end) {
    LocalDateTime result;
    try {
      result = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
    } catch (Exception e) {
      result = LocalDate.parse(date, DATE_FORMATTER).atStartOfDay();
      if (end) {
        result = result.withHour(23).withMinute(59).withSecond(59);
      }
    }
    return result;
  }
  
  public static String formatZonedDateTime(String date) {
    ZonedDateTime zdt = ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    return zdt.format(DATE_TIME_FORMATTER);
  }
}
