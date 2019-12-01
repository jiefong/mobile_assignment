package com.example.testlibrary;

import android.graphics.PointF;

public class MultiSelectListener {

    PinView map;

    public MultiSelectListener(PinView map){
        this.map = map;
    }

    public void draw(){
        PointF start = new PointF(500, 1500);
        PointF destination = new PointF(2000, 500);
        map.setPin(start, destination);
//        map.setRoutes(routes);
    }
}
