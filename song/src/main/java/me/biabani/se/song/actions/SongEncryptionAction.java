package me.biabani.se.song.actions;

import me.biabani.se.song.constants.Constants;
import me.biabani.se.song.tasks.*;
import me.biabani.se.song.tasks.utilities.SliceByteArrayTask;
import me.biabani.se.song.tasks.utilities.TwoByteArrayListXorCalculationTask;
import me.biabani.se.song.transformers.WordsToBlocksTransform;
import me.biabani.se.song.trellis.TriConsumer;

import java.io.File;
import java.util.List;

/**
 * NOTE: Although we made some sort of flexibility in size of blocks for utility tasks.
 * We restrict blockSize to be 16, because of using AES-128 for encryption which is secure and the fact that the increase in block size leads to an unnecessary corresponding increase in file size.
 */
public class SongEncryptionAction implements TriConsumer<File, String, String> {

    private final static int BLOCK_SIZE_IN_BYTES = 16;

    /**
     * encryption phase goes through several tasks.
     * <ul>
     *     <li>tokenize file to a list of words</li>
     *     <li>transform tokens to fixed-size blocks</li>
     *     <li>encrypt plain blocks to add hidden search capability</li>
     *     <li>generate key for each block from encrypting the block to add controlled search feature</li>
     *     <li>generate a pseudo-random number generator function (named as 'G' in article) producing S<sub>i</sub>s</li>
     *     <li>encrypt S<sub>i</sub>s resulting F(s<sub>i</sub>)</li>
     *     <li>concat S<sub>i</sub> with its encryption producing in T<sub>i</sub></li>
     *     <li>XOR X<sub>i</sub> and T<sub>i</sub> reaching C<sub>i</sub></li>
     *     <li>saving C in file</li>
     * </ul>
     * NOTE: Saving to file is done locally. But, if it's going to be used in a real-world application, some API call functionality could be added here.
     *
     * @param file      the file to be encrypted
     * @param outputPath the path we want to save the output file
     * @param masterKey client's masterKey
     */
    @Override
    public void accept(File file, String outputPath, String masterKey) {

        byte[] masterKeyDerivationForXi = MasterKeyDerivationTask.getInstance().apply(masterKey, Constants.XI_SALT, BLOCK_SIZE_IN_BYTES);
        List<byte[]> xi = PlainBlocksEncryptionTask.getInstance().apply(
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForXi, 0, BLOCK_SIZE_IN_BYTES),
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForXi, BLOCK_SIZE_IN_BYTES, BLOCK_SIZE_IN_BYTES * 2),
                WordsToBlocksTransform.getInstance().apply(
                        FileTokenizationTask.getInstance().apply(file),
                        BLOCK_SIZE_IN_BYTES
                )
        );

        byte[] masterKeyDerivationForSi = MasterKeyDerivationTask.getInstance().apply(masterKey, Constants.SI_SALT, BLOCK_SIZE_IN_BYTES);
        List<byte[]> G = PseudoRandomNumberGenerationTask.getInstance().apply(
                xi.size(),
                BLOCK_SIZE_IN_BYTES,
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForSi, 0, BLOCK_SIZE_IN_BYTES),
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForSi, BLOCK_SIZE_IN_BYTES, BLOCK_SIZE_IN_BYTES * 2)
        );


        List<byte[]> li = GetLeftSideOfBlockTask.getInstance().apply(xi);

        byte[] masterKeyDerivationForKi = MasterKeyDerivationTask.getInstance().apply(masterKey, Constants.KI_SALT, BLOCK_SIZE_IN_BYTES);
        List<byte[]> ki = BlockBasedKeyGenerationTask.getInstance().apply(
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForKi, 0, BLOCK_SIZE_IN_BYTES),
                SliceByteArrayTask.getInstance().apply(masterKeyDerivationForKi, BLOCK_SIZE_IN_BYTES, BLOCK_SIZE_IN_BYTES * 2),
                li
        );

        List<byte[]> ti = CalculateTiTask.getInstance().apply(ki, G);
        List<byte[]> ci = TwoByteArrayListXorCalculationTask.getInstance().apply(xi, ti);

        WriteListOfBytesToFileTask.getInstance().accept(outputPath, ci);
    }

    private static SongEncryptionAction singleInstance = null;

    private SongEncryptionAction() {
    }

    public static synchronized SongEncryptionAction getInstance() {
        if (singleInstance == null)
            singleInstance = new SongEncryptionAction();
        return singleInstance;
    }
}
