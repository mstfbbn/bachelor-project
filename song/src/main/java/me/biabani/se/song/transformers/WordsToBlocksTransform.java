package me.biabani.se.song.transformers;

import me.biabani.se.song.tasks.FileTokenizationTask;
import me.biabani.se.song.tasks.PadWordsToBlockTask;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Src: '2 Searching on Encrypted Data'
 * <br>
 * This method changes words to fixed-size byte arrays. if the word is smaller, we use padding.
 * If the word is larger, we break it into pieces.
 */
public class WordsToBlocksTransform implements BiFunction<List<String>, Integer, List<byte[]>> {

    /**
     * @param blockSizeInBytes This is equal to 'n' in the article
     * @param words            List of words in a file which is the output of {@link FileTokenizationTask}
     * @return list of fixed-size byte arrays
     */
    @Override
    public List<byte[]> apply(List<String> words, Integer blockSizeInBytes) {
        List<byte[]> byteList = new ArrayList<>();
        words.forEach(word -> {
            byte[] wordBytes = word.getBytes(StandardCharsets.UTF_8);
            if (wordBytes.length < blockSizeInBytes) {
                byteList.add(PadWordsToBlockTask.getInstance().apply(wordBytes, blockSizeInBytes));
            } else if (wordBytes.length == blockSizeInBytes) {
                byteList.add(wordBytes);
            } else {
                for (int i = 0; i < Math.ceil((double) wordBytes.length / blockSizeInBytes); i++) {
                    if ((i + 1) * blockSizeInBytes < wordBytes.length) {
                        byteList.add(Arrays.copyOfRange(wordBytes, i * blockSizeInBytes, (i + 1) * blockSizeInBytes));
                    } else {
                        byteList.add(PadWordsToBlockTask.getInstance().apply(Arrays.copyOfRange(wordBytes, i * blockSizeInBytes, wordBytes.length), blockSizeInBytes));
                    }
                }
            }
        });

        return byteList;
    }

    private static WordsToBlocksTransform singleInstance = null;

    private WordsToBlocksTransform() {
    }

    public static synchronized WordsToBlocksTransform getInstance() {
        if (singleInstance == null)
            singleInstance = new WordsToBlocksTransform();
        return singleInstance;
    }
}
