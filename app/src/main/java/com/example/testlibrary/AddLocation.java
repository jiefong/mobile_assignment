package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddLocation extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference mStorageRef;
    List<LocationInfo> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        //setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        locationList = new ArrayList<>();

        PinViewAddLocation imageView = (PinViewAddLocation) findViewById(R.id.imageMap);
        imageView.setImage(ImageSource.resource(R.drawable.fsktm_block_b));

        Spinner spinnerMap = (Spinner) findViewById(R.id.spinnerMap);

        //change this array to get all the map from Firebase
        String[] arrayMapName = new String[] {
                "Main entrance", "DK 1", "DK 2"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayMapName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMap.setAdapter(adapter);

        spinnerMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = view.getContext();
                Toast.makeText(context, "test", Toast.LENGTH_SHORT);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void handleMapChange(Object sender){

        PinView imageView = (PinView)findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.fsktm_block_b));
    }

    public void addLocation(View v){
        boolean check = true;

        //Get Location name
        EditText ETLocationName = (EditText) findViewById(R.id.editTextLocationName);
        String locationName = ETLocationName.getText().toString();
        if(locationName == null){
            Toast.makeText(this, "Please key in the location name", Toast.LENGTH_SHORT).show();
            check = false;
        }

        //Get Map Name
        String mapName = "FSKTM BLock B ground floor";
        if(mapName == null){
            Toast.makeText(this, "Please select a map", Toast.LENGTH_SHORT).show();
            check = false;
        }

        //get the pin
        PinViewAddLocation mapImage = (PinViewAddLocation) findViewById(R.id.imageMap);
        PointF coor = mapImage.getPoint();
        if(coor == null){
            Toast.makeText(this, "Please pin on the map", Toast.LENGTH_SHORT).show();
            check = false;
        }

        if(check){
            System.out.println("---------------------------");
            System.out.println("adding to database");
            System.out.println(locationName);
            System.out.println(coor.x);
            //if success means database now has json tree database>message>hello,world!
            final LocationInfo info = new LocationInfo();
            info.setName(locationName);
            info.setX(coor.x);
            info.setY(coor.y);
            info.setMapName(mapName);
            myRef.child(locationName).setValue(info, new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {

                    if (error == null){
                        Toast.makeText(getApplicationContext(), "complete", Toast.LENGTH_SHORT);
                        Intent intent = new Intent(getApplication(), AddLocationStep2.class);
                        intent.putExtra("location", info.getName());
                        startActivity(intent);
                    }else{
                        System.out.println("false--------------------");
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                    }
                }
            });
        }

    }
}
