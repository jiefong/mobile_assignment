package com.example.testlibrary;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private Button btnChangePassword;
    private EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private String currentPassword, newPassword, confirmNewPassword;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference().child("admins");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    Admin u = userSnapShot.getValue(Admin.class);
                    if (u.getUsername().equals(Admin.getInstance().getUsername())) {
                        key = userSnapShot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        setupUI();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                define();
                if (validate()) {
                    mDatabase = mDatabase.child(key);

                    mDatabase.child("password").setValue((newPassword))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ChangePassword.this, "Change password successful!", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChangePassword.this, "Change password fail! Try again later", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });

                }

            }
        });
    }

    private void setupUI() {
        btnChangePassword = findViewById(R.id.button_change_password);
        etCurrentPassword = findViewById(R.id.edit_text_current_password);
        etNewPassword = findViewById(R.id.edit_text_new_password);
        etConfirmNewPassword = findViewById(R.id.edit_text_confirm_new_password);
    }

    private void define() {
        currentPassword = etCurrentPassword.getText().toString().trim();
        newPassword = etNewPassword.getText().toString().trim();
        confirmNewPassword = etConfirmNewPassword.getText().toString().trim();
    }

    private boolean validate() {

        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(getApplicationContext(), "Enter current password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(currentPassword).matches((Admin.getInstance().getPassword()))) {
            Toast.makeText(getApplicationContext(), "Enter correct current password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(getApplicationContext(), "Enter new password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches(confirmNewPassword)) {
            Toast.makeText(getApplicationContext(), "Confirm password is not same as password, reenter!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}

