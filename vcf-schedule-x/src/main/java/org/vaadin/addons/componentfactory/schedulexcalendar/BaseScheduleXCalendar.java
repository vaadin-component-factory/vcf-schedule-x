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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.View;

@SuppressWarnings("serial")
@NpmPackage(value = "@schedule-x/calendar", version = "2.29.0")
@NpmPackage(value = "@schedule-x/events-service", version = "2.29.0")
@NpmPackage(value = "@schedule-x/theme-default", version = "2.29.0")
@NpmPackage(value = "@schedule-x/resize", version = "2.29.0")
@CssImport("@schedule-x/theme-default/dist/index.css")
@CssImport("./styles/vcf-schedule-x-calendar-styles.css")
@Setter
@Getter
public abstract class BaseScheduleXCalendar extends Div {

  protected static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");
  protected static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

  public BaseScheduleXCalendar(List<? extends View> views, EventProvider eventProvider,
      Configuration configuration) {
    this(views, eventProvider);
    this.configuration = configuration;
  }

  public BaseScheduleXCalendar(List<? extends View> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars) {
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
    return events != null ? String.format("[%s]",
        events.stream().map(event -> event.getJson()).collect(Collectors.joining(","))) : "";
  }

  protected String configurationToJson() {
    return configuration != null ? configuration.getJson() : "{}";
  }

  /**
   * Serialize all calendars as a JSON object. Example: { "personal": { ... }, "work": { ... } }
   */
  protected String calendarsToJson() {
    if (calendars == null || calendars.isEmpty()) {
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
    String events = eventsToJson(LocalDateTime.parse(start, DATE_TIME_FORMATTER),
        LocalDateTime.parse(end, DATE_TIME_FORMATTER));
    this.getElement().executeJs("this.calendar.eventsService.set(JSON.parse($0))", events);
  }

  /**
   * Sets the calendar view.
   * 
   * @param view the view to set
   * @param selectedDate the current selected date
   */
  public abstract void setView(View view, LocalDate selectedDate);

  /**
   * Sets the calendar date.
   * 
   * @param selectedDate the date to set
   */
  public abstract void setSelectedDate(LocalDate selectedDate);

  public abstract void navigateForwards();

  public abstract void navigateBackwards();

  /**
   * Handles calendar event click.
   * 
   * @param eventId id of the event being clicked
   */
  @ClientCallable
  private void onCalendarEventClick(String eventId) {
    ComponentUtil.fireEvent(this, new CalendarEventClickEvent(this, eventId, false));
  }

  /**
   * Event fired when a calendar event is clicked.
   */
  @Getter
  public class CalendarEventClickEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private String eventId;

    public CalendarEventClickEvent(BaseScheduleXCalendar source, String eventId,
        boolean fromClient) {
      super(source, fromClient);
      this.eventId = eventId;
    }
  }

  /**
   * Adds a CalendarEventClickEvent listener.
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addCalendarEventClickEventListener(
      ComponentEventListener<CalendarEventClickEvent> listener) {
    return addListener(CalendarEventClickEvent.class, listener);
  }

  /**
   * Handles selected date update on client side. The selected date has format YYYY-MM-DD.
   * 
   * @param selectedDate the new selected date
   */
  @ClientCallable
  private void onSelectedDateUpdate(String selectedDate) {
    ComponentUtil.fireEvent(this,
        new SelectedDateUpdateEvent(this, LocalDate.parse(selectedDate), false));
  }

  /**
   * Event fired when the selected date is updated on client side.
   */
  @Getter
  public class SelectedDateUpdateEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private LocalDate selectedDate;

