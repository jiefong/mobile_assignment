package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddLocationStep2 extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myConnection;
    List<LocationInfo> locationList;
    List<String> locationKeyList;
    List<Connection> addedConnections;

    LocationInfo currentLocation;

    MultiSelectionSpinner mySpinner;
    MultiSelectListener listener;

    PinView imageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_step2);

        //setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        myConnection = database.getReference().child("Connection");

        locationList = new ArrayList<>();
        locationKeyList = new ArrayList<>();
        addedConnections = new ArrayList<>();

        //current location is used to save the new location
        currentLocation = new LocationInfo();

        //get x and y
        float x = 0, y = 0;
        String location, mapName;
        boolean isDestination;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                onBackPressed();
            } else {
                x = extras.getFloat("x");
                y = extras.getFloat("y");
                location = extras.getString("location");
                mapName = extras.getString("mapName");
                isDestination = extras.getBoolean("isDestination");

                currentLocation.setX(x);
                currentLocation.setY(y);
                currentLocation.setMapName(mapName);
                currentLocation.setName(location);
                currentLocation.setDestination(isDestination);
            }
        }
        //set map image
        imageView = (PinView) findViewById(R.id.imageMap);
        imageView.setImage(ImageSource.resource(R.drawable.fsktm_block_b));

        final PointF currentLoc = new PointF(x, y);

        imageView.setCurrentLocation(currentLoc);

        mySpinner = findViewById(R.id.spn_items);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(getApplicationContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO clear all the connection
                Toast.makeText(getApplicationContext(), "Clear", Toast.LENGTH_SHORT);
            }
        });

        // Read data from the database using this listener
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                locationList.clear();
                locationKeyList.clear();

                //initialize the multiselect spinner
                ArrayList<Item> items = new ArrayList<>();

                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                    locationList.add(u);
                    locationKeyList.add(userSnapShot.getKey());
                    //Create item based on the location list
                }
                //set spinner items for Location
                for (LocationInfo location : locationList) {
                    items.add(Item.builder().name(location.name).value(location.name).build());
                }
                mySpinner.setItems(items);
                setMarkers(locationList);

                setSpinnerListener(locationList, currentLoc);
                //set marker of objects on map
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        myConnection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                addedConnections.clear();

                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    Connection u = userSnapShot.getValue(Connection.class);
                    addedConnections.add(u);
                    //Create item based on the location list
                }

                //set marker of objects on map
                setConnections(addedConnections);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void addLocationConnection(View v) {
        final ArrayList<LocationInfo> connectedLocations = listener.getSelected();
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
        final String key = myRef.push().getKey();
        myRef.child(key).setValue(currentLocation, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    // add connections
                    // for each of the selected connections
                    Connection connection;
                    if (connectedLocations.size() > 0) {
                        for (LocationInfo connectedLocation : connectedLocations) {
                            int index = -1;
                            for (int i = 0; i < locationList.size(); i++) {
                                if(locationList.get(i).getName() == connectedLocation.getName()){
                                    index = i;
                                }
                            }
                            connection = new Connection();
                            connection.setLocation_1(currentLocation);
                            connection.setLocation_2(connectedLocation);
                            connection.setLocationKey_1(key);
                            connection.setLocationKey_2(locationKeyList.get(index));
                            myConnection.child(connection.getName()).setValue(connection, new DatabaseReference.CompletionListener() {
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    if (error == null) {
                                        //TODO go to admin layout page
                                        Intent intent = new Intent(getApplication(), MainActivity.class);
                                        startActivity(intent);
                                        //go back to the main page
                                        //stimulate the whole network
                                    } else {
                                        //TODO maybe need to remove the location added
                                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                        }
                    } else {
                        //TODO go to admin layout page
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                    }

                } else {
                    //TODO back to step 1 ??
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void goStep1(View v) {
        super.onBackPressed();
    }

    public void setMarkers(List<LocationInfo> locations) {
        imageView.setMarker(locations);
    }

    public void setConnections(List<Connection> connections) {
        imageView.setAddedConnections(connections);
    }

    public void setSpinnerListener(List<LocationInfo> locationList, PointF currentLoc) {
        listener = new MultiSelectListener(imageView, locationList, currentLoc);
        mySpinner.setMultiSelectListener(listener);
    }
}
