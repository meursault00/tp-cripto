package ar.edu.itba.ssshare.stego;

public final class LSBDecoder {
    private LSBDecoder() {}

    /** Extrae 'len' bytes ocultos de 'pixels' usando 1 LSB por píxel, respetando padding BMP. */
    public static byte[] extract(byte[] pixels, int len, int width, int height) {
        int rowSize = ((width + 3) / 4) * 4;
        byte[] out = new byte[len];
        int bitIndex = 0;

        for (int row = 0; row < height && bitIndex < len * 8; row++) {
            int rowStart = row * rowSize;
            for (int col = 0; col < width && bitIndex < len * 8; col++) {
                int byteIndex = rowStart + col;
                int bit = pixels[byteIndex] & 1;
                out[bitIndex / 8] |= (bit << (7 - (bitIndex % 8)));
                bitIndex++;
            }
        }

        return out;
    }

}
