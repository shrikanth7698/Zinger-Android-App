package com.food.ordering.swaggy.data.model;

public class Shop {

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
}
