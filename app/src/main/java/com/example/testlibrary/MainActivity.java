package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
