import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Polynomial {

    private static final int LIMIT = 100;
    private int degree;
    private List<Integer> coefficients;

    public Polynomial(List<Integer> coefficients) throws PolynomialException {
        this.coefficients = coefficients;
        this.degree = coefficients.size() - 1;
    }

    public Polynomial(int degree) throws PolynomialException {
        Random rand = new Random();
        this.degree = degree;

        if (degree < 0) {
            throw new PolynomialException("Invalid degree specification");
        }

        this.coefficients = new ArrayList<>(degree + 1);

        for (int i = 0; i <= degree; i++) {
            coefficients.add(i, rand.nextInt(LIMIT) + 1);
            if (coefficients.get(i) == 0) {
                coefficients.add(i, rand.nextInt(LIMIT) + 1);
            }
        }
    }

    public int getDegree() {
        return this.degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public List<Integer> getCoefficients() {
        return this.coefficients;
    }

    public void setCoefficients(List<Integer> coefficients) {
        this.coefficients = coefficients;
    }

    public int getLength() {
        return this.coefficients.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polynomial p =(Polynomial) o;

        if (degree != p.degree) return false;

        return Arrays.equals(new List[]{coefficients}, new List[]{p.coefficients});
    }

    @Override
    public int hashCode() {
        int result = degree;
        result = 31 * result + Arrays.hashCode(new List[]{coefficients});
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("Degree ").append(degree).append(" -> ");

        for(int i = 0; i <= this.degree; i++){
            sb.append(this.coefficients.get(i)).append("x ^ ").append(i).append(" + ");
        }

        return sb.toString();
    }

    // Shifts a polynomial to the left by a given offset.
    public static Polynomial Shift (Polynomial polynomial, int offset) throws PolynomialException {
        if (offset < 0)
            throw new PolynomialException("Invalid offset specification!");

        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i < offset; i++) {
            coefficients.add(0);
        }
        for (int i = 0; i < polynomial.getLength(); i++) {
            coefficients.add(polynomial.getCoefficients().get(i));
        }
        return new Polynomial(coefficients);
    }

}
