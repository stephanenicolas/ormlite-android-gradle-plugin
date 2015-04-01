package com.github.stephanenicolas.ormgap;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import java.io.File;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;

/**
 * Action to create the ORM Lite config file.
 * Allows to fully test the task.
 *
 * @author SNI.
 */
public class CreateOrmLiteConfigAction extends OrmLiteConfigUtil {
    private static final String ERROR_DURING_CREATION_CONFIG_FILE
        = "An error occurred during creation of ORM Lite config file.";

    private final File destinationFile;
    private File searchDir;
    private Logger logger;

    public CreateOrmLiteConfigAction(File destinationFile,
                                     File searchDir) {
        this.destinationFile = destinationFile;
        this.searchDir = searchDir;
    }

    public boolean execute() {
        boolean result = true;
        try {
            writeConfigFile(destinationFile, searchDir);
        } catch (Exception e) {
            log(e, ERROR_DURING_CREATION_CONFIG_FILE);
        }
        return result;
    }

    private void log(Exception e, String msg) {
        if (logger == null) {
            System.out.println(msg);
            e.printStackTrace();
        } else {
            logger.log(LogLevel.ERROR, msg, e);
        }
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
