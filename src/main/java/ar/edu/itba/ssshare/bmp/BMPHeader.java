package ar.edu.itba.ssshare.bmp;

/** Encapsula los 54 bytes iniciales de un BMP. Solo getters/setters clave. */
public class BMPHeader {
    private final int fileSize;
    private final int pixelArrayOffset;
    private final int width;
    private final int height;
    private final short seed;       // bytes 6-7 reservados
    private final short shareIndex; // bytes 8-9 reservados

    // --- constructor y parse estático ---

    private BMPHeader(int fileSize, int pixelArrayOffset,
                      int width, int height,
                      short seed, short shareIndex) {
        this.fileSize = fileSize;
        this.pixelArrayOffset = pixelArrayOffset;
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.shareIndex = shareIndex;
    }

    public int paletteSize(byte[] raw) {
        int bpp = little16(raw, 28);
        return (bpp <= 8) ? (1 << bpp) * 4 : 0;
    }


    public static BMPHeader parse(byte[] raw) {
        // TODO: validar firma "BM", profundidad 8 bpp, sin compresión.
        int size  = little32(raw, 2);
        short seed        = little16(raw, 6);
        short shareIndex  = little16(raw, 8);
        int offset = little32(raw, 10);
        int w      = little32(raw, 18);
        int h      = little32(raw, 22);
        return new BMPHeader(size, offset, w, h, seed, shareIndex);
    }

    public void write(byte[] dest) {
        // TODO: serializar los 54 bytes (por ahora asumimos ya hay plantilla).
        throw new UnsupportedOperationException("write header TODO");
    }

    // ---------------- little-endian helpers ----------------
    private static int little32(byte[] a, int off) {
        return (a[off]&0xFF)       |
              ((a[off+1]&0xFF)<<8) |
              ((a[off+2]&0xFF)<<16)|
              ((a[off+3]&0xFF)<<24);
    }
    private static short little16(byte[] a, int off) {
        return (short)((a[off]&0xFF) | ((a[off+1]&0xFF)<<8));
    }

    // getters clave
    public int   fileSize()          { return fileSize; }
    public int   pixelArrayOffset()  { return pixelArrayOffset; }
    public int   getWidth()          { return width; }
    public int   getHeight()         { return height; }
    public short getSeed()           { return seed; }
    public short getShareIndex()     { return shareIndex; }
}
