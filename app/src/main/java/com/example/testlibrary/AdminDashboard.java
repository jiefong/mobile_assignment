package com.example.testlibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class AdminDashboard extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_add_map, R.id.nav_delete_map,
                R.id.nav_add_location, R.id.nav_delete_location, R.id.nav_generate_qr, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                //Label is the name of the fragment
                CharSequence destinationLabel = destination.getLabel();
                Intent intent = new Intent(getApplicationContext(), AdminDashboard.class);
                if (!destinationLabel.equals("Dashboard") && !destinationLabel.equals("Log Out")){
                    if (destinationLabel.equals("Add Map"))
                        intent = new Intent(getApplicationContext(), AddMap.class);
                    else if (destinationLabel.equals("Delete Map"))
                        intent = new Intent(getApplicationContext(), DeleteMap.class);
                    else if (destinationLabel.equals("Add Location"))
                        intent = new Intent(getApplicationContext(), AddLocation.class);
                    else if (destinationLabel.equals("Delete Location"))
                        intent = new Intent(getApplicationContext(), DeleteLocation.class);
                    else if (destinationLabel.equals("Generate QR Code"))
                        intent = new Intent(getApplicationContext(), GenerateQRCode.class);
                    //use the label to intent??

                    startActivity(intent);
                }else{
                    if(destinationLabel.equals("Log Out")){
                        logoutDialog();
                    }
                }

            }
        });

        //start of the codes for dashboard part
        imageView = findViewById(R.id.imageView);
        spinner = findViewById(R.id.spinner);
        tvMap = findViewById(R.id.map);
        tvRating = findViewById(R.id.rating);
        tvLocation = findViewById(R.id.location);
        tvConnection = findViewById(R.id.connection);

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
                for(DataSnapshot userSnapShot : dataSnapshot.getChildren()){
                    MapObject u = userSnapShot.getValue(MapObject.class);
                    mapList.add(u);
                    keyList.add(userSnapShot.getKey());
                }
                mapArray = new String[mapList.size()];
                for(int i = 0 ; i < mapList.size(); i++){
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
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    //this function is used to show the side drawer
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    //functions for dashboard views
    public void setInitial(){
        if(!mapList.isEmpty()) {
            tvMap.setText(Integer.toString(mapList.size()));
        }
        else{
            tvMap.setText("0");
        }
        if(!ratingList.isEmpty()) {
            double total = 0;
            for (String s : ratingList) {
                total += Double.parseDouble(s);
            }
            tvRating.setText(String.format(".1f", total / ratingList.size()));
        }
        else{
            tvRating.setText("0");
        }
    }

    public void setAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,mapArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Context context = view.getContext();
                String temp = (String) parent.getItemAtPosition(position);
                for(int i = 0; i < mapList.size(); i++){
                    if(mapList.get(i).getName().matches(temp)){
                        theKey = keyList.get(i);
                        theUri = mapList.get(i).getImgUri();
                    }
                }
                setContent();
//                System.out.println(theKey);
//                System.out.println(theUri);
                if(!theKey.isEmpty() || !theKey.equals("")){
                    storageReference.child(theKey).child(theUri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageView.setVisibility(View.VISIBLE);
                            Glide.with(context).load(uri.toString()).into(imageView);
                        }
                    });}
                else{
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                imageView.setVisibility(View.GONE);
            }
        });
    }


    public void goBackManage(View view) {
        finish();
    }

    public void setContent(){
        int locationNum = 0;
        int connectionNum = 0;
        if(!locationList.isEmpty()) {
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

    //for log out uses
    public void logoutDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Admin.getInstance().setUsername("");
                        Admin.getInstance().setPassword("");
                        Intent myIntent = new Intent(AdminDashboard.this, AdminLogin.class);
                        startActivity(myIntent);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
