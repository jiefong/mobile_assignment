package com.example.testlibrary.ui.admin_dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.testlibrary.Admin;
import com.example.testlibrary.AdminDashboard;
import com.example.testlibrary.AdminLogin;
import com.example.testlibrary.Connection;
import com.example.testlibrary.LocationInfo;
import com.example.testlibrary.MapObject;
import com.example.testlibrary.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminDashboardFragment extends Fragment {

    private AdminDashboardViewModel adminDashboardViewModel;

    //for dashboard uses
    private DatabaseReference databaseReference, locationRef, connectionRef, ratingRef;
    private StorageReference storageReference;
    private ImageView imageView;
    private Spinner spinner;
    private ArrayList<LocationInfo> locationList;
    private ArrayList<Connection> connectionList;
    private ArrayList<MapObject> mapList;
    private ArrayList<String> keyList, locationKeyList, ratingList;
    private TextView tvMap, tvRating, tvLocation, tvConnection;
    private String theKey, theUri;
    private String[] mapArray;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminDashboardViewModel =
                ViewModelProviders.of(this).get(AdminDashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

//start of the codes for dashboard part
        imageView = root.findViewById(R.id.imageView);
        spinner = root.findViewById(R.id.spinner);
        tvMap = root.findViewById(R.id.map);
        tvRating = root.findViewById(R.id.rating);
        tvLocation = root.findViewById(R.id.location);
        tvConnection = root.findViewById(R.id.connection);

        locationRef = FirebaseDatabase.getInstance().getReference().child("LocationList");
        connectionRef = FirebaseDatabase.getInstance().getReference().child("Connection");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        ratingRef = FirebaseDatabase.getInstance().getReference().child("ratings");
        storageReference = FirebaseStorage.getInstance().getReference().child("map");
        ratingList = new ArrayList<>();
        locationList = new ArrayList<>();
        connectionList = new ArrayList<>();
        locationKeyList = new ArrayList<>();
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

                locationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        locationList.clear();
                        locationKeyList.clear();
                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                            LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                            locationList.add(u);
                            locationKeyList.add(userSnapShot.getKey());
                            //Create item based on the location list
                        }

                        connectionRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                connectionList.clear();
                                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                                    Connection u = userSnapShot.getValue(Connection.class);
                                    connectionList.add(u);
                                    //Create item based on the location list
                                }

                                ratingRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // This method is called once with the initial value and again
                                        // whenever data at this location is updated.
                                        ratingList.clear();
                                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                                            String u = userSnapShot.getValue(String.class);
                                            String s = u.split(",")[1];
                                            String str = s.split(": ")[1];
                                            ratingList.add(str);
                                            //Create item based on the location list
                                        }
                                        setInitial();
                                        setAdapter();
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
        return root;
    }

    //functions for dashboard views
    public void setInitial() {
        if (!mapList.isEmpty()) {
            tvMap.setText(Integer.toString(mapList.size()));
        } else {
            tvMap.setText("0");
        }
        if (!ratingList.isEmpty()) {
            double total = 0;
            for (String s : ratingList) {
                total += Double.parseDouble(s);
            }
            tvRating.setText(String.format("%.1f", (total / ratingList.size())));
        } else {
            tvRating.setText("0");
        }
    }

    public void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, mapArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                setContent();
//                System.out.println(theKey);
//                System.out.println(theUri);
                if (!theKey.isEmpty() || !theKey.equals("")) {
                    storageReference.child(theKey).child(theUri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageView.setVisibility(View.VISIBLE);
                            Glide.with(context).load(uri.toString()).into(imageView);
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

    public void setContent() {
        int locationNum = 0;
        int connectionNum = 0;
        if (!locationList.isEmpty()) {
            for (int i = 0; i < locationList.size(); i++) {
                if (locationList.get(i).getMapName().equals(theKey)) {
                    locationNum++;
                    for (Connection c : connectionList) {
                        if (c.getLocationKey_1().equals(locationKeyList.get(i)) || c.getLocationKey_2().equals(locationKeyList.get(i))) {
                            connectionNum++;
                        }
                    }
                }
            }
        }
        tvLocation.setText(Integer.toString(locationNum));
        tvConnection.setText(Integer.toString(connectionNum));
    }

}
