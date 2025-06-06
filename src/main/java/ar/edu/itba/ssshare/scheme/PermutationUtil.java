package ar.edu.itba.ssshare.scheme;

import java.util.Random;

public final class PermutationUtil {
    private PermutationUtil() {}

    /** XOR a partir de la semilla. */
    public static void xorWithRandomBits(byte[] pixels, int seed) {
        Random r = new Random(seed);
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] ^= (byte) r.nextInt(256);
        }
    }
}