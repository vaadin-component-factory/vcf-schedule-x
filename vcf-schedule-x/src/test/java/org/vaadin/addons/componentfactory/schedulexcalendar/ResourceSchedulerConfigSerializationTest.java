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
import elemental.json.Json;
import elemental.json.JsonObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Resource;

class ResourceSchedulerConfigSerializationTest {

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

    JsonObject json = Json.parse(config.getJson());

    assertEquals(120, json.getNumber("hourWidth"));
    assertEquals(300, json.getNumber("dayWidth"));
    assertEquals(20, json.getNumber("eventHeight"));
    assertEquals(50, json.getNumber("resourceHeight"));
    assertTrue(json.getBoolean("dragAndDrop"));
    assertTrue(json.getBoolean("resize"));
    assertFalse(json.getBoolean("infiniteScroll"));
  }

  @Test
  void testInitialHoursAndDaysSerialization() {
    ResourceSchedulerConfig config = new ResourceSchedulerConfig();
    config.setInitialHours(new Pair<>(LocalDateTime.of(2025, 6, 1, 8, 0),
        LocalDateTime.of(2025, 6, 1, 17, 0)));
    config.setInitialDays(
        new Pair<>(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 7)));

    JsonObject json = Json.parse(config.getJson());
    assertEquals("2025-06-01 08:00,2025-06-01 17:00", json.getString("initialHours"));
    assertEquals("2025-06-01,2025-06-07", json.getString("initialDays"));
  }

  @Test
  void testResourcesSerialization() {
    ResourceSchedulerConfig config = new ResourceSchedulerConfig();
    config.setResources(List.of(new Resource("r1")));

    JsonObject json = Json.parse(config.getJson());
    assertTrue(json.getArray("resources").length() > 0);
    assertEquals("r1", json.getArray("resources").getObject(0).getString("id"));
  }
}

