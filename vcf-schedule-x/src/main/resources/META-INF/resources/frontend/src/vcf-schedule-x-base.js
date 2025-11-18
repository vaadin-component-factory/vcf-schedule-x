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

import 'temporal-polyfill/global';
import { createCalendar } from '@schedule-x/calendar';
import { createCalendarControlsPlugin } from '@schedule-x/calendar-controls';
import { createCurrentTimePlugin } from '@schedule-x/current-time'
import { createDragAndDropPlugin } from '@schedule-x/drag-and-drop';
import { createEventRecurrencePlugin, createEventsServicePlugin } from "@schedule-x/event-recurrence";
import { createResizePlugin } from '@schedule-x/resize';
import { createScrollControllerPlugin } from '@schedule-x/scroll-controller';
import { createSchedulingAssistant } from '@sx-premium/scheduling-assistant';
import { createIcalendarPlugin } from '@schedule-x/ical';
import { addDays } from '@schedule-x/shared';
import {
	getZonedDateTime,
	handleOnEventClick,
	handleOnSelectedDateUpdate,
	handleEventUpdate,
	processViews,	
	setSelectedDate,
	setSelectedView,
	subscribeToSchedulingAssistantUpdates,
	updateFirstDayOfWeek,
	updateLocale,
	updateTimeZone,
	updateViews,
	updateDayBoundaries,
	updateWeekOptions,
	updateCalendars,
	updateMinDate,
	updateMaxDate,
	updateMonthGridOptions,
	updateEvents
} from './vcf-schedule-x-utils.js';

/**
 * Creates and renders a Schedule-X calendar using provided factories and configuration.
 *
 * @param {HTMLElement} container - The container element holding the calendar.
 * @param {Object} viewFactories - A map of view function names to their factory functions.
 * @param {string} config - The calendar configuration.
 * @param {string} calendarsJson - The calendars as JSON.
 * @param {Object} calendarOptions - Additional options.
 */
export function createCommonCalendar(container, viewFactories, config, calendarsJson, calendarOptions = {}) {
		
	const viewFnNames = JSON.parse(calendarOptions.viewsJson || "[]");
	const parsedCalendars = JSON.parse(calendarsJson || "[]");

	const views = processViews(viewFnNames, viewFactories, calendarOptions.resourceConfig);

	const eventsServicePlugin = createEventsServicePlugin();
	const resizePlugin = createResizePlugin(config.resizeInterval);
	const dragAndDropPlugin = createDragAndDropPlugin(config.dragAndDropInterval);
	const scrollControllerPlugin = createScrollControllerPlugin(config.scrollControllerConfig);
	const calendarControlsPlugin = createCalendarControlsPlugin();
   	const recurrencePlugin = createEventRecurrencePlugin();
	
	let div = container;
	
    let plugins = [calendarControlsPlugin, dragAndDropPlugin, eventsServicePlugin, recurrencePlugin, resizePlugin, scrollControllerPlugin];
    
    // Add Draw plugin if applies
    const drawPlugin = calendarOptions.drawPlugin;    
    if (drawPlugin) plugins.push(drawPlugin);
    
     // Add iCal plugin if applies
    if(config.iCal){
		 const icalendarPlugin = createIcalendarPlugin({data:config.iCal.iCal});
		 plugins.push(icalendarPlugin);
	}
	
    // Add Current Time Indicator plugin if applies
    if(config.currentTimeIndicatorConfig) {
		const currentTimeIndicatorPlugin = createCurrentTimePlugin(config.currentTimeIndicatorConfig);
		plugins.push(currentTimeIndicatorPlugin);	
	}

	// Add Scheduling Assistant plugin if applies
	let schedulingAssistantConfigured = false;
	if(calendarOptions.resourceConfig && calendarOptions.schedulingAssistantConfig){
		const schedulingAssitantPlugin = createSchedulingAssistant(calendarOptions.schedulingAssistantConfig);
		plugins.push(schedulingAssitantPlugin);	
		schedulingAssistantConfigured = true;
	}

	const calendar = createCalendar({
		views: views,
		calendars: parsedCalendars,
		callbacks: {
			onRangeUpdate(range) {
				updateEvents(div, range);
			},
			beforeRender($app) {
				
				// Wrap the original setView to be able to notify server-side when view or date changes on client-side
                const originalSetView = $app.calendarState.setView;
                $app.calendarState.setView = function(viewName, selectedDate) {
                    const result = originalSetView.call(this, viewName, selectedDate);
            
                    // Dispatch event so server side can listen
                    container.parentElement.dispatchEvent(new CustomEvent('calendar-state-view-date-updated', {
                        detail: {
                            viewName,
                            selectedDate
                        }
                    }));
                    
                    return result;
                };
                
                // Update events
				const range = $app.calendarState.range.value;
				updateEvents(div, range);
			},
			onEventClick(calendarEvent) {
				handleOnEventClick(div, calendarEvent);
			},
			/**
		     * Is called when the selected date is updated
		     * */
			onSelectedDateUpdate(date) {
				handleOnSelectedDateUpdate(div, date);
			},
			/**
		     * Runs after the calendar is rendered
		     * */
		    onRender($app) {
		      handleOnSelectedDateUpdate(div, $app.datePickerState.selectedDate.value);
		    },			
			/**
			 * Is called when an event is updated through drag and drop or resize.
			 * */
			onEventUpdate(updatedEvent) {
				handleEventUpdate(div, updatedEvent);
			},
           onMouseDownDateTime(dateTime, mouseDownEvent) {
            if(drawPlugin) {
                 drawPlugin.drawTimeGridEvent(dateTime, mouseDownEvent, {
                   title: config.drawOptions.defaultTitle
                 })
             };
           },
           onMouseDownMonthGridDate(date, _mouseDownEvent) {
            if(drawPlugin) {
                 drawPlugin.drawMonthGridEvent(date, {
                   title: config.drawOptions.defaultTitle
                 })
             };
           },
           onMouseDownDateGridDate(date, mouseDownEvent) {
            if(drawPlugin) {
                 drawPlugin.drawDateGridEvent(date, mouseDownEvent, {
                   title: config.drawOptions.defaultTitle
                 })
             };
           }
		},
		...config
	}, plugins);

	calendar.render(div);
	div.calendar = calendar;
	container.calendar = calendar;
	
	// Dispatch event to know calendar was rendered
	container.parentElement.dispatchEvent(new CustomEvent('calendar-rendered'));
	
	// Subscribe to Scheduling Assistant updates if applies
	if(schedulingAssistantConfigured){
		subscribeToSchedulingAssistantUpdates(container);
	}
}

