package ar.edu.itba.ssshare.scheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Implementación mínimal de createShadows (k fijo a 8). */
public final class SecretSharingScheme {

    public static List<byte[]> createShadows(byte[] permuted, int k, int n) {
        if (k != 8) throw new UnsupportedOperationException("Solo k=8 por ahora");

        int blocks = permuted.length / k;
        System.out.println("blocks: " + blocks);
        System.out.println(permuted.length / k);
        List<byte[]> shadows = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            shadows.add(new byte[blocks]);
        }

        for (int b = 0; b < blocks; b++) {
            // Convertimos los k bytes del bloque a coeficientes enteros sin truncar
            int[] a = new int[k];
            for (int i = 0; i < k; i++) {
                a[i] = Byte.toUnsignedInt(permuted[b * k + i]);
            }

            // Repetimos hasta que ningún f(x) dé 256
            boolean ok;
            do {
                ok = true;
                Polynomial poly = new Polynomial(a);
                for (int j = 0; j < n; j++) {
                    int y = poly.eval(j + 1);
                    if (y == 256) { //NO QUIRO LOS 256 >:(
                        ok = false;
                        for (int t = 0; t < k; t++) {
                            if (a[t] != 0) {
                                a[t]--;
                                break;
                            }
                        }
                        break; // salir del for y volver a verificar todos los j
                    }
                }
            } while (!ok);

            // Usamos los coeficientes ajustados para calcular cada sombra
            Polynomial poly = new Polynomial(a);
            for (int j = 0; j < n; j++) {
                int y = poly.eval(j + 1); // garantizado ∈ [0,255]
                shadows.get(j)[b] = (byte) y;
            }
        }

        return shadows;
    }


    public static byte[] recoverSecret(List<byte[]> shadows, int k) {
        if (shadows.size() < k)
            throw new IllegalArgumentException("Se necesitan al menos k sombras");

        int blocks = shadows.get(0).length;
        byte[] fullRecovered = new byte[blocks * k];

        int[] xs = new int[k];
        for (int i = 0; i < k; i++) xs[i] = i + 1;

        for (int b = 0; b < blocks; b++) {
            int[] ys = new int[k];
            for (int i = 0; i < k; i++) {
                ys[i] = Byte.toUnsignedInt(shadows.get(i)[b]);
            }

            for (int coeffIndex = 0; coeffIndex < k; coeffIndex++) {
                int coeff = Lagrange.interpolateAtZero(xs, ys);
                fullRecovered[b * k + coeffIndex] = (byte) (coeff );

                for (int i = 0; i < k; i++) {
                    long numerator = Lagrange.mod(ys[i] - coeff);
                    ys[i] = (int) Lagrange.mod( numerator * Lagrange.modInverse(xs[i]));
                }
            }
        }

        // Return only original data, not padded
        return Arrays.copyOf(fullRecovered,fullRecovered.length);
    }





}
