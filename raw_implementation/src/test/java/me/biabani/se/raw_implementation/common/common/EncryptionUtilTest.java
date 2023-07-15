package me.biabani.se.raw_implementation.common.common;

import me.biabani.se.raw_implementation.common.EncryptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class EncryptionUtilTest {

    @BeforeAll
    @Test
    public static void aesEncryptECBMode_haveNoRandomness() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String text = "hello", key = "abcdefghijklmnop";
        String encoded1 = Base64.getEncoder().encodeToString(EncryptionUtil.aesEncryptECB(key.getBytes(StandardCharsets.UTF_8), text.getBytes(StandardCharsets.UTF_8)));
        String encoded2 = Base64.getEncoder().encodeToString(EncryptionUtil.aesEncryptECB(key.getBytes(StandardCharsets.UTF_8), text.getBytes(StandardCharsets.UTF_8)));
        Assertions.assertEquals(encoded1, encoded2);
    }
}