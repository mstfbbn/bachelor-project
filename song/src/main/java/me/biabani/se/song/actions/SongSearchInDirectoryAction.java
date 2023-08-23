package me.biabani.se.song.actions;

import me.biabani.se.song.trellis.TriFunction;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Computations of search is done by server, which is the whole idea of searchable encryption.
 */
public class SongSearchInDirectoryAction implements TriFunction<String, List<byte[]>, List<byte[]>, List<String>> {

    /**
     * The search on a folder is recursive and all subFolders are checked to have the word.
     *
     * @param rootDirectoryPath the root directory that search will run on it recursively
     * @param word the word to be searched (X<sub>i</sub>)
     * @param userKey K<sub>i</sub> is used for encryption of S<sub>i</sub>
     * @return list of paths that the word is found
     */
    @Override
    public List<String> apply(String rootDirectoryPath, List<byte[]> word, List<byte[]> userKey) {
        List<String> results = new ArrayList<>();

        File rootDirectory = new File(rootDirectoryPath);
        if (!rootDirectory.isDirectory()) {
            throw new RuntimeException(new NotDirectoryException(rootDirectoryPath));
        }

        Optional.ofNullable(rootDirectory.listFiles())
                .ifPresent(files ->
                        Stream.of(files)
                                .forEach(file -> {
                                    if (file.isDirectory()) {
                                        results.addAll(apply(file.getAbsolutePath(), word, userKey));
                                    } else if (file.isFile()) {
                                        if (SongSearchInOneFileAction.getInstance().apply(file, word, userKey)) {
                                            results.add(file.getAbsolutePath());
                                        }
                                    }
                                }));

        return results;
    }

    private static SongSearchInDirectoryAction singleInstance = null;

    private SongSearchInDirectoryAction() {
    }

    public static synchronized SongSearchInDirectoryAction getInstance() {
        if (singleInstance == null)
            singleInstance = new SongSearchInDirectoryAction();
        return singleInstance;
    }
}
