package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    FirebaseStorage storage;
    StorageReference mStorageRef;
    List<LocationInfo> locationList;

    MultiSelectionSpinner mySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_step2);

        //setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
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

                for(DataSnapshot userSnapShot : dataSnapshot.getChildren()){
                    LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                    locationList.add(u);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        //set map image
        PinView imageView = (PinView)findViewById(R.id.imageMap);
        imageView.setImage(ImageSource.resource(R.drawable.fsktm_block_b));

        //initialize the multiselect spinner
        ArrayList<Item> items = new ArrayList<>();
        items.add(Item.builder().name("Item 1").value("item-1").build());
        items.add(Item.builder().name("Item 2").value("item-2").build());
        items.add(Item.builder().name("Item 3").value("item-3").build());

        System.out.println(items);

        mySpinner = findViewById(R.id.spn_items);
        mySpinner.setItems(items);
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
        MultiSelectListener listener = new MultiSelectListener(imageView);
        mySpinner.setMultiSelectListener(listener);
    }




}
