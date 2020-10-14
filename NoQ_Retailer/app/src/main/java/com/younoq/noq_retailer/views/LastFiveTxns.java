package com.younoq.noq_retailer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.younoq.noq_retailer.R;
import com.younoq.noq_retailer.adapters.TxnAdapter;
import com.younoq.noq_retailer.classes.Txn;
import com.younoq.noq_retailer.models.BackgroundWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class LastFiveTxns extends AppCompatActivity {

    TextView tv;
    public RecyclerView recyclerView;
    public String phone;
    private final String TAG = "LastFiveTxnsActivity";
    JSONArray jsonArray1,  jsonArray2, productsArray;
    JSONObject jobj11, jobj12;
    List<Txn> txnList;
    TxnAdapter txnAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_five_txns);

        tv = findViewById(R.id.lft_tv1);
        recyclerView = findViewById(R.id.lft_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txnList = new ArrayList<>();

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");

        retrieve_last_five_txns();

    }

    private void retrieve_last_five_txns() {

        try {

            final String type = "retrieve_last_txns";
            String res = new BackgroundWorker(this).execute(type, phone).get();
            Log.d(TAG, "Last Five Txns : "+res);

            jsonArray1 = new JSONArray(res);
            jobj11 = jsonArray1.getJSONObject(0);
            if(!jobj11.getBoolean("error")){
                System.out.println("Output Is True");

                jobj12 = jsonArray1.getJSONObject(1);
                jsonArray2 = jobj12.getJSONArray("data");
//                Log.d(TAG, "Stores Array Hopefully : "+jsonArray2+ " length : "+jsonArray2.length());
                for (int i = 0; i < jsonArray2.length(); i++){

                    JSONObject obj = jsonArray2.getJSONObject(i);
                    // Extracting the Products Array from the result.
                    productsArray = obj.getJSONArray("products");

                    txnList.add(
                            new Txn(
                                    obj.get("receipt_no").toString(),
                                    obj.get("payment_mode").toString(),
                                    obj.get("referral_used").toString(),
                                    obj.get("timestamp").toString(),
                                    obj.get("total_items").toString(),
                                    obj.get("final_amt").toString(),
                                    obj.get("store_addr").toString(),
                                    obj.get("store_name").toString(),
                                    obj.get("store_city").toString(),
                                    obj.get("store_state").toString(),
                                    obj.get("order_type").toString(),
                                    "",
                                    Integer.parseInt(obj.get("delivery_duration").toString()),
                                    productsArray
                            )
                    );

                }

                txnAdapter = new TxnAdapter(this, txnList);
                recyclerView.setAdapter(txnAdapter);

                System.out.println("After Setting Adapter");
                System.out.println("Txn List : "+txnList);

            } else {

                tv.setVisibility(View.GONE);
                Toast.makeText(this, "There are No Transactions From your Account.", Toast.LENGTH_SHORT).show();

            }


        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void go_back(View view) {

        super.onBackPressed();

    }
}
