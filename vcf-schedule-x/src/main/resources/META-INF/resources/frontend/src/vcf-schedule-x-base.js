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

import { createCalendar } from '@schedule-x/calendar';
import { createEventsServicePlugin } from '@schedule-x/events-service';
import { createResizePlugin } from '@schedule-x/resize';
import {
	handleOnEventClick,
	handleOnSelectedDateUpdate,
	handleEventUpdateOnResize,
	processConfiguration,
	setCalendarSelectedDate,
	setCalendarView,
	updateEvents
} from './vcf-schedule-x-utils.js';

/**
 * Creates and renders a Schedule-X calendar using provided factories and configuration.
 *
 * @param {HTMLElement} container - The container element holding the calendar.
 * @param {Object} viewFactories - A map of view function names to their factory functions.
 * @param {Object} viewNameMap - A map of view function names to view names (strings).
 * @param {string} configJson - The calendar configuration as JSON.
 * @param {string} calendarsJson - The calendars as JSON.
 * @param {Object} calendarOptions - Additional options.
 */
export function createCommonCalendar(container, viewFactories, viewNameMap, configJson, calendarsJson, calendarOptions = {}) {
	const viewFnNames = JSON.parse(calendarOptions.viewsJson || "[]");
	const config = processConfiguration(configJson, viewNameMap);
	const parsedCalendars = JSON.parse(calendarsJson || "[]");

	const views = viewFnNames
		.map(fnName => viewFactories[fnName])
		.filter(Boolean)
		.map(factory => factory(calendarOptions.resourceConfig)); // optional for resource views

	const eventsServicePlugin = createEventsServicePlugin();
	const resizePlugin = createResizePlugin(calendarOptions.resizeInterval);

	let div = document.getElementById(container.id);

	const calendar = createCalendar({
		views: views,
		calendars: parsedCalendars,
		callbacks: {
			onRangeUpdate(range) {
				updateEvents(div, range);
			},
			beforeRender($app) {
				const range = $app.calendarState.range.value;
				updateEvents(div, range);
			},
			onEventClick(calendarEvent) {
				handleOnEventClick(div, calendarEvent);
			},
			onSelectedDateUpdate(date) {
				handleOnSelectedDateUpdate(div, date);
			},
			/**
			 * Is called when an event is updated through drag and drop or resize
			 * */
			onEventUpdate(updatedEvent) {
				handleEventUpdateOnResize(div, updatedEvent);
			},
		},
		...config
	}, [eventsServicePlugin, resizePlugin]);

	calendar.render(div);
	div.calendar = calendar;
	container.calendar = calendar;
}

/**
 * Sets the current view on the calendar.
 * 
 * @param {HTMLElement} container 
 * @param {string} view 
 * @param {Object} viewNameMap 
 */
export function setView(container, view, viewNameMap) {
	setCalendarView(container.calendar, viewNameMap[view]);
}

/**
 * Sets the selected date on the calendar.
 * 
 * @param {HTMLElement} container 
 * @param {string} selectedDate 
 */
export function setSelectedDate(container, selectedDate) {
	setCalendarSelectedDate(container.calendar, selectedDate);
}

/**
 * Adds an event to the calendar.
 */
export function addEvent(container, calendarEvent) {
	const eventJson = JSON.parse(calendarEvent);
	const eventId = eventJson.id;
	container.calendar.eventsService.add(eventJson);
	container.dispatchEvent(new CustomEvent('calendar-event-added', { detail: { eventId: eventId } }));
}

/**
 * Removes an event from the calendar.
 */
export function removeEvent(container, calendarEventId) {
	container.calendar.eventsService.remove(calendarEventId);
	container.dispatchEvent(new CustomEvent('calendar-event-removed', { detail: { eventId: calendarEventId } }));
}

/**
 * Updates an existing event.
 */
export function updateEvent(container, calendarEvent) {
	const eventJson = JSON.parse(calendarEvent);
	const eventId = eventJson.id;
	container.calendar.eventsService.update(eventJson);
	container.dispatchEvent(new CustomEvent('calendar-event-updated', { detail: { eventId: eventId } }));
}

/**
 * This function adapts navigation logic from the Schedule-X library.
 * Original source: 
 * https://github.com/schedule-x/schedule-x/blob/main/packages/calendar/src/components/header/forward-backward-navigation.tsx
 * 
 * Schedule-X is licensed under the MIT License:
 * https://github.com/schedule-x/schedule-x/blob/main/LICENSE
 */
export function navigateCalendar(calendar, direction) {
	const $app = calendar.$app;
	const selectedView = $app.config.views.value.find(
		view => view.name === $app.calendarState.view.value
	);
	if (!selectedView) return;

	const unitCount = direction === 'forwards'
		? selectedView.backwardForwardUnits
		: -selectedView.backwardForwardUnits;

	const nextDate = selectedView.backwardForwardFn(
		$app.datePickerState.selectedDate.value,
		unitCount
	);

	// minDate / maxDate bounds check
	if ((direction === 'forwards' && $app.config.maxDate.value && nextDate > $app.config.maxDate.value) ||
		(direction === 'backwards' && $app.config.minDate.value && nextDate < $app.config.minDate.value)) {
		return;
	}

	setCalendarSelectedDate(calendar, nextDate);
}
