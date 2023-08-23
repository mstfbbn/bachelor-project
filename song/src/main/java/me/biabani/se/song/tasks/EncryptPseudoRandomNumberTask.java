package me.biabani.se.song.tasks;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.function.BiFunction;

public class EncryptPseudoRandomNumberTask implements BiFunction<byte[], byte[], byte[]> {

    @Override
    public byte[] apply(byte[] key, byte[] raw) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(raw);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static EncryptPseudoRandomNumberTask singleInstance = null;

    private EncryptPseudoRandomNumberTask() {
    }

    public static synchronized EncryptPseudoRandomNumberTask getInstance() {
        if (singleInstance == null) singleInstance = new EncryptPseudoRandomNumberTask();
        return singleInstance;
    }
}
