package org.vaadin.addons.componentfactory.schedulexcalendar.util;

import com.vaadin.flow.internal.Pair;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * Java representation of the ResourceViewConfig for the Schedule-X Resource Scheduler. This
 * configuration is used to customize the behavior and appearance of resource views.
 * 
 * @see <a href=
 *      "https://schedule-x.dev/docs/calendar/resource-scheduler#resourceschedulerconfig">ResourceSchedulerConfig
 *      documentation</a>
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class ResourceSchedulerConfig implements Serializable {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

  // TODO: Callbacks not implemented

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
      js.put("initialHours", initialHours.getFirst().format(DATE_TIME_FORMATTER) + ","
          + initialHours.getSecond().format(DATE_TIME_FORMATTER));
    }

    if (initialDays != null) {
      js.put("initialDays", initialDays.getFirst().format(DATE_FORMATTER) + ","
          + initialDays.getSecond().format(DATE_FORMATTER));
    }

    return js.toJson();
  }

}

