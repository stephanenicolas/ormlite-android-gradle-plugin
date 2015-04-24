package com.github.stephanenicolas.ormgap;

import java.io.File;
import java.io.IOException;

/**
 * Create an empty ORM Lite config file.
 * This allows the app to compile and allows to ignore
 * the config file in git source tree..
 *
 * @author SNI.
 */
public class CreateOrmLiteEmptyConfigAction {
    private final File configFile;

    public CreateOrmLiteEmptyConfigAction(File configFile) {
        this.configFile = configFile;
    }

    public void execute() throws IOException, InterruptedException {
        if (configFile.exists()) {
            boolean deleted = configFile.delete();
            if (!deleted) {
                throw new RuntimeException("ORMGAP could not delete the ormlite file in : " + configFile.getAbsolutePath());
            }
        }

        boolean created = configFile.createNewFile();
        if (!created) {
            throw new RuntimeException("ORMGAP could not create empty ormlite file in : " + configFile.getAbsolutePath());
        }
    }
}
