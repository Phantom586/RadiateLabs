package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PaymentFailed extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failed);

        saveInfoLocally = new SaveInfoLocally(this);
    }

//    public void Go_to_BarcodeScanner(View view) {
//        Intent in = new Intent(PaymentFailed.this, BarcodeScannerActivity.class);
//        in.putExtra("activity", "UCA");
//        in.putExtra("Type", "Store_Scan");
//        startActivity(in);
//    }

    public void Go_to_Cart(View view) {
        Intent in = new Intent(this, CartActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }

    public void Go_to_Profile(View view) {
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentFailed.this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentFailed.this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }
}
