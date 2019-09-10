package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProductDetails extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
    ImageView im, im1, im2;
    Button add_to_basket, cancel;
    public static String[] data;

    Temp_Basket temp_basket = new Temp_Basket(this);

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
        im1 = findViewById(R.id.pd_im_1);
        im2 = findViewById(R.id.pd_im_2);
        add_to_basket = findViewById(R.id.pd_add_to_basket);
        cancel = findViewById(R.id.pd_cancel);

        Intent in = getIntent();
        String res = in.getStringExtra(BarcodeScannerActivity.RESULT);
        String img_name = in.getStringExtra(BarcodeScannerActivity.BARCODE);
        img_name += ".png";

        String url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/images/"+img_name;
//        String url = "https://picsum.photos/300";
        final String TAG = "ProductDetails";
        Log.d(TAG, "Product Details : "+res);

        Glide.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(im);

        try{

            JSONArray jsonArray = new JSONArray(res);
            JSONObject jobj = jsonArray.getJSONObject(1);
            tv1.setText(jobj.getString("Barcode"));
            tv2.setText(jobj.getString("Product_Name"));
            tv3.setText(jobj.getString("MRP"));
            tv4.setText(jobj.getString("Retailers_Price"));
            tv5.setText(jobj.getString("Our_Price"));
            tv6.setText(jobj.getString("Total_Discount"));

        }catch(Exception e){
            e.printStackTrace();
        }

//        if (!res.equals("")) {
//
//            data = res.split("-", 7);
//            tv1.setText(data[0]);
//            tv2.setText(data[1]);
//            tv3.setText(data[2]);
//            tv4.setText(data[3]);
//            tv5.setText(data[4]);
//            tv6.setText(data[5]);
//        }

    }

    public void Add_To_Basket(View view) {

        temp_basket.add_to_temp_basket(data);
        Toast.makeText(this, "Product Added to Basket Successfully", Toast.LENGTH_SHORT).show();

    }

    public void OnCancel(View view) {

        Intent in = new Intent(this, BarcodeScannerActivity.class);
        in.putExtra("Type", "Product_Scan");
        startActivity(in);

    }
}
