package org.vaadin.addons.componentfactory.template.util;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Configuration implements Serializable {
  
  /**
   * The preferred view to display when the calendar is first rendered.
   * all views that you import have a "name" property, which helps you identify them.
   * Defaults to the first view in the "views" array
   * */
  private View defaultView;
  
  /**
   * The default date to display when the calendar is first rendered. Only accepts YYYY-MM-DD format.
   * Defaults to the current date
   * */
  private String selectedDate;
  
  /**
   * Set the language. List of supported languages: https://schedule-x.dev/docs/calendar/language
   *
   * Defaults to 'en-US'
   * */
  private String locale;
  
  /**
   * Set which day is to be considered the starting day of the week. 0 = Sunday, 1 = Monday, (...other days) 6 = Saturday
   * Defaults to 1 (Monday)
   * */
  private Integer firstDayOfWeek; 
  
  // TODO
  
  public String getJson() {
    JsonObject js = Json.createObject();
    Optional.ofNullable(defaultView).ifPresent(value -> js.put("defaultView", value.getName()));
    Optional.ofNullable(selectedDate).ifPresent(value -> js.put("selectedDate", value));
    Optional.ofNullable(locale).ifPresent(value -> js.put("locale", value));
    Optional.ofNullable(firstDayOfWeek).ifPresent(value -> js.put("firstDayOfWeek", value));
    return js.toJson();
  }

}
