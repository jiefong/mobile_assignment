package com.example.testlibrary;

public class MapObject {

    String name, bitmap;

    public MapObject(String n, String u){
        this.name = n;
        this.bitmap = u;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
