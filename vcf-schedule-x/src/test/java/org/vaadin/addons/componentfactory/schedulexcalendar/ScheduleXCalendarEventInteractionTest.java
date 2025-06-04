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
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventQueryFilter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarViewType;

class ScheduleXCalendarEventInteractionTest {

  private ScheduleXCalendar calendar;

  @BeforeEach
  void setUp() {
    List<CalendarViewType> views = List.of(CalendarViewType.WEEK);
    Event event =
        new Event("e1", LocalDateTime.of(2025, 6, 2, 10, 00), LocalDateTime.of(2025, 6, 2, 12, 00));
    CallbackDataProvider<Event, EventQueryFilter> dataProvider = new CallbackDataProvider<>(
        query -> Collections.singletonList(event).stream(),
        query -> 1
    );
    calendar = new ScheduleXCalendar(views, dataProvider, new Configuration());
    CalendarTestUtils.forceCalendarRendered(calendar);
  }

  @Test
  void testAddEventCallsExecuteJs() {
    ScheduleXCalendar spy = Mockito.spy(calendar);
    Element mockElement = mock(Element.class);
    when(spy.getElement()).thenReturn(mockElement);

    Event event =
        new Event("e2", LocalDateTime.of(2025, 6, 1, 9, 0), LocalDateTime.of(2025, 6, 1, 10, 0));

    spy.addEvent(event);

    verify(mockElement, times(1)).executeJs(contains(".addEvent"), eq(spy.container), eq(event.getJson()));
  }

  @Test
  void testRemoveEventCallsExecuteJs() {
    ScheduleXCalendar spy = Mockito.spy(calendar);
    Element mockElement = mock(Element.class);
    when(spy.getElement()).thenReturn(mockElement);
    
    spy.removeEvent("e1");

    verify(spy.getElement(), times(1)).executeJs(contains(".removeEvent"), eq(spy.container), eq("e1"));
  }

  @Test
  void testUpdateEventCallsExecuteJs() {
    ScheduleXCalendar spy = Mockito.spy(calendar);
    Element mockElement = mock(Element.class);
    when(spy.getElement()).thenReturn(mockElement);

    Event event =
        new Event("e2", LocalDateTime.of(2025, 6, 1, 9, 0), LocalDateTime.of(2025, 6, 1, 10, 0));

    spy.updateEvent(event);

    // Modified verification: target spy.container
    verify(spy.getElement(), times(1)).executeJs(contains(".updateEvent"), eq(spy.container),
        eq(event.getJson()));
  }

  @Test
  void testEventAddedListenerFires() {
    AtomicReference<String> addedEventId = new AtomicReference<>();
    calendar.addCalendarEventAddedEventListener(e -> addedEventId.set(e.getEventId()));

    CalendarTestUtils.fireCalendarEventAdded(calendar, "abc123");

    assertEquals("abc123", addedEventId.get());
  }

  @Test
  void testEventRemovedListenerFires() {
    AtomicReference<String> removedEventId = new AtomicReference<>();
    calendar.addCalendarEventRemovedEventListener(e -> removedEventId.set(e.getEventId()));

    CalendarTestUtils.fireCalendarEventRemoved(calendar, "abc123");

    assertEquals("abc123", removedEventId.get());
  }

  @Test
  void testEventUpdatedListenerFires() {
    AtomicReference<String> updatedEventId = new AtomicReference<>();
    calendar.addCalendarEventUpdatedEventListener(e -> updatedEventId.set(e.getEventId()));

    CalendarTestUtils.fireCalendarEventUpdated(calendar, "abc123");

    assertEquals("abc123", updatedEventId.get());
  }
}
