package com.example.profiler.models;

public class Record {
    private String title, description, image, time;
    private int profileID, id;

    public Record(){}

    public Record(int id, String title, String description, String image, String time, int profileID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.time = time;
        this.profileID = profileID;
    }
    public Record(String title, String description, String image, String time, int profileID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.time = time;
        this.profileID = profileID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }
}
