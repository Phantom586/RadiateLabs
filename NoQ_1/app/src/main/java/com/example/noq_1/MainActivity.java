package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText et;
    Button btn;
    ProgressBar pb;

    public static final String Phone = "com.example.noq.PHONE";
    public static final String Otp = "com.example.noq.OTP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        et = findViewById(R.id.et_phone);
        btn = findViewById(R.id.btn_cont);

        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();

        pb = findViewById(R.id.pb);

    }

    public String send_otp() {

        return "9865";
    }

    public void onContinue(View v) {

        final String OTP;
        OTP = send_otp();

        final String phone = et.getText().toString();
        final SharedPreferences sharedPreferences = getSharedPreferences("Content_main", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Phone", phone);
        editor.apply();

        if ( phone.equals("") ) {

            Toast.makeText(this, "Please Enter a Mobile Number!", Toast.LENGTH_SHORT).show();

        } else {

            if ( phone.length() == 10 || phone.length() == 12) {

                pb.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pb.setVisibility(View.INVISIBLE);

                        Intent in = new Intent(MainActivity.this, OTPConfirmActivity.class);
                        in.putExtra(Phone, phone);
                        in.putExtra(Otp, OTP);
                        startActivity(in);

                    }
                }, 1000);

            } else {

                Toast.makeText(this, "Please Enter a Valid Phone Number!!", Toast.LENGTH_SHORT).show();
                et.setText("");

            }
        }

    }
}
