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

import com.vaadin.flow.component.ComponentUtil;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;

public class CalendarTestUtils {

  public static void forceCalendarRendered(BaseScheduleXCalendar calendar) {
    try {
      Field field = BaseScheduleXCalendar.class.getDeclaredField("calendarRendered");
      field.setAccessible(true);
      field.setBoolean(calendar, true);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Unable to set calendarRendered flag", e);
    }
  }

  public static void fireCalendarEventAdded(BaseScheduleXCalendar calendar, String eventId) {
    ComponentUtil.fireEvent(calendar,
        new BaseScheduleXCalendar.CalendarEventAddedEvent(calendar, false, eventId));
  }

  public static void fireCalendarEventRemoved(BaseScheduleXCalendar calendar, String eventId) {
    ComponentUtil.fireEvent(calendar,
        new BaseScheduleXCalendar.CalendarEventRemovedEvent(calendar, false, eventId));
  }

  public static void fireCalendarEventUpdated(BaseScheduleXCalendar calendar, String eventId) {
    ComponentUtil.fireEvent(calendar,
        new BaseScheduleXCalendar.CalendarEventUpdatedEvent(calendar, false, eventId));
  }

  public static void fireEventUpdate(BaseScheduleXCalendar calendar, String eventId, String start,
      String end) {
    ComponentUtil.fireEvent(calendar,
        calendar.new EventUpdateEvent(calendar, eventId,
            LocalDateTime.parse(start, DateTimeFormatUtils.DATE_TIME_FORMATTER),
            LocalDateTime.parse(end, DateTimeFormatUtils.DATE_TIME_FORMATTER), false));
  }

}
