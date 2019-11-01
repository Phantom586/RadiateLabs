package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

import java.util.concurrent.ExecutionException;

import static android.view.View.GONE;

public class UserCredentialsActivity extends AppCompatActivity {

    Button regbtn;
    EditText et, et1, et2;
    TextView tv, tv1;
    ProgressBar progressBar;
    String User_number;

    String verified = "";

    SaveInfoLocally save_data = new SaveInfoLocally(this);

    public static final String Name = "com.example.noq.NAME";
    public static final String Email = "com.example.noq.EMAIL";
    public static final String Phone = "com.example.noq.PHONE";
    public static String Pno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credentials);
        setupUI(findViewById(R.id.main_parent));

        final String otp_success = "OTP Verified Successfully";

        regbtn = findViewById(R.id.reg_btn);

        et = findViewById(R.id.et);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);

        progressBar = findViewById(R.id.spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

        progressBar.setVisibility(View.INVISIBLE);

        tv = findViewById(R.id.otp_success);
        tv1 = findViewById(R.id.tv_disp);

        tv.setText(otp_success);
        tv.setVisibility(View.VISIBLE);

//        final Boolean save_user_details;

        Intent in = getIntent();
        User_number = in.getStringExtra(OTPConfirmActivity.Phone);
//        save_user_details = in.getBooleanExtra(OTPConfirmActivity.Save_User_Data, true);

        tv1.setText(User_number);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                tv.setVisibility(View.INVISIBLE);
            }
        }, 2000);

    }

    public void Register(View v) throws ExecutionException, InterruptedException {

        final String f_name = et.getText().toString().trim();
        final String email = et2.getText().toString().trim();
        Pno = et1.getText().toString().trim();

        et.setError(null);
        et1.setError(null);

        View focusView = null;

        Boolean flag = true;
        Boolean flag_phone = false;
        long id1;

        if (TextUtils.isEmpty(f_name)) {

            et.setError(getString(R.string.required));
            focusView = et;

        } else {

            progressBar.setVisibility(View.VISIBLE);
            final Intent intent;

            if ( !TextUtils.isEmpty(Pno) ) {

                Pno = "+91"+Pno;
//                Pno = "+44"+Pno;

                if (Pno.length() == 13 ) {

                    flag_phone = true;

                    final String type1 = "verify_user";

                    verified = new BackgroundWorker(this).execute(type1, Pno).get();

                    final String TAG = "UserCredentialsActivity";
                    Boolean b = Boolean.parseBoolean(verified.trim());
                    Log.d(TAG, "before_Verification : result = " +verified.length());

                    if ( b ) {

                        tv.setVisibility(View.VISIBLE);

                        intent = new Intent(UserCredentialsActivity.this, ReferralSuccessfulActivity.class);

                    } else {

                        intent = new Intent(UserCredentialsActivity.this, ReferralUnsuccessfulActivity.class);

                    }

                } else {

                    et1.setError(getString(R.string.invalid_phone_number));
                    focusView = et1;
                    intent = new Intent();
                    flag = false;

                }

            } else {

                intent = new Intent(UserCredentialsActivity.this, BarcodeScannerActivity.class);

            }

            if ( flag ) {

                final String type2 = "store_user";
                String verify = new BackgroundWorker(this).execute(type2, f_name, email, User_number, Pno).get();

                if ( flag_phone ) {
                    final String type = "update_ref";
                    String update = new BackgroundWorker(this).execute(type, User_number, Pno).get();
                }

                saveLoginDetails(User_number);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(GONE);
                        tv.setVisibility(GONE);

                        Toast.makeText(UserCredentialsActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                        intent.putExtra(Name, f_name);
                        intent.putExtra(Email, email);
                        intent.putExtra(Phone, Pno);
                        intent.putExtra("activity", "UCA");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        final String TAG = "UserCredentialsActivity";
                        Log.d(TAG, "After_Verification : result = " + verified);
                        startActivity(intent);

                    }
                }, 600);

            } else {

                progressBar.setVisibility(View.INVISIBLE);

            }

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
                    hideSoftKeyboard(UserCredentialsActivity.this);
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
