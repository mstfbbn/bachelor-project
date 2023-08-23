package me.biabani.se.song.tasks.utilities;

import me.biabani.se.song.trellis.TriFunction;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AesCbcEncryptionTask implements TriFunction<byte[], byte[], byte[], byte[]> {

    @Override
    public byte[] apply(byte[] key, byte[] iv, byte[] raw) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(raw);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    private static AesCbcEncryptionTask singleInstance = null;

    private AesCbcEncryptionTask() {
    }

    public static synchronized AesCbcEncryptionTask getInstance() {
        if (singleInstance == null) singleInstance = new AesCbcEncryptionTask();
        return singleInstance;
    }
}
