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

    private final File configFile;
    private File searchDir;
    private String classpath;
    private final OrmLiteConfigUtil ormLiteConfigUtil = new OrmLiteConfigUtil();

    public CreateOrmLiteConfigAction(File configFile,
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
                                 "com.github.stephanenicolas.ormgap.OrmLiteConfigUtil",
                                 configFile.getAbsolutePath(),
                                 searchDir.getAbsolutePath());
        builder
            .inheritIO()
            .directory(searchDir);
        final int result = builder.start().waitFor();
        if (result != 0) {
            throw new RuntimeException("OrmLiteConfigUtil finished with code: " + result);
        }
    }
}
