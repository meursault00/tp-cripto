package ar.edu.itba.ssshare.stego;

public final class LSBDecoder {
    private LSBDecoder() {}

    /** Extrae 'len' bytes ocultos de 'pixels' usando 1 LSB por píxel. */
    public static byte[] extract(byte[] pixels, int len) {
        if (pixels.length < len * 8)
            throw new IllegalArgumentException("No hay suficientes píxeles");
        byte[] out = new byte[len];
        int p = 0;
        for (int i = 0; i < len; i++) {
            int v = 0;
            for (int bit = 7; bit >= 0; bit--) {
                v |= (pixels[p++] & 1) << bit;
            }
            out[i] = (byte) v;
        }
        return out;
    }

    public static byte[] extract(byte[] pixels, int len, int startPixel) {

        if (pixels.length <  len * 8)
            throw new IllegalArgumentException("No hay suficientes píxeles");

        byte[] out = new byte[len];
        int p = startPixel;

        for (int i = 0; i < len; i++) {
            int v = 0;
            for (int bit = 7; bit >= 0; bit--) {
                v |= (pixels[p++] & 1) << bit;
            }
            out[i] = (byte) v;
        }
        return out;
    }



    public static byte[] extractHeaderLSB(byte[] pixels, int bytes) {
        byte[] out = new byte[bytes];
        int p = 0;
        for (int i = 0; i < bytes; i++) {
            int v = 0;
            for (int bit = 7; bit >= 0; bit--) {
                v |= (pixels[p++] & 1) << bit;
            }
            out[i] = (byte) v;
        }
        return out;
    }

}