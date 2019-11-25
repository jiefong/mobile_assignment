package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define database & reference
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        //put value to database using setValue()
        myRef.setValue("Hello, World!");
        //if success means database now has json tree database>message>hello,world!

        // Read from the database using this listener
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void goScanActivity(View view){
        //        test for qr code scanner
        Intent intent = new Intent(this, BarcodeScanCameraActivity.class);
        startActivity(intent);
    }

    public void showQRcode(View v){
        //TODO show the sample QR code
    }

}
