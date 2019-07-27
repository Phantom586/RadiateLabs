package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
        setupUI(findViewById(R.id.main_parent));

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

    public String send_otp() {

        return "9865";

    }

    public void verify_otp(String check_otp) {

        Intent intent = getIntent();
//        final String checkOTP = intent.getStringExtra(MainActivity.Otp);
        final String phone = intent.getStringExtra(MainActivity.Phone);
        final Boolean save_user_details = intent.getBooleanExtra(MainActivity.Save_User_Data, true);

        View focusView;
        String checkOTP;

        checkOTP = send_otp();

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

    public void OnContinue(View v) {

        otp.setError(null);

        progressBar.setVisibility(View.VISIBLE);

        View focusView;
        String checkOTP;

        final String check_otp = otp.getText().toString().trim();


        if (TextUtils.isEmpty(check_otp)) {

            progressBar.setVisibility(View.INVISIBLE);
            otp.setError(getString(R.string.blank_otp));
            focusView = otp;

        } else {

            verify_otp(check_otp);

        }

    }

    public void OnResend(View v){

        final String check_otp = otp.getText().toString();

        send_otp();
        verify_otp(check_otp);

    }

    public void OnRe_Enter(View v) {

        Intent in = new Intent(OTPConfirmActivity.this, MainActivity.class);
        startActivity(in);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(OTPConfirmActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
