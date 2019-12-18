package com.example.testlibrary.ui.add_map;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.testlibrary.AddMap;
import com.example.testlibrary.MapObject;
import com.example.testlibrary.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddMapFragment extends Fragment {

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private EditText etImageName;
    private String imgName;
    private Uri imgUri;
    private String bitmap;
    FrameLayout frameLayout;
    ImageView ivPreview;
    Bitmap selectedImage;

    private View root;

    private AddMapViewModel addMapViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addMapViewModel =
                ViewModelProviders.of(this).get(AddMapViewModel.class);
        root = inflater.inflate(R.layout.fragment_add_map, container, false);

        storageReference = FirebaseStorage.getInstance().getReference().child("map");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mapObject");
        mAuth = FirebaseAuth.getInstance();
        etImageName = root.findViewById(R.id.editTextImageName);
        frameLayout = root.findViewById(R.id.frameLayout);

        return root;

    }

    public final static int PICK_PHOTO_CODE = 1046;

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getActivity().getPackageManager()) != null) {
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
                selectedImage = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imgUri);
                // Load the selected image into a preview
                frameLayout.setVisibility(View.VISIBLE);
                ivPreview = (ImageView) root.findViewById(R.id.imageViewSelected);
                ivPreview.setImageBitmap(selectedImage);
            } catch(IOException e) { }
        }
    }

    public void uploadImage(View v){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are you sure you want to add this map?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        confirmUploadImage();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void confirmUploadImage(){
        bitmap = imgUri.getLastPathSegment();
        MapObject m = new MapObject(etImageName.getText().toString().trim(),bitmap);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(m);
        storageReference.child(key).child(imgUri.getLastPathSegment()).putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Map is successfully added!",Toast.LENGTH_LONG).show();
//                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Map is fail to be added!",Toast.LENGTH_LONG).show();
//                finish();
            }
        });
    }

    public void goBackManage(View view) {

//        finish();
    }
}