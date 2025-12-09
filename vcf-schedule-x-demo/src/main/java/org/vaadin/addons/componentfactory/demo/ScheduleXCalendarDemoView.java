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
package org.vaadin.addons.componentfactory.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.CurrentTimeIndicatorConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.DrawOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.ICal;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration.ScrollControllerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXCalendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event.EventOptions;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventQueryFilter;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.RecurrenceRule;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.RecurrenceRule.Day;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.RecurrenceRule.Frequency;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.RecurrenceRule.Until;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarViewType;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.RecurrenceEvaluator;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.TimeInterval;

/**
 * View for {@link ScheduleXCalendar} demo.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Route(value = "calendar", layout = DemoMainLayout.class)
public class ScheduleXCalendarDemoView extends ScheduleXBaseDemoView {

  private List<Event> events;
  private ScheduleXCalendar calendar;
  private CalendarHeaderComponent header;

  @Override
  protected void createDemo() {
    // begin-source-example
    // source-example-heading: Calendar Demo

    // add calendar configuration
    LocalDate today = LocalDate.now();
    Configuration configuration = new Configuration();
    configuration.setSelectedDate(today.plusDays(1));
    configuration.setDefaultView(CalendarViewType.WEEK);
    configuration.setDragAndDropInterval(TimeInterval.MIN_30);
    // create categories for events
    Map<String, Calendar> calendars = getCalendars();

    // create events
    Event event1 = new Event("1", LocalDateTime.of(today.minusDays(2), LocalTime.of(10, 05)),
        LocalDateTime.of(today.minusDays(2), LocalTime.of(10, 35)));
    event1.setTitle("Coffee with John");
    event1.setCalendarId("leisure");
    Event event2 = new Event("2", LocalDateTime.of(today.minusDays(1), LocalTime.of(16, 00)),
        LocalDateTime.of(today.minusDays(1), LocalTime.of(16, 45)));
    event2.setTitle("Meeting with Jackie O.");
    event2.setCalendarId("work");
    Event event3 = new Event("3", LocalDateTime.of(today, LocalTime.of(15, 00)),
        LocalDateTime.of(today, LocalTime.of(15, 25)));
    event3.setTitle("Onboarding team meeting");
    event3.setCalendarId("work");
    Event event4 = new Event("4", LocalDateTime.of(today.plusDays(1), LocalTime.of(19, 00)),
        LocalDateTime.of(today.plusDays(1), LocalTime.of(19, 45)));
    event4.setTitle("Doctor's appointment");
    event4.setCalendarId("leisure");
    Event event5 = new Event("5", LocalDateTime.of(today.plusDays(2), LocalTime.of(11, 00)),
        LocalDateTime.of(today.plusDays(2), LocalTime.of(12, 30)));
    event5.setTitle("Team meeting");
    event5.setCalendarId("work");
    Event event6 = new Event("6", LocalDateTime.of(today.plusDays(3), LocalTime.of(10, 00)),
        LocalDateTime.of(today.plusDays(3), LocalTime.of(10, 45)));
    event6.setTitle("Team meeting");
    event6.setCalendarId("work");
    Event event7 = new Event("7", LocalDateTime.of(today.plusDays(4), LocalTime.of(12, 00)),
        LocalDateTime.of(today.plusDays(4), LocalTime.of(13, 00)));
    event7.setTitle("Lunch with John");
    event7.setCalendarId("leisure");
    Event event8 = new Event("8", LocalDateTime.of(today.plusDays(15), LocalTime.of(10, 00)),
        LocalDateTime.of(today.plusDays(15), LocalTime.of(11, 00)));
    event8.setTitle("Meeting with Anne");
    event8.setCalendarId("work");
    
    // define options for event 1 to add additional classes for styling
    EventOptions event1Options = new EventOptions();
    event1Options.setAdditionalClasses(Arrays.asList("my_additional_class"));
    event1.setOptions(event1Options);
    
    events = new ArrayList<Event>();
    events.addAll(Arrays.asList(event1, event2, event3, event4, event5, event6, event7, event8));

    // current time indicator
    CurrentTimeIndicatorConfig currentTimeIndicator = new CurrentTimeIndicatorConfig();
    currentTimeIndicator.setFullWeekWidth(true);
    currentTimeIndicator.setTimeZoneOffset(120);
    configuration.setCurrentTimeIndicatorConfig(currentTimeIndicator);

    // configure initial scroll value
    ScrollControllerConfig scrollControllerConfig = new ScrollControllerConfig();
    scrollControllerConfig.setInitialScroll(LocalTime.of(14, 50));
    configuration.setScrollControllerConfig(scrollControllerConfig);

    // Ical
    ICal ical = new ICal();
    ical.setiCal("""
        BEGIN:VCALENDAR
        VERSION:2.0
        CALSCALE:GREGORIAN
        BEGIN:VEVENT
        SUMMARY:Good morning
        DTSTART;TZID=America/New_York:20250506T103400
        DTEND;TZID=America/New_York:20250506T110400
        LOCATION:1000 Broadway Ave.\\, Brooklyn
        DESCRIPTION: Access-A-Ride trip to 900 Jay St.\\, Brooklyn
        STATUS:CONFIRMED
        SEQUENCE:3
        END:VEVENT
        BEGIN:VEVENT
        RRULE:FREQ=DAILY;COUNT=3
        SUMMARY:Good night
        DTSTART;TZID=America/New_York:20250509T200000
        DTEND;TZID=America/New_York:20250509T203000
        LOCATION:900 Jay St.\\, Brooklyn
        DESCRIPTION: Access-A-Ride trip to 1000 Broadway Ave.\\, Brooklyn
        STATUS:CONFIRMED
        SEQUENCE:3
        END:VEVENT
        END:VCALENDAR
        """);
    configuration.setiCal(ical);

    // draw options
    DrawOptions drawOptions = new DrawOptions();
    drawOptions.setDefaultTitle("New event");
    drawOptions.setSnapDrawDuration(TimeInterval.MIN_15);
    configuration.setDrawOptions(drawOptions);

    CallbackDataProvider<Event, EventQueryFilter> dataProvider =
        new CallbackDataProvider<>(query -> {
          EventQueryFilter filter = query.getFilter().orElse(null);
          if (filter != null) {
            return events.stream().filter(event -> {
              boolean isWithinRange = !event.getStart().isAfter(filter.getEndDate())
                  && !event.getEnd().isBefore(filter.getStartDate());
              boolean isRecurringInRange = event.getRecurrenceRule() != null && RecurrenceEvaluator
                  .occursInRange(event.getRecurrenceRule(), event.getStart().toLocalDate(),
                      filter.getStartDate().toLocalDate(), filter.getEndDate().toLocalDate());
              return isWithinRange || isRecurringInRange;
            });
          }
          return events.stream();
        }, query -> {
          EventQueryFilter filter = query.getFilter().orElse(null);
          if (filter != null) {
            return (int) events.stream().filter(event -> {
              boolean isWithinRange = !event.getStart().isAfter(filter.getEndDate())
                  && !event.getEnd().isBefore(filter.getStartDate());
              boolean isRecurringInRange = event.getRecurrenceRule() != null && RecurrenceEvaluator
                  .occursInRange(event.getRecurrenceRule(), event.getStart().toLocalDate(),
                      filter.getStartDate().toLocalDate(), filter.getEndDate().toLocalDate());
              return isWithinRange || isRecurringInRange;
            }).count();
          }
          return events.size();
        });

    // create calendar
    calendar = new ScheduleXCalendar(
        Arrays.asList(CalendarViewType.DAY, CalendarViewType.WEEK, CalendarViewType.MONTH_GRID,
            CalendarViewType.MONTH_AGENDA, CalendarViewType.LIST),
        dataProvider, configuration, calendars);

    // add event click listener
    calendar.addCalendarEventClickEventListener(
        e -> Notification.show("Event with id " + e.getEventId() + " clicked"));

    // add listener on event on dnd
    calendar.addEventUpdateEventListener(e -> {
      String updatedEventId = e.getEventId();
      Optional<Event> optionalEvent =
          events.stream().filter(ev -> ev.getId().equals(updatedEventId)).findFirst();
      optionalEvent.ifPresent(event -> {
        event.setStart(e.getStartDate());
        event.setEnd(e.getEndDate());
        Notification.show("Event with id " + updatedEventId + " updated");
      });
    });

    // create header component
    header = new CalendarHeaderComponent(calendar);

    calendar.setHeight("600px");
    // end-source-example

    calendar.setId("calendar-demo");

    HorizontalLayout horizontal1 =
        new HorizontalLayout(getSingleEventHandlingLayout(), getRecurringEventHandlingLayout());
    horizontal1.setWidthFull();

    HorizontalLayout horizontal2 =
        new HorizontalLayout(getScrollingLayout(), getCurrentTimeIndicatorLayout());
    horizontal2.setWidthFull();
    addCard("Calendar Demo", header, calendar, horizontal1, horizontal2);
  }

  // begin-source-example
  // source-example-heading: Additional code used in the demo
  private FieldSet getSingleEventHandlingLayout() {
    VerticalLayout layout = new VerticalLayout();
    layout.setWidthFull();

    LocalDate testEventDate = LocalDate.now().plusDays(1);

    Event testEvent = new Event("test", LocalDateTime.of(testEventDate, LocalTime.of(10, 00)),
        LocalDateTime.of(testEventDate, LocalTime.of(11, 00)));
    testEvent.setTitle("Test event");

    Button addTestEventButton = new Button("Click to add test event");
    Button updateTestEventButton = new Button("Click to update test event");
    Button removeTestEventButton = new Button("Click to remove test event");

    addTestEventButton.addClickListener(e -> {
      calendar.addEvent(testEvent);
      events.add(testEvent);
      updateTestEventButton.setEnabled(true);
      removeTestEventButton.setEnabled(true);
    });
    addTestEventButton.setDisableOnClick(true);

    updateTestEventButton.addClickListener(e -> {
      testEvent.setTitle("Test event updated");
      calendar.updateEvent(testEvent);
    });
    updateTestEventButton.setEnabled(false);
    updateTestEventButton.setDisableOnClick(true);

    removeTestEventButton.addClickListener(e -> {
      calendar.removeEvent(testEvent.getId());
      events.remove(testEvent);
      testEvent.setTitle("Test event");
      addTestEventButton.setEnabled(true);
      updateTestEventButton.setEnabled(false);
    });
    removeTestEventButton.setEnabled(false);
    removeTestEventButton.setDisableOnClick(true);

    calendar.addCalendarEventDrawnEventListener(e -> events.add(e.getEvent()));

    calendar.setDrawnEventValidationCallback(
        e -> e.getStart().getHour() > 7 || e.getStart().getHour() < 1);

    calendar.addCalendarEventAddedEventListener(
        e -> Notification.show("Calendar event with id '" + e.getEventId() + "' added."));

    calendar.addCalendarEventUpdatedEventListener(
        e -> Notification.show("Calendar event with id '" + e.getEventId() + "' updated."));

    calendar.addCalendarEventRemovedEventListener(
        e -> Notification.show("Calendar event with id '" + e.getEventId() + "' removed."));

    layout.add(addTestEventButton, updateTestEventButton, removeTestEventButton);
    return createFieldSetLayout("Single Event Handling testing", layout);
  }

  private FieldSet getRecurringEventHandlingLayout() {
    VerticalLayout layout = new VerticalLayout();
    layout.setWidthFull();

    LocalDate testEventDate = LocalDate.of(2025, 05, 05);
    String eventTitle = "Bi-Weekly Event Monday and Wednesday";

    Event testEvent =
        new Event("recurring-test", LocalDateTime.of(testEventDate, LocalTime.of(10, 00)),
            LocalDateTime.of(testEventDate, LocalTime.of(12, 00)));
    testEvent.setTitle(eventTitle);

    // rrule: 'FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,WE;UNTIL=20260505T235959'
    RecurrenceRule recurrenceRule = new RecurrenceRule(Frequency.WEEKLY);
    recurrenceRule.setInterval(2);
    recurrenceRule.setByDay(Arrays.asList(Day.MO, Day.WE));
    LocalDate untilDate = testEventDate.plusYears(1);
    LocalTime untilTime = LocalTime.of(23, 59, 59);
    Until until = new Until(untilDate, untilTime);
    recurrenceRule.setUntil(until);
    testEvent.setRecurrenceRule(recurrenceRule);
    testEvent.setExcludedDates(
        Arrays.asList(LocalDateTime.of(LocalDate.of(2025, 06, 04), LocalTime.of(10, 00)),
            LocalDateTime.of(LocalDate.of(2025, 06, 18), LocalTime.of(10, 00))));
    EventOptions eventOptions = new EventOptions();
    eventOptions.setDisableDND(true);
    eventOptions.setDisableResize(true);
    testEvent.setOptions(eventOptions);

    Button addTestEventButton = new Button("Click to add recurring test event");
    Button updateTestEventButton = new Button("Click to update recurring test event");
    Button removeTestEventButton = new Button("Click to remove recurring test event");

    addTestEventButton.addClickListener(e -> {
      calendar.addEvent(testEvent);
      events.add(testEvent);
      updateTestEventButton.setEnabled(true);
      removeTestEventButton.setEnabled(true);
    });
    addTestEventButton.setDisableOnClick(true);

    updateTestEventButton.addClickListener(e -> {
      testEvent.setTitle(eventTitle + " Updated");
      calendar.updateEvent(testEvent);
    });
    updateTestEventButton.setEnabled(false);
    updateTestEventButton.setDisableOnClick(true);

    removeTestEventButton.addClickListener(e -> {
      calendar.removeEvent(testEvent.getId());
      events.remove(testEvent);
      testEvent.setTitle(eventTitle);
      addTestEventButton.setEnabled(true);
      updateTestEventButton.setEnabled(false);
    });
    removeTestEventButton.setEnabled(false);
    removeTestEventButton.setDisableOnClick(true);

    layout.add(addTestEventButton, updateTestEventButton, removeTestEventButton);
    return createFieldSetLayout("Recurring Event Handling testing", layout);
  }

  private FieldSet getScrollingLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();

    TimePicker timePicker = new TimePicker("Select a time to scroll to");
    timePicker.addValueChangeListener(e -> {
      calendar.scrollTo(e.getValue());
    });

    layout.add(timePicker);

    VerticalLayout helperLayout =
        createLayoutWithHelperText("Only available for week and day views", layout);

    return createFieldSetLayout("Scrolling testing", helperLayout);
  }

  private FieldSet getCurrentTimeIndicatorLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();
    
    Checkbox fullWeekWidth = new Checkbox(
        calendar.getConfiguration().getCurrentTimeIndicatorConfig().getFullWeekWidth());
    fullWeekWidth.setLabel("Check to display the indicator in full width of the week");
    fullWeekWidth.addValueChangeListener(e -> {
      boolean enabled = e.getValue();
      calendar.getConfiguration().getCurrentTimeIndicatorConfig().setFullWeekWidth(enabled);
    });

    layout.add(fullWeekWidth);

    VerticalLayout helperLayout =
        createLayoutWithHelperText("Only available for week view", layout);

    return createFieldSetLayout("Current Time Indicator testing", helperLayout);
  }
  // end-source-example
}
