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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import org.vaadin.addons.componentfactory.schedulexcalendar.EventProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXCalendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar.ColorDefinition;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;

/**
 * View for {@link ScheduleXCalendar} demo.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Route("")
public class ScheduleXCalendarDemoView extends DemoView {

  @Override
  public void initView() {
    this.getStyle().set("max-width", "1500px");
    createBasicDemo();

    addCard("Additional code used in the demo", new Span("These methods are used in the demo."));
  }

  private void createBasicDemo() {
    Div message = createMessageDiv("basic-use-demo-message");

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
    event2.setTitle("Meeting with Jackie");
    event2.setCalendarId("work");

    Configuration configuration = new Configuration();
    configuration.setSelectedDate("2025-04-17");
    configuration.setDefaultView(CalendarView.MONTH_GRID);

    ScheduleXCalendar calendar = new ScheduleXCalendar(
        Arrays.asList(CalendarView.DAY, CalendarView.WEEK, CalendarView.MONTH_GRID,
            CalendarView.MONTH_AGENDA),
        EventProvider.of(Arrays.asList(event1, event2)), configuration, calendars);

    // calendar.addValueChangeListener(ev->{
    // updateMessage(message, calendar);
    // });
    // end-source-example

    calendar.setId("basic-use-demo");

    addCard("Basic Use Demo", calendar, message);
  }


  // begin-source-example
  // source-example-heading: Additional code used in the demo
  /**
   * Additional code used in the demo
   */
  private void updateMessage(Div message, ScheduleXCalendar calendar) {
    // message.setText("Entered text: " + calendar.getValue());
  }

  private Div createMessageDiv(String id) {
    Div message = new Div();
    message.setId(id);
    message.getStyle().set("whiteSpace", "pre");
    return message;
  }
  // end-source-example
}
