package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShopDetails extends AppCompatActivity {

    TextView et1, et2, et3;
    public static final String Type = "com.example.noq_1.TYPE";

    SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        et1 = findViewById(R.id.sd_et1);
        et2 = findViewById(R.id.sd_et2);
        et3 = findViewById(R.id.sd_et3);

        Intent in = getIntent();
        String details = in.getStringExtra(BarcodeScannerActivity.RESULT);
        String sid = in.getStringExtra(BarcodeScannerActivity.BARCODE);

        saveInfoLocally = new SaveInfoLocally(this);
        saveInfoLocally.set_store_id(sid);

        try{

            JSONArray jsonArray = new JSONArray(details);
            JSONObject jobj = jsonArray.getJSONObject(1);
            et1.setText(jobj.getString("Store_Name"));
            final String tmp1 = jobj.getString("Store_Address")+ "," + jobj.getString("Store_City");
            et2.setText(tmp1);
            final String tmp2 = jobj.getString("Store_State") + "," + jobj.getString("Store_Country");
            et3.setText(tmp2);

        } catch(Exception e){
            e.printStackTrace();
        }
//        String[] user_data;
//        if(!details.equals("") && details.length() > 0){
//
//            user_data = details.split("-", 8);
//            et1.setText(user_data[0]);
//            String temp = user_data[1] + ", " + user_data[2];
//            et2.setText(temp);
//            temp = user_data[3] + ", " + user_data[4];
//            et3.setText(temp);
//
//        }
    }

    public void startShopping(View v){

        Intent in = new Intent(ShopDetails.this, BarcodeScannerActivity.class);
        in.putExtra("Type", "Product_Scan");
        startActivity(in);

    }
}
