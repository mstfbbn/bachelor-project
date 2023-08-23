package me.biabani.se.song.actions;

import me.biabani.se.song.tasks.EncryptPseudoRandomNumberTask;
import me.biabani.se.song.tasks.utilities.CheckTwoByteArrayEqualityTask;
import me.biabani.se.song.tasks.utilities.SliceByteArrayTask;
import me.biabani.se.song.tasks.utilities.SliceByteArrayWithCustomOutputLengthTask;
import me.biabani.se.song.tasks.utilities.TwoByteArrayXorCalculationTask;
import me.biabani.se.song.trellis.TriFunction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SongSearchInOneFileAction implements TriFunction<File, List<byte[]>, List<byte[]>, Boolean> {

    /**
     * Searching in a file is a series of small tasks. First, the file is read to a byte array, and it will be iterated block by block.
     * For each block, routines are run as below:
     * <ol>
     * <li>Extract the encrypted block into variable C<sub>i</sub></li>
     * <li>By XORing C<sub>i</sub> and X<sub>i</sub>, we reach T<sub>i</sub></li>
     * <li>Divide T<sub>i</sub> to &lt;S<sub>i</sub>, F<sub>k</sub>(S<sub>i</sub>)&gt;</li>
     * <li>Calculate F<sub>k</sub>(S<sub>i</sub>) again by using K<sub>i</sub> provided by user</li>
     * <li>if calculated F and the one extracted from the file are equal, it means that the computations are valid and the word exists in this file</li>
     * </ol>
     *
     * @param xi the word to be searched (X<sub>i</sub>)
     * @param ki k<sub>i</sub> is used for encryption of s<sub>i</sub>
     * @return whether the word exists in file or not
     */
    @Override
    public Boolean apply(File file, List<byte[]> xi, List<byte[]> ki) {
        try {
            assert xi.size() == ki.size();
            assert !xi.isEmpty();
            int blockSizeInBytes = xi.get(0).length;
            byte[] bytes = Files.readAllBytes(file.toPath());
            int numberOfBlocks = bytes.length / blockSizeInBytes;
            int numberOfIterations = numberOfBlocks - xi.size() + 1;
            for (int i = 0; i < numberOfIterations; i++) {
                boolean match = true;
                for (int j = 0; j < xi.size(); j++) {
                    byte[] ci = SliceByteArrayTask.getInstance().apply(bytes, (i + j) * blockSizeInBytes, (i + j + 1) * blockSizeInBytes);
                    byte[] ti = TwoByteArrayXorCalculationTask.getInstance().apply(xi.get(j), ci);

                    byte[] s = SliceByteArrayWithCustomOutputLengthTask.getInstance().apply(ti, 0, blockSizeInBytes / 2, blockSizeInBytes, 0);
                    byte[] bigF = SliceByteArrayWithCustomOutputLengthTask.getInstance().apply(ti, blockSizeInBytes / 2, blockSizeInBytes, blockSizeInBytes, 0);

                    byte[] calculatedF = EncryptPseudoRandomNumberTask.getInstance().apply(ki.get(j), s);

                    boolean matchInternal = CheckTwoByteArrayEqualityTask.getInstance().apply(bigF, calculatedF, blockSizeInBytes / 2);
                    if (!matchInternal) {
                        match = false;
                        break;
                    } else {
                        System.out.printf("hello");
                    }
                }
                if (match) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private static SongSearchInOneFileAction singleInstance = null;

    private SongSearchInOneFileAction() {
    }

    public static synchronized SongSearchInOneFileAction getInstance() {
        if (singleInstance == null)
            singleInstance = new SongSearchInOneFileAction();
        return singleInstance;
    }
}
