package me.biabani.se.song.actions;

import me.biabani.se.song.constants.Constants;
import me.biabani.se.song.tasks.MasterKeyDerivationTask;
import me.biabani.se.song.tasks.PseudoRandomNumberGenerationTask;
import me.biabani.se.song.tasks.RemovePaddingsOfBlockTask;
import me.biabani.se.song.tasks.WriteListOfBytesToFileTask;
import me.biabani.se.song.tasks.utilities.*;
import me.biabani.se.song.transformers.ListOfByteArrayToSingleByteArrayTransform;
import me.biabani.se.song.trellis.TriConsumer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: This is done on client's side.
 */
public class SongDecryptionAction implements TriConsumer<File, String, String> {

    private static final int BLOCK_SIZE_IN_BYTES = 16;

    /**
     * Decryption action is consist of several smaller steps which are presented beneath:
     * <ul>
     *     <li>Generate primitives for PRNG, encryption of S<sub>i</sub>, and decryption of X<sub>i</sub></li>
     *     <li>write the file into a byte array</li>
     *     <li>Create PRNG 'G' which is a list of S<sub>i</sub>
     *     <br>
     *     NOTE: we have to know the number of blocks  for 'G', because we used in producing the internal PRNG
     *     </li>
     *     <li>looping through it block by block:
     *     <br>
     *     <ol>
     *         <li>Extract the encrypted block into variable C<sub>i</sub></li>
     *         <li>By XORing C<sub>i</sub> and S<sub>i</sub>, we reach L<sub>i</sub>, according to '4.4 Scheme IV: The Final Scheme paragraph 2'</li>
     *         <li>By encrypting L<sub>i</sub>, we get to K<sub>i</sub></li>
     *         <li>Calculate F<sub>k</sub>(S<sub>i</sub>) by using K<sub>i</sub></li>
     *         <li>Concat S<sub>i</sub> and F<sub>k</sub>(S<sub>i</sub>) to produce T<sub>i</sub></li>
     *         <li>XORing T<sub>i</sub> and C<sub>i</sub> results in X<sub>i</sub></li>
     *         <li>Decrypting X<sub>i</sub> with primitives computed upper will give us W<sub>i</sub> which is padded</li>
     *         <li>After removing the padding, we have the raw format of blocks.</li>
     *     </ol>
     *     </li>
     * </ul>
     * NOTE: The file must have been encrypted with the same primitives.
     *
     * @param file the file to be decrypted
     * @param outputPath the path we want to save the output file
     * @param masterKey user's password
     */
    @Override
    public void accept(File file, String outputPath, String masterKey) {
        try {
            byte[] masterKeyDerivationForSi = MasterKeyDerivationTask.getInstance().apply(masterKey, Constants.SI_SALT, BLOCK_SIZE_IN_BYTES);
            byte[] masterKeyDerivationForKi = MasterKeyDerivationTask.getInstance().apply(masterKey, Constants.KI_SALT, BLOCK_SIZE_IN_BYTES);
            byte[] kiEncryptionKey = SliceByteArrayTask.getInstance().apply(masterKeyDerivationForKi, 0, BLOCK_SIZE_IN_BYTES);
            byte[] kiEncryptionIV = SliceByteArrayTask.getInstance().apply(masterKeyDerivationForKi, BLOCK_SIZE_IN_BYTES, BLOCK_SIZE_IN_BYTES * 2);
            byte[] masterKeyDerivationForXi = MasterKeyDerivationTask.getInstance().apply(masterKey, Constants.XI_SALT, BLOCK_SIZE_IN_BYTES);
            byte[] xiDecryptionKey = SliceByteArrayTask.getInstance().apply(masterKeyDerivationForXi, 0, BLOCK_SIZE_IN_BYTES);
            byte[] xiDecryptionIV = SliceByteArrayTask.getInstance().apply(masterKeyDerivationForXi, BLOCK_SIZE_IN_BYTES, BLOCK_SIZE_IN_BYTES * 2);

            byte[] bytes = Files.readAllBytes(file.toPath());
            int numberOfBlocks = bytes.length / BLOCK_SIZE_IN_BYTES;

            // calculate pseudo-random number generator
            List<byte[]> G = PseudoRandomNumberGenerationTask.getInstance().apply(
                    numberOfBlocks,
                    BLOCK_SIZE_IN_BYTES,
                    SliceByteArrayTask.getInstance().apply(masterKeyDerivationForSi, 0, BLOCK_SIZE_IN_BYTES),
                    SliceByteArrayTask.getInstance().apply(masterKeyDerivationForSi, BLOCK_SIZE_IN_BYTES, BLOCK_SIZE_IN_BYTES * 2)
            );

            List<byte[]> plainBlocks = new ArrayList<>();

            for (int i = 0; i < numberOfBlocks; i++) {
                byte[] ci = SliceByteArrayTask.getInstance().apply(bytes, i * BLOCK_SIZE_IN_BYTES, (i + 1) * BLOCK_SIZE_IN_BYTES);
                byte[] si = SliceByteArrayWithCustomOutputLengthTask.getInstance().apply(G.get(i), 0, BLOCK_SIZE_IN_BYTES / 2, BLOCK_SIZE_IN_BYTES, 0);

                byte[] xorOfSiAndCi = TwoByteArrayXorCalculationTask.getInstance().apply(ci, si);
                byte[] li = SliceByteArrayWithCustomOutputLengthTask.getInstance().apply(xorOfSiAndCi, 0, BLOCK_SIZE_IN_BYTES / 2, BLOCK_SIZE_IN_BYTES, 0);
                byte[] ki = AesCbcEncryptionTask.getInstance().apply(kiEncryptionKey, kiEncryptionIV, li);

                byte[] bigF = AesEcbEncryptionTask.getInstance().apply(ki, si);
                byte[] ti = ListOfByteArrayToSingleByteArrayTransform.getInstance().apply(
                        List.of(SliceByteArrayTask.getInstance().apply(si, 0, BLOCK_SIZE_IN_BYTES / 2),
                                SliceByteArrayTask.getInstance().apply(bigF, 0, BLOCK_SIZE_IN_BYTES / 2))
                );

                byte[] xi = TwoByteArrayXorCalculationTask.getInstance().apply(ci, ti);
                byte[] wi = AesCbcDecryptionTask.getInstance().apply(xiDecryptionKey, xiDecryptionIV, xi);

                plainBlocks.add(RemovePaddingsOfBlockTask.getInstance().apply(wi));
            }

            WriteListOfBytesToFileTask.getInstance().accept(outputPath, plainBlocks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static SongDecryptionAction singleInstance = null;

    private SongDecryptionAction() {
    }

    public static synchronized SongDecryptionAction getInstance() {
        if (singleInstance == null)
            singleInstance = new SongDecryptionAction();
        return singleInstance;
    }
}
