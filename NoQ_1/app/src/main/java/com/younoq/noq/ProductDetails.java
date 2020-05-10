package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductDetails extends AppCompatActivity {

    TextView tv_bcode, tv_prod_name, tv_prod_mrp, tv_retailer_price, tv5, tv6, tv_prod_qty, tv_prod_status;
    public String t7, comingFrom, shoppingMethod, category_name;
    Button add_to_basket, cancel, add_more;
    ImageView im, im_go_to_cart, im_add, im_delete;
    public static String res;
    public static String b_code = " ", img_name;
    public boolean hasImage;
    public static int p_qty = 1;
    public int available_quantity = 0;
    private Bundle prodData;
    private ArrayList<String> prodDetails;
    final String TAG = "ProductDetails";
    LinearLayout ll_selective_linear_layout;

    JSONArray jsonArray;
    JSONObject jobj = null;

    DBHelper mydb;
    SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        tv_bcode = findViewById(R.id.pd_bcode);
        tv_prod_name = findViewById(R.id.pd_prod_name);
        tv_prod_mrp = findViewById(R.id.pd_prod_mrp);
        tv_retailer_price = findViewById(R.id.pd_retailers_price);
        im_go_to_cart = findViewById(R.id.pd_cart);
        add_more = findViewById(R.id.pd_add_more);
        ll_selective_linear_layout = findViewById(R.id.pd_selective_linear_layout);
        tv_prod_status = findViewById(R.id.pd_prod_status);
        im_add = findViewById(R.id.pd_add);
        im_delete = findViewById(R.id.pd_delete);
//        tv5 = findViewById(R.id.pd_tv_5);
//        tv6 = findViewById(R.id.pd_tv_6);
        tv_prod_qty = findViewById(R.id.pd_qty);
        im = findViewById(R.id.pd_prod_img);
        add_to_basket = findViewById(R.id.pd_add_to_basket);
//        cancel = findViewById(R.id.pd_cancel);
        prodDetails = new ArrayList<>();

        mydb = new DBHelper(this);
        saveInfoLocally = new SaveInfoLocally(this);

        // Fetching Details from the Intent.
        Intent in = getIntent();
        comingFrom = in.getStringExtra("comingFrom");
        shoppingMethod = in.getStringExtra("shoppingMethod");

        if(comingFrom.equals("BarcodeScan")){
            res = in.getStringExtra("result");
        } else if(comingFrom.equals("ProductList")){
            // Extracting Product Details List from the Bundle.
            prodData = in.getExtras();
        }

        retrieve_product_details();

        im_go_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in  = new Intent(v.getContext(), CartActivity.class);;
                if(shoppingMethod.equals("InStore")){
                    in.putExtra("shoppingMethod", shoppingMethod);
                } else if(shoppingMethod.equals("Takeaway")){
                    in.putExtra("comingFrom", "ProductDetails");
                    in.putExtra("category_name", category_name);
                    in.putExtra("shoppingMethod", shoppingMethod);
                }
                startActivity(in);
            }
        });

        im_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p_qty < available_quantity){
                    p_qty += 1;
                    tv_prod_qty.setText(String.valueOf(p_qty));
                } else {
                    Toast.makeText(v.getContext(), "You have reached the max. available qty for this product.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        im_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p_qty > 1){
                    p_qty -= 1;
                    tv_prod_qty.setText(String.valueOf(p_qty));
                }  else {
                    Toast.makeText(v.getContext(), "You have reached the minimum limit for this Item.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        add_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in;
                if(shoppingMethod.equals("InStore")) {
                    in  = new Intent(v.getContext(), BarcodeScannerActivity.class);
                    in.putExtra("Type", "Product_Scan");
                } else if(shoppingMethod.equals("Takeaway")) {
                    in  = new Intent(ProductDetails.this, ProductsList.class);
                    in.putExtra("comingFrom", "ProductDetails");
                    in.putExtra("category_name", category_name);
                    in.putExtra("shoppingMethod", shoppingMethod);
                } else {
                    in = new Intent();
                }
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
            }
        });

        t7 = Integer.toString(p_qty);
        tv_prod_qty.setText(t7);

    }

    void retrieve_product_details() {

        final String sid = saveInfoLocally.get_store_id();

        if(shoppingMethod.equals("InStore"))
            add_more.setText(R.string.ca_scan);

        if(comingFrom.equals("BarcodeScan")) {

            try{

                jsonArray = new JSONArray(res);
                jobj = jsonArray.getJSONObject(1);
                b_code = jobj.getString("Barcode");
                tv_bcode.setText(b_code);
                tv_prod_name.setText(jobj.getString("Product_Name"));
                String temp = "MRP ₹"+jobj.getString("MRP");
                tv_prod_mrp.setText(temp);
                temp = "₹"+jobj.getString("Retailers_Price");
                tv_retailer_price.setText(temp);

                available_quantity = Integer.parseInt(jobj.getString("quantity"));

                img_name = b_code;
//            temp = "₹"+jobj.getString("Our_Price");
//            tv5.setText(temp);
//            temp = "₹"+jobj.getString("Total_Discount");
//            tv6.setText(temp);
                temp = jobj.getString("has_image");
                hasImage = temp.toLowerCase().equals("true");

            }catch(Exception e){
                e.printStackTrace();
            }

        } else if(comingFrom.equals("ProductList")){

            // Extracting Products String from Bundle.
            prodDetails = prodData.getStringArrayList("productDetails");
            Log.d(TAG, "Product Details : "+prodDetails);

            // Marking Sure that there is some content in the prodDetails ArrayList.
            if(prodDetails.size() > 0) {

                // Retrieving the Product Quantity from the Local Database.
                available_quantity = Integer.parseInt(prodDetails.get(9));

                // Setting the Barcode's value.
                b_code = prodDetails.get(1);
                tv_bcode.setText(prodDetails.get(1));
                tv_prod_name.setText(prodDetails.get(2));
                String temp = "MRP ₹"+prodDetails.get(3);
                tv_prod_mrp.setText(temp);
                temp = "₹"+ prodDetails.get(4);
                tv_retailer_price.setText(temp);

                img_name = prodDetails.get(1);
                // Converting String to Boolean.
                final String tmp = prodDetails.get(7).toLowerCase();
                hasImage = tmp.equals("true");
                category_name = prodDetails.get(8);

            }

        }

        if(available_quantity == 0){
            ll_selective_linear_layout.setVisibility(View.GONE);
            add_to_basket.setVisibility(View.INVISIBLE);
            tv_prod_status.setVisibility(View.VISIBLE);
        }

        img_name += ".png";

//        Log.d(TAG, "Has Image : "+hasImage);
//
//        // If Product has Image, only Then show the Image.
        if (hasImage) {

            String url;
            if(sid.equals("3")){
                url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/school_images/"+img_name;
                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(im);
            } else {
                url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/images/" + img_name;
                Glide.with(this)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(im);
            }

        }

//        String url = "https://picsum.photos/300";
//        Log.d(TAG, "Product Details : "+res);

    }

    @Override
    public void onBackPressed() {
        Intent in;
        if(shoppingMethod.equals("InStore")){
            in  = new Intent(ProductDetails.this, BarcodeScannerActivity.class);
            in.putExtra("Type", "Product_Scan");
        } else if(shoppingMethod.equals("Takeaway")){
            in  = new Intent(ProductDetails.this, ProductsList.class);
            in.putExtra("coming_from", "ProductDetails");
            in.putExtra("shoppingMethod", shoppingMethod);
            in.putExtra("category_name", category_name);
        } else {
            in = new Intent();
        }
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }

    public void Add_To_Basket(View view) {

        final String sid = saveInfoLocally.get_store_id();
        Log.d(TAG, "Store Id : "+sid+" Barcode : "+b_code);
        boolean product_exists = false;

        if(!b_code.equals(" ")){
            product_exists = mydb.product_exists(b_code, sid, shoppingMethod);
//            Log.d(TAG, "Product Exists : "+product_exists);
        } else {
            Toast.makeText(this, "Some Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
        }
        if(product_exists){

            boolean isUpdated = mydb.update_product(b_code, sid, p_qty, shoppingMethod);
            Log.d(TAG, "isUpdated : "+isUpdated);
            if(isUpdated){
                Toast.makeText(this, "Product Added to Basket Successfully", Toast.LENGTH_SHORT).show();
                // Resetting the Value of Product_Quantity as the Product has been added to basket.
                p_qty = 1;
                tv_prod_qty.setText(String.valueOf(p_qty));
            } else {
                Toast.makeText(this, "Error!! Kindly Try Again..", Toast.LENGTH_SHORT).show();
            }

        } else {

            boolean isInserted;

            if(comingFrom.equals("BarcodeScan")){
                isInserted = mydb.insertData(res, sid, p_qty, shoppingMethod);
            }
            else if(comingFrom.equals("ProductList")){
                Log.d(TAG, "Product Details  : "+prodDetails);
                isInserted = mydb.insertProductData(prodDetails, p_qty);

            } else {
                isInserted = false;
            }
            Log.d(TAG, "isInserted : "+isInserted);
            if (isInserted){
                Toast.makeText(this, "Product Added to Basket Successfully", Toast.LENGTH_SHORT).show();
                // Resetting the Value of Product_Quantity as the Product has been added to basket.
                p_qty = 1;
                tv_prod_qty.setText(String.valueOf(p_qty));
            }else{
                Toast.makeText(this, "Some Problem Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void Go_Back(View view) {
        super.onBackPressed();
    }
}
