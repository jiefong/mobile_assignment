package com.example.testlibrary;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectListener {

    PinView map;
    List<LocationInfo> locationList;
    PointF current;
    ArrayList<LocationInfo> selected = new ArrayList<>();

    public MultiSelectListener(PinView map, List<LocationInfo> locationList, PointF current){
        this.map = map;
        this.locationList = locationList;
        this.current = current;
    }

    public void draw(ArrayList<Item> selectedItems){
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
