package me.biabani.se.song.tasks;

import me.biabani.se.song.tasks.utilities.AesEcbEncryptionTask;
import me.biabani.se.song.tasks.utilities.SliceByteArrayTask;
import me.biabani.se.song.tasks.utilities.SliceByteArrayWithCustomOutputLengthTask;
import me.biabani.se.song.transformers.ListOfByteArrayToSingleByteArrayTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CalculateTiTask implements BiFunction<List<byte[]>, List<byte[]>, List<byte[]>> {

    @Override
    public List<byte[]> apply(List<byte[]> ki, List<byte[]> si) {
        assert ki.size() == si.size();

        List<byte[]> ti = new ArrayList<>();
        for (int i = 0; i < si.size(); i++) {
            byte[] smallS = SliceByteArrayWithCustomOutputLengthTask.getInstance().apply(si.get(i), 0, si.get(i).length / 2, si.get(i).length, 0);
            ti.add(
                    ListOfByteArrayToSingleByteArrayTransform.getInstance()
                            .apply(
                                    List.of(
                                            SliceByteArrayTask.getInstance().apply(smallS, 0, smallS.length / 2),
                                            SliceByteArrayTask.getInstance().apply(AesEcbEncryptionTask.getInstance().apply(ki.get(i), smallS), 0, smallS.length / 2)
                                    )));
        }
        return ti;
    }

    private static CalculateTiTask singleInstance = null;

    private CalculateTiTask() {
    }

    public static synchronized CalculateTiTask getInstance() {
        if (singleInstance == null)
            singleInstance = new CalculateTiTask();
        return singleInstance;
    }
}
