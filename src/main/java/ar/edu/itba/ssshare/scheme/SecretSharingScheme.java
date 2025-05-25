package ar.edu.itba.ssshare.scheme;

import java.util.ArrayList;
import java.util.List;

/** Implementación mínimal de createShadows (k fijo a 8). */
public final class SecretSharingScheme {

    public static List<byte[]> createShadows(byte[] permuted, int k, int n) {
        if (k != 8) throw new UnsupportedOperationException("Solo k=8 por ahora");
        int blocks = permuted.length / k;
        List<byte[]> shadows = new ArrayList<>();
        for (int i = 0; i < n; i++) shadows.add(new byte[blocks]);

        for (int b = 0; b < blocks; b++) {
            int[] a = new int[k];
            for (int i = 0; i < k; i++) {
                int v = Byte.toUnsignedInt(permuted[b*k + i]);
                a[i] = (v > 250) ? 250 : v;           // truncación estilo Thien-Lin
            }
            Polynomial poly = new Polynomial(a);
            for (int j = 0; j < n; j++) {
                int y = poly.eval(j + 1);             // x = 1..n
                shadows.get(j)[b] = (byte) y;
            }
        }
        return shadows;
    }

    public static byte[] recoverSecret(List<byte[]> shadows, int k) {
        if (shadows.size() < k)
            throw new IllegalArgumentException("Se necesitan al menos k sombras");

        int blocks = shadows.get(0).length;
        byte[] secret = new byte[blocks * k];

        for (int b = 0; b < blocks; b++) {
            int[] x = new int[k];
            int[] y = new int[k];
            for (int i = 0; i < k; i++) {
                x[i] = i + 1; // x_i = 1..k (mismo que en distribución)
                y[i] = Byte.toUnsignedInt(shadows.get(i)[b]);
            }

            // Interpolar en x=0 para obtener los k coeficientes originales
            for (int j = 0; j < k; j++) {
                int[] shiftedY = new int[k];
                for (int m = 0; m < k; m++) {
                    // Evaluar en x=0 del polinomio multiplicado por x^j
                    shiftedY[m] = (y[m] * pow(x[m], j)) % Polynomial.P;
                }
                int coeff = Lagrange.interpolateAtZero(x, shiftedY);
                secret[b * k + j] = (byte) coeff;
            }
        }

        return secret;
    }

    private static int pow(int base, int exp) {
        long res = 1;
        long b = base % Polynomial.P;
        while (exp > 0) {
            if ((exp & 1) == 1) res = (res * b) % Polynomial.P;
            b = (b * b) % Polynomial.P;
            exp >>= 1;
        }
        return (int) res;
    }

}
