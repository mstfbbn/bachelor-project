package me.biabani.se.raw_implementation.common;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import jakarta.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
            throw new RuntimeException("error encrypting using AES");
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

    public static KeyPair rsaKeyGenerator() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] rsaEncrypt(byte[] publicKeyByteArray, byte[] rawText) throws InvalidKeyException {
        try {
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyByteArray));
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return encryptCipher.doFinal(rawText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] rsaDecrypt(byte[] privateKeyByteArray, byte[] encrypted) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyByteArray));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encrypted);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
