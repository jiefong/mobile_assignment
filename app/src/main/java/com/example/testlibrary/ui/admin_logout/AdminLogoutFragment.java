package com.example.testlibrary.ui.admin_logout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.testlibrary.Admin;
import com.example.testlibrary.AdminDashboard;
import com.example.testlibrary.AdminLogin;
import com.example.testlibrary.AdminLogout;
import com.example.testlibrary.R;
import com.example.testlibrary.ui.generate_qr_code.GenerateQRCodeViewModel;

public class AdminLogoutFragment extends Fragment {

    private AdminLogoutViewModel adminLogoutViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminLogoutViewModel = ViewModelProviders.of(this).get(AdminLogoutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_admin_logout, container, false);
        logoutDialog();
        return root;
    }

    public void logoutDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Admin.getInstance().setUsername("");
                        Admin.getInstance().setPassword("");
                        Intent myIntent = new Intent(getContext(), AdminLogin.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(myIntent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent myIntent = new Intent(getContext(), AdminDashboard.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(myIntent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
