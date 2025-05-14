/*
 * Copyright 2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.addons.componentfactory.schedulexcalendar.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.RecurrenceRule.Day;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.RecurrenceRule.Frequency;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.RecurrenceRule.Until;

class RecurrenceRuleTest {

  @Test
  void testDailyRuleWithCountAndInterval() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.DAILY);
    rule.setCount(5);
    rule.setInterval(2);

    String expected = "FREQ=DAILY;COUNT=5;INTERVAL=2";
    assertEquals(expected, rule.getRule());
  }

  @Test
  void testWeeklyRuleWithByDay() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.WEEKLY);
    rule.setByDay(Arrays.asList(Day.MO, Day.WE, Day.FR));

    String expected = "FREQ=WEEKLY;BYDAY=MO,WE,FR";
    assertEquals(expected, rule.getRule());
  }

  @Test
  void testMonthlyRuleWithByMonthDay() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.MONTHLY);
    rule.setByMonthDay(Arrays.asList(1, 15, 31));

    String expected = "FREQ=MONTHLY;BYMONTHDAY=1,15,31";
    assertEquals(expected, rule.getRule());
  }

  @Test
  void testUntilWithDateOnly() {
    LocalDate date = LocalDate.of(2025, 12, 25);
    RecurrenceRule rule = new RecurrenceRule(Frequency.YEARLY);
    rule.setUntil(new Until(date));

    String expected = "FREQ=YEARLY;UNTIL=20251225";
    assertEquals(expected, rule.getRule());
  }

  @Test
  void testUntilWithDateTime() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    LocalTime time = LocalTime.of(14, 30, 0);
    RecurrenceRule rule = new RecurrenceRule(Frequency.DAILY);
    rule.setUntil(new Until(date, time));

    String expected = "FREQ=DAILY;UNTIL=20250601T143000";
    assertEquals(expected, rule.getRule());
  }

  @Test
  void testWorkweekStartDay() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.WEEKLY);
    rule.setWkst(Day.MO);

    String expected = "FREQ=WEEKLY;WKST=MO";
    assertEquals(expected, rule.getRule());
  }

  @Test
  void testComplexRuleCombination() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.WEEKLY);
    rule.setInterval(1);
    rule.setByDay(List.of(Day.TU, Day.TH));
    rule.setUntil(new Until(LocalDate.of(2025, 8, 1)));
    rule.setWkst(Day.SU);

    String expected = "FREQ=WEEKLY;INTERVAL=1;BYDAY=TU,TH;UNTIL=20250801;WKST=SU";
    assertEquals(expected, rule.getRule());
  }

  @Test
  void testEmptyByDayAndByMonthDayShouldNotAppear() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.MONTHLY);
    rule.setByDay(Collections.emptyList());
    rule.setByMonthDay(Collections.emptyList());

    assertEquals("FREQ=MONTHLY", rule.getRule());
  }

  @Test
  void testNullOptionalFieldsShouldNotCauseException() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.DAILY);

    // All optional fields are null by default
    assertDoesNotThrow(rule::getRule);
    assertEquals("FREQ=DAILY", rule.getRule());
  }

  @Test
  void testSetNullByDayOrByMonthDayDoesNotThrow() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.WEEKLY);
    rule.setByDay(null);
    rule.setByMonthDay(null);

    assertEquals("FREQ=WEEKLY", rule.getRule());
  }

  @Test
  void testSetNullUntilDoesNotThrow() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.WEEKLY);
    rule.setUntil(null);

    assertEquals("FREQ=WEEKLY", rule.getRule());
  }

  @Test
  void testUntilFormattingWithMidnightTime() {
    LocalDate date = LocalDate.of(2025, 10, 1);
    RecurrenceRule rule = new RecurrenceRule(Frequency.DAILY);
    rule.setUntil(new Until(date, LocalTime.MIDNIGHT));

    assertEquals("FREQ=DAILY;UNTIL=20251001T000000", rule.getRule());
  }

  @Test
  void testUntilFormattingWithSingleDigitDateParts() {
    LocalDate date = LocalDate.of(2025, 1, 5);
    RecurrenceRule rule = new RecurrenceRule(Frequency.MONTHLY);
    rule.setUntil(new Until(date));

    assertEquals("FREQ=MONTHLY;UNTIL=20250105", rule.getRule());
  }

  @Test
  void testSettingAllNullsYieldsValidMinimalRule() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.YEARLY);
    rule.setCount(null);
    rule.setInterval(null);
    rule.setByDay(null);
    rule.setByMonthDay(null);
    rule.setUntil(null);
    rule.setWkst(null);

    assertEquals("FREQ=YEARLY", rule.getRule());
  }

  @Test
  void testSingleByDayAndByMonthDay() {
    RecurrenceRule rule = new RecurrenceRule(Frequency.MONTHLY);
    rule.setByDay(List.of(Day.FR));
    rule.setByMonthDay(List.of(15));

    assertEquals("FREQ=MONTHLY;BYDAY=FR;BYMONTHDAY=15", rule.getRule());
  }
}
