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
package org.vaadin.addons.componentfactory.schedulexcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.CurrentTimeIndicatorConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.DayBoundaries;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.DrawOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.ICal;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.MonthGridOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.ScrollControllerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.WeekOptions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class ConfigurationSerializationTest {

  private static final JsonMapper MAPPER = JsonMapper.builder().build();

  @Test
  void testBasicConfigurationSerialization() {
    Configuration config = new Configuration();
    config.setLocale(Locale.US);
    config.setSelectedDate(LocalDate.of(2025, 5, 20));
    config.setFirstDayOfWeek(1);

    JsonNode json = MAPPER.readTree(config.getJson());

    assertEquals("en-US", json.get("locale").asString());
    assertEquals("2025-05-20", json.get("selectedDate").asString());
    assertEquals(1, json.get("firstDayOfWeek").asInt());
  }

  @Test
  void testDayBoundariesSerialization() {
    Configuration config = new Configuration();
    DayBoundaries db = new DayBoundaries();
    db.setStart(LocalTime.of(6, 0));
    db.setEnd(LocalTime.of(18, 0));
    config.setDayBoundaries(db);

    JsonNode json = MAPPER.readTree(config.getJson());
    JsonNode boundaries = json.get("dayBoundaries");

    assertEquals("06:00", boundaries.get("start").asString());
    assertEquals("18:00", boundaries.get("end").asString());
  }

  @Test
  void testWeekOptionsSerialization() {
    Configuration config = new Configuration();
    WeekOptions wo = new WeekOptions();
    wo.setGridHeight(800);
    wo.setnDays(5);
    wo.setEventWidth(90);
    wo.setTimeAxisFormatOptions(Map.of("hour", "2-digit", "minute", "2-digit"));
    wo.setEventOverlap(false);
    config.setWeekOptions(wo);

    JsonNode json = MAPPER.readTree(config.getJson());
    JsonNode week = json.get("weekOptions");

    assertEquals(800, week.get("gridHeight").asInt());
    assertEquals(5, week.get("nDays").asInt());
    assertEquals(90, week.get("eventWidth").asInt());
    assertFalse(week.get("eventOverlap").asBoolean());
    assertEquals("2-digit", week.get("timeAxisFormatOptions").get("minute").asString());
  }

  @Test
  void testMonthGridOptionsSerialization() {
    Configuration config = new Configuration();
    MonthGridOptions mo = new MonthGridOptions();
    mo.setnEventsPerDay(3);
    config.setMonthGridOptions(mo);

    JsonNode json = MAPPER.readTree(config.getJson());
    assertEquals(3, json.get("monthGridOptions").get("nEventsPerDay").asInt());
  }

  @Test
  void testDrawOptionsSerialization() {
    Configuration config = new Configuration();
    DrawOptions draw = new DrawOptions();
    draw.setDefaultTitle("New");
    draw.setSnapDrawDuration(
        org.vaadin.addons.componentfactory.schedulexcalendar.util.TimeInterval.MIN_30);
    config.setDrawOptions(draw);

    JsonNode json = MAPPER.readTree(config.getJson());
    JsonNode drawJson = json.get("drawOptions");

    assertEquals("New", drawJson.get("defaultTitle").asString());
    assertEquals(30, drawJson.get("snapDrawDuration").asInt());
  }

  @Test
  void testICalSerialization() {
    Configuration config = new Configuration();
    ICal iCal = new ICal();
    iCal.setiCal("BEGIN:VCALENDAR...");
    config.setiCal(iCal);

    JsonNode json = MAPPER.readTree(config.getJson());
    assertEquals("BEGIN:VCALENDAR...", json.get("iCal").get("iCal").asString());
  }

  @Test
  void testCurrentTimeIndicatorConfig() {
    Configuration config = new Configuration();
    CurrentTimeIndicatorConfig current = new CurrentTimeIndicatorConfig();
    current.setFullWeekWidth(true);
    current.setTimeZoneOffset(-180);
    config.setCurrentTimeIndicatorConfig(current);

    JsonNode json = MAPPER.readTree(config.getJson());
    JsonNode timeJson = json.get("currentTimeIndicatorConfig");

    assertTrue(timeJson.get("fullWeekWidth").asBoolean());
    assertEquals(-180, timeJson.get("timeZoneOffset").asInt());
  }

  @Test
  void testScrollControllerConfig() {
    Configuration config = new Configuration();
    ScrollControllerConfig scroll = new ScrollControllerConfig();
    scroll.setInitialScroll(LocalTime.of(8, 30));
    config.setScrollControllerConfig(scroll);

    JsonNode json = MAPPER.readTree(config.getJson());
    assertEquals("08:30", json.get("scrollControllerConfig").get("initialScroll").asString());
  }

}
