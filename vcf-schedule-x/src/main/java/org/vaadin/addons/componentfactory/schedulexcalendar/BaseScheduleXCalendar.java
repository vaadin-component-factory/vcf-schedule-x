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
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.DayBoundaries;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.MonthGridOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration.WeekOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ViewType;

@SuppressWarnings("serial")
@NpmPackage(value = "@schedule-x/calendar", version = "2.30.0")
@NpmPackage(value = "@schedule-x/theme-default", version = "2.30.0")
@NpmPackage(value = "@schedule-x/resize", version = "2.30.0")
@NpmPackage(value = "@schedule-x/drag-and-drop", version = "2.30.0")
@NpmPackage(value = "@schedule-x/current-time", version = "2.30.0")
@NpmPackage(value = "@schedule-x/scroll-controller", version = "2.30.0")
@NpmPackage(value = "@schedule-x/calendar-controls", version = "2.30.0")
@NpmPackage(value = "@schedule-x/event-recurrence", version = "2.30.0")
@NpmPackage(value = "@schedule-x/ical", version = "2.30.0")
@CssImport("@schedule-x/theme-default/dist/index.css")
@CssImport("./styles/vcf-schedule-x-calendar-styles.css")
@Setter
@Getter
public abstract class BaseScheduleXCalendar extends Div {
  
  private boolean calendarRendered;

  /**
   * Views available to the user.
   */
  private List<? extends ViewType> views = new ArrayList<>();

  /**
   * Map of calendar IDs to their configuration. The key will be used as the JS object key on the
   * client.
   */
  private Map<String, Calendar> calendars = new HashMap<String, Calendar>();

  private EventProvider eventProvider;

  /**
   * Current calendar view being shown.
   */
  private ViewType view;

  /**
   * Optional global calendar configuration settings.
   */
  private Configuration configuration;

  public BaseScheduleXCalendar() {
    this.setId(String.valueOf(this.hashCode()));
    setClassName("vcf-schedule-x-calendar");
  }

  public BaseScheduleXCalendar(List<? extends ViewType> views, EventProvider eventProvider) {
    this();
    this.views = new ArrayList<>(views);
    this.eventProvider = eventProvider;
  }

  public BaseScheduleXCalendar(List<? extends ViewType> views, EventProvider eventProvider,
      Configuration configuration) {
    this(views, eventProvider);
    this.configuration = configuration;
  }

  public BaseScheduleXCalendar(List<? extends ViewType> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars) {
    this(views, eventProvider, configuration);
    this.calendars = calendars;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.initCalendar();
    addCalendarRenderedListener(() -> {
    });
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
    String events = eventsToJson(LocalDateTime.parse(start, DateTimeFormatUtils.DATE_TIME_FORMATTER),
        LocalDateTime.parse(end, DateTimeFormatUtils.DATE_TIME_FORMATTER));
    this.getElement().executeJs(
        "if (this.calendar.$app.config.plugins.ICalendarPlugin) "
        + "{"
        + " this.calendar.$app.config.plugins.ICalendarPlugin.between($1, $2);"
        + " JSON.parse($0).forEach(event => this.calendar.eventsService.add(event));"
        + "}"
        + " else "
        + "{"
        + " this.calendar.eventsService.set(JSON.parse($0))"
        + "}"
        + " if(this.calendar.$app.config.plugins.eventRecurrence){"
        + " this.calendar.$app.config.plugins.eventRecurrence.onRangeUpdate({$1, $2})"
        + "}",
        events, start, end);
  }

  protected abstract String getJsConnector();

