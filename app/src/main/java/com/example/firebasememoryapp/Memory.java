package com.example.firebasememoryapp;

public class Memory {
    int rating;
    String name;
    String desc;
    int imageResourceId;

    public Memory(int rating, String name, String desc, int imageResourceId) {
        this.rating = rating;
        this.name = name;
        this.desc = desc;
        this.imageResourceId = imageResourceId;
    }

    public Memory(int rating, String name, String desc){
        this.rating = rating;
        this.name = name;
        this.desc = desc;
        imageResourceId = 0;
    }

    public Memory() {
        rating = 0;
        name = "";
        desc = "";
        imageResourceId = 0;

    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
