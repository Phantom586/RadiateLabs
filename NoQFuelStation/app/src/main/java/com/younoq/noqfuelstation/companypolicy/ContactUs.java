package com.younoq.noqfuelstation.companypolicy;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noqfuelstation.R;
import android.os.Bundle;
import android.view.View;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
    }

    public void go_back(View view) { super.onBackPressed(); }
}