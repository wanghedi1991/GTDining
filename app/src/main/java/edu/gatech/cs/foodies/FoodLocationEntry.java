package edu.gatech.cs.foodies;

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocationEntry {
    private int id;
    private String name;
    private int imageUrl;
    private String owner;

    public FoodLocationEntry() {
        id = -1;
        name = "";
        imageUrl = R.drawable.panda;
        owner = "";
    }

    public FoodLocationEntry(int id, String name, int imageUrl, String owner) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
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

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
