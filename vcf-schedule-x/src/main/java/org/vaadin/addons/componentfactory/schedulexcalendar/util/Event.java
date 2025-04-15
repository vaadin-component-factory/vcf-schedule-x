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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Calendar event definition.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/events">Events documentation</a>
 */
@SuppressWarnings("serial")
@Setter
@Getter
@RequiredArgsConstructor
public class Event implements Serializable {

  @NonNull
  private String id;

  @NonNull
  private String start;

  @NonNull
  private String end;

  private String title;

  private String description;

  private String location;

  /**
   * Names of the participants.
   */
  private List<String> people;

  /**
   * Id of the calendar. This is the calendarId which links to a specific calendar (e.g., "work",
   * "leisure"). See {@link Calendar}
   */
  private String calendarId;

  /**
   * Optional configuration for the event.
   */
  private EventOptions options;

  /**
   * Optional custom content to render in different views.
   */
  private EventCustomContent customContent;

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Event other = (Event) obj;
    return Objects.equals(id, other.id);
  }

  public String getJson() {
    JsonObject js = Json.createObject();
    js.put("id", id);
    js.put("start", start);
    js.put("end", end);

    Optional.ofNullable(title).ifPresent(value -> js.put("title", value));
    Optional.ofNullable(description).ifPresent(value -> js.put("description", value));
    Optional.ofNullable(location).ifPresent(value -> js.put("location", value));
    Optional.ofNullable(calendarId).ifPresent(value -> js.put("calendarId", value));

    if (people != null && !people.isEmpty()) {
      JsonArray jsonPeople = Json.createArray();
      for (int i = 0; i < people.size(); i++) {
        jsonPeople.set(i, people.get(i));
      }
      js.put("people", jsonPeople);
    }

    if (options != null) {
      js.put("_options", options.toJson());
    }

    if (customContent != null) {
      JsonObject jsonCustomContent = Json.createObject();
      Optional.ofNullable(customContent.getTimeGrid())
          .ifPresent(v -> jsonCustomContent.put("timeGrid", v));
      Optional.ofNullable(customContent.getDateGrid())
          .ifPresent(v -> jsonCustomContent.put("dateGrid", v));
      Optional.ofNullable(customContent.getMonthGrid())
          .ifPresent(v -> jsonCustomContent.put("monthGrid", v));
      Optional.ofNullable(customContent.getMonthAgenda())
          .ifPresent(v -> jsonCustomContent.put("monthAgenda", v));
      js.put("_customContent", jsonCustomContent);
    }

    return js.toJson();
  }

  @Setter
  @Getter
  public static class EventOptions implements Serializable {
    private Boolean disableDND;
    private Boolean disableResize;
    private List<String> additionalClasses;

    public JsonObject toJson() {
      JsonObject json = Json.createObject();
      Optional.ofNullable(disableDND).ifPresent(value -> json.put("disableDND", value));
      Optional.ofNullable(disableResize).ifPresent(value -> json.put("disableResize", value));
      if (additionalClasses != null && !additionalClasses.isEmpty()) {
        JsonArray jsonArray = Json.createArray();
        for (int i = 0; i < additionalClasses.size(); i++) {
          jsonArray.set(i, additionalClasses.get(i));
        }
        json.put("additionalClasses", jsonArray);
      }
      return json;
    }
  }

  public LocalDateTime getStartDateTime() {
    return LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }

  public LocalDateTime getEndDateTime() {
    return LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }

  @Setter
  @Getter
  public static class EventCustomContent implements Serializable {
    private String timeGrid;
    private String dateGrid;
    private String monthGrid;
    private String monthAgenda;
  }
}
