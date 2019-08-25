package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ProductDetails extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
    ImageView im, im1, im2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        tv1 = findViewById(R.id.pd_tv_1);
        tv2 = findViewById(R.id.pd_tv_2);
        tv3 = findViewById(R.id.pd_tv_3);
        tv4 = findViewById(R.id.pd_tv_4);
        tv5 = findViewById(R.id.pd_tv_5);
        tv6 = findViewById(R.id.pd_tv_6);
        tv7 = findViewById(R.id.pd_tv_7);
        im = findViewById(R.id.pd_im);
        im1 = findViewById(R.id.pd_im_1);
        im2 = findViewById(R.id.pd_im_2);

        Intent in = getIntent();
        String res = in.getStringExtra(BarcodeScannerActivity.RESULT);
        String img_name = in.getStringExtra(BarcodeScannerActivity.BARCODE);
        img_name += ".png";

        Glide.with(this).load("ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/images/"+img_name).into(im);

        if (res.equals("")) {

            String data[] = res.split("-", 7);
            tv1.setText(data[0]);
            tv2.setText(data[1]);
            tv3.setText(data[2]);
            tv4.setText(data[3]);
            tv5.setText(data[4]);
            tv6.setText(data[5]);
        }

    }
}
