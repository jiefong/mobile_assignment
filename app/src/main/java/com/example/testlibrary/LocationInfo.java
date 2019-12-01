package com.example.testlibrary;

import android.graphics.PointF;

public class LocationInfo {
    String name;
    float x,y;
    String mapName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public PointF getPoint(){
        return new PointF(getX(), getY());
    }

    public void setMapName(String mapName){
        this.mapName = mapName;
    }

    public String getMapName(){
        return mapName;
    }
}
