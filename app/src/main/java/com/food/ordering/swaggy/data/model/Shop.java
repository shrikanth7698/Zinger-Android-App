package com.food.ordering.swaggy.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Shop implements Parcelable {

    String name;
    String desc;
    String rating;
    String imageUrl;

    public Shop(String name, String desc, String rating, String imageUrl) {
        this.name = name;
        this.desc = desc;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeString(this.rating);
        dest.writeString(this.imageUrl);
    }

    protected Shop(Parcel in) {
        this.name = in.readString();
        this.desc = in.readString();
        this.rating = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel source) {
            return new Shop(source);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };
}
