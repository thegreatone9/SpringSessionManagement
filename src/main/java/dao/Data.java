package dao;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author musa.khan
 * @since 08/01/2021
 */
@Component
public class Data {

    public List<String> getMeals() {
        return Arrays.asList("apples", "mangoes", "oranges", "bananas");
    }

    public List<String> getCustomers() {
        return Arrays.asList("John", "Doe", "Samantha", "Derreck");
    }

    public List<String> getOptions() {
        return Arrays.asList("meals", "customers");
    }
}