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

    TextView tv1, tv2, tv3, tv4, tv5;
    SaveInfoLocally saveInfoLocally;

    public final String TAG = "NoQStores";
    public String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noq_stores);

        tv1 = findViewById(R.id.text_v1);
        tv2 = findViewById(R.id.text_v2);
        tv3 = findViewById(R.id.text_v3);
        tv4 = findViewById(R.id.text_v4);
        tv5 = findViewById(R.id.text_v5);

        saveInfoLocally = new SaveInfoLocally(this);

        // Fetching the User Details
        fetch_User_Details();


    }

    public void fetch_User_Details() {

        final String ref_bal = saveInfoLocally.getReferralBalance();
        final String bal = "₹"+ref_bal;
        Log.d(TAG, bal);
        tv5.setText(bal);
        tv1.setText(saveInfoLocally.getUserName());
        tv2.setText(saveInfoLocally.getEmail());
        tv3.setText(saveInfoLocally.getPhone());
        tv4.setText(saveInfoLocally.getReferralNo());

    }


    public void go_back(View view) {
        super.onBackPressed();
    }

}
