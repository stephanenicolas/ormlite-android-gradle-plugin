package com.github.stephanenicolas.ormgap;

import java.io.File;
import java.nio.file.Paths;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * A gradle task to generate an ORM Lite configuration file.
 *
 * @author SNI
 */
public class CreateOrmLiteConfigTask extends DefaultTask {
    private Object configFile;
    private Object sourceDir;

    public CreateOrmLiteConfigTask() {
        this.sourceDir = getProject().file("src/main/java/");

        this.configFile = Paths.get(this.getProject().getProjectDir().toString(),
                                    "src/main/res/raw/ormlite_config.txt").toFile();
    }

    @OutputDirectory
    public File getConfigFile() {
        return this.getProject().file(configFile);
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    @InputFiles
    public FileCollection getSources() {
        ConfigurableFileTree result = getProject().fileTree(this.sourceDir);
        result.include("**/*.java");
        return result;
    }

    public void into(Object file) {
        this.configFile = file;
    }

    @TaskAction
    protected void exec() {
        final CreateOrmLiteConfigAction createOrmLiteConfigAction
            = new CreateOrmLiteConfigAction(getConfigFile(),
                                            getProject().file(sourceDir));

        createOrmLiteConfigAction.setLogger(getLogger());
        boolean workDone = createOrmLiteConfigAction.execute();

        this.setDidWork(workDone);
    }
}
