package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.younoq.noq.R;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ReferralUnsuccessfulActivity extends AppCompatActivity {

//    AnimationDrawable success_disp;
//    ImageView img;
    Button bt_retry, bt_cont;
    public String phone;
    TextView tv;

    public static final String Type = "com.example.noq_1.TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_unsuccessful);

//        img = findViewById(R.id.un_image);
//        img.setBackgroundResource(R.drawable.animation);

        bt_cont = findViewById(R.id.btn_cont);
//        bt_retry = findViewById(R.id.btn_try_again);

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");

//        success_disp = (AnimationDrawable)img.getBackground();
//
//        success_disp.start();
    }

//    public void onRetry(View v) {
//
//        Intent in = new Intent(ReferralUnsuccessfulActivity.this, UserCredentialsActivity.class);
//        startActivity(in);
//
//    }

    public void onContinue(View v) {

//        Intent in  = new Intent(ReferralUnsuccessfulActivity.this, NoqStores.class);
        Intent in  = new Intent(ReferralUnsuccessfulActivity.this, Covid19.class);
        in.putExtra("Phone", phone);
        in.putExtra("activity", "UCA");
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

    }
}