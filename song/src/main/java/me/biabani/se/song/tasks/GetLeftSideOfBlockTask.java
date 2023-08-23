package me.biabani.se.song.tasks;

import me.biabani.se.song.tasks.utilities.SliceByteArrayWithCustomOutputLengthTask;

import java.util.List;
import java.util.function.Function;

/**
 * According to '4.4 Scheme IV: The Final Scheme' second paragraph, we have to divide the result of {@link PlainBlocksEncryptionTask} into two parts. Xi into &lt;Li , Ri&gt;
 */
public class GetLeftSideOfBlockTask implements Function<List<byte[]>, List<byte[]>> {

    @Override
    public List<byte[]> apply(List<byte[]> encryptedBlocks) {
        return encryptedBlocks.stream()
                .map(
                        encryptedBlock ->
                                SliceByteArrayWithCustomOutputLengthTask.getInstance()
                                        .apply(encryptedBlock, 0, encryptedBlock.length / 2, encryptedBlock.length, 0)
                ).toList();
    }

    private static GetLeftSideOfBlockTask singleInstance = null;

    private GetLeftSideOfBlockTask() {
    }

    public static synchronized GetLeftSideOfBlockTask getInstance() {
        if (singleInstance == null)
            singleInstance = new GetLeftSideOfBlockTask();
        return singleInstance;
    }
}
