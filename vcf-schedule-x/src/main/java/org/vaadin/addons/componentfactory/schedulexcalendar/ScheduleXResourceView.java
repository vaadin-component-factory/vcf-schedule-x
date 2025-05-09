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

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceSchedulerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.SchedulingAssistantConfig;

/**
 * A view for displaying resources (people, rooms, equipment etc.) in a time grid.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/resource-scheduler">Resource Scheduler</a>
 * 
 */
@SuppressWarnings("serial")
@NpmPackage(value = "preact", version = "10.26.4")
@NpmPackage(value = "@preact/signals", version = "2.0.2")
@NpmPackage(value = "@sx-premium/resource-scheduler", version = "3.15.0")
@NpmPackage(value = "@sx-premium/scheduling-assistant", version = "3.15.0")
@JsModule("./src/vcf-schedule-x-resource-view.js")
@CssImport("@sx-premium/resource-scheduler/index.css")
@CssImport("@sx-premium/scheduling-assistant/index.css")
@Setter
@Getter
public class ScheduleXResourceView extends BaseScheduleXCalendar {

  private ResourceSchedulerConfig resourceSchedulerConfig;

  private SchedulingAssistantConfig schedulingAssistantConfig;

  public ScheduleXResourceView() {
    super();
    setClassName("vcf-schedule-x-resource-view");
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider) {
    super(views, eventProvider);
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider,
      Configuration configuration) {
    super(views, eventProvider, configuration);
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars) {
    super(views, eventProvider, configuration, calendars);
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars,
      ResourceSchedulerConfig resourceSchedulerConfig) {
    super(views, eventProvider, configuration, calendars);
    this.resourceSchedulerConfig = resourceSchedulerConfig;
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars,
      ResourceSchedulerConfig resourceSchedulerConfig,
      SchedulingAssistantConfig schedulingAssistantConfig) {
    this(views, eventProvider, configuration, calendars, resourceSchedulerConfig);
    this.schedulingAssistantConfig = schedulingAssistantConfig;
  }

  @Override
  protected void initCalendar() {
    this.getElement().executeJs("vcfschedulexresourceview.create($0, $1, $2, $3, $4, $5)", this,
        viewsToJson(), configurationToJson(), calendarsToJson(), resourceSchedulerConfigToJson(),
        schedulingAssistantConfigToJson());
  }

  protected String resourceSchedulerConfigToJson() {
    return resourceSchedulerConfig != null ? resourceSchedulerConfig.getJson() : "{}";
  }

  protected String schedulingAssistantConfigToJson() {
    return schedulingAssistantConfig != null ? schedulingAssistantConfig.getJson() : "{}";
  }

  @Override
  protected String getJsConnector() {
    return "vcfschedulexresourceview";
  }

  @Override
  public void navigateForwards() {
    this.getElement().executeJs("vcfschedulexresourceview.navigateForwards($0)", this);
  }

  @Override
  public void navigateBackwards() {
    this.getElement().executeJs("vcfschedulexresourceview.navigateBackwards($0)", this);
  }

  @Override
  public void addEvent(Event event) {
    this.getElement().executeJs("vcfschedulexresourceview.addEvent($0, $1);", this,
        event.getJson());
  }

  @Override
  public void removeEvent(String eventId) {
    this.getElement().executeJs("vcfschedulexresourceview.removeEvent($0, $1);", this, eventId);
  }

  @Override
  public void updateEvent(Event event) {
    this.getElement().executeJs("vcfschedulexresourceview.updateEvent($0, $1);", this,
        event.getJson());
  }

  /**
   * Event fired when Scheduling Assistant is updated.
   */
  @Getter
  @DomEvent("scheduling-assistant-update")
  public static class SchedulingAssistantUpdateEvent extends ComponentEvent<ScheduleXResourceView> {

    private final String currentStart;
    private final String currentEnd;
    private final boolean hasCollision;

    public SchedulingAssistantUpdateEvent(ScheduleXResourceView source, boolean fromClient,
        @EventData("event.detail.currentStart") String currentStart,
        @EventData("event.detail.currentEnd") String currentEnd,
        @EventData("event.detail.hasCollision") boolean hasCollision) {
      super(source, fromClient);
      this.currentStart = currentStart;
      this.currentEnd = currentEnd;
      this.hasCollision = hasCollision;
    }
  }

  /**
   * Adds a SchedulingAssistantUpdateEvent listener.
   * 
   * @param listener the listener to be added
   * @return a handle that can be used for removing the listener
   */
  public Registration addSchedulingAssistantUpdateListener(
      ComponentEventListener<SchedulingAssistantUpdateEvent> listener) {
    return addListener(SchedulingAssistantUpdateEvent.class, listener);
  }

}
