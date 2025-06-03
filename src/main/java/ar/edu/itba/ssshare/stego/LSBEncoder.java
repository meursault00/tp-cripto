package ar.edu.itba.ssshare.stego;

public final class LSBEncoder {
    private LSBEncoder() {}

    /** Oculta 'data' en 'pixels' usando 1 LSB por píxel. */
    public static void embed(byte[] pixels, byte[] data) {
        int needed = data.length * 8;
        if (pixels.length < needed) {
            throw new IllegalArgumentException("Imagen demasiado chica para la sombra");
        }
        int p = 0;
        for (byte b : data) {
            for (int bit = 7; bit >= 0; bit--) {
                int v = (b >> bit) & 1;
                pixels[p] = (byte)((pixels[p] & 0xFE) | v);
                p++;
            }
        }
    }

    public static void embed(byte[] pixels, byte[] data, int startPixel) {
        int needed = data.length * 8;
        if (pixels.length < needed) {
            throw new IllegalArgumentException("Imagen demasiado chica para la sombra");
        }
        int p = startPixel;
        for (byte datum : data) {
            for (int bit = 7; bit >= 0; bit--) {
                int bitVal = (datum >> bit) & 1;
                pixels[p] = (byte) ((pixels[p] & 0xFE) | bitVal);
                p++;
            }
        }
    }





    public static void embedHeaderLSB(byte[] pixels, byte[] data) {
        int p = 0;
        for (byte datum : data) {
            for (int bit = 7; bit >= 0; bit--) {
                int bitVal = (datum >> bit) & 1;
                pixels[p] = (byte) ((pixels[p] & 0xFE) | bitVal);
                p++;
            }
        }
    }

}
