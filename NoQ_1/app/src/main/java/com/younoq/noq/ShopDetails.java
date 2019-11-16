package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShopDetails extends AppCompatActivity {

    TextView et1, et2, et3, h1, h2;
    Button btn;
    public static final String Type = "com.example.noq_1.TYPE";

    SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        h1 = findViewById(R.id.sd_h1);
        h2 = findViewById(R.id.sd_h2);
        et1 = findViewById(R.id.sd_et1);
        et2 = findViewById(R.id.sd_et2);
        et3 = findViewById(R.id.sd_et3);
        btn = findViewById(R.id.sd_btn);

        Intent in = getIntent();
        String details = in.getStringExtra(BarcodeScannerActivity.RESULT);
        String sid = in.getStringExtra(BarcodeScannerActivity.BARCODE);

        saveInfoLocally = new SaveInfoLocally(this);
        saveInfoLocally.set_store_id(sid);

        if(sid.equals("3")){
            h1.setText(R.string.sd_school_h1);
            h2.setText(R.string.sd_school);
            btn.setText(R.string.sd_btn);
        }

        try{

            JSONArray jsonArray = new JSONArray(details);
            JSONObject jobj = jsonArray.getJSONObject(1);

            final String store_name = jobj.getString("Store_Name");
            final String store_addr = jobj.getString("Store_Address");
            final String store_city = jobj.getString("Store_City");
            final String store_state = jobj.getString("Store_State");
            final String store_country = jobj.getString("Store_Country");
            saveInfoLocally.setStoreName(store_name);
            final String addr = store_addr + ", " + store_city + ", " + store_state + ", " + store_country;
            final String TAG = "ShopDetails";
            saveInfoLocally.setStoreAddress(addr);
            et1.setText(store_name);

            final String tmp1 = store_addr+ "," + store_city;
            et2.setText(tmp1);
            final String tmp2 = store_state + "," + store_country;
            et3.setText(tmp2);

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void startShopping(View v){

        Intent in = new Intent(ShopDetails.this, BarcodeScannerActivity.class);
        in.putExtra("Type", "Product_Scan");
        in.putExtra("activity", "");
        startActivity(in);

    }
}
