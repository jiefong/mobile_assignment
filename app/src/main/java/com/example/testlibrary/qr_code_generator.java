package com.example.qrcodegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class qr_code_generator extends AppCompatActivity {

    private Button buttongenerate;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        final Context context = this;
        editText = (EditText) this.findViewById(R.id.editText);
        buttongenerate = (Button) this.findViewById(R.id.buttongenerate);
        buttongenerate.setOnClickListener(View);
        String textQr = editText.getText().toString();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textQr, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Intent intent = new Intent(context, showcode.class);
            intent.putExtra("pic", bitmap);
            context.startActivity(intent);
        } catch  (WriterException e) {
            e.printStackTrace();
        }


    }



}
}
