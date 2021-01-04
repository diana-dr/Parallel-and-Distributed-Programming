import Domain.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Computation {

        private Matrix matrix1;
        private Matrix matrix2;
        private int size;
        private int numberOfThreads;
        private long startTime;
        private long endTime;

        public Computation(Matrix matrix1, Matrix matrix2, int size, int numberOfThreads) {
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
            this.size = size;
            this.numberOfThreads = numberOfThreads;
            this.startTime = 0;
            this.endTime = 0;
        }

        public void lowLevel(int computation) throws InterruptedException {
            MatrixMultiplication matrixMultiplication = new MatrixMultiplication(matrix1, matrix2, size, numberOfThreads);
            startTime = System.currentTimeMillis();

            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < numberOfThreads; i++) {
                if (computation == 1)
                    threads.add(new Thread(matrixMultiplication.computation1));
                else if (computation == 2)
                    threads.add(new Thread(matrixMultiplication.computation2));
                else
                    threads.add(new Thread(matrixMultiplication.computation3));
            }

            for (Thread thread: threads) {
                thread.start();
            }
            for (Thread thread: threads) {
                thread.join();
            }

            endTime = System.currentTimeMillis();
//            System.out.println(matrixMultiplication.getResult());

        }

        public void threadPool(int computation) throws InterruptedException {
            MatrixMultiplication matrixMultiplication = new MatrixMultiplication(matrix1, matrix2, size, numberOfThreads);
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            startTime = System.currentTimeMillis();

            if (computation == 1)
                for (int i = 0; i < numberOfThreads; i++) {

                    Runnable task = matrixMultiplication.computation1;

                    executorService.submit(task);
                }

            else if (computation == 2)
                for (int i = 0; i < numberOfThreads; i++) {

                    Runnable task = matrixMultiplication.computation2;

                    executorService.submit(task);
                }
            else
                for (int i = 0; i < numberOfThreads; i++) {

                    Runnable task = matrixMultiplication.computation3;

                    executorService.submit(task);
                }

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
            endTime = System.currentTimeMillis();
//            System.out.println(matrixMultiplication.getResult());
        }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
