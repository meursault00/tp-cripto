package ar.edu.itba.ssshare;

import ar.edu.itba.ssshare.scheme.SecretSharingScheme;
import ar.edu.itba.ssshare.stego.LSBDecoder;
import ar.edu.itba.ssshare.stego.LSBEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static final int HEADER_SIZE_AND_PALETTE = 1078;

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || "-h".equals(args[0]) || "--help".equals(args[0])) {
            printHelp();
            return;
        }

        Map<String, String> params = parseArguments(args);

        String mode = params.get("mode");
        String secret = params.get("secret");
        String kStr = params.get("k");
        String nStr = params.get("n");
        String dir = params.getOrDefault("dir", ".");

        // Validaciones
        if (mode == null || (!mode.equals("d") && !mode.equals("r"))) {
            System.err.println("Error: Debe especificar -d (distribuir) o -r (recuperar).");
            return;
        }
        if (secret == null || kStr == null) {
            System.err.println("Error: Debe especificar -secret <imagen> y -k <número>.");
            return;
        }

        int k, n = -1;
        try {
            k = Integer.parseInt(kStr);
            if (nStr != null) {
                n = Integer.parseInt(nStr);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: k y n deben ser números enteros.");
            return;
        }

        // Imprimir resumen
        System.out.println("Modo: " + (mode.equals("d") ? "Distribuir" : "Recuperar"));
        System.out.println("Imagen secreta: " + secret);
        System.out.println("k: " + k);
        if (mode.equals("d")) {
            if (nStr != null)
                System.out.println("n: " + n);
        }
        System.out.println("Directorio: " + dir);

        // TODO: llamar funciones
        if (mode.equals("d")) {
             distribuir(secret, k, n, dir); //-d -secret examples\secret\boca.bmp -k 8 -n 8 -dir examples\portadoras
        } else {
             recuperar(secret, k, dir); //-r -secret examples\secret\recuperada.bmp -k 8  -dir examples\portadoras\
        }
    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-d" -> map.put("mode", "d");
                case "-r" -> map.put("mode", "r");
                case "-secret" -> {
                    if (i + 1 < args.length) map.put("secret", args[++i]);
                }
                case "-k" -> {
                    if (i + 1 < args.length) map.put("k", args[++i]);
                }
                case "-n" -> {
                    if (i + 1 < args.length) map.put("n", args[++i]);
                }
                case "-dir" -> {
                    if (i + 1 < args.length) map.put("dir", args[++i]);
                }
                default -> {
                    if (!args[i].startsWith("-")) {
                        System.err.println("Argumento desconocido: " + args[i]);
                    }
                }
            }
        }
        return map;
    }

    private static void printHelp() {
        System.out.println("""
            Uso:
              visualSSS -d  -secret <img.bmp> -k <num> [-n <num>] [-dir <path>]
              visualSSS -r  -secret <out.bmp> -k <num> [-dir <path>]
            """);
    }

    public static void distribuir(String secretPath, int k, int n, String dir) throws IOException {

        byte[] secretData = Files.readAllBytes(Paths.get(secretPath)); // por ahora todo el archivo
        byte[] dataToHide = Arrays.copyOfRange(secretData, HEADER_SIZE_AND_PALETTE, secretData.length);


        List<byte[]> shadows = SecretSharingScheme.createShadows(dataToHide, k, n);


        int acum =0;
        for (int i = 0; i < n; i++) {
            Path carrierPath = Paths.get(dir, "cover" + (i+1) + ".bmp");
            byte[] carrier = Files.readAllBytes(carrierPath);

            // Separar header y píxeles

            byte[] header = Arrays.copyOfRange(carrier, 0, HEADER_SIZE_AND_PALETTE);
            byte[] pixels = Arrays.copyOfRange(carrier, HEADER_SIZE_AND_PALETTE, carrier.length);

            // Ocultar sombra en píxeles
            LSBEncoder.embed(pixels, shadows.get(i));
            acum+=shadows.get(i).length;
            // Recombinar y guardar
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(header);
            out.write(pixels);

            Path outPath = Paths.get(dir, "sombra" + (i+1) + ".bmp");
            Files.write(outPath, out.toByteArray());
        }

        System.out.println("total de pixeles de sombra" + acum);

        System.out.println("Sombras embebidas en imágenes guardadas en " + dir);
    }

    public static void recuperar(String outputPath, int k, String dir) throws IOException {
        List<byte[]> shadows = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            Path path = Paths.get(dir, "sombra" + (i + 1) + ".bmp");
            byte[] data = Files.readAllBytes(path);

            byte[] pixels = Arrays.copyOfRange(data, HEADER_SIZE_AND_PALETTE, data.length);

            int len = (data.length - HEADER_SIZE_AND_PALETTE) / 8;
            byte[] shadow = LSBDecoder.extract(pixels, len);

            shadows.add(shadow);
        }

        // Usamos una cover cualquiera para obtener el header BMP (todas tienen el mismo)
        byte[] cover = Files.readAllBytes(Path.of("examples/portadoras/cover1.bmp"));
        byte[] coverHeader = Arrays.copyOfRange(cover, 0, HEADER_SIZE_AND_PALETTE);


        // Recuperamos el secreto sin header
        byte[] secret = SecretSharingScheme.recoverSecret(shadows, k);
        System.out.println(secret.length);

        // Le agregamos el header para que sea un BMP válido
        byte[] secretWithHeader = new byte[coverHeader.length + secret.length];
        System.arraycopy(coverHeader, 0, secretWithHeader, 0, coverHeader.length);
        System.arraycopy(secret, 0, secretWithHeader, coverHeader.length , secret.length);

        Files.write(Paths.get(outputPath), secretWithHeader);
        System.out.println("Secreto reconstruido guardado en " + outputPath);
    }



}