package ar.edu.itba.ssshare.scheme;

/** Evalúa polinomios mod 251. Sin optimizaciones aún. */
public final class Polynomial {
    public static final int P = 257;

    private final int[] coeff; // a0..ak  (k = coeff.length-1)

    public Polynomial(int[] coeff) {
        this.coeff = coeff;
    }

    public int eval(int x) {
        long result = 0;
        long powX = 1;

        for (int a : coeff) {
            result = (result + (a * powX) % P) % P;
            powX = (powX * x) % P;
        }

        return (int) ((result + P) % P); // Asegura valor positivo
    }

}
