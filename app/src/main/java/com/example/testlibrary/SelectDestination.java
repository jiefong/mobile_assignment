package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class SelectDestination extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);

        String[] arrayCurLocation = new String[] {
               "Main entrance"
        };
        String[] arrayLocationName = new String[] {
                "Main entrance", "DK 1", "DK 2"
        };

        Spinner curLocation = (Spinner) findViewById(R.id.spinnerCurrentLocation);
        Spinner destination = (Spinner) findViewById(R.id.spinnerDestination);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayCurLocation);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        curLocation.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayLocationName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destination.setAdapter(adapter);
    }

    public void showMap(View v){
        //        test for qr code scanner
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
