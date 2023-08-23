package me.biabani.se.song.tasks.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class AesEcbEncryptionTaskTest {

    @Test
    public void aesEncryptECBMode_haveNoRandomness() {
        String text = "abcdabcdabcdabcd", key = "abcdabcdabcdabcd";
        String encoded1 = Base64.getEncoder().encodeToString(AesEcbEncryptionTask.getInstance().apply(key.getBytes(StandardCharsets.UTF_8), text.getBytes(StandardCharsets.UTF_8)));
        String encoded2 = Base64.getEncoder().encodeToString(AesEcbEncryptionTask.getInstance().apply(key.getBytes(StandardCharsets.UTF_8), text.getBytes(StandardCharsets.UTF_8)));
        Assertions.assertEquals(encoded1, encoded2);
    }
}