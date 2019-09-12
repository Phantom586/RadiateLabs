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
    TextView tv1, tv2, tv3;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ProductList = new ArrayList<>();
        dbHelper = new DBHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor res = dbHelper.retrieveData();
        if(res.getCount() == 0){
            Toast.makeText(this, "No Products Added Yet..", Toast.LENGTH_SHORT).show();
        } else {
            while(res.moveToNext()){
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

        final String TAG = "CartActivity";
        Log.d(TAG, "Product List : "+ProductList);
        adapter = new ProductAdapter(this, ProductList);
        recyclerView.setAdapter(adapter);

//        im = findViewById(R.id.c_im);
//        tv1 = findViewById(R.id.c_tv1);
//        tv2 = findViewById(R.id.c_tv2);
//        tv3 = findViewById(R.id.c_tv3);
    }
}
