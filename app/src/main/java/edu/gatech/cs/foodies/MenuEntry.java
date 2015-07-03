package edu.gatech.cs.foodies;

/**
 * Created by Hedi Wang on 2015/6/24.
 */
public class MenuEntry {
    private String name;
    private double price;
    private int calorie;

    public MenuEntry(String name, double price, int calorie) {
        this.name = name;
        this.price = price;
        this.calorie = calorie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }
}
