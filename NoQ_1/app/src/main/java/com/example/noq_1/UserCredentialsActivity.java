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
import android.widget.TextView;
import android.widget.Toast;

public class UserCredentialsActivity extends AppCompatActivity {

    Button regbtn;
    EditText et, et1, et2;
    TextView tv, tv1;
    ProgressBar pb;

    public static final String Name = "com.example.noq.NAME";
    public static final String Email = "com.example.noq.EMAIL";
    public static final String Phone = "com.example.noq.PHONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credentials);

        final SharedPreferences sharedPreferences = getSharedPreferences("Content_main", Context.MODE_PRIVATE);
        final String otp_success = "OTP Verified Successfully";

        regbtn = findViewById(R.id.reg_btn);

        et = findViewById(R.id.et);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);

        pb = findViewById(R.id.indeterminateBar);

        pb.setVisibility(View.INVISIBLE);

        tv = findViewById(R.id.otp_success);
        tv1 = findViewById(R.id.tv_disp);

        tv.setText(otp_success);
        tv.setVisibility(View.VISIBLE);

        final String User_number;

        Intent in = getIntent();
        User_number = in.getStringExtra(OTPConfirmActivity.Phone);

        tv1.setText(User_number);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                tv.setVisibility(View.GONE);
            }
        }, 4000);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String f_name = et.getText().toString();
                final String email = et2.getText().toString();
                final String Pno = et1.getText().toString();
                Boolean flag = true;


                if ( f_name.equals("") ) {

                    Toast.makeText(UserCredentialsActivity.this, "Please Enter Your Credentials!", Toast.LENGTH_SHORT).show();

                } else {

                    pb.setVisibility(View.VISIBLE);
                    final Intent intent;

                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("name", f_name);
//                    editor.putString("email", email);
                    editor.putString("Phone", Pno);
                    editor.apply();

                    if ( !Pno.equals("") ) {

                        if ( Pno.length() == 10 || Pno.length() == 12 ) {

                            Boolean verified;
                            verified = verify_referrer(User_number, Pno);

                            if ( verified ) {

                                intent = new Intent(UserCredentialsActivity.this, ReferralSuccessfulActivity.class);

                            } else {

                                intent = new Intent(UserCredentialsActivity.this, ReferralUnsuccessfulActivity.class);

                            }

                        } else {

                            Toast.makeText(UserCredentialsActivity.this, "Please Enter a Valid Number!!", Toast.LENGTH_SHORT).show();
                            et1.setText("");
                            intent = new Intent();
                            flag = false;

                        }

                    } else {

                        intent = new Intent(UserCredentialsActivity.this, BarcodeScannerActivity.class);

                    }

                    if ( flag ) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                pb.setVisibility(View.INVISIBLE);

                                Toast.makeText(UserCredentialsActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                intent.putExtra(Name, f_name);
                                intent.putExtra(Email, email);
                                intent.putExtra(Phone, Pno);
                                startActivity(intent);

                            }
                        }, 1000);

                    } else {

                        pb.setVisibility(View.INVISIBLE);

                    }

                }
            }
        });
    }

    public Boolean verify_referrer(String u_no, String phone) {

        if ( u_no.equals(phone) ) {

            Toast.makeText(this, "Please Enter a Different Number !", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            return true;
        }
    }
}
