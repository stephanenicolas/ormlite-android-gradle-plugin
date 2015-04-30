package com.github.stephanenicolas.ormgap

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.PluginCollection
import org.gradle.api.tasks.compile.JavaCompile

/**
 * ORM LITE ANDROID PLUGIN
 * It will:
 * <ul>
 *   <li> add a task to create the configuration file for ORM LITE
 *   <li> insert it into the build task graph of your android project
 *   <li> let you configure the name of the configuration file ?
 *   <li> work for libraries ?
 *   <li> work for tests ?
 * </ul>*/
public class ORMGAPPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    def hasApp = project.plugins.withType(AppPlugin)
    def hasLib = project.plugins.withType(LibraryPlugin)
    ensureProjectIsAndroidAppOrLib(hasApp, hasLib)

    def extension = getExtension()
    def pluginExtension = getPluginExtension()
    if (extension && pluginExtension) {
      project.extensions.create(extension, pluginExtension)
    }

    final def log = project.logger
    final String LOG_TAG = "ORMGAP"

    final def variants
    if (hasApp) {
      variants = project.android.applicationVariants
    } else {
      variants = project.android.libraryVariants
    }

    configure(project)

    variants.all { variant ->
      log.debug("In variant '${variant.name}'.")

      JavaCompile javaCompile = variant.javaCompile

      FileCollection classpathFileCollection = project.files(project.android.bootClasspath)
      classpathFileCollection += javaCompile.classpath
      classpathFileCollection += project.files(javaCompile.destinationDir)

      String variantName = ((ApplicationVariant) variant).mergedFlavor.name

      def createConfigFileTask = "createORMLiteConfigFile${variant.name.capitalize()}"
      project.task(createConfigFileTask, type: CreateOrmLiteConfigTask) {
        description = "Create an ORM Lite configuration file"
        setSources(project.android.sourceSets[variantName].java.srcDirs[0].canonicalPath)
        setResFolder(project.android.sourceSets[variantName].res.srcDirs[0].canonicalPath)
        setClasspath(classpathFileCollection.asPath)
        into("ormlite_config.txt")
        outputs.upToDateWhen {
          false
        }
      }
      project.tasks.getByName(createConfigFileTask).mustRunAfter(javaCompile)

      log.debug("ORMLite config file creation task installed after compile task.")
      variant.assemble.dependsOn(createConfigFileTask)
      if (!hasLib) {
        variant.install?.dependsOn(createConfigFileTask)
      }
      log.debug("Done with variant '${variant.name}'.")
    }
    log.debug("Done.")
  }

  protected void ensureProjectIsAndroidAppOrLib(PluginCollection<AppPlugin> hasApp,
      PluginCollection<LibraryPlugin> hasLib) {
    if (!hasApp && !hasLib) {
      throw new IllegalStateException("'android' or 'android-library' plugin required.")
    }
  }

  /**
   * Hook to configure the project under build.
   * Can be used to add other extensions, plugins, etc.
   * @param project the project under build.
   */
  protected void configure(Project project) {
    //we use the file build.properties that contains the version of
    //the extension to use. This avoids all problems related to using version x.y.+
    Properties properties = new Properties()
    properties.load(getClass().getClassLoader().getResourceAsStream("build.properties"))
    project.dependencies {
      provided 'com.github.stephanenicolas.ormgap:ormgap-ormlite-extension:' + properties.get("com.github.stephanenicolas.ormgap.version")
    }
  }

  /**
   * @return the name of the class of the plugin extension associated to the project's extension.
   * Can be null, then no extension is created.
   * @see #getExtension()
   */
  private Class getPluginExtension() {
    ORMGAPPluginExtension
  }

  /**
   * @return the extension of the project that this plugin can create.
   * It will be associated to the plugin extension.
   * Can be null, then no extension is created.
   * @see #getPluginExtension()
   */
  private String getExtension() {
    "ormgap"
  }
}
