package ar.edu.itba.ssshare.scheme;

import java.math.BigInteger;
import java.util.*;

public class Lagrange {

    static int modInverse(int a) {
        return BigInteger.valueOf(mod(a)).modInverse(BigInteger.valueOf(Polynomial.P)).intValue();
    }

    static long mod(long a) {
        a %= Polynomial.P;
        return (a < 0) ? a + Polynomial.P : a;
    }

    public static int interpolateAtZero(int[] x, int[] y) {
        int result = 0;
        int k = x.length;

        for (int i = 0; i < k; i++) {
            long numerator = 1;
            long denominator = 1;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                numerator = mod(numerator * (-x[j]));
                denominator = mod(denominator * (x[i] - x[j]));
            }

            long li0 = mod(numerator * modInverse((int) denominator));
            result = (int) mod(result + li0 * y[i]);
        }

        return result;
    }


}
