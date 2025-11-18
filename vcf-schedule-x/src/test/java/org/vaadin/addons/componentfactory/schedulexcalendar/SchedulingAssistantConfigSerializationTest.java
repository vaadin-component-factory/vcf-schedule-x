package org.vaadin.addons.componentfactory.schedulexcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SchedulingAssistantConfigSerializationTest {

  @Test
  void testBasicSerialization() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 1, 10, 0);

    SchedulingAssistantConfig config = new SchedulingAssistantConfig(start, end);

    JsonObject json = Json.parse(config.getJson());

    assertEquals("2025-06-01T09:00:00", json.getString("initialStart"));
    assertEquals("2025-06-01T10:00:00", json.getString("initialEnd"));
  }
}

