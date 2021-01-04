package utils;

import domain.Product;

import java.util.Random;

public class RandomStringUtils {

    public RandomStringUtils() {
    }

    private static final String SOURCES = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String generateString(Random random, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = SOURCES.charAt(random.nextInt(SOURCES.length()));
        }
        return new String(text);
    }

    public Product generateProduct(Random random) {
        String type = this.generateString(random, 10);
        float price = random.nextFloat() * 100;

        return new Product(type, price);
    }
}
