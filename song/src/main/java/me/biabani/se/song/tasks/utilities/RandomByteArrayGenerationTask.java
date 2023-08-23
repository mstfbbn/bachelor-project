package me.biabani.se.song.tasks.utilities;

import java.security.SecureRandom;
import java.util.function.Function;

public class RandomByteArrayGenerationTask implements Function<Integer, byte[]> {

    @Override
    public byte[] apply(Integer length) {
        byte[] byteArray = new byte[length];
        new SecureRandom().nextBytes(byteArray);
        return byteArray;
    }

    private static RandomByteArrayGenerationTask singleInstance = null;

    private RandomByteArrayGenerationTask() {
    }

    public static synchronized RandomByteArrayGenerationTask getInstance() {
        if (singleInstance == null)
            singleInstance = new RandomByteArrayGenerationTask();
        return singleInstance;
    }
}
