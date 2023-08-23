package me.biabani.se.song.tasks.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class TwoByteArrayListXorCalculationTask implements BiFunction<List<byte[]>, List<byte[]>, List<byte[]>> {

    @Override
    public List<byte[]> apply(List<byte[]> l1, List<byte[]> l2) {
        assert l1.size() == l2.size();
        List<byte[]> xoredList = new ArrayList<>();
        for (int i = 0; i < l1.size(); i++) {
            xoredList.add(TwoByteArrayXorCalculationTask.getInstance().apply(l1.get(i), l2.get(i)));
        }
        return xoredList;
    }

    private static TwoByteArrayListXorCalculationTask singleInstance = null;

    private TwoByteArrayListXorCalculationTask() {
    }

    public static synchronized TwoByteArrayListXorCalculationTask getInstance() {
        if (singleInstance == null)
            singleInstance = new TwoByteArrayListXorCalculationTask();
        return singleInstance;
    }
}
