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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.View;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@NpmPackage(value = "@schedule-x/calendar", version = "2.28.0")
@NpmPackage(value = "@schedule-x/events-service", version = "2.28.0")
@NpmPackage(value = "@schedule-x/theme-default", version = "2.28.0")
@CssImport("@schedule-x/theme-default/dist/index.css")
@CssImport("./styles/vcf-schedule-x-calendar-styles.css")
@Setter
@Getter
public abstract class BaseScheduleXCalendar extends Div {

  /**
   * Views available to the user.
   */
  private List<? extends View> views = new ArrayList<>();

  /**
   * Map of calendar IDs to their configuration. The key will be used as the JS object key on the
   * client.
   */
  private Map<String, Calendar> calendars = new HashMap<String, Calendar>();
  
  private EventProvider eventProvider;

  /**
   * Optional global calendar configuration settings.
   */
  private Configuration configuration;
  
  public BaseScheduleXCalendar() {
    this.setId(String.valueOf(this.hashCode()));
    setClassName("vcf-schedule-x-calendar");
  }

  public BaseScheduleXCalendar(List<? extends View> views, EventProvider eventProvider) {
    this();
    this.views = new ArrayList<>(views);
    this.eventProvider = eventProvider;
  }

  public BaseScheduleXCalendar(List<? extends View> views, EventProvider eventProvider, Configuration configuration) {
    this(views, eventProvider);
    this.configuration = configuration;
  }

  public BaseScheduleXCalendar(List<? extends View> views, EventProvider eventProvider, Configuration configuration,
      Map<String, Calendar> calendars) {
    this(views, eventProvider, configuration);
    this.calendars = calendars;
  }
  
  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.initCalendar();
  }

  protected abstract void initCalendar();

  protected String viewsToJson() {
    JsonArray jsonArray = Json.createArray();
    for (int i = 0; i < views.size(); i++) {
      jsonArray.set(i, views.get(i).getName());
    }
    return jsonArray.toJson();
  }

  protected String eventsToJson(LocalDateTime start, LocalDateTime end) {
    List<Event> events = eventProvider.getEvents(start, end);
    return events != null
        ? String.format("[%s]",events.stream().map(event -> event.getJson()).collect(Collectors.joining(",")))
        : "";
  }
  
  protected String configurationToJson() {
    return configuration != null ? configuration.getJson() : "{}";
  }

  /**
   * Serialize all calendars as a JSON object. Example: { "personal": { ... }, "work": { ... } }
   */
  protected String calendarsToJson() {
    if(calendars.isEmpty()) {
      return "{}";
    }
    JsonObject calendarJson = Json.createObject();
    calendars.forEach((id, calendar) -> calendarJson.put(id, calendar.toJsonObject()));
    return calendarJson.toJson();
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    this.getElement().removeAllChildren();
  }
  
  @ClientCallable
  void updateRange(String start, String end) {
    String events = eventsToJson(LocalDateTime.parse(start,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), LocalDateTime.parse(end,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    this.getElement().executeJs("this.calendar.eventsService.set(JSON.parse($0))", events);
  }
  
}
