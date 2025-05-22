package ar.edu.itba.ssshare;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
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
            // distribuir(secret, k, n, dir);
        } else {
            // recuperar(secret, k, dir);
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
}