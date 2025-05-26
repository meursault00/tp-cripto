package ar.edu.itba.ssshare.scheme;

/** Evalúa polinomios mod 251. Sin optimizaciones aún. */
public final class Polynomial {
    public static final int P = 257;

    private final int[] coeff; // a0..ak  (k = coeff.length-1)

    public Polynomial(int[] coeff) {
        this.coeff = coeff;
    }

    public int eval(int x) {
        long sum = 0;
        long power = 1;
        for (int a : coeff) {
            sum += a * power;
            power = (power * x) % P;
        }
        return (int)(sum % P);
    }
}
