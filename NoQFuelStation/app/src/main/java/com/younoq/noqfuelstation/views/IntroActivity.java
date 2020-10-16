package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;

import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.Logger;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class IntroActivity extends AppCompatActivity {

    private SaveInfoLocally saveInfoLocally;
    final String TAG = "IntroActivity";
    private Button getStarted;
    private Animation btnAnim;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveInfoLocally = new SaveInfoLocally(this);
        logger = new Logger(this);

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "onCreate()", "User opened the App\n");

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","Checking isFirstLogin Status : "+ saveInfoLocally.isFirstLogin() +", and hasFinishedIntro : "+ saveInfoLocally.hasFinishedIntro() +"\n");

        if(!saveInfoLocally.isFirstLogin() || saveInfoLocally.hasFinishedIntro()) {

            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "onCreate()", "Not First Login, hence Routing to MainActivity \n");

            Intent in = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(in);
            finish();

        }

        Log.d("IntroActivity", "not Found");

        setContentView(R.layout.activity_intro);

        getStarted = findViewById(R.id.ia_btn_get_started);
        btnAnim = AnimationUtils.loadAnimation(this, R.anim.btn_animation);
        getStarted.setAnimation(btnAnim);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Setting the Flag as True.
                saveInfoLocally.setHasFinishedIntro();

                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
                finish();

            }
        });

    }

}