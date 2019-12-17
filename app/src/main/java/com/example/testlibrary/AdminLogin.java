package com.example.testlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminLogin extends AppCompatActivity {

    Button btnSignIn;
    TextView tvRegister;
    EditText etUsername, etPassword;
    DatabaseReference databaseReference;
    List<Admin> adminList;
    String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("admins");
        System.out.println(databaseReference);
        adminList = new ArrayList<>();

        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.button_signin);
        tvRegister = findViewById(R.id.register);

        databaseReference.addValueEventListener(new ValueEventListener() {
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

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String thisUsername = etUsername.getText().toString();
                String thisPassword = etPassword.getText().toString();
                if(validate(thisUsername,thisPassword)) {
                    Admin.getInstance().setUsername(thisUsername);
                    Admin.getInstance().setPassword(thisPassword);
                    //need change
                    Intent myIntent = new Intent(AdminLogin.this, AdminLandingPage.class);
                    startActivity(myIntent);
                    finish();
                }
                else{
                    Toast.makeText(AdminLogin.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        //admin register need implement?
        tvRegister.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(AdminLogin.this, AdminRegistration.class);
                        startActivity(myIntent);
                        finish();
                    }
                }
        );
    }
    private boolean validate(String un, String pw){
        for(int i = 0; i < adminList.size(); i++){
            if(adminList.get(i).getUsername().matches(un)){
                if(adminList.get(i).getPassword().matches(pw)){
                    //save current user
                    Admin.getInstance().setUsername(adminList.get(i).getUsername());
                    Admin.getInstance().setPassword(adminList.get(i).getUsername());
                    return true;
                }
                errorMessage = "Password incorrect";
            }
            errorMessage = "This user is not exist";
        }
        errorMessage = "Fail to login";
        return false;
    }
}
