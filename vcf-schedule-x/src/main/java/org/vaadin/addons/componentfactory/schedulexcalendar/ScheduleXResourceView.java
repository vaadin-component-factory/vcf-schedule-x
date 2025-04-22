package org.vaadin.addons.componentfactory.schedulexcalendar;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceSchedulerConfig;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.View;

/**
 * A view for displaying resources (people, rooms, equipment etc.) in a time grid.
 * 
 * @see <a href="https://schedule-x.dev/docs/calendar/resource-scheduler">Resource Scheduler</a>
 * 
 */
@SuppressWarnings("serial")
@NpmPackage(value = "preact", version = "10.26.4")
@NpmPackage(value = "@preact/signals", version = "2.0.2")
@NpmPackage(value = "@sx-premium/resource-scheduler", version = "3.15.0")
@JsModule("./src/vcf-schedule-x-resource-view.js")
@CssImport("@sx-premium/resource-scheduler/index.css")
@Setter
@Getter
public class ScheduleXResourceView extends BaseScheduleXCalendar {

  private ResourceSchedulerConfig resourceSchedulerConfig;

  public ScheduleXResourceView() {
    super();
    setClassName("vcf-schedule-x-resource-view");
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider) {
    super(views, eventProvider);
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider,
      Configuration configuration) {
    super(views, eventProvider, configuration);
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars) {
    super(views, eventProvider, configuration, calendars);
  }

  public ScheduleXResourceView(List<ResourceView> views, EventProvider eventProvider,
      Configuration configuration, Map<String, Calendar> calendars,
      ResourceSchedulerConfig resourceSchedulerConfig) {
    super(views, eventProvider, configuration, calendars);
    this.resourceSchedulerConfig = resourceSchedulerConfig;
  }

  @Override
  protected void initCalendar() {
    this.getElement().executeJs("vcfschedulexresourceview.create($0, $1, $2, $3, $4)", this,
        viewsToJson(), configurationToJson(), calendarsToJson(), resourceSchedulerConfigToJson());
  }

  protected String resourceSchedulerConfigToJson() {
    return resourceSchedulerConfig != null ? resourceSchedulerConfig.getJson() : "{}";
  }

  @Override
  public void setView(View view, LocalDate selectedDate) {
    this.getElement().executeJs("vcfschedulexresourceview.setView($0, $1, $2);", this,
        view.getName(), selectedDate.format(DATE_FORMATTER));
  }

  @Override
  public void setSelectedDate(LocalDate selectedDate) {
    this.getElement().executeJs("vcfschedulexresourceview.setSelectedDate($0, $1);", this,
        selectedDate.format(DATE_FORMATTER));
  }

  @Override
  public void navigateForwards() {
    // TODO Auto-generated method stub    
  }

  @Override
  public void navigateBackwards() {
    // TODO Auto-generated method stub
  }

  @Override
  public void addEvent(Event event) {
    this.getElement().executeJs("vcfschedulexresourceview.addEvent($0, $1);", this, event.getJson());
  }
  
  @Override
  public void removeEvent(String eventId) {
    this.getElement().executeJs("vcfschedulexresourceview.removeEvent($0, $1);", this, eventId);
  }
  
  @Override
  public void updateEvent(Event event) {
    this.getElement().executeJs("vcfschedulexresourceview.updateEvent($0, $1);", this, event.getJson());
  }
}
