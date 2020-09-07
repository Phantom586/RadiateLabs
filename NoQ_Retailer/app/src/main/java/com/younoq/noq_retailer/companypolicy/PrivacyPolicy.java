package com.younoq.noq_retailer.companypolicy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.younoq.noq_retailer.R;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
    }

    public void go_back(View view) {
        super.onBackPressed();
    }
}
