import Domain.Matrix;

import java.io.IOException;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        int matrixSize = 1000;
        int numberOfThreads = 8;

        Matrix matrix1 = new Matrix(matrixSize, matrixSize);
        Matrix matrix2 = new Matrix(matrixSize, matrixSize);

        Random random = new Random();

        for (int i = 0; i < matrixSize; i ++) {
            for (int j = 0; j < matrixSize; j ++) {
                matrix1.setCellValue(i, j, random.nextInt(10));
                matrix2.setCellValue(i, j, random.nextInt(10));
            }
        }

//        System.out.println(matrix1);
//        System.out.println(matrix2);

        Computation computation = new Computation(matrix1, matrix2, matrixSize, numberOfThreads);

        computation.threadPool(1);

        System.out.println("Start time: " + computation.getStartTime());
        System.out.println("End time: " + computation.getEndTime());
        System.out.println(computation.getEndTime() - computation.getStartTime());
    }

}
