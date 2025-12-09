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

import java.util.List;
import java.util.Map;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventQueryFilter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceViewType;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.shared.Registration;

/**
 * A view for displaying resources (people, rooms, equipment etc.) in a time grid.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/resource-scheduler">Resource Scheduler</a>
 * 
 */
@SuppressWarnings("serial")
@NpmPackage(value = "preact", version = "10.26.4")
@NpmPackage(value = "@preact/signals", version = "2.0.2")
@NpmPackage(value = "@sx-premium/resource-scheduler", version = "14.0.0")
@NpmPackage(value = "@sx-premium/scheduling-assistant", version = "14.0.0")
@JsModule("./src/vcf-schedule-x-resource-scheduler.js")
public class ScheduleXResourceScheduler extends BaseScheduleXCalendar {

  private ResourceSchedulerConfig resourceSchedulerConfig;

  private SchedulingAssistantConfig schedulingAssistantConfig;

  public ScheduleXResourceScheduler(List<ResourceViewType> views,
      CallbackDataProvider<Event, EventQueryFilter> dataProvider, Configuration configuration,
      ResourceSchedulerConfig resourceSchedulerConfig) {
    super(views, dataProvider, configuration);
    this.resourceSchedulerConfig = resourceSchedulerConfig;
    this.resourceSchedulerConfig.setCalendar(this);
  }

  public ScheduleXResourceScheduler(List<ResourceViewType> views,
      CallbackDataProvider<Event, EventQueryFilter> dataProvider, Configuration configuration,
      Map<String, Calendar> calendars, ResourceSchedulerConfig resourceSchedulerConfig) {
    super(views, dataProvider, configuration, calendars);
    this.resourceSchedulerConfig = resourceSchedulerConfig;
    this.resourceSchedulerConfig.setCalendar(this);
  }

  public ScheduleXResourceScheduler(List<ResourceViewType> views,
      CallbackDataProvider<Event, EventQueryFilter> dataProvider, Configuration configuration,
      Map<String, Calendar> calendars, ResourceSchedulerConfig resourceSchedulerConfig,
      SchedulingAssistantConfig schedulingAssistantConfig) {
    this(views, dataProvider, configuration, calendars, resourceSchedulerConfig);
    
    if (ResourceViewType.DAILY.equals(this.getView())
        && schedulingAssistantConfig != null) {
      throw new IllegalArgumentException(
          "Scheduling Assistant is not supported with ResourceViewType.DAILY. "
              + "Use an ResourceViewType.HOURLY view instead.");
    }
   
    this.schedulingAssistantConfig = schedulingAssistantConfig;
    this.schedulingAssistantConfig.setCalendar(this);
  }

  @Override
  protected String getJsConnector() {
    return "vcfschedulexresourcescheduler";
  }

  @Override
  protected void initCalendar(boolean refreshView) {
    if (!refreshView) {
      this.getElement().executeJs(getJsConnector() + ".create($0, $1, $2, $3, $4, $5)",
          this.container, viewsToJson(), configurationToJson(), calendarsToJson(),
          resourceSchedulerConfigToJson(), schedulingAssistantConfigToJson());
    } else {
      this.getElement().executeJs(getJsConnector() + ".create($0, $1, $2, $3, $4, $5, $6)",
          this.container, viewsToJson(), configurationToJson(), calendarsToJson(),
          resourceSchedulerConfigToJson(), schedulingAssistantConfigToJson(),
          this.getView().getName());
    }
  }

  protected String resourceSchedulerConfigToJson() {
    return resourceSchedulerConfig != null ? resourceSchedulerConfig.getJson() : "{}";
  }

  protected String schedulingAssistantConfigToJson() {
    return schedulingAssistantConfig != null ? schedulingAssistantConfig.getJson() : "{}";
  }

  public ResourceSchedulerConfig getResourceSchedulerConfig() {
    return resourceSchedulerConfig;
  }

  public SchedulingAssistantConfig getSchedulingAssistantConfig() {
    return schedulingAssistantConfig;
  }

  public void setSchedulingAssistantConfig(SchedulingAssistantConfig schedulingAssistantConfig) {
    this.schedulingAssistantConfig = schedulingAssistantConfig;
    if (this.schedulingAssistantConfig != null) {
      this.schedulingAssistantConfig.setCalendar(this);
    }
    this.refreshCalendar();
  }

  /**
   * Event fired when Scheduling Assistant is updated.
   */
  @DomEvent("scheduling-assistant-update")
  public static class SchedulingAssistantUpdateEvent
      extends ComponentEvent<ScheduleXResourceScheduler> {

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

    public String getCurrentStart() {
      return DateTimeFormatUtils.formatZonedDateTime(currentStart);
    }

    public String getCurrentEnd() {
      return DateTimeFormatUtils.formatZonedDateTime(currentEnd);
    }

    public boolean isHasCollision() {
      return hasCollision;
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
