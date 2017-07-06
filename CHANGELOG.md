### version 2.0.0 (July 6th 2017)

# fix issue 10: https://github.com/stephanenicolas/ormlite-android-gradle-plugin/issues/10 
# fix issue 9: https://github.com/stephanenicolas/ormlite-android-gradle-plugin/issues/9

the ormlite_config filename can be customized via the plugin extension
the file is now generated in the asset folder and this makes it much smoother w.r.t to Android build cycle. Before we were using the resources but we needed the classes to be generatd which was creating a cycle (classes need resources to be compiled). This is now solved.


