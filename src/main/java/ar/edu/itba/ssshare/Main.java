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
             distribuir(secret, k, n, dir); //-d -secret examples\secret\boca32x32.bmp -k 8 -n 8 -dir examples\portadoras
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

        List<byte[]> shadows = SecretSharingScheme.createShadows(secretData, k, n);

        for (int i = 0; i < n; i++) {
            Path carrierPath = Paths.get(dir, "c" + (i+1) + ".bmp");
            byte[] carrier = Files.readAllBytes(carrierPath);

            // Separar header y píxeles
            int headerSize = 54; // BMP clásico
            byte[] header = Arrays.copyOfRange(carrier, 0, headerSize);
            byte[] pixels = Arrays.copyOfRange(carrier, headerSize, carrier.length);

            // Ocultar sombra en píxeles
            LSBEncoder.embed(pixels, shadows.get(i));

            // Recombinar y guardar
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(header);
            out.write(pixels);

            Path outPath = Paths.get(dir, "sombra" + (i+1) + ".bmp");
            Files.write(outPath, out.toByteArray());
        }

        System.out.println("Sombras embebidas en imágenes guardadas en " + dir);
    }

    public static void recuperar(String outputPath, int k, String dir) throws IOException {
        List<byte[]> shadows = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            Path path = Paths.get(dir, "sombra" + (i+1) + ".bmp");
            byte[] data = Files.readAllBytes(path);

            int headerSize = 54;
            byte[] pixels = Arrays.copyOfRange(data, headerSize, data.length);

            // Asumimos que todas las sombras tienen el mismo largo (por ejemplo 1024)
            // Podés inferir esto o pasarlo como parámetro (más robusto)
            int len = (data.length - headerSize) / 8; // o un valor fijo si conocés la longitud original

            byte[] shadow = LSBDecoder.extract(pixels, len);
            shadows.add(shadow);
        }

        byte[] secret = SecretSharingScheme.recoverSecret(shadows, k);
        Files.write(Paths.get(outputPath), secret);
        System.out.println("Secreto reconstruido guardado en " + outputPath);
    }


}