package me.biabani.se.song.tasks;

import me.biabani.se.song.tasks.utilities.Argon2HashGenerationTask;
import me.biabani.se.song.tasks.utilities.Sha256GenerationTask;
import me.biabani.se.song.trellis.TriFunction;

/**
 * Saving a key, an IV, or another key for some other functionality and other thing is not optimal.
 * The good approach is to derive values from a masterKey.
 */
public class MasterKeyDerivationTask implements TriFunction<String, String, Integer, byte[]> {

    @Override
    public byte[] apply(String masterKey, String fixedString, Integer blockSizeInBytes) {
        byte[] fixedStringSha256 = Sha256GenerationTask.getInstance().apply(fixedString);
        byte[] masterKeySha256 = Sha256GenerationTask.getInstance().apply(masterKey);
        return Argon2HashGenerationTask.getInstance().apply(permute(masterKey.getBytes(), fixedStringSha256, blockSizeInBytes * 2), masterKeySha256, blockSizeInBytes * 2);
    }

    private byte[] permute(byte[] a, byte[] b, int resultLength) {
        assert resultLength > 0;
        assert a.length > 0 && b.length > 0;

        byte[] result = new byte[resultLength];

        for (int i = 0; i < result.length; i += 2) {
            if (a.length > (i / 2)) {
                result[i] = a[i / 2];
            }
        }
        int bIndex;
        for (int i = 1; i <= resultLength; i += 2) {
            bIndex = ((i - 1) / 2);
            if ((b.length) > bIndex) {
                result[i] = b[bIndex];
            }
        }

        return result;
    }

    private static MasterKeyDerivationTask singleInstance = null;

    private MasterKeyDerivationTask() {
    }

    public static synchronized MasterKeyDerivationTask getInstance() {
        if (singleInstance == null)
            singleInstance = new MasterKeyDerivationTask();
        return singleInstance;
    }
}
