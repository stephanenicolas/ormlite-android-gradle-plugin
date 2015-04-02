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
    private String configFileName;
    private Object sourceDir;
    private String classpath;

    public CreateOrmLiteConfigTask() {
        this.sourceDir = getProject().file("src/main/");
        configFileName = "ormlite_config.txt";
    }

    @OutputDirectory
    public File getOutputDirectory() {
        File rawFolder = new File(this.getProject().getProjectDir(), "src/main/res/raw/");
        if (!rawFolder.exists()) {
            throw new StopExecutionException("Raw folder not found: " + rawFolder);
        }

        File configFile = new File(rawFolder, "ormlite_config.txt");
        return configFile.getParentFile();
    }

    public String getConfigFile() {
        return configFileName;
    }

    public void setConfigFile(String configFileName) {
        this.configFileName = configFileName;
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

    public void into(String configFileName) {
        this.configFileName = configFileName;
    }

    @TaskAction
    protected void exec() throws IOException, SQLException, InterruptedException {
        List<URL> urls = addClasspathToClassLoader(classpath);
        URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));

        final CreateOrmLiteConfigAction createOrmLiteConfigAction
            = new CreateOrmLiteConfigAction(configFileName,
                                            getProject().file(sourceDir),
                                            classpath);

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
