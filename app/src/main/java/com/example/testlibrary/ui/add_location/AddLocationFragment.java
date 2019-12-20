package com.example.testlibrary.ui.add_location;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.testlibrary.AddLocationStep2;
import com.example.testlibrary.Connection;
import com.example.testlibrary.LocationInfo;
import com.example.testlibrary.MapObject;
import com.example.testlibrary.PinViewAddLocation;
import com.example.testlibrary.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class AddLocationFragment extends Fragment {

    private AddLocationViewModel addLocationViewModel;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference myRef, myConnection;
    List<LocationInfo> locationList;
    List<Connection> addedConnections;

    PinViewAddLocation imageView;

    Spinner spinnerMap;
    private ArrayList<MapObject> mapList;
    private ArrayList<String> keyList;
    private String theKey, theUri;
    private String[] mapArray;

    Button addButton;

    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addLocationViewModel =
                ViewModelProviders.of(this).get(AddLocationViewModel.class);
        root = inflater.inflate(R.layout.fragment_add_location, container, false);

//setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        myConnection = database.getReference().child("Connection");
        locationList = new ArrayList<>();
        addedConnections = new ArrayList<>();

        imageView = (PinViewAddLocation) root.findViewById(R.id.imageMap);

        spinnerMap = (Spinner) root.findViewById(R.id.spinnerMap);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        storageReference = FirebaseStorage.getInstance().getReference().child("map");
        keyList = new ArrayList<>();
        mapList = new ArrayList<>();

        addButton = root.findViewById(R.id.button3);

        addButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                addLocation(view);
            }
        });

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
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        return root;
    }

    public void setMarker(List<LocationInfo> locationList){
        List<LocationInfo> filteredList = new ArrayList<>();

        for (LocationInfo location : locationList){
            if(location.getMapName().equals(theKey)){
                filteredList.add(location);
            }
        }
        imageView.setMarker(filteredList);
    }

    public void setConnections(List<Connection> addedConnections){
        List<Connection> filteredConnections = new ArrayList<>();

        for (Connection connection : addedConnections){
            if(connection.getLocation_1().getMapName().equals(theKey)){
                filteredConnections.add(connection);
            }
        }

        imageView.setAddedConnections(filteredConnections);
    }

    public void setAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,mapArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMap.setAdapter(adapter);

        spinnerMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                if(!theKey.isEmpty() || !theKey.equals("")){
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
                                            imageView.setImageResolution(bitmap.getWidth(), bitmap.getHeight());
                                        }
                                    });
                        }
                    });}
                else{
                    imageView.setVisibility(View.GONE);
                }
                setMarker(locationList);
                setConnections(addedConnections);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                imageView.setVisibility(View.GONE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addLocation(View v){
        boolean check = true;

        //Get Location name
        EditText ETLocationName = (EditText) root.findViewById(R.id.editTextLocationName);
        String locationName = ETLocationName.getText().toString();

        if(locationName.length() == 0){
            Toast.makeText(getContext(), "Please key in the location name", Toast.LENGTH_SHORT).show();
            check = false;
        }else{
            System.out.println("-------------------");
        }

        //get the pin
        final PinViewAddLocation mapImage = (PinViewAddLocation) root.findViewById(R.id.imageMap);
        final PointF coor = mapImage.getPoint();

        if(coor == null){
            Toast.makeText(getContext(), "Please pin on the map", Toast.LENGTH_SHORT).show();
            check = false;
        }

        //get map name or key
        String mapName = spinnerMap.getSelectedItem().toString();
        String mapKey = "";
        if(mapName == null){
            Toast.makeText(getContext(), "Please select a map", Toast.LENGTH_SHORT).show();
            check = false;
        }else{
            for (int i =0; i< mapList.size(); i++){
                if(mapList.get(i).getName().equals(mapName)){
                    mapKey = keyList.get(i);
                }
            }
        }

        //get the checkbox
        CheckBox checkBoxIsDestination = (CheckBox) root.findViewById(R.id.checkboxIsDestination);
        boolean isDestination = checkBoxIsDestination.isChecked();

        check = false;
        if(check){
            Toast.makeText(getContext(), "complete", Toast.LENGTH_SHORT);
            Intent intent = new Intent(getActivity(), AddLocationStep2.class);
            intent.putExtra("location", locationName);
            intent.putExtra("mapName", mapKey);
            intent.putExtra("x", coor.x);
            intent.putExtra("y", coor.y);
            intent.putExtra("isDestination", isDestination);
            startActivity(intent);

        }

    }

    public void backToAdminMenu(View v){

//        super.onBackPressed();
    }
}
