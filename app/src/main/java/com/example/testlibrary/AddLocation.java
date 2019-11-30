package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;

public class AddLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        PinView imageView = (PinView)findViewById(R.id.imageMap);
        imageView.setImage(ImageSource.resource(R.drawable.fsktm_block_b));

        Spinner spinnerMap = (Spinner) findViewById(R.id.spinnerMap);

        //change this array to get all the map from Firebase
        String[] arrayMapName = new String[] {
                "Main entrance", "DK 1", "DK 2"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayMapName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMap.setAdapter(adapter);

        spinnerMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = view.getContext();
                Toast.makeText(context, "test", Toast.LENGTH_SHORT);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    public void handleMapChange(Object sender){

        PinView imageView = (PinView)findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.fsktm_block_b));
    }
}
