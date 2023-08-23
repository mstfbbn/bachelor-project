package me.biabani.se.song.tasks;

import java.util.Arrays;
import java.util.function.Function;

/**
 * By having a statistical idea and knowing that delimiters are included, starting from start is more optimal to do it in reverse.
 */
public class RemovePaddingsOfBlockTask implements Function<byte[], byte[]> {

    @Override
    public byte[] apply(byte[] bytes) {
        int firstIndexOfZero = -1;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                firstIndexOfZero = i;
                break;
            }
        }

        return (firstIndexOfZero == -1) ? bytes : Arrays.copyOfRange(bytes, 0, firstIndexOfZero);
    }

    private static RemovePaddingsOfBlockTask singleInstance = null;

    private RemovePaddingsOfBlockTask() {
    }

    public static synchronized RemovePaddingsOfBlockTask getInstance() {
        if (singleInstance == null)
            singleInstance = new RemovePaddingsOfBlockTask();
        return singleInstance;
    }
}
