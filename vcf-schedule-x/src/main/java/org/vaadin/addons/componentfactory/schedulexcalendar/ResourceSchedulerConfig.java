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

import com.vaadin.flow.internal.Pair;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Resource;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;

/**
 * Java representation of the configuration options for the {@link ScheduleXResourceScheduler
 * Schedule-X Resource Scheduler}. This configuration is used to customize the behavior and
 * appearance of resource scheduler.
 * 
 * @see <a href=
 *      "https://schedule-x.dev/docs/calendar/resource-scheduler#resourceschedulerconfig">ResourceSchedulerConfig
 *      documentation</a>
 */
@SuppressWarnings("serial")
public class ResourceSchedulerConfig extends BaseConfiguration implements Serializable {

  /**
   * Width of a column in the hourly view.
   */
  private Integer hourWidth;

  /**
   * Width of a column in the daily view.
   */
  private Integer dayWidth;

  /**
   * List of resources to display.
   */
  private List<Resource> resources = new ArrayList<Resource>();

  /**
   * Height of a resource row.
   */
  private Integer resourceHeight;

  /**
   * Height of an event.
   */
  private Integer eventHeight;

  /**
   * Whether drag and drop should be enabled.
   */
  private boolean dragAndDrop = false;

  /**
   * Whether resizing should be enabled.
   */
  private boolean resize = false;

  /**
   * Whether infinite scroll should be enabled.
   */
  private boolean infiniteScroll = false;

  /**
   * Optionally sets the initially displayed hours in the hourly view.
   */
  private Pair<LocalDateTime, LocalDateTime> initialHours;

  /**
   * Optionally sets the initially displayed days in the daily view.
   */
  private Pair<LocalDate, LocalDate> initialDays;

  public Integer getHourWidth() {
    return hourWidth;
  }

  public void setHourWidth(Integer hourWidth) {
    this.hourWidth = hourWidth;
    this.runRefresh();
  }

  public Integer getDayWidth() {
    return dayWidth;
  }

  public void setDayWidth(Integer dayWidth) {
    this.dayWidth = dayWidth;
    this.runRefresh();
  }

  public List<Resource> getResources() {
    return resources;
  }

  public void setResources(List<Resource> resources) {
    this.resources = resources;
    this.runRefresh();
  }

  public Integer getResourceHeight() {
    return resourceHeight;
  }

  public void setResourceHeight(Integer resourceHeight) {
    this.resourceHeight = resourceHeight;
    this.runRefresh();
  }

  public Integer getEventHeight() {
    return eventHeight;
  }

  public void setEventHeight(Integer eventHeight) {
    this.eventHeight = eventHeight;
    this.runRefresh();
  }

  public boolean isDragAndDrop() {
    return dragAndDrop;
  }

  public void setDragAndDrop(boolean dragAndDrop) {
    this.dragAndDrop = dragAndDrop;
    this.runRefresh();
  }

  public boolean isResize() {
    return resize;
  }

  public void setResize(boolean resize) {
    this.resize = resize;
    this.runRefresh();
  }

  public boolean isInfiniteScroll() {
    return infiniteScroll;
  }

  public void setInfiniteScroll(boolean infiniteScroll) {
    this.infiniteScroll = infiniteScroll;
    this.runRefresh();
  }

  public Pair<LocalDateTime, LocalDateTime> getInitialHours() {
    return initialHours;
  }

  public void setInitialHours(Pair<LocalDateTime, LocalDateTime> initialHours) {
    this.initialHours = initialHours;
    this.runRefresh();
  }

  public Pair<LocalDate, LocalDate> getInitialDays() {
    return initialDays;
  }

  public void setInitialDays(Pair<LocalDate, LocalDate> initialDays) {
    this.initialDays = initialDays;
    this.runRefresh();
  }

  public String getJson() {
    JsonObject js = Json.createObject();
    Optional.ofNullable(hourWidth).ifPresent(value -> js.put("hourWidth", value));
    Optional.ofNullable(dayWidth).ifPresent(value -> js.put("dayWidth", value));
    Optional.ofNullable(resourceHeight).ifPresent(value -> js.put("resourceHeight", value));
    Optional.ofNullable(eventHeight).ifPresent(value -> js.put("eventHeight", value));
    js.put("dragAndDrop", dragAndDrop);
    js.put("resize", resize);
    js.put("infiniteScroll", infiniteScroll);

    if (resources != null && !resources.isEmpty()) {
      JsonArray resArray = Json.createArray();
      for (int i = 0; i < resources.size(); i++) {
        resArray.set(i, Json.parse(resources.get(i).getJson()));
      }
      js.put("resources", resArray);
    }

    if (initialHours != null) {
      js.put("initialHours", initialHours.getFirst().format(DateTimeFormatUtils.DATE_TIME_FORMATTER)
          + "," + initialHours.getSecond().format(DateTimeFormatUtils.DATE_TIME_FORMATTER));
    }

    if (initialDays != null) {
      js.put("initialDays", initialDays.getFirst().format(DateTimeFormatUtils.DATE_FORMATTER) + ","
          + initialDays.getSecond().format(DateTimeFormatUtils.DATE_FORMATTER));
    }

    return js.toJson();
  }

}

