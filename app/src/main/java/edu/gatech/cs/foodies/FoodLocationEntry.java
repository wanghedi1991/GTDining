package edu.gatech.cs.foodies;

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocationEntry {
    private String title;
    private String promo;
    private int imageUrl;

    public FoodLocationEntry(String title, String promo, int imageUrl) {
        this.title = title;
        this.promo = promo;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
