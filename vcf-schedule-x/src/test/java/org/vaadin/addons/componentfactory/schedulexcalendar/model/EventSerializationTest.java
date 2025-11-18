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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class EventSerializationTest {

  @Test
  void testBasicEventSerialization() {
    Event event = new Event("e1", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    event.setTitle("Meeting");
    event.setDescription("Discuss roadmap");
    event.setLocation("Zoom");
    event.setCalendarId("work");

    JsonObject json = Json.parse(event.getJson());

    assertEquals("e1", json.getString("id"));
    assertEquals("2025-06-01T09:00:00", json.getString("start"));
    assertEquals("2025-06-01T10:00:00", json.getString("end"));
    assertEquals("Meeting", json.getString("title"));
    assertEquals("Discuss roadmap", json.getString("description"));
    assertEquals("Zoom", json.getString("location"));
    assertEquals("work", json.getString("calendarId"));
  }

  @Test
  void testEventWithPeopleList() {
    Event event = new Event("e2", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    event.setPeople(List.of("Alice", "Bob"));

    JsonObject json = Json.parse(event.getJson());
    assertTrue(json.hasKey("people"));
    assertEquals("Alice", json.getArray("people").getString(0));
    assertEquals("Bob", json.getArray("people").getString(1));
  }

  @Test
  void testEventWithOptions() {
    Event event = new Event("e3", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    Event.EventOptions options = new Event.EventOptions();
    options.setDisableDND(true);
    options.setDisableResize(false);
    options.setAdditionalClasses(List.of("vip", "highlight"));
    event.setOptions(options);

    JsonObject json = Json.parse(event.getJson());
    JsonObject opts = json.getObject("_options");

    assertTrue(opts.getBoolean("disableDND"));
    assertFalse(opts.getBoolean("disableResize"));
    assertEquals("vip", opts.getArray("additionalClasses").getString(0));
    assertEquals("highlight", opts.getArray("additionalClasses").getString(1));
  }

  @Test
  void testEventWithCustomContent() {
    Event event = new Event("e4", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    Event.EventCustomContent content = new Event.EventCustomContent();
    content.setTimeGrid("<b>Custom</b>");
    event.setCustomContent(content);

    JsonObject json = Json.parse(event.getJson());
    assertEquals("<b>Custom</b>", json.getObject("_customContent").getString("timeGrid"));
  }

  @Test
  void testEventWithRecurrenceRule() {
    Event event = new Event("e5", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    RecurrenceRule rule = new RecurrenceRule(RecurrenceRule.Frequency.WEEKLY);
    rule.setInterval(1);
    rule.setByDay(List.of(RecurrenceRule.Day.MO, RecurrenceRule.Day.WE));
    event.setRecurrenceRule(rule);

    JsonObject json = Json.parse(event.getJson());

    assertEquals("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,WE", json.getString("rrule"));
  }
  
}
