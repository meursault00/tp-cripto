package ar.edu.itba.ssshare.stego;

public final class LSBEncoder {
    private LSBEncoder() {}

    /** Oculta 'data' en 'pixels' usando 1 LSB por píxel. */
    public static void embed(byte[] pixels, byte[] data, int width, int height) {
        int rowSize = ((width + 3) / 4) * 4;
        int totalBits = data.length * 8;
        int bitIndex = 0;

        for (int row = 0; row < height && bitIndex < totalBits; row++) {
            int rowStart = row * rowSize;
            for (int col = 0; col < width && bitIndex < totalBits; col++) {
                int byteIndex = rowStart + col;
                int bytePos = bitIndex / 8;
                int bitPos = 7 - (bitIndex % 8);
                int bit = (data[bytePos] >> bitPos) & 1;
                pixels[byteIndex] = (byte)((pixels[byteIndex] & 0xFE) | bit);
                bitIndex++;
            }
        }
    }

}
