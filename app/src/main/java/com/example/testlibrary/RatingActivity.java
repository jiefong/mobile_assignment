package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText etComment;
    RatingBar ratingBar;
    DatabaseReference databaseReference;
    String rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Intent intent = this.getIntent();
        rating = intent.getStringExtra("route");

        databaseReference  = FirebaseDatabase.getInstance().getReference().child("ratings");

        etComment = findViewById(R.id.comment);
        btnSubmit = findViewById(R.id.btn_submit);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = rating + " ,Rating : "+ ratingBar.getRating() + " ,Comment : " + etComment.getText().toString().trim();
                databaseReference.push().setValue(rating);
                //Toast.makeText(RatingActivity.this, rating, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
