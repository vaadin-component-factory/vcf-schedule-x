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
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class EventSerializationTest {

  private static final JsonMapper MAPPER = JsonMapper.builder().build();

  @Test
  void testBasicEventSerialization() {
    Event event = new Event("e1", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    event.setTitle("Meeting");
    event.setDescription("Discuss roadmap");
    event.setLocation("Zoom");
    event.setCalendarId("work");

    JsonNode json = MAPPER.readTree(event.getJson());

    assertEquals("e1", json.get("id").asString());
    assertEquals("2025-06-01T09:00:00", json.get("start").asString());
    assertEquals("2025-06-01T10:00:00", json.get("end").asString());
    assertEquals("Meeting", json.get("title").asString());
    assertEquals("Discuss roadmap", json.get("description").asString());
    assertEquals("Zoom", json.get("location").asString());
    assertEquals("work", json.get("calendarId").asString());
  }

  @Test
  void testEventWithPeopleList() {
    Event event = new Event("e2", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    event.setPeople(List.of("Alice", "Bob"));

    JsonNode json = MAPPER.readTree(event.getJson());
    assertTrue(json.has("people"));
    assertEquals("Alice", json.get("people").get(0).asString());
    assertEquals("Bob", json.get("people").get(1).asString());
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

    JsonNode json = MAPPER.readTree(event.getJson());
    JsonNode opts = json.get("_options");

    assertTrue(opts.get("disableDND").asBoolean());
    assertFalse(opts.get("disableResize").asBoolean());
    assertEquals("vip", opts.get("additionalClasses").get(0).asString());
    assertEquals("highlight", opts.get("additionalClasses").get(1).asString());
  }

  @Test
  void testEventWithCustomContent() {
    Event event = new Event("e4", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    Event.EventCustomContent content = new Event.EventCustomContent();
    content.setTimeGrid("<b>Custom</b>");
    event.setCustomContent(content);

    JsonNode json = MAPPER.readTree(event.getJson());
    assertEquals("<b>Custom</b>", json.get("_customContent").get("timeGrid").asString());
  }

  @Test
  void testEventWithRecurrenceRule() {
    Event event = new Event("e5", LocalDateTime.of(2025, 06, 01, 9, 00),
        LocalDateTime.of(2025, 06, 01, 10, 00));
    RecurrenceRule rule = new RecurrenceRule(RecurrenceRule.Frequency.WEEKLY);
    rule.setInterval(1);
    rule.setByDay(List.of(RecurrenceRule.Day.MO, RecurrenceRule.Day.WE));
    event.setRecurrenceRule(rule);

    JsonNode json = MAPPER.readTree(event.getJson());

    assertEquals("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,WE", json.get("rrule").asString());
  }

}
