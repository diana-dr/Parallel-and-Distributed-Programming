import Domain.Matrix;

public class MatrixMultiplication {
    private int size;
    private final Matrix matrix1;
    private final Matrix matrix2;
    private Matrix result;
    private int step;
    private int numberOfThreads;

    public MatrixMultiplication(Matrix matrix1, Matrix matrix2, int size, int numberOfThreads) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.size = size;
        this.step = 0;
        this.result = new Matrix(size, size);
        this.numberOfThreads = numberOfThreads;
    }

    private void multiplySubTask(int matrix1RowIndex, int matrix2ColIndex) {
        for (int i = 0; i < size; i++) {
            int oldValue = result.getCellValue(matrix1RowIndex, matrix2ColIndex);
            int firstMatrixVal = matrix1.getCellValue(matrix1RowIndex, i);
            int secondMatrixVal = matrix2.getCellValue(i, matrix2ColIndex);
            result.setCellValue(matrix1RowIndex, matrix2ColIndex, oldValue + firstMatrixVal * secondMatrixVal);
        }
    }

    public Runnable computation1 = () -> {
        int core = step++;
        for (int matrix1RowIndex = core * size / numberOfThreads; matrix1RowIndex < (core + 1) * size / numberOfThreads; matrix1RowIndex++) {
            for (int matrix2ColIndex = 0; matrix2ColIndex < size; matrix2ColIndex++) {
                multiplySubTask(matrix1RowIndex, matrix2ColIndex);
            }
        }
    };

    public Runnable computation2 = () -> {
        int core = step++;
        for (int matrix1RowIndex = 0; matrix1RowIndex < size; matrix1RowIndex++) {
            for (int matrix2ColIndex = core * size / numberOfThreads; matrix2ColIndex < (core + 1) * size / numberOfThreads; matrix2ColIndex++) {
                multiplySubTask(matrix1RowIndex, matrix2ColIndex);
            }
        }
    };

    public Runnable computation3 = () -> {
        int core = step++;
        for (int index = core; index < size * size; index += numberOfThreads) {
            int matrix1RowIndex = index / size;
            int matrix2ColIndex = index % size;
            multiplySubTask(matrix1RowIndex, matrix2ColIndex);
        }
    };

    public Matrix getResult() {
        return result;
    }
}
