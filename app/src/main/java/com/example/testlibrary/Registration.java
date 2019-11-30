package com.example.testlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

public class Registration extends AppCompatActivity {

    private TextView text;
    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private String username, password,confirmPassword;
    private DatabaseReference mDatabase;
    List<Admin> adminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("admins");
        adminList = new ArrayList<>();

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirm_password);
        btnRegister = findViewById(R.id.button_register);
        text = findViewById(R.id.sign_in);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminList.clear();

                for(DataSnapshot userSnapShot : dataSnapshot.getChildren()){
                    Admin u = userSnapShot.getValue(Admin.class);
                    adminList.add(u);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        final Intent myIntent = new Intent(this, MainActivity.class);
        text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                startActivity(myIntent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                confirmPassword = etConfirmPassword.getText().toString().trim();

                // Code here executes on main thread after user presses button
                if(validate()) {

                    mDatabase = mDatabase.child(""+username);
                    Admin admin = Admin.getInstance();
                    admin.setUsername(username);
                    admin.setPassword(password);

                    mDatabase.setValue(admin)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Registration.this, "Registration successful! Sign in now.", Toast.LENGTH_LONG).show();
                                    startActivity(myIntent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Registration.this, "Registration Fail! Sign in now.", Toast.LENGTH_LONG).show();
                                    startActivity(myIntent);
                                }
                            });

                }

            }
        });
    }

    private boolean validate(){
        boolean check = true;

        for(int i = 0; i < adminList.size(); i++) {
            if (adminList.get(i).getUsername().matches(username)) {
                Toast.makeText(getApplicationContext(), "Username is taken!", Toast.LENGTH_SHORT).show();
                return check = false;
            }
        }

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
            return check = false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return check = false;
        }

        if (!password.matches(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Comfirm password is not same as password, reenter!", Toast.LENGTH_SHORT).show();
            return check = false;
        }

        return check;
    }
}
