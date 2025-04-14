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

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
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
   * Id of the calendar. See {@link Calendar}
   */
  private String calendarId;

  // TODO complete
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
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
    return js.toJson();
  }

}
