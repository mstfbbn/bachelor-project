package me.biabani.se.song.actions;

import me.biabani.se.song.constants.Constants;
import me.biabani.se.song.tasks.*;
import me.biabani.se.song.tasks.utilities.SliceByteArrayTask;
import me.biabani.se.song.tasks.utilities.TokenizeStringTask;
import me.biabani.se.song.transformers.WordsToBlocksTransform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

class SongSearchInOneFileActionTest {

    @Test
    public void SongSearchInOneFileAction_testIfFindsTheFile() {
        String masterKey = "abcd";
        String word = "useful channel";
        Integer blockSizeInBytes = 16;
        byte[] masterKeyDerivationForXi = MasterKeyDerivationTask.getInstance().apply(masterKey, Constants.XI_SALT, blockSizeInBytes);
        List<byte[]> xi = PlainBlocksEncryptionTask.getInstance().apply(
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForXi, 0, blockSizeInBytes),
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForXi, blockSizeInBytes, blockSizeInBytes * 2),
                WordsToBlocksTransform.getInstance().apply(
                        TokenizeStringTask.getInstance().apply(word),
                        blockSizeInBytes
                ));
        List<byte[]> li = GetLeftSideOfBlockTask.getInstance().apply(xi);

        byte[] masterKeyDerivationForKi = MasterKeyDerivationTask.getInstance().apply(masterKey, Constants.KI_SALT, blockSizeInBytes);
        List<byte[]> ki = BlockBasedKeyGenerationTask.getInstance().apply(
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForKi, 0, blockSizeInBytes),
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForKi, blockSizeInBytes, blockSizeInBytes * 2),
                li
        );
        Assertions.assertTrue(SongSearchInOneFileAction.getInstance().apply(new File("sample.txt.bin"), xi, ki));
    }
}