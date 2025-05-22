package ar.edu.itba.ssshare;

import ar.edu.itba.ssshare.bmp.BMPImage;
import ar.edu.itba.ssshare.scheme.SecretSharingScheme;
import ar.edu.itba.ssshare.stego.LSBEncoder;
import ar.edu.itba.ssshare.stego.LSBDecoder;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0 || "-h".equals(args[0]) || "--help".equals(args[0])) {
            printHelp();
            return;
        }
        // TODO: parsear flags (-d / -r / -secret / -k / -n / -dir)
        System.out.println("Argumentos aún no implementados.");
    }

    private static void printHelp() {
        System.out.println("""
            Uso:
              visualSSS -d  -secret <img.bmp> -k <num> [-n <num>] [-dir <path>]
              visualSSS -r  -secret <out.bmp> -k <num> [-dir <path>]
            """);
    }
}
