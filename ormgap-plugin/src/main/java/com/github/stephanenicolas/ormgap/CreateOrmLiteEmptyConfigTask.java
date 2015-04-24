package com.github.stephanenicolas.ormgap;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskAction;

/**
 * Generate an ORM Lite configuration file.
 *
 * @author SNI
 */
public class CreateOrmLiteEmptyConfigTask extends DefaultTask {
    private File configFileName;
    private File rawDir;

    public CreateOrmLiteEmptyConfigTask() {
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

    @InputFiles
    public FileCollection getSources() {
        File rawFolder = new File(this.getProject().getProjectDir(), "src/main/res/raw/");
        if (!rawFolder.exists()) {
            throw new StopExecutionException("Raw folder not found: " + rawFolder);
        }

        File configFile = new File(rawFolder, "ormlite_config.txt");
        ConfigurableFileTree result = getProject().fileTree(configFile);
        return result;
    }

    public void into(String configFileName) {
        this.configFileName = new File(rawDir, configFileName);
    }

    public void setResFolder(Object relativePath) {
        File resFolder = getProject().file(relativePath);
        rawDir = new File(resFolder, "raw");
    }

    @TaskAction
    protected void exec() throws IOException, SQLException, InterruptedException {
        final CreateOrmLiteEmptyConfigAction createOrmLiteEmptyConfigAction
            = new CreateOrmLiteEmptyConfigAction(configFileName);

        createOrmLiteEmptyConfigAction.execute();

        this.setDidWork(true);
    }
}
