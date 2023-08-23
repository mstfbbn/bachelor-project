package me.biabani.se.song.actions;

import org.junit.jupiter.api.Test;

import java.io.File;

class SongDecryptionActionTest {

    @Test
    public void accept_isRunnableWithoutError() {
        SongDecryptionAction.getInstance().accept(new File("sample.txt.bin"), "sample.txt.decrypted", "abcd");
    }
}