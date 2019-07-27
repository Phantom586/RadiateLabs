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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

public class MainActivity extends AppCompatActivity{

    EditText et;
    Button btn;
    ProgressBar progressBar;
    CheckBox remember_me;
    Boolean save_user_data = false;

    public static final String Phone = "com.example.noq_1.PHONE";
//    public static final String Otp = "com.example.noq_1.OTP";
    public static final String Save_User_Data = "com.example.noq_1.SAVE_USER_DATA";

    SaveInfoLocally save_data = new SaveInfoLocally(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.main_parent));


        et = findViewById(R.id.et_phone);
        btn = findViewById(R.id.btn_cont);
        remember_me = findViewById(R.id.remember_me);

        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();

        progressBar = findViewById(R.id.spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

    }

//    public String send_otp() {
//
//        return "9865";
//    }

    public void onContinue(View v) {

        final String phone = et.getText().toString().trim();
//        final String OTP;
//        OTP = send_otp();

        et.setError(null);

        View focusView = null;

        if ( TextUtils.isEmpty(phone) ) {

            et.setError(getString(R.string.blank_phone));
            focusView = et;

        } else {

            if ( phone.length() == 10 || phone.length() == 12) {

                progressBar.setVisibility(View.VISIBLE);

                if ( UserAlreadyExists(phone) ) {

//                    et.setError(getString(R.string.already_exists));
//                    focusView = et;
//                    flag = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);

                            Intent in = new Intent(MainActivity.this, MyProfile.class);
                            in.putExtra(Phone, phone);
                            startActivity(in);

                        }
                    }, 600);

                } else {

                    if ( remember_me.isChecked() ) {

                        saveLoginDetails(phone);
                        save_user_data = true;

                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);

                            Intent in = new Intent(MainActivity.this, OTPConfirmActivity.class);
                            in.putExtra(Phone, phone);
//                            in.putExtra(Otp, OTP);
                            // Passing the boolean that indicates whether the user clicked on RememberMe Box or not.
                            in.putExtra(Save_User_Data, save_user_data);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(in);

                        }
                    }, 1000);

                }

            } else {

                et.setError(getString(R.string.invalid_phone_number));
                focusView = et;

            }
        }

    }
    
    private boolean UserAlreadyExists(String Phone) {

        boolean data = save_data.UserExists(Phone);
        return data;
        
    }

    private void saveLoginDetails(String Phone) {

        save_data.saveLoginDetails(Phone);

    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this);
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
