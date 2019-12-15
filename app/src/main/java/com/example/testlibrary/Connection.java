package com.example.testlibrary;

import android.graphics.PointF;

public class Connection {

    private LocationInfo location_1;
    private LocationInfo location_2;

    private String locationKey_1;
    private String locationKey_2;

    public Connection(){
    }
    public String getName(){
        return location_1.getName() + " to " + location_2.getName();
    }

    public float getDistance(){
        PointF point1 = location_1.getPoint();
        PointF point2 = location_2.getPoint();
        float x = point1.x - point2.x;
        float y = point1.y = point2.y;

        float distance = (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        return distance;
    }
    public void setLocation_1(LocationInfo location){
        this.location_1 = location;
    }
    public void setLocation_2(LocationInfo location){
        this.location_2 = location;
    }
    public LocationInfo getLocation_1(){
        return location_1;
    }

    public LocationInfo getLocation_2(){
        return location_2;
    }

    public void setLocationKey_1(String locationKey){
        locationKey_1 = locationKey;
    }

    public void setLocationKey_2(String locationKey){
        locationKey_2 = locationKey;
    }

    public String getLocationKey_1(){
        return locationKey_1;
    }
    public String getLocationKey_2(){
        return locationKey_2;
    }
    public boolean checkLocationConnection(LocationInfo location){
        if(location_1 == location || location_2 == location)
            return true;
        return false;
    }
}
