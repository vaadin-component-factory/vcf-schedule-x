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
import com.vaadin.flow.internal.Pair;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Resource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class ResourceSchedulerConfigSerializationTest {

  private static final JsonMapper MAPPER = JsonMapper.builder().build();

  @Test
  void testBasicSerialization() {
    ResourceSchedulerConfig config = new ResourceSchedulerConfig();
    config.setHourWidth(120);
    config.setDayWidth(300);
    config.setEventHeight(20);
    config.setResourceHeight(50);
    config.setDragAndDrop(true);
    config.setResize(true);
    config.setInfiniteScroll(false);

    JsonNode json = MAPPER.readTree(config.getJson());

    assertEquals(120, json.get("hourWidth").asInt());
    assertEquals(300, json.get("dayWidth").asInt());
    assertEquals(20, json.get("eventHeight").asInt());
    assertEquals(50, json.get("resourceHeight").asInt());
    assertTrue(json.get("dragAndDrop").asBoolean());
    assertTrue(json.get("resize").asBoolean());
    assertFalse(json.get("infiniteScroll").asBoolean());
  }

  @Test
  void testInitialHoursAndDaysSerialization() {
    ResourceSchedulerConfig config = new ResourceSchedulerConfig();
    config.setInitialHours(new Pair<>(LocalDateTime.of(2025, 6, 1, 8, 0),
        LocalDateTime.of(2025, 6, 1, 17, 0)));
    config.setInitialDays(
        new Pair<>(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 7)));

    JsonNode json = MAPPER.readTree(config.getJson());
    assertEquals("2025-06-01T08:00:00,2025-06-01T17:00:00", json.get("initialHours").asString());
    assertEquals("2025-06-01,2025-06-07", json.get("initialDays").asString());
  }

  @Test
  void testResourcesSerialization() {
    ResourceSchedulerConfig config = new ResourceSchedulerConfig();
    config.setResources(List.of(new Resource("r1")));

    JsonNode json = MAPPER.readTree(config.getJson());
    assertTrue(json.get("resources").size() > 0);
    assertEquals("r1", json.get("resources").get(0).get("id").asString());
  }
}

