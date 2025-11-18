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

import elemental.json.Json;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.DateTimeFormatUtils;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.LocaleUtils;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.TimeInterval;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ViewType;

/**
 * Java representation of the configuration options for the Schedule-X Calendar. This configuration
 * is used to customize the behavior and appearance of the calendar views.
 */
@SuppressWarnings("serial")
public class Configuration extends BaseConfiguration implements Serializable {

  /**
   * The preferred view to display when the calendar is first rendered. all views that you import
   * have a "name" property, which helps you identify them. Defaults to the first view in the
   * "views" list.
   */
  private ViewType defaultView;

  /**
   * The default date to display when the calendar is first rendered. Only accepts YYYY-MM-DD
   * format. Defaults to the current date.
   */
  private LocalDate selectedDate;

  /**
   * Set the language. List of supported languages: https://schedule-x.dev/docs/calendar/language
   * Defaults to 'en-US'.
   */
  private Locale locale;

  /**
   * Set which day is to be considered the starting day of the week. 0 = Sunday, 1 = Monday,
   * (...other days) 6 = Saturday Defaults to 1 (Monday)
   */
  private Integer firstDayOfWeek;
  
  /**
   * Set the timezone.
   * Defaults to 'UTC'
   * */
  private ZoneId timeZone;

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

  public ViewType getDefaultView() {
    return defaultView;
  }

  public void setDefaultView(ViewType defaultView) {
    this.defaultView = defaultView;
    this.runRefresh();
  }

  public LocalDate getSelectedDate() {
    return selectedDate;
  }

  public void setSelectedDate(LocalDate selectedDate) {
    if (this.getCalendar() != null) {
      this.getCalendar().updateDate(selectedDate);
    }
    this.selectedDate = selectedDate;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    LocaleUtils.validateLocale(locale);
    if (this.getCalendar() != null) {
      this.getCalendar().updateLocale(locale);
    }
    this.locale = locale;
  }
  
  public ZoneId getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(ZoneId timeZone) {
    if (this.getCalendar() != null) {
      this.getCalendar().updateTimeZone(timeZone);
    }
    this.timeZone = timeZone;
  }

  public Integer getFirstDayOfWeek() {
    return firstDayOfWeek;
  }

  public void setFirstDayOfWeek(Integer firstDayOfWeek) {
    if (this.getCalendar() != null) {
      this.getCalendar().updateFirstDayOfWeek(firstDayOfWeek);
    }
    this.firstDayOfWeek = firstDayOfWeek;
  }

  public boolean isDark() {
    return isDark;
  }

  public void setDark(boolean isDark) {
    this.isDark = isDark;
    this.runRefresh();
  }

  public DayBoundaries getDayBoundaries() {
    return dayBoundaries;
  }

  public void setDayBoundaries(DayBoundaries dayBoundaries) {
    if (dayBoundaries != null) {
      dayBoundaries.setConfiguration(this);
    }
    if (this.getCalendar() != null) {
      this.getCalendar().updateDayBoundaries(dayBoundaries);
    }
    this.dayBoundaries = dayBoundaries;
  }
  
  public LocalDate getMinDate() {
    return minDate;
  }

  public void setMinDate(LocalDate minDate) {
    if (this.getCalendar() != null) {
      this.getCalendar().updateMinDate(minDate);
    }
    this.minDate = minDate;
  }

  public LocalDate getMaxDate() {
    return maxDate;
  }

  public void setMaxDate(LocalDate maxDate) {
    if (this.getCalendar() != null) {
      this.getCalendar().updateMaxDate(maxDate);
    }
    this.maxDate = maxDate;
  }

  public WeekOptions getWeekOptions() {
    return weekOptions;
  }

  public void setWeekOptions(WeekOptions weekOptions) {
    if (weekOptions != null) {
      weekOptions.setConfiguration(this);
    }
    if (this.getCalendar() != null) {
      this.getCalendar().updateWeekOptions(weekOptions);
    }
    this.weekOptions = weekOptions;
  }

  public MonthGridOptions getMonthGridOptions() {
    return monthGridOptions;
  }

  public void setMonthGridOptions(MonthGridOptions monthGridOptions) {
    if (monthGridOptions != null) {
      monthGridOptions.setConfiguration(this);
    }
    if (this.getCalendar() != null) {
      this.getCalendar().updateMonthGridOptions(monthGridOptions);
    }
    this.monthGridOptions = monthGridOptions;
  }

