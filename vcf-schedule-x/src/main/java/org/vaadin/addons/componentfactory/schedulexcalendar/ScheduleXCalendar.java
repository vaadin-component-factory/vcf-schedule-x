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

import com.vaadin.flow.component.dependency.JsModule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.View;

/**
 * Vaadin Wrapper Add-on for <a href="https://schedule-x.dev/">Schedule-X Calendar</a>.
 *
 */
@SuppressWarnings("serial")
@JsModule("./src/vcf-schedule-x-calendar.js")
public class ScheduleXCalendar extends BaseScheduleXCalendar {

  public ScheduleXCalendar() {
    super();
  }

  public ScheduleXCalendar(List<CalendarView> views, EventProvider eventProvider) {
    super(views, eventProvider);
  }

  public ScheduleXCalendar(List<CalendarView> views, EventProvider eventProvider,
      Configuration configuration) {
    super(views, eventProvider, configuration);
  }

  public ScheduleXCalendar(List<CalendarView> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars) {
    super(views, eventProvider, configuration, calendars);
  }

  @Override
  protected void initCalendar() {
    this.getElement().executeJs("vcfschedulexcalendar.create($0, $1, $2, $3)", this, viewsToJson(),
        configurationToJson(), calendarsToJson());
  }

  @Override
  public void setView(View view, LocalDate selectedDate) {
    this.getElement().executeJs("vcfschedulexcalendar.setView($0, $1, $2);", this, view.getName(),
        selectedDate.format(DATE_FORMATTER));
  }

  @Override
  public void setSelectedDate(LocalDate selectedDate) {
    this.getElement().executeJs("vcfschedulexcalendar.setSelectedDate($0, $1);", this,
        selectedDate.format(DATE_FORMATTER));
  }

  @Override
  public void navigateForwards() {
    this.getElement().executeJs("vcfschedulexcalendar.navigateForwards($0)", this);
  }

  @Override
  public void navigateBackwards() {
    this.getElement().executeJs("vcfschedulexcalendar.navigateBackwards($0)", this);   
  }

  @Override
  public void addEvent(Event event) {
    this.getElement().executeJs("vcfschedulexcalendar.addEvent($0, $1);", this, event.getJson());
  }

  @Override
  public void removeEvent(String eventId) {
    this.getElement().executeJs("vcfschedulexcalendar.removeEvent($0, $1);", this, eventId);
  }
  
  @Override
  public void updateEvent(Event event) {
    this.getElement().executeJs("vcfschedulexcalendar.updateEvent($0, $1);", this, event.getJson());
  } 
  
  /**
   * Scroll to an specific time.
   * Only available for week and day views.
   * 
   * @param time the time to scroll to in the view
   */
  public void scrollTo(LocalTime time) {
    this.getElement().executeJs("vcfschedulexcalendar.scrollTo($0, $1);", this, time.format(TIME_FORMATTER));
  }
}
