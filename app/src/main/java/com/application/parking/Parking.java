package com.application.parking;

import java.util.ArrayList;

public class Parking {
    String Owner, Image, Description, Location, Title,Key;
    double Lat, Long;
    int numOfParking, Price;
    ArrayList<Comment> Comments = new ArrayList<>();

    public Parking(String owner, String image, String description, String location, String title, double lat, double aLong, int numOfParking, int price, String key, ArrayList<Comment> comments) {
        Owner = owner;
        Image = image;
        Description = description;
        Location = location;
        Title = title;
        Lat = lat;
        Long = aLong;
        this.numOfParking = numOfParking;
        Price = price;
        Key = key;
        Comments = comments;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public void setNumOfParking(int numOfParking) {
        this.numOfParking = numOfParking;
    }

    public void setKey(String key) {
        Key = key;
    }

    public void setComments(ArrayList<Comment> comments) {
        Comments = comments;
    }

    public String getKey() {
        return Key;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getOwner() {
        return Owner;
    }

    public String getImage() {
        return Image;
    }

    public String getDescription() {
        return Description;
    }

    public String getLocation() {
        return Location;
    }

    public String getTitle() {
        return Title;
    }

    public double getLat() {
        return Lat;
    }

    public double getLong() {
        return Long;
    }

    public int getNumOfParking() {
        return numOfParking;
    }

    public int getPrice() {
        return Price;
    }

    public ArrayList<Comment> getComments() {
        return Comments;
    }
}
