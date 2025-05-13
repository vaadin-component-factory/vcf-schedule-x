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
package org.vaadin.addons.componentfactory.schedulexcalendar.model;

import elemental.json.Json;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.TimeInterval;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.View;

/**
 * Java representation of the configuration options for the Schedule-X Calendar. This configuration
 * is used to customize the behavior and appearance of the calendar views.
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class Configuration implements Serializable {

  /**
   * The preferred view to display when the calendar is first rendered. all views that you import
   * have a "name" property, which helps you identify them. Defaults to the first view in the
   * "views" list.
   */
  private View defaultView;

  /**
   * The default date to display when the calendar is first rendered. Only accepts YYYY-MM-DD
   * format. Defaults to the current date.
   */
  private LocalDate selectedDate;

  /**
   * Set the language. List of supported languages: https://schedule-x.dev/docs/calendar/language
   * Defaults to 'en-US'.
   */
  private String locale;

  /**
   * Set which day is to be considered the starting day of the week. 0 = Sunday, 1 = Monday,
   * (...other days) 6 = Saturday Defaults to 1 (Monday)
   */
  private Integer firstDayOfWeek;

  /**
   * Render the calendar in dark mode. Defaults to false.
   */
  private boolean isDark = false;

  private DayBoundaries dayBoundaries;

  /**
   * The minimum date that can be displayed.
   */
  private LocalDate minDate;

  /**
   * The maximum date that can be displayed.
   */
  private LocalDate maxDate;

  private WeekOptions weekOptions;

  private MonthGridOptions monthGridOptions;

  private DrawOptions drawOptions;

  private ICal iCal;

  /**
   * Display week numbers. Not 100% according to ISO 8601, which considers a week to start on Monday
   * and end on Sunday. Since Schedule-X enables to configure the first day of the week, the week
   * numbers are calculated based on that.
   */
  private boolean showWeekNumbers = false;

  /**
   * Toggle automatic view change when the calendar is resized below a certain width breakpoint.
   * Defaults to true.
   */
  private boolean isResponsive = true;

  /**
   * Skip validating events when initializing the calendar. This can help you gain a bit of
   * performance if you are loading a lot of events, and you are sure that the events are valid.
   */
  private boolean skipValidation = true;

  /**
   * Time interval that can be configured for event resizing.
   * <p>
   * Available values are 15 (default), 30 and 60.
   */
  private TimeInterval resizeInterval;

  /**
   * Time interval that can be configured for drag and drop of an event.
   * <p>
   * Available values are 15 (default), 30 and 60.
   */
  private TimeInterval dragAndDropInterval;

  private CurrentTimeIndicatorConfig currentTimeIndicatorConfig;

  private ScrollControllerConfig scrollControllerConfig;

  public String getJson() {
    JsonObject js = Json.createObject();
    Optional.ofNullable(defaultView).ifPresent(value -> js.put("defaultView", value.getName()));
    Optional.ofNullable(selectedDate).ifPresent(
        value -> js.put("selectedDate", value.format(DateTimeFormatUtils.DATE_FORMATTER)));
    Optional.ofNullable(locale).ifPresent(value -> js.put("locale", value));
    Optional.ofNullable(firstDayOfWeek).ifPresent(value -> js.put("firstDayOfWeek", value));
    js.put("isDark", isDark);
    Optional.ofNullable(dayBoundaries).ifPresent(value -> js.put("dayBoundaries", value.toJson()));
    Optional.ofNullable(minDate)
        .ifPresent(value -> js.put("minDate", value.format(DateTimeFormatUtils.DATE_FORMATTER)));
    Optional.ofNullable(maxDate)
        .ifPresent(value -> js.put("maxDate", value.format(DateTimeFormatUtils.DATE_FORMATTER)));
    Optional.ofNullable(weekOptions).ifPresent(value -> js.put("weekOptions", value.toJson()));
    Optional.ofNullable(drawOptions).ifPresent(value -> js.put("drawOptions", value.toJson()));
    Optional.ofNullable(iCal).ifPresent(value -> js.put("iCal", value.toJson()));
    Optional.ofNullable(monthGridOptions)
        .ifPresent(value -> js.put("monthGridOptions", value.toJson()));
    js.put("showWeekNumbers", showWeekNumbers);
    js.put("isResponsive", isResponsive);
    js.put("skipValidation", skipValidation);
    Optional.ofNullable(resizeInterval)
        .ifPresent(value -> js.put("resizeInterval", value.getInterval()));
    Optional.ofNullable(dragAndDropInterval)
        .ifPresent(value -> js.put("dragAndDropInterval", value.getInterval()));
    Optional.ofNullable(currentTimeIndicatorConfig)
        .ifPresent(value -> js.put("currentTimeIndicatorConfig", value.toJson()));
    Optional.ofNullable(scrollControllerConfig)
        .ifPresent(value -> js.put("scrollControllerConfig", value.toJson()));
    return js.toJson();
  }

  /**
   * Decides which hours should be displayed in the week and day grids.
   * <p>
   * Only full hours are allowed; 01:30, for example, is not allowed.
   * <p>
   * Defaults to midnight - midnight (a full day)
   * <p>
   * Can also be set to a "hybrid" day, such as { start: '06:00', end: '03:00' }, meaning each day
   * starts at 6am but extends into the next day until 3am.
   */
  @Getter
  @Setter
  public static class DayBoundaries implements Serializable {
    private LocalTime start;
    private LocalTime end;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(start)
          .ifPresent(value -> js.put("start", value.format(DateTimeFormatUtils.TIME_FORMATTER)));
      Optional.ofNullable(end)
          .ifPresent(value -> js.put("end", value.format(DateTimeFormatUtils.TIME_FORMATTER)));
      js.put("dayBoundaries", js);
      return js;
    }
  }

  @Getter
  @Setter
  public static class WeekOptions implements Serializable {
    /**
     * The total height in px of the week grid (week and day views).
     */
    private Integer gridHeight;
    /**
     * The number of days to display in week view.
     */
    private Integer nDays;
    /**
     * The width in percentage of the event element in the week grid. Defaults to 100, but can be
     * used to leave a small margin to the right of the event.
     */
    private Integer eventWidth;
    /**
     * Intl.DateTimeFormatOptions used to format the hour labels on the time axis.
     * <p>
     * Default: { hour: 'numeric' }.
     * <p>
     * Example: { hour: '2-digit', minute: '2-digit' }.
     */
    private Map<String, String> timeAxisFormatOptions;
    /**
     * Determines whether concurrent events can overlap. Defaults to true. Set to false to disable
     * overlapping.
     */
    private boolean eventOverlap = true;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(gridHeight).ifPresent(value -> js.put("gridHeight", value));
      Optional.ofNullable(nDays).ifPresent(value -> js.put("nDays", value));
      Optional.ofNullable(eventWidth).ifPresent(value -> js.put("eventWidth", value));
      Optional.ofNullable(timeAxisFormatOptions).ifPresent(map -> {
        JsonObject opts = Json.createObject();
        map.forEach(opts::put);
        js.put("timeAxisFormatOptions", opts);
      });
      js.put("eventOverlap", eventOverlap);
      return js;
    }
  }

  @Getter
  @Setter
  public static class MonthGridOptions implements Serializable {
    /**
     * Number of events to display in a day cell before the "+ N events" button is shown.
     */
    private Integer nEventsPerDay;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(nEventsPerDay).ifPresent(value -> js.put("nEventsPerDay", value));
      return js;
    }
  }

  /**
   * Optional options for drawing events.
   */
  @Getter
  @Setter
  public static class DrawOptions implements Serializable {

    /**
     * Time interval that can be configured, in minutes, at which a time grid-event can be drawn.
     * Valid values: 15, 30, 60
     */
    private TimeInterval snapDrawDuration;
    
    /**
     * Default title to use when drawing an event.
     */
    private String defaultTitle;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(snapDrawDuration)
          .ifPresent(value -> js.put("snapDrawDuration", value.getInterval()));
      Optional.ofNullable(defaultTitle).ifPresent(value -> js.put("defaultTitle", value));
      return js;
    }
  }

  /**
   * iCalendar data configuration.
   */
  @Getter
  @Setter
  public static class ICal implements Serializable {

    /**
     * iCalendar source.
     */
    private String iCal;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(iCal).ifPresent(value -> js.put("iCal", value));
      return js;
    }
  }

  /**
   * Configuration to add a current time indicator to the calendar. It will automatically update
   * every minute.
   */
  @Getter
  @Setter
  public static class CurrentTimeIndicatorConfig implements Serializable {

    /**
     * Whether the indicator should be displayed in the full width of the week. Defaults to false
     */
    private Boolean fullWeekWidth;

    /**
     * Time zone offset in minutes. Can be any offset valid according to UTC (-720 to 840).
     */
    private Integer timeZoneOffset;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(fullWeekWidth).ifPresent(value -> js.put("fullWeekWidth", value));
      Optional.ofNullable(timeZoneOffset).ifPresent(value -> js.put("timeZoneOffset", value));
      return js;
    }
  }

  /**
   * Configuration to control the scrolling in the week and day view grids.
   */
  @Getter
  @Setter
  public static class ScrollControllerConfig implements Serializable {

    /**
     * Initial scroll value.
     */
    private LocalTime initialScroll;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(initialScroll).ifPresent(
          value -> js.put("initialScroll", value.format(DateTimeFormatUtils.TIME_FORMATTER)));
      return js;
    }
  }
}
