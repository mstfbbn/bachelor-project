package me.biabani.encryption.searchable.common;

import java.util.Arrays;

public class ArrayUtil {

    public static byte[] resizeArray(byte[] array, int newLength) {
        if (array.length == newLength) {
            return array;
        } else if (array.length > newLength) {
            byte[] newArray = new byte[newLength];
            System.arraycopy(array, 0, newArray, 0, newLength);
            return newArray;
        } else {
            return Arrays.copyOfRange(array, 0, newLength);
        }
    }
}
