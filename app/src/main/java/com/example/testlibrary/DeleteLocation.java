package com.example.testlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DeleteLocation extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference myRef, myConnection;
    List<LocationInfo> locationList;
    List<Connection> addedConnections;

    PinView imageView;

    Spinner spinnerMap;
    private ArrayList<MapObject> mapList;
    private ArrayList<String> keyList;
    private String theKey, theUri;
    private String[] mapArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_location);

        //setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        myConnection = database.getReference().child("Connection");
        locationList = new ArrayList<>();
        addedConnections = new ArrayList<>();

        imageView = (PinView) findViewById(R.id.imageMap);

        spinnerMap = (Spinner) findViewById(R.id.spinnerMap);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        storageReference = FirebaseStorage.getInstance().getReference().child("map");
        keyList = new ArrayList<>();
        mapList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keyList.clear();
                mapList.clear();
                imageView.setVisibility(View.GONE);
                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    MapObject u = userSnapShot.getValue(MapObject.class);
                    mapList.add(u);
                    keyList.add(userSnapShot.getKey());
                }
                mapArray = new String[mapList.size()];
                for (int i = 0; i < mapList.size(); i++) {
                    mapArray[i] = mapList.get(i).getName();
                }
                setAdapter();

                // Read data from the database using this listener
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        locationList.clear();

                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                            LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                            if(u.getMapName().equals(theKey)){
                                locationList.add(u);

                            }
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    public void setMarker(List<LocationInfo> locationList) {
        imageView.setMarker(locationList);
    }

    public void setConnections(List<Connection> connections) {
        imageView.setAddedConnections(connections);
    }

    public void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mapArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMap.setAdapter(adapter);

        spinnerMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Context context = view.getContext();
                String temp = (String) parent.getItemAtPosition(position);
                for (int i = 0; i < mapList.size(); i++) {
                    if (mapList.get(i).getName().matches(temp)) {
                        theKey = keyList.get(i);
                        theUri = mapList.get(i).getImgUri();
                    }
                }
                if (!theKey.isEmpty() || !theKey.equals("")) {
                    storageReference.child(theKey).child(theUri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageView.setVisibility(View.VISIBLE);

                            Glide.with(context)
                                    .asBitmap()
                                    .load(uri.toString())
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                            imageView.setImage(ImageSource.bitmap(bitmap)); //For SubsampleImage
                                        }
                                    });
                        }
                    });
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                imageView.setVisibility(View.GONE);
            }
        });
    }

    public void addLocation(View v) {
        boolean check = true;

        //Get Location name
        EditText ETLocationName = (EditText) findViewById(R.id.editTextLocationName);
        String locationName = ETLocationName.getText().toString();
        if (locationName == null) {
            Toast.makeText(this, "Please key in the location name", Toast.LENGTH_SHORT).show();
            check = false;
        }

        //get the pin
        final PinViewAddLocation mapImage = (PinViewAddLocation) findViewById(R.id.imageMap);
        final PointF coor = mapImage.getPoint();

        if (coor == null) {
            Toast.makeText(this, "Please pin on the map", Toast.LENGTH_SHORT).show();
            check = false;
        }

        //get map name or key
        String mapName = spinnerMap.getSelectedItem().toString();
        String mapKey = "";
        if (mapName == null) {
            Toast.makeText(this, "Please select a map", Toast.LENGTH_SHORT).show();
            check = false;
        } else {
            for (int i = 0; i < mapList.size(); i++) {
                if (mapList.get(i).getName().equals(mapName)) {
                    mapKey = keyList.get(i);
                }
            }
        }

        //get the checkbox
        CheckBox checkBoxIsDestination = (CheckBox) findViewById(R.id.checkboxIsDestination);
        boolean isDestination = checkBoxIsDestination.isChecked();

        if (check) {
            Toast.makeText(getApplicationContext(), "complete", Toast.LENGTH_SHORT);
            Intent intent = new Intent(getApplication(), AddLocationStep2.class);
            intent.putExtra("location", locationName);
            intent.putExtra("mapName", mapKey);
            intent.putExtra("x", coor.x);
            intent.putExtra("y", coor.y);
            intent.putExtra("isDestination", isDestination);
            startActivity(intent);

        }

    }
}
