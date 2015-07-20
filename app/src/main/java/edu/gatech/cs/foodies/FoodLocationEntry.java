package edu.gatech.cs.foodies;

import android.location.Location;

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocationEntry {
    private int id;
    private String name;
    private String imageUrl;
    private Location location;

    public FoodLocationEntry() {
        id = -1;
        name = "";
        imageUrl = "";
    }

    public FoodLocationEntry(int id, String name, String imageUrl, String owner) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
