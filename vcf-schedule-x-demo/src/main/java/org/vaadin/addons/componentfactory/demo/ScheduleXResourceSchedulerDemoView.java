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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.demo.Card;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
 * View for {@link ScheduleXResourceScheduler} demo.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Route(value = "resource", layout = DemoMainLayout.class)
public class ScheduleXResourceSchedulerDemoView extends ScheduleXBaseDemoView {

  private List<Event> events;
  private List<Resource> resources;
  private Configuration configuration;
  private Map<String, Calendar> calendars;
  private ResourceSchedulerConfig resourceSchedulerConfig;
  private ScheduleXResourceScheduler resourceScheduler;
  private CalendarHeaderComponent header;
  private Card resourceSchedulerCard;
  private FieldSet resourcesLayout;
  private FieldSet localeTestingLayout;
  private FieldSet infiniteScrollTestingLayout;
  private FieldSet scheduleAssistantTestingLayout;

  @Override
  protected void createDemo() {
    // begin-source-example
    // source-example-heading: Resource Scheduler Demo

    // calendar configuration
    configuration = new Configuration();
    configuration.setSelectedDate(LocalDate.of(2024, 05, 06));
    configuration.setDefaultView(ResourceViewType.HOURLY);
    configuration.setLocale(Locale.GERMANY);

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
    Event event4 = new Event("3", LocalDateTime.of(eventsDate.plusDays(10), LocalTime.of(10, 00)),
        LocalDateTime.of(eventsDate.plusDays(10), LocalTime.of(14, 00)));
    event4.setTitle("Adam");
    event4.setCalendarId("work");
    event4.setResourceId("conveyor-belt-a-2");

    events = new ArrayList<Event>();
    events.addAll(Arrays.asList(event1, event2, event3, event4));

    // create resource view
    resourceScheduler = getScheduleXResourceScheduler();

    // create header component
    header = new CalendarHeaderComponent(resourceScheduler);

    // add event click listener
    resourceScheduler.addCalendarEventClickEventListener(
        e -> Notification.show("Event with id " + e.getEventId() + " clicked"));

    // add listener on event resizing or dnd
    resourceScheduler.addEventUpdateEventListener(e -> {
      String updatedEventId = e.getEventId();
      Optional<Event> optionalEvent =
          events.stream().filter(ev -> ev.getId().equals(updatedEventId)).findFirst();
      optionalEvent.ifPresent(event -> {
        event.setStart(e.getStartDate());
        event.setEnd(e.getEndDate());
      });
    });

    // create demo card containing resource view
    createResourceSchedulerDemoCard();

    // end-source-example

    resourceScheduler.setId("resource-scheduler-demo");

    addCard("Resource Scheduler Demo", resourceSchedulerCard);
  }

  // begin-source-example
  // source-example-heading: Additional code used in the demo
  /**
   * Additional code used in the demo
   */

  private void createResourceSchedulerDemoCard() {
    resourceSchedulerCard = new Card();
    resourcesLayout = getResourceHandlingLayout();
    localeTestingLayout = getLocaleTestingLayout();
    infiniteScrollTestingLayout = getInfiniteScrollTestingLayout();
    scheduleAssistantTestingLayout = getSchedulingAssistantTestingLayout();
    resourceSchedulerCard.add(header, resourceScheduler, resourcesLayout, localeTestingLayout,
        infiniteScrollTestingLayout, scheduleAssistantTestingLayout);
  }

  private ScheduleXResourceScheduler getScheduleXResourceScheduler() {
    CallbackDataProvider<Event, EventQueryFilter> dataProvider =
        new CallbackDataProvider<>(query -> events.stream(), query -> events.size());
    return new ScheduleXResourceScheduler(
        Arrays.asList(ResourceViewType.HOURLY, ResourceViewType.DAILY), dataProvider, configuration,
        calendars, resourceSchedulerConfig);
  }

  private FieldSet getResourceHandlingLayout() {
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
    return createFieldSetLayout("Resource Handling testing", layout);
  }

