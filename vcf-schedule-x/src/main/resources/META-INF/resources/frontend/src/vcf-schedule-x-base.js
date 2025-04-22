import { createCalendar } from '@schedule-x/calendar';
import { createEventsServicePlugin } from '@schedule-x/events-service';
import {
	handleOnEventClick,
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
 * @param {Object} calendarOptions - Additional options, like viewsJson or resourceConfig.
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
		},
		...config
	}, [eventsServicePlugin]);

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
