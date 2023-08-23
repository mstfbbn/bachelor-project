package me.biabani.se.song.tasks.utilities;

import me.biabani.se.song.trellis.TriFunction;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

/**
 * Argon2 is a key derivation function that was selected as the winner of the 2015 Password Hashing Competition.
 * It can be customized by different parameters according to resources and desired security level. <br>
 * <p>
 * NOTE: {@link #hashLength} is set to 32 byte because we want to get two 16 byte parameters 'key' and 'iv'.
 */
public class Argon2HashGenerationTask implements TriFunction<byte[], byte[], Integer, byte[]> {

    private static final int iterations = 5;
    private static final int memLimit = 66536;
    private static final int parallelism = 1;

    @Override
    public byte[] apply(byte[] password, byte[] salt, Integer hashLength) {

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(iterations)
                .withMemoryAsKB(memLimit)
                .withParallelism(parallelism)
                .withSalt(salt);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());
        byte[] result = new byte[hashLength];
        generator.generateBytes(password, result, 0, result.length);
        return result;
    }

    private static Argon2HashGenerationTask singleInstance = null;

    private Argon2HashGenerationTask() {
    }

    public static synchronized Argon2HashGenerationTask getInstance() {
        if (singleInstance == null)
            singleInstance = new Argon2HashGenerationTask();
        return singleInstance;
    }
}
