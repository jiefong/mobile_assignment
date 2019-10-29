package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //test for qr code scanner
//        Intent intent = new Intent(this, BarcodeScanCameraActivity.class);
//        startActivity(intent);


        //test for map and pin
        PinView imageView = (PinView)findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.monkey));
        PointF point = new PointF(150, 350);
        imageView.setPin(point);


    }
}
