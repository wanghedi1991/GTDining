package edu.gatech.cs.foodies;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hedi Wang on 2015/6/18.
 */
public class FoodLocationEntry implements Parcelable {
    private int id;
    private String name;
    private String imageUrl;
    private Location location;

    public FoodLocationEntry() {
        id = -1;
        name = "";
        imageUrl = "";
        location = new Location("");
    }

    public FoodLocationEntry(int id, String name, String imageUrl, String owner) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    private FoodLocationEntry(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imageUrl = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeParcelable(location, 0);
    }

    public static final Parcelable.Creator<FoodLocationEntry> CREATOR = new Parcelable.Creator<FoodLocationEntry>() {

        @Override
        public FoodLocationEntry createFromParcel(Parcel source) {
            return new FoodLocationEntry(source);
        }

        @Override
        public FoodLocationEntry[] newArray(int size) {
            return new FoodLocationEntry[size];
        }
    };
}
