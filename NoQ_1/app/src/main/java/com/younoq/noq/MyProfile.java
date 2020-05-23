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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MyProfileActivity";

    TextView tvv1, tvv2, nav_img, tv_bonus_amt, tv_city_name, tv_alcohol, tv_books, tv_dairy, tv_grocery, tv_poultry, tv_school;
    SaveInfoLocally saveInfoLocally;
    String phone, city_name;
    private Boolean exit = false;
    LinearLayout linearLayout;

    JSONArray jsonArray1,  jsonArray2;
    JSONObject jobj11, jobj12;
    JSONArray jsonArray;
    JSONObject jobj1, jobj2;
    private HashMap<String, HashMap<String, Object>> storesList;

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

        tv_city_name = findViewById(R.id.mp_city_name);
        linearLayout = findViewById(R.id.mp_city_ll);
        tv_alcohol = findViewById(R.id.mp_alcohol_cs);
        tv_books = findViewById(R.id.mp_stationary_cs);
        tv_dairy = findViewById(R.id.mp_dairy_cs);
        tv_grocery = findViewById(R.id.mp_grocery_cs);
        tv_poultry = findViewById(R.id.mp_meat_cs);
        tv_alcohol = findViewById(R.id.mp_alcohol_cs);
        tv_school = findViewById(R.id.mp_school_cs);

        // If the app is opened for the First Time, and there is No DirectLogin to the App.
        if (!isNotfirstLogin() && !isDirectLogin){

            tv_bonus_amt = findViewById(R.id.mp_bonus_amt);
            tv_bonus_amt.setVisibility(View.VISIBLE);

        }

//        progressBar = findViewById(R.id.mp_spin_kit);
//        Sprite cubeGrid = new CubeGrid();
//        progressBar.setIndeterminateDrawable(cubeGrid);

