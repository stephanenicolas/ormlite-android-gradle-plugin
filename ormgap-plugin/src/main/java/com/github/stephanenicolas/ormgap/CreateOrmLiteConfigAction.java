package com.github.stephanenicolas.ormgap;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
        String cmd = "cd "
            + searchDir.getAbsolutePath()
            + " && java -cp "
            + classpath
            + " com.j256.ormlite.android.apptools.OrmLiteConfigUtil "
            + configFile;

        System.out.println("Command: " + cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        final int result = process.waitFor();
        if (result != 0) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuffer buffer = new StringBuffer();
            String line;
            do {
                line = bufferedReader.readLine();
                if (line != null) {
                    buffer.append(line + '\n');
                }
            } while (line != null);
        }
    }
}
