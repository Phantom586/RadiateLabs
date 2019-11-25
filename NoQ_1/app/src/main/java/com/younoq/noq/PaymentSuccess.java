package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class PaymentSuccess extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;
    TextView tv1;
    String ref_bal_used;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        saveInfoLocally = new SaveInfoLocally(this);
        tv1 = findViewById(R.id.tv_ref_succ);

        Intent in = getIntent();
        ref_bal_used = in.getStringExtra("referral_balance_used");
        // Pushing Updates to DB/
        pushUpdatesToDatabase();

        final String sid = saveInfoLocally.get_store_id();
        if (sid.equals("3")) {
            tv1.setText(R.string.ps_school);
        }
    }

    public void pushUpdatesToDatabase() {

        final String phone = saveInfoLocally.getPhone();
        try {
            // Pushing the Referral_Amount_Used to Users_Table.
            final String type = "update_referral_used";
            final String res = new AwsBackgroundWorker(this).execute(type, phone, ref_bal_used).get();
            // Removing the Extra Space from the Fetched Result.
            final String check = res.trim();
            if(!check.equals("FALSE")) {
                // Now, when the Referral_Amount_Used is updated, now we can calculate the Referral_Amount_Balance.
                final String type1 = "update_referral_balance";
                final String res1 = new AwsBackgroundWorker(this).execute(type1, phone).get();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
