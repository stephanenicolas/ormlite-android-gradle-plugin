### version 3.0.4 (TBR)

### version 3.0.3 (Oct. 7th 2017)

* fix issue 20: https://github.com/stephanenicolas/ormlite-android-gradle-plugin/issues/20
ORM GAP now works with gradle 4.2.1

### version 3.0.2 (skipped)

### version 3.0.1 (September 30th 2017)

* fix issue 18: https://github.com/stephanenicolas/ormlite-android-gradle-plugin/issues/18
* fix issue 16: https://github.com/stephanenicolas/ormlite-android-gradle-plugin/issues/16
ORM GAP now adds the extension as `compileOnly`

### version 3.0.0 (September 29th 2017)

* fix issue 14: https://github.com/stephanenicolas/ormlite-android-gradle-plugin/issues/14
ORM GAP is now incremental and cacheable and uses gradle 4.1
 
### version 2.00 (July 6th 2017)

* fix issue 10: https://github.com/stephanenicolas/ormlite-android-gradle-plugin/issues/10 
* fix issue 9: https://github.com/stephanenicolas/ormlite-android-gradle-plugin/issues/9

the ormlite_config filename can be customized via the plugin extension
the file is now generated in the asset folder and this makes it much smoother w.r.t to Android build cycle. Before we were using the resources but we needed the classes to be generatd which was creating a cycle (classes need resources to be compiled). This is now solved.


