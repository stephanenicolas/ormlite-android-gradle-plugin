package com.github.stephanenicolas.ormgap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction;

/**
 * Generate an ORM Lite configuration file.
 *
 * @author SNI
 */
public class CreateOrmLiteConfigTask extends DefaultTask {
    private File configFile;
    private Object sourceDir;
    private String classpath;

    public CreateOrmLiteConfigTask() {
        this.sourceDir = getProject().file("src/main/java/");

        File rawFolder = new File(this.getProject().getProjectDir(), "src/main/res/raw/");
        if (!rawFolder.exists()) {
            throw new StopExecutionException("Raw folder not found: " + rawFolder);
        }

        configFile = new File(rawFolder, "ormlite_config.txt");
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return configFile.getParentFile();
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public void setClasspath(String classpath) throws IOException {
        this.classpath = classpath;
    }

    private List<URL> addClasspathToClassLoader(String classpath) throws IOException {
        String[] classPathEntries = classpath.split(":");
        List<URL> urls = new ArrayList<>();
        for (String classPathEntry : classPathEntries) {
            if (!classPathEntry.contains("com.j256.ormlite/ormlite")) {
                urls.add(getProject().file(classPathEntry).toURI().toURL());
                getLogger().log(LogLevel.DEBUG, classPathEntry + " has been added to ORM Lite create config task's classpath");
            }
        }
        return urls;
    }

    @InputFiles
    public FileCollection getSources() {
        ConfigurableFileTree result = getProject().fileTree(this.sourceDir);
        result.include("**/*.java");
        return result;
    }

    public void into(Object configFile) {
        this.configFile = getProject().file(configFile);
    }

    @TaskAction
    protected void exec() throws IOException, SQLException {
        final File configFile = getConfigFile();
        if (!configFile.exists()) {
            configFile.createNewFile();
        }
        List<URL> urls = addClasspathToClassLoader(classpath);
        URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));

        final CreateOrmLiteConfigAction createOrmLiteConfigAction
            = new CreateOrmLiteConfigAction(configFile,
                                            getProject().file(sourceDir),
                                            urlClassLoader);

        createOrmLiteConfigAction.execute();

        this.setDidWork(true);
    }

    public void addURL(URLClassLoader classLoader, URL u) throws IOException {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, u);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }//end try catch
    }//end method
}
