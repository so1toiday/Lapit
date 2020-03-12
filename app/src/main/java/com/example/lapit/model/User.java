package com.example.lapit.model;

public class User {
    String uid;
    String name;
    String image;
    String thum_image;
    String status;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User(String uid, String name, String image, String thum_image, String status) {
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.thum_image = thum_image;
        this.status = status;
    }


    public User(String uid) {
        this.uid = uid;
    }

    public User(String name, String image, String thum_image, String status) {
        this.name = name;
        this.image = image;
        this.thum_image = thum_image;
        this.status = status;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThum_image() {
        return thum_image;
    }

    public void setThum_image(String thum_image) {
        this.thum_image = thum_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
