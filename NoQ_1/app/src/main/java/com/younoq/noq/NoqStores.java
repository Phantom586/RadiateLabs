package com.younoq.noq;

import android.content.Intent;
import android.media.Image;
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
import android.widget.ImageView;
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

public class NoqStores extends AppCompatActivity {

    TextView tv_im_name, tv_name, tv_email, tv_referral_amt, tv_total_savings, tv_phone_no;
    SaveInfoLocally saveInfoLocally;

    public final String TAG = "NoQStores";
    public String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noq_stores);

        tv_im_name = findViewById(R.id.ns_tv_name);
        tv_name = findViewById(R.id.ns_username);
//        tv_total_savings = findViewById(R.id.ns_total_savings);
        tv_referral_amt = findViewById(R.id.ns_referral_amt);
        tv_phone_no = findViewById(R.id.ns_phone_no);
        tv_email = findViewById(R.id.ns_email);

        saveInfoLocally = new SaveInfoLocally(this);

        // Fetching the User Details
        fetch_User_Details();

    }

    public void fetch_User_Details() {

        final String ref_bal = saveInfoLocally.getReferralBalance();
        final String bal = "₹"+ref_bal;
        tv_email.setText(saveInfoLocally.getEmail());
        tv_phone_no.setText(saveInfoLocally.getPhone());
        tv_referral_amt.setText(bal);

        final String uname = saveInfoLocally.getUserName();
        tv_name.setText(uname);

        final String[] name_credentials = uname.split(" ", 2);
        String na;
        if (name_credentials.length >= 2) {
//                        Log.d(TAG, "name Length Greater than Two");
            final String f = name_credentials[0];
            final String l = name_credentials[1];
            na = String.valueOf(f.charAt(0)) + l.charAt(0);
        } else {
//                        Log.d(TAG, "name Length Smaller than Two");
            final String f = name_credentials[0];
            na = String.valueOf(f.charAt(0));
        }

        tv_im_name.setText(na);

    }

    public void go_back(View view) {
        super.onBackPressed();
    }

    public void Logout(View view) {

        final String type = "set_logout_flag";
        final String phone = saveInfoLocally.getPhone();
        try {
            final String res = new BackgroundWorker(this).execute(type, phone, "True").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        saveInfoLocally.clear_all();
        saveInfoLocally.setPrevPhone(phone);
        Intent in = new Intent(this, MainActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

    }
}
