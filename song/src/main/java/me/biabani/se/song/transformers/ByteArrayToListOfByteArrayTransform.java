package me.biabani.se.song.transformers;

import me.biabani.se.song.tasks.utilities.SliceByteArrayTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ByteArrayToListOfByteArrayTransform implements BiFunction<byte[], Integer, List<byte[]>> {

    @Override
    public List<byte[]> apply(byte[] bytes, Integer blockSize) {
        List<byte[]> result = new ArrayList<>();
        int count = (int) Math.ceil((double) bytes.length / blockSize);
        for (int i = 0; i < count; i++) {
            result.add(SliceByteArrayTask.getInstance().apply(bytes, i * blockSize, Math.min((i + 1) * blockSize, bytes.length)));
        }
        return result;
    }

    private static ByteArrayToListOfByteArrayTransform singleInstance = null;

    private ByteArrayToListOfByteArrayTransform() {
    }

    public static synchronized ByteArrayToListOfByteArrayTransform getInstance() {
        if (singleInstance == null)
            singleInstance = new ByteArrayToListOfByteArrayTransform();
        return singleInstance;
    }
}
