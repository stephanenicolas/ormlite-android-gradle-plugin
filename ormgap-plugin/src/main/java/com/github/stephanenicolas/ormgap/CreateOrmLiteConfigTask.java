package com.github.stephanenicolas.ormgap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.SimpleFileCollection;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.api.tasks.incremental.InputFileDetails;

/**
 * Generate an ORM Lite configuration file.
 *
 * @author SNI
 */
@CacheableTask
public class CreateOrmLiteConfigTask extends DefaultTask {
    public static final String TASK_TEMP_FILE__NAME = "intermediates/incremental/createOrmLiteConfigTask/";
    private File configFileName;
    private Object sourceDir;
    private String classpath;
    private File dstDir;

    public CreateOrmLiteConfigTask() {
    }

    @InputFiles
    public FileCollection getSources() {
        ConfigurableFileTree fileTree = getProject().fileTree(this.sourceDir);
        fileTree.include("**/*.java");
        return fileTree;
    }

    @OutputFile
    public File getOutputFile() {
        return new File(dstDir, "ormlite_config.txt");
    }

    @OutputFile
    private File getStateFile() {
        final File buildDir = getProject().getBuildDir();
        final File taskDir = new File(buildDir, TASK_TEMP_FILE__NAME);
        if(!taskDir.exists()) {
            taskDir.mkdirs();
        }
        return new File(taskDir, "using-ormlite.txt");
    }


    public void setClasspath(String classpath) throws IOException {
        this.classpath = classpath;
    }

    public void into(String configFileName) {
        this.configFileName = new File(dstDir, configFileName);
    }

    public void setSources(Object relativePath) {
        this.sourceDir = getProject().file(relativePath);
    }

    public void setDestDirFolder(Object relativePath) {
        dstDir = getProject().file(relativePath);
        if (!dstDir.exists()) {
            final boolean wasAssetsDirCreated = dstDir.mkdirs();
            if (!wasAssetsDirCreated) {
                throw new RuntimeException("Impossible to create destination folder:" + dstDir.getAbsolutePath());
            }
        }
    }

    @TaskAction
    protected void exec(IncrementalTaskInputs inputs) throws IOException, SQLException, InterruptedException {
        if (!inputs.isIncremental()) {
            getProject().delete(getOutputFile());
        }

        final Set<String> lastFilesInState = loadFileNames();
        final Set<String> newFilesInState = new HashSet<>(lastFilesInState);

        if (!hasNewState(inputs, lastFilesInState, newFilesInState)) {
            return;
        }
        saveFileNames(newFilesInState);

        final CreateOrmLiteConfigAction createOrmLiteConfigAction
                = new CreateOrmLiteConfigAction(configFileName,
                                                getProject().file(sourceDir),
                                                classpath, getLogger());

        createOrmLiteConfigAction.execute();

        this.setDidWork(true);
    }

    private boolean hasNewState(IncrementalTaskInputs inputs, final Set<String> lastFilesInState, final Set<String> newFilesInState) {
        final AtomicBoolean hasChanged = new AtomicBoolean(false);
        inputs.outOfDate(new Action<InputFileDetails>() {
            @Override public void execute(InputFileDetails inputFileDetails) {
                final String absolutePath = inputFileDetails.getFile().getAbsolutePath();
                if(inputFileDetails.isAdded() && isUsingOrmLite(inputFileDetails.getFile())) {
                    newFilesInState.add(absolutePath);
                    hasChanged.set(true);
                    getLogger().debug("New file using ormlite: " + absolutePath);
                } else if (inputFileDetails.isModified() && lastFilesInState.contains(absolutePath)) {
                    getLogger().debug("Modified file using ormlite: " + absolutePath);
                    hasChanged.set(true);
                } else if(isUsingOrmLite(inputFileDetails.getFile())) {
                    getLogger().debug("Out of date file using ormlite: " + absolutePath);
                    newFilesInState.add(absolutePath);
                    hasChanged.set(true);
                }
            }
        });

        inputs.removed(new Action<InputFileDetails>() {
            @Override public void execute(InputFileDetails inputFileDetails) {
                final String absolutePath = inputFileDetails.getFile().getAbsolutePath();
                if(lastFilesInState.contains(absolutePath)) {
                    getLogger().debug("Removed file using ormlite: " + absolutePath);
                    newFilesInState.remove(absolutePath);
                    hasChanged.set(true);
                }

            }
        });
        return hasChanged.get();
    }

    private void saveFileNames(Set<String> fileNameSet) throws IOException {
        getLogger().debug("saving new state: " + fileNameSet.toString());
        final File stateFile = getStateFile();
        PrintWriter fileWriter = null;
        try {
            fileWriter = new PrintWriter(new FileWriter(stateFile));
            final ArrayList<String> sortedfileNameList = new ArrayList<>(fileNameSet);
            for (String fileName : sortedfileNameList) {
                fileWriter.println(fileName);
            }
        } finally {
            if(fileWriter!=null) {
                fileWriter.close();
            }
        }
    }

    private Set<String> loadFileNames() throws IOException {
        Set<String> files = new HashSet<>();
        final File stateFile = getStateFile();
        if(!stateFile.exists()) {
            return files;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(stateFile));
            while (reader.ready()) {
                files.add(reader.readLine());
            }
        } finally {
            if(reader!=null) {
                reader.close();
            }
        }
        getLogger().debug("loading new state: " +  files.toString());
        return files;
    }

    private SimpleFileCollection findFilesUsingOrmLite(Iterable<File> fileSet) {
        SimpleFileCollection result = new SimpleFileCollection();
        for (File file : fileSet) {
            if(isUsingOrmLite(file)) {
                result.getFiles().add(file);
            }
        }
        return result;
    }

    private boolean isUsingOrmLite(File file) {
        BufferedReader reader = null;
        boolean found = false;
        try {
            reader = new BufferedReader(new FileReader(file));
            while (reader.ready() && !found) {
                if (reader.readLine().contains("com.j256.ormlite")) {
                    found = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return found;
    }
}
