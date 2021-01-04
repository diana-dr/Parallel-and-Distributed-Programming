package service;

import domain.Product;
import repo.Bill;
import repo.Deposit;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SaleService implements Runnable {

    private Deposit deposit;
    private float totalPrice = 0.0f;
    private Lock mutex = new ReentrantLock(true);
    private Bill bill;

    public SaleService(Deposit deposit) {
        this.deposit = deposit;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Float computeTotalPrice() {
        for (Product product : deposit.getStoredProducts())
            product.getMutex().lock();
        for (Product product : deposit.getStoredProducts()) {
            totalPrice += product.getPrice() * this.bill.getQuantityOfProduct(product);
        }
        for (Product product : deposit.getStoredProducts())
            product.getMutex().unlock();
        return totalPrice;
    }

    @Override
    public void run() {
        for (Product product: this.bill.getStoredProducts()) {
//            this.mutex.lock();
            product.getMutex().lock();
            try {
                this.deposit.remove(product, this.bill.getQuantityOfProduct(product));
                System.out.println(this.deposit.toString());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            finally {
                product.getMutex().unlock();
            }
//            this.mutex.unlock();
        }
    }

    public void remove(Product product, int quantity) {
        this.deposit.remove(product, quantity);
    }

    public void add(Product product, int quantity) {
        this.deposit.add(product, quantity);
    }

    public Set<Product> getAllProducts() {
        return this.deposit.getStoredProducts();
    }

    public Integer getQuantityOfroduct(Product product) {
        return this.deposit.getQuantityOfProduct(product);
    }

    public Deposit getDeposit() {
        return this.deposit;
    }
}
