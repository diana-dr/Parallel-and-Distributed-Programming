package Threads;

import Model.Vector;

import java.lang.Thread;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Producer extends Thread {

    Integer capacity;
    final Vector result;
    Vector vector1;
    Vector vector2;
    boolean received = true;
    boolean ready = false;
    Lock mutex = new ReentrantLock();
    Condition isReceived = mutex.newCondition();
    Condition isReady = mutex.newCondition();

    public Integer getCapacity() {
        return capacity;
    }

    public Producer(Vector vector1, Vector vector2) {
        this.capacity = vector1.getSize();
        result = new Vector(capacity);
        this.vector1 = vector1;
        this.vector2 = vector2;

    }

    public void run() {
        for (int i = 0; i < capacity; i++) {
            try {
                mutex.lock();
                result.add(vector1.get(i) * vector2.get(i));
//                System.out.println("produced");
                while (!received)
                    isReceived.await();
                received = false;
                ready = true;
                isReady.signal();
                mutex.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
