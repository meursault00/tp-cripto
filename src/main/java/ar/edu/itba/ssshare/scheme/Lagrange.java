package ar.edu.itba.ssshare.scheme;

import java.math.BigInteger;
import java.util.*;

public class Lagrange {

    static final int MOD =257;

    static int modInverse(int a) {
        return BigInteger.valueOf(a).modInverse(BigInteger.valueOf(MOD)).intValue();
    }

    static int mod(int a) {
        a %= MOD;
        return (a < 0) ? a + MOD : a;
    }

    static int interpolateAtZero(int[] x, int[] y) {
        int result = 0;
        for (int i = 0; i < x.length; i++) {
            int numerator = 1;
            int denominator = 1;
            for (int j = 0; j < x.length; j++) {
                if (i != j) {
                    numerator = mod(numerator * -x[j]);
                    denominator = mod(denominator * (x[i] - x[j]));
                }
            }
            int li0 = mod(numerator * modInverse(denominator));
            result = mod(result + li0 * y[i]);
        }
        return result;
    }

}
