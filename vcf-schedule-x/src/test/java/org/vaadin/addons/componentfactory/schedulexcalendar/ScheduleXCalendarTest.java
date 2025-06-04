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

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventQueryFilter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarViewType;

class ScheduleXCalendarTest {

  private Configuration configuration;
  private ScheduleXCalendar calendar;
  private List<CalendarViewType> views;

  @BeforeEach
  void setUp() {
    // Mock event provider replaced with CallbackDataProvider
    LocalDate date = LocalDate.of(2025, 01, 01);
    Event event = new Event("event-id", LocalDateTime.of(date, LocalTime.of(10, 00)),
        LocalDateTime.of(date, LocalTime.of(12, 00)));

    CallbackDataProvider<Event, EventQueryFilter> dataProvider = new CallbackDataProvider<>(
        query -> Collections.singletonList(event).stream(),
        query -> 1
    );

    // Minimal configuration
    configuration = new Configuration();
    configuration.setDefaultView(CalendarViewType.WEEK);
    configuration.setFirstDayOfWeek(1);
    configuration.setSelectedDate(date);

    views = List.of(CalendarViewType.WEEK, CalendarViewType.DAY);
    calendar = new ScheduleXCalendar(views, dataProvider, configuration);
    CalendarTestUtils.forceCalendarRendered(calendar);
  }

  @Test
  void testGetEventsFromProvider() {
    LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 1, 2, 0, 0);

    List<Event> events = calendar.getDataProvider().fetch(new Query<>(0, Integer.MAX_VALUE, null, null, new EventQueryFilter(start, end))).toList();
    assertEquals(1, events.size());
    assertEquals("event-id", events.get(0).getId());
  }

  @Test
  void testInitialViewSetCorrectly() {
    assertEquals(CalendarViewType.WEEK, calendar.getView());
    assertEquals(CalendarViewType.WEEK, calendar.getConfiguration().getDefaultView());
  }

  @Test
  void testSetDateUpdatesConfiguration() {
    LocalDate date = LocalDate.of(2025, 1, 5);
    calendar.setDate(date);
    assertEquals(date, calendar.getDate());
    assertEquals(date, calendar.getConfiguration().getSelectedDate());
  }

  @Test
  void testSetFirstDayOfWeekUpdatesConfiguration() {
    calendar.setFirstDayOfWeek(2);
    assertEquals(2, calendar.getFirstDayOfWeek());
    assertEquals(2, calendar.getConfiguration().getFirstDayOfWeek());
  }

  @Test
  void testSetViewsUpdatesInternalList() {
    List<CalendarViewType> newViews = List.of(CalendarViewType.WEEK, CalendarViewType.MONTH_GRID);
    calendar.setViews(newViews);
    assertEquals(CalendarViewType.WEEK, calendar.getViews().get(0));
    assertEquals(CalendarViewType.MONTH_GRID, calendar.getViews().get(1));
  }
}
