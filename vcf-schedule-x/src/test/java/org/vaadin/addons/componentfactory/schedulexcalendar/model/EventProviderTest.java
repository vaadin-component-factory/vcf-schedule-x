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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventProviderTest {

  private List<Event> eventList;
  private EventProvider provider;

  @BeforeEach
  void setup() {
    eventList = new ArrayList<>();
    provider = EventProvider.of(eventList);
  }

  @Test
  void testAddingEventUpdatesProviderResults() {
    LocalDateTime now = LocalDateTime.of(2025, 6, 1, 9, 0);
    LocalDateTime later = now.plusHours(1);

    Event event = new Event("e1", now, later);
    eventList.add(event); 

    List<Event> events = provider.getEvents(now.minusHours(1), later.plusHours(1));

    assertEquals(1, events.size());
    assertEquals("e1", events.get(0).getId());
  }

  @Test
  void testNonMatchingEventIsFilteredOut() {
    LocalDateTime now = LocalDateTime.of(2025, 6, 1, 9, 0);
    Event event =
        new Event("e2", LocalDateTime.of(2025, 7, 1, 10, 0), LocalDateTime.of(2025, 7, 1, 11, 0));
    eventList.add(event);

    List<Event> result = provider.getEvents(now, now.plusHours(1));

    assertTrue(result.isEmpty(), "Non-overlapping event should be filtered out");
  }
}
