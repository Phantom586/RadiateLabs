package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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

public class MainActivity extends AppCompatActivity{

    EditText et;
    Button btn;
    ProgressBar progressBar;
    CheckBox remember_me;
    Boolean save_user_data = false;

    public static final String Phone = "com.example.noq_1.PHONE";
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
        remember_me = findViewById(R.id.remember_me);

        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();

        progressBar = findViewById(R.id.spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

    }

    public String generatePIN()
    {

        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;

        return String.valueOf(randomPIN);

    }

    public void onContinue(View v) throws ExecutionException, InterruptedException {

        final String phone = "+91"+et.getText().toString().trim();
//        Log.d(TAG, phone);
//        Log.d(TAG, otp);
//        final String OTP;
//        OTP = send_otp();

        et.setError(null);

        View focusView = null;

        if ( TextUtils.isEmpty(phone) ) {

            et.setError(getString(R.string.blank_phone));
            focusView = et;

        } else {

            if ( phone.length() == 13) {

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

                    final String otp = generatePIN();
                    new SendOTP().execute(otp, phone);

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
                            in.putExtra(Otp, otp);
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

    private static class SendOTP extends AsyncTask<String, Integer, String> {

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
                String post_data = URLEncoder.encode("phone", "UtF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode("OTP : "+msg, "UTF-8");
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
