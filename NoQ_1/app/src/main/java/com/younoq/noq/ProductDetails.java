package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProductDetails extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
    public String t7;
    Button add_to_basket, cancel;
    ImageView im;
    public static String res;
    public static String b_code = " ";
    public static int p_qty = 1;
    final String TAG = "ProductDetails";

    JSONArray jsonArray;
    JSONObject jobj = null;

    DBHelper mydb;
    SharedPreferences sharedPreferences;
    SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        tv1 = findViewById(R.id.pd_tv_1);
        tv2 = findViewById(R.id.pd_tv_2);
        tv3 = findViewById(R.id.pd_tv_3);
        tv4 = findViewById(R.id.pd_tv_4);
        tv5 = findViewById(R.id.pd_tv_5);
        tv6 = findViewById(R.id.pd_tv_6);
        tv7 = findViewById(R.id.pd_tv_7);
        im = findViewById(R.id.pd_im);
        add_to_basket = findViewById(R.id.pd_add_to_basket);
        cancel = findViewById(R.id.pd_cancel);

        mydb = new DBHelper(this);
        saveInfoLocally = new SaveInfoLocally(this);

        Intent in = getIntent();
        res = in.getStringExtra(BarcodeScannerActivity.RESULT);
        String img_name = in.getStringExtra(BarcodeScannerActivity.BARCODE);
        img_name += ".png";

        sharedPreferences = this.getSharedPreferences("LoginDetails", 0);
        final String sid = sharedPreferences.getString("Store_id", "");

        String url;
        if(sid.equals("3")){
            url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/school_images/"+img_name;
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(im);
        } else {
            url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/images/" + img_name;
            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(im);
        }

//        String url = "https://picsum.photos/300";
        Log.d(TAG, "Product Details : "+res);



        try{

            jsonArray = new JSONArray(res);
            jobj = jsonArray.getJSONObject(1);
            b_code = jobj.getString("Barcode");
            tv1.setText(b_code);
            tv2.setText(jobj.getString("Product_Name"));
            String temp = "₹"+jobj.getString("MRP");
            tv3.setText(temp);
            temp = "₹"+jobj.getString("Retailers_Price");
            tv4.setText(temp);
            temp = "₹"+jobj.getString("Our_Price");
            tv5.setText(temp);
            temp = "₹"+jobj.getString("Total_Discount");
            tv6.setText(temp);

        }catch(Exception e){
            e.printStackTrace();
        }

        t7 = Integer.toString(p_qty);
        tv7.setText(t7);

    }

    @Override
    public void onBackPressed() {
        Intent in  = new Intent(ProductDetails.this, BarcodeScannerActivity.class);
        in.putExtra("Type", "Product_Scan");
        in.putExtra("activity", "UCA");
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }

    public void Add_To_Basket(View view) {

        final String sid = saveInfoLocally.get_store_id();
//        Log.d(TAG, "Store Id : "+sid);
        boolean product_exists = false;

        if(!b_code.equals(" ")){
            product_exists = mydb.product_exists(b_code);
//            Log.d(TAG, "Product Exists : "+product_exists);
        } else {
            Toast.makeText(this, "Some Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
        }
        if(product_exists){

            boolean isUpdated = mydb.update_product(b_code);
            Log.d(TAG, "isUpdated : "+isUpdated);
//            if(isUpdated){
//                Toast.makeText(this, "Product Added to Basket Successfully", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Error!! Kindly Try Again..", Toast.LENGTH_SHORT).show();
//            }

        } else {

            boolean isInserted = mydb.insertData(res, sid, p_qty);
            Log.d(TAG, "isInserted : "+isInserted);
//            if (isInserted){
//                Toast.makeText(this, "Product Added to Basket Successfully", Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(this, "Some Problem Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
//            }

        }

//        Intent in = new Intent(this, CartActivity.class);
//        startActivity(in);

    }

//    public void OnCancel(View view) {
//
//        Intent in = new Intent(this, BarcodeScannerActivity.class);
//        in.putExtra("Type", "Product_Scan");
//        startActivity(in);
//
//    }

//    public void Add_Qty(View view) {
//        p_qty += 1;
//        t7 = Integer.toString(p_qty);
//        tv7.setText(t7);
//    }

//    public void Sub_qty(View view) {
//        if(p_qty > 0) {
//            p_qty -= 1;
//        }else{
//            Toast.makeText(this, "You've reached Minimum limit for this Product.", Toast.LENGTH_SHORT).show();
//        }
//        t7 = Integer.toString(p_qty);
//        tv7.setText(t7);
//    }

    public void Scan_Product(View view) {
        Intent in  = new Intent(ProductDetails.this, BarcodeScannerActivity.class);
        in.putExtra("Type", "Product_Scan");
        in.putExtra("activity", "UCA");
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }
}
