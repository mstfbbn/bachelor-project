package me.biabani.encryption.searchable.common;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Note: For AES, key length must be 128, 192 or 256 bit long.
 */
public class EncryptionUtil {

    public static String md5Encrypt(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("could not MD5 hash string value <" + str + ">");
        }
    }

    public static byte[] aesEncryptECB(byte[] key, byte[] text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(text);
    }

    /**
     * encrypts text using AES CBC mode and PKCS5 padding
     *
     * @return base64 encoded format of cypher text
     */
    public static byte[] aesEncryptCBC(byte[] key, byte[] iv, byte[] rawText) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(rawText);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error decrypting using AES");
        }
    }

    public static String aesDecrypt(byte[] iv, byte[] key, byte[] encrypted) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error decrypting using AES");
        }
    }

    public static byte[] aesGenerateRandomIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

}
