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

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@SuppressWarnings("serial")
@Route("")
public class DemoMainLayout extends AppLayout {

  public DemoMainLayout() {
    DrawerToggle toggle = new DrawerToggle();

    H1 title = new H1("vcf-schedule-x demo");
    title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

    SideNav nav = getSideNav();

    Scroller scroller = new Scroller(nav);
    scroller.setClassName(LumoUtility.Padding.SMALL);

    addToDrawer(scroller);
    addToNavbar(toggle, title);
  }

  private SideNav getSideNav() {
    SideNav sideNav = new SideNav();
    sideNav.addItem(
            new SideNavItem("Calendar", "/calendar"),
            new SideNavItem("Resource View", "/resource"),
            new SideNavItem("Scheduling Assistant", "/scheduling"));
    return sideNav;
}
}
