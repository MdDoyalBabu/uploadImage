package com.example.imageupload.handler;

import com.google.firebase.database.Exclude;

public class Upload {

    public Upload(){

    }

    private  String imageName;
    private  String imageIUri;
    private String key;

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public Upload(String imageName, String imageIUri) {
        this.imageName = imageName;
        this.imageIUri = imageIUri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageIUri() {
        return imageIUri;
    }

    public void setImageIUri(String imageIUri) {
        this.imageIUri = imageIUri;
    }
}
