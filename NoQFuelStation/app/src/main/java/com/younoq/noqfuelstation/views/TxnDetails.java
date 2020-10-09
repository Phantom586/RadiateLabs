package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.adapters.TxnDetailsAdapter;
import com.younoq.noqfuelstation.classes.Product;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TxnDetails extends AppCompatActivity {

    private TextView tv_amt_paid, tv_time, tv_store_name, tv_paid_via, tv_receipt_no, tv_you_saved, tv_order_type, tv_order_msg;
    private SimpleDateFormat inputDateFormat, outputDateFormat, outputTimeFormat;
    private final String TAG = "TxnDetailsActivity";
    private String productsString, delivery_dur;
    private TxnDetailsAdapter txnDetailsAdapter;
    private LinearLayout linearLayout_amt_paid;
    private SaveInfoLocally saveInfoLocally;
    private ArrayList<String> txnDetails;
    private List<Product> productList;
    private RecyclerView recyclerView;
    private int delivery_duration;
    private JSONArray jsonArray;
    private JSONObject jObj;
    private Bundle txnData;
    private Date date;

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



    }

    public void go_back(View view) {
    }
}