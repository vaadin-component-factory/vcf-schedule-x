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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.vaadin.addons.componentfactory.schedulexcalendar.EventProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXCalendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar.ColorDefinition;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration.CurrentTimeIndicatorConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration.ScrollControllerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.RecurrenceRule;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.RecurrenceRule.Day;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.RecurrenceRule.Frequency;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.RecurrenceRule.Until;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.TimeInterval;

/**
 * View for {@link ScheduleXCalendar} demo.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Route("")
public class ScheduleXCalendarDemoView extends DemoView {

  private List<Event> events;

  private ScheduleXCalendar calendar;

  @Override
  public void initView() {
    this.getStyle().set("max-width", "1500px");
    createBasicDemo();

    addCard("Additional code used in the demo", new Span("These methods are used in the demo."));
  }

  private void createBasicDemo() {
    // begin-source-example
    // source-example-heading: Basic Use Demo

    Calendar work = new Calendar("work");
    work.setLightColors(new ColorDefinition("#f91c45", "#ffd2dc", "#59000d"));
    work.setDarkColors(new ColorDefinition("#ffc0cc", "#a24258", "#ffdee6"));
    Calendar leisure = new Calendar("leisure");
    leisure.setLightColors(new ColorDefinition("#1cf9b0", "#dafff0", "#004d3d"));
    leisure.setDarkColors(new ColorDefinition("#c0fff5", "#42a297", "#e6fff5"));
    Map<String, Calendar> calendars = Map.of("work", work, "leisure", leisure);

    LocalDate today = LocalDate.now();

    Event event1 = new Event("1", LocalDateTime.of(today.minusDays(2), LocalTime.of(10, 05)),
        LocalDateTime.of(today.minusDays(2), LocalTime.of(10, 35)));
    event1.setTitle("Coffee with John");
    event1.setCalendarId("leisure");
    Event event2 = new Event("2", LocalDateTime.of(today.minusDays(1), LocalTime.of(16, 00)),
        LocalDateTime.of(today.minusDays(1), LocalTime.of(16, 45)));
    event2.setTitle("Meeting with Jackie O.");
    event2.setCalendarId("work");

    Event event3 = new Event("3", LocalDateTime.of(today.plusDays(5), LocalTime.of(15, 00)),
        LocalDateTime.of(today.plusDays(5), LocalTime.of(15, 25)));
    event3.setTitle("Onboarding team meeting");
    event3.setCalendarId("work");

    events = new ArrayList<Event>();
    events.addAll(Arrays.asList(event1, event2, event3));

    Configuration configuration = new Configuration();
    configuration.setSelectedDate(today.plusDays(1));
    configuration.setDefaultView(CalendarView.WEEK);
    configuration.setDragAndDropInterval(TimeInterval.MIN_30);
    CurrentTimeIndicatorConfig currentTimeIndicator = new CurrentTimeIndicatorConfig();
    currentTimeIndicator.setFullWeekWidth(true);
    currentTimeIndicator.setTimeZoneOffset(120);
    configuration.setCurrentTimeIndicatorConfig(currentTimeIndicator);

    // configure initial scroll value
    ScrollControllerConfig scrollControllerConfig = new ScrollControllerConfig();
    scrollControllerConfig.setInitialScroll(LocalTime.of(14, 50));
    configuration.setScrollControllerConfig(scrollControllerConfig);

    calendar = new ScheduleXCalendar(Arrays.asList(CalendarView.DAY, CalendarView.WEEK,
        CalendarView.MONTH_GRID, CalendarView.MONTH_AGENDA), EventProvider.of(events),
        configuration, calendars);
    calendar.setDrawSnapDuration(15);

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

    CalendarHeaderComponent header = new CalendarHeaderComponent(calendar);

    calendar.setHeight("500px");
    // end-source-example

    calendar.setId("basic-use-demo");

    HorizontalLayout horizontal1 =
        new HorizontalLayout(getSingleEventHandlingLayout(), getScrollingLayout());
    horizontal1.setWidthFull();
    addCard("Basic Use Demo", header, calendar, horizontal1, getRecurringEventHandlingLayout());
  }

  // begin-source-example
  // source-example-heading: Additional code used in the demo
  private FieldSet getSingleEventHandlingLayout() {
    HorizontalLayout layout = new HorizontalLayout();
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
    HorizontalLayout layout = new HorizontalLayout();
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

    return createFieldSetLayout("Scrolling testing (only available for week and day views)",
        layout);
  }

  private FieldSet createFieldSetLayout(String text, Component component) {
    FieldSet fieldSet = new FieldSet(text, component);
    fieldSet.getStyle().setBorderRadius("var(--lumo-border-radius-s)");
    return fieldSet;
  }

  // end-source-example
}
