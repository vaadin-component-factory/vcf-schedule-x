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
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXResourceScheduler;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;

/**
 * Calendar event definition.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/events">Events documentation</a>
 */
@SuppressWarnings("serial")
public class Event implements Serializable {

  /**
   * A unique identifier for the event.
   */
  private String id;

  /**
   * The start date time of the event.
   */
  private LocalDateTime start;

  /**
   * The end date time of the event.
   */
  private LocalDateTime end;

  /**
   * The title of the event.
   */
  private String title;

  /**
   * A description of the event.
   */
  private String description;

  /**
   * The location of the event.
   */
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

  private EventOptions options;

  private EventCustomContent customContent;

  /**
   * Id of the resource, only for {@link ScheduleXResourceScheduler Resource View}
   */
  private String resourceId;

  /**
   * The recurrence rule for the events if applicable.
   */
  private RecurrenceRule recurrenceRule;

  /**
   * List of date-times to be excluded from the recurrence set.
   */
  private List<LocalDateTime> excludedDates;

  /**
   * Constructs an {@code Event} with the specified ID, start, and end date-times.
   *
   * @param id the unique identifier of the event
   * @param start the start date and time of the event
   * @param end the end date and time of the event
   */
  public Event(String id, LocalDateTime start, LocalDateTime end) {
    this.id = id;
    this.start = start;
    this.end = end;
  }

  /**
   * Constructs an {@code Event} from an ID and string representations of the start and end
   * date-times.
   * <p>
   * Supported formats for {@code start} and {@code end}:
   * <ul>
   * <li>{@code YYYY-MM-DD} — treated as midnight (start of day)</li>
   * <li>{@code YYYY-MM-DD HH:mm} — treated as the specified date and time</li>
   * </ul>
   * If the {@code end} value is given in the {@code YYYY-MM-DD} format, it is internally adjusted
   * to represent the end of that day (23:59:59).
   *
   * @param id the unique identifier of the event
   * @param start the start date-time string in one of the supported formats
   * @param end the end date-time string in one of the supported formats
   */
  public Event(String id, String start, String end) {
    this(id, DateTimeFormatUtils.parseDate(start, false), DateTimeFormatUtils.parseDate(end, true));
  }

  /**
   * Constructs an {@code Event} from a JSON representation.
   * <p>
   * Required fields in the JSON object:
   * <ul>
   * <li>{@code id} – unique event ID</li>
   * <li>{@code start} – start date-time string (format: {@code YYYY-MM-DD} or
   * {@code YYYY-MM-DD HH:mm})</li>
   * <li>{@code end} – end date-time string (same formats as {@code start})</li>
   * </ul>
   *
   * @param json the {@link JsonValue} representing the event data
   */
  public Event(JsonValue json) {
    JsonObject js = (JsonObject) json;
    this.id = js.getString("id");
    this.start = DateTimeFormatUtils.parseDate(js.getString("start"), false);
    this.end = DateTimeFormatUtils.parseDate(js.getString("end"), true);
    this.title = js.hasKey("title") ? js.getString("title") : null;
    this.description = js.hasKey("description") ? js.getString("description") : null;
    this.location = js.hasKey("location") ? js.getString("location") : null;
    this.calendarId = js.hasKey("calendarId") ? js.getString("calendarId") : null;
    this.resourceId = js.hasKey("resourceId") ? js.getString("resourceId") : null;

    if (js.hasKey("people")) {
      JsonArray jsonPeople = js.getArray("people");
      for (int i = 0; i < jsonPeople.length(); i++) {
        people.add(jsonPeople.get(i).asString());
      }
    }

    if (js.hasKey("_options")) {
      options = new EventOptions();
      options.disableDND = js.getObject("_options").getBoolean("disableDND");
      options.disableResize = js.getObject("_options").getBoolean("disableResize");
      JsonArray additionalClasses = js.getObject("_options").getArray("additionalClasses");
      for (int i = 0; i < additionalClasses.length(); i++) {
        options.additionalClasses.add(additionalClasses.get(i).asString());
      }
    }

    if (js.hasKey("_customContent")) {
      customContent = new EventCustomContent();
      JsonObject jsonCustomContent = js.getObject("_customContent");
      customContent.timeGrid =
          jsonCustomContent.hasKey("timeGrid") ? jsonCustomContent.getString("timeGrid") : null;
      customContent.dateGrid =
          jsonCustomContent.hasKey("dateGrid") ? jsonCustomContent.getString("dateGrid") : null;
      customContent.monthGrid =
          jsonCustomContent.hasKey("monthGrid") ? jsonCustomContent.getString("monthGrid") : null;
      customContent.monthAgenda =
          jsonCustomContent.hasKey("monthAgenda") ? jsonCustomContent.getString("monthAgenda")
              : null;
    }
  }

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getStart() {
    return start;
  }

