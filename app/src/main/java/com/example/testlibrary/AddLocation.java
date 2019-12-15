package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddLocation extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef, myConnection;
    List<LocationInfo> locationList;
    List<Connection> addedConnections;

    PinViewAddLocation imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        //setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        myConnection = database.getReference().child("Connection");
        locationList = new ArrayList<>();
        addedConnections = new ArrayList<>();

        Spinner spinnerMap = (Spinner) findViewById(R.id.spinnerMap);

        //change this array to get all the map from Firebase
        String[] arrayMapName = new String[] {
                "Main entrance", "DK 1", "DK 2"
        };

        imageView = (PinViewAddLocation) findViewById(R.id.imageMap);
        imageView.setImage(ImageSource.resource(R.drawable.fsktm_block_b));
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fsktm_block_b);
        imageView.setImageResolution(bmp.getWidth(), bmp.getHeight());

        // Read data from the database using this listener
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                locationList.clear();

                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                    locationList.add(u);
                    //Create item based on the location list
                }
                setMarker(locationList);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayMapName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMap.setAdapter(adapter);

        spinnerMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = view.getContext();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void setMarker(List<LocationInfo> locationList){
        imageView.setMarker(locationList);
    }

    public void setConnections(List<Connection> connections){
        System.out.println(connections);
        imageView.setAddedConnections(connections);
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
        final PinViewAddLocation mapImage = (PinViewAddLocation) findViewById(R.id.imageMap);
        final PointF coor = mapImage.getPoint();

        if(coor == null){
            Toast.makeText(this, "Please pin on the map", Toast.LENGTH_SHORT).show();
            check = false;
        }

        //get the checkbox
        CheckBox checkBoxIsDestination = (CheckBox) findViewById(R.id.checkboxIsDestination);
        boolean isDestination = checkBoxIsDestination.isChecked();

        if(check){
            Toast.makeText(getApplicationContext(), "complete", Toast.LENGTH_SHORT);
            Intent intent = new Intent(getApplication(), AddLocationStep2.class);
            intent.putExtra("location", locationName);
            intent.putExtra("mapName", mapName);
            intent.putExtra("x", coor.x);
            intent.putExtra("y", coor.y);
            intent.putExtra("isDestination", isDestination);
            startActivity(intent);

        }

    }
}
