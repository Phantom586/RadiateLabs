package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.AwsBackgroundWorker;
import com.younoq.noqfuelstation.models.Logger;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class Covid19 extends AppCompatActivity {

    private final String TAG = "Covid19 Activity";
    private SaveInfoLocally saveInfoLocally;
    private String phone, activity, logDate;
    private Boolean exit_flag = false;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);

        saveInfoLocally = new SaveInfoLocally(this);
        logger = new Logger(this);

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        activity = in.getStringExtra("activity");

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","Values in getIntent ->  phone : "+phone+"\n");

    }

    public void onContinue(View view) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onContinue()","onContinue Method Clicked\n");

        Intent in;
        // Retrieving the City from SharedPreferences if Present.
        final String city = saveInfoLocally.getStoreCity();
        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","Fetching the City from the SharedPreferences if present : {"+city+"}\n");
        if(!city.equals("")){
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "onCreate()","NextActivity -> PetrolPumps Activity\n");
            in = new Intent(this, PetrolPumpsNoq.class);
        } else {
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "onCreate()","NextActivity -> CitySelect Activity\n");
            in = new Intent(this, CitySelect.class);
        }
        in.putExtra("Phone", phone);
        // Checking the Intent is Coming from which Activity.
        if(activity.equals("MP")){
            in.putExtra("isDirectLogin", true);
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "onContinue()","is DirectLogin : true \n");
        }
        else if(activity.equals("UCA")){
            in.putExtra("isDirectLogin", false);
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "onContinue()","is DirectLogin : false \n");
        }

        // Uploading the Logs to the Server
        try {

            final String res = new AwsBackgroundWorker(this).execute("upload_logs").get();
            Log.d(TAG, "Logs Uploaded to Server : "+res);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        startActivity(in);

    }

    @Override
    public void onBackPressed() {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onBackPressed()","onBackPressed() Func. called.\n");

        if (exit_flag) {
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "onBackPressed()","User Exited the app.\n");
            moveTaskToBack(true);
        } else {
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "onBackPressed()","User pressed the back button.\n");
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit_flag = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit_flag = false;
                }
            }, 3 * 1000);

        }

    }
}