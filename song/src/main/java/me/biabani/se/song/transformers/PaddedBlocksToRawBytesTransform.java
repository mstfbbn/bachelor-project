package me.biabani.se.song.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PaddedBlocksToRawBytesTransform implements Function<List<byte[]>, List<byte[]>> {

    @Override
    public List<byte[]> apply(List<byte[]> paddedBlocks) {
        List<byte[]> plains = new ArrayList<>();

        for (byte[] block : paddedBlocks) {

        }

        return plains;
    }
}
