package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProductsCategory extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;
    ImageView im_go_to_cart;
    final private String TAG = "ProductsCategory";
    JSONArray jsonArray, jsonArray1;
    List<Category> categoriesList;
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    private String shoppingMethod;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_category);

        saveInfoLocally = new SaveInfoLocally(this);
        im_go_to_cart = findViewById(R.id.pc_cart);
        recyclerView = findViewById(R.id.pc_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DBHelper(this);

        Intent in = getIntent();
        shoppingMethod = in.getStringExtra("shoppingMethod");

        categoriesList = new ArrayList<>();

        retrieve_categories();

    }

    void retrieve_categories() {

        final String store_id = saveInfoLocally.get_store_id();
        final String type= "retrieve_categories";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, store_id).get();
            Log.d(TAG, res);

            jsonArray = new JSONArray(res);

            for(int i = 0; i < jsonArray.length(); i++){

                jsonArray1 = jsonArray.getJSONArray(i);
//                Log.d(TAG, "Item - "+i+" "+jsonArray1.getString(0));
                final int times_purchased = Integer.parseInt(jsonArray1.getString(0));
                categoriesList.add(
                        new Category(
                                jsonArray1.getString(0),
                                jsonArray1.getString(1),
                                times_purchased
                        )
                );

            categoryAdapter = new CategoryAdapter(this, categoriesList, shoppingMethod);
            recyclerView.setAdapter(categoryAdapter);

            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        dbHelper = new DBHelper(this);
        Log.d(TAG, "Shopping Method in Category :"+ shoppingMethod);
        new MaterialAlertDialogBuilder(this)
            .setTitle("Do you want to Exit "+shoppingMethod+" Shopping?")
            .setMessage(R.string.bs_exit_in_store_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.bs_exit_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbHelper.Delete_all_rows();
                    // Retrieving the Store Shopping methods related Info, from SharedPreferences.
                    final boolean in_store = saveInfoLocally.getIs_InStore();
                    final boolean takeaway = saveInfoLocally.getIs_Takeaway();
                    final boolean home_delivery = saveInfoLocally.getIs_Home_Delivery();

                    Intent in = new Intent(ProductsCategory.this, ChooseShopType.class);
                    in.putExtra("in_store", in_store);
                    in.putExtra("takeaway", takeaway);
                    in.putExtra("home_delivery", home_delivery);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                }
            })
            .setNegativeButton(R.string.bs_exit_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(BarcodeScannerActivity.this, "Don't Exit", Toast.LENGTH_SHORT).show();
                }
            })
            .show();
    }

    public void Go_to_Basket(View view) {

        Intent in = new Intent(this, CartActivity.class);
        in.putExtra("comingFrom", "ProductCategory");
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);

    }
}
