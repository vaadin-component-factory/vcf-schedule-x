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
package org.vaadin.addons.componentfactory.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.vaadin.addons.componentfactory.schedulexcalendar.BaseScheduleXCalendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.CalendarViewType;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ResourceViewType;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.ViewType;

/**
 * Creates a component to be use as header for {@link ScheduleXCalendarDemoView} and
 * {@link ScheduleXResourceSchedulerDemoView}.
 */
@SuppressWarnings("serial")
public class CalendarHeaderComponent extends HorizontalLayout {
  
  private final ViewChangeListener viewChangeListener;
  
  private DatePicker datePicker;
  private ComboBox<ViewType> viewsComboBox;
  
  public CalendarHeaderComponent(BaseScheduleXCalendar calendar) {
    this(calendar, null);
  }

  public CalendarHeaderComponent(BaseScheduleXCalendar calendar, ViewChangeListener listener) {

    this.viewChangeListener = listener;
    
    this.setWidthFull();

    Configuration calendarConf = calendar.getConfiguration();
    LocalDate selectedDate =
        calendarConf.getSelectedDate() != null ? calendarConf.getSelectedDate() : null;
    List<? extends ViewType> views = calendar.getViews();
    ViewType defaultView =
        calendarConf.getDefaultView() != null ? calendarConf.getDefaultView() : views.get(0);

    datePicker = new DatePicker("Date");
    if (selectedDate != null) {
      datePicker.setValue(selectedDate);
    }
    datePicker.addValueChangeListener(e -> {
      calendar.setDate(e.getValue());
    });
    
    calendar.addSelectedDateUpdateEventListener(e -> {
      datePicker.setValue(e.getSelectedDate());
    });

    viewsComboBox = new ComboBox<ViewType>("View");
    viewsComboBox.setItems(new ArrayList<ViewType>(views));
    viewsComboBox.setItemLabelGenerator((item) -> {
      if (item instanceof CalendarViewType) {
        switch ((CalendarViewType) item) {
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
      } else if (item instanceof ResourceViewType) {
        switch ((ResourceViewType) item) {
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
      if (viewChangeListener != null) {
        viewChangeListener.onViewChange(e.getValue());
      }
    });

    Button todayButton = new Button("Today");
    todayButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    todayButton.addClickListener(e -> {
      LocalDate today = LocalDate.now();
      calendar.setDate(today);
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
    this.addToEnd(viewsComboBox, datePicker, headerComponentDisclaimer());
    this.setAlignItems(Alignment.BASELINE);
  }

  public DatePicker getDatePicker() {
    return datePicker;
  }

  public ComboBox<ViewType> getViewsComboBox() {
    return viewsComboBox;
  } 
  
  private Button headerComponentDisclaimer() {
    Button headerInfoButton = new Button(new Icon(VaadinIcon.INFO_CIRCLE_O));
    headerInfoButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
    headerInfoButton.setTooltipText("The header section is not part of the vcf-schedule-x component. This component is responsible only for rendering the calendar. The header layout shown here was created exclusively for demonstration purposes in the demo module.");
    Tooltip tooltip = headerInfoButton.getTooltip().withManual(true);
    headerInfoButton.addClickListener(event -> {
      tooltip.setOpened(!tooltip.isOpened());
    });
    return headerInfoButton;
  }
}
