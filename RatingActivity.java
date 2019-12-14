package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class RatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Button submit = findViewById(R.id.btn_submit);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = "Rating is :"+ ratingBar.getRating();
                Toast.makeText(RatingActivity.this,rating, Toast.LENGTH_LONG).show();
            }
        });

    }
}
