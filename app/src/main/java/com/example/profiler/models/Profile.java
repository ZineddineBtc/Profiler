package com.example.profiler.models;

public class Profile {
    private int id;
    private String photo, name, bio, phone, birthday, email, address, interests,
                    relationshipStatus, occupation;

    public Profile(){}

    public Profile(String photo, String name, String bio, String phone, String birthday,
                   String email, String address, String interests,
                   String relationshipStatus, String occupation) {
        this.photo = photo;
        this.name = name;
        this.bio = bio;
        this.phone = phone;
        this.birthday = birthday;
        this.email = email;
        this.address = address;
        this.interests = interests;
        this.relationshipStatus = relationshipStatus;
        this.occupation = occupation;
    }

    public Profile(int id, String photo, String name, String bio, String phone, String birthday,
                   String email, String address, String interests,
                   String relationshipStatus, String occupation) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.bio = bio;
        this.phone = phone;
        this.birthday = birthday;
        this.email = email;
        this.address = address;
        this.interests = interests;
        this.relationshipStatus = relationshipStatus;
        this.occupation = occupation;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
