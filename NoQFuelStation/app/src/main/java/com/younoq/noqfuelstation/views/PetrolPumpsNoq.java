package com.younoq.noqfuelstation.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.adapters.PetrolPumpAdapter;
import com.younoq.noqfuelstation.classes.PetrolPump;
import com.younoq.noqfuelstation.companypolicy.AboutUs;
import com.younoq.noqfuelstation.companypolicy.ContactUs;
import com.younoq.noqfuelstation.companypolicy.PrivacyPolicy;
import com.younoq.noqfuelstation.companypolicy.RefundPolicy;
import com.younoq.noqfuelstation.companypolicy.TermsAndConditions;
import com.younoq.noqfuelstation.models.AwsBackgroundWorker;
import com.younoq.noqfuelstation.models.BackgroundWorker;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PetrolPumpsNoq extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private TextView tvv1, tvv2, nav_img, tv_city_name;
    private String TAG = "StoresNoq", storeList, phone, city_name, city_area;
    private SaveInfoLocally saveInfoLocally;
    private Boolean exit = false;
    private JSONArray jsonArray, jsonArray1;
    private JSONObject jobj1, jobj2, jobj3;
    private List<PetrolPump> petrolPumpList;
    private PetrolPumpAdapter petrolPumpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petrol_pumps_noq);

        tv_city_name = findViewById(R.id.ppa_city);
        recyclerView = findViewById(R.id.pp_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        saveInfoLocally = new SaveInfoLocally(this);
        petrolPumpList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // To get the status of the Header in the Navigation View.
        View headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Fetching Elements in Navigation Drawer.
        tvv1 = headerView.findViewById(R.id.text_view1);
        tvv2 = headerView.findViewById(R.id.text_view2);
        nav_img = headerView.findViewById(R.id.mp_img_txt);

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");

        setUserDetails();

        fetch_referral_amt();

        fetchPetrolPumps();

    }

    public void setUserDetails() {

        // Retrieving the City Name from the SharedPreferences
        city_name = saveInfoLocally.getStoreCity();
        city_area = saveInfoLocally.getStoreCityArea();

        final String cityArea = city_name + ", " + city_area;
        tv_city_name.setText(cityArea);

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
                final String addr = jobj2.getString("address");

                saveInfoLocally.setUserName(uname);
                saveInfoLocally.setEmail(email);
                saveInfoLocally.setReferralNo(jobj2.getString("referral_phone_number"));
                saveInfoLocally.setUserAddress(addr);

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

        } catch (ExecutionException | JSONException | InterruptedException e) {
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

    private void fetchPetrolPumps() {

        final String type = "fetchPetrolPumps";
        try {

            final String res = new BackgroundWorker(this).execute(type, city_name, city_area).get();
            Log.d(TAG, "petrol Pumps Res : "+res);

            jsonArray1 = new JSONArray(res.trim());
            for (int index = 0; index < jsonArray1.length(); index++) {

                jobj3 = jsonArray1.getJSONObject(index);

                petrolPumpList.add(
                        new PetrolPump(
                                jobj3.getString("id"),
                                jobj3.getString("name"),
                                jobj3.getString("addr"),
                                jobj3.getString("city"),
                                jobj3.getString("pin"),
                                jobj3.getString("state"),
                                jobj3.getString("country"),
                                jobj3.getString("phone_no"),
                                jobj3.getString("image")
                        )
                );

            }

            petrolPumpAdapter = new PetrolPumpAdapter(this, petrolPumpList);
            recyclerView.setAdapter(petrolPumpAdapter);


        } catch (ExecutionException | InterruptedException | JSONException e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_about_us) {
            Intent in = new Intent(PetrolPumpsNoq.this, AboutUs.class);
            startActivity(in);
        } else if (id == R.id.nav_contact) {
            Intent in = new Intent(PetrolPumpsNoq.this, ContactUs.class);
            startActivity(in);
        } else if (id == R.id.nav_terms_cond) {
            Intent in = new Intent(PetrolPumpsNoq.this, TermsAndConditions.class);
            startActivity(in);
        } else if (id == R.id.nav_privacy_policy) {
            Intent in = new Intent(PetrolPumpsNoq.this, PrivacyPolicy.class);
            startActivity(in);
        } else if (id == R.id.nav_refund_policy) {
            Intent in = new Intent(PetrolPumpsNoq.this, RefundPolicy.class);
            startActivity(in);
        } else if (id == R.id.nav_last_five_txns) {
            Intent in = new Intent(PetrolPumpsNoq.this, LastFiveTxns.class);
            in.putExtra("Phone", phone);
            startActivity(in);
        }else if (id == R.id.nav_logout) {

            final String type = "set_logout_flag";
            try {
                final String res = new BackgroundWorker(this).execute(type, phone, "True").get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            saveInfoLocally.clear_all();
            saveInfoLocally.setPrevPhone(phone);
            saveInfoLocally.setHasFinishedIntro();
            Intent in = new Intent(this, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SelectCity(View view) {

        // Setting the City to blank.
        saveInfoLocally.setStoreCity("");

        Intent in = new Intent(view.getContext(), CitySelect.class);
        in.putExtra("Phone", phone);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

    }

}