package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ReferralUnsuccessfulActivity extends AppCompatActivity {

    AnimationDrawable success_disp;
    ImageView img;
    Button bt_retry, bt_cont;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_unsuccessful);

        img = findViewById(R.id.un_image);
        img.setBackgroundResource(R.drawable.animation);

        bt_cont = findViewById(R.id.btn_cont);
        bt_retry = findViewById(R.id.btn_try_again);

        success_disp = (AnimationDrawable)img.getBackground();

        success_disp.start();
    }

    public void onRetry(View v) {

        Intent in = new Intent(ReferralUnsuccessfulActivity.this, UserCredentialsActivity.class);
        startActivity(in);

    }

    public void onContinue(View v) {

        Intent in  = new Intent(ReferralUnsuccessfulActivity.this, BarcodeScannerActivity.class);
        startActivity(in);

    }
}
