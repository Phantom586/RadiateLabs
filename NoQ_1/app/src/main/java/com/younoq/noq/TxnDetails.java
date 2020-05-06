package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TxnDetails extends AppCompatActivity {

    private final String TAG = "TxnDetailsActivity";
    TextView tv_amt_paid, tv_time, tv_paid_to, tv_paid_via, tv_store_addr, tv_receipt_no;
    private RecyclerView recyclerView;
    private Bundle txnData;
    private ArrayList<String> txnDetails;
    private String productsString;
    private SimpleDateFormat inputDateFormat, outputDateFormat, outputTimeFormat;
    private Date date;
    private JSONArray jsonArray;
    private JSONObject jObj;
    private List<Product> productList;
    private TxnDetailsAdapter txnDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txn_details);

        inputDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
        outputDateFormat = new SimpleDateFormat("MMM dd");
        outputTimeFormat = new SimpleDateFormat("HH:mm a");

        productList = new ArrayList<>();

        recyclerView = findViewById(R.id.td_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tv_amt_paid = findViewById(R.id.td_amt_paid);
        tv_time = findViewById(R.id.td_time);
        tv_paid_to = findViewById(R.id.td_paid_to);
        tv_paid_via = findViewById(R.id.td_pay_mode);
        tv_store_addr = findViewById(R.id.td_store_addr);
        tv_receipt_no = findViewById(R.id.td_receipt_no);


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
        tv_paid_to.setText(txnDetails.get(4));

        // Receipt No.
        tv_receipt_no.setText(txnDetails.get(0));

        final String store_addr = txnDetails.get(5) + ", " + txnDetails.get(6);
        tv_store_addr.setText(store_addr);

        final String timestamp = txnDetails.get(2);

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

        tv_paid_via.setText(txnDetails.get(3));

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
                                jObj.getString("total_rp"),
                                jObj.getString("retailer_price"),
                                jObj.getString("our_price"),
                                jObj.getString("total_discount"),
                                jObj.getString("no_of_items"),
                                jObj.getString("has_image"),
                                "0",
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
