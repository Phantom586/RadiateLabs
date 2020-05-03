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
    EditText full_name, referral_no, email;
    TextView tv, tv1;
    ProgressBar progressBar;
    String User_number;

    String verified = "";

    SaveInfoLocally save_data = new SaveInfoLocally(this);
    final String TAG = "UserCredentialsActivity";

    public static final String Name = "com.example.noq.NAME";
    public static final String Email = "com.example.noq.EMAIL";
    public static String Pno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credentials);
        setupUI(findViewById(R.id.main_parent));

        final String otp_success = "OTP Verified Successfully";

        regbtn = findViewById(R.id.reg_btn);

        full_name = findViewById(R.id.uca_full_name);
        full_name.setFocusable(true);
        full_name.setFocusableInTouchMode(true);
        full_name.requestFocus();

        referral_no = findViewById(R.id.uca_ref_no);
        email = findViewById(R.id.uca_email);

        progressBar = findViewById(R.id.spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

        progressBar.setVisibility(View.INVISIBLE);

//        tv = findViewById(R.id.otp_success);
        tv1 = findViewById(R.id.tv_disp);

//        tv.setText(otp_success);
//        tv.setVisibility(View.VISIBLE);

//        final Boolean save_user_details;

        Intent in = getIntent();
        User_number = in.getStringExtra("Phone");
        final String TAG = "UserCredentialsActivity";
        Log.d(TAG, "Phone No in UCA : "+User_number);
//        save_user_details = in.getBooleanExtra(OTPConfirmActivity.Save_User_Data, true);

//        tv1.setText(User_number);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                tv.setVisibility(View.INVISIBLE);
//            }
//        }, 3000);

    }

    public void Register(View v) throws ExecutionException, InterruptedException {

        final String f_name = full_name.getText().toString().trim();
        final String user_email = email.getText().toString().trim();
        Pno = referral_no.getText().toString().trim();

        full_name.setError(null);
        referral_no.setError(null);

        View focusView = null;

        Boolean flag = true;
        Boolean flag_phone = false;
        long id1;

        if (TextUtils.isEmpty(f_name)) {

            full_name.setError(getString(R.string.required));
            focusView = full_name;

        } else {

            progressBar.setVisibility(View.VISIBLE);
            final Intent intent;

            if ( !TextUtils.isEmpty(Pno) ) {

                Pno = "+91"+Pno;
//                Pno = "+44"+Pno;
//
                if (Pno.length() == 13 ) {

                    final String type1 = "verify_user";

                    verified = new BackgroundWorker(this).execute(type1, Pno).get();

                    Boolean b = Boolean.parseBoolean(verified.trim());
                    Log.d(TAG, "before_Verification : result = " +verified.length());

                    if ( b ) {

//                        tv.setVisibility(View.VISIBLE);
                        // Referee exists in the DB, so update Referral Details.
                        flag_phone = true;

                        intent = new Intent(UserCredentialsActivity.this, ReferralSuccessfulActivity.class);

                    } else {

                        intent = new Intent(UserCredentialsActivity.this, ReferralUnsuccessfulActivity.class);

                    }

                } else {

                    referral_no.setError(getString(R.string.invalid_phone_number));
                    focusView = referral_no;
                    intent = new Intent();
                    flag = false;

                }

            } else {

//                intent = new Intent(UserCredentialsActivity.this, NoqStores.class);
                intent = new Intent(UserCredentialsActivity.this, Covid19.class);
            }

            if ( flag ) {

                final String type2 = "store_user";
                String verify = new BackgroundWorker(this).execute(type2, f_name, user_email, User_number, Pno).get();

                final String type3 = "greet_user";
                String verify1 = new AwsBackgroundWorker(this).execute(type3, User_number, f_name).get();

                // -------------------- Temporary Bonus Rs.100 For Each User. ----------------------
                final String type4 = "update_bonus_amt";
                String verify2 = new AwsBackgroundWorker(this).execute(type4, User_number).get();

                if ( flag_phone ) {
                    final String type = "update_ref";
                    String update = new BackgroundWorker(this).execute(type, User_number, Pno).get();

                    // Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the Referred No.
                    final String type1 = "update_referral_balance";
                    final String isUpdated = new AwsBackgroundWorker(this).execute(type1, Pno).get();
                    Log.d(TAG, "Updated Referee's Referral_Balance : "+isUpdated);

//                    // Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the User No.
//                    final String isUpdated1 = new AwsBackgroundWorker(this).execute(type1, User_number).get();
//                    Log.d(TAG, "Updated User's Referral_Balance : "+isUpdated1);
                }

                // -------------------- Temporary Bonus Rs.100 For Each User. ----------------------
                // Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the User No.
                final String type1 = "update_referral_balance";
                final String isUpdated1 = new AwsBackgroundWorker(this).execute(type1, User_number).get();
                Log.d(TAG, "Updated User's Referral_Balance : "+isUpdated1);

                saveLoginDetails(User_number);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(GONE);
//                        tv.setVisibility(GONE);

                        Toast.makeText(UserCredentialsActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                        intent.putExtra(Name, f_name);
                        intent.putExtra(Email, user_email);
                        intent.putExtra("Phone", User_number);
                        intent.putExtra("activity", "UCA");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
