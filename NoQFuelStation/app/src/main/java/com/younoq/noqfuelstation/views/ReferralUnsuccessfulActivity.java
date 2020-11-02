package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class ReferralUnsuccessfulActivity extends AppCompatActivity {

    private Button btn_cont;
    private Logger logger;
    private String phone, bonus_amt;
    private static final String TAG = "ReferralUnsuccessfulActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_unsuccessful);

        btn_cont = findViewById(R.id.btn_cont);
        logger = new Logger(this);

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","onCreate() Func. called\n");

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        bonus_amt = in.getStringExtra("bonus_amt");

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","Values in getIntent -> phone : "+phone+"\n");

    }

    public void onContinue(View view) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onContinue()","onContinue() Func. called.\n");

        Intent in  = new Intent(ReferralUnsuccessfulActivity.this, Covid19.class);
        in.putExtra("Phone", phone);
        in.putExtra("activity", "UCA");
        in.putExtra("bonus_amt", bonus_amt);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","Values in Intent to Covid19 Activity -> phone : "+phone+", bonus_amt : "+bonus_amt+"\n");
        startActivity(in);

    }
}