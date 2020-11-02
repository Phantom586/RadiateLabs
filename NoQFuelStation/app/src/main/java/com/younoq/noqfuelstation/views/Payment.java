package com.younoq.noqfuelstation.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.Api;
import com.younoq.noqfuelstation.models.AwsBackgroundWorker;
import com.younoq.noqfuelstation.models.BackgroundWorker;
import com.younoq.noqfuelstation.models.Logger;
import com.younoq.noqfuelstation.models.PChecksum;
import com.younoq.noqfuelstation.models.PConstants;
import com.younoq.noqfuelstation.models.Paytm;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Payment extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private TextView tv_ref_disp_amt, tv_tot_amt, tv_ref_amt, tv_final_amt, tv_rs50, tv_rs300,
            tv_rs500, tv_rs1000, tv_pump_name, tv_flat_discount;
    private String pump_name, ref_bal, ref_bal_used, phone, coming_from;
    private double total_our_price = 0, tot_discount = 0;
    private JSONObject jobj1, jobj2, jobj3, jobj4;
    private final String TAG = "PaymentActivity";
    private ConstraintLayout constraintLayout;
    private SaveInfoLocally saveInfoLocally;
    private JSONArray jsonArray, jsonArray1;
    private ArrayList<String> txnData;
    private TextInputLayout til_input;
    private ProgressBar progressBar;
    private boolean isReferralUsed;
    private Button btn_payment;
    private EditText et_input;
    private Bundle txnReceipt;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setupUI(findViewById(R.id.p_constraint_layout));

        saveInfoLocally = new SaveInfoLocally(this);
        constraintLayout = findViewById(R.id.p_constraint_layout);
        tv_ref_disp_amt = findViewById(R.id.p_disp_referral_amt);
        tv_flat_discount = findViewById(R.id.p_flat_discount);
        btn_payment = findViewById(R.id.p_btn_payment);
        tv_ref_amt = findViewById(R.id.p_referral_amt);
        tv_final_amt = findViewById(R.id.p_final_amt);
        tv_pump_name = findViewById(R.id.p_pp_name);
        tv_tot_amt = findViewById(R.id.p_total_amt);
        tv_rs50 = findViewById(R.id.p_rs50);

        tv_rs300 = findViewById(R.id.p_rs300);
        tv_rs500 = findViewById(R.id.p_rs500);
        tv_rs1000 = findViewById(R.id.p_rs1000);
        til_input = findViewById(R.id.p_til_input);
        et_input = findViewById(R.id.p_et_input);
        logger = new Logger(this);
        txnData = new ArrayList<>();
        txnReceipt = new Bundle();

        progressBar = findViewById(R.id.spin_kit);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","onCreate() Func. called\n");

        phone = saveInfoLocally.getPhone();

        /* Generating the SessionID for the Current Session. */
        try {
            final String sess = toHexString(getSHA(getRandomString()+phone+getRandomString()));
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "onCreate()","Generating the SessionID : "+sess+"\n");
            Log.d(TAG, "Session Id : "+sess);
            saveInfoLocally.setSessionID(sess);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Intent in = getIntent();
        pump_name = in.getStringExtra("pump_name");
        coming_from = in.getStringExtra("coming_from");
        tv_pump_name.setText(pump_name);
       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","Values received from Intent pump_name : "+pump_name+"\n");

        et_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_payment.setVisibility(View.GONE);
            }
        });

        tv_rs50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(R.string.p_rs_50);
                btn_payment.setVisibility(View.GONE);
            }
        });

        tv_rs300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(R.string.p_rs_300);
                btn_payment.setVisibility(View.GONE);
            }
        });

        tv_rs500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(R.string.p_rs_500);
                btn_payment.setVisibility(View.GONE);
            }
        });

        tv_rs1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(R.string.p_rs_1000);
                btn_payment.setVisibility(View.GONE);
            }
        });

        if (coming_from.equals("PaymentSuccess"))
            fetch_noq_cash();

        ref_bal = saveInfoLocally.getReferralBalance();
        final String r_disp_bal = "₹" + ref_bal;
        tv_ref_disp_amt.setText(r_disp_bal);

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","Retrieved Referral balance from SharedPreferences : "+ref_bal+"\n");

        if(et_input.requestFocus())
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    public void fetch_noq_cash(){

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "fetch_noq_cash()","fetch_referral_amt() Func. called\n");
        final String type = "retrieve_referral_amt";
        final String phone = saveInfoLocally.getPhone();
        try {

            final String res = new AwsBackgroundWorker(this).execute(type, phone).get();
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "fetch_noq_cash()","Referral Balance Retrieved From ServerDB : "+res+"\n");
            String ref_bal;
            try
            {
                jsonArray1 = new JSONArray(res);
                jobj3 = jsonArray1.getJSONObject(0);
                if(!jobj3.getBoolean("error")){
                    jobj4 = jsonArray1.getJSONObject(1);
                    ref_bal = jobj4.getString("referral_balance");
                    Log.d(TAG, "Referral Amount Balance : "+ref_bal);
                    /* Saving the Referral_Amount_Balance to SharedPreferences to be used in CartActivity */
                    saveInfoLocally.setReferralBalance(ref_bal);
                   /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "fetch_noq_cash()","Stored the Referral Balance in the SharedPreferences.\n");
                }

            } catch (JSONException e) {
                e.printStackTrace();
               /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "fetch_noq_cash()",e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "fetch_noq_cash()",e.getMessage());
        }

    }

    private String generateTxn_Order() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void calculateFinalAmt(View view) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "calculateFinalAmt()","calculateFinalAmt() Func. called\n");

        final String enteredAmt = et_input.getText().toString();

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "calculateFinalAmt()","Amt. that the User Entered : "+enteredAmt+"\n");

        if (!enteredAmt.equals("") && isNumber(enteredAmt)) {

           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "calculateFinalAmt()","Validated the Amt. Entered by the User.\n");

            double final_amt = 0;
            final int noq_cash = Integer.parseInt(ref_bal);
            final double eAmt = Double.parseDouble(enteredAmt);

            if (noq_cash >= 10) {

                isReferralUsed = true;

                /* Entered Amt should be ₹10 Minimum. */
                if (eAmt >= 10) {

                    ref_bal_used = "10";
                    final_amt = eAmt - 10;

                } else {

                    ref_bal_used = String.valueOf(eAmt);
                    final_amt = 0;

                }

            } else {

                ref_bal_used = "0";
                isReferralUsed = false;

                /* Calculating the amt of referral balance would be used. */
                if (eAmt >= 30 && eAmt < 200)
                    tot_discount = 10;
                else if (eAmt >= 200 && eAmt < 500)
                    tot_discount = 15;
                else if (eAmt >= 500 && eAmt < 1000)
                    tot_discount = 25;
                else if (eAmt >= 1000)
                    tot_discount = 50;
                else
                    tot_discount = 0;

                final_amt = eAmt - tot_discount;

            }

            total_our_price = eAmt - tot_discount;

            /* This means the user has entered the amt. in the EditText. */
            Log.d(TAG, "Entered Amt : "+et_input.getText().toString());

            final String tot_amt = "₹" + enteredAmt;
            tv_tot_amt.setText(tot_amt);

            final String ref_bal_to_be_used = "- ₹" + ref_bal_used;
            tv_ref_amt.setText(ref_bal_to_be_used);

            final String f_discount = "- ₹" + tot_discount;
            tv_flat_discount.setText(f_discount);

            final String fin_amt = "₹" + final_amt;
            tv_final_amt.setText(fin_amt);

            final String fin_amt_to_be_paid = "Pay " + fin_amt;
            btn_payment.setText(fin_amt_to_be_paid);
            btn_payment.setVisibility(View.VISIBLE);

        } else {

           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "calculateFinalAmt()", "The Amt. Entered by the User not Validated Successfully, making the Payment Button Invisible.\n");

            btn_payment.setVisibility(View.GONE);

            Snackbar snackbar = Snackbar.make(constraintLayout, "Please enter an amount!", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ckout_d));
            snackbar.show();

        }

    }

    public void onContinue(View view) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onContinue()","onContinue() Func. called\n");

        String txnAmount = tv_final_amt.getText().toString();
        txnAmount = txnAmount.replace("₹", "");

        final double amt_to_pay = Double.parseDouble(txnAmount);

        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onContinue()","TxnAmount to be sent to Paytm : "+txnAmount+"\n");

        if (amt_to_pay > 0) {

            generateCheckSum(txnAmount);

        } else {

            progressBar.setVisibility(View.VISIBLE);
            // Doing all the things to be done after Successful Payment(which is already done here :-)..)
            afterPaymentConfirm(ref_bal_used, generateTxn_Order(), generateTxn_Order(), "[Referral_Used]");
            // Redirect to Payment Successful Page.
            Log.d(TAG, "Sending the User to PaymentSuccess Activity");
            Log.d(TAG, "TxnReceipt Details : "+txnReceipt.toString());
            Intent in = new Intent(this, PaymentSuccess.class);
            in.putExtra("referral_balance_used", ref_bal_used);
            in.putExtras(txnReceipt);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            progressBar.setVisibility(View.GONE);
            startActivity(in);

        }

    }

    public void afterPaymentConfirm(String ref_bal_used, String Txn_ID, String Order_ID, String Pay_Mode) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "afterPaymentConfirm()","afterPaymentConfirm() Func. called\n");

        /* Retrieving the User_Phone_NO from SharedPreferences. */
        final String user_phone_no = saveInfoLocally.getPhone();
       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "afterPaymentConfirm()","Users Phone No. retrieved from SharedPreferences : "+user_phone_no+"\n");

        try {

            /* Now Inserting the Transaction Details into the Invoice_Table. */
            final String type3 = "Store_Invoice";
            /* Converting the required values to String */
            String tot_retailer_price = et_input.getText().toString();
            Log.d(TAG, "Total_Retailer_Price for Storing in Invoice : "+tot_retailer_price);

            final String tot_mrp = tot_retailer_price;
            final String tot_our_price = String.valueOf(total_our_price);

            final String total_discount = String.valueOf(tot_discount);

            String txnAmount = tv_final_amt.getText().toString();
            txnAmount = txnAmount.replace("₹", "");

            /* TODO:// Add Code to fetch comments when Store_ID = "3", for now its ""; */
            String rest = new BackgroundWorker(this).execute(type3, user_phone_no, tot_mrp, total_discount, txnAmount, ref_bal_used, "", Txn_ID, Order_ID, Pay_Mode, tot_retailer_price, tot_our_price).get();
            Log.d(TAG, "Invoice Result : " + rest);

           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "afterPaymentConfirm()","Store Invoice Result : "+rest+"\n");

            /* Verifying if the Push to Invoice Table was Successful or not. */
            rest = rest.trim();
            if (!rest.equals("FALSE")) {
                /* Retrieve the details from the result of the Invoice Push. */
                String receipt_no = "";
                String final_user_amt = "";
                String tot_retail_price = "";
                String to_our_price = "";
                String tot_discount = "";
                String tot_savings = "";
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
                        tot_savings = jobj2.getString("tot_savings");
                        time = jobj2.getString("time");
                        comment = jobj2.getString("comment");
                    }

                } catch (JSONException e) {
                   /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "afterPaymentConfirm()",e.getMessage());
                    e.printStackTrace();
                }
                /* If Invoice is Successfully Pushed to DB, then Send the Invoice SMS to the user. */
                final String type4 = "Send_Invoice_Msg";
                final String sms_res = new BackgroundWorker(this).execute(type4, time, final_user_amt, receipt_no, tot_retail_price, tot_savings).get();
               /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "afterPaymentConfirm()","'Send_Invoice_Msg' BackgroundWorker called. Result : "+sms_res+" \n");

                /* Then we have to send the Invoice_Msg to the Retailer also. */
                Log.d(TAG, "Sending Retailer Invoice Sms");
                final String type5 = "Send_Retailer_Invoice_Msg";
                final String sms = new AwsBackgroundWorker(this).execute(type5, time, final_user_amt, receipt_no, tot_retail_price).get();
               /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "afterPaymentConfirm()","'Send_Retailer_Invoice_Msg' BackgroundWorker called. Result : "+sms+" \n");

                /* Storing the Details in txnData ArrayList. */
                txnData.add(receipt_no);
                txnData.add(tot_discount);
                txnData.add(tot_retail_price);
                txnData.add(ref_bal_used);
                txnData.add(to_our_price);
                txnData.add(final_user_amt);
                txnData.add(time);
                txnData.add(Pay_Mode);
                txnData.add("1");
                txnData.add(tot_savings);
                /* Adding the txnData ArrayList to txnReceipt Bundle. */
                txnReceipt.putStringArrayList("txnReceipt", txnData);
                Log.d(TAG, "Stored Required Details in Bundle");
               /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "afterPaymentConfirm()","Invoice Details : "+txnReceipt.toString()+"\n");

            }

        } catch (NullPointerException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "afterPaymentConfirm()",e.getMessage());
        }

    }

    private void generateCheckSum(String txnAmount) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "generateCheckSum()","generateCheckSum() Func. called\n");

        /* creating a retrofit object. */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        /* creating the retrofit api service */
        Api apiService = retrofit.create(Api.class);

        /* creating paytm object */
        /* containing all the values required */
        final Paytm paytm = new Paytm(
                PConstants.M_ID,
                PConstants.CHANNEL_ID,
                txnAmount,
                PConstants.WEBSITE,
                PConstants.CALLBACK_URL,
                PConstants.INDUSTRY_TYPE_ID
        );

        final String c_url = paytm.getCallBackUrl() + paytm.getOrderId();

        /* creating a call object from the apiService */
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

        /* making the call to generate checksum */
        call.enqueue(new Callback<PChecksum>() {
            @Override
            public void onResponse(Call<PChecksum> call, Response<PChecksum> response) {

                /* once we get the checksum we will initiailize the payment. */
                /* the method is taking the checksum we got and the paytm object as the parameter */
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<PChecksum> call, Throwable t) {

            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "initializePaytmPayment()","initializePaytmPayment() Func. called\n");

        /* getting paytm service for Staging. */
        /* PaytmPGService Service = PaytmPGService.getStagingService(); */

        /* use this when using for production. */
        PaytmPGService Service = PaytmPGService.getProductionService();

        /* creating a hashmap and adding all the values required */
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

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "initializePaytmPayment()","Data being sent to Paytm : "+paramMap.toString()+"\n");


        /* creating a paytm order object using the hashmap */
        PaytmOrder order = new PaytmOrder(paramMap);

        /* intializing the paytm service */
        Service.initialize(order, null);

        /* finally starting the payment transaction */
        Service.startPaymentTransaction(this, true, true, this);

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onTransactionResponse()","onTransactionResponse() Func. called\n");

        Log.d(TAG, "From onTransactionResponse : "+bundle.toString());
       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onTransactionResponse()","Response Bundle : "+bundle.toString()+"\n");

        try {
            /* Retrieving the Txn_Details from the TxnResponse i.e., bundle. */
            final String txn_status = bundle.get("STATUS").toString();
            final String Txn_ID = bundle.get("TXNID").toString();
            final String Order_ID = bundle.get("ORDERID").toString();
            final String Pay_Mode = bundle.get("PAYMENTMODE").toString();
            final String Time = bundle.get("TXNDATE").toString();
            /* Verifying if the Transaction was Successful or not. */
            if (txn_status.equals("TXN_SUCCESS")) {

               /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "onTransactionResponse()","TXN Successful Status was given, now calling 're-verify_checksum'.\n");
                /* Forwarding the Order_id to the Server for the Re-Verification Process. */
                final String type1 = "re-verify_checksum";
                final String res1 = new BackgroundWorker(this).execute(type1, Order_ID).get();
               /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "onTransactionResponse()","'re-verify_checksum' Result : "+res1+".\n");
                /* Converting the result String in form of JSONObject from the response to JSONObject. */
                JSONObject jobj = new JSONObject(res1);
                /* Extracting the required value from JSONObject. */
                final String status = jobj.getString("STATUS");
                Log.d(TAG, "Re-verify Status : " + status);

                /* Re-Verifying if the Transaction was Successful or not. */
                if (status.equals("TXN_SUCCESS")) {

                   /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "onTransactionResponse()","Again the Txn was Successful, now calling afterPaymentConfirm().\n");
                    /* Doing all the things to be done after Successful Payment. */
                    afterPaymentConfirm(ref_bal_used, Txn_ID, Order_ID, Pay_Mode);
                    /* Saving the Referral_Balance Value to SharedPreference. */
                    saveInfoLocally.setReferralBalance("0");
                   /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "onTransactionResponse()","Setting the Referral Balance's Value to 0.\n");
                   /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "onTransactionResponse()","Intent Values sent to PaymentSuccess referral_balance_used : "+ref_bal_used+".\n");
                    /* Intent to PaymentSuccess Activity. */
                    Intent in = new Intent(this, PaymentSuccess.class);
                    in.putExtra("referral_balance_used", ref_bal_used);
                    in.putExtras(txnReceipt);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);

                } else {

                   /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "onTransactionResponse()","The Txn was Unsuccessful, routing the user to PaymentFailed.\n");

                    final String Error_MSG = bundle.get("errorMessage").toString();
                    /* If the Re-verification of the Txn Fails. */
                    Log.d(TAG, "Payment Verification Failed.");
                   /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "onTransactionResponse()","Intent Values Order_Time : "+Time+", Pay_Mode : "+Pay_Mode+", Error_Msg : "+Error_MSG+".\n");
                    Intent in = new Intent(Payment.this, PaymentFailed.class);
                    in.putExtra("Order_Time", Time);
                    in.putExtra("Payment_Mode", Pay_Mode);
                    in.putExtra("Error_MSG", Error_MSG);
                    startActivity(in);
                }

            } else {

               /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "onTransactionResponse()","The Txn was Unsuccessful, routing the user to PaymentFailed.\n");

                final String Error_MSG = bundle.get("errorMessage").toString();
                /* If the Txn Fails. */
                Log.d(TAG, "Payment Failed.");
               /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "onTransactionResponse()","Intent Values Order_Time : "+Time+", Pay_Mode : "+Pay_Mode+", Error_Msg : "+Error_MSG+".\n");
                Intent in = new Intent(Payment.this, PaymentFailed.class);
                in.putExtra("Order_Time", Time);
                in.putExtra("Payment_Mode", Pay_Mode);
                in.putExtra("Error_MSG", Error_MSG);
                startActivity(in);
            }
        } catch (NullPointerException | JSONException | ExecutionException | InterruptedException e) {
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "onTransactionResponse()",e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void networkNotAvailable() {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "networkNotAvailable()","networkNotAvailable() Func. called\n");

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "clientAuthenticationFailed()","clientAuthenticationFailed() Func. called\n");

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "someUIErrorOccurred()","someUIErrorOccurred() Func. called\n");

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onErrorLoadingWebPage()","onErrorLoadingWebPage() Func. called\n");

    }

    @Override
    public void onBackPressedCancelTransaction() {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onBackPressedCancelTransaction()","onBackPressedCancelTransaction() Func. called\n");

        final String pumpName = saveInfoLocally.getPumpName();
        final String pumpAddr = saveInfoLocally.getPumpAddress() + ", " + saveInfoLocally.getStoreCity();
        final String p_name = pumpName + ", " + pumpAddr;

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onBackPressedCancelTransaction()","Values in Intent to Payment pump_name "+p_name+"\n");

        Intent in = new Intent(Payment.this, Payment.class);
        in.putExtra("coming_from", "PaymentSuccess");
        in.putExtra("pump_name", p_name);
        startActivity(in);

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onTransactionCancel()","onTransactionCancel() Func. called\n");

    }

    @Override
    public void onBackPressed() {

        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(Payment.this, PetrolPumpsNoq.class);
        in.putExtra("Phone", phone);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

    }

    private String getRandomString(){
        int n = 4;
        /* chose a Character random from this String */
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        /* create StringBuffer size of AlphaNumericString */
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            /* generate a random number between */
            /* 0 to AlphaNumericString variable length */
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            /* add Character one by one in end of sb */
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private byte[] getSHA(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(str.getBytes(StandardCharsets.UTF_8));
    }

    private String toHexString(byte[] strHash){
        BigInteger num = new BigInteger(1, strHash);
        StringBuilder hexString = new StringBuilder(num.toString(16));
        while(hexString.length() < 32){
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    private boolean isNumber(String amt) {

        try {
            Double.parseDouble(amt);
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "isNumber()","Verifying the No. Entered by the User : "+ amt +"\n");
            return true;
        } catch (NumberFormatException e) {
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG,"isNumber()", e.getMessage());
            return false;
        }

    }

    private void setupUI(View view) {

        /* Set up touch listener for non-text box views to hide keyboard. */
        if ((view instanceof ImageView)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Payment.this);
                    return false;
                }
            });
        }

        /* If a layout container, iterate over children and seed recursion. */
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}