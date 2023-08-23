package me.biabani.se.song.tasks;

import me.biabani.se.song.transformers.ByteArrayToListOfByteArrayTransform;
import me.biabani.se.song.transformers.ListOfByteArrayToSingleByteArrayTransform;
import me.biabani.se.song.trellis.QuadFunction;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The goal is to produce a list of pseudorandom values which is deterministic.
 */
public class PseudoRandomNumberGenerationTask implements QuadFunction<Integer, Integer, byte[], byte[], List<byte[]>> {

    @Override
    public List<byte[]> apply(Integer numberOfBlocks, Integer blockSize, byte[] key, byte[] iv) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            assert iv.length > 0;
            Random internalPRNG = new Random((long) numberOfBlocks * iv[0]);

            List<byte[]> result = new ArrayList<>();
            for (int i = 0; i < numberOfBlocks; i++) {
                result.add(getNextRandomByteArray(internalPRNG, blockSize));
            }

            byte[] pseudoRandomTotalByteArray = cipher.doFinal(ListOfByteArrayToSingleByteArrayTransform.getInstance().apply(result));

            return ByteArrayToListOfByteArrayTransform.getInstance().apply(pseudoRandomTotalByteArray, blockSize);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getNextRandomByteArray(Random prng, int length) {
        byte[] byteArray = new byte[length];
        prng.nextBytes(byteArray);
        return byteArray;
    }

    private static PseudoRandomNumberGenerationTask singleInstance = null;

    private PseudoRandomNumberGenerationTask() {
    }

    public static synchronized PseudoRandomNumberGenerationTask getInstance() {
        if (singleInstance == null) singleInstance = new PseudoRandomNumberGenerationTask();
        return singleInstance;
    }
}
