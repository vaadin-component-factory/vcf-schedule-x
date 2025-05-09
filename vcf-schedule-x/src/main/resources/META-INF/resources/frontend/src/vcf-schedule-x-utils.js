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
	div.$server.updateRange(range.start, range.end);
}

export function setSelectedView(calendar, viewName) {
	calendar.$app.config.plugins.calendarControls.setView(viewName);
}

export function setSelectedDate(calendar, selectedDate) {
	calendar.$app.config.plugins.calendarControls.setDate(selectedDate);
}

export function handleOnEventClick(div, calendarEvent) {
	div.$server.onCalendarEventClick(calendarEvent.id);
}

export function handleOnSelectedDateUpdate(div, date) {
	div.$server.onSelectedDateUpdate(date);
}

/**
 * This handles event updates on resize or dnd
 */
export function handleEventUpdate(div, updatedEvent) {
	div.$server.onEventUpdate(updatedEvent.id, updatedEvent.start, updatedEvent.end);
}

export function updateFirstDayOfWeek(calendar, firstDayOfWeek) {
	calendar.$app.config.plugins.calendarControls.setFirstDayOfWeek(firstDayOfWeek);
}

export function updateLocale(calendar, locale) {
	calendar.$app.config.plugins.calendarControls.setLocale(locale);
}

export function updateViews(calendar, viewFnNames, viewFactories) {
	const $app = calendar.$app;
	const views = processViews(viewFnNames, viewFactories, $app.resourceViewConfig)
	calendar.$app.config.plugins.calendarControls.setviews(views);
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
	calendar.$app.config.plugins.calendarControls.setMinDate(minDate);
}

export function updateMaxDate(calendar, maxDate) {
	calendar.$app.config.plugins.calendarControls.setMaxDate(maxDate);
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
		container.dispatchEvent(new CustomEvent('scheduling-assistant-update', {
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

