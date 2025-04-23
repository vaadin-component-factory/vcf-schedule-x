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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.vaadin.addons.componentfactory.schedulexcalendar.EventProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXCalendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar.ColorDefinition;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;

/**
 * View for {@link ScheduleXCalendar} demo.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Route("")
public class ScheduleXCalendarDemoView extends DemoView {

  private List<Event> events;
  
  @Override
  public void initView() {
    this.getStyle().set("max-width", "1500px");
    createBasicDemo();
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

    Event event1 =
        new Event("1", LocalDateTime.of(LocalDate.of(2025, 04, 15), LocalTime.of(10, 05)),
            LocalDateTime.of(LocalDate.of(2025, 04, 15), LocalTime.of(10, 35)));
    event1.setTitle("Coffee with John");
    event1.setCalendarId("leisure");
    Event event2 =
        new Event("2", LocalDateTime.of(LocalDate.of(2025, 04, 16), LocalTime.of(16, 00)),
            LocalDateTime.of(LocalDate.of(2025, 04, 16), LocalTime.of(16, 45)));
    event2.setTitle("Meeting with Jackie O.");
    event2.setCalendarId("work");

    Event event3 =
        new Event("3", LocalDateTime.of(LocalDate.of(2025, 05, 29), LocalTime.of(15, 00)),
            LocalDateTime.of(LocalDate.of(2025, 05, 29), LocalTime.of(15, 25)));
    event3.setTitle("Onboarding team meeting");
    event3.setCalendarId("work");
        
    events = new ArrayList<Event>();
    events.addAll(Arrays.asList(event1, event2, event3));

    Configuration configuration = new Configuration();
    configuration.setSelectedDate(LocalDate.of(2025, 04, 17));
    configuration.setDefaultView(CalendarView.MONTH_GRID);
      
    ScheduleXCalendar calendar = new ScheduleXCalendar(
        Arrays.asList(CalendarView.DAY, CalendarView.WEEK, CalendarView.MONTH_GRID,
            CalendarView.MONTH_AGENDA),
        EventProvider.of(events), configuration, calendars);

    calendar.addCalendarEventClickEventListener(
        e -> Notification.show("Event with id " + e.getEventId() + " clicked"));

    CalendarHeaderComponent header = new CalendarHeaderComponent(calendar);
    
    // end-source-example

    calendar.setId("basic-use-demo");

    addCard("Basic Use Demo", header, calendar, getEventHandlingLayout(calendar));
  }

  private HorizontalLayout getEventHandlingLayout(ScheduleXCalendar calendar) {
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

    calendar.addCalendarEventAddedEventListener(
        e -> Notification.show("Calendar event with id '" + e.getEventId() + "' added."));
    
    calendar.addCalendarEventUpdatedEventListener(
        e -> Notification.show("Calendar event with id '" + e.getEventId() + "' updated."));
    
    calendar.addCalendarEventRemovedEventListener(
        e -> Notification.show("Calendar event with id '" + e.getEventId() + "' removed."));

    layout.add(addTestEventButton, updateTestEventButton, removeTestEventButton);
    return layout;
  }
}
