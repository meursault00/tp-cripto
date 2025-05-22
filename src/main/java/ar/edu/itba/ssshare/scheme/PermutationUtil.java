package ar.edu.itba.ssshare.scheme;

import java.util.Random;

public final class PermutationUtil {
    private PermutationUtil() {}

    /** Fisher–Yates shuffle reproducible. */
    public static int[] generate(int size, long seed) {
        int[] p = new int[size];
        for (int i = 0; i < size; i++) p[i] = i;
        Random r = new Random(seed);
        for (int i = size - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int tmp = p[i]; p[i] = p[j]; p[j] = tmp;
        }
        return p;
    }

    /** Inversa de la permutación. */
    public static int[] invert(int[] perm) {
        int[] inv = new int[perm.length];
        for (int i = 0; i < perm.length; i++) {
            inv[perm[i]] = i;
        }
        return inv;
    }
}
