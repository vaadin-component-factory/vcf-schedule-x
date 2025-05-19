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

/**
 * Represents a single calendar definition.
 * <p>
 * The events of a single calendar can be sorted into different categories, called calendars.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/calendars">Calendars documentation</a>
 */
@SuppressWarnings("serial")
public class Calendar implements Serializable {

  /**
   * Used internally as a CSS part (lowercase, no spaces).
   */
  private String colorName;

  /**
   * Optional colors for light theme.
   */
  private ColorDefinition lightColors;

  /**
   * Optional colors for dark theme.
   */
  private ColorDefinition darkColors;

  public Calendar(String colorName) {
    this.colorName = colorName;
  }

  public Calendar(String colorName, ColorDefinition lightColors) {
    this(colorName);
    this.lightColors = lightColors;
  }

  public Calendar(String colorName, ColorDefinition lightColors, ColorDefinition darkColors) {
    this(colorName, lightColors);
    this.darkColors = darkColors;
  }

  public String getColorName() {
    return colorName;
  }

  public void setColorName(String colorName) {
    this.colorName = colorName;
  }

  public ColorDefinition getLightColors() {
    return lightColors;
  }

  public void setLightColors(ColorDefinition lightColors) {
    this.lightColors = lightColors;
  }

  public ColorDefinition getDarkColors() {
    return darkColors;
  }

  public void setDarkColors(ColorDefinition darkColors) {
    this.darkColors = darkColors;
  }

  /**
   * Serialize this calendar to a JsonObject (excluding its ID).
   */
  public JsonObject toJsonObject() {
    JsonObject js = Json.createObject();
    js.put("colorName", colorName);
    Optional.ofNullable(lightColors)
        .ifPresent(colors -> js.put("lightColors", colors.toJsonObject()));
    Optional.ofNullable(darkColors)
        .ifPresent(colors -> js.put("darkColors", colors.toJsonObject()));
    return js;
  }

  /**
   * Represents a theme color block (light or dark).
   */
  public static class ColorDefinition implements Serializable {

    private String main;
    private String container;
    private String onContainer;

    public ColorDefinition(String main, String container, String onContainer) {
      this.main = main;
      this.container = container;
      this.onContainer = onContainer;
    }

    public String getMain() {
      return main;
    }

    public void setMain(String main) {
      this.main = main;
    }

    public String getContainer() {
      return container;
    }

    public void setContainer(String container) {
      this.container = container;
    }

    public String getOnContainer() {
      return onContainer;
    }

    public void setOnContainer(String onContainer) {
      this.onContainer = onContainer;
    }

    public JsonObject toJsonObject() {
      JsonObject js = Json.createObject();
      js.put("main", main);
      js.put("container", container);
      js.put("onContainer", onContainer);
      return js;
    }
  }
}
