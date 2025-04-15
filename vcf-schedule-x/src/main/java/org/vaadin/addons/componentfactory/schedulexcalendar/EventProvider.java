package org.vaadin.addons.componentfactory.schedulexcalendar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;

import com.vaadin.flow.function.SerializableBiFunction;

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
      return events.stream().filter(
          event -> event.getStartDateTime().isAfter(startDate) && event.getEndDateTime().isBefore(endDate))
          .collect(Collectors.toList());
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