//        recyclerView = findViewById(R.id.mp_recyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        StoreList = new ArrayList<>();

        setUserDetails();

        retrieve_stores_categories();

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

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting the City to blank.
                saveInfoLocally.setStoreCity("");

                Intent in = new Intent(v.getContext(), CitySelect.class);
                in.putExtra("Phone", phone);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
            }
        });
    }

    private boolean isNotfirstLogin() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
        Boolean res = sharedPreferences.getBoolean("isIntroOpened", false);
        Log.d(TAG, "isNotFirstLogin : "+res);
        return res;

    }

    public void setUserDetails() {

        // Retrieving the City Name from the SharedPreferences
        city_name = saveInfoLocally.getStoreCity();
        tv_city_name.setText(city_name);

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

    public void retrieve_stores_categories(){

        storesList = new HashMap<>();
        // List to Store the Coming Soon TextViews.
        List<TextView> categoriesTextViews = new ArrayList<>();
        categoriesTextViews.add(tv_alcohol);
        categoriesTextViews.add(tv_books);
        categoriesTextViews.add(tv_dairy);
        categoriesTextViews.add(tv_grocery);
        categoriesTextViews.add(tv_poultry);
        categoriesTextViews.add(tv_school);

        final String type = "retrieve_stores_categories";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, city_name).get();
            Log.d(TAG, "Stores Categories : " + res);

            jsonArray1 = new JSONArray(res);
            for(int index = 0; index < jsonArray1.length(); index++){

                jobj1 = jsonArray1.getJSONObject(index);
                final String category_name = jobj1.getString("category");

                List<String> storeList = new ArrayList<>();
                HashMap<String, Object> storeDetails = new HashMap<>();

                jsonArray2 = jobj1.getJSONArray("stores");
                for(int index1 = 0; index1 < jsonArray2.length(); index1++){

                    storeList.add(jsonArray2.get(index1).toString());

                }

                final boolean available = jobj1.getString("availability").equals("true");
                // adding the availability status and stores list in the storeDetails.
                storeDetails.put("stores", storeList);
                storeDetails.put("availability", jobj1.getString("availability"));
                // adding the storeDetails to storesList HashMap.
                storesList.put(category_name, storeDetails);

                if(available)
                    categoriesTextViews.get(index).setVisibility(View.INVISIBLE);
                else
                    categoriesTextViews.get(index).setVisibility(View.VISIBLE);

            }

            Log.d(TAG, "Stores List : "+storesList);


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
            Intent in = new Intent(MyProfile.this, UserProfile.class);
//            in.putExtra("activity", "MP");
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void alcoholAndBeverage(View view) {

        final HashMap<String, Object> categoryDetails = storesList.get("Alcohol & Beverages");
        Log.d(TAG, "Alcohol Details : "+categoryDetails);
        final boolean available = categoryDetails.get("availability").equals("true");
        if(available){
            Log.d(TAG, "Alcohol Availability : "+available);

            Bundle storeList = new Bundle();

            final ArrayList<String> listStores = (ArrayList)categoryDetails.get("stores");
            Log.d(TAG, "Alcohol Stores List : "+listStores);

            storeList.putStringArrayList("storesList", listStores);

            Intent in = new Intent(this, StoresNoq.class);
            in.putExtras(storeList);
            startActivity(in);
        } else {
            Toast.makeText(this, "Sorry! this category is not available yet in this region", Toast.LENGTH_SHORT).show();
        }

    }

    public void BooksAndStationary(View view) {

        final HashMap<String, Object> categoryDetails = storesList.get("Books, Stationary & Art");
        Log.d(TAG, "Books Details : "+categoryDetails);
        final boolean available = categoryDetails.get("availability").equals("true");
        if(available){
            Log.d(TAG, "Books Availability : "+available);

            Bundle storeList = new Bundle();

            final ArrayList<String> listStores = (ArrayList)categoryDetails.get("stores");
            Log.d(TAG, "Books Stores List : "+listStores);

            storeList.putStringArrayList("storesList", listStores);

            Intent in = new Intent(this, StoresNoq.class);
            in.putExtras(storeList);
            startActivity(in);
        } else {
            Toast.makeText(this, "Sorry! this category is not available yet in this region", Toast.LENGTH_SHORT).show();
        }

    }

    public void dairyAndBakery(View view) {

        final HashMap<String, Object> categoryDetails = storesList.get("Dairy & Bakery");
        Log.d(TAG, "Dairy Details : "+categoryDetails);
        final boolean available = categoryDetails.get("availability").equals("true");
        if(available){
            Log.d(TAG, "Dairy Availability : "+available);

            Bundle storeList = new Bundle();

            final ArrayList<String> listStores = (ArrayList)categoryDetails.get("stores");
            Log.d(TAG, "Dairy Stores List : "+listStores);

            storeList.putStringArrayList("storesList", listStores);

            Intent in = new Intent(this, StoresNoq.class);
            in.putExtras(storeList);
            startActivity(in);
        } else {
            Toast.makeText(this, "Sorry! this category is not available yet in this region", Toast.LENGTH_SHORT).show();
        }

    }

    public void grocery(View view) {

        final HashMap<String, Object> categoryDetails = storesList.get("Grocery");
        Log.d(TAG, "Grocery Details : "+categoryDetails);
        final boolean available = categoryDetails.get("availability").equals("true");
        if(available){
            Log.d(TAG, "Grocery Availability : "+available);

            Bundle storeList = new Bundle();

            final ArrayList<String> listStores = (ArrayList)categoryDetails.get("stores");
            Log.d(TAG, "Grocery Stores List : "+listStores);

            storeList.putStringArrayList("storesList", listStores);

            Intent in = new Intent(this, StoresNoq.class);
            in.putExtras(storeList);
            startActivity(in);
        } else {
            Toast.makeText(this, "Sorry! this category is not available yet in this region", Toast.LENGTH_SHORT).show();
        }

    }

    public void meatAndPoultry(View view) {

        final HashMap<String, Object> categoryDetails = storesList.get("Meat & Poultry");
        Log.d(TAG, "Meat Details : "+categoryDetails);
        final boolean available = categoryDetails.get("availability").equals("true");
        if(available){
            Log.d(TAG, "Meat Availability : "+available);

            Bundle storeList = new Bundle();

            final ArrayList<String> listStores = (ArrayList)categoryDetails.get("stores");
            Log.d(TAG, "Meat Stores List : "+listStores);

            storeList.putStringArrayList("storesList", listStores);

            Intent in = new Intent(this, StoresNoq.class);
            in.putExtras(storeList);
            startActivity(in);
        } else {
            Toast.makeText(this, "Sorry! this category is not available yet in this region", Toast.LENGTH_SHORT).show();
        }

    }

    public void schoolPayment(View view) {

        final HashMap<String, Object> categoryDetails = storesList.get("School Payments");
        Log.d(TAG, "School Payments Details : "+categoryDetails);
        final boolean available = categoryDetails.get("availability").equals("true");
        if(available){
            Log.d(TAG, "School Availability : "+available);

            Bundle storeList = new Bundle();

            final ArrayList<String> listStores = (ArrayList)categoryDetails.get("stores");
            Log.d(TAG, "School Stores List : "+listStores);

            storeList.putStringArrayList("storesList", listStores);

            Intent in = new Intent(this, StoresNoq.class);
            in.putExtras(storeList);
            startActivity(in);
        } else {
            Toast.makeText(this, "Sorry! this category is not available yet in this region", Toast.LENGTH_SHORT).show();
        }

    }
}