    public SelectedDateUpdateEvent(BaseScheduleXCalendar source, LocalDate selectedDate,
        boolean fromClient) {
      super(source, fromClient);
      this.selectedDate = selectedDate;
    }
  }

  /**
   * Adds a SelectedDateUpdateEvent listener.
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addSelectedDateUpdateEventListener(
      ComponentEventListener<SelectedDateUpdateEvent> listener) {
    return addListener(SelectedDateUpdateEvent.class, listener);
  }

  /**
   * Adds the given event to the calendar.
   * 
   * @param event calendar event to be added
   */
  public abstract void addEvent(Event event);

  /**
   * Removes the event with given id from the calendar.
   * 
   * @param eventId id of the event to be removed
   */
  public abstract void removeEvent(String eventId);

  /**
   * Updates the given event.
   * 
   * @param event the event to be updated
   */
  public abstract void updateEvent(Event event);

  /**
   * Event fired when a calendar event is added to the calendar.
   */
  @Getter
  @DomEvent("calendar-event-added")
  public static class CalendarEventAddedEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final String eventId;

    public CalendarEventAddedEvent(BaseScheduleXCalendar source, boolean fromClient,
        @EventData(value = "event.detail.eventId") String eventId) {
      super(source, fromClient);
      this.eventId = eventId;
    }
  }

  /**
   * Adds a CalendarEventAddedEvent listener.
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addCalendarEventAddedEventListener(
      ComponentEventListener<CalendarEventAddedEvent> listener) {
    return addListener(CalendarEventAddedEvent.class, listener);
  }

  /**
   * Event fired when a calendar event is removed from the calendar.
   */
  @Getter
  @DomEvent("calendar-event-removed")
  public static class CalendarEventRemovedEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final String eventId;

    public CalendarEventRemovedEvent(BaseScheduleXCalendar source, boolean fromClient,
        @EventData(value = "event.detail.eventId") String eventId) {
      super(source, fromClient);
      this.eventId = eventId;
    }
  }

  /**
   * Adds a CalendarEventRemovedEvent listener.
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addCalendarEventRemovedEventListener(
      ComponentEventListener<CalendarEventRemovedEvent> listener) {
    return addListener(CalendarEventRemovedEvent.class, listener);
  }

  /**
   * Event fired when a calendar event is updated.
   */
  @Getter
  @DomEvent("calendar-event-updated")
  public static class CalendarEventUpdatedEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final String eventId;

    public CalendarEventUpdatedEvent(BaseScheduleXCalendar source, boolean fromClient,
        @EventData(value = "event.detail.eventId") String eventId) {
      super(source, fromClient);
      this.eventId = eventId;
    }
  }

  /**
   * Adds a CalendarEventUpdatedEvent listener.
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addCalendarEventUpdatedEventListener(
      ComponentEventListener<CalendarEventUpdatedEvent> listener) {
    return addListener(CalendarEventUpdatedEvent.class, listener);
  }

  /**
   * Handles event update on resize.
   * 
   * @param eventId the id of the updated event
   * @param start the new start date of the updated event
   * @param end the new end date of the updated event
   */
  @ClientCallable
  private void onEventUpdateOnResize(String eventId, String start, String end) {
    ComponentUtil.fireEvent(this,
        new EventUpdateOnResizeEvent(this, eventId, LocalDateTime.parse(start, DATE_TIME_FORMATTER),
            LocalDateTime.parse(end, DATE_TIME_FORMATTER), false));
  }

  /**
   * Event fired when an event is updated on resize on client side.
   */
  @Getter
  public class EventUpdateOnResizeEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private String eventId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public EventUpdateOnResizeEvent(BaseScheduleXCalendar source, String eventId,
        LocalDateTime startDate, LocalDateTime endDate, boolean fromClient) {
      super(source, fromClient);
      this.eventId = eventId;
      this.startDate = startDate;
      this.endDate = endDate;
    }
  }

  /**
   * Adds a EventUpdateOnResizeEvent listener.
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addEventUpdateOnResizeEventListener(
      ComponentEventListener<EventUpdateOnResizeEvent> listener) {
    return addListener(EventUpdateOnResizeEvent.class, listener);
  }

}
