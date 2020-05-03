package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

import java.util.concurrent.ExecutionException;

public class OTPConfirmActivity extends AppCompatActivity {

    EditText otp;
    ProgressBar progressBar;
    TextView p_no, tv_change_mob_no;

    Button cont, resend;
    ImageView re_enter;
    String phone = "";
    static String checkOTP = "";
    static String otp_pin = "";
    static String next_activity = "";
    SaveInfoLocally save_data;
    final String TAG = "OTPConfirmActivity";

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
//        re_enter = findViewById(R.id.btn_re_enter_no);
        tv_change_mob_no = findViewById(R.id.otp_change_mob_no);
        p_no = findViewById(R.id.op_tv_2);

        progressBar = findViewById(R.id.spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

        progressBar.setVisibility(View.INVISIBLE);
        save_data = new SaveInfoLocally(this);

        Intent intent = getIntent();
        phone = intent.getStringExtra("Phone");
        final String user_no = " " + phone;
        // Putting an Underline under the Phone No.
        SpannableString no = new SpannableString(user_no);
        no.setSpan(new UnderlineSpan(), 0, user_no.length(), 0);
        p_no.setText(no);
        final String change_mob_no = "Click Here!";
        // Putting an Underline under the Click Here!.
        SpannableString click_here = new SpannableString(change_mob_no);
        click_here.setSpan(new UnderlineSpan(), 0, change_mob_no.length(), 0);
        tv_change_mob_no.setText(click_here);

        final String TAG = "OTPConfirmActivity";
        Log.d(TAG, "Phone No in OTPConfirmActivity : "+phone);
        checkOTP = intent.getStringExtra(MainActivity.Otp);
        next_activity = intent.getStringExtra("next_activity");

    }

    public String generatePIN()
    {

        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;

        return String.valueOf(randomPIN);

    }

    private boolean isNumber(String phone) {

        try {
            Double.parseDouble(phone);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    public void verify_otp(String checkOTP) {

//        final Boolean save_user_details = intent.getBooleanExtra(MainActivity.Save_User_Data, true);

        View focusView;
        Log.d(TAG, "OTP in verify_otp : "+checkOTP);

        final String check_otp = otp.getText().toString();

        if ( check_otp.equals(checkOTP) ) {

            Intent in;

            if (next_activity.equals("MP")){

                // Means the User Exists in DP, so change the logout_flag to False
                final String type = "set_logout_flag";
                try {
                    final String res = new BackgroundWorker(this).execute(type, phone, "False").get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                in = new Intent(OTPConfirmActivity.this, Covid19.class);
                saveLoginDetails(phone);

            } else {

                in = new Intent(OTPConfirmActivity.this, UserCredentialsActivity.class);

            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    progressBar.setVisibility(View.GONE);
                    in.putExtra("Phone", phone);
                    in.putExtra("activity", "MP");
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    in.putExtra(Save_User_Data, save_user_details);
                    startActivity(in);

                }
            }, 1000);

        } else {

            progressBar.setVisibility(View.INVISIBLE);
            otp.setError(getString(R.string.invalid_otp));
            otp.setText("");
            focusView = otp;

        }

    }

    public void OnContinue(View v) {

        otp.setError(null);

        progressBar.setVisibility(View.VISIBLE);

        View focusView;

        final String check_otp = otp.getText().toString().trim();
        Log.d(TAG, "Otp length : "+check_otp.length());

//        if (TextUtils.isEmpty(check_otp)) {
        if ( check_otp.length() == 0 || !isNumber(check_otp)) {

            progressBar.setVisibility(View.INVISIBLE);
            otp.setError(getString(R.string.blank_otp));
            focusView = otp;

        } else {

            Log.d(TAG, "OTP in OnContinue : "+checkOTP);
            verify_otp(checkOTP);

        }

    }

    public void OnResend(View v) throws ExecutionException, InterruptedException {

        otp_pin = generatePIN();
//        otp_pin = "9865";
        otp.setText("");
        otp.setError(null);

        Log.d(TAG, "Phone No. in OnResend : "+phone);

        progressBar.setVisibility(View.VISIBLE);
        final String type = "send_msg";
        final String msg = otp_pin + " is your NoQ Verification Code.Don't Share it with other people.The code is valid for only 5 minutes.";
        String result = new BackgroundWorker(this).execute(type, msg, phone).get();

        checkOTP = otp_pin;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 500);
        Log.d(TAG, "OTP in OnResend : "+checkOTP);

    }


    public void goToLanding(View view) {

        Intent in = new Intent(OTPConfirmActivity.this, MainActivity.class);
        startActivity(in);

    }

    private void saveLoginDetails(String Phone) {

        save_data.removeNumber();
        save_data.saveLoginDetails(Phone);

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
