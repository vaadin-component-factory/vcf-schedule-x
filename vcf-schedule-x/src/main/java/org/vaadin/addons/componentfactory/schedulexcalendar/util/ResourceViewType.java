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
package org.vaadin.addons.componentfactory.schedulexcalendar.util;

import org.vaadin.addons.componentfactory.schedulexcalendar.ScheduleXResourceScheduler;

/**
 * Enum representing the supported view types for {@link ScheduleXResourceScheduler}
 */
public enum ResourceViewType implements ViewType {

  HOURLY("createHourlyView", "hourly"), DAILY("createDailyView", "daily");

  private String name;

  private String viewName;

  private ResourceViewType(String name, String viewName) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getViewName() {
    return viewName;
  }

  public static ResourceViewType fromViewName(String viewName) {
    for (ResourceViewType type : values()) {
      if (type.getViewName().equalsIgnoreCase(viewName)) {
        return type;
      }
    }
    return null;
  }

}