  public DrawOptions getDrawOptions() {
    return drawOptions;
  }

  public void setDrawOptions(DrawOptions drawOptions) {
    this.drawOptions = drawOptions;
    if (this.drawOptions != null) {
      this.drawOptions.setConfiguration(this);
    }
    this.runRefresh();
  }

  public ICal getiCal() {
    return iCal;
  }

  public void setiCal(ICal iCal) {
    this.iCal = iCal;
    this.runRefresh();
  }

  public boolean isShowWeekNumbers() {
    return showWeekNumbers;
  }

  public void setShowWeekNumbers(boolean showWeekNumbers) {
    this.showWeekNumbers = showWeekNumbers;
    this.runRefresh();
  }

  public boolean isResponsive() {
    return isResponsive;
  }

  public void setResponsive(boolean isResponsive) {
    this.isResponsive = isResponsive;
    this.runRefresh();
  }

  public boolean isSkipValidation() {
    return skipValidation;
  }

  public void setSkipValidation(boolean skipValidation) {
    this.skipValidation = skipValidation;
    this.runRefresh();
  }

  public TimeInterval getResizeInterval() {
    return resizeInterval;
  }

  public void setResizeInterval(TimeInterval resizeInterval) {
    this.resizeInterval = resizeInterval;
    this.runRefresh();
  }

  public TimeInterval getDragAndDropInterval() {
    return dragAndDropInterval;
  }

  public void setDragAndDropInterval(TimeInterval dragAndDropInterval) {
    this.dragAndDropInterval = dragAndDropInterval;
    this.runRefresh();
  }

  public CurrentTimeIndicatorConfig getCurrentTimeIndicatorConfig() {
    return currentTimeIndicatorConfig;
  }

  public void setCurrentTimeIndicatorConfig(CurrentTimeIndicatorConfig currentTimeIndicatorConfig) {
    this.currentTimeIndicatorConfig = currentTimeIndicatorConfig;
    if (this.currentTimeIndicatorConfig != null) {
      this.currentTimeIndicatorConfig.setConfiguration(this);
    }
    this.runRefresh();
  }

  public ScrollControllerConfig getScrollControllerConfig() {
    return scrollControllerConfig;
  }

  public void setScrollControllerConfig(ScrollControllerConfig scrollControllerConfig) {
    this.scrollControllerConfig = scrollControllerConfig;
    if (this.scrollControllerConfig != null) {
      this.scrollControllerConfig.setConfiguration(this);
    }
    this.runRefresh();
  }

  public String getJson() {
    JsonObject js = Json.createObject();
    Optional.ofNullable(defaultView).ifPresent(value -> js.put("defaultView", value.getName()));
    Optional.ofNullable(selectedDate).ifPresent(
        value -> js.put("selectedDate", value.format(DateTimeFormatUtils.DATE_FORMATTER)));
    Optional.ofNullable(locale)
        .ifPresent(value -> js.put("locale", LocaleUtils.toScheduleXLocale(value)));
    Optional.ofNullable(timeZone).ifPresent(value -> js.put("timezone", timeZone.getId()));
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
  public static class DayBoundaries extends BaseConfigurationSection implements Serializable {

    private LocalTime start;
    private LocalTime end;

    public LocalTime getStart() {
      return start;
    }

    public void setStart(LocalTime start) {
      this.start = start;
      this.updateDayBoundaries();
    }

    public LocalTime getEnd() {
      return end;
    }

    public void setEnd(LocalTime end) {
      this.end = end;
      this.updateDayBoundaries();
    }
    
    private void updateDayBoundaries() {
      if (this.getConfiguration() != null) {
        ((Configuration) this.getConfiguration()).setDayBoundaries(this);
      }
    }

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(start)
          .ifPresent(value -> js.put("start", value.format(DateTimeFormatUtils.TIME_FORMATTER)));
      Optional.ofNullable(end)
          .ifPresent(value -> js.put("end", value.format(DateTimeFormatUtils.TIME_FORMATTER)));
      return js;
    }
  }

  public static class WeekOptions extends BaseConfigurationSection implements Serializable {
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

    public Integer getGridHeight() {
      return gridHeight;
    }

