package com.example.testlibrary;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectListener {

    AddLocationStep2 activity;
    PinView map;
    List<LocationInfo> locationList;
    PointF current;
    ArrayList<LocationInfo> selected = new ArrayList<>();

    public MultiSelectListener(PinView map, List<LocationInfo> locationList, PointF current){
        this.map = map;
        this.locationList = locationList;
        this.current = current;
        this.activity = activity;
    }

    public void draw(ArrayList<Item> selectedItems){
        selected.clear();
        for(Item item : selectedItems){
            for (LocationInfo location: locationList){
                if(item.getName() == location.getName()){
                    selected.add(location);
                }
            }
        }
        map.setConnections(current, selected);
    }

    public ArrayList<LocationInfo> getSelected(){
        return selected;
    }
}