  /**
   * Sets the calendar view.
   * 
   * @param view the view to set
   */
  public void setView(ViewType view) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setView($0, $1);", this, view.getName());
      this.view = view;
    });
  }

  /**
   * Gets the current calendar view in display.
   * 
   * @return the current view
   */
  public ViewType getView() {
    return this.view == null ? this.getViews().get(0) : this.view;
  }

  /**
   * Sets the calendar date.
   * 
   * @param selectedDate the date to set
   */
  public void setDate(LocalDate selectedDate) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setDate($0, $1);", this,
          selectedDate.format(DateTimeFormatUtils.DATE_FORMATTER));
      if (configuration == null) {
        configuration = new Configuration();
      }
      configuration.setSelectedDate(selectedDate);
    });
  };

  /**
   * Gets the current date set in calendar.
   * 
   * @return current date
   */
  public LocalDate getDate() {
    return configuration.getSelectedDate();
  }

  /**
   * Set the first day of the week for the calendar. Value must be between 0 and 6 where 0 is
   * Sunday, 1 is Monday etc.
   * 
   * @param firstDayOfWeek day to be shown as first day of the week;
   */
  public void setFirstDayOfWeek(Integer firstDayOfWeek) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setFirstDayOfWeek($0, $1);", this,
          firstDayOfWeek);
      if (configuration == null) {
        configuration = new Configuration();
      }
      configuration.setFirstDayOfWeek(firstDayOfWeek);
    });
  }

  /**
   * Returns the day(number) shown as first day of the week in the calendars.
   * 
   * @return first day of the week
   */
  public Integer getFirstDayOfWeek() {
    return configuration.getFirstDayOfWeek();
  }

  /**
   * Sets the language for the calendar. List of supported languages:
   * https://schedule-x.dev/docs/calendar/language
   * 
   * @param locale locale for the calendar
   */
  public void setCalendarLocale(String locale) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setLocale($0, $1);", this, locale);
      if (configuration == null) {
        configuration = new Configuration();
      }
      configuration.setLocale(locale);
    });
  }

  /**
   * Returns the current language/locale of the calendar.
   * 
   * @return the locale use by the calendar
   */
  public String getCalendarLocale() {
    return configuration.getLocale();
  }

  /**
   * Sets the available views for the calendar. The views to be set must include the currently
   * active view name. At least one view must be passed into this function.
   * 
   * @param views the views to be shown by the calendar
   */
  public void setViews(List<? extends ViewType> views) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setViews($0, $1);", this, viewsToJson());
      this.views = new ArrayList<>(views);
    });
  }

  /**
   * Sets the day boundaries of the calendar.
   * 
   * @param dayBoundaries the day boundaries of the calendar
   */
  public void setDayBoundaries(DayBoundaries dayBoundaries) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setDayBoundaries($0, $1);", this,
          dayBoundaries.toJson());
      if (configuration == null) {
        configuration = new Configuration();
      }
      configuration.setDayBoundaries(dayBoundaries);
    });
  }

  /**
   * Gets the day boundaries of the calendar.
   * 
   * @return the day boundaries of the calendar
   */
  public DayBoundaries getDayBoundaries() {
    return configuration.getDayBoundaries();
  }

  /**
   * Sets the week options of the calendar.
   * 
   * @param weekOptions the week options of the calendar
   */
  public void setWeekOptions(WeekOptions weekOptions) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setWeekOptions($0, $1);", this,
          weekOptions.toJson());
      if (configuration == null) {
        configuration = new Configuration();
      }
      configuration.setWeekOptions(weekOptions);
    });
  }

  /**
   * Gets the week options of the calendar.
   * 
   * @return the week options of the calendar
   */
  public WeekOptions getWeekOptions() {
    return configuration.getWeekOptions();
  }

  /**
   * Sets the available calendars to be displayed in the calendar.
   * 
   * @param calendars the calendars to be displayed in the calendar
   */
  public void setCalendars(Map<String, Calendar> calendars) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setCalendars($0, $1);", this,
          calendarsToJson());
      this.calendars = calendars;
    });
  }

  /**
   * Sets the min date for the calendar navigation.
   * 
   * @param minDate the min date for the calendar navigation
   */
  public void setMinDate(LocalDate minDate) {
    this.getElement().executeJs(getJsConnector() + ".setMinDate($0, $1);", this,
        minDate.format(DateTimeFormatUtils.DATE_FORMATTER));
    if (configuration == null) {
      configuration = new Configuration();
    }
    configuration.setMinDate(minDate);
  }

  /**
   * Gets the min date for the calendar navigation.
   * 
   * @return the min date for the calendar navigation
   */
  public LocalDate getMinDate() {
    return configuration.getMinDate();
  }

  /**
   * Sets the max date for the calendar navigation.
   * 
   * @param maxDate the max date for the calendar navigation
   */
  public void setMaxDate(LocalDate maxDate) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setMaxDate($0, $1);", this,
          maxDate.format(DateTimeFormatUtils.DATE_FORMATTER));
      if (configuration == null) {
        configuration = new Configuration();
      }
      configuration.setMaxDate(maxDate);
    });
  }

  /**
   * Sets the max date for the calendar navigation.
   * 
   * @return the max date for the calendar navigation
   */
  public LocalDate getMaxDate() {
    return configuration.getMaxDate();
  }

  /**
   * Sets the month grid options of the calendar.
   * 
   * @param monthGridOptions the month grid options of the calendar
   */
  public void setMonthGridOptions(MonthGridOptions monthGridOptions) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setMonthGridOptions($0, $1);", this,
          monthGridOptions.toJson());
      if (configuration == null) {
        configuration = new Configuration();
      }
      configuration.setMonthGridOptions(monthGridOptions);
    });
  }

  /**
   * Gets the month grid options of the calendar.
   * 
   * @return the month grid options of the calendar
   */
  public MonthGridOptions getMonthGridOptions() {
    return configuration.getMonthGridOptions();
  }

  private void executeOnCalendarRendered(SerializableRunnable runnable) {
    if (!calendarRendered) {
      addCalendarRenderedListener(runnable);
    } else {
      runnable.run();
    }
  }

  private void addCalendarRenderedListener(SerializableRunnable onRendered) {
    this.getElement().addEventListener("calendar-rendered", ev -> {
      if (!calendarRendered) {
        calendarRendered = true;
        onRendered.run();
      }
    });
  }

  /**
   * Allows to navigate calendar forwards.
   */
  public void navigateForwards() { 
    this.getElement().executeJs(getJsConnector() + ".navigateForwards($0)", this);
  }

  /**
   * Allows to navigate calendar backwards.
   */
  public void navigateBackwards() {
    this.getElement().executeJs(getJsConnector() + ".navigateBackwards($0)", this);
  }

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
  public void addEvent(Event event) {
    this.getElement().executeJs(getJsConnector() + ".addEvent($0, $1);", this, event.getJson());
  }

  /**
   * Removes the event with given id from the calendar.
   * 
   * @param eventId id of the event to be removed
   */
  public void removeEvent(String eventId) {
    this.getElement().executeJs(getJsConnector() + ".removeEvent($0, $1);", this, eventId);
  }

  /**
   * Updates the given event.
   * 
   * @param event the event to be updated
   */
  public void updateEvent(Event event) {
    this.getElement().executeJs(getJsConnector() + ".updateEvent($0, $1);", this, event.getJson());
  }

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
   * Handles event update on resize or drag and drop.
   * 
   * @param eventId the id of the updated event
   * @param start the new start date of the updated event
   * @param end the new end date of the updated event
   */
  @ClientCallable
  private void onEventUpdate(String eventId, String start, String end) {
    ComponentUtil.fireEvent(this,
        new EventUpdateEvent(this, eventId, LocalDateTime.parse(start, DateTimeFormatUtils.DATE_TIME_FORMATTER),
            LocalDateTime.parse(end, DateTimeFormatUtils.DATE_TIME_FORMATTER), false));
  }

  /**
   * Event fired when an event is updated on resize or drag and drop on client side.
   */
  @Getter
  public class EventUpdateEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private String eventId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public EventUpdateEvent(BaseScheduleXCalendar source, String eventId, LocalDateTime startDate,
        LocalDateTime endDate, boolean fromClient) {
      super(source, fromClient);
      this.eventId = eventId;
      this.startDate = startDate;
      this.endDate = endDate;
    }
  }

  /**
   * Adds a {@code EventUpdateEvent} listener.
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addEventUpdateEventListener(
      ComponentEventListener<EventUpdateEvent> listener) {
    return addListener(EventUpdateEvent.class, listener);
  }

}
