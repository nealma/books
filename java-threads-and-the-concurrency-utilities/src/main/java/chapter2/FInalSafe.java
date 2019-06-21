package chapter2;

import java.util.Set;
import java.util.TreeSet;

public class FInalSafe {

}

final class Fruit {

    private final Set<String> fruits =  new TreeSet<>();

    public Fruit() {
        fruits.add("Apple");
        fruits.add("Pear");
        fruits.add("Strawberry");
    }

}

/**
 * this 脱离
 */
class ThisEscape {
    private static ThisEscape lastCreatedInstance;

    public ThisEscape() {
        lastCreatedInstance = this;
    }
}