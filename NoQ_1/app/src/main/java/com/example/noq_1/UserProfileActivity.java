package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4;
    Button btn;

    myDBClass helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tv1 = findViewById(R.id.text_v1);
        tv2 = findViewById(R.id.text_v2);
        tv3 = findViewById(R.id.text_v3);
        tv4 = findViewById(R.id.text_v4);

        btn = findViewById(R.id.btn_scan_barcode);

        helper = new myDBClass(this);

        Intent in = getIntent();
        String phone = in.getStringExtra(MainActivity.Phone);

        String buffer;
        buffer = helper.queryData(phone);

        String[] data = buffer.split(" ", 6);

        tv1.setText(data[1]);
        tv2.setText(data[2]);
        tv3.setText(data[3]);
        tv4.setText(data[4]);
    }

    public void ScanBarcode(View v) {

        Intent in = new Intent(UserProfileActivity.this, BarcodeScannerActivity.class);
        startActivity(in);

    }
}
