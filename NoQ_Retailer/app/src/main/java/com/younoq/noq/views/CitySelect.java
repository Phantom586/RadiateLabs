package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.younoq.noq.R;
import com.younoq.noq.adapters.CityAdapter;
import com.younoq.noq.classes.City;
import com.younoq.noq.models.AwsBackgroundWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class CitySelect extends AppCompatActivity {

    private CityAdapter cityAdapter;
    private RecyclerView recyclerView;
    private List<City> citiesList;
    private final String TAG = "CitySelect";
    private JSONArray jsonArray1;
    private JSONObject jobj;
    private String phone, isDirectLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);

        recyclerView = findViewById(R.id.city_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        citiesList = new ArrayList<>();

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        isDirectLogin = in.getStringExtra("isDirectLogin");

        retrieve_cities();

    }

    private void retrieve_cities() {

        final String type = "retrieve_cities";
        try {

            final String res = new AwsBackgroundWorker(this).execute(type).get();
            Log.d(TAG, "Cities List : "+res);

            jsonArray1 = new JSONArray(res);
            for(int index = 0; index < jsonArray1.length(); index++){
               jobj = jsonArray1.getJSONObject(index);

               Log.d(TAG, "City Name : "+ jobj.getString("city"));

               citiesList.add(
                      new City(
                              jobj.getString("city"),
                              jobj.getString("exists")
                      )
               );
            }

            cityAdapter = new CityAdapter(this, citiesList, phone, isDirectLogin);
            recyclerView.setAdapter(cityAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
