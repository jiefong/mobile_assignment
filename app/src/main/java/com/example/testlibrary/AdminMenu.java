package com.example.testlibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminMenu extends AppCompatActivity {

    private Button btnAddMap, btnDeleteMap, btnAddLocation, btnDeleteLocation, btnGenerateQr, btnChangePassword, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_menu);

        btnAddMap = findViewById(R.id.button_add_map);
        btnAddMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, AddMap.class));
            }
        });

        btnDeleteMap = findViewById(R.id.button_delete_map);
        btnDeleteMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, DeleteMap.class));
            }
        });

        btnAddLocation = findViewById(R.id.button_add_location);
        btnAddLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, AddLocation.class));
            }
        });

        btnDeleteLocation = findViewById(R.id.button_delete_location);
        btnDeleteLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, DeleteMap.class));
            }
        });

        btnGenerateQr = findViewById(R.id.button_generate_qr_code);
        btnGenerateQr.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, GenerateQRCode.class));
            }
        });

        btnChangePassword = findViewById(R.id.button_change_password);
        btnChangePassword.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMenu.this, ChangePassword.class));
            }
        });

        btnLogout = findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void logoutDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Admin.getInstance().setUsername("");
                        Admin.getInstance().setPassword("");
                        Intent myIntent = new Intent(AdminMenu.this, AdminLogin.class);
                        startActivity(myIntent);
                        finish();
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


}