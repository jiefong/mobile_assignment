package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SelectDestination extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<LocationInfo> locationList;
    ArrayList<String> locationStringList;

    Spinner destination, curLocation;

    String currentLocationKey;
    String[] arrayCurLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                onBackPressed();
            } else {
                currentLocationKey = extras.getString("currentLocationKey");
            }
        }
        curLocation = (Spinner) findViewById(R.id.spinnerCurrentLocation);
        destination = (Spinner) findViewById(R.id.spinnerDestination);

        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        locationStringList = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                locationList.clear();
                locationStringList.clear();

                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                    String key = userSnapShot.getKey();
//                    locationList.add(u);
                    if (u.getDestination()) {
                        locationStringList.add(u.getName());
                    }
                    System.out.println("comparing "+key+ " to "+currentLocationKey);
                    if(key.equals(currentLocationKey)){
                        locationStringList.remove(u.getName());
                        arrayCurLocation = new String[]{
                                u.getName()
                        };
                    }
                    //Create item based on the location list
                }
                setDestinationSelection(locationStringList);
                if(arrayCurLocation == null){
                    onBackPressed();
                }else{
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(),
                            android.R.layout.simple_spinner_item, arrayCurLocation);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    curLocation.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void showMap(View v) {
        String start = curLocation.getSelectedItem().toString();
        String end = destination.getSelectedItem().toString();
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("Start", start);
        intent.putExtra("End", end);
        startActivity(intent);
        finish();
    }

    public void setDestinationSelection(ArrayList<String> locationStringList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, locationStringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destination.setAdapter(adapter);
    }

}
