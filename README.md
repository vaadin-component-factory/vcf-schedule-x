# Schedule-X Add-on for Vaadin Flow

This is a flow add-on based on the Schedule-X javascript library.

This component is part of Vaadin Component Factory.

## Features

* Support for Calendar and Resources views

## Running the component demo
Run from the command line:
- `mvn  -pl vcf-schedule-x-demo -Pwar install jetty:run`

Then navigate to `http://localhost:8080/`

## Installing the component
Run from the command line:
- `mvn clean install -DskipTests`

## Profiles
### Profile "directory"
This profile, when enabled, will create the zip file for uploading to Vaadin's directory

### Profile "production"
This profile, when enabled, will execute a production build for the demo

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
Add code samples in this section

## Flow documentation
Documentation for Vaadin Flow can be found in [Flow documentation](https://vaadin.com/docs/latest/flow).

## License
Distributed under Apache Licence 2.0. 

### Sponsored development
Major pieces of development of this add-on has been sponsored by multiple customers of Vaadin. Read more about Expert on Demand at: [Support](https://vaadin.com/support) and [Pricing](https://vaadin.com/pricing).
