package me.biabani.se.song.tasks.utilities;

import me.biabani.se.song.trellis.TriFunction;

import java.util.Arrays;

/**
 * slicing a byte array is commonly used in encryption projects. the caller have to be careful with the bounds to avoid exceptions.
 */
public class SliceByteArrayTask implements TriFunction<byte[], Integer, Integer, byte[]> {

    @Override
    public byte[] apply(byte[] byteArray, Integer start, Integer end) {
        assert end <= byteArray.length;
        assert start >= 0;
        assert end >= start;

        return Arrays.copyOfRange(byteArray, start, end);
    }

    private static SliceByteArrayTask singleInstance = null;

    private SliceByteArrayTask() {
    }

    public static synchronized SliceByteArrayTask getInstance() {
        if (singleInstance == null)
            singleInstance = new SliceByteArrayTask();
        return singleInstance;
    }
}
