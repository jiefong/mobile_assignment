package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ManageMap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_map);
    }

    public void goAddMap(View view) {
        Intent addMapAct = new Intent(this,AddMap.class);
        startActivity(addMapAct);
    }

    public void goDeleteMap(View view) {
        Intent deleteMapAct = new Intent(this,DeleteMap.class);
        startActivity(deleteMapAct);
    }

    public void goBackMain(View view) {
        // for back button
    }
}
