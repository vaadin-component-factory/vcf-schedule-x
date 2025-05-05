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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.demo.Card;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  private List<Event> events;
  private List<Resource> resources;
  private Configuration configuration;
  private Map<String, Calendar> calendars;
  private ResourceSchedulerConfig resourceSchedulerConfig;
  private ScheduleXResourceView resourceView;
  private CalendarHeaderComponent header;
  private Card resourceViewCard;
  private HorizontalLayout resourcesLayout;

  @Override
  public void initView() {
    this.getStyle().set("max-width", "1500px");
    createBasicDemo();

    addCard("Additional code used in the demo", new Span("These methods are used in the demo."));
  }

  private void createBasicDemo() {
    // begin-source-example
    // source-example-heading: Basic Use Demo

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
    resourceSchedulerConfig.setResize(true);
    resourceSchedulerConfig.setDragAndDrop(true);

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

    // create header component
    header = new CalendarHeaderComponent(resourceView);

    // add event click listener
    resourceView.addCalendarEventClickEventListener(
        e -> Notification.show("Event with id " + e.getEventId() + " clicked"));

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

    // create demo card
    createResourceViewDemoCard();

    // end-source-example

    resourceView.setId("basic-use-demo");

    addCard("Basic Use Demo", resourceViewCard);
  }

  // begin-source-example
  // source-example-heading: Additional code used in the demo
  /**
   * Additional code used in the demo
   */

  private void createResourceViewDemoCard() {
    resourceViewCard = new Card();
    resourcesLayout = getResourceHandlingLayout();
    resourceViewCard.add(header, resourceView, resourcesLayout);
  }

  private ScheduleXResourceView getScheduleXResourceView() {
    return new ScheduleXResourceView(Arrays.asList(ResourceView.HOURLY, ResourceView.DAILY),
        EventProvider.of(events), configuration, calendars, resourceSchedulerConfig);
  }

  private HorizontalLayout getResourceHandlingLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();

    // nested resource to be added as child of 'Conveyor Belt A'
    Resource resource1_3 = new Resource("test-resource");
    resource1_3.setLabel("Test Resource");
    resource1_3.setColorName("test-resource");
    ColorDefinition lightColorResource1_3 = new ColorDefinition("#f9d71c", "#fff5aa", "#594800");
    resource1_3.setLightColors(lightColorResource1_3);

    // new resource
    Resource resource3 = new Resource("test-resource-2");
    resource3.setLabel("Test Resource 2");
    resource3.setColorName("test-resource-2");
    ColorDefinition lightColorResource3 = new ColorDefinition("#2f5c56", "#a5e9e0", "#59000d");
    resource3.setLightColors(lightColorResource3);

    Button addNestedResourceButton = new Button("Click to add resource to 'Conveyor Belt A'");
    Button addNewResourceButton = new Button("Click to add new resource");

    addNestedResourceButton.addClickListener(e -> {
      for (Resource resource : resources) {
        if (resource.getId().equals("conveyor-belt-a")) {
          List<Resource> resourcesUpdated = new ArrayList<Resource>(resource.getResources());
          resourcesUpdated.add(resource1_3);
          resource.setResources(resourcesUpdated);
          break;
        }
      }
      refreshViewOnResourceUpdate();
    });
    addNestedResourceButton.setDisableOnClick(true);

    addNewResourceButton.addClickListener(e -> {
      resources.add(resource3);
      refreshViewOnResourceUpdate();
    });
    addNewResourceButton.setDisableOnClick(true);

    layout.add(addNestedResourceButton, addNewResourceButton);
    return layout;
  }

  private void refreshViewOnResourceUpdate() {
    resourceSchedulerConfig.setResources(resources);
    resourceViewCard.removeAll();
    resourceView = getScheduleXResourceView();
    header = new CalendarHeaderComponent(resourceView);
    resourceViewCard.add(header, resourceView, resourcesLayout);
  }

  // end-source-example

}
