package com.example.testlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddMap extends AppCompatActivity {

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private EditText etImageName;
    private String imgName;
    private Uri imgUri;
    private String bitmap;
    ImageView ivPreview;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_map);

        storageReference = FirebaseStorage.getInstance().getReference().child("map");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        mAuth = FirebaseAuth.getInstance();
        etImageName = findViewById(R.id.editTextImageName);
    }

    public final static int PICK_PHOTO_CODE = 1046;

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            imgUri = data.getData();
            // Do something with the photo based on Uri
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                // Load the selected image into a preview
                ivPreview = (ImageView) findViewById(R.id.imageViewSelected);
                ivPreview.setImageBitmap(selectedImage);
            } catch(IOException e) { }
        }
    }

    public void uploadImage(View v){
        bitmap = imgUri.getLastPathSegment();
        MapObject m = new MapObject(etImageName.getText().toString().trim(),bitmap);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(m);
        storageReference.child(key).child(imgUri.getLastPathSegment()).putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddMap.this, "success!",Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddMap.this, "fail!",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void goBackManage(View view) {
        finish();
    }
}
