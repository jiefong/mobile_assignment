package com.example.testlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference mStorageRef;
    List<LocationInfo> locationList;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define database & reference
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference().child("LocationList");
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        locationList = new ArrayList<>();

//        //if success means database now has json tree database>message>hello,world!
//        LocationInfo info = new LocationInfo();
//        info.setName("example");
//        info.setX(1500);
//        info.setY(1500);
//        myRef.child("example").setValue(info);


        // Read data from the database using this listener
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                locationList.clear();

                for(DataSnapshot userSnapShot : dataSnapshot.getChildren()){
                    LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                    locationList.add(u);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        button = findViewById(R.id.button);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminLogin.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
    }

    public void goScanActivity(View view){
        //go scan QR code page
        Intent intent = new Intent(this, BarcodeScanCameraActivity.class);
        startActivity(intent);
    }

    public void showQRcode(View v){
        //TODO show the sample QR code
    }

}
