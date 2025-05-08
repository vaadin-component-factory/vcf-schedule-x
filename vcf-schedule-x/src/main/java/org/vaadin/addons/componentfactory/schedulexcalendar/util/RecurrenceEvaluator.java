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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class to evaluate recurrence rules against a date range.
 * <p>
 * This evaluator checks whether at least one occurrence of a recurring event
 * defined by a {@link RecurrenceRule} falls within a given date range.
 * </p>
 *
 * <p>
 * Supported rule parts:
 * <ul>
 *   <li>{@code FREQ=DAILY|WEEKLY|MONTHLY|YEARLY}</li>
 *   <li>{@code INTERVAL=n}</li>
 *   <li>{@code COUNT=n}</li>
 *   <li>{@code UNTIL=YYYYMMDD or YYYYMMDDTHHMMSS}</li>
 *   <li>{@code BYDAY=MO,TU,...} (for WEEKLY)</li>
 *   <li>{@code BYMONTHDAY=1,15,...} (for MONTHLY)</li>
 * </ul>
 * </p>
 *
 * <p>
 * This evaluator does <b>not</b> generate all occurrences. It stops early once
 * it detects that an occurrence falls within the provided date range.
 * </p>
 */
public class RecurrenceEvaluator {

  /**
   * Checks whether a recurring event described by {@code rule} and starting on
   * {@code startDate} has at least one occurrence between {@code rangeStart} and
   * {@code rangeEnd}.
   *
   * @param rule       the recurrence rule
   * @param startDate  the start date of the first occurrence
   * @param rangeStart the inclusive start of the range to check
   * @param rangeEnd   the exclusive end of the range to check
   * @return true if at least one recurrence falls in the range, false otherwise
   */
  public static boolean occursInRange(RecurrenceRule rule, LocalDate startDate,
                                      LocalDate rangeStart, LocalDate rangeEnd) {

    if (rule == null || rule.getFreq() == null) {
      return false;
    }

    int interval = rule.getInterval() != null ? rule.getInterval() : 1;
    Integer count = rule.getCount();
    LocalDate until = rule.getUntil() != null ? rule.getUntil().getDate() : null;

    Set<DayOfWeek> byDays = rule.getByDay() != null
        ? rule.getByDay().stream().map(RecurrenceEvaluator::toDayOfWeek).collect(Collectors.toSet())
        : Set.of();

    Set<Integer> byMonthDays = rule.getByMonthDay() != null
        ? Set.copyOf(rule.getByMonthDay())
        : Set.of();

    LocalDate current = startDate;
    int occurrences = 0;

    while (true) {
      // Stop by COUNT
      if (count != null && occurrences >= count) {
        break;
      }

      // Stop by UNTIL
      if (until != null && current.isAfter(until)) {
        break;
      }

      boolean valid = switch (rule.getFreq()) {
        case DAILY -> true;

        case WEEKLY -> byDays.isEmpty() || byDays.contains(current.getDayOfWeek());

        case MONTHLY -> byMonthDays.isEmpty() || byMonthDays.contains(current.getDayOfMonth());

        case YEARLY -> true; // Simplified
      };

      if (valid && !current.isBefore(rangeStart) && current.isBefore(rangeEnd)) {
        return true; // Found an occurrence within range
      }

      // Advance to next occurrence
      switch (rule.getFreq()) {
        case DAILY -> current = current.plusDays(interval);
        case WEEKLY -> current = current.plusWeeks(interval);
        case MONTHLY -> current = current.plusMonths(interval);
        case YEARLY -> current = current.plusYears(interval);
      }

      occurrences++;
    }

    return false;
  }

  private static DayOfWeek toDayOfWeek(RecurrenceRule.Day day) {
    return switch (day) {
      case MO -> DayOfWeek.MONDAY;
      case TU -> DayOfWeek.TUESDAY;
      case WE -> DayOfWeek.WEDNESDAY;
      case TH -> DayOfWeek.THURSDAY;
      case FR -> DayOfWeek.FRIDAY;
      case SA -> DayOfWeek.SATURDAY;
      case SU -> DayOfWeek.SUNDAY;
    };
  }
}
