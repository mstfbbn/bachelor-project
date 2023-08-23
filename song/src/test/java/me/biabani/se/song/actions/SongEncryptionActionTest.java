package me.biabani.se.song.actions;

import org.junit.jupiter.api.Test;

import java.io.File;

class SongEncryptionActionTest {

    @Test
    public void SongEncryptionAction_isRunnableWithoutError() {
        SongEncryptionAction.getInstance().accept(new File("src/test/resources/sample.txt"), "sample.txt.bin", "abcd");
    }
}