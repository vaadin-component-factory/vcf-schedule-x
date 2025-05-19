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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.CurrentTimeIndicatorConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.DayBoundaries;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.DrawOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.ICal;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.MonthGridOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.ScrollControllerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.WeekOptions;

class ConfigurationSerializationTest {

  @Test
  void testBasicConfigurationSerialization() {
    Configuration config = new Configuration();
    config.setLocale("es");
    config.setSelectedDate(LocalDate.of(2025, 5, 20));
    config.setFirstDayOfWeek(1);

    JsonObject json = Json.parse(config.getJson());

    assertEquals("es", json.getString("locale"));
    assertEquals("2025-05-20", json.getString("selectedDate"));
    assertEquals(1, json.getNumber("firstDayOfWeek"));
  }

  @Test
  void testDayBoundariesSerialization() {
    Configuration config = new Configuration();
    DayBoundaries db = new DayBoundaries();
    db.setStart(LocalTime.of(6, 0));
    db.setEnd(LocalTime.of(18, 0));
    config.setDayBoundaries(db);

    JsonObject json = Json.parse(config.getJson());
    JsonObject boundaries = json.getObject("dayBoundaries");

    assertEquals("06:00", boundaries.getString("start"));
    assertEquals("18:00", boundaries.getString("end"));
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

    JsonObject json = Json.parse(config.getJson());
    JsonObject week = json.getObject("weekOptions");

    assertEquals(800, week.getNumber("gridHeight"));
    assertEquals(5, week.getNumber("nDays"));
    assertEquals(90, week.getNumber("eventWidth"));
    assertFalse(week.getBoolean("eventOverlap"));
    assertEquals("2-digit", week.getObject("timeAxisFormatOptions").getString("minute"));
  }

  @Test
  void testMonthGridOptionsSerialization() {
    Configuration config = new Configuration();
    MonthGridOptions mo = new MonthGridOptions();
    mo.setnEventsPerDay(3);
    config.setMonthGridOptions(mo);

    JsonObject json = Json.parse(config.getJson());
    assertEquals(3, json.getObject("monthGridOptions").getNumber("nEventsPerDay"));
  }

  @Test
  void testDrawOptionsSerialization() {
    Configuration config = new Configuration();
    DrawOptions draw = new DrawOptions();
    draw.setDefaultTitle("New");
    draw.setSnapDrawDuration(
        org.vaadin.addons.componentfactory.schedulexcalendar.util.TimeInterval.MIN_30);
    config.setDrawOptions(draw);

    JsonObject json = Json.parse(config.getJson());
    JsonObject drawJson = json.getObject("drawOptions");

    assertEquals("New", drawJson.getString("defaultTitle"));
    assertEquals(30, drawJson.getNumber("snapDrawDuration"));
  }

  @Test
  void testICalSerialization() {
    Configuration config = new Configuration();
    ICal iCal = new ICal();
    iCal.setiCal("BEGIN:VCALENDAR...");
    config.setiCal(iCal);

    JsonObject json = Json.parse(config.getJson());
    assertEquals("BEGIN:VCALENDAR...", json.getObject("iCal").getString("iCal"));
  }

  @Test
  void testCurrentTimeIndicatorConfig() {
    Configuration config = new Configuration();
    CurrentTimeIndicatorConfig current = new CurrentTimeIndicatorConfig();
    current.setFullWeekWidth(true);
    current.setTimeZoneOffset(-180);
    config.setCurrentTimeIndicatorConfig(current);

    JsonObject json = Json.parse(config.getJson());
    JsonObject timeJson = json.getObject("currentTimeIndicatorConfig");

    assertTrue(timeJson.getBoolean("fullWeekWidth"));
    assertEquals(-180, timeJson.getNumber("timeZoneOffset"));
  }

  @Test
  void testScrollControllerConfig() {
    Configuration config = new Configuration();
    ScrollControllerConfig scroll = new ScrollControllerConfig();
    scroll.setInitialScroll(LocalTime.of(8, 30));
    config.setScrollControllerConfig(scroll);

    JsonObject json = Json.parse(config.getJson());
    assertEquals("08:30", json.getObject("scrollControllerConfig").getString("initialScroll"));
  }

}