/**
 * Changes the calendar view.
 * 
 * @param {HTMLElement} container 
 * @param {string} view 
 * @param {Object} viewNameMap 
 */
export function setView(container, view, viewNameMap) {
	setSelectedView(container.calendar, viewNameMap[view]);	
}

/**
 * Sets the date of the calendar.
 * 
 * @param {HTMLElement} container 
 * @param {string} selectedDate 
 */
export function setDate(container, selectedDate) {
	setSelectedDate(container.calendar, selectedDate);
}

/**
 * Sets the first day of the week for the calendar. 
 * Value must be between 0 and 6 where 0 is Sunday, 1 is Monday etc.
 * 
 * @param {HTMLElement} container 
 * @param {string} firstDayOfWeek 
 * 
 */
export function setFirstDayOfWeek(container, firstDayOfWeek) {
	updateFirstDayOfWeek(container.calendar, firstDayOfWeek);
}

/**
 * Sets the locale of the calendar.
 * 
 * @param {HTMLElement} container 
 * @param {string} locale 
 * 
 */
export function setLocale(container, locale) {
	updateLocale(container.calendar, locale);
}

export function setTimeZone(container, timeZone) {
	updateTimeZone(container.calendar, timeZone);
}

/**
 * Sets the available views for the calendar. The views to be set must include the currently active view name. 
 * At least one view must be passed into this function.
 * 
 * @param {HTMLElement} container 
 * @param {string} viewsJson 
 * @param {Object} viewFactories
 * 
 */
export function setViews(container, viewsJson, viewFactories) {
	const viewFnNames = JSON.parse(viewsJson || "[]");
	updateViews(container.calendar, viewFnNames, viewFactories);	
}

/**
 * Sets the day boundaries of the calendar.
 * 
 * @param {HTMLElement} container 
 * @param {string} dayBoundariesJson 
 * 
 */
export function setDayBoundaries(container, dayBoundariesJson) {
	updateDayBoundaries(container.calendar, dayBoundariesJson);
}

/**
 * Sets the week options of the calendar.
 * 
 * @param {HTMLElement} container 
 * @param {string} weekOptionsJson 
 * 
 */
export function setWeekOptions(container, weekOptionsJson) {
	updateWeekOptions(container.calendar, weekOptionsJson);
}

/** 
 * Sets the available calendars to be displayed in the calendar.
 * 
 * @param {HTMLElement} container 
 * @param {string} calendarsJson  
 * 
 */
export function setCalendars(container, calendarsJson) {
	const calendars = JSON.parse(calendarsJson);
	updateCalendars(container.calendar, calendars);
}

/**
 * Sets the min date for the calendar navigation.
 * 
 * @param {HTMLElement} container 
 * @param {string} minDate 
 * 
 */
