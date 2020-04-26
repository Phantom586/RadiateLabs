package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{

    EditText et;
    Button btn;
    ProgressBar progressBar;
    Boolean save_user_data = false;
    private Boolean exit = false;

    public static final String TAG = "MainActivity";
    public static final String Otp = "com.example.noq_1.OTP";
    public static final String Save_User_Data = "com.example.noq_1.SAVE_USER_DATA";

    SaveInfoLocally save_data = new SaveInfoLocally(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.main_parent));

        et = findViewById(R.id.et_phone);
        btn = findViewById(R.id.btn_cont);
//        remember_me = findViewById(R.id.remember_me);

        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();

        progressBar = findViewById(R.id.spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

        final String num = save_data.getPhone();

        if(num.length() == 13) {
            Direct_Login(num);
        } else {
            final String nu = save_data.getPrevPhone();
            et.setText(nu.replace("+91", ""));
//          et.setText(nu.replace("+44", ""));
        }

    }

    public void Direct_Login(String num){

//        Intent in = new Intent(MainActivity.this, MyProfile.class);
        Intent in = new Intent(MainActivity.this, Covid19.class);
        in.putExtra("Phone", num);
        in.putExtra("activity", "MP");
        startActivity(in);

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

    public void onContinue(View v) throws ExecutionException, InterruptedException {

        final String phone = "+91"+et.getText().toString().trim();
//        final String phone = "+44"+et.getText().toString().trim();
//        final String phone = et.getText().toString().trim();

        et.setError(null);

        View focusView = null;

        if ( phone.length() < 4 ) {

            et.setError(getString(R.string.blank_phone));
            focusView = et;

        } else {

            if ( phone.length() == 13 && isNumber(phone)) {

                progressBar.setVisibility(View.VISIBLE);

//                if ( UserAlreadyExists(phone) ) {
                // Comparing Current no. provided by user with old no. present in Shared Pref.
//                if ( CompareCurrentWithPrev(phone) ) {
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            saveLoginDetails(phone);
//                            progressBar.setVisibility(View.GONE);
//
//                            Intent in = new Intent(MainActivity.this, MyProfile.class);
//                            in.putExtra("Phone", phone);
//                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(in);
//
//                        }
//                    }, 600);
//
//                } else {

                final String TAG = "MainActivity";
                Log.d(TAG, "Phone in MainActivity : "+phone);

                final String otp = generatePIN();
//                    final String otp = "9865";
                final String type = "send_msg";
                final String msg = otp + " is your NoQ Verification Code.Don't Share it with other people.The code is valid for only 5 minutes.";
                new BackgroundWorker(this).execute(type, msg, phone);

                Intent in = new Intent(MainActivity.this, OTPConfirmActivity.class);

                if(UserExistsInDB(phone)){
                    in.putExtra("next_activity", "MP");
                    Log.d(TAG, "User Exists in ServerDB");
                } else {
                    in.putExtra("next_activity", "UCA");
                    Log.d(TAG, "User Doesn't Exists in ServerDB");
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(View.GONE);

                        in.putExtra("Phone", phone);
                        in.putExtra(Otp, otp);
                        // Passing the boolean that indicates whether the user clicked on RememberMe Box or not.
//                            in.putExtra(Save_User_Data, save_user_data);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);

                    }
                }, 1000);

//                }

            } else {

                et.setError(getString(R.string.invalid_phone_number));
                focusView = et;

            }
        }

    }

    // Return True if the Current no. provided by user is same as Previous no.(if Present, otherwise
    // returns False) stored in SharedPref, else False.
//    private boolean CompareCurrentWithPrev(String phone){
//
//        final String prev_num = save_data.getPrevPhone();
//        if(prev_num.length() == 13) {
//            if (prev_num.equals(phone)) {
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//
//    }

    private boolean UserExistsInDB(String Phone) throws ExecutionException, InterruptedException {

//        Boolean data = save_data.UserExists(Phone);
        final String type = "verify_user";
        String res = new BackgroundWorker(this).execute(type, Phone).get();
        Boolean b = Boolean.parseBoolean(res.trim());

        String TAG = "MainActivity";
        Log.d(TAG, "in UserAlreadyExists : result = " + res.length());

        if( b ){
            return true;
        } else {
            return false;
        }
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

    @Override
    public void onBackPressed() {
        if (exit) {
            moveTaskToBack(true);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

}
