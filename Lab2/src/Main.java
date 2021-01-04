import Model.Vector;
import Threads.Consumer;
import Threads.Producer;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Main {

    public static void main(String[] args) {

        int size = 100;

        Vector vector1 = new Vector(size);
        Vector vector2 = new Vector(size);

        Random random = new Random();

        for (int i = 0; i < size; i++) {
            vector1.add(random.nextInt(10 - 1) + 1);
            vector2.add(random.nextInt(10 - 1) + 1);
        }

        System.out.println(vector1);
        System.out.println(vector2);

        Producer producer = new Producer(vector1, vector2);
        Consumer consumer = new Consumer(producer);

        Thread thread1 = new Thread(producer);
        Thread thread2 = new Thread(consumer);

        thread2.start();
        thread1.start();

    }
}
