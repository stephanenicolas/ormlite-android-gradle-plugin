package com.github.stephanenicolas.ormgap;

import java.io.File;
import java.io.IOException;
import org.gradle.api.logging.Logger;

/**
 * Create the ORM Lite config file.
 * Allows to fully test the task.
 *
 * @author SNI.
 */
public class CreateOrmLiteConfigAction {
    private final File configFile;
    private File searchDir;
    private String classpath;
    private Logger logger;

    public CreateOrmLiteConfigAction(File configFile,
                                     File searchDir,
                                     String classpath,
                                     Logger logger) {
        this.configFile = configFile;
        this.searchDir = searchDir;
        this.classpath = classpath;
        this.logger = logger;
    }

    public void execute() throws IOException, InterruptedException {
        ProcessBuilder builder
            = new ProcessBuilder("java",
                                 "-cp",
                                 classpath,
                                 "com.github.stephanenicolas.ormgap.OrmLiteConfigUtil",
                                 configFile.getAbsolutePath(),
                                 searchDir.getAbsolutePath());

        logger.debug("Generating ORMLite Config file using command line: " + builder.command());
        builder
            .inheritIO()
            .directory(searchDir);
        final int result = builder.start().waitFor();
        if (result != 0) {
            throw new RuntimeException("OrmLiteConfigUtil finished with code: " + result);
        }
    }
}
