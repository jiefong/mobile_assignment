package com.example.testlibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GenerateQRCode extends AppCompatActivity {

    private Button btnGenerate;
    private EditText etEmail;
    private Spinner spinner;
    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr_code);

        btnGenerate = findViewById(R.id.button_generate_qr_code);
        etEmail = findViewById(R.id.email);
        spinner = findViewById(R.id.spinner);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!etEmail.getText().toString().trim().isEmpty() || !etEmail.getText().toString().trim().equals("")) {
                    target = "myHome";
                    sendReport();
                }
            }
        });
    }

    private void sendReport() {
        //File local = new File(file.getAbsolutePath());

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + etEmail.getText().toString().trim()));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code for " + target);
//        emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml(new StringBuilder().append("<img src=\"https://chart.googleapis.com/chart?cht=qr&chl=Hello+world&choe=UTF-8&chs=200x200\"/>").toString()));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please download the QR code of " + target + " using this link : "
                + "https://chart.googleapis.com/chart?cht=qr&chl=" + target + "&choe=UTF-8&chs=200x200 " +
                "\n\nSent from Navigator, \nadmin");
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
        Toast.makeText(GenerateQRCode.this, "Generate QR code successful!", Toast.LENGTH_LONG).show();
        finish();
    }
}

