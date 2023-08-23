package me.biabani.se.song.tasks;

import me.biabani.se.song.trellis.TriFunction;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * src: '4.2 Scheme II: Controlled Searching'
 * for limiting the server to only have access to results of the correct indexes, we generate keys
 * for each block deterministically based on the word of that block.
 * <br>
 * mathematical form is k<sub>i</sub> = f(l<sub>i</sub>)
 */
public class BlockBasedKeyGenerationTask implements TriFunction<byte[], byte[], List<byte[]>, List<byte[]>> {

    @Override
    public List<byte[]> apply(byte[] key, byte[] iv, List<byte[]> li) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return li.stream().map(item -> {
                try {
                    return cipher.doFinal(item);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    private static BlockBasedKeyGenerationTask singleInstance = null;

    private BlockBasedKeyGenerationTask() {
    }

    public static synchronized BlockBasedKeyGenerationTask getInstance() {
        if (singleInstance == null)
            singleInstance = new BlockBasedKeyGenerationTask();
        return singleInstance;
    }
}
