package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Covid19 extends AppCompatActivity {

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");

    }

    public void onContinue(View view) {
        Intent in = new Intent(this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }
}
