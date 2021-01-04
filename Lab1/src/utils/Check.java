package utils;

import domain.Product;
import repo.Bill;
import service.SaleService;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Check implements Runnable {

    private static Lock mutex = new ReentrantLock(true);
    private List<Bill> billsRecord;
    private List<SaleService> services;

    public Check(List<Bill> billsRecord, List<SaleService> services) {
        this.billsRecord = billsRecord;
        this.services = services;
    }

    @Override
    public void run() {
        System.err.println("Verifying the stock...");
        mutex.lock();
        double sum = billsRecord.stream().mapToDouble(i -> i.getStoredProducts().stream()
                .mapToDouble(Product::getPrice).sum()).sum();
        if(services.stream().mapToDouble(i -> {
            if (i == null)
                return 0.0f;
            else
                return i.computeTotalPrice();
        }).sum() == sum) {
            System.err.println("Stock verification failed!");
        }
        else {
            System.err.println("Verification Successful!");
        }
        mutex.unlock();
    }
}
