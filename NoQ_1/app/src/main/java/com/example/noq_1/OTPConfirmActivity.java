package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

public class OTPConfirmActivity extends AppCompatActivity {

    EditText otp;
    ProgressBar progressBar;

    Button cont, resend, re_enter;
    public static final String Phone = "com.example.noq.PHONE";
    public static final String Save_User_Data = "com.example.noq1.SAVE_USER_DATA";

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

        progressBar = findViewById(R.id.spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

        progressBar.setVisibility(View.INVISIBLE);


    }

    public void send_otp() {

    }

    public void OnContinue(View v) {

        otp.setError(null);

        progressBar.setVisibility(View.VISIBLE);

        View focusView;

        Intent intent = getIntent();
        final String checkOTP = intent.getStringExtra(MainActivity.Otp);
        final String phone = intent.getStringExtra(MainActivity.Phone);
        final Boolean save_user_details = intent.getBooleanExtra(MainActivity.Save_User_Data, true);

        final String check_otp = otp.getText().toString();


        if (TextUtils.isEmpty(check_otp)) {

            progressBar.setVisibility(View.INVISIBLE);
            otp.setError(getString(R.string.blank_otp));
            focusView = otp;

        } else {

            if ( check_otp.equals(checkOTP) ) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(View.GONE);
                        Intent in = new Intent(OTPConfirmActivity.this, UserCredentialsActivity.class);
                        in.putExtra(Phone, phone);
                        in.putExtra(Save_User_Data, save_user_details);
                        startActivity(in);

                    }
                }, 1000);

            } else {

                progressBar.setVisibility(View.INVISIBLE);
                otp.setError(getString(R.string.invalid_otp));
                focusView = otp;

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
