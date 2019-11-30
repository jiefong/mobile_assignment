package com.example.testlibrary;

import android.graphics.PointF;

public class Location {

    private PointF point;
    private String name;
    private String map;

    public Location(PointF point, String name, String map){
        this.point = point;
        this.name = name;
        this.map = map;
    }

    public PointF getPoint(){
        return point;
    }

    public String getName(){
        return name;
    }

}