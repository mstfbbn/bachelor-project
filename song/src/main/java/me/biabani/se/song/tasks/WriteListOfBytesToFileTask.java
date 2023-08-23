package me.biabani.se.song.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.util.List;
import java.util.function.BiConsumer;

public class WriteListOfBytesToFileTask implements BiConsumer<String, List<byte[]>> {

    @Override
    public void accept(String filePath, List<byte[]> bytes) {
        File file = new File(filePath);
        if (file.exists()) {
            throw new RuntimeException(new FileAlreadyExistsException(filePath));
        }
        try {
            if (!file.createNewFile()) {
                throw new RuntimeException(new FileSystemAlreadyExistsException(filePath));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileOutputStream output = new FileOutputStream(filePath, true)) {
            bytes.forEach(item -> {
                try {
                    output.write(item);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static WriteListOfBytesToFileTask singleInstance = null;

    private WriteListOfBytesToFileTask() {
    }

    public static synchronized WriteListOfBytesToFileTask getInstance() {
        if (singleInstance == null)
            singleInstance = new WriteListOfBytesToFileTask();
        return singleInstance;
    }
}
