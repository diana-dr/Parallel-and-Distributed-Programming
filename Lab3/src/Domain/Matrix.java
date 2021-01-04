package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Matrix {

    private List<List<Integer>> values;
    private int rows;
    private int columns;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        this.values = new ArrayList<>(rows);
        for (int i = 0; i < this.rows; i ++) {
            values.add(new ArrayList<>(columns));

            for (int j = 0; j < this.columns; j ++) {
                values.get(i).add(0);
            }
        }
    }

    public int getColumnsNumber() {
        return this.columns;
    }

    public int getRowsNumber() {
        return this.rows;
    }

    public void setCellValue(int row, int col, int value){
        this.values.get(row).set(col, value);
    }

    public int getCellValue(int row, int col){
        return this.values.get(row).get(col);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.rows; i++){
            stringBuilder.append(this.values.get(i).toString()).append("\n");
        }

        return stringBuilder.toString();
    }
}
