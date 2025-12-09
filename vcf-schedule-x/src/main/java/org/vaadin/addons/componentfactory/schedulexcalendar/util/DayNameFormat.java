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
package org.vaadin.addons.componentfactory.schedulexcalendar.util;

/**
 * Enumeration for day name format options in the resource scheduler.
 * Configures how day names are displayed in the daily view.
 * When not set (null), day names are disabled (equivalent to false).
 */
public enum DayNameFormat {
  /**
   * Display day names in short format (e.g., "Mon")
   */
  SHORT("short"),

  /**
   * Display day names in long format (e.g., "Monday")
   */
  LONG("long"),

  /**
   * Display day names in narrow format (e.g., "M")
   */
  NARROW("narrow");

  private final String value;

  DayNameFormat(String value) {
    this.value = value;
  }

  /**
   * Gets the string value.
   *
   * @return the string representation of this format
   */
  public String getValue() {
    return value;
  }
}
