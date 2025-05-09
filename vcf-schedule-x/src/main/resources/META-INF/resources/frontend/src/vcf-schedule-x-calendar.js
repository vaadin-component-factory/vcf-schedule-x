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
	createViewDay, createViewMonthAgenda,
	createViewMonthGrid, createViewWeek
} from '@schedule-x/calendar';

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
import { createDrawPlugin } from "@sx-premium/draw";

const viewFactoryMap = {
	createViewDay,
	createViewMonthAgenda,
	createViewMonthGrid,
	createViewWeek
};

const viewNameMap = {
	createViewDay: createViewDay().name,
	createViewMonthAgenda: createViewMonthAgenda().name,
	createViewMonthGrid: createViewMonthGrid().name,
	createViewWeek: createViewWeek().name
};

window.vcfschedulexcalendar = {
	create(container, viewsJson, configJson, calendarsJson) {
        const parsedConfig = JSON.parse(configJson);
        let drawSnapDuration = parsedConfig.drawOptions.snapDrawDuration;
        const drawPlugin = createDrawPlugin({
          // (Optional) callback that runs on mouseup after drawing an event, before calling onFinishDrawing
          onBeforeFinishDrawing: (async (event) => {
            let validation = await container.$server.validateDrawnEvent(event.id, event.start, event.end);
            if (validation) {
                container.$server.addEvent(event);
            }
            return false;
          }),
         
          // (Optional) configure the intervals, in minutes, at which a time grid-event can be drawn. Valid values: 15, 30, 60
          snapDuration: drawSnapDuration
        });
        setTimeout(() =>
            createCommonCalendar(container, viewFactoryMap, viewNameMap, configJson, calendarsJson, {
                viewsJson
            }, drawPlugin)
        );
	},

	setView(container, view) {
		setView(container, view, viewNameMap);
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
		setViews(container, viewsJson, viewFactoryMap);
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
	
	/**
	 * Scroll to a specific time. Only available for week and day views.
	 */
	scrollTo(container, time) {
		container.calendar.$app.config.plugins.scrollController.scrollTo(time);
	}
		
};
