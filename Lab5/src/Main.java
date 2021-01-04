import java.util.concurrent.ExecutionException;

public class Main {

    private static int DEGREE = 100;

    private static void sequential(Polynomial polynomial1, Polynomial polynomial2) throws PolynomialException {
        //SEQUENTIAL MULTIPLICATION
        System.out.println("Sequential multiplication: ");
        System.out.println("First polynomial: " + polynomial1.toString());
        System.out.println("Second polynomial: " + polynomial2.toString());

        long startTime = System.currentTimeMillis();
        Polynomial resultSequential = PolynomialOperations.sequentialMultiplication(polynomial1, polynomial2);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Result polynomial: " + resultSequential.toString());
        System.out.println("Total execution time: " + totalTime + " ms.\n");
    }

    private static void parallel(Polynomial polynomial1, Polynomial polynomial2, int threadsNumber) throws PolynomialException, InterruptedException {
        //PARALLEL MULTIPLICATION
        System.out.println("Parallel multiplication: ");
        System.out.println("First polynomial: " + polynomial1.toString());
        System.out.println("Second polynomial: " + polynomial2.toString());

        long startTime = System.currentTimeMillis();
        Polynomial resultSequential = PolynomialOperations.parallelMultiplication(polynomial1, polynomial2, threadsNumber);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Result polynomial: " + resultSequential.toString());
        System.out.println("Total execution time: " + totalTime + " ms.\n");
    }

    private static void sequentialKaratsuba(Polynomial polynomial1, Polynomial polynomial2) throws PolynomialException {
        //SEQUENTIAL KARATSUBA MULTIPLICATION
        System.out.println("Sequential Karatsuba multiplication: ");
        System.out.println("First polynomial: " + polynomial1.toString());
        System.out.println("Second polynomial: " + polynomial2.toString());

        long startTime = System.currentTimeMillis();
        Polynomial resultSequential = PolynomialOperations.sequentialKaratsubaMultiplication(polynomial1, polynomial2);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Result polynomial: " + resultSequential.toString());
        System.out.println("Total execution time: " + totalTime + " ms.\n");
    }

    private static void parallelKaratsuba(Polynomial polynomial1, Polynomial polynomial2, int threadsNumber) throws PolynomialException, InterruptedException, ExecutionException, ExecutionException {
        //PARALLEL KARATSUBA MULTIPLICATION
        System.out.println("Parallel Karatsuba multiplication: ");
        System.out.println("First polynomial: " + polynomial1.toString());
        System.out.println("Second polynomial: " + polynomial2.toString());

        long startTime = System.currentTimeMillis();
        Polynomial resultSequential = PolynomialOperations.parallerKaratsubaMultiplication(polynomial1, polynomial2, threadsNumber);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Result polynomial: " + resultSequential.toString());
        System.out.println("Total execution time: " + totalTime + " ms.\n");
    }

    public static void main(String[] args) throws PolynomialException, InterruptedException, ExecutionException {

        Polynomial polynomial1 = new Polynomial(DEGREE);
        Polynomial polynomial2 = new Polynomial(DEGREE);

        //SEQUENTIAL MULTIPLICATION
        sequential(polynomial1, polynomial2);

        //PARALLEL MULTIPLICATION
        parallel(polynomial1, polynomial2, 5);

        //SEQUENTIAL KARATSUBA
        sequentialKaratsuba(polynomial1, polynomial2);

        //PARALLEL KARATSUBA
        parallelKaratsuba(polynomial1, polynomial2, 4);
    }
}

