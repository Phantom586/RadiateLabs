package com.younoq.noq_retailer.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.younoq.noq_retailer.R;
import com.younoq.noq_retailer.adapters.OrdersAdapter;
import com.younoq.noq_retailer.classes.Txn;
import com.younoq.noq_retailer.models.AwsBackgroundWorker;
import com.younoq.noq_retailer.models.Utilities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Orders extends AppCompatActivity {

    private SimpleDateFormat date_to_string, date_to_string_MMM, date_to_string_yy, date_to_string_dd, db_date_format, disp_single;
    private TextView tv_s_date, tv_s_month, tv_s_year, tv_e_date, tv_e_month, tv_e_year, tv_order_date, tv_total_orders;
    private String start_date = "", end_date = "", store_name = "", store_id = "";
    private JSONArray jsonArray, jsonArray1, productsArray, jsonArray2;
    private LinearLayout ll_start_date, ll_end_date, ll_order_details;
    private static final String TAG = "OrdersActivity";
    private SmartMaterialSpinner selectStoreSpinner;
    private CoordinatorLayout coordinatorLayout;
    private JSONObject jobj, jobj1, jobj2;
    private OrdersAdapter ordersAdapter;
    private RecyclerView recyclerView;
    private Button btn_fetch_order;
    private List<String> storeList;
    private String[] storeIDArray;
    private Utilities utilities;
    private List<Txn> txnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        db_date_format = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.ENGLISH);
        date_to_string = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH);
        date_to_string_MMM = new SimpleDateFormat("MMM", Locale.ENGLISH);
        date_to_string_yy = new SimpleDateFormat("yy", Locale.ENGLISH);
        date_to_string_dd = new SimpleDateFormat("dd", Locale.ENGLISH);
        disp_single = new SimpleDateFormat("MMM d", Locale.ENGLISH);
        coordinatorLayout = findViewById(R.id.ao_coordinator_layout);
        ll_order_details = findViewById(R.id.ao_linear_layout);
        tv_total_orders = findViewById(R.id.ao_total_orders);
        btn_fetch_order = findViewById(R.id.ao_fetch_order);
        /* selectStoreSpinner = findViewById(R.id.ao_spinner); */
        ll_start_date = findViewById(R.id.ao_start_date);
        tv_order_date = findViewById(R.id.ao_order_date);
        ll_end_date = findViewById(R.id.ao_end_date);
        tv_s_month = findViewById(R.id.ao_s_month);
        tv_e_month = findViewById(R.id.ao_e_month);
        tv_s_date = findViewById(R.id.ao_s_date);
        tv_e_date = findViewById(R.id.ao_e_date);
        tv_e_year = findViewById(R.id.ao_e_year);
        tv_s_year = findViewById(R.id.ao_s_year);
        utilities = new Utilities(this);
        storeList = new ArrayList<>();
        txnList = new ArrayList<>();

        recyclerView = findViewById(R.id.ao_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_fetch_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchOrders();

            }
        });

        store_id = "15";

        /* selectStoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                store_name = storeList.get(position);
                store_id = storeIDArray[position];
                Log.d(TAG, "Store_ID : "+store_name+", Store_Name : "+store_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }); */

        ll_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Start Date");

                final MaterialDatePicker start_date_picker = builder.build();

                start_date_picker.show(getSupportFragmentManager(), "DATE_PICKER");

                start_date_picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis((long)selection);

                        start_date = "";

                        final Date date = calendar.getTime();
                        start_date = date_to_string.format(date);
                        /* Log.d(TAG, "Date : "+calendar.get(Calendar.DAY_OF_MONTH)+", Month : "+calendar.get(Calendar.MONTH)+", Year : "+calendar.get(Calendar.YEAR)); */
                        Log.d(TAG, "Start Date : "+start_date);
                        tv_s_date.setText(date_to_string_dd.format(date));
                        tv_s_month.setText(date_to_string_MMM.format(date));
                        tv_s_year.setText(date_to_string_yy.format(date));

                    }
                });

            }
        });

        ll_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("End Date");

                final MaterialDatePicker end_date_picker = builder.build();

                end_date_picker.show(getSupportFragmentManager(), "DATE_PICKER");

                end_date_picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        end_date = "";

                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis((long)selection);

                        final Date date = calendar.getTime();
                        end_date = date_to_string.format(date);
                        /* Log.d(TAG, "Date : "+calendar.get(Calendar.DAY_OF_MONTH)+", Month : "+calendar.get(Calendar.MONTH)+", Year : "+calendar.get(Calendar.YEAR)); */
                        Log.d(TAG, "End Date : "+end_date);
                        tv_e_date.setText(date_to_string_dd.format(date));
                        tv_e_month.setText(date_to_string_MMM.format(date));
                        tv_e_year.setText(date_to_string_yy.format(date));

                    }
                });

            }
        });

        final Calendar calendar = Calendar.getInstance();
        final Date date = calendar.getTime();
        start_date = date_to_string.format(calendar.getTime());
        tv_order_date.setText(disp_single.format(date));
        fetchOrders();

        /* fetch_stores(); */

    }

    /* private  void fetch_stores() {

        final String type = "fetchStores";

        try {

            final String res = new AwsBackgroundWorker(this).execute(type).get();
            Log.d(TAG, "Stores List :"+res);

            jsonArray2 = new JSONArray(res.trim());

            storeIDArray = new String[jsonArray2.length()];

            for (int index = 0; index < jsonArray2.length(); index++) {

                jobj = jsonArray2.getJSONObject(index);

                storeIDArray[index] = jobj.getString("id");

                storeList.add(jobj.getString("name"));

            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        selectStoreSpinner.setItem(storeList);

    } */

    public void Go_Back(View view) {
        super.onBackPressed();
    }



    public void fetchOrders() {

        String order_date = "";
        boolean flag = true, s_date = false, e_date = false;

        Log.d(TAG, "Start_Date : "+start_date+", End_Date : "+end_date);

        if (!store_id.equals("")) {

            if (start_date.equals("") && end_date.equals("")) {

                Log.d(TAG, "1st If Clause -> Start_Date : "+start_date+", End_Date : "+end_date);

                flag = false;
                utilities.showTopSnackBar(this, coordinatorLayout,  "Fields can not be empty!", R.color.ckout_d);

            } else if (!start_date.equals("") && !end_date.equals("")) {

                start_date += " 00:00:00";
                end_date += " 23:59:00";

                if (utilities.isBefore(start_date, end_date)) {

                    /* Setting Start and End Dates flags to true, hence they'll be reset. */
                    s_date = true;
                    e_date = true;

                    Log.d(TAG, "2nd If Clause -> Start_Date : "+start_date+", End_Date : "+end_date);
                    try {

                        final Date o_date1 = db_date_format.parse(start_date);
                        final Date o_date2 = db_date_format.parse(end_date);
                        order_date = disp_single.format(o_date1) + " - " + disp_single.format(o_date2);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {

                    flag = false;

                    utilities.showTopSnackBar(this, coordinatorLayout,  "Start Date should be smaller than the End Date!", R.color.ckout_d);

                }

            } else if (!start_date.equals("")) {

                end_date += start_date + " 23:59:00";
                start_date += " 00:00:00";

                /* Setting End_Date flag to true, hence, it'll be reset. */
                e_date = true;

                Log.d(TAG, "3rd If Clause -> Start_Date : "+start_date+", End_Date : "+end_date);
                try {

                    final Date o_date = db_date_format.parse(start_date);
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTime(o_date);
                    order_date = disp_single.format(calendar.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {

                flag = false;

                Log.d(TAG, "Else Clause -> Start_Date : "+start_date+", End_Date : "+end_date);

                tv_e_date.setText("DD");
                tv_e_month.setText("MM");
                tv_e_year.setText("YY");

                /* Setting End_Date flag to true, hence it'll be reset. */
                e_date = true;

                utilities.showTopSnackBar(this, coordinatorLayout, "Start date is required!", R.color.ckout_d);

            }

        } else {

            flag = false;

            utilities.showTopSnackBar(this, coordinatorLayout,  "Please Select a Store First!", R.color.ckout_d);

        }

        if (flag) {

            /* Log.d(TAG, "Start_Date : "+start_date+", End_Date : "+end_date); */

            if(txnList.size() > 0) {
                Log.d(TAG, "Clearing the RecyclerView");
                txnList.clear();
                ordersAdapter.notifyDataSetChanged();
            }

            tv_order_date.setText(order_date);

            final String type = "fetch_orders";
            try {

                final String res = new AwsBackgroundWorker(this).execute(type, start_date, end_date, store_id).get();
                Log.d(TAG, "Fetch Order Result : "+res);

                jsonArray = new JSONArray(res);
                jobj = jsonArray.getJSONObject(0);
                if(!jobj.getBoolean("error")) {

                    ll_order_details.setVisibility(View.VISIBLE);

                    jobj1 = jsonArray.getJSONObject(1);
                    jsonArray1 = jobj1.getJSONArray("data");

                    final String total_orders = jsonArray1.length() + " Order(s)";
                    tv_total_orders.setText(total_orders);

                    Log.d(TAG, "Result Array Size : "+jsonArray1.length());

                    for (int i = 0; i < jsonArray1.length(); i++) {

                        JSONObject obj = jsonArray1.getJSONObject(i);
                        /* Extracting the Products Array from the result. */
                        productsArray = obj.getJSONArray("products");

                        txnList.add(
                                new Txn(
                                        obj.get("receipt_no").toString(),
                                        obj.get("payment_mode").toString(),
                                        obj.get("referral_used").toString(),
                                        obj.get("timestamp").toString(),
                                        obj.get("total_items").toString(),
                                        obj.get("final_amt").toString(),
                                        obj.get("store_addr").toString(),
                                        obj.get("store_name").toString(),
                                        obj.get("store_city").toString(),
                                        obj.get("store_state").toString(),
                                        obj.get("order_type").toString(),
                                        obj.get("customer").toString(),
                                        0,
                                        productsArray
                                )
                        );

                    }

                    ordersAdapter = new OrdersAdapter(this, txnList);
                    recyclerView.setAdapter(ordersAdapter);

                } else {

                    final String tot_orders = "0 Order(s)";
                    tv_total_orders.setText(tot_orders);

                    utilities.showTopSnackBar(this, coordinatorLayout,  "No Orders Found!", R.color.ckout_d);

                }

                /* Resetting the Start and End Date. */
                if (s_date) {
                    Log.d(TAG, "Start Date is to be reset.");
                    start_date = "";
                    tv_s_date.setText("DD");
                    tv_s_month.setText("MM");
                    tv_s_year.setText("YY");
                }
                if (e_date) {
                    Log.d(TAG, "End Date is to be reset.");
                    end_date = "";
                    tv_e_date.setText("DD");
                    tv_e_month.setText("MM");
                    tv_e_year.setText("YY");
                }

            } catch (ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }

        }

    }
}