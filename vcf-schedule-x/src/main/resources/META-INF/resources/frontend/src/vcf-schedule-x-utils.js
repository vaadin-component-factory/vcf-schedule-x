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

export function processConfiguration(configJson, viewNameMap) {
	if (!configJson) return {};

	const parsedConfig = JSON.parse(configJson);
	
	if (parsedConfig.selectedDate && typeof parsedConfig.selectedDate === 'string') {
	  try {
	    parsedConfig.selectedDate = Temporal.PlainDate.from(parsedConfig.selectedDate);
	  } catch (e) {
	    console.error("Failed to parse selectedDate:", e);
	  }
	}
	
	if (parsedConfig.minDate && typeof parsedConfig.minDate === 'string') {
	  try {
	    parsedConfig.minDate = Temporal.PlainDate.from(parsedConfig.minDate);
	  } catch (e) {
	    console.error("Failed to parse minDate:", e);
	  }
	}
	
	if (parsedConfig.maxDate && typeof parsedConfig.maxDate === 'string') {
	  try {
	    parsedConfig.maxDate = Temporal.PlainDate.from(parsedConfig.maxDate);
	  } catch (e) {
	    console.error("Failed to parse maxDate:", e);
	  }
	}

	if (parsedConfig.defaultView) {
		parsedConfig.defaultView = viewNameMap[parsedConfig.defaultView];
	}

	return parsedConfig;
}

export function processViews(viewFnNames, viewFactories, resourceConfig) {
	return viewFnNames
		.map(fnName => viewFactories[fnName])
		.filter(Boolean)
		.map(factory => factory(resourceConfig)); // optional for resource views
}

export function updateEvents(div, range){
	div.parentElement.$server.updateRange(range.start, range.end);
}

export function updateResourceSchedulerRange(container, range){
	container.parentElement.$server.updateResourceSchedulerRange(range.start, range.end);
}

export function setSelectedView(calendar, viewName) {
	calendar.$app.config.plugins.calendarControls.setView(viewName);
}

export function setSelectedDate(calendar, selectedDate) {
	calendar.$app.config.plugins.calendarControls.setDate(Temporal.PlainDate.from(selectedDate));
}

export function handleOnEventClick(div, calendarEvent) {
	div.parentElement.$server.onCalendarEventClick(calendarEvent.id, calendarEvent.start, calendarEvent.end);
}

export function handleOnSelectedDateUpdate(div, date) {
	div.parentElement.$server.onSelectedDateUpdate(date);
}

/**
 * This handles event updates on resize or dnd
 */
export function handleEventUpdate(div, updatedEvent) {
	div.parentElement.$server.onEventUpdate(updatedEvent.id, updatedEvent.start, updatedEvent.end);
}

export function updateFirstDayOfWeek(calendar, firstDayOfWeek) {
	calendar.$app.config.plugins.calendarControls.setFirstDayOfWeek(firstDayOfWeek);
}

export function updateLocale(calendar, locale) {
	calendar.$app.config.plugins.calendarControls.setLocale(locale);
}

export function updateTimeZone(calendar, timeZone) {
	calendar.$app.config.plugins.calendarControls.setTimeZone(timeZone);
}

export function updateViews(calendar, viewFnNames, viewFactories) {
	const $app = calendar.$app;
	const views = processViews(viewFnNames, viewFactories, $app.resourceViewConfig)
	calendar.$app.config.plugins.calendarControls.setViews(views);
}

export function updateDayBoundaries(calendar, dayBoundaries) {
	calendar.$app.config.plugins.calendarControls.setDayBoundaries(dayBoundaries);
}

export function updateWeekOptions(calendar, weekOptions) {
	calendar.$app.config.plugins.calendarControls.setWeekOptions(weekOptions);
}

export function updateCalendars(calendar, calendars) {
	calendar.$app.config.plugins.calendarControls.setCalendars(calendars);
}

export function updateMinDate(calendar, minDate) {
	calendar.$app.config.plugins.calendarControls.setMinDate(Temporal.PlainDate.from(minDate));
}

export function updateMaxDate(calendar, maxDate) {
	calendar.$app.config.plugins.calendarControls.setMaxDate(Temporal.PlainDate.from(maxDate));
}

export function updateMonthGridOptions(calendar, monthGridOptions) {
	calendar.$app.config.plugins.calendarControls.setMonthGridOptions(monthGridOptions);
}

/**
 * Subscribes to updates of Scheduling Assistant to inform server side about the updates.
 */
export function subscribeToSchedulingAssistantUpdates(container) {
	const plugin = container.calendar.$app.config.plugins["scheduling-assistant"];

	const emitCombinedUpdate = () => {
		container.parentElement.dispatchEvent(new CustomEvent('scheduling-assistant-update', {
			detail: {
				currentStart: plugin.currentStart.value,
				currentEnd: plugin.currentEnd.value,
				hasCollision: plugin.hasCollision.value
			}
		}));
	};

	// Subscribe and emit combined update when any signal changes
	plugin.currentStart.subscribe(emitCombinedUpdate);
	plugin.currentEnd.subscribe(emitCombinedUpdate);
	plugin.hasCollision.subscribe(emitCombinedUpdate);
}

export function getZonedDateTime(container, dateTime) {
	const plainDateTime = Temporal.PlainDateTime.from(dateTime);
	return plainDateTime.toZonedDateTime(container.calendar.$app.config.timezone.value); 
}

/**
 * Checks if an event spans an entire day (0:00 - 23:59) on the same date and converts to PlainDate for day/week views.
 * This allows schedule-x to display the event in the header rather than in the time grid.
 * Only processes events that start and end on the same date.
 * 
 * @param {Object} calendar - The calendar object
 * @param {Object} event - The event to process
 * @returns {Object} The potentially modified event
 */
export function processAllDayEventForView(calendar, event) {
	if (!event || !event.start || !event.end) {
		return event;
	}
	
	// Get current view name
	let currentViewName;
	try {
		if (calendar && calendar.$app && calendar.$app.calendarState && calendar.$app.calendarState.view) {
			currentViewName = calendar.$app.calendarState.view.value;
		}
	} catch (e) {
		return event;
	}
	
	// Only process for day and week views
	if (currentViewName !== 'day' && currentViewName !== 'week') {
		return event;
	}
	
	// Check if already a PlainDate
	if (event.start instanceof Temporal.PlainDate) {
		return event;
	}
	
	// Check if event spans entire day (0:00 to 23:59) on the same date
	if (event.start instanceof Temporal.ZonedDateTime && event.end instanceof Temporal.ZonedDateTime) {
		const startDate = event.start.toPlainDate();
		const endDate = event.end.toPlainDate();
		
		// Only process if start and end are on the same date
		if (Temporal.PlainDate.compare(startDate, endDate) !== 0) {
			return event;
		}
		
		const startHour = event.start.hour;
		const startMinute = event.start.minute;
		const endHour = event.end.hour;
		const endMinute = event.end.minute;
		
		const isStartAtMidnight = startHour === 0 && startMinute === 0;
		const isEndAt2359 = endHour === 23 && endMinute === 59;
		
		if (isStartAtMidnight && isEndAt2359) {
			// Convert to PlainDate for all-day events
			event.start = startDate;
			event.end = startDate;
		}
	}
	
	return event;
}

