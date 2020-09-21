package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class ReferralSuccessfulActivity extends AppCompatActivity {

    private Button btn;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_successful);

        btn = findViewById(R.id.btn_succ);
        logger = new Logger(this);

        Intent in = getIntent();
        final String name = in.getStringExtra(UserCredentialsActivity.Name);
        final String phone = in.getStringExtra("Phone");
        final String TAG = "ReferralSuccessful";
        Log.d(TAG, "Phone No in ReferralSuccessfulActivity : "+phone);
        final String email = in.getStringExtra(UserCredentialsActivity.Email);

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","Values in getIntent -> name : "+name+", phone : "+phone+", email : "+email+"\n");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent in = new Intent(ReferralSuccessfulActivity.this, NoqStores.class);
                Intent in = new Intent(ReferralSuccessfulActivity.this, Covid19.class);
                in.putExtra("Phone", phone);
                in.putExtra("activity", "UCA");
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // Storing Logs in the Logger.
                logger.writeLog(TAG, "onCreate()","Values in Intent to Covid19 Activity -> phone : "+phone+"\n");
                startActivity(in);

            }
        });

    }
}