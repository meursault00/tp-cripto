package ar.edu.itba.ssshare.bmp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Representa un BMP de 8 bits sin compresión. */
public class BMPImage {
    private final BMPHeader header;
    private final byte[] palette; // 1024 bytes (256*4)
    private final byte[] pixels;  // datos brutos

    public BMPImage(BMPHeader header, byte[] palette, byte[] pixels) {
        this.header = header;
        this.palette = palette;
        this.pixels  = pixels;
    }

    // Getters básicos
    public BMPHeader header()        { return header; }
    public byte[]    palette()       { return palette; }
    public byte[]    pixels()        { return pixels;  }
    public int       width()         { return header.getWidth(); }
    public int       height()        { return header.getHeight(); }

    /** Lee un archivo BMP y devuelve la instancia. */
    public static BMPImage read(Path path) throws IOException {
        byte[] all = Files.readAllBytes(path);
        BMPHeader h = BMPHeader.parse(all);
        byte[] pal = new byte[h.paletteSize(all)];
        System.arraycopy(all, 54, pal, 0, pal.length);
        byte[] pix = new byte[all.length - h.pixelArrayOffset()];
        System.arraycopy(all, h.pixelArrayOffset(), pix, 0, pix.length);
        return new BMPImage(h, pal, pix);
    }

    /** Guarda la imagen en disco (sobrescribe si existe). */
    public void write(Path path) throws IOException {
        byte[] out = new byte[header.fileSize()];
        header.write(out);
        System.arraycopy(palette, 0, out, 54, palette.length);
        System.arraycopy(pixels,  0, out, header.pixelArrayOffset(), pixels.length);
        Files.write(path, out);
    }
}
