package me.biabani.se.song.transformers;

import java.util.List;
import java.util.function.Function;

/**
 * A list of smaller byte arrays are concatenated into one big byte array.
 * Although in most cases, byte arrays in list have the same size, because of being used in block ciphers.
 * This task supposes they have different sizes to offer more dynamic implementation.
 */
public class ListOfByteArrayToSingleByteArrayTransform implements Function<List<byte[]>, byte[]> {

    @Override
    public byte[] apply(List<byte[]> byteArrayList) {
        assert byteArrayList != null;
        int newLength = byteArrayList.stream().mapToInt(item -> item.length).sum();
        byte[] accumulated = new byte[newLength];
        int index = 0;
        for (byte[] byteArray : byteArrayList) {
            for (byte b : byteArray) {
                accumulated[index] = b; // we use index to support different length of each byteArray
                index++;
            }
        }
        return accumulated;
    }

    private static ListOfByteArrayToSingleByteArrayTransform singleInstance = null;

    private ListOfByteArrayToSingleByteArrayTransform() {
    }

    public static synchronized ListOfByteArrayToSingleByteArrayTransform getInstance() {
        if (singleInstance == null)
            singleInstance = new ListOfByteArrayToSingleByteArrayTransform();
        return singleInstance;
    }
}
