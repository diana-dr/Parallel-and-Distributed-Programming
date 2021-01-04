import domain.Product;
import repo.Bill;
import repo.Deposit;
import service.SaleService;
import utils.RandomStringUtils;
import utils.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final int noOfThreads = 10;
    private static final int verificationThreads = 3;
    private static Deposit deposit = new Deposit();
    private static List<Bill> billsRecord = new ArrayList<>();
    private static RandomStringUtils randomStringUtils = new RandomStringUtils();
    private static List<SaleService> services = new ArrayList<>();
    private static List<Check> checks = new ArrayList<>();

    private static void generateRandomProductsToDeposit() {
        Random random = new Random();
        for (int i = 0; i < 10; i ++) {
            Product product = randomStringUtils.generateProduct(new Random());
            deposit.add(product, Math.abs(random.nextInt(1000)));
        }
    }

    private static Bill generateRandomBill(SaleService service) {
        Random random = new Random();
        List<Product> products = new ArrayList<>(service.getAllProducts());
        Bill bill = new Bill();
        for (int i = 0; i < products.size() / 2; i++) {
            Product product = products.get(random.nextInt(products.size()));
            int quantity = Math.abs(random.nextInt(5));
            bill.add(product, quantity);
        }
        return bill;
    }

    public static void main(String[] args) {

        float start = System.nanoTime() / 1000000;
        generateRandomProductsToDeposit();

        for (int i = 0; i < noOfThreads; i++) {

            SaleService service = new SaleService(deposit);
            System.out.println("Bill: ");
            Bill bill = generateRandomBill(service);
            service.setBill(bill);
            billsRecord.add(bill);

            for (Product product : bill.getStoredProducts()) {
                System.out.println(product.toString());
                System.out.println(bill.getQuantityOfProduct(product));
            }
            services.add(service);
        }

        for (int j = 0; j < verificationThreads; j++) {
            Check check = new Check(billsRecord, services);
            checks.add(check);
        }

        List<Thread> threads = new ArrayList<>();
        List<Thread> checkTreads = new ArrayList<>();

        services.forEach(t -> threads.add(new Thread(t)));
        checks.forEach(check -> checkTreads.add(new Thread(check)));

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        checks.forEach(Check::run);

        float end = System.nanoTime() / 1000000;
        System.out.println("\nEnd work: " + (end - start) / 1000 + " seconds");
    }
}
