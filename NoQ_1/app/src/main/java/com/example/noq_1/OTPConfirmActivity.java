package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OTPConfirmActivity extends AppCompatActivity {

    EditText otp;

    Button cont, resend, re_enter;
    public static final String Phone = "com.example.noq.PHONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpconfirm);

        otp = findViewById(R.id.otp_enter);

        otp.setFocusable(true);
        otp.setFocusableInTouchMode(true);
        otp.requestFocus();

        cont = findViewById(R.id.btn_continue);
        resend = findViewById(R.id.btn_resend_otp);
        re_enter = findViewById(R.id.btn_re_enter_no);


    }

    public void send_otp() {

    }

    public void OnContinue(View v) {

        Intent intent = getIntent();
        String checkOTP = intent.getStringExtra(MainActivity.Otp);
        String phone = intent.getStringExtra(MainActivity.Phone);

        final String check_otp = otp.getText().toString();


        if ( check_otp.equals("") ) {

            Toast.makeText(this, "Please Enter the OTP!", Toast.LENGTH_SHORT).show();

        } else {

            if ( check_otp.equals(checkOTP) ) {

                Intent in = new Intent(OTPConfirmActivity.this, UserCredentialsActivity.class);
                in.putExtra(Phone, phone);
                startActivity(in);

            } else {

                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                otp.setText("");


            }
        }

    }

    public void OnResend(View v){

        send_otp();

    }

    public void OnRe_Enter(View v) {

        Intent in = new Intent(OTPConfirmActivity.this, MainActivity.class);
        startActivity(in);
    }
}
