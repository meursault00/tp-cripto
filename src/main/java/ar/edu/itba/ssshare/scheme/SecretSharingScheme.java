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
                a[i] = Math.min(v, 250);           // truncación estilo Thien-Lin
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

        int[] xs = new int[k];
        for (int i = 0; i < k; i++) {
            xs[i] = i + 1; // x-values: 1, 2, 3, ...
        }

        for (int b = 0; b < blocks; b++) {
            int[] ys = new int[k];
            for (int i = 0; i < k; i++) {
                ys[i] = Byte.toUnsignedInt(shadows.get(i)[b]);
            }

            // Versión encajada: P(x) = ((s_k x + s_{k-1}) x + ...) + s_1
            for (int coeffIndex = 0; coeffIndex < k; coeffIndex++) {
                // Interpolar en x = 0
                int coeff = Lagrange.interpolateAtZero(xs, ys);
                secret[b * k + coeffIndex] = (byte) coeff;

                // Calcular nuevos ys para la siguiente iteración
                for (int i = 0; i < k; i++) {
                    int numerator = Lagrange.mod(ys[i] - coeff);
                    ys[i] = Lagrange.mod(numerator * Lagrange.modInverse(xs[i]));
                }
            }
        }

        return secret;
    }




}
