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

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.vaadin.addons.componentfactory.template.ScheduleXCalendar;
import org.vaadin.addons.componentfactory.template.util.Configuration;
import org.vaadin.addons.componentfactory.template.util.Event;
import org.vaadin.addons.componentfactory.template.util.View;

/**
 * View for {@link ScheduleXCalendar} demo.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Route("")
public class ScheduleXCalendarDemoView extends DemoView {

  @Override
  public void initView() {
    this.getStyle().set("max-width", "1500px");
    createBasicDemo();

    addCard("Additional code used in the demo", new Span("These methods are used in the demo."));
  }

  private void createBasicDemo() {
    Div message = createMessageDiv("basic-use-demo-message");

    // begin-source-example
    // source-example-heading: Basic Use Demo

    Event event1 = new Event("1", "2025-04-15 10:05", "2025-04-15 10:35");
    event1.setTitle("Coffee with John");
    Event event2 = new Event("2", "2025-04-16 16:00", "2025-04-16 16:45");
    event2.setTitle("Meeting with Jackie");
    Configuration configuration = new Configuration();
    configuration.setSelectedDate("2025-04-17");
    configuration.setDefaultView(View.WEEK);
    ScheduleXCalendar calendar = new ScheduleXCalendar(
        Arrays.asList(View.DAY, View.WEEK,View.MONTH_GRID, View.MONTH_AGENDA),
        Arrays.asList(event1, event2), configuration);

    // calendar.addValueChangeListener(ev->{
    // updateMessage(message, paperInput);
    // });
    // end-source-example

    calendar.setId("basic-use-demo");

    addCard("Basic Use Demo", calendar, message);
  }


  // begin-source-example
  // source-example-heading: Additional code used in the demo
  /**
   * Additional code used in the demo
   */
  private void updateMessage(Div message, ScheduleXCalendar paperInput) {
    // message.setText("Entered text: " + paperInput.getValue());
  }

  private Div createMessageDiv(String id) {
    Div message = new Div();
    message.setId(id);
    message.getStyle().set("whiteSpace", "pre");
    return message;
  }
  // end-source-example
}
