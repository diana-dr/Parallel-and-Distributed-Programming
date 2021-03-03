package com.example.puzzle;

import com.example.puzzle.domain.Puzzle;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Solver {
  private final int FOUND = -1;
  private final ExecutorService executorService;
  private final int INFINITY = Integer.MAX_VALUE;
  private final Stack<Puzzle> solution;
  private final int NUMBER_OF_THREADS;

  public Solver(int numberOfThreads) {
    this.NUMBER_OF_THREADS = numberOfThreads;
    this.executorService = Executors.newFixedThreadPool(numberOfThreads);
    solution = new Stack<>();
  }

  public Stack<Puzzle> findSolution(Puzzle root) throws ExecutionException, InterruptedException {
    int minBound = root.getHeuristics();
    solution.add(root);
    AbstractMap.SimpleEntry<Integer, Stack<Puzzle>> answer = null;

    while (true) {
      answer = searchParallel(solution, 0, minBound, NUMBER_OF_THREADS);
//      System.out.println(minBound);
      if (answer.getKey() == -1) {
        executorService.shutdown();
        executorService.awaitTermination(1000000, TimeUnit.SECONDS);
        return answer.getValue();
      }
      minBound = answer.getKey();
    }
  }

  public AbstractMap.SimpleEntry<Integer, Stack<Puzzle>> searchParallel(Stack<Puzzle> stack, int numSteps, int bound,
                                                                        int nrThreads)
      throws ExecutionException, InterruptedException {
    if (nrThreads <= 1) {
      return search(stack, numSteps, bound);
    }
    Puzzle current = stack.peek();
    int estimation = numSteps + current.getHeuristics();
    if (estimation > bound) {
      return new AbstractMap.SimpleEntry<>(estimation, stack);
    }
//    if (estimation > 80) {
//      return new AbstractMap.SimpleEntry<>(estimation, stack);
//    }
    if (current.getHeuristics() == 0) {
      return new AbstractMap.SimpleEntry<>(FOUND, stack);
    }
    int min = INFINITY;
    List<Puzzle> moves = current.successors();
    List<Future<AbstractMap.SimpleEntry<Integer, Stack<Puzzle>>>> futures = new ArrayList<>();
    for (Puzzle next : moves) {
      Stack<Puzzle> copy = (Stack<Puzzle>) stack.clone();
      copy.push(next);
      Future<AbstractMap.SimpleEntry<Integer, Stack<Puzzle>>> f =
          executorService.submit(
              () -> searchParallel(copy, numSteps + 1, bound, nrThreads / moves.size()));
      futures.add(f);
    }
    for (Future<AbstractMap.SimpleEntry<Integer, Stack<Puzzle>>> f : futures) {
      AbstractMap.SimpleEntry<Integer, Stack<Puzzle>> result = f.get();
      int t = result.getKey();
      if (t == -1) {
        return new AbstractMap.SimpleEntry<>(FOUND, result.getValue());
      }
      if (t < min) {
        min = t;
      }
    }
    return new AbstractMap.SimpleEntry<>(min, stack);
  }

  public AbstractMap.SimpleEntry<Integer, Stack<Puzzle>> search(Stack<Puzzle> stack, int numSteps, int bound) {
    Puzzle current = stack.peek();
    int estimation = numSteps + current.getHeuristics();
    if (estimation > bound) {
      return new AbstractMap.SimpleEntry<>(estimation, stack);
    }
//    if (estimation > 80) {
//      return new AbstractMap.SimpleEntry<>(estimation, stack);
//    }
    if (current.getHeuristics() == 0) {
      return new AbstractMap.SimpleEntry<>(FOUND, stack);
    }
    int min = INFINITY;
    for (Puzzle next : current.successors()) {
      if (!solution.contains(next)) {
        stack.push(next);
        AbstractMap.SimpleEntry<Integer, Stack<Puzzle>> result = search(stack, numSteps + 1, bound);
        int t = result.getKey();
        if (t == -1) {
          return new AbstractMap.SimpleEntry<>(FOUND, stack);
        }
        if (t < min) {
          min = t;
        }
        stack.pop();
      }
    }
    return new AbstractMap.SimpleEntry<>(min, stack);
  }
}
