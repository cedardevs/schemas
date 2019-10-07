[![Build Status](https://circleci.com/gh/cedardevs/schemas.svg?style=svg)](https://circleci.com/gh/cedardevs/schemas)
[![codecov](https://codecov.io/gh/cedardevs/schemas/branch/master/graph/badge.svg)](https://codecov.io/gh/cedardevs/schemas)
[![](https://jitpack.io/v/cedardevs/schemas.svg)](https://jitpack.io/#cedardevs/schemas)


# CEDAR Schemas

The purpose of this project is to provide a common, public repository for use with our other metadata storage, analysis, and search tools. These schemas, and associated code, allow not only our system components to interface cleanly, but also enable outside users to build applications that can as well.

## Avro
Avro schemas are available for interfacing with the Kafka topics in the PSI project. Additionally, a set of Avro schemas is available for a single geometry defined in GeoJSON. Using the [Gradle Avro Plugin](https://github.com/commercehub-oss/gradle-avro-plugin), we have auto-generated Java files corresponding to the Avro objects. These generated classes have been included in the source code.


## JSON Schema
JSON Schema documents are available to document interfaces with both PSI and CEDAR Search projects, as well as a single geometry GeoJSON. In addition to details provided both here and in the Avro schemas, field value verification is possible using JSON Schemas (see the GeoJSONAvroSpec for an example).

## JSON-LD
Coming soon.

## Adding The Schemas Repo as a Dependency
In a gradle build project, add the following to your `build.gradle` file to include schemas in your dependencies:
```
   repositories {
        // Add the jitpack repo to the end of your list of repositories
        maven { url "https://jitpack.io" }
   }
   dependencies {
         implementation 'com.github.cedardevs:schemas:{tag}'
   }
```
When using schemas in development, you can use the `master-SNAPSHOT` tag to pull the latest version from the master branch. This snapshot tag will work with any branch, however.
