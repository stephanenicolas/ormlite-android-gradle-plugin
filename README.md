ORM Lite Android Gradle Plugin [![Build Status](https://travis-ci.org/stephanenicolas/ormlite-android-gradle-plugin.svg?branch=master)](https://travis-ci.org/stephanenicolas/ormlite-android-gradle-plugin)[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.ormgap/ormgap-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.ormgap/ormgap-plugin)
--------
========

A Gradle plugin for Android to generate an ORMLite configuration file and boost DAOs creations.

### Usage 

```xml
//build.gradle

buildscript {
  repositories {
    ...
    mavenCentral()
  }

  dependencies {
    ...
    classpath 'com.github.stephanenicolas.ormgap:ormgap-plugin:1.0.0-SNAPSHOT'
  }
}

apply plugin: 'android'
apply plugin: 'ormgap'
...

```

### Example

An example can be found [in the GH repo](https://github.com/stephanenicolas/ormlite-android-gradle-plugin/tree/master/ormgap-example).

### Benchmarking

Our plan is to make a benchmarking app using th example android app.

For now, we can only give you a number from our experience at Groupon: the average gain, for all devices of our 50 million users is 10 ms per DAO creation. It might not seem much, but for large apps, it makes a difference. In our app, we gained 400 ms with ORMGAP.

### CI

Travis is almost ready at : https://travis-ci.org/stephanenicolas/ormlite-android-gradle-plugin

### Credits

ORMGAP has been possible thanks to [Groupon](http://groupon.com) ! 

<img src="https://pbs.twimg.com/profile_images/428288841082871808/Q114lCq3_400x400.png" alt="Groupon logo" width= "200px" height= "200px"/>

And, yes, [we are hiring Android coders](https://jobs.groupon.com/careers/engineering/).

ORMGAP is part of [our open source effort](https://github.com/groupon). 

License
-------

	Copyright (C) 2015 Stéphane NICOLAS

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	     http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
