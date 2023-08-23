package me.biabani.se.song.tasks.utilities;

import me.biabani.se.song.trellis.TriFunction;

public class CheckTwoByteArrayEqualityTask implements TriFunction<byte[], byte[], Integer, Boolean> {

    /**
     * checking is done from index zero to the provided length
     *
     * @param b1     first byte array
     * @param b2     second byte array
     * @param length length for testing the values
     * @return if it's a match or not
     */
    @Override
    public Boolean apply(byte[] b1, byte[] b2, Integer length) {
        assert b1.length >= length;
        assert b2.length >= length;

        boolean isMatch = true;
        for (int i = 0; i < length; i++) {
            if (b1[i] != b2[i]) {
                isMatch = false;
                break;
            }
        }
        return isMatch;
    }


    private static CheckTwoByteArrayEqualityTask singleInstance = null;

    private CheckTwoByteArrayEqualityTask() {
    }

    public static synchronized CheckTwoByteArrayEqualityTask getInstance() {
        if (singleInstance == null)
            singleInstance = new CheckTwoByteArrayEqualityTask();
        return singleInstance;
    }
}
