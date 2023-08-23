package me.biabani.se.song.tasks.utilities;

import java.util.function.BiFunction;

/**
 * A good feature of XOR is that it's reversible. it means by having two out of three of the parameters, you can reach the third one. <br>
 * For example, if a ^ b = c, then b ^ c = a
 */
public class TwoByteArrayXorCalculationTask implements BiFunction<byte[], byte[], byte[]> {

    @Override
    public byte[] apply(byte[] b1, byte[] b2) {
        assert b1.length == b2.length;
        byte[] xor = new byte[b1.length];
        for (int i = 0; i < b1.length; i++) {
            xor[i] = (byte) (b1[i] ^ b2[i]);
        }
        return xor;
    }

    private static TwoByteArrayXorCalculationTask singleInstance = null;

    private TwoByteArrayXorCalculationTask() {
    }

    public static synchronized TwoByteArrayXorCalculationTask getInstance() {
        if (singleInstance == null)
            singleInstance = new TwoByteArrayXorCalculationTask();
        return singleInstance;
    }
}
