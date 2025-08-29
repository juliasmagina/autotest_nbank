package Generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomData {
    private RandomData() {
    }


    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase()
                + RandomStringUtils.randomAlphabetic(5).toLowerCase()
                + RandomStringUtils.randomNumeric(1)
                + "$";
    }

    public static float getBalance() {
        int min = 1;
        int max = 5000;
        int number = ThreadLocalRandom.current().nextInt(min, max);
        return (float) number;

    }

    public static String getNewUsername() {
        return RandomStringUtils.randomAlphabetic(5)
                + " " + RandomStringUtils.randomAlphabetic(5);
    }
}
