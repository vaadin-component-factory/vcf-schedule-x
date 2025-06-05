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

import {
	createHourlyView,
	createDailyView,
	createConfig,
	TimeUnits
} from '@sx-premium/resource-scheduler';

import {
	addEvent,
	createCommonCalendar,
	navigateCalendar,
	removeEvent,
	setView,
	setDate, 
	setFirstDayOfWeek,
	setLocale,
	setViews,
	setDayBoundaries,
	setWeekOptions,
	setCalendars,
	setMinDate,
	setMaxDate,
	setMonthGridOptions,
	updateEvent
} from './vcf-schedule-x-base.js';

import {
	updateResourceSchedulerRange
} from './vcf-schedule-x-utils.js';

import { signal } from "@preact/signals";

import '@sx-premium/resource-scheduler/index.css';
import '@sx-premium/scheduling-assistant/index.css';

const resourceViewFactoryMap = {
	createHourlyView,
	createDailyView
};

const resourceViewNameMap = {
	createHourlyView: createHourlyView().name,
	createDailyView: createDailyView().name
};

window.vcfschedulexresourcescheduler = {
	create(container, viewsJson, configJson, calendarsJson, resourceConfigJson, schedulingAssistantJson) {
		setTimeout(() => {
			const resourceConfig = createConfig();
			this._processResourceSchedulerConfig(resourceConfig, resourceConfigJson);
			
			// attach lazy loading callbacks
			// callback that runs when the user scrolls the daily view
		    resourceConfig.onLazyLoadMonth = (dates) => {
				if (!Array.isArray(dates) || dates.length === 0) return;

				const start = new Date(dates[0]);
				let end;
				
				if (dates.length === 1) {
				  // only one date, extend to the end of that month
				  end = new Date(start);
			      end.setUTCMonth(start.getUTCMonth() + 1, 0); // last day of month
			      end.setUTCHours(23, 59, 59, 999);
				} else {
				  // the last visible date
				  end = new Date(dates[dates.length - 1]);
				}	
				
				updateResourceSchedulerRange(container, { start, end });
			};
		
			// callback that runs when the user scrolls the hourly view
		    resourceConfig.onLazyLoadDate = (dates) => {
				if (!Array.isArray(dates) || dates.length === 0) return;

				const start = new Date(dates[0]);
				let end;
				
				if (dates.length === 1) {
				  // only one date, extend to end of that day
				  end = new Date(dates[0]);
				  end.setUTCHours(23, 59, 59, 999);
				} else {
				  // the last visible date
				  end = new Date(dates[dates.length - 1]);
				}	
				
				updateResourceSchedulerRange(container, { start, end });
		    };
			
			// get scheduling assistant configuration if available
			const schedulingAssistantConfig = schedulingAssistantJson === "{}" ? null : JSON.parse(schedulingAssistantJson);

			createCommonCalendar(container, resourceViewFactoryMap, resourceViewNameMap, configJson, calendarsJson, {
				viewsJson,
				resourceConfig,
				schedulingAssistantConfig
			});
		});
	},

	_processResourceSchedulerConfig(resourceConfig, resourceConfigJson) {
		const parsed = JSON.parse(resourceConfigJson);
		const timeUnits = new TimeUnits();

		this._assignIfExists(resourceConfig, parsed, 'hourWidth');
		this._assignIfExists(resourceConfig, parsed, 'dayWidth');
		this._assignIfExists(resourceConfig, parsed, 'resources', resources =>
			resources.map(resource => ({
				...resource,
				isOpen: signal(resource.isOpen)
			}))
		);
		this._assignIfExists(resourceConfig, parsed, 'resourceHeight');
		this._assignIfExists(resourceConfig, parsed, 'eventHeight');
		this._assignIfExists(resourceConfig, parsed, 'dragAndDrop');
		this._assignIfExists(resourceConfig, parsed, 'resize');
		this._assignIfExists(resourceConfig, parsed, 'infiniteScroll');
		this._assignIfExists(resourceConfig, parsed, 'initialHours', raw => {
			const [start, end] = this._parseInitialRange(raw);
			return timeUnits.getDayHoursBetween(start, end);
		});
		this._assignIfExists(resourceConfig, parsed, 'initialDays', raw => {
			const [start, end] = this._parseInitialRange(raw);
			return timeUnits.getDaysBetween(start, end);
		});
	},

	_parseInitialRange(value) {
		if (typeof value === 'string') {
			const [start, end] = value.split(',').map(s => s.trim());
			return [start, end];
		}
		return [];
	},

	_assignIfExists(target, source, key, transform = v => v) {
		const raw = source[key];
		if (raw !== undefined && raw !== '') {
			target[key].value = transform(raw);
		}
	},

	setView(container, view) {
		setView(container, view, resourceViewNameMap);
	},

	setDate(container, selectedDate) {
		setDate(container, selectedDate);
	},
	
	setFirstDayOfWeek(container, firstDayOfWeek) {
		setFirstDayOfWeek(container, firstDayOfWeek);
	},
	
	setLocale(container, locale) {
		setLocale(container, locale);
	},
	
	setViews(container, viewsJson) {
		setViews(container, viewsJson, resourceViewFactoryMap);
	},
	
	setDayBoundaries(container, dayBoundariesJson) {
		setDayBoundaries(container, dayBoundariesJson);
	},
	
	setWeekOptions(container, weekOptionsJson) {
		setWeekOptions(container, weekOptionsJson);
	},
	
	setCalendars(container, calendarsJson) {
		setCalendars(container, calendarsJson);
	},
	
	setMinDate(container, minDate){
		setMinDate(container, minDate);
	},
	
	setMaxDate(container, maxDate){
		setMaxDate(container, maxDate);
	},
	
	setMonthGridOptions(container, monthGridOptionsJson){
		setMonthGridOptions(container, monthGridOptionsJson);
	},
	
	addEvent(container, calendarEvent) {
		addEvent(container, calendarEvent);
	},
	
	removeEvent(container, calendarEventId) {
		removeEvent(container, calendarEventId);
	},
	
	updateEvent(container, calendarEvent) {
		updateEvent(container, calendarEvent);
	},
	
	navigateForwards(container) {
		navigateCalendar(container.calendar, 'forwards');
	},
	
	navigateBackwards(container) {
		navigateCalendar(container.calendar, 'backwards');
	},
};