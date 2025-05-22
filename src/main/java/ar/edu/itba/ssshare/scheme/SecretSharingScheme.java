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

    // TODO: recoverSecret(...)
}
