package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StoresNoq extends AppCompatActivity {

    private JSONArray jsonArray1, jsonArray2;
    private JSONObject jobj11, jobj12;
    private List<Store> StoreList;
    private StoresAdapter storesAdapter;
    private RecyclerView recyclerView;
    private ArrayList<String> storesList;
    private Bundle storesListData;
    private String TAG = "StoresNoq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_noq);

        StoreList = new ArrayList<>();
        recyclerView = findViewById(R.id.sn_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        storesList = new ArrayList<>();
        storesListData = new Bundle();

        Intent in = getIntent();
        storesListData = in.getExtras();

        retrieve_current_stores();;

    }

    public void retrieve_current_stores(){

        storesList = storesListData.getStringArrayList("storesList");
        Log.d(TAG, "Stores String : "+storesList);

        final String type = "retrieve_stores_data";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, storesList.toString()).get();
            Log.d(TAG, "Result : " + res);

            jsonArray1 = new JSONArray(res);

            for (int i = 0; i < jsonArray1.length(); i++){

                JSONObject obj = jsonArray1.getJSONObject(i);

                StoreList.add(
                        new Store(
                                obj.get("store_id").toString(),
                                obj.get("store_name").toString(),
                                obj.get("store_addr").toString(),
                                obj.get("store_city").toString(),
                                obj.get("pin").toString(),
                                obj.get("store_state").toString(),
                                obj.get("store_country").toString(),
                                obj.get("phone_no").toString(),
                                obj.get("in_store").toString().toLowerCase().equals("true"),
                                obj.get("takeaway").toString().toLowerCase().equals("true"),
                                obj.get("home_delivery").toString().toLowerCase().equals("true")
                        )
                );

            }

            storesAdapter = new StoresAdapter(this, StoreList);
            recyclerView.setAdapter(storesAdapter);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
