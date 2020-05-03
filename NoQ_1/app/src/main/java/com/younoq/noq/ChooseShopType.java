package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ChooseShopType extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;
    public static String TAG = "ChooseShopType Activity";
    TextView tv_in_store_title, tv_in_store, tv_takeaway, tv_home_delivery;
    CardView cv_in_store, cv_takeaway, cv_home_delivery;
    private boolean in_store, takeaway, home_delivery;
    LinearLayout ll_delivery, ll_takeaway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shop_type);

        saveInfoLocally = new SaveInfoLocally(this);
        tv_in_store = findViewById(R.id.acs_card1_tv_2);
        tv_in_store_title = findViewById(R.id.acs_card1_tv_1);
        tv_takeaway = findViewById(R.id.acs_card2_tv_2);
        tv_home_delivery = findViewById(R.id.acs_card3_tv_2);
        cv_in_store = findViewById(R.id.acs_card1);
        cv_takeaway = findViewById(R.id.acs_card2);
        cv_home_delivery = findViewById(R.id.acs_card3);
        ll_delivery = findViewById(R.id.acs_linear_layout_delivery);
        ll_takeaway = findViewById(R.id.acs_linear_layout_takeaway);

        retrieve_shop_details();

    }

    private void retrieve_shop_details() {

        final String store_id = saveInfoLocally.get_store_id();
        final String type = "verify_store";
        String res = "";

        try {
            res = new BackgroundWorker(this).execute(type, store_id).get();
            Log.d(TAG, "Store Scan Result : "+res+" length : "+res.length());
            JSONArray jsonArray = new JSONArray(res);
            JSONObject jobj = jsonArray.getJSONObject(0);
            if(!jobj.getBoolean("error")){
                JSONArray jsonArray1 = new JSONArray(res);
                JSONObject jobj1 = jsonArray1.getJSONObject(1);
//
                in_store = jobj1.getString("in_store").equals("true");
                takeaway = jobj1.getString("takeaway").equals("true");
                home_delivery = jobj1.getString("home_delivery").equals("true");
                final String tmp = "In_Store : " + in_store + ", Takeaway : " + takeaway + ", Home_Delivery : "+home_delivery;
                Log.d(TAG, tmp);

                // Checking If In_store is Available
                if(in_store){
                    if(store_id.equals("3")){
                        tv_in_store.setText(R.string.cst_in_school_desc);
                        tv_in_store_title.setText(R.string.cst_in_school);
                    } else {
                        tv_in_store.setText(R.string.cst_in_shop_desc);
                    }
                }
                else
                    tv_in_store.setText(R.string.cst_coming_soon);
                // Checking If Takeaway is Available
                if(takeaway) {
                    ll_takeaway.setVisibility(View.GONE);
                    tv_takeaway.setText(R.string.cst_takeaway_desc);
                }
                else {
                    tv_takeaway.setText(R.string.cst_coming_soon);
                    ll_takeaway.setVisibility(View.VISIBLE);
                }
                // Checking If Home Delivery is Available
                if(home_delivery) {
                    ll_delivery.setVisibility(View.GONE);
                    tv_home_delivery.setText(R.string.cst_takeaway_desc);
                }
                else {
                    tv_home_delivery.setText(R.string.cst_coming_soon);
                    ll_delivery.setVisibility(View.VISIBLE);
                }

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void inStore(View view) {
        if (in_store){

            Log.d(TAG, "In Store Clicked");
            Intent in = new Intent(this, BarcodeScannerActivity.class);
            in.putExtra("Type", "Product_Scan");
            startActivity(in);

        }
        else
            Toast.makeText(this, "This Facility isn't available yet", Toast.LENGTH_SHORT).show();
    }

    public void takeaway(View view) {
        if (takeaway) {

            Log.d(TAG, "In Store Clicked");
            Intent in = new Intent(this, ProductsList.class);
            startActivity(in);

        }
        else
            Toast.makeText(this, "This Facility isn't available yet", Toast.LENGTH_SHORT).show();
    }

    public void homeDelivery(View view) {
        if (home_delivery)
            Log.d(TAG, "Home Delivery Clicked");
        else
            Toast.makeText(this, "This Facility isn't available yet", Toast.LENGTH_SHORT).show();
    }

    public void showInterestTakeaway(View view) {
        Log.d(TAG, "Shown Interest in Takeaway");
        Toast.makeText(this, "Thanks for Showing your Interest", Toast.LENGTH_SHORT).show();
    }


    public void showInterestDelivery(View view) {
        Log.d(TAG, "Shown Interest in Home Delivery");
        Toast.makeText(this, "Thanks for Showing your Interest", Toast.LENGTH_SHORT).show();
    }

    public void onContinue(View view) {
    }
}
