package me.biabani.se.song.tasks;

import me.biabani.se.song.transformers.WordsToBlocksTransform;
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
 * As mentioned in '4.3 Scheme III', we should encrypt each word to enable support for hidden searches.
 * In another term, this functionality produces X(i) from W(i)
 */
public class PlainBlocksEncryptionTask implements TriFunction<byte[], byte[], List<byte[]>, List<byte[]>> {

    /**
     * The article regulates this encryption to be deterministic, which reminds of ECB mode.
     * But, CBC is deterministic in whole package and can be used.
     *
     * @param key         the key for encryption
     * @param iv          initialization vector
     * @param plainBlocks list of blocks which is the output of {@link WordsToBlocksTransform}
     * @return list of encrypted words
     */
    @Override
    public List<byte[]> apply(byte[] key, byte[] iv, List<byte[]> plainBlocks) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return plainBlocks.stream().map(item -> {
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

    private static PlainBlocksEncryptionTask singleInstance = null;

    private PlainBlocksEncryptionTask() {
    }

    public static synchronized PlainBlocksEncryptionTask getInstance() {
        if (singleInstance == null)
            singleInstance = new PlainBlocksEncryptionTask();
        return singleInstance;
    }
}
