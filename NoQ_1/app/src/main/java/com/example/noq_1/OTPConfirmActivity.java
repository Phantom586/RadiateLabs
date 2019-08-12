package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

public class OTPConfirmActivity extends AppCompatActivity {

    EditText otp;
    ProgressBar progressBar;

    Button cont, resend, re_enter;
    String phone = "";
    static String checkOTP = "";
    static String otp_pin = "";
    final String TAG = "OTPConfirmActivity";

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

        Intent intent = getIntent();
        phone = intent.getStringExtra(MainActivity.Phone);
        checkOTP = intent.getStringExtra(MainActivity.Otp);


    }

    public String generatePIN()
    {

        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;

        return String.valueOf(randomPIN);

    }

    public void verify_otp(String checkOTP) {

//        final Boolean save_user_details = intent.getBooleanExtra(MainActivity.Save_User_Data, true);

        View focusView;
        Log.d(TAG, "OTP in verify_otp : "+checkOTP);

        final String check_otp = otp.getText().toString();

        if ( check_otp.equals(checkOTP) ) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    progressBar.setVisibility(View.GONE);
                    Intent in = new Intent(OTPConfirmActivity.this, UserCredentialsActivity.class);
                    in.putExtra(Phone, phone);
//                    in.putExtra(Save_User_Data, save_user_details);
                    startActivity(in);

                }
            }, 1000);

        } else {

            progressBar.setVisibility(View.INVISIBLE);
            otp.setError(getString(R.string.invalid_otp));
            otp.setText("");
            otp.setError(null);
            focusView = otp;

        }

    }

    public void OnContinue(View v) {

        otp.setError(null);

        progressBar.setVisibility(View.VISIBLE);

        View focusView;

        final String check_otp = otp.getText().toString().trim();

        if (TextUtils.isEmpty(check_otp)) {

            progressBar.setVisibility(View.INVISIBLE);
            otp.setError(getString(R.string.blank_otp));
            focusView = otp;

        } else {

            Log.d(TAG, "OTP in OnContinue : "+checkOTP);
            verify_otp(checkOTP);

        }

    }

    private static class SendOTP extends AsyncTask<String, Integer, String>{

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

            String insert_data_url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/Amazon/send_message.php";

            try {

                final String msg = params[0];
                final String phone = params[1];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode("OTP : "+msg, "UTF-8") + "&" +
                        URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                final String TAG = "OTPConfirmActivity";
                Log.d(TAG, result.toString());

                //json = result.toString();
                //jsonObject = new JSONObject(result.toString());

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
            // Do things like hide the progress bar or change a TextView

        }

    }

    public void OnResend(View v) throws ExecutionException, InterruptedException {

        otp_pin = generatePIN();
        otp.setText("");
        otp.setError(null);

        Log.d(TAG, "Phone No. in OnResend : "+phone);

        progressBar.setVisibility(View.VISIBLE);
        String resut = new SendOTP().execute(otp_pin, phone).get();

        checkOTP = otp_pin;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 500);
        Log.d(TAG, "OTP in OnResend : "+checkOTP);

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