export function setMinDate(container, minDate) {
	updateMinDate(container.calendar, minDate);
}

/**
 * Sets the max date for the calendar navigation.
 * 
 * @param {HTMLElement} container 
 * @param {string} maxDate 
 * 
 */
export function setMaxDate(container, maxDate) {
	updateMaxDate(container.calendar, maxDate);
}

/**
 * Sets the week options of the calendar.
 * 
 * @param {HTMLElement} container 
 * @param {string} monthGridOptionsJson 
 * 
 */
export function setMonthGridOptions(container, monthGridOptionsJson) {
	updateMonthGridOptions(container.calendar, monthGridOptionsJson);
}

/**
 * Adds an event to the calendar.
 */
export function addEvent(container, calendarEvent) {
	const eventJson = JSON.parse(calendarEvent);
	if(eventJson.start) {
		eventJson.start = getZonedDateTime(container, eventJson.start);
	}
	if(eventJson.end) {
		eventJson.end = getZonedDateTime(container, eventJson.end);
	}
	const eventId = eventJson.id;
	container.calendar.eventsService.add(eventJson);
	container.parentElement.dispatchEvent(new CustomEvent('calendar-event-added', { detail: { eventId: eventId } }));
}

/**
 * Removes an event from the calendar.
 */
export function removeEvent(container, calendarEventId) {
	container.calendar.eventsService.remove(calendarEventId);
	container.parentElement.dispatchEvent(new CustomEvent('calendar-event-removed', { detail: { eventId: calendarEventId } }));
}

/**
 * Updates an existing event.
 */
export function updateEvent(container, calendarEvent) {
	const eventJson = JSON.parse(calendarEvent);
	if(eventJson.start) {
		eventJson.start = getZonedDateTime(container, eventJson.start);
	}
	if(eventJson.end) {
		eventJson.end = getZonedDateTime(container, eventJson.end);
	}
	const eventId = eventJson.id;
	container.calendar.eventsService.update(eventJson);
	container.parentElement.dispatchEvent(new CustomEvent('calendar-event-updated', { detail: { eventId: eventId } }));
}

export function onUpdateRange(container, events, start, end){
	if (!container || !container.calendar) {
        return;
    } 
		
    if (container.calendar.$app.config.plugins.ICalendarPlugin){
		const parsedStart = getZonedDateTime(container, start);
		const parsedEnd = getZonedDateTime(container, end);
		container.calendar.$app.config.plugins.ICalendarPlugin.between(parsedStart, parsedEnd);
		    
		let allEvents = container.calendar.eventsService.getAll();
	    allEvents.forEach(event => {
	      event._options = {};
	      event._options.disableDND = true;
	      event._options.disableResize = true;
	      container.calendar.eventsService.update(event);
	    });
		
		JSON.parse(events).forEach(event => {
			if(event.start) {
	          event.start = getZonedDateTime(container, event.start);
	        }
		    if(event.end) {
		      event.end = getZonedDateTime(container, event.end);
		    }    
			container.calendar.eventsService.add(event);
		});
    } else {
		const eventsJson = JSON.parse(events);
	    eventsJson.forEach(event => {
	      if(event.start) {
	         event.start = getZonedDateTime(container, event.start);
	      }
	      if(event.end) {
	         event.end = getZonedDateTime(container, event.end);
	      }    
	    });    
	    container.calendar.eventsService.set(eventsJson);
    } 
	
    if(container.calendar.$app.config.plugins.eventRecurrence){
		 const parsedStart = getZonedDateTime(container, start);
		 const parsedEnd = getZonedDateTime(container, end);
	     container.calendar.$app.config.plugins.eventRecurrence.onRangeUpdate({parsedStart, parsedEnd})
    }        
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
	const views = $app.config.views.value;
	const selectedView = views.find(
		view => view.name === $app.calendarState.view.value
	);
	
	if (!selectedView) return;
				
	if(selectedView.name === "hourly"){
		selectedView.backwardForwardFn = addDays;
	}	

	const unitCount = direction === 'forwards'
		? selectedView.backwardForwardUnits
		: -selectedView.backwardForwardUnits;

	const nextDate = selectedView.backwardForwardFn(
		$app.datePickerState.selectedDate.value,
		unitCount
	);

	// minDate / maxDate bounds check
	if ((direction === 'forwards' && $app.config.maxDate.value && Temporal.PlainDate.compare(nextDate, $app.config.maxDate.value) > 0) ||
		(direction === 'backwards' && $app.config.minDate.value && Temporal.PlainDate.compare($app.config.minDate.value, nextDate) > 0)) {
		return;
	}

	setSelectedDate(calendar, nextDate);
}
