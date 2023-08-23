package me.biabani.se.song.tasks;

import java.util.function.BiFunction;

public class PadWordsToBlockTask implements BiFunction<byte[], Integer, byte[]> {

    @Override
    public byte[] apply(byte[] bytes, Integer blockSizeInBytes) {
        byte[] res = new byte[blockSizeInBytes];
        System.arraycopy(bytes, 0, res, 0, Math.min(bytes.length, blockSizeInBytes));
        return res;
    }

    private static PadWordsToBlockTask singleInstance = null;

    private PadWordsToBlockTask() {
    }

    public static synchronized PadWordsToBlockTask getInstance() {
        if (singleInstance == null)
            singleInstance = new PadWordsToBlockTask();
        return singleInstance;
    }
}
