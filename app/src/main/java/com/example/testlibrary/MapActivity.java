package com.example.testlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    FirebaseDatabase database;
    private StorageReference storageReference;
    DatabaseReference myRef;
    DatabaseReference myConnection, databaseReference;
    List<LocationInfo> locationList;
    List<String> locationKeyList;

    private ArrayList<MapObject> mapList;
    private ArrayList<String> keyList;
    private String[] mapArray;

    PinView imageView;

    List<Connection> addedConnections;

    String startString, endString;
    int startLocation, endLocation;

    //used for djikstra
    DijkstraAlgo dj;

    float[][] matrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String location, mapName;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                onBackPressed();
            } else {
                startString = extras.getString("Start");
                endString = extras.getString("End");
            }
        }
        //setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        myConnection = database.getReference().child("Connection");

        locationList = new ArrayList<>();
        locationKeyList = new ArrayList<>();
        addedConnections = new ArrayList<>();

        //test for map and pin
        imageView = (PinView) findViewById(R.id.imageView);

        // Dijsktra algo
        dj = new DijkstraAlgo();

        //TODO get all the points available in database
        // Read data from the database using this listener
        //read all the image object and select the correct one
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        storageReference = FirebaseStorage.getInstance().getReference().child("map");
        keyList = new ArrayList<>();
        mapList = new ArrayList<>();

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

                    final String mapKey = u.getMapName();

                    if (u.getName().equals(startString)) {
                        startLocation = locationList.indexOf(u);

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                keyList.clear();
                                mapList.clear();
                                imageView.setVisibility(View.GONE);
                                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                                    MapObject u = userSnapShot.getValue(MapObject.class);
                                    String key = userSnapShot.getKey();
                                    if (key.equals(mapKey)) setMap(u, key);
                                }
                                mapArray = new String[mapList.size()];
                                for (int i = 0; i < mapList.size(); i++) {
                                    mapArray[i] = mapList.get(i).getName();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else if (u.getName().equals(endString)) {
                        endLocation = locationList.indexOf(u);
                    }
                    //Create item based on the location list
                }

                //set spinner items for Location
                for (LocationInfo location : locationList) {
                    items.add(Item.builder().name(location.name).value(location.name).build());
                }
                initializeMatrix(locationList);
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
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        //TODO get all the vertex and calculate the distance
        //TODO create a matrix of the whole network
        //TODO put into dijkstra algo to get shortest path
        //TODO draw into the map
    }

    public void initializeMatrix(List<LocationInfo> locationList) {
        this.locationList = locationList;
        int size = locationList.size();
        matrix = new float[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    public void setMap(MapObject u, String mapKey) {
        final Context context = getApplicationContext();
        String theKey = mapKey;
        String theUri = u.getImgUri();

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

    public void setConnections(List<Connection> connections) {
        int index1, index2;
        for (Connection connection : connections) {
            String one = connection.getLocationKey_1();
            String two = connection.getLocationKey_2();

            index1 = locationKeyList.indexOf(one);
            index2 = locationKeyList.indexOf(two);

            matrix[index1][index2] = connection.getDistance();
            matrix[index2][index1] = connection.getDistance();

            for (float[] x : matrix) {
                for (float y : x) {
                    System.out.print(y + " ");
                }
                System.out.println();
            }
        }

        ArrayList<Integer> path = dj.dijkstra(matrix, startLocation, endLocation);
//        after get the path get the nessary location info
//        use imageview to draw the map
        List<LocationInfo> locationPath = new ArrayList<>();
        for (Integer num : path) {
            locationPath.add(locationList.get(num));
        }
        imageView.setRoute(locationPath);
    }

    public void gotoRating(View view) {
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("route", "Navigation : From " + startString + " to " + endString);
        startActivity(intent);
        finish();
    }
}
