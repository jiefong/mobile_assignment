package com.example.testlibrary;

public class MapObject {

    String name, imgUri;

    MapObject(){}

    public MapObject(String n, String u){
        this.name = n;
        this.imgUri = u;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String bitmap) {
        this.imgUri = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
