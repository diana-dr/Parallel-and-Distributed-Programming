package Threads;

import java.util.concurrent.locks.Lock;

public class Consumer extends Thread {

    Producer producer;
    Integer product;

    public Consumer(Producer producer)
    {
        this.producer = producer;
        product = 0;
    }

    public void run()
    {
        for (int i = 0; i < producer.getCapacity(); i++) {
            producer.mutex.lock();
            try {
                while (!producer.ready)
                    producer.isReady.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                producer.received = true;
                producer.ready = false;
                producer.isReceived.signal();
                producer.mutex.unlock();
                product += producer.result.get(i);
//                System.out.println("consumed");
            }
        }
        System.out.println("\nScalar product = " + product);
    }
}
