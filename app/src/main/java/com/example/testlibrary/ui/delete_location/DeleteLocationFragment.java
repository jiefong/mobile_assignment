package com.example.testlibrary.ui.delete_location;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.testlibrary.Connection;
import com.example.testlibrary.LocationInfo;
import com.example.testlibrary.MapObject;
import com.example.testlibrary.PinView;
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
import java.util.List;

public class DeleteLocationFragment extends Fragment {

    private DeleteLocationViewModel deleteLocationViewModel;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference myRef, myConnection;
    List<LocationInfo> locationList, filteredList;
    List<Connection> addedConnections;

    PinView imageView;

    Spinner spinnerMap, spinnerDeleteLocation;
    private ArrayList<MapObject> mapList;
    private ArrayList<String> keyList, locationKeyList;
    private String theKey, theUri;
    private String[] mapArray;
    private LocationInfo  theItem = new LocationInfo();
    private String tempRemove;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deleteLocationViewModel =
                ViewModelProviders.of(this).get(DeleteLocationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_delete_location, container, false);

//setting up the database
        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        myConnection = database.getReference().child("Connection");
        locationList = new ArrayList<>();
        locationKeyList = new ArrayList<>();
        filteredList = new ArrayList<>();
        addedConnections = new ArrayList<>();

        imageView = (PinView) root.findViewById(R.id.imageMap);

        spinnerMap = (Spinner) root.findViewById(R.id.spinnerMap);
        spinnerDeleteLocation = (Spinner) root.findViewById(R.id.spinnerDeleteLocation);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        storageReference = FirebaseStorage.getInstance().getReference().child("map");

        keyList = new ArrayList<>();
        mapList = new ArrayList<>();

        Button btnDeleteLocation = (Button) root.findViewById(R.id.btnDeleteLocation);

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
                        locationKeyList.clear();
                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                            LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                            locationList.add(u);
                            locationKeyList.add(userSnapShot.getKey());
                            //Create item based on the location list
                        }
                        setMarker();
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
                        setConnections();
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

        spinnerDeleteLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = view.getContext();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        btnDeleteLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //get spinner item
//                System.out.println(locationList);
//                System.out.println(locationKeyList);
//                Log.e("TAG", "gg: " + locationList);
//                Log.e("TAG", "gg: " + locationKeyList);
                int index = locationList.indexOf(theItem);
                String keyDeleteLocation = locationKeyList.get(index);
                // Voon voon please continue from here thankssss
                //welcome
                for(Connection c : addedConnections){
                    if(c.getLocationKey_1().equals(keyDeleteLocation) || c.getLocationKey_2().equals(keyDeleteLocation)){
                        myConnection.child(c.getName()).removeValue();
                    }
                }
                myRef.child(keyDeleteLocation).removeValue();
                Toast.makeText(getContext(),"Location is deleted !",Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    public void setMarker() {
        filteredList.clear();

        for (LocationInfo location : locationList){
            if(tempRemove == null){
                if(location.getMapName().equals(theKey)){
                    filteredList.add(location);
                }
            }else{
                if(location.getMapName().equals(theKey) && !location.getName().equals(tempRemove)){
                    filteredList.add(location);
                }
            }

        }
        imageView.setMarker(filteredList);
    }

    public void setConnections() {
        List<Connection> filteredConnections = new ArrayList<>();

        for (Connection connection : addedConnections){
            if(tempRemove == null) {
                if (connection.getLocation_1().getMapName().equals(theKey)) {
                    filteredConnections.add(connection);
                }
            }else{
                if (connection.getLocation_1().getMapName().equals(theKey)
                        && !(connection.getLocation_1().getName().equals(tempRemove)
                        || connection.getLocation_2().getName().equals(tempRemove))) {
                    filteredConnections.add(connection);
                }
            }
        }

        imageView.setAddedConnections(filteredConnections);
    }

    public void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mapArray);
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

                            setMarker();
                            setConnections();
                            setDeleteLocation();
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

    public void setDeleteLocation(){
        final String[] locationArray = new String[filteredList.size()];

        for (int i =0; i< filteredList.size(); i++){
            locationArray[i] = filteredList.get(i).getName();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, locationArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeleteLocation.setAdapter(adapter);

        spinnerDeleteLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Context context = view.getContext();
                String temp = (String) parent.getItemAtPosition(position);
                for(LocationInfo i : filteredList){
                    if(i.getName().equals(temp)){
                        theItem = i;
                    }
                }
                //theItem = filteredList.get(position);
                //remove the point and related connections
                tempRemove = temp;
                setMarker();
                setConnections();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                imageView.setVisibility(View.GONE);
            }
        });
    }

    public void goBackManage(View view) {

        //do transaction change
        return;
    }
}