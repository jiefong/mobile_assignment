package com.example.testlibrary;

import android.graphics.PointF;

public class Connection {

    private LocationInfo location_1;
    private LocationInfo location_2;

    public Connection(LocationInfo location_1, LocationInfo location_2){
        this.location_1 = location_1;
        this.location_2 = location_2;
    }

    public float getDistance(){
        PointF point1 = location_1.getPoint();
        PointF point2 = location_2.getPoint();
        float x = point1.x - point2.x;
        float y = point1.y = point2.y;

        float distance = (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        return distance;
    }

    public LocationInfo getLocation1(){
        return location_1;
    }

    public LocationInfo getLocation2(){
        return location_2;
    }

    public boolean checkLocationConnection(LocationInfo location){
        if(location_1 == location || location_2 == location)
            return true;
        return false;
    }
}
