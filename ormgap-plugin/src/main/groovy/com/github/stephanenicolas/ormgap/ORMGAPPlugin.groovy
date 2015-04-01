package com.github.stephanenicolas.ormgap

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
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
    final String LOG_TAG = this.getClass().getName()

    final def variants
    if (hasApp) {
      variants = project.android.applicationVariants
    } else {
      variants = project.android.libraryVariants
    }

    configure(project)

    variants.all { variant ->
      log.debug(LOG_TAG, "Transforming classes in variant '${variant.name}'.")

      JavaCompile javaCompile = variant.javaCompile
      FileCollection classpathFileCollection = project.files(project.android.bootClasspath)
      classpathFileCollection += javaCompile.classpath

      def transformTask = "transform${transformerClassName}${variant.name.capitalize()}"
      project.task(transformTask, type: TransformationTask) {
        description = "Transform a file using ${transformerClassName}"
        destinationDir = project.file(transformationDir)
        from("${javaCompile.destinationDir.path}")
        transformation = transformer
        classpath = classpathFileCollection
        outputs.upToDateWhen {
          false
        }
        eachFile {
          log.debug(LOG_TAG, "Transformed:" + it.path)
        }
      }

      project.tasks.getByName(transformTask).mustRunAfter(javaCompile)

      log.debug(LOG_TAG, "Transformation installed after compile")
      variant.assemble.dependsOn(transformTask)
      if (!hasLib) {
        variant.install?.dependsOn(transformTask)
      }
    }
  }

  protected void ensureProjectIsAndroidAppOrLib(PluginCollection<AppPlugin> hasApp, PluginCollection<LibraryPlugin> hasLib) {
    if (!hasApp && !hasLib) {
      throw new IllegalStateException("'android' or 'android-library' plugin required.")
    }
  }

  /**
   * Hook to configure the project under build.
   * Can be used to add other extensions, plugins, etc.
   * @param project the project under build.
   */
  protected void configure(Project project) {}

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
