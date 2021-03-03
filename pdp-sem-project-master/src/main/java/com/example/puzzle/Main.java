package com.example.puzzle;

import com.example.puzzle.domain.Puzzle;
import com.example.puzzle.domain.PuzzleException;

import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import mpi.MPI;

public class Main {
    public static void main(String[] args) {
//         play(4);
         threadSolve();
//        mpiSolve(args);
    }

    private static void play(int size) {
        Puzzle puzzle = new Puzzle(size, true);
        try {
            puzzle.shift(Puzzle.Shift.UP);
        } catch (PuzzleException e) { e.printStackTrace(); }
        System.out.println("Try to get to the result using commands \"up\" | \"down\" | \"left\" |\"right\" \n" +
                "The first letter of either word will suffice (u, d, l, r) \n" +
                "Type \"exit\" to quit \n" +
                "Here's what that should look like:\n" +
                new Puzzle(size, true).toString());
        Scanner input = new Scanner(System.in);

        while (! puzzle.isSolution()) {
            System.out.printf("Your puzzle\n%s\n>", puzzle.toString());
            String line = input.nextLine();
            char command = line.charAt(0);
            try {
                switch (command) {
                    case 'u' : { puzzle.shift(Puzzle.Shift.UP); break; }
                    case 'd' : { puzzle.shift(Puzzle.Shift.DOWN); break; }
                    case 'l' : { puzzle.shift(Puzzle.Shift.LEFT); break; }
                    case 'r' : { puzzle.shift(Puzzle.Shift.RIGHT); break; }
                    case 'e' : { return; }
                    default : { System.out.printf("Wrong input: \"%s\" \n", line); break; }
                }
            } catch (PuzzleException e) { System.out.printf("Can't shift %s \n", line); }
        }
        System.out.printf("You did it! Good Job! \n%s", puzzle.toString());
    }

    private static void mpiSolve(String[] args) {
        MPI.Init(args);
        new MpiSolver().start();
    }

    private static void threadSolve() {
        //
        // int[][] board = new int[][] {{7, 1, 10, 4}, {3, 11, 14, 8}, {0, 9, 5, 15}, {2, 13, 6, 12}};
        // int[][] board = new int[][] {{15, 14, 8, 12}, {10, 11, 9, 13}, {2, 6, 5, 1}, {3, 7, 4, 0}};
        //
        int[][] board = new int[][] {{1, 2, 3, 10}, {11, 9, 7, 4}, {5, 6, 0, 8}, {13, 14, 15, 12}};
        Puzzle puzzle = new Puzzle(board);
        Solver solver = new Solver(4);
        try {
            Stack<Puzzle> solution = solver.findSolution(puzzle);
            System.out.println("solution");
            for (Puzzle p : solution) {
                System.out.println(p.toString());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
