package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static android.view.View.GONE;

public class UserCredentialsActivity extends AppCompatActivity {

    Button regbtn;
    EditText et, et1, et2;
    TextView tv, tv1;
    ProgressBar progressBar;
    String User_number;

    static String verified = "";
    static Boolean status = false;

    public static final String Name = "com.example.noq.NAME";
    public static final String Email = "com.example.noq.EMAIL";
    public static final String Phone = "com.example.noq.PHONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credentials);

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

        final Boolean save_user_details;

        Intent in = getIntent();
        User_number = in.getStringExtra(OTPConfirmActivity.Phone);
        save_user_details = in.getBooleanExtra(OTPConfirmActivity.Save_User_Data, true);

        tv1.setText(User_number);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                tv.setVisibility(View.INVISIBLE);
            }
        }, 4000);

    }

    public void Register(View v){

        final String f_name = et.getText().toString();
        final String email = et2.getText().toString();
        final String Pno = et1.getText().toString();

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

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

                if ( Pno.length() == 10 || Pno.length() == 12 ) {

                    flag_phone = true;

                    new Verify_Referrer().execute(User_number, Pno);

                    final String TAG = "Background Worker";
                    Log.d(TAG, "before_Vefification" + verified);

//                    while(!status){
//                        Log.d(TAG, "Waiting for referrer's no. to be verified");
//                    }

                    if ( verified.equals("TRUE") ) {

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

                if ( flag_phone ) {

                    // To Check whether the user checked Remember Me Box or not. If true then Save data otherwise don't save.
//                            if (save_user_details) {

//                    final String type = "save_user_details";

                    backgroundWorker.execute(f_name, email, User_number, Pno);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(GONE);
                        tv.setVisibility(GONE);

                        Toast.makeText(UserCredentialsActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                        intent.putExtra(Name, f_name);
                        intent.putExtra(Email, email);
                        intent.putExtra(Phone, Pno);
                        startActivity(intent);

                    }
                }, 1000);

            } else {

                progressBar.setVisibility(View.INVISIBLE);

            }

        }
    }

    private static class Verify_Referrer extends AsyncTask<String, Integer, String> {


        StringBuilder result = new StringBuilder();

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {

//            String type = params[0];
            String retrieve_data_url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/verify_update_ref_user.php";

            try {

                final String user = params[0];
                final String referrer = params[1];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("user", "UTF-8")+"="+URLEncoder.encode(user, "UTF-8")+ "&" +
                                    URLEncoder.encode("referrer", "UTF-8")+"="+URLEncoder.encode(referrer, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                final String TAG = "Background Worker";

                Log.d(TAG, "in onBackground" + result.toString());

                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            user_data = result.split("-", 6);
//            final String TAG = "Background Worker";
//            Log.d(TAG, "in onPostExecute" + result);
//            Log.d(TAG, result.getClass().getName());
            verified = result;
            status = true;

            // Do things like hide the progress bar or change a TextView

        }
    }

//    public Boolean verify_referrer(String u_no, String phone) {
//
//        if ( u_no.equals(phone) ) {
//
//            Toast.makeText(this, "Please Enter a Different Number !", Toast.LENGTH_SHORT).show();
//            return false;
//
//        } else {
//            return true;
//        }
//    }
}
