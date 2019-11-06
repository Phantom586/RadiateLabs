package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    // Declaring Required Variables
    RecyclerView recyclerView;
    ProductAdapter adapter;
    Button payment_btn;
    SaveInfoLocally save;

    JSONArray jsonArray;
    JSONObject jobj1, jobj2;
    List<Product> ProductList;
    ImageView im, im1, im2;
    EditText comment;
    TextView tv4;
    DBHelper dbHelper;
    public static Double total_amt = 0.0;
    public static Double total_mrp = 0.0;
    public static Double total_discount = 0.0;
    String txnAmount;
    public static final String TAG = "CartActivity";

    // Function to Delete a Specific Item from the Basket, based on its position in the cart.
    public void removeItem(int position, int id, Double price){
        // Removing the Product from the ProductList.
        ProductList.remove(position);
        // Notifying the Adapter of the Change.
        adapter.notifyItemRemoved(position);
        // Creating an Instance of the DB.
        dbHelper = new DBHelper(this);
        if(total_amt > 0.0){
            total_amt -= price;
        } else {
//            Toast.makeText(this, "Kindly Revisit the Page..", Toast.LENGTH_SHORT).show();
        }
        if(total_amt == 0.0){
            payment_btn.setVisibility(View.INVISIBLE);
        } else {
            payment_btn.setVisibility(View.VISIBLE);
        }
        final String amt = "₹"+total_amt;
        tv4.setText(amt);
        // Deleting the Specific Product from the DB.
        dbHelper.DeleteData_by_id(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ProductList = new ArrayList<>();
        dbHelper = new DBHelper(this);
        save = new SaveInfoLocally(this);

        tv4 = findViewById(R.id.c_tv4);
        payment_btn = findViewById(R.id.btn_payment);
        comment = findViewById(R.id.c_comm);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // For Retrieving the Products from the SqliteDB and Creating an Instance of Product class.
        Cursor res = dbHelper.retrieveData();
        if(res.getCount() == 0){
            Toast.makeText(this, "No Products Added Yet..", Toast.LENGTH_SHORT).show();
        } else {
            while(res.moveToNext()){
                total_amt += Double.parseDouble(res.getString(6));
                total_mrp += Double.parseDouble(res.getString(5));
                total_discount += Double.parseDouble(res.getString(9));
                Log.d(TAG, "Total MRP : "+total_mrp);
                Log.d(TAG, "Total Discount : "+total_discount);
                ProductList.add(
                  new Product(
                          res.getInt(0),
                          res.getString(1),
                          res.getString(2),
                          res.getString(4),
                          res.getString(5),
                          res.getString(6),
                          res.getString(7),
                          res.getString(8),
                          res.getString(9),
                          res.getString(3)
                  ));
            }
        }

        if(total_amt == 0.0){
            payment_btn.setVisibility(View.INVISIBLE);
        } else {
            payment_btn.setVisibility(View.VISIBLE);
        }

        final String amt = "₹"+total_amt;
        tv4.setText(amt);

        final String TAG = "CartActivity";
//        Log.d(TAG, "Product List : "+ProductList);
        adapter = new ProductAdapter(this, ProductList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductAdapter.onItemClickListener() {
            @Override
            public void onDeleteClick(int position, int id, Double price) {
                Toast.makeText(CartActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                removeItem(position, id, price);
            }

//            @Override
//            public void onItemClick(int position) {
//
//            }
        });

    }

    public void Go_to_BarcodeScanner(View view) {
        Intent in = new Intent(CartActivity.this, BarcodeScannerActivity.class);
        in.putExtra("Type", "Product_Scan");
        startActivity(in);
    }

    @Override
    protected void onStop() {
        super.onStop();
        total_amt = 0.0;
    }

    public void Make_Payment(View view) {

        generateCheckSum();

    }

    private void generateCheckSum() {

        //getting the tax amount first.=
        final String str = tv4.getText().toString();
        txnAmount = str.replace("₹", "");

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                PConstants.M_ID,
                PConstants.CHANNEL_ID,
                txnAmount,
                PConstants.WEBSITE,
                PConstants.CALLBACK_URL,
                PConstants.INDUSTRY_TYPE_ID
        );

        final String c_url = paytm.getCallBackUrl() + paytm.getOrderId();

        //creating a call object from the apiService
        Call<PChecksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                c_url,
                paytm.getIndustryTypeId()
        );

        //making the call to generate checksum
        call.enqueue(new Callback<PChecksum>() {
            @Override
            public void onResponse(Call<PChecksum> call, Response<PChecksum> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<PChecksum> call, Throwable t) {

            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service
//        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", PConstants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
        final String c_url = paytm.getCallBackUrl() + paytm.getOrderId();
        Log.d(TAG, "Callback URL : "+c_url);
        paramMap.put("CALLBACK_URL", c_url);
        paramMap.put("CHECKSUMHASH", checksumHash);


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, this);

    }

    //all these overriden method is to detect the payment result accordingly
    @Override
    public void onTransactionResponse(Bundle bundle) {

        final String user_phone_no = save.getPhone();
        Log.d(TAG, bundle.toString());
        try {
            final String txn_status = bundle.get("STATUS").toString();
            final String order_id = bundle.get("ORDERID").toString();
            // Verifying if the Transaction was Successful or not.
            if (txn_status.equals("TXN_SUCCESS")) {

                // Forwarding the Order_id to the Server for the Re-Verification Process.
                final String type1 = "re-verify_checksum";
                final String res1 = new BackgroundWorker(this).execute(type1, order_id).get();

                // Converting the result String in form of JSONObject from the response to JSONObject.
                JSONObject jobj = new JSONObject(res1);
                // Extracting the required value from JSONObject.
                final String status = jobj.getString("STATUS");
                Log.d(TAG, "Re-verify Status : " + status);

                // Re-Verifying if the Transaction was Successful or not.
                if (status.equals("TXN_SUCCESS")) {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
                    // Now creating a AsyncTask for Inserting the Products list into the Server.
                    final String type2 = "retrieve_data";
                    String rab = "";
                    final String result = new BackgroundWorker(this).execute(type2, user_phone_no).get();
//                    Log.d(TAG, "User Details : "+result);
                    try
                    {
                        jsonArray = new JSONArray(result);
                        jobj1 = jsonArray.getJSONObject(0);
                        if(!jobj1.getBoolean("error")){
                            jobj2 = jsonArray.getJSONObject(1);
                            rab = jobj2.getString("referral_amount_balance");
                            Log.d(TAG, "Referral Amount Balance : "+rab);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String type = "Store_Basket";
                    final String res = new BackgroundWorker(this).execute(type, user_phone_no).get();

                    final String type3 = "Store_Invoice";
                    String rest = new BackgroundWorker(this).execute(type3, user_phone_no, String.valueOf(total_mrp), String.valueOf(total_discount), txnAmount, rab, comment.getText().toString()).get();
                    Log.d(TAG, "Invoice Result : "+rest);

                    // Verifying the Response from the server for successful insertion of the Data.
                    boolean b = Boolean.parseBoolean(res.trim());
                    if (b) {

                        // Verifying if the Push to Invoice Table was Successful or not.
                        rest = rest.trim();
                        if(!rest.equals("FALSE")){
                            // Retrieve the details from the result of the Invoice Push.
                            String final_user_amt = "";
                            String time = "";
                            String comment = "";
                            try
                            {
                                jsonArray = new JSONArray(rest);
                                jobj1 = jsonArray.getJSONObject(0);
                                if(!jobj1.getBoolean("error")){
                                    jobj2 = jsonArray.getJSONObject(1);
                                    final_user_amt = jobj2.getString("final_amt");
                                    time = jobj2.getString("time");
                                    comment = jobj2.getString("comment");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // If Invoice is Successfully Pushed to DB, then Send the Invoice SMS to the user.
                            final String type4 = "Send_Invoice_Msg";
                            final String sms_res = new BackgroundWorker(this).execute(type4, time, final_user_amt, comment).get();
                        }
                        dbHelper = new DBHelper(this);
                        // Now after the Re-Verification of Payment, Deleting all the Products Stored in the DB.
                        dbHelper.Delete_all_rows();
                        // Intenting to CartActivity to Update the List of the deleted Products.
                        Intent in = new Intent(this, PaymentSuccess.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);
                    } else {
                        Toast.makeText(this, "Some Error Occurred in DB! Try Again..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Payment Verification Failed.", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(CartActivity.this, PaymentFailed.class);
                    startActivity(in);
                }

            } else {
                Toast.makeText(this, "Payment Failed.", Toast.LENGTH_LONG).show();
                Intent in = new Intent(CartActivity.this, PaymentFailed.class);
                startActivity(in);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
//        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.d(TAG, bundle.toString());
        Toast.makeText(this, s + bundle.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
