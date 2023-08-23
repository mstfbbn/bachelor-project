package me.biabani.se.song.tasks.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

/**
 * SHA3-256 offers more security than the one in SHA 2 family.
 */
public class Sha256GenerationTask implements Function<String, byte[]> {

    @Override
    public byte[] apply(String s) {
        try {
            return MessageDigest.getInstance("SHA3-256").digest(s.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static Sha256GenerationTask singleInstance = null;

    private Sha256GenerationTask() {
    }

    public static synchronized Sha256GenerationTask getInstance() {
        if (singleInstance == null)
            singleInstance = new Sha256GenerationTask();
        return singleInstance;
    }
}
