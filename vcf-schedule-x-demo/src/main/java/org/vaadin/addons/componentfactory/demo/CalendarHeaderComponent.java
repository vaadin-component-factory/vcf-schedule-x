package org.vaadin.addons.componentfactory.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.vaadin.addons.componentfactory.schedulexcalendar.BaseScheduleXCalendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceView;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.View;

@SuppressWarnings("serial")
public class CalendarHeaderComponent extends HorizontalLayout {

  public CalendarHeaderComponent(BaseScheduleXCalendar calendar) {

    this.setWidthFull();

    Configuration calendarConf = calendar.getConfiguration();
    LocalDate selectedDate =
        calendarConf.getSelectedDate() != null ? calendarConf.getSelectedDate() : null;
    List<? extends View> views = calendar.getViews();
    View defaultView =
        calendarConf.getDefaultView() != null ? calendarConf.getDefaultView() : views.get(0);

    DatePicker datePicker = new DatePicker("Date");
    if (selectedDate != null) {
      datePicker.setValue(selectedDate);
    }
    datePicker.addValueChangeListener(e -> {
      calendar.setSelectedDate(e.getValue());
    });
    
    calendar.addSelectedDateUpdateEventListener(e -> {
      datePicker.setValue(e.getSelectedDate());
    });

    ComboBox<View> viewsComboBox = new ComboBox<View>("View");
    viewsComboBox.setItems(new ArrayList<View>(views));
    viewsComboBox.setItemLabelGenerator((item) -> {
      if (item instanceof CalendarView) {
        switch ((CalendarView) item) {
          case DAY:
            return "Day";
          case WEEK:
            return "Week";
          case MONTH_GRID:
            return "Month";
          case MONTH_AGENDA:
            return "Agenda";
          default:
            break;
        }
      } else if (item instanceof ResourceView) {
        switch ((ResourceView) item) {
          case DAILY:
            return "Daily";
          case HOURLY:
            return "Hourly";
          default:
            break;
        }
      }
      return null;
    });
    viewsComboBox.setValue(defaultView);
    viewsComboBox.addValueChangeListener(e -> {
      calendar.setView(e.getValue());
    });

    Button todayButton = new Button("Today");
    todayButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    todayButton.addClickListener(e -> {
      LocalDate today = LocalDate.now();
      calendar.setSelectedDate(today);
      datePicker.setValue(today);
    });

    Button previousButton = new Button(VaadinIcon.CHEVRON_LEFT.create());
    previousButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY,
        ButtonVariant.LUMO_CONTRAST);
    previousButton.addClickListener(e -> calendar.navigateBackwards());
    Button nextButton = new Button(VaadinIcon.CHEVRON_RIGHT.create());
    nextButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY,
        ButtonVariant.LUMO_CONTRAST);
    nextButton.addClickListener(e -> calendar.navigateForwards());

    this.addToStart(todayButton, previousButton, nextButton);
    this.addToEnd(viewsComboBox, datePicker);
    this.setAlignItems(Alignment.BASELINE);
  }

}
