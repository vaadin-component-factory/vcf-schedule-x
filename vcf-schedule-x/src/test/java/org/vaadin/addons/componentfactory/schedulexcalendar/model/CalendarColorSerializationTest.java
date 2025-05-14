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
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar.ColorDefinition;

class CalendarColorSerializationTest {

  @Test
  void testCssVariableColorsAreSerializedCorrectly() {
    Calendar work = new Calendar("work");
    work.setLightColors(new ColorDefinition("var(--light-color)", "var(--light-container)",
        "var(--light-on-container)"));
    work.setDarkColors(new ColorDefinition("var(--dark-color)", "var(--dark-container)",
        "var(--dark-on-container)"));

    JsonObject json = work.toJsonObject();

    JsonObject light = json.getObject("lightColors");
    JsonObject dark = json.getObject("darkColors");

    assertEquals("var(--light-color)", light.getString("main"));
    assertEquals("var(--light-container)", light.getString("container"));
    assertEquals("var(--light-on-container)", light.getString("onContainer"));

    assertEquals("var(--dark-color)", dark.getString("main"));
    assertEquals("var(--dark-container)", dark.getString("container"));
    assertEquals("var(--dark-on-container)", dark.getString("onContainer"));
  }

  @Test
  void testMultipleCalendarsColorNamesAndThemes() {
    Calendar work = new Calendar("work");
    work.setLightColors(new ColorDefinition("var(--a)", "var(--b)", "var(--c)"));

    Calendar leisure = new Calendar("leisure");
    leisure.setDarkColors(new ColorDefinition("var(--x)", "var(--y)", "var(--z)"));

    Map<String, Calendar> calendars = Map.of("work", work, "leisure", leisure);

    JsonObject all = Json.createObject();
    calendars.forEach((id, cal) -> all.put(id, cal.toJsonObject()));

    assertTrue(all.hasKey("work"));
    assertEquals("work", all.getObject("work").getString("colorName"));
    assertTrue(all.getObject("work").hasKey("lightColors"));

    assertTrue(all.hasKey("leisure"));
    assertTrue(all.getObject("leisure").hasKey("darkColors"));
    assertEquals("var(--x)", all.getObject("leisure").getObject("darkColors").getString("main"));
  }
}
