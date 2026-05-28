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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.Map;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar.ColorDefinition;

/**
 * Base class for demo creation.
 */
@SuppressWarnings("serial")
@CssImport("./styles/demo-styles.css")
public abstract class ScheduleXBaseDemoView extends VerticalLayout {

  private boolean initialized;

  public ScheduleXBaseDemoView() {
    this.addClassNames("vcf-schedule-x-demo", "demo-view");
    this.setSizeFull();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    if (!initialized) {
      initialized = true;
      createDemo();
    }
  }

  protected abstract void createDemo();

  protected Div addCard(String title, Component... components) {
    Div card = new Div();
    card.addClassName("demo-card");
    card.setWidthFull();
    card.add(new H3(title));
    card.add(components);
    add(card);
    return card;
  }

  protected Map<String, Calendar> getCalendars() {
    Calendar work = new Calendar("work");
    work.setLightColors(new ColorDefinition("var(--calendar-work-light-color)",
        "var(--calendar-work-light-container)", "var(--calendar-work-light-on-container)"));
    work.setDarkColors(new ColorDefinition("var(--calendar-work-dark-color)",
        "var(--calendar-work-dark-container)", "var(--calendar-work-dark-on-container)"));
    Calendar leisure = new Calendar("leisure");
    leisure.setLightColors(new ColorDefinition("var(--calendar-leisure-light-color)",
        "var(--calendar-leisure-light-container)", "var(--calendar-leisure-light-on-container)"));
    leisure.setDarkColors(new ColorDefinition("var(--calendar-leisure-dark-color)",
        "var(--calendar-leisure-dark-container)", "var(--calendar-leisure-dark-on-container)"));
    return Map.of("work", work, "leisure", leisure);
  }

  protected FieldSet createFieldSetLayout(String text, Component component) {
    FieldSet fieldSet = new FieldSet(text, component);
    fieldSet.getStyle().setBorderRadius("var(--lumo-border-radius-s)");
    return fieldSet;
  }  
  
  protected VerticalLayout createLayoutWithHelperText(String text, Component component) {
    Span helperSpan = new Span(text);
    helperSpan.getStyle().set("font-size", "small");
    helperSpan.getStyle().set("padding-bottom", "5px");
    VerticalLayout helperLayout = new VerticalLayout();
    helperLayout.setSpacing(false);
    helperLayout.setPadding(false);
    helperLayout.add(helperSpan, component);
    return helperLayout;
  }
    
}
