package me.biabani.se.song.tasks.utilities;

import me.biabani.se.song.trellis.QuinFunction;

/**
 * sometimes we want to slice an array, but the result may not have the length equal to difference of startIndex and endIndex.
 * Here, we offer a more customizable slice function where result's total length and the starting index of it can be set manually.
 */
public class SliceByteArrayWithCustomOutputLengthTask implements QuinFunction<byte[], Integer, Integer, Integer, Integer, byte[]> {

    @Override
    public byte[] apply(byte[] source, Integer sourceStartIndex, Integer sourceEndIndex, Integer outputLength, Integer outputStartIndex) {
        assert outputLength >= 0;

        int interval = sourceEndIndex - sourceStartIndex;
        assert interval > 0;
        assert outputLength - outputStartIndex >= interval;

        byte[] output = new byte[outputLength];
        System.arraycopy(source, sourceStartIndex, output, outputStartIndex, interval);
        return output;
    }


    private static SliceByteArrayWithCustomOutputLengthTask singleInstance = null;

    private SliceByteArrayWithCustomOutputLengthTask() {
    }

    public static synchronized SliceByteArrayWithCustomOutputLengthTask getInstance() {
        if (singleInstance == null)
            singleInstance = new SliceByteArrayWithCustomOutputLengthTask();
        return singleInstance;
    }
}
