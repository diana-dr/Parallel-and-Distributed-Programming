import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PolynomialOperations {

    public static Polynomial Add(Polynomial p1, Polynomial p2) {
        int min = Math.min(p1.getDegree(), p2.getDegree());
        int max = Math.max(p1.getDegree(), p2.getDegree());

        List<Integer> coefficients = new ArrayList<>(max + 1);

        // add corresponding coefficients
        for (int i = 0; i <= min; i++)
            coefficients.add(p1.getCoefficients().get(i) + p2.getCoefficients().get(i));

        // the remaining coefficients from the higher degree polynomial are added to the result polynomial
        for (int i = min + 1; i <= max; i++)
            if (i <= p1.getDegree())
                coefficients.add(i, p1.getCoefficients().get(i));
            else
                coefficients.add(i, p2.getCoefficients().get(i));

        Polynomial result = null;

        try {
            result = new Polynomial(coefficients);
        } catch (PolynomialException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static Polynomial Subtract(Polynomial p1, Polynomial p2)
    {
        int min = Math.min(p1.getDegree(), p2.getDegree());
        int max = Math.max(p1.getDegree(), p2.getDegree());

        List<Integer> coefficients = new ArrayList<>(max + 1);

        for (int i = 0; i <= min; i++)
            coefficients.add(p1.getCoefficients().get(i) - p2.getCoefficients().get(i));

        for (int i = min + 1; i <= max; i++)
            if (i <= p1.getDegree())
                coefficients.add(i, p1.getCoefficients().get(i));
            else
                coefficients.add(i, -1 * p2.getCoefficients().get(i));

        // after subtraction the polynomial, the degree has to be updated
        int degree = coefficients.size() - 1;
        while (coefficients.get(degree) == 0 && degree > 0)
            degree --;

        // the remaining coefficients are saved into a new array
        List<Integer> newCoeff = new ArrayList<>(degree + 1);

        for (int i = 0; i < degree + 1; i++) {
            newCoeff.add(i, coefficients.get(i));
        }

        Polynomial result = null;
        try {
            result = new Polynomial(newCoeff);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }


    public static Polynomial sequentialMultiplication(Polynomial p1, Polynomial p2) throws PolynomialException {
        int resultSize = p1.getDegree() + p2.getDegree() + 1;
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i < resultSize; i++) {
            coefficients.add(0);
        }
        for (int i = 0; i < p1.getCoefficients().size(); i++) {
            for (int j = 0; j < p2.getCoefficients().size(); j++) {
                int index = i + j;
                int value = p1.getCoefficients().get(i) * p2.getCoefficients().get(j);
                coefficients.set(index, coefficients.get(index) + value);
            }
        }
        return new Polynomial(coefficients);
    }

    public static Polynomial parallelMultiplication(Polynomial p1, Polynomial p2, int nrOfThreads) throws InterruptedException, PolynomialException {
        //we  calculate the result size
        int resultSize = p1.getDegree() + p2.getDegree() + 1;

        //we initialize the coefficients array with 0
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i < resultSize; i++) {
            coefficients.add(0);
        }
        Polynomial result = new Polynomial(coefficients);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nrOfThreads);

        //we divide the work for each thread
        int bound = result.getLength() / nrOfThreads;
        if (bound == 0) {
            bound = 1;
        }

        for (int i = 0; i < result.getLength(); i += bound) {
            //we establish the working interval for each thread
            int end = i + bound;
            OperationsTask task = new OperationsTask(i, end, p1, p2, result);
            executor.execute(task);
        }

        executor.shutdown();
        executor.awaitTermination(50, TimeUnit.SECONDS);

        return result;
    }

    public static List<Polynomial> dividePolynoms(Polynomial polynomial1, Polynomial polynomial2) throws PolynomialException {
        int len = Math.max(polynomial1.getDegree(), polynomial2.getDegree()) / 2;
        List<Polynomial> result = new ArrayList<>();

        result.add(new Polynomial(polynomial1.getCoefficients().subList(0, len)));
        result.add(new Polynomial(polynomial1.getCoefficients().subList(len, polynomial1.getLength())));
        result.add(new Polynomial(polynomial2.getCoefficients().subList(0, len)));
        result.add(new Polynomial(polynomial2.getCoefficients().subList(len, polynomial2.getLength())));

        return result;
    }

    public static Polynomial sequentialKaratsubaMultiplication(Polynomial polynomial1, Polynomial polynomial2) throws PolynomialException {

        if (polynomial1.getDegree() < 2 || polynomial2.getDegree() < 2) {
            return sequentialMultiplication(polynomial1, polynomial2);
        }

        int len = Math.max(polynomial1.getDegree(), polynomial2.getDegree()) / 2;

        //we divide each polynomial in 2 parts: low and high
        Polynomial lowP1 = dividePolynoms(polynomial1, polynomial2).get(0);
        Polynomial highP1 = dividePolynoms(polynomial1, polynomial2).get(1);
        Polynomial lowP2 = dividePolynoms(polynomial1, polynomial2).get(2);
        Polynomial highP2 = dividePolynoms(polynomial1, polynomial2).get(3);

        Polynomial z1 = sequentialKaratsubaMultiplication(lowP1, lowP2);
        Polynomial z2 = sequentialKaratsubaMultiplication(Add(lowP1, highP1), Add(lowP2, highP2));
        Polynomial z3 = sequentialKaratsubaMultiplication(highP1, highP2);

        Polynomial r1 = Polynomial.Shift(z3, 2 * len);
        Polynomial r2 = Polynomial.Shift(Subtract(Subtract(z2, z3), z1), len);

        return Add(Add(r1, r2), z1);
    }

    public static Polynomial parallerKaratsubaMultiplication(Polynomial polynomial1, Polynomial polynomial2, int currentDepth) throws ExecutionException, InterruptedException, PolynomialException {

        if (currentDepth > 4) {
            return sequentialKaratsubaMultiplication(polynomial1, polynomial2);
        }

        if (polynomial1.getDegree() < 2 || polynomial2.getDegree() < 2) {
            return sequentialKaratsubaMultiplication(polynomial1, polynomial2);
        }

        int len = Math.max(polynomial1.getDegree(), polynomial2.getDegree()) / 2;

        //we divide each polynomial in 2 parts: low and high
        Polynomial lowP1 = dividePolynoms(polynomial1, polynomial2).get(0);
        Polynomial highP1 = dividePolynoms(polynomial1, polynomial2).get(1);
        Polynomial lowP2 = dividePolynoms(polynomial1, polynomial2).get(2);
        Polynomial highP2 = dividePolynoms(polynomial1, polynomial2).get(3);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Callable<Polynomial> task1 = () -> parallerKaratsubaMultiplication(lowP1, lowP2, currentDepth + 1);
        Callable<Polynomial> task2 = () -> parallerKaratsubaMultiplication(Add(lowP1, highP1), Add(lowP2, highP2), currentDepth + 1);
        Callable<Polynomial> task3 = () -> parallerKaratsubaMultiplication(highP1, highP2, currentDepth);

        Future<Polynomial> f1 = executor.submit(task1);
        Future<Polynomial> f2 = executor.submit(task2);
        Future<Polynomial> f3 = executor.submit(task3);

        executor.shutdown();

        Polynomial z1 = f1.get();
        Polynomial z2 = f2.get();
        Polynomial z3 = f3.get();

        executor.awaitTermination(60, TimeUnit.SECONDS);

        Polynomial r1 = Polynomial.Shift(z3, 2 * len);
        Polynomial r2 = Polynomial.Shift(Subtract(Subtract(z2, z3), z1), len);
        return Add(Add(r1, r2), z1);
    }



}
