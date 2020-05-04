package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PaymentFailed extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;
    DBHelper dbHelper;

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

        dbHelper = new DBHelper(this);
        new MaterialAlertDialogBuilder(this)
                .setTitle("Do you want to Exit the In_Store Shopping Method ?")
                .setMessage(R.string.bs_exit_in_store_msg)
                .setPositiveButton(R.string.bs_exit_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(BarcodeScannerActivity.this, "Exit Store", Toast.LENGTH_SHORT).show();
                        dbHelper.Delete_all_rows();
//                        final String phone = saveInfoLocally.getPhone();
                        Intent in = new Intent(PaymentFailed.this, ChooseShopType.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        in.putExtra("Phone", phone);
                        startActivity(in);
                    }
                })
                .setNegativeButton(R.string.bs_exit_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(BarcodeScannerActivity.this, "Don't Exit", Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(PaymentFailed.this, CartActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        in.putExtra("Phone", phone);
                        startActivity(in);
                    }
                })
                .show();
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentFailed.this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentFailed.this, CartActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        in.putExtra("Phone", phone);
        startActivity(in);
    }
}
