package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProductsList extends AppCompatActivity {

    TextView tv_store_name, tv_category_name;
    String store_id, store_name, category_name, shoppingMethod, coming_from;
    private String TAG ="ProductList";
    SaveInfoLocally saveInfoLocally;
    JSONArray jsonArray, jsonArray1;
    JSONObject jobj;
    List<Product> productList;
    RecyclerView recyclerView;
    ProductListAdapter productListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        tv_store_name = findViewById(R.id.apl_store_name);
        tv_category_name = findViewById(R.id.apl_category_name);
        saveInfoLocally = new SaveInfoLocally(this);
        recyclerView = findViewById(R.id.apl_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

        productList = new ArrayList<>();

        Intent in= getIntent();
        category_name = in.getStringExtra("category_name");
        shoppingMethod = in.getStringExtra("shoppingMethod");
        coming_from = in.getStringExtra("coming_from");
        Log.d(TAG, "Coming From : "+coming_from);

        store_id = saveInfoLocally.get_store_id();
        store_name = saveInfoLocally.getStoreName() +", "+ saveInfoLocally.getStoreAddress();
        tv_store_name.setText(store_name);

        tv_category_name.setText(category_name);

        retrieve_products_list();

    }

    void retrieve_products_list() {

        final String type = "retrieve_products_list";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, store_id, category_name).get();
//            Log.d(TAG, "Products list : "+res);

            jsonArray = new JSONArray(res);

            for(int i = 0; i < jsonArray.length(); i++){

                jsonArray1 = jsonArray.getJSONArray(i);
//                Log.d(TAG, "Item - "+i+" "+jsonArray1.getString(0));
                productList.add(
                        new Product(
                                0,
                                jsonArray1.getString(1),
                                jsonArray1.getString(0),
                                jsonArray1.getString(3),
                                jsonArray1.getString(5),
                                "0",
                                jsonArray1.getString(7),
                                jsonArray1.getString(10),
                                "0",
                                "0",
                                jsonArray1.getString(15),
                                jsonArray1.getString(16),
                                jsonArray1.getString(17)
                        )
                );

            }

            productListAdapter = new ProductListAdapter(this, productList, shoppingMethod);
            recyclerView.setAdapter(productListAdapter);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void Go_to_Basket(View view) {
        Intent in = new Intent(this, CartActivity.class);
        in.putExtra("category_name", category_name);
        in.putExtra("comingFrom", "ProductList");
        Log.d(TAG, "Shopping Method in ProductList :"+ shoppingMethod);
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);
    }

    public void Go_to_Profile(View view) {
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, ProductsCategory.class);
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);
//        if(coming_from.equals("Cart")){
//            Intent in = new Intent(this, ProductsCategory.class);
//            in.putExtra("shoppingMethod", shoppingMethod);
//            startActivity(in);
//        } else {
//            super.onBackPressed();
//        }
    }
}
