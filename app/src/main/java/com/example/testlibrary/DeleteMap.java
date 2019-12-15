package com.example.testlibrary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DeleteMap extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ImageView imageView;
    private Spinner spinner;
    private ArrayList<MapObject> mapList;
    private ArrayList<String> keyList;
    private String theKey, theUri;
    private String[] mapArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_map);

        imageView = findViewById(R.id.imageViewSelected);
        spinner = findViewById(R.id.spinner);

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


    public void goBackManage(View view) {
        finish();
    }

    public void deleteMap(View view){
        storageReference.child(theKey).delete();
        databaseReference.child(theKey).removeValue();
    }
}
