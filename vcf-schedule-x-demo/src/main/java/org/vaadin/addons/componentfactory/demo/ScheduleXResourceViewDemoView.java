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

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import org.vaadin.addons.componentfactory.schedulexcalendar.EventProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXResourceView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar.ColorDefinition;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Resource;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceSchedulerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceView;

/**
 * View for {@link ScheduleXResourceView} demo.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Route("resource")
public class ScheduleXResourceViewDemoView extends DemoView {

  @Override
  public void initView() {
    this.getStyle().set("max-width", "1500px");
    createBasicDemo();
  }

  private void createBasicDemo() {
    // begin-source-example
    // source-example-heading: Basic Use Demo

    Calendar work = new Calendar("work");
    work.setLightColors(new ColorDefinition("#f91c45", "#ffd2dc", "#59000d"));
    work.setDarkColors(new ColorDefinition("#ffc0cc", "#a24258", "#ffdee6"));
    Calendar leisure = new Calendar("leisure");
    leisure.setLightColors(new ColorDefinition("#1cf9b0", "#dafff0", "#004d3d"));
    leisure.setDarkColors(new ColorDefinition("#c0fff5", "#42a297", "#e6fff5"));
    Map<String, Calendar> calendars = Map.of("work", work, "leisure", leisure);

    Configuration configuration = new Configuration();
    configuration.setSelectedDate(LocalDate.of(2024, 05, 06));
    configuration.setDefaultView(ResourceView.HOURLY);

    Resource resource1 = new Resource("conveyor-belt-a");
    resource1.setLabel("Conveyor Belt A");
    Resource resource1_1 = new Resource("conveyor-belt-a-1");
    resource1_1.setLabel("Conveyor Belt A 1");
    resource1_1.setColorName("belt-a-1");
    ColorDefinition lightColorResource1_1 = new ColorDefinition("#1cf9b0", "#dafff0", "#004d3d");
    resource1_1.setLightColors(lightColorResource1_1);

    Resource resource1_2 = new Resource("conveyor-belt-a-2");
    resource1_2.setLabel("Conveyor Belt A 2");
    resource1_2.setColorName("belt-a-2");
    ColorDefinition lightColorResource1_2 = new ColorDefinition("#1c7df9", "#d2e7ff", "#002859");
    resource1_2.setLightColors(lightColorResource1_2);

    resource1.setResources(Arrays.asList(resource1_1, resource1_2));

    Resource resource2 = new Resource("conveyor-belt-b");
    resource2.setLabel("Conveyor Belt B");
    resource2.setColorName("belt-b");
    ColorDefinition lightColorResource2 = new ColorDefinition("#1c7df9", "#d2e7ff", "#002859");
    resource2.setLightColors(lightColorResource2);

    ResourceSchedulerConfig resourceSchedulerConfig = new ResourceSchedulerConfig();
    resourceSchedulerConfig.setResources(Arrays.asList(resource1, resource2));

    Event event1 =
        new Event("1", LocalDateTime.of(LocalDate.of(2024, 05, 06), LocalTime.of(02, 00)),
            LocalDateTime.of(LocalDate.of(2024, 05, 06), LocalTime.of(07, 55)));
    event1.setTitle("Tom");
    event1.setCalendarId("leisure");
    event1.setResourceId("conveyor-belt-b");
    Event event2 = new Event("2", LocalDateTime.of(LocalDate.of(2024, 05, 06), LocalTime.of(8, 00)),
        LocalDateTime.of(LocalDate.of(2024, 05, 06), LocalTime.of(14, 00)));
    event2.setTitle("Marsha");
    event2.setCalendarId("work");
    event2.setResourceId("conveyor-belt-a-1");
    Event event3 = new Event("3", LocalDateTime.of(LocalDate.of(2024, 05, 06), LocalTime.of(8, 00)),
        LocalDateTime.of(LocalDate.of(2024, 05, 06), LocalTime.of(14, 00)));
    event3.setTitle("Jane");
    event3.setCalendarId("work");
    event3.setResourceId("conveyor-belt-a-2");

    ScheduleXResourceView resourceView =
        new ScheduleXResourceView(Arrays.asList(ResourceView.HOURLY, ResourceView.DAILY),
            EventProvider.of(Arrays.asList(event1, event2, event3)), configuration, calendars,
            resourceSchedulerConfig);
    
    CalendarHeaderComponent header = new CalendarHeaderComponent(resourceView);
    
    resourceView.addCalendarEventClickEventListener(
        e -> Notification.show("Event with id " + e.getEventId() + " clicked"));

    // end-source-example

    resourceView.setId("basic-use-demo");

    addCard("Basic Use Demo", header, resourceView);
  }

}
