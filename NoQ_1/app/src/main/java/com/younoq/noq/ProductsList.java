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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProductsList extends AppCompatActivity {

    TextView tv_store_name;
    String store_id, store_name;
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
        saveInfoLocally = new SaveInfoLocally(this);
        recyclerView = findViewById(R.id.apl_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();

        store_id = saveInfoLocally.get_store_id();
        store_name = saveInfoLocally.getStoreName() +", "+ saveInfoLocally.getStoreAddress();
        tv_store_name.setText(store_name);

        retrieve_products_list();

    }

    void retrieve_products_list() {

        final String type = "retrieve_products_list";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, store_id).get();
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
                                "",
                                jsonArray1.getString(7),
                                jsonArray1.getString(10),
                                "",
                                "0",
                                jsonArray1.getString(15),
                                jsonArray1.getString(16)
                        )
                );

            }

            productListAdapter = new ProductListAdapter(this, productList);
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
        startActivity(in);
    }

    public void Go_to_Profile(View view) {
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }
}
