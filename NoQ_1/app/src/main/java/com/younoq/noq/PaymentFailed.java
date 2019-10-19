package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PaymentFailed extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;
    public static final String Phone = "com.example.noq_1.PHONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failed);
    }

    public void Go_to_BarcodeScanner(View view) {
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentFailed.this, MyProfile.class);
        in.putExtra(Phone, phone);
        startActivity(in);
    }

    public void Go_to_Profile(View view) {
        Intent in = new Intent(PaymentFailed.this, BarcodeScannerActivity.class);
        in.putExtra("Type", "Product_Scan");
        startActivity(in);
    }
}
