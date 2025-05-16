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
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.vaadin.flow.component.ComponentUtil;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXResourceScheduler.SchedulingAssistantUpdateEvent;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.ResourceSchedulerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceViewType;

class ScheduleXResourceSchedulerEventTest {

  @Test
  void testSchedulingAssistantUpdateEventIsFiredAndHandled() {
    // Setup component
    ScheduleXResourceScheduler resourceScheduler = new ScheduleXResourceScheduler(List.of(ResourceViewType.HOURLY),
        EventProvider.of((s, e) -> List.of()), new Configuration(), null,
        new ResourceSchedulerConfig());
    CalendarTestUtils.forceCalendarRendered(resourceScheduler);

    // Prepare container for values received
    AtomicReference<String> receivedStart = new AtomicReference<>();
    AtomicReference<String> receivedEnd = new AtomicReference<>();
    AtomicReference<Boolean> receivedCollision = new AtomicReference<>();

    // Register listener
    resourceScheduler.addSchedulingAssistantUpdateListener(event -> {
      receivedStart.set(event.getCurrentStart());
      receivedEnd.set(event.getCurrentEnd());
      receivedCollision.set(event.isHasCollision());
    });

    // Simulate client-side event firing
    ComponentUtil.fireEvent(resourceScheduler,
        new SchedulingAssistantUpdateEvent(resourceScheduler, true, "2025-06-01 09:00", "2025-06-01 10:00", true // hasCollision
        ));

    // Assertions
    assertEquals("2025-06-01 09:00", receivedStart.get());
    assertEquals("2025-06-01 10:00", receivedEnd.get());
    assertTrue(receivedCollision.get());
  }
}

