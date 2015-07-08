package edu.gatech.cs.foodies;

/**
 * Created by Hedi Wang on 2015/7/7.
 */
public class RestaurantEntry {
    private int id;
    private String name;
    private String owner;

    public RestaurantEntry() {
        id = -1;
        name = "";
        owner = "";
    }

    public RestaurantEntry(int id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
