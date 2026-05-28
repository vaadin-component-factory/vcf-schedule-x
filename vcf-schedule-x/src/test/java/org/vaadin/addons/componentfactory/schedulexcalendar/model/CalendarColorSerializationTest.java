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
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar.ColorDefinition;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

class CalendarColorSerializationTest {

  @Test
  void testCssVariableColorsAreSerializedCorrectly() {
    Calendar work = new Calendar("work");
    work.setLightColors(new ColorDefinition("var(--light-color)", "var(--light-container)",
        "var(--light-on-container)"));
    work.setDarkColors(new ColorDefinition("var(--dark-color)", "var(--dark-container)",
        "var(--dark-on-container)"));

    ObjectNode json = work.toJsonObject();

    ObjectNode light = (ObjectNode) json.get("lightColors");
    ObjectNode dark = (ObjectNode) json.get("darkColors");

    assertEquals("var(--light-color)", light.get("main").asString());
    assertEquals("var(--light-container)", light.get("container").asString());
    assertEquals("var(--light-on-container)", light.get("onContainer").asString());

    assertEquals("var(--dark-color)", dark.get("main").asString());
    assertEquals("var(--dark-container)", dark.get("container").asString());
    assertEquals("var(--dark-on-container)", dark.get("onContainer").asString());
  }

  @Test
  void testMultipleCalendarsColorNamesAndThemes() {
    Calendar work = new Calendar("work");
    work.setLightColors(new ColorDefinition("var(--a)", "var(--b)", "var(--c)"));

    Calendar leisure = new Calendar("leisure");
    leisure.setDarkColors(new ColorDefinition("var(--x)", "var(--y)", "var(--z)"));

    Map<String, Calendar> calendars = Map.of("work", work, "leisure", leisure);

    ObjectNode all = JsonNodeFactory.instance.objectNode();
    calendars.forEach((id, cal) -> all.set(id, cal.toJsonObject()));

    assertTrue(all.has("work"));
    assertEquals("work", all.get("work").get("colorName").asString());
    assertTrue(all.get("work").has("lightColors"));

    assertTrue(all.has("leisure"));
    assertTrue(all.get("leisure").has("darkColors"));
    assertEquals("var(--x)", all.get("leisure").get("darkColors").get("main").asString());
  }
}