  public void setStart(LocalDateTime start) {
    this.start = start;
  }

  public LocalDateTime getEnd() {
    return end;
  }

  public void setEnd(LocalDateTime end) {
    this.end = end;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<String> getPeople() {
    return people;
  }

  public void setPeople(List<String> people) {
    this.people = people;
  }

  public String getCalendarId() {
    return calendarId;
  }

  public void setCalendarId(String calendarId) {
    this.calendarId = calendarId;
  }

  public EventOptions getOptions() {
    return options;
  }

  public void setOptions(EventOptions options) {
    this.options = options;
  }

  public EventCustomContent getCustomContent() {
    return customContent;
  }

  public void setCustomContent(EventCustomContent customContent) {
    this.customContent = customContent;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public RecurrenceRule getRecurrenceRule() {
    return recurrenceRule;
  }

  public void setRecurrenceRule(RecurrenceRule recurrenceRule) {
    this.recurrenceRule = recurrenceRule;
  }

  public List<LocalDateTime> getExcludedDates() {
    return excludedDates;
  }

  public void setExcludedDates(List<LocalDateTime> excludedDates) {
    this.excludedDates = excludedDates;
  }

  public String getJson() {
    JsonObject js = Json.createObject();
    js.put("id", id);
    js.put("start", start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    js.put("end", end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

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

    Optional.ofNullable(resourceId).ifPresent(value -> js.put("resourceId", value));

    Optional.ofNullable(recurrenceRule).ifPresent(value -> js.put("rrule", value.getRule()));

    if (excludedDates != null && !excludedDates.isEmpty()) {
      JsonArray jsonExDates = Json.createArray();
      for (int i = 0; i < excludedDates.size(); i++) {
        jsonExDates.set(i, excludedDates.get(i).format(DateTimeFormatUtils.DATE_TIME_FORMATTER));
      }
      js.put("exdate", jsonExDates);
    }

    return js.toJson();
  }

  /**
   * Configure the behavior of individual events by adding an _options object to the event. All the
   * properties are optional.
   */
  public static class EventOptions implements Serializable {

    /**
     * Disables drag and drop for the event.
     */
    private Boolean disableDND;

    /**
     * Disables resizing for the event.
     */
    private Boolean disableResize;

    /**
     * Additional classes to add to the event.
     */
    private List<String> additionalClasses;

    public Boolean getDisableDND() {
      return disableDND;
    }

    public void setDisableDND(Boolean disableDND) {
      this.disableDND = disableDND;
    }

    public Boolean getDisableResize() {
      return disableResize;
    }

    public void setDisableResize(Boolean disableResize) {
      this.disableResize = disableResize;
    }

    public List<String> getAdditionalClasses() {
      return additionalClasses;
    }

    public void setAdditionalClasses(List<String> additionalClasses) {
      this.additionalClasses = additionalClasses;
    }

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

  /**
   * Optional custom content to render in different views.
   */
  public static class EventCustomContent implements Serializable {

    /**
     * Custom HTML to display in the time grid of week/day views.
     */
    private String timeGrid;

    /**
     * Custom HTML to display in the date grid of week/day views.
     */
    private String dateGrid;

    /**
     * Custom HTML to display in the month view.
     */
    private String monthGrid;

    /**
     * Custom HTML to display in the month agenda view.
     */
    private String monthAgenda;

    public String getTimeGrid() {
      return timeGrid;
    }

    public void setTimeGrid(String timeGrid) {
      this.timeGrid = timeGrid;
    }

    public String getDateGrid() {
      return dateGrid;
    }

    public void setDateGrid(String dateGrid) {
      this.dateGrid = dateGrid;
    }

    public String getMonthGrid() {
      return monthGrid;
    }

    public void setMonthGrid(String monthGrid) {
      this.monthGrid = monthGrid;
    }

    public String getMonthAgenda() {
      return monthAgenda;
    }

    public void setMonthAgenda(String monthAgenda) {
      this.monthAgenda = monthAgenda;
    }
  }

}
