package com.younoq.noq;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MyProfileActivity";

    TextView tvv1, tvv2, nav_img, tv_bonus_amt;
    SaveInfoLocally saveInfoLocally;
    String phone;
    private Boolean exit = false;

    JSONArray jsonArray1,  jsonArray2;
    JSONObject jobj11, jobj12;
    JSONArray jsonArray;
    JSONObject jobj1, jobj2;

    ProgressBar progressBar;
    RecyclerView recyclerView;
    StoresAdapter storesAdapter;
    List<Store> StoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        saveInfoLocally = new SaveInfoLocally(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // To get the status of the Header in the Navigation View.
        View headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        final boolean isDirectLogin = in.getBooleanExtra("isDirectLogin", false);
        Log.d(TAG, "isDirectLogin in MyProfile : "+isDirectLogin);

        // Generating the SessionID for the Current Session.
        try {
            final String sess = toHexString(getSHA(getRandomString()+phone+getRandomString()));
            Log.d(TAG, "Session Id : "+sess);
            saveInfoLocally.setSessionID(sess);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Fetching Elements in Navigation Drawer.
        tvv1 = headerView.findViewById(R.id.text_view1);
        tvv2 = headerView.findViewById(R.id.text_view2);
        nav_img = headerView.findViewById(R.id.mp_img_txt);

        // If the app is opened for the First Time, and there is No DirectLogin to the App.
        if (!isNotfirstLogin() && !isDirectLogin){

            tv_bonus_amt = findViewById(R.id.mp_bonus_amt);
            tv_bonus_amt.setVisibility(View.VISIBLE);

        }

//        progressBar = findViewById(R.id.mp_spin_kit);
//        Sprite cubeGrid = new CubeGrid();
//        progressBar.setIndeterminateDrawable(cubeGrid);

        recyclerView = findViewById(R.id.mp_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        StoreList = new ArrayList<>();

        setUserDetails();

        retrieve_current_stores();

        fetch_referral_amt();

        if (!isNotfirstLogin() && !isDirectLogin){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_bonus_amt.setVisibility(View.GONE);
                }
            }, 3000);
            // Changing the Login Status.
            changeFirstLoginStatus();
        }
    }

    private boolean isNotfirstLogin() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
        Boolean res = sharedPreferences.getBoolean("isIntroOpened", false);
        Log.d(TAG, "isNotFirstLogin : "+res);
        return res;

    }

    public void setUserDetails() {

        try {

            final String type = "retrieve_data";
            String data = new BackgroundWorker(this).execute(type, phone).get();
            System.out.println(data);

            jsonArray = new JSONArray(data);
            jobj1 = jsonArray.getJSONObject(0);
            if(!jobj1.getBoolean("error")) {
                jobj2 = jsonArray.getJSONObject(1);

                final String uname = jobj2.getString("name");
                final String email = jobj2.getString("email");

                saveInfoLocally.setUserName(uname);
                saveInfoLocally.setEmail(email);
                saveInfoLocally.setReferralNo(jobj2.getString("referral_phone_number"));

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

                nav_img.setText(na);
                tvv1.setText(uname);
                tvv2.setText(email);

            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
//                    final String bal = "₹"+ref_bal;
//                    tv5.setText(bal);
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

    public void retrieve_current_stores(){

        final String type = "retrieve_stores_data";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type).get();
//            Log.d(TAG, "Result : " + res);

            jsonArray1 = new JSONArray(res);
            jobj11 = jsonArray1.getJSONObject(0);
            if(!jobj11.getBoolean("error")){

                jobj12 = jsonArray1.getJSONObject(1);
                jsonArray2 = jobj12.getJSONArray("data");
//                Log.d(TAG, "Stores Array Hopefully : "+jsonArray2+ " length : "+jsonArray2.length());
                for (int i = 0; i < jsonArray2.length(); i++){

                    JSONObject obj = jsonArray2.getJSONObject(i);

                    StoreList.add(
                            new Store(
                                    obj.get("store_id").toString(),
                                    obj.get("store_name").toString(),
                                    obj.get("store_addr").toString(),
                                    obj.get("store_city").toString(),
                                    obj.get("pin").toString(),
                                    obj.get("store_state").toString(),
                                    obj.get("store_country").toString(),
                                    obj.get("phone_no").toString(),
                                    obj.get("in_store").toString().toLowerCase().equals("true"),
                                    obj.get("takeaway").toString().toLowerCase().equals("true"),
                                    obj.get("home_delivery").toString().toLowerCase().equals("true")
                            )
                    );

                }

                storesAdapter = new StoresAdapter(this, StoreList);
                recyclerView.setAdapter(storesAdapter);

            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void changeFirstLoginStatus() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.apply();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my_profile, menu);
        return false;
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
        } else if (id == R.id.nav_last_five_txns) {
            Intent in = new Intent(MyProfile.this, LastFiveTxns.class);
            in.putExtra("Phone", phone);
            startActivity(in);
        }else if (id == R.id.nav_logout) {

            final String type = "set_logout_flag";
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

        } else if (id == R.id.profile) {
            Intent in = new Intent(MyProfile.this, NoqStores.class);
//            in.putExtra("activity", "MP");
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
