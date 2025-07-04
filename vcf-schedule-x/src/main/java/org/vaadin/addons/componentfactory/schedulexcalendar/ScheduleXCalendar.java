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

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.EventQueryFilter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarViewType;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonValue;

/**
 * Vaadin Wrapper Add-on for <a href="https://schedule-x.dev/">Schedule-X Calendar</a>.
 *
 */
@SuppressWarnings("serial")
@NpmPackage(value = "@sx-premium/draw", version = "3.17.0")
@JsModule("./src/vcf-schedule-x-calendar.js")
public class ScheduleXCalendar extends BaseScheduleXCalendar {

  private DrawnEventValidationCallback drawnEventValidationCallback;

  public ScheduleXCalendar(List<CalendarViewType> views, CallbackDataProvider<Event, EventQueryFilter> dataProvider,
      Configuration configuration) {
    super(views, dataProvider, configuration);
  }

  public ScheduleXCalendar(List<CalendarViewType> views, CallbackDataProvider<Event, EventQueryFilter> dataProvider,
      Configuration configuration, Map<String, Calendar> calendars) {
    super(views, dataProvider, configuration, calendars);
  }

  @Override
  protected String getJsConnector() {
    return "vcfschedulexcalendar";
  }

  @Override
  protected void initCalendar(boolean refreshView) {
    if (!refreshView) {
      this.getElement().executeJs(getJsConnector() + ".create($0, $1, $2, $3)", this.container,
          viewsToJson(), configurationToJson(), calendarsToJson());
    } else {
      this.getElement().executeJs(getJsConnector() + ".create($0, $1, $2, $3, $4)", this.container,
          viewsToJson(), configurationToJson(), calendarsToJson(), this.getView().getName());
    }
  }

  @ClientCallable
  void addEvent(JsonValue jsonValue) {
    Event event = new Event(jsonValue);
    this.fireEvent(new CalendarEventDrawnEvent(this, true, event));
  }

  /**
   * Scroll to an specific time. Only available for week and day views.
   * 
   * @param time the time to scroll to in the view
   */
  public void scrollTo(LocalTime time) {
    this.getElement().executeJs("vcfschedulexcalendar.scrollTo($0, $1);", this.container,
        time.format(DateTimeFormatUtils.TIME_FORMATTER));
  }

  /**
   * Sets the drawn event validation callback.
   * 
   * @param drawnEventValidationCallback
   */
  public void setDrawnEventValidationCallback(
      DrawnEventValidationCallback drawnEventValidationCallback) {
    this.drawnEventValidationCallback = drawnEventValidationCallback;
  }

  /**
   * Validates drawn event by calling drawnEventValidationCallback.
   * 
   * @param eventId the id of the updated event
   * @param start the new start date of the updated event
   * @param end the new end date of the updated event
   */
  @ClientCallable
  private boolean validateDrawnEvent(String eventId, String start, String end) {
    if (drawnEventValidationCallback != null) {
      return drawnEventValidationCallback.apply(new Event(eventId, start, end));
    }
    return true;
  }

  /**
   * Callback interface for validating drawn events.
   */
  @FunctionalInterface
  public static interface DrawnEventValidationCallback
      extends SerializableFunction<Event, Boolean> {
  }

  public static class CalendarEventDrawnEvent extends ComponentEvent<BaseScheduleXCalendar> {

    private final Event event;

    public CalendarEventDrawnEvent(BaseScheduleXCalendar source, boolean fromClient, Event event) {
      super(source, fromClient);
      this.event = event;
    }

    public Event getEvent() {
      return event;
    }
  }

  public Registration addCalendarEventDrawnEventListener(
      ComponentEventListener<CalendarEventDrawnEvent> listener) {
    return addListener(CalendarEventDrawnEvent.class, listener);
  }

}
