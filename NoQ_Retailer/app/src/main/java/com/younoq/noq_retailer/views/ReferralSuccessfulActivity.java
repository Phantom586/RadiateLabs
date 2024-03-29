package com.younoq.noq_retailer.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.younoq.noq_retailer.R;
import com.younoq.noq_retailer.models.Logger;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ReferralSuccessfulActivity extends AppCompatActivity {

    /* AnimationDrawable success_disp; */
    /*ImageView img; */
    Button btn;

    public static final String Type = "com.example.noq_1.TYPE";

    public static final String NAME = "com.example.noq_1.NAME";
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_successful);
        /* img = findViewById(R.id.image);
        img.setBackgroundResource(R.drawable.animation);
        success_disp = (AnimationDrawable)img.getBackground(); */
        btn = findViewById(R.id.btn_succ);
        logger = new Logger(this);

        /* success_disp.start(); */

        Intent in = getIntent();
        final String name = in.getStringExtra(UserCredentialsActivity.Name);
        final String phone = in.getStringExtra("Phone");
        final String TAG = "ReferralSuccessful";
        Log.d(TAG, "Phone No in ReferralSuccessfulActivity : "+phone);
        final String email = in.getStringExtra(UserCredentialsActivity.Email);

        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","Values in getIntent -> name : "+name+", phone : "+phone+", email : "+email+"\n");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Intent in = new Intent(ReferralSuccessfulActivity.this, NoqStores.class); */
                Intent in = new Intent(ReferralSuccessfulActivity.this, Covid19.class);
                in.putExtra("Phone", phone);
                in.putExtra("activity", "UCA");
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "onCreate()","Values in Intent to Covid19 Activity -> phone : "+phone+"\n");
                startActivity(in);

            }
        });
    }
}
