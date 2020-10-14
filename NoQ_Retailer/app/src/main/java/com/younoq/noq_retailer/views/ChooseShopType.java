package com.younoq.noq_retailer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.younoq.noq_retailer.R;
import com.younoq.noq_retailer.models.AwsBackgroundWorker;
import com.younoq.noq_retailer.models.SaveInfoLocally;

import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ChooseShopType extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;
    public static String TAG = "ChooseShopType Activity";
    TextView tv_in_store_title, tv_store_name;
    private boolean in_store, takeaway, home_delivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shop_type);

        saveInfoLocally = new SaveInfoLocally(this);
        tv_in_store_title = findViewById(R.id.acs_card1_tv_1);
        tv_store_name = findViewById(R.id.cst_store_name);

        Intent in = getIntent();
        in_store = in.getBooleanExtra("in_store", false);
        takeaway = in.getBooleanExtra("takeaway", false);
        home_delivery = in.getBooleanExtra("home_delivery", false);

        retrieve_shop_details();

    }

    private void retrieve_shop_details() {

        final String store_name = saveInfoLocally.getStoreName() + ", " + saveInfoLocally.getStoreAddress();
        tv_store_name.setText(store_name);

        final String store_id = saveInfoLocally.get_store_id();
        final String type = "verify_store";
        String res = "";

        final String tmp = "In_Store : " + in_store + ", Takeaway : " + takeaway + ", Home_Delivery : "+home_delivery;
        Log.d(TAG, tmp);

    }

    public void partnerDelivery(View view) {

        Intent in = new Intent(this, ChoosePartnerDelivery.class);
        startActivity(in);

    }

    @Override
    public void onBackPressed() {
        final String phone = saveInfoLocally.getPhone();
        final String storeList = saveInfoLocally.getCategoryStores();
        Intent in = new Intent(this, StoresNoq.class);
        in.putExtra("Phone", phone);
        in.putExtra("isDirectLogin", false);
        in.putExtra("storesList", storeList);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }
}
