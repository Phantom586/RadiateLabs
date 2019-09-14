package com.example.noq_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProductAdapter adapter;

    List<Product> ProductList;
    ImageView im;
    TextView tv4;
    DBHelper dbHelper;
    public int total_amt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ProductList = new ArrayList<>();
        dbHelper = new DBHelper(this);

        tv4 = findViewById(R.id.c_tv4);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor res = dbHelper.retrieveData();
        if(res.getCount() == 0){
            Toast.makeText(this, "No Products Added Yet..", Toast.LENGTH_SHORT).show();
        } else {
            while(res.moveToNext()){
                total_amt += Integer.parseInt(res.getString(4));
                ProductList.add(
                  new Product(
                          res.getString(1),
                          res.getString(3),
                          res.getString(4),
                          res.getString(5),
                          res.getString(6),
                          res.getString(7),
                          res.getString(2)
                  ));
            }
        }

        final String amt = "₹"+total_amt;
        tv4.setText(amt);

        final String TAG = "CartActivity";
        Log.d(TAG, "Product List : "+ProductList);
        adapter = new ProductAdapter(this, ProductList);
        recyclerView.setAdapter(adapter);

//        im = findViewById(R.id.c_im);
//        tv1 = findViewById(R.id.c_tv1);
//        tv2 = findViewById(R.id.c_tv2);
//        tv3 = findViewById(R.id.c_tv3);
    }

    @Override
    protected void onStop() {
        super.onStop();
        total_amt = 0;
    }
}
