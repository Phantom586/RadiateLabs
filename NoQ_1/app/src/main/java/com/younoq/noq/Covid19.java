package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class Covid19 extends AppCompatActivity {

    private String phone, activity;
    private Boolean exit_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        activity = in.getStringExtra("activity");

    }

    public void onContinue(View view) {
        Intent in = new Intent(this, MyProfile.class);
        in.putExtra("Phone", phone);
        // Checking the Intent is Coming from which Activity.
        if(activity.equals("MP"))
            in.putExtra("isDirectLogin", true);
        else if(activity.equals("UCA"))
            in.putExtra("isDirectLogin", false);
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
