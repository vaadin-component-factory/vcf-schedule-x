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
	createCalendar, createViewDay,
	createViewMonthAgenda,
	createViewMonthGrid,
	createViewWeek
} from '@schedule-x/calendar';

const viewFactoryMap = {
	createViewDay: createViewDay,
	createViewMonthAgenda: createViewMonthAgenda,
	createViewMonthGrid: createViewMonthGrid,
	createViewWeek: createViewWeek
};

const viewNameMap = {
	createViewDay: createViewDay().name,
	createViewMonthAgenda: createViewMonthAgenda().name,
	createViewMonthGrid: createViewMonthGrid().name,
	createViewWeek: createViewWeek().name
};

window.vcfschedulexcalendar = {

	create: function(container, viewsJson, eventsJson, configJson) {
		setTimeout(() => this._createCalendar(container, viewsJson, eventsJson, configJson));
	},

	_createCalendar: function(container, viewsJson, eventsJson, configJson) {

		const viewFnNames = JSON.parse(viewsJson || "[]");
		const parsedEvents = JSON.parse(eventsJson || "[]");
		const config = this._processConfiguration(configJson);

		const views = viewFnNames
			.map(fnName => viewFactoryMap[fnName])
			.filter(Boolean)
			.map(factory => factory());

		// create calendar		 	  
		const calendar = createCalendar({
			views: views,
			events: parsedEvents,
			...config
		})

		calendar.render(document.getElementById(container.id));

	},

	_processConfiguration(configJson) {
		if (!configJson) return {};

		const parsedConfig = JSON.parse(configJson);

		if (parsedConfig.defaultView) {
			parsedConfig.defaultView = viewNameMap[parsedConfig.defaultView];
		}

		return parsedConfig;
	}
}