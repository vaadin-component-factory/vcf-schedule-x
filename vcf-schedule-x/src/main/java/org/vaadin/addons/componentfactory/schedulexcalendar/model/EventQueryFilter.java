package org.vaadin.addons.componentfactory.schedulexcalendar.model;

import java.time.LocalDateTime;

/**
 * Filter object for querying events in the DataProvider.
 */
public class EventQueryFilter {

  private LocalDateTime startDate;
  private LocalDateTime endDate;

  public EventQueryFilter(LocalDateTime startDate, LocalDateTime endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }
}