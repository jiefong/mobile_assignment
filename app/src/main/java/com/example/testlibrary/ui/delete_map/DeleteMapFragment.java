package com.example.testlibrary.ui.delete_map;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
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

public class DeleteMapFragment extends Fragment {

    private DeleteMapViewModel deleteMapViewModel;

    private DatabaseReference databaseReference, locationRef, connectionRef;
    private StorageReference storageReference;
    private ImageView imageView;
    private Spinner spinner;
    private ArrayList<LocationInfo> locationList;
    private ArrayList<Connection> connectionList;
    private ArrayList<MapObject> mapList;
    private ArrayList<String> keyList, locationKeyList;
    private String theKey, theUri;
    private String[] mapArray;
    private Button btnDelete;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deleteMapViewModel =
                ViewModelProviders.of(this).get(DeleteMapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_delete_map, container, false);

        imageView = root.findViewById(R.id.imageViewSelected);
        spinner = root.findViewById(R.id.spinner);
        btnDelete = root.findViewById(R.id.btnDeleteMap);

        locationRef = FirebaseDatabase.getInstance().getReference().child("LocationList");
        connectionRef = FirebaseDatabase.getInstance().getReference().child("Connection");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        storageReference = FirebaseStorage.getInstance().getReference().child("map");
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMap();
            }
        });
        return root;
    }

    public void setAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item,mapArray);
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
                System.out.println(theKey);
                System.out.println(theUri);
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

    public void deleteMap(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are you sure you want to delete this map?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        confirmDeleteMap();
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

    public void confirmDeleteMap(){
        storageReference.child(theKey).delete();
        Toast.makeText(getContext(),"Map is deleted!", Toast.LENGTH_SHORT);
        databaseReference.child(theKey).removeValue();
        for(int i = 0; i < locationList.size(); i++){
            if(locationList.get(i).getMapName().equals(theKey)){
                locationRef.child(locationKeyList.get(i)).removeValue();
                for(Connection c : connectionList){
                    if(c.getLocationKey_1().equals(locationKeyList.get(i)) || c.getLocationKey_2().equals(locationKeyList.get(i))){
                        connectionRef.child(c.getName()).removeValue();
                    }
                }
            }
        }
    }
}
