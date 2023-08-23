package me.biabani.se.song.tasks;

import me.biabani.se.song.tasks.utilities.TokenizeStringTask;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;

/**
 * Content of a file can be mapped to a list of words. before anything, we have to break a file into a list of words.
 * Therefore, this function is the first thing to do at encryption time.
 */
public class FileTokenizationTask implements Function<File, List<String>> {

    /**
     * @param file the file to be tokenized
     * @return list of words in file
     */
    @Override
    public List<String> apply(File file) {
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            return TokenizeStringTask.getInstance().apply(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileTokenizationTask singleInstance = null;

    private FileTokenizationTask() {
    }

    public static synchronized FileTokenizationTask getInstance() {
        if (singleInstance == null)
            singleInstance = new FileTokenizationTask();
        return singleInstance;
    }
}
