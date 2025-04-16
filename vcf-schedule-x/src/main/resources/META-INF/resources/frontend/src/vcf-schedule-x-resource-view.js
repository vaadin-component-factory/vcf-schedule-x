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
import { createHourlyView, createDailyView, createConfig, TimeUnits } from '@sx-premium/resource-scheduler';
import { createEventsServicePlugin } from '@schedule-x/events-service';
import { signal } from "@preact/signals";

const resourceViewFactoryMap = {
	createHourlyView,
	createDailyView
};

const resourceViewNameMap = {
	createHourlyView: createHourlyView().name,
	createDailyView: createDailyView().name
};

window.vcfschedulexresourceview = {

	create: function(container, viewsJson, configJson, calendarsJson, resourceConfigJson) {
		setTimeout(() => this._createResourceView(container, viewsJson, configJson, calendarsJson, resourceConfigJson));
	},

	_createResourceView: function(container, viewsJson, configJson, calendarsJson, resourceConfigJson) {

		const viewFnNames = JSON.parse(viewsJson || "[]");
		const config = this._processConfiguration(configJson);
		const parsedCalendars = JSON.parse(calendarsJson || "[]");

		const resourceConfig = createConfig();
		this._processResourceSchedulerConfig(resourceConfig, resourceConfigJson);

		const views = viewFnNames
			.map(fnName => resourceViewFactoryMap[fnName])
			.filter(Boolean)
			.map(factory => factory(resourceConfig));

		const eventsServicePlugin = createEventsServicePlugin();

		let div = document.getElementById(container.id);

		// create calendar		 	  
		const calendar = createCalendar({
			views: views,
			calendars: parsedCalendars,
			callbacks: {
				onRangeUpdate(range) {
					div.$server.updateRange(range.start, range.end);
				},
				beforeRender($app) {
					const range = $app.calendarState.range.value;
					div.$server.updateRange(range.start, range.end);
				},
			},
			...config
		},
			[eventsServicePlugin]
		)

		calendar.render(div);
		div.calendar = calendar;
	},

	/** 
	 * Parse recieved json for ResourceSchedulerConfig
	 */
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
		this._assignIfExists(
			resourceConfig,
			parsed,
			'initialHours',
			raw => {
				const [start, end] = this._parseInitialRange(raw);
				return timeUnits.getDayHoursBetween(start, end);
			}
		);
		this._assignIfExists(
			resourceConfig,
			parsed,
			'initialDays',
			raw => {
				const [start, end] = this._parseInitialRange(raw);
				return timeUnits.getDaysBetween(start, end);
			}
		);
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

	_processConfiguration(configJson) {
		if (!configJson) return {};

		const parsedConfig = JSON.parse(configJson);

		if (parsedConfig.defaultView) {
			parsedConfig.defaultView = resourceViewNameMap[parsedConfig.defaultView];
		}

		return parsedConfig;
	}
}