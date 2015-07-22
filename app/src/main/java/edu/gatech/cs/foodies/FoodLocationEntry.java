package edu.gatech.cs.foodies;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocationEntry {
    private int id;
    private String name;
    private String imageUrl;
    private String tag;
    private Location location;
    private boolean distanceSet;

    public FoodLocationEntry() {
        id = -1;
        name = "";
        imageUrl = "";
        tag = "";
        location = new Location("");
        distanceSet = false;
    }

    public FoodLocationEntry(int id, String name, String imageUrl, String tag) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.tag = tag;
        distanceSet = false;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isDistanceSet() {
        return distanceSet;
    }

    public void setDistanceSet(boolean distanceSet) {
        this.distanceSet = distanceSet;
    }
}
