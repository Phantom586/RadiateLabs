package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.adapters.TxnAdapter;
import com.younoq.noqfuelstation.classes.Txn;
import com.younoq.noqfuelstation.models.BackgroundWorker;
import com.younoq.noqfuelstation.models.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LastFiveTxns extends AppCompatActivity {

    private JSONArray jsonArray1,  jsonArray2, productsArray;
    private final String TAG = "LastFiveTxnsActivity";
    private RecyclerView recyclerView;
    private JSONObject jobj11, jobj12;
    private TxnAdapter txnAdapter;
    private List<Txn> txnList;
    private Logger logger;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_five_txns);

        recyclerView = findViewById(R.id.lft_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        logger = new Logger(this);
        txnList = new ArrayList<>();

        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","onCreate() Func. called\n");

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");

        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","Values received from Intent Phone : "+phone+"\n");

        retrieve_last_five_txns();

    }

    private void retrieve_last_five_txns() {

        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "retrieve_last_five_txns()","retrieve_last_five_txns() Func. called\n");

        try {

            final String type = "retrieve_last_txns";
            String res = new BackgroundWorker(this).execute(type, phone).get();
            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "retrieve_last_five_txns()","BackgroundWorker 'retrieve_last_txns' called Result : "+res+"\n");
            Log.d(TAG, "Last Five Txns : "+res);

            jsonArray1 = new JSONArray(res);
            jobj11 = jsonArray1.getJSONObject(0);
            if(!jobj11.getBoolean("error")){

                jobj12 = jsonArray1.getJSONObject(1);
                jsonArray2 = jobj12.getJSONArray("data");

                for (int i = 0; i < jsonArray2.length(); i++){

                    JSONObject obj = jsonArray2.getJSONObject(i);

                    if (productsArray == null)
                        productsArray = new JSONArray();

                    txnList.add(
                            new Txn(
                                    obj.get("receipt_no").toString(),
                                    obj.get("payment_mode").toString(),
                                    obj.get("referral_used").toString(),
                                    obj.get("timestamp").toString(),
                                    obj.get("total_retailers_price").toString(),
                                    obj.get("final_amt").toString(),
                                    obj.get("store_addr").toString(),
                                    obj.get("store_name").toString(),
                                    obj.get("store_city").toString(),
                                    obj.get("store_state").toString(),
                                    obj.get("order_type").toString()
                            )
                    );

                }

                txnAdapter = new TxnAdapter(this, txnList);
                recyclerView.setAdapter(txnAdapter);

                System.out.println("After Setting Adapter");
                System.out.println("Txn List : "+txnList);

            } else {

                /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "retrieve_last_five_txns()","No Txns Found.\n");
                Toast.makeText(this, "There are No Transactions From your Account.", Toast.LENGTH_SHORT).show();

            }


        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "retrieve_last_five_txns()",e.getMessage());
        }

    }

    public void go_back(View view) { super.onBackPressed(); }
}