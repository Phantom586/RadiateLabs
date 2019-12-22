package com.younoq.noq;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class MyProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tv1, tv2, tv3, tv4, tv5, tvv1, tvv2;
    Button btn_lg;
    JSONArray jsonArray;
    JSONObject jobj1, jobj2;
    SaveInfoLocally saveInfoLocally;

    public final String TAG = "MyProfile";
    public String phone;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        tv1 = findViewById(R.id.text_v1);
        tv2 = findViewById(R.id.text_v2);
        tv3 = findViewById(R.id.text_v3);
        tv4 = findViewById(R.id.text_v4);
        tv5 = findViewById(R.id.text_v5);
        btn_lg = findViewById(R.id.mp_logout);

        saveInfoLocally = new SaveInfoLocally(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(MyProfile.this, BarcodeScannerActivity.class);
                in.putExtra("Type", "Store_Scan");
                startActivity(in);

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // To get the status of the Header in the Navigation View.
        View headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // My Things Start Here
        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        Log.d(TAG, "Phone No in MyProfile : "+phone);

        // Generating the SessionID for the Current Session.
        try {
            final String sess = toHexString(getSHA(getRandomString()+phone+getRandomString()));
            Log.d(TAG, "Session Id : "+sess);
            saveInfoLocally.setSessionID(sess);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        final String type = "retrieve_user_details";


        tvv1 = headerView.findViewById(R.id.text_view1);
        tvv2 = headerView.findViewById(R.id.text_view2);

        // Retrieving the Referral_Balance_Amount of the User
        fetch_referral_amt();

        // Fetching the User Details
        new MyTask().execute(phone);


    }

    public void fetch_referral_amt(){

        final String type = "retrieve_referral_amt";
        final String phone = saveInfoLocally.getPhone();
        try {

            final String res = new AwsBackgroundWorker(this).execute(type, phone).get();
            String ref_bal;
            try
            {
                jsonArray = new JSONArray(res);
                jobj1 = jsonArray.getJSONObject(0);
                if(!jobj1.getBoolean("error")){
                    jobj2 = jsonArray.getJSONObject(1);
                    ref_bal = jobj2.getString("referral_balance");
                    Log.d(TAG, "Referral Amount Balance : "+ref_bal);
                    // Saving the Referral_Amount_Balance to SharedPreferences to be used in CartActivity/
                    saveInfoLocally.setReferralBalance(ref_bal);
                    final String bal = "₹"+ref_bal;
                    tv5.setText(bal);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getRandomString(){
        int n = 4;
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private byte[] getSHA(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(str.getBytes(StandardCharsets.UTF_8));
    }

    private String toHexString(byte[] strHash){
        BigInteger num = new BigInteger(1, strHash);
        StringBuilder hexString = new StringBuilder(num.toString(16));
        while(hexString.length() < 32){
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public void logout(View view) throws ExecutionException, InterruptedException {

        final String type = "set_logout_flag";
        final String res = new BackgroundWorker(this).execute(type, phone, "True").get();
        saveInfoLocally.clear_all();
        saveInfoLocally.setPrevPhone(phone);
        Intent in = new Intent(this, MainActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

    }

    private class MyTask extends AsyncTask<String, Integer, String> {


        StringBuilder result = new StringBuilder();
//        String[] user_data;

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
            String retrieve_data_url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/retrieve_data.php";

            try {

                final String phone = params[0];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8");
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

                Log.d(TAG, result.toString());

                return result.toString().trim();

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
//            Log.d(TAG, "Users Data in Post : "+result);

            try
            {
                jsonArray = new JSONArray(result);
                jobj1 = jsonArray.getJSONObject(0);
                if(!jobj1.getBoolean("error")){
                    jobj2 = jsonArray.getJSONObject(1);

                    final String uname = jobj2.getString("name");
                    saveInfoLocally.setUserName(uname);
                    tvv1.setText(uname);
                    tvv2.setText(jobj2.getString("email"));

                    tv1.setText(jobj2.getString("name"));
                    tv2.setText(jobj2.getString("email"));
                    tv3.setText(jobj2.getString("phone"));
                    tv4.setText(jobj2.getString("referral_phone_number"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_cart) {
//            // Handle the camera action
//            Intent in = new Intent(MyProfile.this, CartActivity.class);
//            startActivity(in);
//        }else
            if (id == R.id.nav_about_us) {
            Intent in = new Intent(MyProfile.this, AboutUs.class);
            startActivity(in);
        } else if (id == R.id.nav_contact) {
            Intent in = new Intent(MyProfile.this, ContactUs.class);
            startActivity(in);
        } else if (id == R.id.nav_terms_cond) {
            Intent in = new Intent(MyProfile.this, TermsAndConditions.class);
            startActivity(in);
        } else if (id == R.id.nav_privacy_policy) {
            Intent in = new Intent(MyProfile.this, PrivacyPolicy.class);
            startActivity(in);
        } else if (id == R.id.nav_refund_policy) {
            Intent in = new Intent(MyProfile.this, RefundPolicy.class);
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
