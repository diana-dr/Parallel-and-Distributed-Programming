package domain;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Product {

    private String name;
    private float price;
    private Lock mutex = new ReentrantLock(true);

    public Product(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public Lock getMutex() {
        return mutex;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
