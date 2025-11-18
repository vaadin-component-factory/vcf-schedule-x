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
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.DayBoundaries;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.MonthGridOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.WeekOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventQueryFilter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarViewType;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.LocaleUtils;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceViewType;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ViewType;

@SuppressWarnings("serial")
@NpmPackage(value = "temporal-polyfill", version = "0.3.0")
@NpmPackage(value = "@schedule-x/calendar", version = "3.4.0")
@NpmPackage(value = "@schedule-x/theme-default", version = "3.4.0")
@NpmPackage(value = "@schedule-x/resize", version = "3.4.0")
@NpmPackage(value = "@schedule-x/drag-and-drop", version = "3.4.0")
@NpmPackage(value = "@schedule-x/current-time", version = "3.4.0")
@NpmPackage(value = "@schedule-x/scroll-controller", version = "3.4.0")
@NpmPackage(value = "@schedule-x/calendar-controls", version = "3.4.0")
@NpmPackage(value = "@schedule-x/event-recurrence", version = "3.4.0")
@NpmPackage(value = "@schedule-x/ical", version = "3.4.0")
@CssImport("@schedule-x/theme-default/dist/index.css")
@CssImport("./styles/vcf-schedule-x-calendar-styles.css")
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

  private CallbackDataProvider<Event, EventQueryFilter> dataProvider;

  /**
   * Current calendar view being shown.
   */
  private ViewType view;

  /**
   * Optional global calendar configuration settings.
   */
  private Configuration configuration;

  /**
   * Div containing the calendar.
   * 
   */
  protected Div container;

  private Registration refreshRegistration;

  public BaseScheduleXCalendar(List<? extends ViewType> views,
      CallbackDataProvider<Event, EventQueryFilter> dataProvider, Configuration configuration) {
    this.initCalendarContainer();
    this.add(container);
    this.views = new ArrayList<>(views);
    this.dataProvider = dataProvider;
    this.configuration = configuration;
    this.configuration.setCalendar(this);
  }

  public BaseScheduleXCalendar(List<? extends ViewType> views,
      CallbackDataProvider<Event, EventQueryFilter> dataProvider, Configuration configuration,
      Map<String, Calendar> calendars) {
    this(views, dataProvider, configuration);
    this.calendars = calendars;
  }

  private void initCalendarContainer() {
    container = new Div();
    container.setId("calendar-container-" + String.valueOf(this.hashCode()));
    container.setClassName("vcf-schedule-x-calendar");
    container.setSizeFull();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.initCalendar(false);
    addCalendarRenderedListener(() -> {
      // This listener ensures that `calendarRendered` is true after initial render
      // and subsequent calls to executeOnCalendarRendered will run immediately.
    });
  }

  /**
   * Initializes calendar with the initial configuration.
   */
  protected abstract void initCalendar(boolean refreshView);  

  /**
   * Schedule a calendar re-initialization to be called before the client response.
   */
  private void requireRefresh() {
    getUI().ifPresent(ui -> {
      if (refreshRegistration != null) {
        refreshRegistration.remove();
      }
      if (this.isAttached()) {
        refreshRegistration = ui.beforeClientResponse(this, context -> {
          initCalendar(this.view != null && this.view != this.configuration.getDefaultView());
        });
      }
    });    
  }

  /**
   * Refreshes the calendar by re-initializing it with the most current configuration. This triggers
   * a rebuild of the calendar component on the client side.
   */
  public void refreshCalendar() {
    this.getElement().executeJs("return").then(e -> {
      if(calendarRendered) {
        this.calendarRendered = false;
        this.remove(container);
        this.initCalendarContainer();
        this.add(container);
        this.requireRefresh();
      }
    });
  }

  protected String viewsToJson() {
    JsonArray jsonArray = Json.createArray();
    for (int i = 0; i < views.size(); i++) {
      jsonArray.set(i, views.get(i).getName());
    }
    return jsonArray.toJson();
  }

  protected String eventsToJson(LocalDateTime start, LocalDateTime end) {
    List<Event> events = dataProvider
        .fetch(new Query<>(0, Integer.MAX_VALUE, null, null, new EventQueryFilter(start, end)))
        .toList();
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

  public CallbackDataProvider<Event, EventQueryFilter> getDataProvider() {
    return dataProvider;
  }

  public void setDataProvider(CallbackDataProvider<Event, EventQueryFilter> dataProvider) {
    this.dataProvider = dataProvider;
  }

  /**
   * Returns current calendar configuration.
   * 
   * @return the configuration of the calendar
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    this.getElement().removeAllChildren();
  }

  @ClientCallable
  void updateRange(String start, String end) {
    LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);
    String events = eventsToJson(startDate, endDate);
    updateRange(events, start, end);
  }
  
  void updateRange(String events, String start, String end) {
    this.container.getElement().executeJs(getJsConnector() + ".onUpdateRange($0, $1, $2, $3);",
        this.container, events, start, end);
  }
  
  @ClientCallable
  void updateResourceSchedulerRange(String start, String end){
    LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);
    String events = eventsToJson(startDate, endDate);
    updateRange(events, start, end);
  }

  protected abstract String getJsConnector();

  /**
   * Sets the calendar view.
   * 
   * @param view the view to set
   */
  public void setView(ViewType view) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setView($0, $1);", this.container,
          view.getName());
      this.view = view;
    });
  }

  /**
   * Gets the current calendar view in display.
   * 
   * @return the current view
   */
  public ViewType getView() {
    return this.view != null ? this.view
        : this.configuration.getDefaultView() != null ? this.configuration.getDefaultView()
            : this.getViews().get(0);
  }

  /**
   * Sets the calendar date.
   * 
   * @param selectedDate the date to set
   */
  public void setDate(LocalDate selectedDate) {
    configuration.setSelectedDate(selectedDate);
  };

  protected void updateDate(LocalDate selectedDate) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setDate($0, $1);", this.container,
          selectedDate.format(DateTimeFormatUtils.DATE_FORMATTER));
    });
  }

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
    configuration.setFirstDayOfWeek(firstDayOfWeek);
  }

  protected void updateFirstDayOfWeek(Integer firstDayOfWeek) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setFirstDayOfWeek($0, $1);", this.container,
          firstDayOfWeek);
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
  public void setLocale(Locale locale) {
    configuration.setLocale(locale);
  }

  protected void updateLocale(Locale locale) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setLocale($0, $1);", this.container,
          LocaleUtils.toScheduleXLocale(locale));
    });
  }

  /**
   * Returns the current language/locale of the calendar.
   * 
   * @return the locale use by the calendar
   */
  public Locale getLocale() {
    return configuration.getLocale();
  }
  
  /**
   * Sets the time zone of the calendar.
   * 
   * @param timeZone the time zone of the calendar
   */
  public void setTimeZone(ZoneId timeZone) {
    configuration.setTimeZone(timeZone);
  }

  protected void updateTimeZone(ZoneId timeZone) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setTimeZone($0, $1);", this.container,
          timeZone);
    });
  }

  /**
   * Returns the current time zone of the calendar.
   * 
   * @return the time zone use by the calendar
   */
  public ZoneId getTimeZone() {
    return configuration.getTimeZone();
  }
  
  /**
   * Sets the available views for the calendar. The views to be set must include the currently
   * active view name. At least one view must be passed into this function.
   * 
   * @param views the views to be shown by the calendar
   */
  public void setViews(List<? extends ViewType> views) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setViews($0, $1);", this.container,
          viewsToJson());
      this.views = new ArrayList<>(views);
    });
  }

  /**
   * Returns the current views in the calendar.
   * 
   * @return the views currently displayed in the calendar
   */
  public List<? extends ViewType> getViews() {
    return views;
  }

  /**
   * Sets the day boundaries of the calendar.
   * 
   * @param dayBoundaries the day boundaries of the calendar
   */
  public void setDayBoundaries(DayBoundaries dayBoundaries) {
    configuration.setDayBoundaries(dayBoundaries);
  }

  protected void updateDayBoundaries(DayBoundaries dayBoundaries) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setDayBoundaries($0, $1);", this.container,
          dayBoundaries.toJson());
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
    configuration.setWeekOptions(weekOptions);
  }

  protected void updateWeekOptions(WeekOptions weekOptions) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setWeekOptions($0, $1);", this.container,
          weekOptions.toJson());
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
      this.getElement().executeJs(getJsConnector() + ".setCalendars($0, $1);", this.container,
          calendarsToJson());
      this.calendars = calendars;
    });
  }

  /**
   * Returns the available calendars displayed in the calendar.
   * 
   * @return the calendars displayed in the calendar
   */
  public Map<String, Calendar> getCalendars() {
    return calendars;
  }

  /**
   * Sets the min date for the calendar navigation.
   * 
   * @param minDate the min date for the calendar navigation
   */
  public void setMinDate(LocalDate minDate) {
    configuration.setMaxDate(minDate);
  }

  protected void updateMinDate(LocalDate minDate) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setMinDate($0, $1);", this.container,
          minDate.format(DateTimeFormatUtils.DATE_FORMATTER));
    });
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
    configuration.setMaxDate(maxDate);
  }

  protected void updateMaxDate(LocalDate maxDate) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setMaxDate($0, $1);", this.container,
          maxDate.format(DateTimeFormatUtils.DATE_FORMATTER));
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
    configuration.setMonthGridOptions(monthGridOptions);
  }

  protected void updateMonthGridOptions(MonthGridOptions monthGridOptions) {
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".setMonthGridOptions($0, $1);",
          this.container, monthGridOptions.toJson());
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
    this.getElement().executeJs(getJsConnector() + ".navigateForwards($0)", this.container);
  }

  /**
   * Allows to navigate calendar backwards.
   */
  public void navigateBackwards() {
    this.getElement().executeJs(getJsConnector() + ".navigateBackwards($0)", this.container);
  }

  /**
   * Handles calendar event click.
   * 
   * @param eventId id of the event being clicked
   * @param start the start date of the event
   * @param end the end date of the event
   */
  @ClientCallable
  private void onCalendarEventClick(String eventId, String start, String end) {
    String startFormatted = DateTimeFormatUtils.formatZonedDateTime(start);
    String endFormatted = DateTimeFormatUtils.formatZonedDateTime(end);
    ComponentUtil.fireEvent(this,
        new CalendarEventClickEvent(this, eventId, DateTimeFormatUtils.parseDate(startFormatted, false),
            DateTimeFormatUtils.parseDate(endFormatted, true), false));
  }

  /**
   * Event fired when a calendar event is clicked.
   */
  public class CalendarEventClickEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final String eventId;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public CalendarEventClickEvent(BaseScheduleXCalendar source, String eventId,
        LocalDateTime start, LocalDateTime end, boolean fromClient) {
      super(source, fromClient);
      this.eventId = eventId;
      this.start = start;
      this.end = end;
    }

    public String getEventId() {
      return eventId;
    }

    public LocalDateTime getStart() {
      return start;
    }

    public LocalDateTime getEnd() {
      return end;
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
  public class SelectedDateUpdateEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final LocalDate selectedDate;

    public SelectedDateUpdateEvent(BaseScheduleXCalendar source, LocalDate selectedDate,
        boolean fromClient) {
      super(source, fromClient);
      this.selectedDate = selectedDate;
    }

    public LocalDate getSelectedDate() {
      return selectedDate;
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
    this.executeOnCalendarRendered(() -> {
      this.getElement().executeJs(getJsConnector() + ".addEvent($0, $1);", this.container,
          event.getJson());
    });
  }

  /**
   * Removes the event with given id from the calendar.
   * 
   * @param eventId id of the event to be removed
   */
  public void removeEvent(String eventId) {
    this.getElement().executeJs(getJsConnector() + ".removeEvent($0, $1);", this.container,
        eventId);
  }

  /**
   * Updates the given event.
   * 
   * @param event the event to be updated
   */
  public void updateEvent(Event event) {
    this.getElement().executeJs(getJsConnector() + ".updateEvent($0, $1);", this.container,
        event.getJson());
  }

  /**
   * Event fired when a calendar event is added to the calendar.
   */
  @DomEvent("calendar-event-added")
  public static class CalendarEventAddedEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final String eventId;

    public CalendarEventAddedEvent(BaseScheduleXCalendar source, boolean fromClient,
        @EventData(value = "event.detail.eventId") String eventId) {
      super(source, fromClient);
      this.eventId = eventId;
    }

    public String getEventId() {
      return eventId;
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
  @DomEvent("calendar-event-removed")
  public static class CalendarEventRemovedEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final String eventId;

    public CalendarEventRemovedEvent(BaseScheduleXCalendar source, boolean fromClient,
        @EventData(value = "event.detail.eventId") String eventId) {
      super(source, fromClient);
      this.eventId = eventId;
    }

    public String getEventId() {
      return eventId;
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
  @DomEvent("calendar-event-updated")
  public static class CalendarEventUpdatedEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final String eventId;

    public CalendarEventUpdatedEvent(BaseScheduleXCalendar source, boolean fromClient,
        @EventData(value = "event.detail.eventId") String eventId) {
      super(source, fromClient);
      this.eventId = eventId;
    }

    public String getEventId() {
      return eventId;
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
    String startFormatted = DateTimeFormatUtils.formatZonedDateTime(start);
    String endFormatted = DateTimeFormatUtils.formatZonedDateTime(end);
    ComponentUtil.fireEvent(this,
        new EventUpdateEvent(this, eventId,
            LocalDateTime.parse(startFormatted, DateTimeFormatUtils.DATE_TIME_FORMATTER),
            LocalDateTime.parse(endFormatted, DateTimeFormatUtils.DATE_TIME_FORMATTER), false));
  }

  /**
   * Event fired when an event is updated on resize or drag and drop on client side.
   */
  public class EventUpdateEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final String eventId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public EventUpdateEvent(BaseScheduleXCalendar source, String eventId, LocalDateTime startDate,
        LocalDateTime endDate, boolean fromClient) {
      super(source, fromClient);
      this.eventId = eventId;
      this.startDate = startDate;
      this.endDate = endDate;
    }

    public String getEventId() {
      return eventId;
    }

    public LocalDateTime getStartDate() {
      return startDate;
    }

    public LocalDateTime getEndDate() {
      return endDate;
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

  /**
   * Programmatically sets the theme of the calendar to either dark or light mode.
   * <p>
   * For more details, see the <a href="https://schedule-x.dev/docs/calendar#theme">Schedule-X theme
   * documentation</a>.
   *
   * @param dark {@code true} to set the theme to dark mode, {@code false} for light mode
   */
  public void setDarkMode(boolean dark) {
    this.executeOnCalendarRendered(() -> {
      this.container.getElement().executeJs("this.calendar.setTheme($0)", dark ? "dark" : "light");
    });
  }

  /**
   * Event fired when the calendar view and selected date are updated on the client side.
   *
   * <p>
   * This event is dispatched from the client whenever the internal calendar state changes,
   * including both the selected view and the selected date (e.g., due to screen resize).
   * </p>
   */
  @DomEvent("calendar-state-view-date-updated")
  public static class CalendarViewAndDateChangeEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final ViewType viewType;
    private final LocalDate selectedDate;

    public CalendarViewAndDateChangeEvent(BaseScheduleXCalendar source, boolean fromClient,
        @EventData(value = "event.detail.viewName") String viewName,
        @EventData(value = "event.detail.selectedDate") String selectedDate) {
      super(source, fromClient);
      this.viewType = parseViewType(viewName);
      this.selectedDate = LocalDate.parse(selectedDate);
    }

    public ViewType getViewType() {
      return viewType;
    }

    public LocalDate getSelectedDate() {
      return selectedDate;
    }
  }

  /**
   * Adds a {@code CalendarViewAndDateChangeEvent} listener.
   * 
   * <p>
   * This event is dispatched from the client whenever the internal calendar state changes,
   * including both the selected view and the selected date (e.g., due to screen resize).
   * </p>
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addCalendarViewAndDateChangeEvent(
      ComponentEventListener<CalendarViewAndDateChangeEvent> listener) {
    return addListener(CalendarViewAndDateChangeEvent.class, listener);
  }

  /**
   * Parses a view name string to its corresponding {@link ViewType}.
   *
   * <p>
   * It first attempts to match it as a {@link CalendarViewType}. If not found, it then attempts
   * {@link ResourceViewType}. If still not found, an exception is thrown.
   * </p>
   *
   * @param viewName the view name string from the client
   * @return a matching {@link ViewType} instance
   * @throws IllegalArgumentException if the view name does not match any known type
   */
  private static ViewType parseViewType(String viewName) {
    return Optional.ofNullable(CalendarViewType.fromViewName(viewName)).map(ViewType.class::cast)
        .orElseGet(() -> Optional.ofNullable(ResourceViewType.fromViewName(viewName))
            .map(ViewType.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Unknown view type: " + viewName)));
  }

}