  private void refreshViewOnResourceUpdate() {
    resourceSchedulerConfig.setResources(resources);
    resourceSchedulerCard.removeAll();
    resourceScheduler = getScheduleXResourceScheduler();
    header = new CalendarHeaderComponent(resourceScheduler);
    resourceSchedulerCard.add(header, resourceScheduler, resourcesLayout, localeTestingLayout,
        infiniteScrollTestingLayout, scheduleAssistantTestingLayout);
  }

  private FieldSet getLocaleTestingLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();

    Button setGermany = new Button("Back to Germany Locale");
    setGermany.setDisableOnClick(true);
    setGermany.setEnabled(false);

    Button setSpanish = new Button("Change to Spanish Locale");
    setSpanish.setDisableOnClick(true);

    setSpanish.addClickListener(e -> {
      resourceScheduler.setLocale(Locale.forLanguageTag("es-ES"));
      setGermany.setEnabled(true);
    });

    setGermany.addClickListener(e -> {
      resourceScheduler.setLocale(Locale.GERMANY);
      setSpanish.setEnabled(true);
    });

    layout.add(setSpanish, setGermany);
    VerticalLayout helperLayout =
        createLayoutWithHelperText("Set locale using Calendar Controls", layout);
    return createFieldSetLayout("Calendar Locale testing", helperLayout);
  }

  private FieldSet getInfiniteScrollTestingLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();

    Span notification = new Span("Infinite Scroll DISABLED");
    notification.getElement().getStyle().set("font-weight", "bold");

    Checkbox enableInfiniteScrolling = new Checkbox(false);
    enableInfiniteScrolling.setLabel("Check to enable/disable infinite scroll");
    enableInfiniteScrolling.addValueChangeListener(e -> {
      boolean enabled = e.getValue();
      resourceSchedulerConfig.setInfiniteScroll(enabled);
      String notificationText =
          String.format("Infinite Scroll %s", enabled ? "ENABLED" : "DISABLED");
      notification.setText(notificationText);
    });

    layout.add(enableInfiniteScrolling, notification);
    return createFieldSetLayout("Infinite Scroll testing", layout);
  }

  private String proposedStart;
  private String proposedEnd;

  private FieldSet getSchedulingAssistantTestingLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setWidthFull();

    Span notification = new Span();
    notification.setVisible(false);

    Button scheduleEventButton = new Button("Add event on proposed time");
    scheduleEventButton.setVisible(false);
    scheduleEventButton.addClickListener(e -> {
      Event scheduledEvent = new Event(UUID.randomUUID().toString(), proposedStart, proposedEnd);
      scheduledEvent.setTitle("Scheduled Event");
      scheduledEvent.setResourceId("conveyor-belt-a-1");
      resourceScheduler.addEvent(scheduledEvent);
      events.add(scheduledEvent);
    });

    Checkbox addAssistant = new Checkbox("Show Scheduler Assistant", e -> {
      scheduleEventButton.setVisible(e.getValue());
      if (e.getValue()) {
        LocalDate configDate = resourceScheduler.getDate(); // current selected date
        LocalDateTime dateStart = LocalDateTime.of(configDate, LocalTime.of(10, 00));
        LocalDateTime dateEnd = LocalDateTime.of(configDate, LocalTime.of(12, 00));
        SchedulingAssistantConfig schedulingAssistantConfig =
            new SchedulingAssistantConfig(dateStart, dateEnd);

        resourceScheduler.addSchedulingAssistantUpdateListener(ev -> {
          String currentStart = ev.getCurrentStart();
          String currentEnd = ev.getCurrentEnd();
          boolean hasCollision = ev.isHasCollision();

          proposedStart = currentStart;
          proposedEnd = currentEnd;

          notification.setVisible(!hasCollision);
          notification.setText("Available range between " + currentStart + " and " + currentEnd);
          scheduleEventButton.setEnabled(!hasCollision);
        });
        resourceScheduler.setSchedulingAssistantConfig(schedulingAssistantConfig);
      } else {
        notification.setVisible(false);
        resourceScheduler.setSchedulingAssistantConfig(null);
      }

    });

    layout.add(addAssistant, scheduleEventButton, notification);
    layout.setAlignItems(Alignment.BASELINE);
    return createFieldSetLayout("Schedule Assistant testing", layout);
  }

  // end-source-example
}
