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

import elemental.json.Json;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents a single calendar definition.
 * <p>The events of a single calendar can be sorted into different categories, called calendars.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/calendars">Calendars documentation</a>
 */
@SuppressWarnings("serial")
@Getter
@Setter
@RequiredArgsConstructor
public class Calendar implements Serializable {

  /**
   * Used internally as a CSS part (lowercase, no spaces).
   */
  @NonNull
  private String colorName;

  /**
   * Optional colors for light theme.
   */
  private ColorDefinition lightColors;

  /**
   * Optional colors for dark theme.
   */
  private ColorDefinition darkColors;

  /**
   * Serialize this calendar to a JsonObject (excluding its ID).
   */
  public JsonObject toJsonObject() {
    JsonObject js = Json.createObject();
    js.put("colorName", colorName);
    Optional.ofNullable(lightColors).ifPresent(colors -> js.put("lightColors", colors.toJsonObject()));
    Optional.ofNullable(darkColors).ifPresent(colors -> js.put("darkColors", colors.toJsonObject()));
    return js;
  }

  /**
   * Represents a theme color block (light or dark).
   */
  @Getter
  @Setter
  @RequiredArgsConstructor
  public static class ColorDefinition implements Serializable {

    @NonNull
    private String main;

    @NonNull
    private String container;

    @NonNull
    private String onContainer;

    public JsonObject toJsonObject() {
      JsonObject js = Json.createObject();
      js.put("main", main);
      js.put("container", container);
      js.put("onContainer", onContainer);
      return js;
    }
  }
}
