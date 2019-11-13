package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

public class PaymentSuccess extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        saveInfoLocally = new SaveInfoLocally(this);
        tv1 = findViewById(R.id.tv_ref_succ);

        final String sid = saveInfoLocally.get_store_id();
        if (sid.equals("3")){
            tv1.setText(R.string.ps_school);
        }
    }

    public void Go_to_Profile(View view) {
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentSuccess.this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }

    public void Go_to_BarcodeScanner(View view) {
        Intent in = new Intent(PaymentSuccess.this, BarcodeScannerActivity.class);
        in.putExtra("Type", "Product_Scan");
//        in.putExtra("activity", "PaytmSuccess");
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentSuccess.this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }
}
