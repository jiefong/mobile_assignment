package com.example.testlibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class GenerateQRCode extends AppCompatActivity {

    private Button btnGenerate;
    private EditText etEmail;
    private Spinner spinner;
    private String title;
    private DatabaseReference databaseReference, locationReference;
    private StorageReference storageReference;
    private ImageView imageView;
    private ArrayList<MapObject> mapList;
    private ArrayList<LocationInfo> locationList;
    private ArrayList<String> keyList, locationKeyList;
    private String theKey, theUri;
    private String[] mapArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr_code);

        btnGenerate = findViewById(R.id.button_generate_qr_code);
        etEmail = findViewById(R.id.email);
        spinner = findViewById(R.id.spinner);
        imageView = findViewById(R.id.imageViewSelected);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        locationReference = FirebaseDatabase.getInstance().getReference().child("LocationList");
        storageReference = FirebaseStorage.getInstance().getReference().child("map");
        keyList = new ArrayList<>();
        mapList = new ArrayList<>();
        locationList = new ArrayList<>();
        locationKeyList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keyList.clear();
                mapList.clear();
                imageView.setVisibility(View.GONE);
                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    MapObject u = userSnapShot.getValue(MapObject.class);
                    mapList.add(u);
                    keyList.add(userSnapShot.getKey());
                }
                mapArray = new String[mapList.size()];
                for (int i = 0; i < mapList.size(); i++) {
                    mapArray[i] = mapList.get(i).getName();
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!etEmail.getText().toString().trim().isEmpty() || !etEmail.getText().toString().trim().equals("")) {
                    title = (String) spinner.getSelectedItem();
                    sendReport();
                }
            }
        });
    }

    public void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mapArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Context context = view.getContext();
                String temp = (String) parent.getItemAtPosition(position);
                for (int i = 0; i < mapList.size(); i++) {
                    if (mapList.get(i).getName().matches(temp)) {
                        theKey = keyList.get(i);
                        theUri = mapList.get(i).getImgUri();
                    }
                }
                if (!theKey.isEmpty() || !theKey.equals("")) {
                    storageReference.child(theKey).child(theUri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.println(imageView.getVisibility());
                            imageView.setVisibility(View.VISIBLE);
                            Glide.with(context).load(uri.toString()).into(imageView);
                        }
                    });
                } else {
                    imageView.setVisibility(View.GONE);
                }
                locationReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        locationList.clear();
                        locationKeyList.clear();
                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                            LocationInfo u = userSnapShot.getValue(LocationInfo.class);
                            if(u.getMapName().equals(theKey)){
                                locationList.add(u);
                                locationKeyList.add(userSnapShot.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                imageView.setVisibility(View.GONE);
            }
        });
    }

    private void sendReport() {
        //File local = new File(file.getAbsolutePath());

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + etEmail.getText().toString().trim()));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code for " + title);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getMapName().equals(theKey)) {
                sb.append("Please download the QR code of " + locationList.get(i).getName() + " using this link : "
                        + "https://chart.googleapis.com/chart?cht=qr&chl=" + locationKeyList.get(i) + "&choe=UTF-8&chs=200x200\n\n");
                sb.append("Please download the QR code of " + locationList.get(i).getName() + " using this link : "
                        + "https://127.0.0.1/qrcode/" + locationList.get(i).getName() + "/" + locationKeyList.get(i) + "\n\n");
            }
        }
        sb.append("\n\nSent from Navigator, \nadmin");
        emailIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
        Toast.makeText(GenerateQRCode.this, "Generate QR code successful!", Toast.LENGTH_LONG).show();
        finish();
    }
}

