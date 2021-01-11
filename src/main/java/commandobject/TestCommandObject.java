package commandobject;

import java.util.List;

/**
 * @author musa.khan
 * @since 08/01/2021
 */
public class TestCommandObject extends AbstractCommandObject{

    public List<String> meals;
    public List<String> options;
    public List<String> customers;
    public String option;
    public String selection;

    public List<String> getMeals() {
        return meals;
    }

    public void setMeals(List<String> meals) {
        this.meals = meals;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<String> getCustomers() {
        return customers;
    }

    public void setCustomers(List<String> customers) {
        this.customers = customers;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    @Override
    public String toString() {
        return "TestCommandObject{" +
                "option='" + option + '\'' +
                ", selection='" + selection + '\'' + '}';
    }
}