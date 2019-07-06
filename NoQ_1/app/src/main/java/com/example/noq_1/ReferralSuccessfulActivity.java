package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ReferralSuccessfulActivity extends AppCompatActivity {

    AnimationDrawable success_disp;
    ImageView img;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_successful);

        img = findViewById(R.id.image);
        img.setBackgroundResource(R.drawable.animation);
        success_disp = (AnimationDrawable)img.getBackground();
        btn = findViewById(R.id.btn_succ);

        success_disp.start();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(ReferralSuccessfulActivity.this, BarcodeScannerActivity.class);
                startActivity(in);
                finish();

            }
        });
    }
}
