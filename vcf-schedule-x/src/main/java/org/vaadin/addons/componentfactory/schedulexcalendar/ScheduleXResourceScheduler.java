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
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventProvider;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.ResourceSchedulerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.SchedulingAssistantConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceViewType;

/**
 * A view for displaying resources (people, rooms, equipment etc.) in a time grid.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/resource-scheduler">Resource Scheduler</a>
 * 
 */
@SuppressWarnings("serial")
@NpmPackage(value = "preact", version = "10.26.4")
@NpmPackage(value = "@preact/signals", version = "2.0.2")
@NpmPackage(value = "@sx-premium/resource-scheduler", version = "3.16.1")
@NpmPackage(value = "@sx-premium/scheduling-assistant", version = "3.16.1")
@JsModule("./src/vcf-schedule-x-resource-scheduler.js")
@CssImport("@sx-premium/resource-scheduler/index.css")
@CssImport("@sx-premium/scheduling-assistant/index.css")
@Getter
public class ScheduleXResourceScheduler extends BaseScheduleXCalendar {

  private ResourceSchedulerConfig resourceSchedulerConfig;

  private SchedulingAssistantConfig schedulingAssistantConfig;

  public ScheduleXResourceScheduler() {
    super();
    setClassName("vcf-schedule-x-resource-scheduler");
  }

  public ScheduleXResourceScheduler(List<ResourceViewType> views, EventProvider eventProvider) {
    super(views, eventProvider);
  }

  public ScheduleXResourceScheduler(List<ResourceViewType> views, EventProvider eventProvider,
      Configuration configuration) {
    super(views, eventProvider, configuration);
  }

  public ScheduleXResourceScheduler(List<ResourceViewType> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars) {
    super(views, eventProvider, configuration, calendars);
  }

  public ScheduleXResourceScheduler(List<ResourceViewType> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars,
      ResourceSchedulerConfig resourceSchedulerConfig) {
    super(views, eventProvider, configuration, calendars);
    this.resourceSchedulerConfig = resourceSchedulerConfig;
  }

  public ScheduleXResourceScheduler(List<ResourceViewType> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars,
      ResourceSchedulerConfig resourceSchedulerConfig,
      SchedulingAssistantConfig schedulingAssistantConfig) {
    this(views, eventProvider, configuration, calendars, resourceSchedulerConfig);
    this.schedulingAssistantConfig = schedulingAssistantConfig;
  }

  @Override
  protected void initCalendar() {
    this.getElement().executeJs("vcfschedulexresourcescheduler.create($0, $1, $2, $3, $4, $5)", this,
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
    return "vcfschedulexresourcescheduler";
  }

  /**
   * Event fired when Scheduling Assistant is updated.
   */
  @Getter
  @DomEvent("scheduling-assistant-update")
  public static class SchedulingAssistantUpdateEvent extends ComponentEvent<ScheduleXResourceScheduler> {

    private final String currentStart;
    private final String currentEnd;
    private final boolean hasCollision;

    public SchedulingAssistantUpdateEvent(ScheduleXResourceScheduler source, boolean fromClient,
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
