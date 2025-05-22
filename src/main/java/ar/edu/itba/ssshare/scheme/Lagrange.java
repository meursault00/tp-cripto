package ar.edu.itba.ssshare.scheme;

public final class Lagrange {
    private static final int P = Polynomial.P;

    private Lagrange() {}

    /** Devuelve f(0) usando k pares (xi, yi) mod P. k <= 10 en el TP. */
    public static int interpolateAtZero(int[] xs, int[] ys) {
        int k = xs.length;
        int result = 0;
        for (int i = 0; i < k; i++) {
            long num = ys[i];
            long denom = 1;
            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                num   = (num   * (-xs[j])) % P;   // (0 - xj)
                denom = (denom * (xs[i] - xs[j])) % P;
            }
            result = (int)((result + num * modInv(denom)) % P);
        }
        return (result + P) % P;
    }

    /** Inverso multiplicativo mod P (P es primo). */
    private static int modInv(long a) {
        return pow(a, P - 2);
    }

    private static int pow(long base, int exp) {
        long res = 1;
        long b   = base % P;
        while (exp > 0) {
            if ((exp & 1) == 1) res = (res * b) % P;
            b = (b * b) % P;
            exp >>= 1;
        }
        return (int) res;
    }
}
