package com.younoq.noq_retailer.views;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.textfield.TextInputLayout;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.younoq.noq_retailer.R;
import com.younoq.noq_retailer.models.Api;
import com.younoq.noq_retailer.models.AwsBackgroundWorker;
import com.younoq.noq_retailer.models.BackgroundWorker;
import com.younoq.noq_retailer.models.PChecksum;
import com.younoq.noq_retailer.models.PConstants;
import com.younoq.noq_retailer.models.Paytm;
import com.younoq.noq_retailer.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on 31/05/20.
 */

public class DeliveryDetails extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private TextView tv_user_address, tv_delivery_duration, tv_total_amt, tv_ref_amt, tv_delivery_charges, tv_final_amt, tv_discounted_amt, tv_max_delivery_charge;
    private String total_amt, ref_amt, user_addr, txnAmount, tot_retailer_price, tot_our_price, total_discount, total_mrp;
    private String category_name, shoppingMethod, coming_from;
    private int min_charge, max_charge, delivery_charge, item_qty = 0, delivery_duration;
    private double final_amt;
    private SaveInfoLocally saveInfoLocally;
    private String TAG = "DeliveryDetails";
    private JSONArray jsonArray;
    private JSONObject jobj1, jobj2;
    private Bundle txnReceipt;
    private ArrayList<String> txnData;
    private TextInputLayout edit_address;
    private ProgressBar progressBar;
    private LinearLayout free_homeDelivery_linearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        tv_user_address = findViewById(R.id.dd_user_address);
        tv_delivery_duration = findViewById(R.id.dd_delivery_time);
        tv_total_amt = findViewById(R.id.dd_tot_amt);
        tv_ref_amt = findViewById(R.id.dd_ref_bal);
        tv_delivery_charges = findViewById(R.id.dd_delivery_charges);
        tv_final_amt = findViewById(R.id.dd_final_amt);
        edit_address = findViewById(R.id.dd_edit_address);
        tv_discounted_amt = findViewById(R.id.dd_discounted_amt);
        free_homeDelivery_linearlayout = findViewById(R.id.dd_free_hd_linearlayout);
        tv_max_delivery_charge = findViewById(R.id.dd_max_charge);

        progressBar = findViewById(R.id.spin_kit);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        txnReceipt = new Bundle();
        txnData = new ArrayList<>();

        saveInfoLocally = new SaveInfoLocally(this);

        edit_address.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting the User Address TextView to be Editable
                tv_user_address.setEnabled(true);
                edit_address.setEndIconVisible(false);
            }
        });

        tv_user_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        Log.d(TAG, "User has Finished Typing");
                        edit_address.setEndIconVisible(true);
                        edit_address.setEndIconDrawable(R.drawable.icon_check);
                        v.setEnabled(false);
                        return true;
                    }
                }
                return false;
            }
        });

        Intent in = getIntent();
        total_amt = in.getStringExtra("total_amt");
        tot_retailer_price = in.getStringExtra("total_retailer_price");
        tot_our_price = in.getStringExtra("total_our_price");
        total_mrp = in.getStringExtra("total_mrp");
        total_discount = in.getStringExtra("total_discount");
        item_qty = in.getIntExtra("total_items", 0);
        shoppingMethod = in.getStringExtra("shoppingMethod");
        category_name = in.getStringExtra("category_name");
        coming_from = in.getStringExtra("coming_from");

        showDeliveryDetails();

    }

    public void AddMore(View view) {

        Intent in;
        // If coming from Products Category Screen then, go back there.
        if(coming_from.equals("ProductCategory")){
            in  = new Intent(DeliveryDetails.this, ProductsCategory.class);
        } else {
            in  = new Intent(DeliveryDetails.this, ProductsList.class);
            in.putExtra("coming_from", "Cart");
            in.putExtra("category_name", category_name);
        }
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);

    }

    void showDeliveryDetails() {

        // Setting the values for the UI elements
        user_addr = saveInfoLocally.getUserAddress();
        tv_user_address.setText(user_addr);

        final String temp = "₹" + total_amt;
        tv_total_amt.setText(temp);

        // Retrieving the Delivery Duration.
        delivery_duration = saveInfoLocally.getStoreDeliveryDuration();
        Log.d(TAG, "Delivery Duration : "+delivery_duration);
        String timeUnit = "";
        int delivery_time_hours = 0, delivery_time_mins = 0;
        String delivery_dur = "Delivery in ";

        if(delivery_duration >= 0 && delivery_duration < 60){

            delivery_dur += delivery_duration + " mins";

        }
        else if(delivery_duration >= 60){

            delivery_time_hours = delivery_duration / 60;
            delivery_time_mins = delivery_duration % 60;

            if(delivery_time_mins == 0){
                delivery_dur += delivery_time_hours + " hour";
            } else {
                delivery_dur += delivery_time_hours + " hr " + delivery_time_mins + " mins";
            }
        }
        Log.d(TAG, delivery_dur);

        tv_delivery_duration.setText(delivery_dur);

        ref_amt = saveInfoLocally.getReferralBalance();
        final String ref_text = "- ₹"+ref_amt;
        tv_ref_amt.setText(ref_text);

        // Calculating the Amount to be Paid
        final double tot_amt = Double.parseDouble(total_amt);
        final double ref_bal = Double.parseDouble(ref_amt);
        if(ref_bal >= tot_amt){
            final_amt = 0;
        } else {
            final_amt = tot_amt - ref_bal;
        }

        final String tpm = "₹" + final_amt;
        tv_discounted_amt.setText(tpm);

        // Retrieving the Delivery related info from the SharedPreferences.
        delivery_charge = 0;
        min_charge = saveInfoLocally.getMinCharge();
        max_charge = saveInfoLocally.getMaxCharge();

        final String tpm1 = "₹" + max_charge;
        tv_max_delivery_charge.setText(tpm1);

        // Checking the Delivery Charge applicable on the Order
        if(tot_amt >= min_charge && tot_amt < max_charge){

            delivery_charge = saveInfoLocally.getDeliveryCharge();

        }
        // Checking if the Order Amount is greater than Max Delivery Charge.
        if(tot_amt >= max_charge) {
            free_homeDelivery_linearlayout.setVisibility(View.GONE);
        } else {
            free_homeDelivery_linearlayout.setVisibility(View.VISIBLE);
        }

        final String tmp = "+ ₹" + delivery_charge;
        tv_delivery_charges.setText(tmp);

        final_amt += delivery_charge;
        final String fin_amt = "₹" + final_amt;
        tv_final_amt.setText(fin_amt);

    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, CartActivity.class);
        if(coming_from.equals("ProductCategory")){
            in.putExtra("comingFrom", "ProductCategory");
        } else {
            in.putExtra("comingFrom", "Cart");
        }
        in.putExtra("shoppingMethod", shoppingMethod);
        in.putExtra("category_name", category_name);
        startActivity(in);
    }

    private String generateTxn_Order() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void Make_Payment(View view) {

        Double b;
        // Value of Referral_Balance
        b = Double.valueOf(ref_amt);

        final String entered_addr = tv_user_address.getText().toString().trim();

        // Retrieving the Address Entered by the User
        if(!entered_addr.equals("")){

            // Showing the Progressbar.
            progressBar.setVisibility(View.VISIBLE);
            final String phone = saveInfoLocally.getPhone();

            try {

                final String type = "update_address";
                new AwsBackgroundWorker(this).execute(type, phone, entered_addr).get();

                Toast.makeText(this, "Address Updated Successfully", Toast.LENGTH_SHORT).show();
                // Saving the User's Address in SharedPreferences.
                saveInfoLocally.setUserAddress(entered_addr);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {

            tv_user_address.setError("Please enter an address");

        }

        if ( final_amt > 0) {
            // If Total_amount is Greater then Referral_Balance, then Proceed to Payment from Paytm.
            generateCheckSum();
        } else {
            // else go to Payment_Successful Page.
            String ref_bal_used = total_amt;
            // Calculating the Referral_balance to be Stored in SharedPreference.
            final Double cal_ref_bal = b - Double.valueOf(ref_bal_used);
            Log.d(TAG, "Updated Referral Amount : "+cal_ref_bal);
            // Setting the Updated Referral_Balance to SharedPreferences.
            saveInfoLocally.setReferralBalance(String.valueOf(cal_ref_bal));
            // Setting txnAmount's value to final_amt.
            txnAmount = String.valueOf(final_amt);
            // Doing all the things to be done after Successful Payment(which is already done here :-)..)
            afterPaymentConfirm(ref_bal_used, generateTxn_Order(), generateTxn_Order(), "[Referral_Used]");
            // Redirect to Payment Successful Page.
            Log.d(TAG, "Sending the User to PaymentSuccess Activity");
            Intent in = new Intent(this, PaymentSuccess.class);
            in.putExtra("referral_balance_used", ref_bal_used);
            in.putExtras(txnReceipt);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Make the Progressbar Invisible
            progressBar.setVisibility(View.GONE);
            startActivity(in);
        }

    }

    public void afterPaymentConfirm(String ref_bal_used, String Txn_ID, String Order_ID, String Pay_Mode) {

        // Retrieving the User_Phone_NO from SharedPreferences.
        final String user_phone_no = saveInfoLocally.getPhone();

        try {
            // Now Inserting the Products list into the Basket_Table.
            final String type = "Store_Basket";
            final String res = new BackgroundWorker(this).execute(type, user_phone_no).get();
            // Now Inserting the Transaction Details into the Invoice_Table.
            final String type3 = "Store_Invoice";

            // TODO:// Add Code to fetch comments when Store_ID = "3", for now its "";
            String rest = new BackgroundWorker(this).execute(type3, user_phone_no, total_mrp, total_discount, txnAmount, ref_bal_used, "", Txn_ID, Order_ID, Pay_Mode, tot_retailer_price, tot_our_price).get();
            Log.d(TAG, "Invoice Result : " + rest);

            // Verifying if the Push to Basket_Table was Successful or not.
            boolean b = Boolean.parseBoolean(res.trim());
            if (b) {

                // Verifying if the Push to Invoice Table was Successful or not.
                rest = rest.trim();
                if (!rest.equals("FALSE")) {
                    // Retrieve the details from the result of the Invoice Push.
                    String receipt_no = "";
                    String final_user_amt = "";
                    String tot_retail_price = "";
                    String to_our_price = "";
                    String tot_discount = "";
                    String time = "";
                    String comment = "";
                    try {
                        jsonArray = new JSONArray(rest);
                        jobj1 = jsonArray.getJSONObject(0);
                        if (!jobj1.getBoolean("error")) {
                            jobj2 = jsonArray.getJSONObject(1);
                            receipt_no = jobj2.getString("r_no");
                            final_user_amt = jobj2.getString("final_amt");
                            tot_retail_price = jobj2.getString("tot_retail_price");
                            to_our_price = jobj2.getString("tot_our_price");
                            tot_discount = jobj2.getString("tot_discount");
                            time = jobj2.getString("time");
                            Log.d(TAG, "Time : "+time);
                            comment = jobj2.getString("comment");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // If Invoice is Successfully Pushed to DB, then Send the Invoice SMS to the user.
                    final String type4 = "Send_Invoice_Msg";
                    final String sms_res = new BackgroundWorker(this).execute(type4, time, final_user_amt, comment, receipt_no, tot_retail_price, ref_bal_used, tot_discount, to_our_price).get();

                    // Checking if the shoppingMethod is whether Takeaway or Home_Delivery.
//                    if(shoppingMethod.equals("Takeaway") || shoppingMethod.equals("HomeDelivery")){
                        // Then we have to send the Invoice_Msg to the Retailer also.
                        Log.d(TAG, "Sending Retailer Invoice Sms");
                        final String type5 = "Send_Retailer_Invoice_Msg";
                        final String sms = new AwsBackgroundWorker(this).execute(type5, time, final_user_amt, receipt_no, tot_retail_price).get();
//                    }

                    // Storing the Details in txnData ArrayList.
                    txnData.add(receipt_no);
                    txnData.add(tot_discount);
                    txnData.add(tot_retail_price);
                    txnData.add(ref_bal_used);
                    txnData.add(to_our_price);
                    txnData.add(final_user_amt);
                    txnData.add(time);
                    txnData.add(Pay_Mode);
                    txnData.add(String.valueOf(item_qty));
                    // Adding the txnData ArrayList to txnReceipt Bundle.
                    txnReceipt.putStringArrayList("txnReceipt", txnData);
                    Log.d(TAG, "Stored Required Details in Bundle");
                    // Sending an Email to our official Account containing this Invoice Details.
                    // Currently Not Working.
//                    final String type5 = "Send_Invoice_Mail";
//                    final String email_res = new AwsBackgroundWorker(this).execute(type5, time, final_user_amt, comment, receipt_no).get();
//                    Log.d(TAG, "AWS_SES Response : " + email_res);
                }
//                dbHelper = new DBHelper(this);
//                // Now after the Re-Verification of Payment, Deleting all the Products Stored in the DB.
//                dbHelper.Delete_all_rows();
            }
        } catch (NullPointerException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void generateCheckSum() {

        txnAmount = String.valueOf(final_amt);

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

        Log.d(TAG, "Paytm CustomerId : "+paytm.getCustId());
        Log.d(TAG, "Paytm OrderId : "+paytm.getOrderId());

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

        //getting paytm service for Staging.
//        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production.
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

    @Override
    public void onTransactionResponse(Bundle bundle) {

        Log.d(TAG, "From onTransactionResponse : "+bundle.toString());
        try {
            // Retrieving the Txn_Details from the TxnResponse i.e., bundle.
            final String txn_status = bundle.get("STATUS").toString();
            final String order_id = bundle.get("ORDERID").toString();
            final String Txn_ID = bundle.get("TXNID").toString();
            final String Order_ID = bundle.get("ORDERID").toString();
            final String Pay_Mode = bundle.get("PAYMENTMODE").toString();
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
                    // Doing all the things to be done after Successful Payment.
                    afterPaymentConfirm(ref_amt, Txn_ID, Order_ID, Pay_Mode);
                    // Saving the Referral_Balance Value to SharedPreference.
                    saveInfoLocally.setReferralBalance("0");
                    // Intent to PaymentSuccess Activity.
                    Intent in = new Intent(this, PaymentSuccess.class);
                    in.putExtra("referral_balance_used", ref_amt);
                    in.putExtras(txnReceipt);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                } else {
                    // If the Re-verification of the Txn Fails.
                    Toast.makeText(this, "Payment Verification Failed.", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(DeliveryDetails.this, PaymentFailed.class);
                    in.putExtras(txnReceipt);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                }

            } else {
                // If the Txn Fails.
                Toast.makeText(this, "Payment Failed.", Toast.LENGTH_LONG).show();
                Intent in = new Intent(DeliveryDetails.this, PaymentFailed.class);
                in.putExtras(txnReceipt);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
            }
        } catch (NullPointerException | JSONException | ExecutionException | InterruptedException e) {
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
        Intent in;
        if(shoppingMethod.equals("InStore")){
            in  = new Intent(this, CartActivity.class);
            in.putExtra("shoppingMethod", shoppingMethod);
        } else if(shoppingMethod.equals("Takeaway") || shoppingMethod.equals("HomeDelivery")){
            in  = new Intent(this, CartActivity.class);
            in.putExtra("comingFrom", "Cart");
            in.putExtra("shoppingMethod", shoppingMethod);
            in.putExtra("category_name", category_name);
        } else{
            in = new Intent();
        }
//        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

//        Intent in = new Intent(this, CartActivity.class);
//        in.putExtra("shoppingMethod", shoppingMethod);
//        in.putExtra("category_name", category_name);
//        startActivity(in);
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.d(TAG, "Transaction Failed : "+bundle.toString());
//        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}