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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import elemental.json.Json;
import elemental.json.JsonArray;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Configuration;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.Event;
import org.vaadin.addons.componentfactory.schedulexcalendar.util.View;

/**
 * Template Addon showing the basics of how to build a Vaadin Wrapper Addon
 *
 */
@SuppressWarnings("serial")
@NpmPackage(value = "@schedule-x/calendar", version = "2.27.2")
@NpmPackage(value = "@schedule-x/theme-default", version = "2.27.2")
@JsModule("./src/vcf-schedule-x-calendar.js")
@CssImport("@schedule-x/theme-default/dist/index.css")
@CssImport("./styles/vcf-schedule-x-calendar-styles.css")
public class ScheduleXCalendar extends Div {

  private List<View> views = new ArrayList<>();

  private List<Event> events = new ArrayList<>();

  private Configuration configuration;

  public ScheduleXCalendar() {
    this.setId(String.valueOf(this.hashCode()));
    setClassName("vcf-schedule-x-calendar");
  }

  public ScheduleXCalendar(List<View> views, List<Event> events) {
    this();
    this.views = new ArrayList<>(views);
    this.events = new ArrayList<>(events);
  }

  public ScheduleXCalendar(List<View> views, List<Event> events, Configuration configuration) {
    this(views, events);
    this.configuration = configuration;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.initCalendar();
  }

  private void initCalendar() {
    this.getElement().executeJs("vcfschedulexcalendar.create($0, $1, $2, $3)", this, viewsToJson(),
        "[" + eventsToJson() + "]", configuration != null ? configuration.getJson() : "{}");
  }

  private String viewsToJson() {
    JsonArray jsonArray = Json.createArray();
    for (int i = 0; i < views.size(); i++) {
      jsonArray.set(i, views.get(i).getName());
    }
    return jsonArray.toJson();
  }

  private String eventsToJson() {
    return this.events != null
        ? this.events.stream().map(event -> event.getJson()).collect(Collectors.joining(","))
        : "";
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    this.getElement().removeAllChildren();
  }

}
