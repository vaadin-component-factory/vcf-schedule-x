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
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Resource;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceViewType;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventQueryFilter;

class ScheduleXResourceSchedulerTest {

  private ScheduleXResourceScheduler createView(ResourceSchedulerConfig schedulerConfig,
      SchedulingAssistantConfig assistantConfig) {
    List<ResourceViewType> views = List.of(ResourceViewType.HOURLY);
    CallbackDataProvider<Event, EventQueryFilter> dataProvider = new CallbackDataProvider<>(
        query -> Collections.<Event>emptyList().stream(),
        query -> 0
    );
    Configuration configuration = new Configuration();
    configuration.setSelectedDate(LocalDate.of(2024, 05, 06));
    configuration.setDefaultView(ResourceViewType.HOURLY);
    return new ScheduleXResourceScheduler(views, dataProvider, configuration, null, schedulerConfig,
        assistantConfig);
  }

  @Test
  void testConstructorSetsConfigsCorrectly() {
    ResourceSchedulerConfig schedulerConfig = new ResourceSchedulerConfig();
    schedulerConfig.setEventHeight(30);
    schedulerConfig.setResources(List.of(new Resource("rA")));

    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 1, 11, 0);
    SchedulingAssistantConfig assistantConfig = new SchedulingAssistantConfig(start, end);

    ScheduleXResourceScheduler view = createView(schedulerConfig, assistantConfig);

    assertEquals(30, view.getResourceSchedulerConfig().getEventHeight());
    assertEquals("rA", view.getResourceSchedulerConfig().getResources().get(0).getId());
    assertEquals(start, view.getSchedulingAssistantConfig().getInitialStart());
    assertEquals(end, view.getSchedulingAssistantConfig().getInitialEnd());
  }

}
