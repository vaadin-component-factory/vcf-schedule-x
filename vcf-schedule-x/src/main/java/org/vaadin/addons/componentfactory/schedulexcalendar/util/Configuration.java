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

import elemental.json.Json;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * Java representation of the configuration options for the Schedule-X Calendar. This configuration
 * is used to customize the behavior and appearance of the calendar views.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/configuration">Calendar configuration
 * documentation</a>
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class Configuration implements Serializable {
  
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
  private String minDate;

  /**
   * The maximum date that can be displayed.
   */
  private String maxDate;

  private WeekOptions weekOptions;

  private MonthGridOptions monthGridOptions;

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

  public String getJson() {
    JsonObject js = Json.createObject();
    Optional.ofNullable(defaultView).ifPresent(value -> js.put("defaultView", value.getName()));
    Optional.ofNullable(selectedDate).ifPresent(value -> js.put("selectedDate", value.format(DATE_FORMATTER)));
    Optional.ofNullable(locale).ifPresent(value -> js.put("locale", value));
    Optional.ofNullable(firstDayOfWeek).ifPresent(value -> js.put("firstDayOfWeek", value));
    js.put("isDark", isDark);
    Optional.ofNullable(dayBoundaries).ifPresent(value -> js.put("dayBoundaries", value.toJson()));
    Optional.ofNullable(minDate).ifPresent(value -> js.put("minDate", value));
    Optional.ofNullable(maxDate).ifPresent(value -> js.put("maxDate", value));
    Optional.ofNullable(weekOptions).ifPresent(value -> js.put("weekOptions", value.toJson()));
    Optional.ofNullable(monthGridOptions)
        .ifPresent(value -> js.put("monthGridOptions", value.toJson()));
    js.put("showWeekNumbers", showWeekNumbers);
    js.put("isResponsive", isResponsive);
    js.put("skipValidation", skipValidation);
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
    private String start;
    private String end;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(start).ifPresent(value -> js.put("start", value));
      Optional.ofNullable(end).ifPresent(value -> js.put("end", value));
      js.put("dayBoundaries", js);
      return js;
    }
  }

  @Getter
  @Setter
  public static class WeekOptions implements Serializable {
    /**
     * The total height in px of the week grid (week- and day views),
     */
    private Integer gridHeight;
    /**
     * The number of days to display in week view,
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
     * Number of events to display in a day cell before the "+ N events" button is shown
     */
    private Integer nEventsPerDay;

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(nEventsPerDay).ifPresent(value -> js.put("nEventsPerDay", value));
      return js;
    }
  }
}
