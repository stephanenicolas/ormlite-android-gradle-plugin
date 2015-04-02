package com.github.stephanenicolas.ormgap;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
    private ClassLoader classLoader;
    private final OrmLiteConfigUtil ormLiteConfigUtil = new OrmLiteConfigUtil();

    public CreateOrmLiteConfigAction(File configFile,
                                     File searchDir,
                                     ClassLoader classLoader) {
        this.configFile = configFile;
        this.searchDir = searchDir;
        this.classLoader = classLoader;
    }

    public void execute() throws IOException, SQLException {
        ormLiteConfigUtil.setClassLoader(classLoader);
        ormLiteConfigUtil.writeConfigFile(configFile, searchDir);
    }
}
