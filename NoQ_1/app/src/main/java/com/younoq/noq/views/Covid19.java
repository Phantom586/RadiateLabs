package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.younoq.noq.R;
import com.younoq.noq.models.SaveInfoLocally;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class Covid19 extends AppCompatActivity {

    private String phone, activity;
    private Boolean exit_flag = false;
    private SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);

        saveInfoLocally = new SaveInfoLocally(this);

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        activity = in.getStringExtra("activity");

    }

    public void onContinue(View view) {

        Intent in;
        // Retrieving the City from SharedPreferences if Present.
        final String city = saveInfoLocally.getStoreCity();
        if(!city.equals("")){
            in = new Intent(this, MyProfile.class);
        } else {
            in = new Intent(this, CitySelect.class);
        }
        in.putExtra("Phone", phone);
        // Checking the Intent is Coming from which Activity.
        if(activity.equals("MP"))
            in.putExtra("isDirectLogin", true);
        else if(activity.equals("UCA"))
            in.putExtra("isDirectLogin", false);

        // Setting the FirstLoginStatus to false
        saveInfoLocally.setFirstLoginStatus(false);

        startActivity(in);
    }

    @Override
    public void onBackPressed() {

        if (exit_flag) {
            moveTaskToBack(true);
        } else {
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
