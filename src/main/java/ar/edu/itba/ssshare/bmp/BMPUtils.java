package ar.edu.itba.ssshare.bmp;

public final class BMPUtils {
    private BMPUtils() {}

    /** Devuelve los bytes reservados 6-9 como (seed, index). */
    public static short[] readReserved(byte[] header) {
        short seed  = (short)((header[6]&0xFF) | ((header[7]&0xFF)<<8));
        short index = (short)((header[8]&0xFF) | ((header[9]&0xFF)<<8));
        return new short[]{seed, index};
    }
}
