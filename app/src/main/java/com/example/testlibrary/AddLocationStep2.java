package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddLocationStep2 extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myConnection;
    FirebaseStorage storage;
    StorageReference mStorageRef;
    List<LocationInfo> locationList;

    LocationInfo currentLocation;

    MultiSelectionSpinner mySpinner;
    MultiSelectListener listener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_step2);

        //setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        myConnection = database.getReference().child("Connection");
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        locationList = new ArrayList<>();

        // Read data from the database using this listener
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                locationList.clear();

                //set map image
                PinView imageView = (PinView) findViewById(R.id.imageMap);
                imageView.setImage(ImageSource.resource(R.drawable.fsktm_block_b));

                //initialize the multiselect spinner
                ArrayList<Item> items = new ArrayList<>();
                mySpinner = findViewById(R.id.spn_items);

                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                    locationList.add(u);
                    //Create item based on the location list

                }
                //set spinner items for Location
                for (LocationInfo location : locationList) {
                    items.add(Item.builder().name(location.name).value(location.name).build());
                }
                mySpinner.setItems(items);

                //set marker of objects on map
                imageView.setMarker(locationList);

                //get x and y
                float x = 0, y = 0;
                String location, mapName;
                if (savedInstanceState == null) {
                    Bundle extras = getIntent().getExtras();
                    if (extras == null) {
                        onBackPressed();
                    } else {
                        x = extras.getFloat("x");
                        y = extras.getFloat("y");
                        location = extras.getString("location");
                        mapName = extras.getString("mapName");

                        currentLocation.setX(x);
                        currentLocation.setY(y);
                        currentLocation.setMapName(mapName);
                        currentLocation.setName(location);
                    }
                }
                PointF currentLoc = new PointF(x, y);
                imageView.setCurrentLocation(currentLoc);

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

                //set up multi listener
                listener = new MultiSelectListener(imageView, locationList, currentLoc);
                mySpinner.setMultiSelectListener(listener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void addLocationConnection(View v) {

        myRef.child(currentLocation.getName()).setValue(currentLocation, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    // add connections
                    // for each of the selected connections
                    ArrayList<LocationInfo> selected = listener.getSelected();
                    Connection connection;
                    for (LocationInfo connectedLocation : selected) {
                        connection = new Connection(currentLocation, connectedLocation);
                        myConnection.child(connection.getName()).setValue(currentLocation, new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                if (error == null) {
                                    //go back to the main page
                                    //stimulate the whole network
                                } else {
                                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void goStep1(View v) {
        super.onBackPressed();
    }
}
