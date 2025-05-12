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

/**
 * Values that can be use to configure the length, in minutes, of the intervals that are used when
 * dragging, resizing or drawing events. Available values are 15, 30 and 60.
 */
public enum TimeInterval {

  MIN_15(15), MIN_30(30), MIN_60(60);

  private int interval;

  private TimeInterval(int interval) {
    this.interval = interval;
  }

  public int getInterval() {
    return interval;
  }

}
