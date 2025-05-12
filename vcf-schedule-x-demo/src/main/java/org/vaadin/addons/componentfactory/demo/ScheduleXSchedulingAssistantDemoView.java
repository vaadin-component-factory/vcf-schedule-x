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
import com.vaadin.flow.demo.Card;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXResourceView;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Resource;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.ResourceSchedulerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.SchedulingAssistantConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar.ColorDefinition;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceView;

/**
 * View for {@link ScheduleXResourceView} demo with {@code SchedulingAssistantConfig}.
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
  private ScheduleXResourceView resourceView;
  private Card resourceViewCard;
  private FieldSet schedulingAssistantLayout;
  private Button scheduleEventDummyButton;
  
  // saving values for possible event creation based on assistant updates
  private String proposedStart;
  private String proposedEnd;
 
  @Override
  protected void createDemo() {
    // begin-source-example
    // source-example-heading: Scheduling Assistant Demo

    // create categories for events
    Calendar work = new Calendar("work");
    work.setLightColors(new ColorDefinition("#f91c45", "#ffd2dc", "#59000d"));
    work.setDarkColors(new ColorDefinition("#ffc0cc", "#a24258", "#ffdee6"));
    Calendar leisure = new Calendar("leisure");
    leisure.setLightColors(new ColorDefinition("#1cf9b0", "#dafff0", "#004d3d"));
    leisure.setDarkColors(new ColorDefinition("#c0fff5", "#42a297", "#e6fff5"));
    calendars = Map.of("work", work, "leisure", leisure);

    // calendar configuration
    configuration = new Configuration();
    configuration.setSelectedDate(LocalDate.of(2024, 05, 06));
    configuration.setDefaultView(ResourceView.HOURLY);

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
    resourceView = getScheduleXResourceView();

    // add listener on event resizing or dnd
    resourceView.addEventUpdateEventListener(e -> {
      String updatedEventId = e.getEventId();
      Optional<Event> optionalEvent =
          events.stream().filter(ev -> ev.getId().equals(updatedEventId)).findFirst();
      optionalEvent.ifPresent(event -> {
        event.setStart(e.getStartDate());
        event.setEnd(e.getEndDate());
      });
    });

    // add scheduling assistant updates listener
    scheduleEventDummyButton = new Button();
    scheduleEventDummyButton.addClickListener(e -> {
      Event scheduledEvent = new Event(UUID.randomUUID().toString(), proposedStart, proposedEnd);
      scheduledEvent.setTitle("Scheduled Event");
      scheduledEvent.setResourceId("conveyor-belt-a-1");
      resourceView.addEvent(scheduledEvent);
      events.add(scheduledEvent);
    });
    resourceView.addSchedulingAssistantUpdateListener(e -> {
      String currentStart = e.getCurrentStart();
      String currentEnd = e.getCurrentEnd();
      boolean hasCollision = e.isHasCollision();

      proposedStart = currentStart;
      proposedEnd = currentEnd;
      
      String buttonLabel = "Available range between " + currentStart + " and " + currentEnd;
      scheduleEventDummyButton.setText(buttonLabel);
      scheduleEventDummyButton.setEnabled(!hasCollision);
    });

    // create demo card containing resource view
    createResourceViewDemoCard();

    // end-source-example

    resourceView.setId("scheduling-assistant-demo");

    addCard("Scheduling Assistant Demo", resourceViewCard);
  }

  // begin-source-example
  // source-example-heading: Additional code used in the demo
  /**
   * Additional code used in the demo
   */

  private void createResourceViewDemoCard() {
    resourceViewCard = new Card();
    schedulingAssistantLayout = getSchedulingEventHandlingLayout();
    resourceViewCard.add(resourceView, schedulingAssistantLayout);
  }

  private ScheduleXResourceView getScheduleXResourceView() {
    return new ScheduleXResourceView(Arrays.asList(ResourceView.HOURLY),
        EventProvider.of(events), configuration, calendars, resourceSchedulerConfig,
        schedulingAssistantConfig);
  }
  
  private FieldSet getSchedulingEventHandlingLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();    
    layout.add(scheduleEventDummyButton);
    return createFieldSetLayout("Scheduling Event Handling testing", layout);
  }
  // end-source-example
}
