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
package org.vaadin.addons.componentfactory.schedulexcalendar.model;

import com.vaadin.flow.function.SerializableBiFunction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.RecurrenceEvaluator;

/**
 * Provides a flexible mechanism for supplying {@link Event} instances within a given date range.
 * <p>
 * This class allows you to define the source of events either as a fixed list or via a dynamic
 * callback that generates events based on the given time interval. It supports recurring event
 * evaluation through a {@link RecurrenceEvaluator}.
 */
public class EventProvider {

  SerializableBiFunction<LocalDateTime, LocalDateTime, List<Event>> eventProviderCallback;


  /**
   * Constructs an {@code EventProvider} using the given callback function.
   *
   * @param eventProviderCallback a function that returns events within a date range
   */
  public EventProvider(
      SerializableBiFunction<LocalDateTime, LocalDateTime, List<Event>> eventProviderCallback) {
    this.eventProviderCallback = eventProviderCallback;
  }

  /**
   * Creates an {@code EventProvider} that serves events from a fixed list.
   * <p>
   * The returned provider will filter the events to include only those that:
   * <ul>
   *   <li>Start after the requested start time and end before the requested end time</li>
   *   <li>Or, if recurring, have at least one occurrence within the given time range</li>
   * </ul>
   *
   * @param events the list of events to serve
   * @return a new {@code EventProvider} instance
   */
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

  /**
   * Creates an {@code EventProvider} from the given callback.
   *
   * @param eventProviderCallback a function that returns events within a date range
   * @return a new {@code EventProvider} instance
   */
  public static EventProvider of(
      SerializableBiFunction<LocalDateTime, LocalDateTime, List<Event>> eventProviderCallback) {
    EventProvider eventProvider = new EventProvider(eventProviderCallback);
    return eventProvider;
  }

  /**
   * Returns a list of events that occur within the specified date-time range.
   *
   * @param start the start of the range (inclusive)
   * @param end   the end of the range (exclusive)
   * @return the list of events in the specified time interval
   */
  public List<Event> getEvents(LocalDateTime start, LocalDateTime end) {
    return eventProviderCallback.apply(start, end);
  }

}
