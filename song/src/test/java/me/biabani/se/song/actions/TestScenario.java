package me.biabani.se.song.actions;

import me.biabani.se.song.constants.Constants;
import me.biabani.se.song.tasks.BlockBasedKeyGenerationTask;
import me.biabani.se.song.tasks.GetLeftSideOfBlockTask;
import me.biabani.se.song.tasks.MasterKeyDerivationTask;
import me.biabani.se.song.tasks.PlainBlocksEncryptionTask;
import me.biabani.se.song.tasks.utilities.SliceByteArrayTask;
import me.biabani.se.song.tasks.utilities.TokenizeStringTask;
import me.biabani.se.song.transformers.WordsToBlocksTransform;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestScenario {

    private final String masterKey = "abcd";

    @BeforeAll
    public static void deleteFilesBefore() {
        System.out.println(new File("sample.txt.bin").delete() ? "encrypted file successfully deleted" : "fresh start of encryption !!!");
        System.out.println(new File("sample.txt.decrypted").delete() ? "decrypted file successfully deleted" : "fresh start of decryption !!!");
    }

    @AfterAll
    public static void deleteFilesAfter() {
        System.out.println(new File("sample.txt.bin").delete() ? "encrypted file successfully deleted. cleaned up..." : "something wrong :)");
        System.out.println(new File("sample.txt.decrypted").delete() ? "decrypted file successfully deleted. cleaned up..." : "something wrong :)");
    }

    @Test
    @Order(1)
    public void SongEncryptionAction_isRunnableWithoutError() {
        SongEncryptionAction.getInstance().accept(new File("src/test/resources/sample.txt"), "sample.txt.bin", masterKey);
        Assertions.assertTrue(new File("sample.txt.bin").exists());
    }

    @Test
    @Order(2)
    public void SongDecryptionAction_isRunnableWithoutError() {
        SongDecryptionAction.getInstance().accept(new File("sample.txt.bin"), "sample.txt.decrypted", masterKey);
        Assertions.assertTrue(new File("sample.txt.decrypted").exists());
    }

    @Test
    @Order(3)
    public void SongSearchInOneFileAction_testIfFindsTheFile() {
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
