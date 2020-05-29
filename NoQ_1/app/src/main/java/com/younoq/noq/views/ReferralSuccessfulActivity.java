package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.younoq.noq.R;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ReferralSuccessfulActivity extends AppCompatActivity {

//    AnimationDrawable success_disp;
//    ImageView img;
    Button btn;

    public static final String Type = "com.example.noq_1.TYPE";

    public static final String NAME = "com.example.noq_1.NAME";
//    private static final String PHONE = "com.example.noq_1.PHONE";
//    private static final String EMAIL = "com.example.noq_1.EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_successful);

//        img = findViewById(R.id.image);
//        img.setBackgroundResource(R.drawable.animation);
//        success_disp = (AnimationDrawable)img.getBackground();
        btn = findViewById(R.id.btn_succ);

//        success_disp.start();

        Intent in = getIntent();
        final String name = in.getStringExtra(UserCredentialsActivity.Name);
        final String phone = in.getStringExtra("Phone");
        final String TAG = "ReferralSuccessful";
        Log.d(TAG, "Phone No in ReferralSuccessfulActivity : "+phone);
        final String email = in.getStringExtra(UserCredentialsActivity.Email);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent in = new Intent(ReferralSuccessfulActivity.this, NoqStores.class);
                Intent in = new Intent(ReferralSuccessfulActivity.this, Covid19.class);
                in.putExtra("Phone", phone);
                in.putExtra("activity", "UCA");
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);

            }
        });
    }
}