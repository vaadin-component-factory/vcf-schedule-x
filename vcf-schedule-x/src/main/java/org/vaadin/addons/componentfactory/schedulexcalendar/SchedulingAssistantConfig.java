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

import elemental.json.Json;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;

/**
 * Java model representing the SchedulingAssistant plugin config.
 */
@SuppressWarnings("serial")
public class SchedulingAssistantConfig extends BaseConfiguration implements Serializable {

  private LocalDateTime initialStart;

  private LocalDateTime initialEnd;

  public SchedulingAssistantConfig(LocalDateTime initialStart, LocalDateTime initialEnd) {
    this.initialStart = initialStart;
    this.initialEnd = initialEnd;
  }

  public LocalDateTime getInitialStart() {
    return initialStart;
  }

  public void setInitialStart(LocalDateTime initialStart) {
    this.initialStart = initialStart;
    this.runRefresh();
  }

  public LocalDateTime getInitialEnd() {
    return initialEnd;
  }

  public void setInitialEnd(LocalDateTime initialEnd) {
    this.initialEnd = initialEnd;
    this.runRefresh();
  }

  public String getJson() {
    JsonObject js = Json.createObject();
    Optional.ofNullable(initialStart).ifPresent(
        value -> js.put("initialStart", value.format(DateTimeFormatUtils.DATE_TIME_FORMATTER)));
    Optional.ofNullable(initialEnd).ifPresent(
        value -> js.put("initialEnd", value.format(DateTimeFormatUtils.DATE_TIME_FORMATTER)));
    return js.toJson();
  }

}

