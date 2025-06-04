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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.demo.Card;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.ResourceSchedulerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXResourceScheduler;
import org.vaadin.addons.componentfactory.schedulexcalendar.SchedulingAssistantConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar.ColorDefinition;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventQueryFilter;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Resource;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceViewType;

/**
 * View for {@link ScheduleXResourceScheduler} demo with {@code SchedulingAssistantConfig}.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Route(value = "scheduling", layout = DemoMainLayout.class)
public class ScheduleXSchedulingAssistantDemoView extends ScheduleXBaseDemoView {

  private List<Event> events;
  private List<Resource> resources;
  private Configuration configuration;
  private Map<String, Calendar> calendars;
  private ResourceSchedulerConfig resourceSchedulerConfig;
  private SchedulingAssistantConfig schedulingAssistantConfig;
  private ScheduleXResourceScheduler resourceScheduler;
  private Card resourceSchedulerCard;
  private FieldSet schedulingAssistantLayout;
  private Button scheduleEventButton;
  
  // saving values for possible event creation based on assistant updates
  private String proposedStart;
  private String proposedEnd;
 
  @Override
  protected void createDemo() {
    // begin-source-example
    // source-example-heading: Scheduling Assistant Demo
    
    // calendar configuration
    configuration = new Configuration();
    configuration.setSelectedDate(LocalDate.of(2024, 05, 06));
    configuration.setDefaultView(ResourceViewType.HOURLY);

    // create resources
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

    resources = new ArrayList<Resource>();
    resources.addAll(Arrays.asList(resource1, resource2));

    // resource scheduler configuration
    resourceSchedulerConfig = new ResourceSchedulerConfig();
    resourceSchedulerConfig.setResources(resources);

    // scheduling assistant configuration
    LocalDate configDate = LocalDate.of(2024, 05, 06);
    LocalDateTime dateStart = LocalDateTime.of(configDate, LocalTime.of(10, 00));
    LocalDateTime dateEnd = LocalDateTime.of(configDate, LocalTime.of(12, 00));
    schedulingAssistantConfig = new SchedulingAssistantConfig(dateStart, dateEnd);

    // create categories for events
    calendars = getCalendars();
       
    // create events
    LocalDate eventsDate = LocalDate.of(2024, 05, 06);
    Event event1 = new Event("1", LocalDateTime.of(eventsDate, LocalTime.of(02, 00)),
        LocalDateTime.of(eventsDate, LocalTime.of(07, 55)));
    event1.setTitle("Tom");
    event1.setCalendarId("leisure");
    event1.setResourceId("conveyor-belt-b");
    Event event2 = new Event("2", LocalDateTime.of(eventsDate, LocalTime.of(8, 00)),
        LocalDateTime.of(eventsDate, LocalTime.of(14, 00)));
    event2.setTitle("Marsha");
    event2.setCalendarId("work");
    event2.setResourceId("conveyor-belt-a-1");
    Event event3 = new Event("3", LocalDateTime.of(eventsDate, LocalTime.of(8, 00)),
        LocalDateTime.of(eventsDate, LocalTime.of(14, 00)));
    event3.setTitle("Jane");
    event3.setCalendarId("work");
    event3.setResourceId("conveyor-belt-a-2");

    events = new ArrayList<Event>();
    events.addAll(Arrays.asList(event1, event2, event3));

    // create resource view
    resourceScheduler = getScheduleXResourceScheduler();

    // add scheduling assistant updates listener
    scheduleEventButton = new Button();
    scheduleEventButton.addClickListener(e -> {
      Event scheduledEvent = new Event(UUID.randomUUID().toString(), proposedStart, proposedEnd);
      scheduledEvent.setTitle("Scheduled Event");
      scheduledEvent.setResourceId("conveyor-belt-a-1");
      resourceScheduler.addEvent(scheduledEvent);
      events.add(scheduledEvent);
    });
    resourceScheduler.addSchedulingAssistantUpdateListener(e -> {
      String currentStart = e.getCurrentStart();
      String currentEnd = e.getCurrentEnd();
      boolean hasCollision = e.isHasCollision();

      proposedStart = currentStart;
      proposedEnd = currentEnd;
      
      String buttonLabel = "Available range between " + currentStart + " and " + currentEnd;
      scheduleEventButton.setText(buttonLabel);
      scheduleEventButton.setEnabled(!hasCollision);
    });

    // create demo card containing resource view
    createResourceSchedulerDemoCard();

    // end-source-example

    resourceScheduler.setId("scheduling-assistant-demo");

    addCard("Scheduling Assistant Demo", resourceSchedulerCard);
  }

  // begin-source-example
  // source-example-heading: Additional code used in the demo
  /**
   * Additional code used in the demo
   */

  private void createResourceSchedulerDemoCard() {
    resourceSchedulerCard = new Card();
    schedulingAssistantLayout = getSchedulingEventHandlingLayout();
    resourceSchedulerCard.add(resourceScheduler, schedulingAssistantLayout);
  }

  private ScheduleXResourceScheduler getScheduleXResourceScheduler() {
    CallbackDataProvider<Event, EventQueryFilter> dataProvider = new CallbackDataProvider<>(
        query -> events.stream(),
        query -> events.size()
    );
    return new ScheduleXResourceScheduler(Arrays.asList(ResourceViewType.HOURLY),
        dataProvider, configuration, calendars, resourceSchedulerConfig,
        schedulingAssistantConfig);
  }
  
  private FieldSet getSchedulingEventHandlingLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();    
    layout.add(scheduleEventButton);
    return createFieldSetLayout("Scheduling Event Handling testing", layout);
  }
  // end-source-example
}
