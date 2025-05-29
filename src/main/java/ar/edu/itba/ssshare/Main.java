package ar.edu.itba.ssshare;

import ar.edu.itba.ssshare.scheme.PermutationUtil;
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

    private static int readIntLE(byte[] header, int offset) {
        return (header[offset] & 0xFF) |
                ((header[offset + 1] & 0xFF) << 8) |
                ((header[offset + 2] & 0xFF) << 16) |
                ((header[offset + 3] & 0xFF) << 24);
    }


    public static void distribuir(String secretPath, int k, int n, String dir) throws IOException {
        byte[] secretData = Files.readAllBytes(Paths.get(secretPath));
        byte[] dataToHide = Arrays.copyOfRange(secretData, HEADER_SIZE_AND_PALETTE, secretData.length);

        // Generar semilla de 48 bits (hasta 2^48 - 1)
        Random rand = new Random();
        int seed = rand.nextInt(1 << 16); // genera número entre 0 y 65535
        int[] permutation = PermutationUtil.generate(dataToHide.length, seed);

        // Aplicar permutación
        byte[] permuted = new byte[dataToHide.length];
        for (int i = 0; i < permutation.length; i++) {
            permuted[i] = dataToHide[permutation[i]];
        }

        List<byte[]> shadows = SecretSharingScheme.createShadows(permuted, k, n);

        for (int i = 0; i < n; i++) {
            Path carrierPath = Paths.get(dir, "c" + (i + 1) + "450.bmp");
            byte[] carrier = Files.readAllBytes(carrierPath);

            // Header y píxeles
            byte[] header = Arrays.copyOfRange(carrier, 0, HEADER_SIZE_AND_PALETTE);
            byte[] pixels = Arrays.copyOfRange(carrier, HEADER_SIZE_AND_PALETTE, carrier.length);

            // Guardar semilla en bytes 6–7 (solo los 2 bytes menos significativos)
            header[6] = (byte) (seed & 0xFF);
            header[7] = (byte) ((seed >> 8) & 0xFF);


            // Guardar orden en bytes 8–9(solo los 2 bytes menos significativos)
            int orden = i;
            header[8] = (byte) (orden & 0xFF);
            header[9] = (byte) ((orden >> 8) & 0xFF);


            // Ocultar sombra en píxeles
            LSBEncoder.embed(pixels, shadows.get(i));

            // Recombinar
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(header);
            out.write(pixels);

            Path outPath = Paths.get(dir, "sombra" + (i + 1) + ".bmp");
            Files.write(outPath, out.toByteArray());
        }

        System.out.println("Sombras embebidas con semilla " + seed + " en imágenes guardadas en " + dir);
    }


    public static void recuperar(String outputPath, int k, String dir) throws IOException {

        int seed =0;
        Map<Integer,byte[]> xValueMap =  new TreeMap<>();

        for (int i = 0; i < k; i++) {
            Path path = Paths.get(dir, "sombra" + (i + 1) + ".bmp");
            byte[] data = Files.readAllBytes(path);

            // Leer x_i desde bytes 8 y 9
            int order = Byte.toUnsignedInt(data[8]) | (Byte.toUnsignedInt(data[9]) << 8);

            int b0 = Byte.toUnsignedInt(data[6]);
            int b1 = Byte.toUnsignedInt(data[7]);
            seed = b0 | (b1 << 8);

            int len = (data.length - HEADER_SIZE_AND_PALETTE) / 8;

            byte[] pixels = Arrays.copyOfRange(data, HEADER_SIZE_AND_PALETTE, data.length);
            byte[] shadow = LSBDecoder.extract(pixels, len);
            xValueMap.put(order, shadow);
        }

        System.out.println(xValueMap.size() );
        if (xValueMap.size() < k) {
            throw new IllegalStateException("Faltan sombras para reconstruir el secreto");
        }


        List<byte[]> orderedShadows = new ArrayList<>(xValueMap.values());

        System.out.println("RECOVERED seed " + seed);
        System.out.println("RECOVERED orden " + Arrays.toString(new ArrayList<>(xValueMap.keySet()).toArray()));

        // Recuperar secreto permutado
        byte[] permuted = SecretSharingScheme.recoverSecret(orderedShadows, k );


        // Deshacer permutación
        int[] permutation = PermutationUtil.generate(permuted.length, seed);
        int[] inverse = PermutationUtil.invert(permutation);


        byte[] original = new byte[permuted.length];

        for (int i = 0; i < inverse.length; i++) {
            original[i] = permuted[inverse[i]];
        }

        // Obtener header base
        byte[] cover = Files.readAllBytes(Path.of(dir, "sombra1.bmp"));
        byte[] header = Arrays.copyOfRange(cover, 0, HEADER_SIZE_AND_PALETTE);

        // Guardar imagen secreta reconstruida
        byte[] result = new byte[header.length + original.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(original, 0, result, header.length, original.length);
        Files.write(Paths.get(outputPath), result);

        System.out.println("Secreto reconstruido guardado en " + outputPath);
    }




}