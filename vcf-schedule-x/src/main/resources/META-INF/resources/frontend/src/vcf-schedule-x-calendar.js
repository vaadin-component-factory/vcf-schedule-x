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
	createCommonCalendar,
	setView,
	setSelectedDate
} from './vcf-schedule-x-base.js';

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
		setTimeout(() =>
			createCommonCalendar(container, viewFactoryMap, viewNameMap, configJson, calendarsJson, {
				viewsJson
			})
		);
	},

	setView(container, view) {
		setView(container, view, viewNameMap);
	},

	setSelectedDate(container, selectedDate) {
		setSelectedDate(container, selectedDate);
	}
};
