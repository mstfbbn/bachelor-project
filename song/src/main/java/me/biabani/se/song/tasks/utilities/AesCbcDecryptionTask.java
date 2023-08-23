package me.biabani.se.song.tasks.utilities;

import me.biabani.se.song.trellis.TriFunction;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AesCbcDecryptionTask implements TriFunction<byte[], byte[], byte[], byte[]> {

    @Override
    public byte[] apply(byte[] key, byte[] iv, byte[] encrypted) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(encrypted);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private static AesCbcDecryptionTask singleInstance = null;

    private AesCbcDecryptionTask() {
    }

    public static synchronized AesCbcDecryptionTask getInstance() {
        if (singleInstance == null) singleInstance = new AesCbcDecryptionTask();
        return singleInstance;
    }
}
