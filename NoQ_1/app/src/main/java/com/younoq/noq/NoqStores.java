package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoqStores extends AppCompatActivity {

    private final String TAG = "NoqStoresActivity";

    JSONArray jsonArray1,  jsonArray2;
    JSONObject jobj11, jobj12;
    public String User_number;

    ProgressBar progressBar;
    RecyclerView recyclerView;
    StoresAdapter storesAdapter;
    List<Store> StoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noq_stores);

        progressBar = findViewById(R.id.ns_spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

        recyclerView = findViewById(R.id.ns_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        StoreList = new ArrayList<>();

        Intent in = getIntent();
        User_number = in.getStringExtra("Phone");

        retrieve_current_stores();
    }

    public void retrieve_current_stores(){

        final String type = "retrieve_stores_data";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type).get();
//            Log.d(TAG, "Result : " + res);

            jsonArray1 = new JSONArray(res);
            jobj11 = jsonArray1.getJSONObject(0);
            if(!jobj11.getBoolean("error")){

                jobj12 = jsonArray1.getJSONObject(1);
                jsonArray2 = jobj12.getJSONArray("data");
//                Log.d(TAG, "Stores Array Hopefully : "+jsonArray2+ " length : "+jsonArray2.length());
                for (int i = 0; i < jsonArray2.length(); i++){

                    JSONObject obj = jsonArray2.getJSONObject(i);

                    StoreList.add(
                            new Store(
                                    obj.get("store_id").toString(),
                                    obj.get("store_name").toString(),
                                    obj.get("store_addr").toString(),
                                    obj.get("store_city").toString(),
                                    obj.get("pin").toString(),
                                    obj.get("store_state").toString(),
                                    obj.get("store_country").toString()
                            )
                    );
                    // testing the Data Acquired
//                    System.out.println(obj.get("store_id"));
//                    System.out.println(obj.get("store_name"));
//                    System.out.println(obj.get("store_addr"));
//                    System.out.println(obj.get("store_city"));
//                    System.out.println(obj.get("pin"));
//                    System.out.println(obj.get("store_state"));
//                    System.out.println(obj.get("store_country"));

                }

                storesAdapter = new StoresAdapter(this, StoreList);
                recyclerView.setAdapter(storesAdapter);

            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void onContinue(View view) {

        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressBar.setVisibility(View.GONE);

                Intent intent = new Intent(NoqStores.this, MyProfile.class);
                intent.putExtra("Phone", User_number);
                intent.putExtra("activity", "NS");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        }, 800);

    }
}
