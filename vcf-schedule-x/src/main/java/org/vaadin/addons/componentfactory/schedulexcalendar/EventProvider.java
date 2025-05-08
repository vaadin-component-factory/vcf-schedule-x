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

import com.vaadin.flow.function.SerializableBiFunction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.RecurrenceEvaluator;

/**
 * This class provides events from different sources
 */
public class EventProvider {

  SerializableBiFunction<LocalDateTime, LocalDateTime, List<Event>> eventProviderCallback;

  public EventProvider(
      SerializableBiFunction<LocalDateTime, LocalDateTime, List<Event>> eventProviderCallback) {
    this.eventProviderCallback = eventProviderCallback;
  }

  public static EventProvider of(List<Event> events) {
    EventProvider eventProvider = new EventProvider((startDate, endDate) -> {
      return events.stream().filter(event -> {
        boolean isWithin = event.getStart().isAfter(startDate) && event.getEnd().isBefore(endDate);
        boolean isRecurringInRange = false;

        if (event.getRecurrenceRule() != null) {
          LocalDate start = event.getStart().toLocalDate();
          isRecurringInRange = RecurrenceEvaluator.occursInRange(event.getRecurrenceRule(), start,
              startDate.toLocalDate(), endDate.toLocalDate());
        }

        return isWithin || isRecurringInRange;

      }).collect(Collectors.toList());
    });
    return eventProvider;

  }

  public static EventProvider of(
      SerializableBiFunction<LocalDateTime, LocalDateTime, List<Event>> eventProviderCallback) {
    EventProvider eventProvider = new EventProvider(eventProviderCallback);
    return eventProvider;
  }

  public List<Event> getEvents(LocalDateTime start, LocalDateTime end) {
    return eventProviderCallback.apply(start, end);
  }

}
