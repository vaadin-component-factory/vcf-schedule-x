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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.FieldSet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.demo.DemoView;

/**
 * Base class for demo creation.
 */
@SuppressWarnings("serial")
public abstract class ScheduleXBaseDemoView extends DemoView {

  public ScheduleXBaseDemoView() {}
  
  @Override
  public void initView() {
    this.getStyle().set("max-width", "1500px");
    createBasicDemo();

    addCard("Additional code used in the demo", new Span("These methods are used in the demo."));
  }

  protected abstract void createBasicDemo();

  protected FieldSet createFieldSetLayout(String text, Component component) {
    FieldSet fieldSet = new FieldSet(text, component);
    fieldSet.getStyle().setBorderRadius("var(--lumo-border-radius-s)");
    return fieldSet;
  }
  
}
