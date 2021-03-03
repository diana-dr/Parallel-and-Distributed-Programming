package com.example.puzzle;

import com.example.puzzle.domain.Puzzle;
import mpi.MPI;

import java.io.Serializable;
import java.util.*;

public class MpiSolver {
    // changing the master value results in inconsistencies in the code
    private final static int MASTER = 0;
    private final static int NO_THREADS = 4;
    private final static int FOUND = -1;
    private final static int INFINITY = Integer.MAX_VALUE;

    private final static int BOARD_SIZE = 4;

    private final int me;
    private final int np;

    public MpiSolver() {
        me = MPI.COMM_WORLD.Rank();
        np = MPI.COMM_WORLD.Size();
    }

    public void start() {
        try {
            if (me == MASTER) {
                this.master();
            } else {
                this.worker();
            }
        } catch (Exception exception) {
            System.out.printf("ERROR in %d\n", me);
            exception.printStackTrace();
        }
    }

    /**
     * Master does :
     *      -> get Children ()
     *          if the parent is a solution -> print and exit
     *          if the parent is out of bounds -> try again with larger bounds
     *          if there is only one child -> child becomes parent
     *     -> handle Children ()
     *          if there are multiple children -> worker finds the best child
     */
    private void master() {
        int[][] board = new int[][] {
                {1,	2,	3,	4},
                {0,	10,	6,	7},
                {5,	9,	11,	8},
                {13,14,	15,	12},
                /*
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 0}
                 */
        };
        Puzzle root = new Puzzle(board);
        root.setParent(null);
        int minBound = root.getHeuristics();

        List<List<ObjectToSend>> queue = new ArrayList<>();
        queue.add( listOf( new ObjectToSend(0, minBound, root) ) );

        while (queue.size() > 0) {
            List<ObjectToSend> currentList = queue.remove(0);
            if (currentList.size() == 1) {
                ObjectToSend current = currentList.get(0);
                if (current.bound == FOUND) {
                    printSolution(current.puzzle);
                    killAll();
                    return;
                } else {
                    queue.add( getChildren(current) );
                }
            } else {
                List<List<ObjectToSend>> forQueue =
                        handleChildren(currentList, currentList.get(0).puzzle.getParent());
                queue.addAll(forQueue);
            }
        }
    }

    private void printSolution(Puzzle puzzle) {
        if (puzzle.getParent() != null) {
            printSolution(puzzle.getParent());
        }
        System.out.println(puzzle);
        System.out.println(".");
    }

    private void killAll() {
        for (int i = 1; i < np; i += 1) {
            // if noSteps is -1, Worker process returns
            SendOne(i, new Object[]{ new ObjectToSend(-1, 0, new Puzzle(1))});
        }
    }

    /**
     * Only Master can handleChildren, since it involves sending the children to the Worker processes
     * @param children
     * @param parent
     * @return
     */
    private List<List<ObjectToSend>> handleChildren(List<ObjectToSend> children, Puzzle parent) {
        for (int i = 0; i < children.size(); i += 1) {
            SendOne(i+1, new Object[] { children.get(i) });
        }

        List<List<ObjectToSend>> forQueue = new ArrayList<>();
        for (int i = 0; i < children.size(); i += 1) {
            List<ObjectToSend> result = (List<ObjectToSend>) ReceiveOne(i+1);
            forQueue.add(result);
        }
        return forQueue;
    }

    /**
     * Receives an ObjectToSend
     * Returns it's children
     */
    private void worker() {
        while (true) {
            ObjectToSend parent = (ObjectToSend) ReceiveOne(MASTER);
            if (parent.noSteps == -1) return;
            SendOne(MASTER, new Object[]{ getChildren(parent) });
        }
    }

    private List<ObjectToSend> getChildren(ObjectToSend current) {
        // check if found solution

        if(current.puzzle.isSolution()) {
            return listOf(new ObjectToSend(0, FOUND, current.puzzle));
        }

        // check if out of bounds
        int estimation = current.noSteps + current.puzzle.getHeuristics();
//        if (estimation > current.bound || estimation > 80) {
        if (estimation > current.bound)
            return listOf(new ObjectToSend(0, estimation, current.puzzle));
//        }

        List<Puzzle> moves = current.puzzle.successors();
        List<ObjectToSend> forQueue = new ArrayList<>();
        for (Puzzle child : moves) {
            if (hasParent(current.puzzle, child)) continue;
            child.setParent(current.puzzle);
            forQueue.add(new ObjectToSend(current.noSteps + 1, current.bound, child));
        }
        return forQueue;
    }

    /**
     * Return true if the puzzle already has child as a parent
     * @param puzzle
     * @param child
     * @return
     */
    private boolean hasParent(Puzzle puzzle, Puzzle child) {
        if (puzzle == child) return true;
        if (puzzle.getParent() != null) {
            return hasParent(puzzle.getParent(), child);
        } else return false;
    }

    private static <T> List<T> listOf(T element) {
        List<T> toReturn = new ArrayList<>();
        toReturn.add(element);
        return toReturn;
    }

    private static void SendOne(int destination, Object[] object) {
        MPI.COMM_WORLD.Send(object, 0, 1, MPI.OBJECT, destination, 0);
    }

    private static Object ReceiveOne(int source) {
        Object[] result = new Object[1];
        MPI.COMM_WORLD.Recv(result, 0, 1, MPI.OBJECT, source, 0);
        return result[0];
    }

    private static class ObjectToSend implements Serializable {
        public int noSteps;
        public int bound;
        public Puzzle puzzle;

        public ObjectToSend(int noSteps, int bound, Puzzle puzzle) {
            this.noSteps = noSteps;
            this.bound = bound;
            this.puzzle = puzzle;
        }
    }
}
