package org.vaadin.addons.componentfactory.schedulexcalendar.model;

import com.vaadin.flow.data.provider.Query;

/**
 * Query object for filtering events in the DataProvider.
 */
@SuppressWarnings("serial")
public class EventQuery extends Query<Event, EventQueryFilter> {

  public EventQuery(int offset, int limit, EventQueryFilter filter) {
    super(offset, limit, null, null, filter);
  }
}