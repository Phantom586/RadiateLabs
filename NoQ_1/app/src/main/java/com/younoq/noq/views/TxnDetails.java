package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.younoq.noq.R;
import com.younoq.noq.adapters.TxnDetailsAdapter;
import com.younoq.noq.classes.Product;
import com.younoq.noq.models.SaveInfoLocally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class TxnDetails extends AppCompatActivity {

    private final String TAG = "TxnDetailsActivity";
    TextView tv_amt_paid, tv_time, tv_store_name, tv_paid_via, tv_receipt_no, tv_you_saved, tv_order_type, tv_order_msg;
    private RecyclerView recyclerView;
    private Bundle txnData;
    private ArrayList<String> txnDetails;
    private String productsString, delivery_dur;
    private SimpleDateFormat inputDateFormat, outputDateFormat, outputTimeFormat;
    private Date date;
    private JSONArray jsonArray;
    private JSONObject jObj;
    private List<Product> productList;
    private TxnDetailsAdapter txnDetailsAdapter;
    private LinearLayout linearLayout_amt_paid;
    private SaveInfoLocally saveInfoLocally;
    private int delivery_duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txn_details);

        inputDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
        outputDateFormat = new SimpleDateFormat("MMM dd");
        outputTimeFormat = new SimpleDateFormat("hh:mm a");

        productList = new ArrayList<>();

        recyclerView = findViewById(R.id.td_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        saveInfoLocally = new SaveInfoLocally(this);

        tv_amt_paid = findViewById(R.id.td_final_amt);
        tv_time = findViewById(R.id.td_timestamp);
        tv_store_name =findViewById(R.id.td_store_name);
        tv_paid_via = findViewById(R.id.td_pay_mode);
        tv_receipt_no = findViewById(R.id.td_receipt_no);
        tv_you_saved = findViewById(R.id.td_you_saved);
        tv_order_type = findViewById(R.id.td_order_type);
        tv_order_msg = findViewById(R.id.td_order_msg);
        linearLayout_amt_paid = findViewById(R.id.td_linear_layout_amt_paid);

        // Retrieving the Txn Data from the Intent.
        txnData = getIntent().getExtras();
        // show Transaction Data.
        displayTxnData(txnData);

    }

    private void displayTxnData(Bundle txnData) {

        // Extracting Txn Details List from the Bundle.
        txnDetails = txnData.getStringArrayList("txnDetail");
        // Extracting Products String from Bundle.
        productsString = txnData.getString("txnProductArray");

        // Setting Txn Details.
        final String amt_paid = "₹" + txnDetails.get(1);
        tv_amt_paid.setText(amt_paid);

        // Store Name
        final String store_name = txnDetails.get(5) +", "+ txnDetails.get(6);
        tv_store_name.setText(store_name);

        // Order Type
        final String order_type = txnDetails.get(9);

        if(order_type.equals("InStore")){

            tv_order_type.setText(R.string.ps_in_store);
            tv_order_msg.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            // Converting 10dp and 20dp in pixels.
            final float marginTop = 10f, marginBottom = 20f;
            Resources r = getResources();
            final float mTop = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    marginTop,
                    r.getDisplayMetrics()
            );
            final float mBottom = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    marginBottom,
                    r.getDisplayMetrics()
            );
            layoutParams.setMargins(0, Math.round(mTop), 0 , Math.round(mBottom));
            linearLayout_amt_paid.setLayoutParams(layoutParams);

        }
        else if(order_type.equals("Takeaway")){

            tv_order_msg.setVisibility(View.VISIBLE);
            tv_order_type.setText(order_type);
            tv_order_msg.setText(R.string.ps_takeaway_desc);

        }
        else if(order_type.equals("HomeDelivery")){

            // Retrieving the Delivery Duration.
            delivery_duration = Integer.parseInt(txnDetails.get(10));
            Log.d(TAG, "Delivery Duration : "+delivery_duration);
            String timeUnit = "";
            int delivery_time_hours = 0, delivery_time_mins = 0;
            delivery_dur = "Delivery in ";

            if(delivery_duration >= 0 && delivery_duration < 60){

                delivery_dur += delivery_duration + " mins";

            }
            else if(delivery_duration >= 60){

                delivery_time_hours = delivery_duration / 60;
                delivery_time_mins = delivery_duration % 60;

                if(delivery_time_mins == 0){
                    delivery_dur += delivery_time_hours + " hour";
                } else {
                    delivery_dur += delivery_time_hours + " hr " + delivery_time_mins + " mins";
                }
            }

            tv_order_msg.setVisibility(View.VISIBLE);
            tv_order_type.setText(R.string.ps_home_delivery);
            tv_order_msg.setText(delivery_dur);

        }

        // You Saved
        final String you_saved = txnDetails.get(2);
        tv_you_saved.setText(you_saved);

        // Receipt No.
        tv_receipt_no.setText(txnDetails.get(0));

        final String timestamp = txnDetails.get(3);

        try {
            // Converting String Date to Date Object.
            date = inputDateFormat.parse(timestamp);
            Log.d(TAG, "Input Date Parse : "+date);

            final String time = outputDateFormat.format(date) + ", " + outputTimeFormat.format(date);
            tv_time.setText(time);
            Log.d(TAG, " "+time);

//            final String month_date = outputDateFormat.format(date);
//            tv_month_date.setText(month_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String pay_method = txnDetails.get(4);
        if(pay_method.equals("[Referral_Used]"))
            pay_method = "Bonus";
        tv_paid_via.setText(pay_method);

        // Creating List of Products.
        try {

            jsonArray = new JSONArray(productsString);
            for (int i = 0; i < jsonArray.length(); i++) {

                jObj = jsonArray.getJSONObject(i);

                productList.add(
                        new Product(
                                0,
                                "0",
                                jObj.getString("barcode"),
                                jObj.getString("p_name"),
                                "0",
                                "0",
                                jObj.getString("total_rp"),
                                jObj.getString("retailer_price"),
                                jObj.getString("our_price"),
                                jObj.getString("total_discount"),
                                jObj.getString("no_of_items"),
                                jObj.getString("has_image"),
                                "0",
                                "",
                                ""
                        )
                );

            }

            txnDetailsAdapter = new TxnDetailsAdapter(this, productList);
            recyclerView.setAdapter(txnDetailsAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void go_back(View view) {

        super.onBackPressed();

    }
}