    public void setGridHeight(Integer gridHeight) {
      this.gridHeight = gridHeight;
      this.updateWeekOptions();
    }

    public Integer getnDays() {
      return nDays;
    }

    public void setnDays(Integer nDays) {
      this.nDays = nDays;
      this.updateWeekOptions();
    }

    public Integer getEventWidth() {
      return eventWidth;
    }

    public void setEventWidth(Integer eventWidth) {
      this.eventWidth = eventWidth;
      this.updateWeekOptions();
    }

    public Map<String, String> getTimeAxisFormatOptions() {
      return timeAxisFormatOptions;
    }

    public void setTimeAxisFormatOptions(Map<String, String> timeAxisFormatOptions) {
      this.timeAxisFormatOptions = timeAxisFormatOptions;
      this.updateWeekOptions();
    }

    public boolean isEventOverlap() {
      return eventOverlap;
    }

    public void setEventOverlap(boolean eventOverlap) {
      this.eventOverlap = eventOverlap;
      this.updateWeekOptions();
    }
    
    private void updateWeekOptions() {
      if (this.getConfiguration() != null) {
        ((Configuration) this.getConfiguration()).setWeekOptions(this);
      }
    }

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

  public static class MonthGridOptions extends BaseConfigurationSection implements Serializable {
    /**
     * Number of events to display in a day cell before the "+ N events" button is shown.
     */
    private Integer nEventsPerDay;

    public Integer getnEventsPerDay() {
      return nEventsPerDay;
    }

    public void setnEventsPerDay(Integer nEventsPerDay) {
      this.nEventsPerDay = nEventsPerDay;
      this.updateMonthGridOptions();
    }

    private void updateMonthGridOptions() {
      if (this.getConfiguration() != null) {
        ((Configuration) this.getConfiguration()).setMonthGridOptions(this);
      }
    }
    
    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(nEventsPerDay).ifPresent(value -> js.put("nEventsPerDay", value));
      return js;
    }
  }

  /**
   * Optional options for drawing events.
   */
  public static class DrawOptions extends BaseConfigurationSection implements Serializable {

    /**
     * Time interval that can be configured, in minutes, at which a time grid-event can be drawn.
     * Valid values: 15, 30, 60
     */
    private TimeInterval snapDrawDuration;

    /**
     * Default title to use when drawing an event.
     */
    private String defaultTitle;

    public TimeInterval getSnapDrawDuration() {
      return snapDrawDuration;
    }

    public void setSnapDrawDuration(TimeInterval snapDrawDuration) {
      this.snapDrawDuration = snapDrawDuration;
      this.runRefresh();
    }

    public String getDefaultTitle() {
      return defaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
      this.defaultTitle = defaultTitle;
      this.runRefresh();
    }

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
  public static class ICal implements Serializable {

    /**
     * iCalendar source.
     */
    private String iCal;

    public String getiCal() {
      return iCal;
    }

    public void setiCal(String iCal) {
      this.iCal = iCal;
    }

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
  public static class CurrentTimeIndicatorConfig extends BaseConfigurationSection implements Serializable {

    /**
     * Whether the indicator should be displayed in the full width of the week. Defaults to false
     */
    private Boolean fullWeekWidth;

    /**
     * Time zone offset in minutes. Can be any offset valid according to UTC (-720 to 840).
     */
    private Integer timeZoneOffset;

    public Boolean getFullWeekWidth() {
      return fullWeekWidth;
    }

    public void setFullWeekWidth(Boolean fullWeekWidth) {
      this.fullWeekWidth = fullWeekWidth;
      this.runRefresh();
    }

    public Integer getTimeZoneOffset() {
      return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
      this.timeZoneOffset = timeZoneOffset;
      this.runRefresh();
    }

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
  public static class ScrollControllerConfig extends BaseConfigurationSection implements Serializable {

    /**
     * Initial scroll value.
     */
    private LocalTime initialScroll;

    public LocalTime getInitialScroll() {
      return initialScroll;
    }

    public void setInitialScroll(LocalTime initialScroll) {
      this.initialScroll = initialScroll;
      this.runRefresh();
    }

    public JsonObject toJson() {
      JsonObject js = Json.createObject();
      Optional.ofNullable(initialScroll).ifPresent(
          value -> js.put("initialScroll", value.format(DateTimeFormatUtils.TIME_FORMATTER)));
      return js;
    }
  }
}
