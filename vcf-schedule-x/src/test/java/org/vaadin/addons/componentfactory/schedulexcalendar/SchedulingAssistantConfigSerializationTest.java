package org.vaadin.addons.componentfactory.schedulexcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class SchedulingAssistantConfigSerializationTest {

  private static final JsonMapper MAPPER = JsonMapper.builder().build();

  @Test
  void testBasicSerialization() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 1, 10, 0);

    SchedulingAssistantConfig config = new SchedulingAssistantConfig(start, end);

    JsonNode json = MAPPER.readTree(config.getJson());

    assertEquals("2025-06-01T09:00:00", json.get("initialStart").asString());
    assertEquals("2025-06-01T10:00:00", json.get("initialEnd").asString());
  }
}

