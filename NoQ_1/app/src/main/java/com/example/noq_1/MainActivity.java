package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

public class MainActivity extends AppCompatActivity {

    EditText et;
    Button btn;
    ProgressBar progressBar;
    CheckBox remember_me;

    public static final String Phone = "com.example.noq.PHONE";
    public static final String Otp = "com.example.noq.OTP";

    SaveInfoLocally save_data = new SaveInfoLocally(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

    public String send_otp() {

        return "9865";
    }

    public void onContinue(View v) {

        final String phone = et.getText().toString();
        final String OTP;
        OTP = send_otp();

        et.setError(null);

        View focusView = null;
        Boolean flag = true;

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

                    }

                    if ( flag ) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                progressBar.setVisibility(View.GONE);

                                Intent in = new Intent(MainActivity.this, OTPConfirmActivity.class);
                                in.putExtra(Phone, phone);
                                in.putExtra(Otp, OTP);
                                startActivity(in);

                            }
                        }, 1000);

                    }

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

}
