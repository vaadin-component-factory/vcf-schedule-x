# Schedule-X Add-on for Vaadin Flow

This is a Vaadin Flow add-on based on the [Schedule-X](https://schedule-x.dev/) JavaScript component.

It allows you to build an event calendar with multiple customizable views to easily display, organize, and coordinate events.

⚠️ **Important**: This add-on **requires a Schedule-X Premium Subscription**. 
The Resource View feature and several used plugins (e.g., event drawing, iCal support, resource views, etc.) are distributed as private npm packages.

To use this component, you must:
1. [Subscribe to Schedule-X Premium](https://schedule-x.dev/premium)
2. Configure an `.npmrc` file as explained in the [Schedule-X Premium installation guide](https://schedule-x.dev/docs/calendar/installing-premium#1-npmrc)

This component is part of the Vaadin Component Factory.

## Features

- Supported views:
  - Day
  - Week
  - Month Grid
  - Month Agenda
  - List View (since version 2.0.0)
  - Resource View (*)
  
- Java Configuration API:
  - Default view and initial date
  - Week and month view customization (via WeekOptions, MonthGridOptions)
  - Define visible day/time boundaries (DayBoundaries)
  - First day of the week
  - Minimum and maximum date limits
  - Resize/drag interval granularity
  - Scroll controller config
  - Current time indicator customization
  - Draw options
  - ICal integration to import events from an iCalendar source
  
- Calendar Interaction:
  - Programmatically set view, date, first day of week, day boundaries, calendars, etc.
  - Implementation to allow navigate forwards or backwards in the views
  - Scroll to time
  - Configuration updates
  
- Event Support:
  - Create, update, and remove events from server side
  - Define title, location, description, and attendees
  - Assign events to calendars (calendarId)
  - Assign events to resources (resourceId)
  - Recurring events using RRULE (RecurrenceRule) and excluded date support
  - Drag & drop events
  - Resize events
  - Draw events (*)
  
- Lazy loading:
  - Supports lazy loading using CallbackDataProvider

- Resource Scheduler Support(*):
  - Display resources in a time grid (hourly and daily views available)
  - Resource row and event height control
  - Control initial visible hours/days
  - Enable/disable drag and resize
  - Infinite scrolling  
  
- Scheduling Assistant Plugin (*):
  - Conflict detection and available time slot visualization
  - The Scheduling Assistant can only be used with Hourly View. 

- Full Event Listener Support:
  - Event click handling (CalendarEventClickEvent)
  - Event add/update/remove notifications
  - Drag/resize updates (EventUpdateEvent)
  - Selected date change (SelectedDateUpdateEvent)
  - View and date update (CalendarViewAndDateChangeEvent)
  
(*) These features are part of the [Premium Schedule-X package](https://schedule-x.dev/premium).

## Examples

### Calendar with Month View
![01-vcf-schedule-x-month-view](https://github.com/user-attachments/assets/61ec0b82-5267-4449-b80c-c9f2be26bbc3)

### Calendar with Week View
![02-vcf-schedule-x-week-view](https://github.com/user-attachments/assets/c2159807-f8db-44c0-be1c-fc7fd0d8ed41)

### Calendar with Day View
![03-vcf-schedule-x-day-view](https://github.com/user-attachments/assets/f2397f4f-79ad-4b79-9e6c-8deec0031e91)

### Calendar with Agenda View
![04-vcf-schedule-x-agenda-view](https://github.com/user-attachments/assets/56d23718-210d-4fc8-9441-4a4288ee40dc)

### Calendar with List View (since version 2.0.0)
<img width="1491" height="614" alt="schedule-x-list-view-img" src="https://github.com/user-attachments/assets/63ca74bf-8666-48f0-914c-fbbc27795801" />

### Resource Scheduler with Hourly View
![05-vcf-schedule-x-resource-scheduler-hourly](https://github.com/user-attachments/assets/9bcae1fc-a191-4f5d-be9a-27ed9bc1d24f)

### Resource Scheduler with Daily View
![06-vcf-schedule-x-resource-scheduler-daily](https://github.com/user-attachments/assets/70a9649f-f8e9-468f-b6eb-b87eaf71ed6e)

### Resource Scheduler with Scheduling Assistant (Hourly view only)
![07-vcf-schedule-x-scheduling-assistant](https://github.com/user-attachments/assets/77fe966b-3bf7-42de-9f40-81e590db8f3d)

## Installing the component
Run from the command line:
- `mvn clean install -DskipTests`

## Running the component demo
1. Create an `.npmrc` file as described in the [Schedule-X installation guide](https://schedule-x.dev/docs/calendar/installing-premium#1-npmrc).
   Place this file in the root folder of the `vcf-schedule-x-demo` module.

2. Run from the command line:
- `mvn  -pl vcf-schedule-x-demo -Pwar install jetty:run`

3. Then navigate to `http://localhost:8080/`

## Using the component in a Flow application
To use the component in an application using maven,
add the following dependency to your `pom.xml`:
```
<dependency>
    <groupId>org.vaadin.addons.componentfactory</groupId>
    <artifactId>vcf-schedule-x</artifactId>
    <version>${component.version}</version>
</dependency>
```

## How to Use
Usage examples are available in the vcf-schedule-x-demo module.

## Profiles
### Profile "directory"
This profile, when enabled, will create the zip file for uploading to Vaadin's directory

### Profile "production"
This profile, when enabled, will execute a production build for the demo


## Flow documentation
Documentation for Vaadin Flow can be found in [Flow documentation](https://vaadin.com/docs/latest/flow).

## License
Distributed under Apache Licence 2.0. 

### Sponsored development
Major pieces of development of this add-on has been sponsored by multiple customers of Vaadin. Read more about Expert on Demand at: [Support](https://vaadin.com/support) and [Pricing](https://vaadin.com/pricing).
