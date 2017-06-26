ORM Lite Android Gradle Plugin [![Build Status](https://travis-ci.org/stephanenicolas/ormlite-android-gradle-plugin.svg?branch=master)](https://travis-ci.org/stephanenicolas/ormlite-android-gradle-plugin)[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.ormgap/ormgap-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.ormgap/ormgap-plugin)
--------
========

A Gradle plugin for Android to generate an ORMLite configuration file and boost DAOs creations.

As of version 1.0.13, ORM GAP is fully incremental and get executed only when classes using ormlite change.

### Usage 

```groovy
//build.gradle

buildscript {
  repositories {
    ...
    mavenCentral()
  }

  dependencies {
    ...
    classpath 'com.github.stephanenicolas.ormgap:ormgap-plugin:x.y.z'
  }
}

apply plugin: 'android'
apply plugin: 'ormgap'
...

```

You will then need to create your database using the ORMLite config file that will be generated during your build (note : you first need to boostrap the system, get a file generated, then reference it.)

```java
public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
}
```

If you use DAOs : you will need to use the second contructor of ORMLite's DAO class : 
```java
public MyDao(ConnectionSource connectionSource, DatabaseTableConfig tableConfig)
   throws SQLException {
   super(connectionSource, tableConfig);
}
```

You're all set. 
See [ORM Lite docs](http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite.html#Top) for further instructions.


### Example

An example can be found [in the GH repo](https://github.com/stephanenicolas/ormlite-android-gradle-plugin/tree/master/ormgap-example).

### How does it work ? 

We basically just automated a technique that is considered the [best practice for ORM Lite on Android](http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Config-Optimization) : it uses a configuration file, genereated at build time, so that ORMLite doesn't scan annotations.

ORMGAP will do the following to your build : 
* create a task for each variant to generate the ORMLite configuration file (this is customizable, TODO explain the plugin extension).
* we also add a provided dependency to your build that contains our forked utility class. This should disappear in a close future, as soon as we submit a PR to ORM Lite and a new version is released... TODO : submit a PR with ORMLiteConfigUtil changes.

### Benchmarking

Our plan is to make a benchmarking app using th example android app.

For now, we can only give you a number from our experience at Groupon: the average gain, for all devices of our 50 million users is 10 ms per DAO creation. It might not seem much, but for large apps, it makes a difference. In our app, we gained 400 ms with ORMGAP.

### CI

Travis is almost ready at : https://travis-ci.org/stephanenicolas/ormlite-android-gradle-plugin

### Credits

ORMGAP has been possible thanks to [Groupon](http://groupon.com) ! 

<img src="https://avatars2.githubusercontent.com/u/206233?v=3&s=70" alt="Groupon logo"/>

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
