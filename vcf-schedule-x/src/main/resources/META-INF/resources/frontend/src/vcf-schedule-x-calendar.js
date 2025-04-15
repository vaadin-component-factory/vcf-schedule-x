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
import { createEventsServicePlugin } from '@schedule-x/events-service';

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

    create: function(container, viewsJson, configJson, calendarsJson) {
        setTimeout(() => this._createCalendar(container, viewsJson, configJson, calendarsJson));
    },

    _createCalendar: function(container, viewsJson, configJson, calendarsJson) {

        const viewFnNames = JSON.parse(viewsJson || "[]");
        const config = this._processConfiguration(configJson);
        const parsedCalendars = JSON.parse(calendarsJson || "[]");

        const views = viewFnNames
            .map(fnName => viewFactoryMap[fnName])
            .filter(Boolean)
            .map(factory => factory());

        const eventsServicePlugin = createEventsServicePlugin();
        
        let div = document.getElementById(container.id);

        // create calendar		 	  
        const calendar = createCalendar({
            views: views,
            calendars: parsedCalendars,
            callbacks: {
                onRangeUpdate(range) {
                    div.$server.updateRange(range.start, range.end);
                }
            },
            ...config
        },
            [eventsServicePlugin]
        )

        calendar.render(div);
        div.calendar = calendar;

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