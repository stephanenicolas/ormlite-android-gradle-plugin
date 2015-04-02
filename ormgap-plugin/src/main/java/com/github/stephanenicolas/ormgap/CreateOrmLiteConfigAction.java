package com.github.stephanenicolas.ormgap;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import java.io.File;
import java.io.IOException;

/**
 * Create the ORM Lite config file.
 * Allows to fully test the task.
 *
 * @author SNI.
 */
public class CreateOrmLiteConfigAction {
    private static final String ERROR_DURING_CREATION_CONFIG_FILE
        = "An error occurred during creation of ORM Lite config file.";

    private final String configFile;
    private File searchDir;
    private String classpath;
    private ClassLoader classLoader;
    private final OrmLiteConfigUtil ormLiteConfigUtil = new OrmLiteConfigUtil();

    public CreateOrmLiteConfigAction(String configFile,
                                     File searchDir,
                                     String classpath) {
        this.configFile = configFile;
        this.searchDir = searchDir;
        this.classpath = classpath;
    }

    public void execute() throws IOException, InterruptedException {
        ProcessBuilder builder
            = new ProcessBuilder("java",
                                 "-cp",
                                 classpath,
                                 "com.j256.ormlite.android.apptools.OrmLiteConfigUtil",
                                 configFile);
        builder.inheritIO();
        builder.directory(searchDir);
        Process process = builder.start();
        final int result = process.waitFor();
        if (result != 0) {
            throw new RuntimeException("OrmLiteConfigUtil finished with code: " + result);
        }
    }
}
