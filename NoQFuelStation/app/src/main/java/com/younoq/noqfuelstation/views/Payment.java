package com.younoq.noqfuelstation.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private TextView tv_ref_disp_amt, tv_tot_amt, tv_ref_amt, tv_final_amt, tv_rs50, tv_rs300, tv_rs500, tv_rs1000, tv_pump_name;
    private String pump_name, ref_bal, ref_bal_used, phone;
    private final String TAG = "PaymentActivity";
    private ConstraintLayout constraintLayout;
    private SaveInfoLocally saveInfoLocally;
    private ArrayList<String> txnData;
    private TextInputLayout til_input;
    private JSONObject jobj1, jobj2;
    private JSONArray jsonArray;
    private Button btn_payment;
    private EditText et_input;
    private Bundle txnReceipt;
    private Logger logger;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        saveInfoLocally = new SaveInfoLocally(this);
        constraintLayout = findViewById(R.id.p_constraint_layout);
        tv_ref_disp_amt = findViewById(R.id.p_disp_referral_amt);
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

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","onCreate() Func. called\n");

        phone = saveInfoLocally.getPhone();

        // Generating the SessionID for the Current Session.
        try {
            final String sess = toHexString(getSHA(getRandomString()+phone+getRandomString()));
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "onCreate()","Generating the SessionID : "+sess+"\n");
            Log.d(TAG, "Session Id : "+sess);
            saveInfoLocally.setSessionID(sess);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Intent in = getIntent();
        pump_name = in.getStringExtra("pump_name");
        tv_pump_name.setText(pump_name);
        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","Values received from Intent pump_name : "+pump_name+"\n");

        et_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_payment.setVisibility(View.INVISIBLE);
            }
        });

        tv_rs50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(R.string.p_rs_50);
                btn_payment.setVisibility(View.INVISIBLE);
            }
        });

        tv_rs300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(R.string.p_rs_300);
                btn_payment.setVisibility(View.INVISIBLE);
            }
        });

        tv_rs500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(R.string.p_rs_500);
                btn_payment.setVisibility(View.INVISIBLE);
            }
        });

        tv_rs1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(R.string.p_rs_1000);
                btn_payment.setVisibility(View.INVISIBLE);
            }
        });

        ref_bal = saveInfoLocally.getReferralBalance();
        final String r_disp_bal = "₹" + ref_bal;
        tv_ref_disp_amt.setText(r_disp_bal);

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","Retrieved Referral balance from SharedPreferences : "+ref_bal+"\n");

    }

    private String getRandomString(){
        int n = 4;
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
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
            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "isNumber()","Verifying the No. Entered by the User : "+ amt +"\n");
            return true;
        } catch (NumberFormatException e) {
            // Storing the Logs in the Logger.
            logger.writeLog(TAG,"isNumber()", e.getMessage());
            return false;
        }

    }

    public void calculateFinalAmt(View view) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "calculateFinalAmt()","calculateFinalAmt() Func. called\n");

        final String enteredAmt = et_input.getText().toString();

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "calculateFinalAmt()","Amt. that the User Entered : "+enteredAmt+"\n");

        if (!enteredAmt.equals("") && isNumber(enteredAmt)) {

            // Storing Logs in the Logger.
            logger.writeLog(TAG, "calculateFinalAmt()","Validated the Amt. Entered by the User.\n");

            // This means the user has entered the amt. in the EditText.
            Log.d(TAG, "Entered Amt : "+et_input.getText().toString());

            // For every Txn deducting 5% of the payment value with maximum as Rs.25 from the Bonus(if available) as discount.
            double discount = (Double.valueOf(enteredAmt) * 5)/ 100;
            Log.d(TAG, "5% discount on ₹"+enteredAmt+ " : "+discount);

            // Checking whichever among the discount or Rs.25 is smaller.
            discount = Math.min(discount, 25);

            ref_bal_used = String.valueOf(discount);

            // Referral Balance after discount.
            final double curr_ref_bal = Double.valueOf(ref_bal) - discount;

            final String tot_amt = "₹" + enteredAmt;
            tv_tot_amt.setText(tot_amt);

            final String ref_to_be_used = "₹" + discount;
            tv_ref_amt.setText(ref_to_be_used);

            final String fin_amt = "₹" + (Double.valueOf(enteredAmt) - discount);
            tv_final_amt.setText(fin_amt);

            final String fin_amt_to_be_paid = "Pay " + fin_amt;
            btn_payment.setText(fin_amt_to_be_paid);
            btn_payment.setVisibility(View.VISIBLE);

        } else {

            // Storing Logs in the Logger.
            logger.writeLog(TAG, "calculateFinalAmt()","The Amt. Entered by the User not Validated Successfully, making the Payment Button Invisible.\n");

            btn_payment.setVisibility(View.INVISIBLE);

            Snackbar snackbar = Snackbar.make(constraintLayout, "Please enter an amount!", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.BLUE));
            snackbar.show();

        }

    }

    public void onContinue(View view) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onContinue()","onContinue() Func. called\n");

        String txnAmount = tv_final_amt.getText().toString();
        txnAmount = txnAmount.replace("₹", "");

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onContinue()","TxnAmount to be sent to Paytm : "+txnAmount+"\n");

        generateCheckSum(txnAmount);

    }

    public void afterPaymentConfirm(String ref_bal_used, String Txn_ID, String Order_ID, String Pay_Mode) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "afterPaymentConfirm()","afterPaymentConfirm() Func. called\n");

        // Retrieving the User_Phone_NO from SharedPreferences.
        final String user_phone_no = saveInfoLocally.getPhone();
        // Storing Logs in the Logger.
        logger.writeLog(TAG, "afterPaymentConfirm()","Users Phone No. retrieved from SharedPreferences : "+user_phone_no+"\n");

        try {

            // Now Inserting the Transaction Details into the Invoice_Table.
            final String type3 = "Store_Invoice";
            // Converting the required values to String
            String tot_retailer_price = et_input.getText().toString();
            Log.d(TAG, "Total_Retailer_Price for Storing in Invoice : "+tot_retailer_price);

            final String tot_mrp = tot_retailer_price;
            final String tot_our_price = tot_retailer_price;

            final String total_discount = "0";

            String txnAmount = tv_final_amt.getText().toString();
            txnAmount = txnAmount.replace("₹", "");

            // TODO:// Add Code to fetch comments when Store_ID = "3", for now its "";
            String rest = new BackgroundWorker(this).execute(type3, user_phone_no, tot_mrp, total_discount, txnAmount, ref_bal_used, "", Txn_ID, Order_ID, Pay_Mode, tot_retailer_price, tot_our_price).get();
            Log.d(TAG, "Invoice Result : " + rest);

            // Storing Logs in the Logger.
            logger.writeLog(TAG, "afterPaymentConfirm()","Store Invoice Result : "+rest+"\n");

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
                        comment = jobj2.getString("comment");
                    }

                } catch (JSONException e) {
                    // Storing Logs in the Logger.
                    logger.writeLog(TAG, "afterPaymentConfirm()",e.getMessage());
                    e.printStackTrace();
                }
                // If Invoice is Successfully Pushed to DB, then Send the Invoice SMS to the user.
                final String type4 = "Send_Invoice_Msg";
                final String sms_res = new BackgroundWorker(this).execute(type4, time, final_user_amt, comment, receipt_no, tot_retail_price, ref_bal_used, tot_discount, to_our_price).get();
                // Storing Logs in the Logger.
                logger.writeLog(TAG, "afterPaymentConfirm()","'Send_Invoice_Msg' BackgroundWorker called. Result : "+sms_res+" \n");

                // Then we have to send the Invoice_Msg to the Retailer also.
                Log.d(TAG, "Sending Retailer Invoice Sms");
                final String type5 = "Send_Retailer_Invoice_Msg";
                final String sms = new AwsBackgroundWorker(this).execute(type5, time, final_user_amt, receipt_no, tot_retail_price).get();
                // Storing Logs in the Logger.
                logger.writeLog(TAG, "afterPaymentConfirm()","'Send_Retailer_Invoice_Msg' BackgroundWorker called. Result : "+sms+" \n");

                // Storing the Details in txnData ArrayList.
                txnData.add(receipt_no);
                txnData.add(tot_discount);
                txnData.add(tot_retail_price);
                txnData.add(ref_bal_used);
                txnData.add(to_our_price);
                txnData.add(final_user_amt);
                txnData.add(time);
                txnData.add(Pay_Mode);
                txnData.add("1");
                // Adding the txnData ArrayList to txnReceipt Bundle.
                txnReceipt.putStringArrayList("txnReceipt", txnData);
                Log.d(TAG, "Stored Required Details in Bundle");
                // Storing Logs in the Logger.
                logger.writeLog(TAG, "afterPaymentConfirm()","Invoice Details : "+txnReceipt.toString()+"\n");

            }

        } catch (NullPointerException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "afterPaymentConfirm()",e.getMessage());
        }

    }

    private void generateCheckSum(String txnAmount) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "generateCheckSum()","generateCheckSum() Func. called\n");

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

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "initializePaytmPayment()","initializePaytmPayment() Func. called\n");

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

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "initializePaytmPayment()","Data being sent to Paytm : "+paramMap.toString()+"\n");


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, this);

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onTransactionResponse()","onTransactionResponse() Func. called\n");

        Log.d(TAG, "From onTransactionResponse : "+bundle.toString());
        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onTransactionResponse()","Response Bundle : "+bundle.toString()+"\n");

        try {
            // Retrieving the Txn_Details from the TxnResponse i.e., bundle.
            final String txn_status = bundle.get("STATUS").toString();
            final String Txn_ID = bundle.get("TXNID").toString();
            final String Order_ID = bundle.get("ORDERID").toString();
            final String Pay_Mode = bundle.get("PAYMENTMODE").toString();
            final String Time = bundle.get("TXNDATE").toString();
            // Verifying if the Transaction was Successful or not.
            if (txn_status.equals("TXN_SUCCESS")) {

                // Storing Logs in the Logger.
                logger.writeLog(TAG, "onTransactionResponse()","TXN Successful Status was given, now calling 're-verify_checksum'.\n");
                // Forwarding the Order_id to the Server for the Re-Verification Process.
                final String type1 = "re-verify_checksum";
                final String res1 = new BackgroundWorker(this).execute(type1, Order_ID).get();
                // Storing Logs in the Logger.
                logger.writeLog(TAG, "onTransactionResponse()","'re-verify_checksum' Result : "+res1+".\n");
                // Converting the result String in form of JSONObject from the response to JSONObject.
                JSONObject jobj = new JSONObject(res1);
                // Extracting the required value from JSONObject.
                final String status = jobj.getString("STATUS");
                Log.d(TAG, "Re-verify Status : " + status);

                // Re-Verifying if the Transaction was Successful or not.
                if (status.equals("TXN_SUCCESS")) {

                    // Storing Logs in the Logger.
                    logger.writeLog(TAG, "onTransactionResponse()","Again the Txn was Successful, now calling afterPaymentConfirm().\n");
                    // Doing all the things to be done after Successful Payment.
                    afterPaymentConfirm(ref_bal_used, Txn_ID, Order_ID, Pay_Mode);
                    // Saving the Referral_Balance Value to SharedPreference.
                    saveInfoLocally.setReferralBalance("0");
                    // Storing Logs in the Logger.
                    logger.writeLog(TAG, "onTransactionResponse()","Setting the Referral Balance's Value to 0.\n");
                    // Storing Logs in the Logger.
                    logger.writeLog(TAG, "onTransactionResponse()","Intent Values sent to PaymentSuccess referral_balance_used : "+ref_bal_used+".\n");
                    // Intent to PaymentSuccess Activity.
                    Intent in = new Intent(this, PaymentSuccess.class);
                    in.putExtra("referral_balance_used", ref_bal_used);
                    in.putExtras(txnReceipt);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);

                } else {

                    // Storing Logs in the Logger.
                    logger.writeLog(TAG, "onTransactionResponse()","The Txn was Unsuccessful, routing the user to PaymentFailed.\n");

                    final String Error_MSG = bundle.get("errorMessage").toString();
                    // If the Re-verification of the Txn Fails.
                    Log.d(TAG, "Payment Verification Failed.");
                    // Storing Logs in the Logger.
                    logger.writeLog(TAG, "onTransactionResponse()","Intent Values Order_Time : "+Time+", Pay_Mode : "+Pay_Mode+", Error_Msg : "+Error_MSG+".\n");
                    Intent in = new Intent(Payment.this, PaymentFailed.class);
                    in.putExtra("Order_Time", Time);
                    in.putExtra("Payment_Mode", Pay_Mode);
                    in.putExtra("Error_MSG", Error_MSG);
                    startActivity(in);
                }

            } else {

                // Storing Logs in the Logger.
                logger.writeLog(TAG, "onTransactionResponse()","The Txn was Unsuccessful, routing the user to PaymentFailed.\n");

                final String Error_MSG = bundle.get("errorMessage").toString();
                // If the Txn Fails.
                Log.d(TAG, "Payment Failed.");
                // Storing Logs in the Logger.
                logger.writeLog(TAG, "onTransactionResponse()","Intent Values Order_Time : "+Time+", Pay_Mode : "+Pay_Mode+", Error_Msg : "+Error_MSG+".\n");
                Intent in = new Intent(Payment.this, PaymentFailed.class);
                in.putExtra("Order_Time", Time);
                in.putExtra("Payment_Mode", Pay_Mode);
                in.putExtra("Error_MSG", Error_MSG);
                startActivity(in);
            }
        } catch (NullPointerException | JSONException | ExecutionException | InterruptedException e) {
            // Storing Logs in the Logger.
            logger.writeLog(TAG, "onTransactionResponse()",e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void networkNotAvailable() {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "networkNotAvailable()","networkNotAvailable() Func. called\n");

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "clientAuthenticationFailed()","clientAuthenticationFailed() Func. called\n");

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "someUIErrorOccurred()","someUIErrorOccurred() Func. called\n");

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onErrorLoadingWebPage()","onErrorLoadingWebPage() Func. called\n");

    }

    @Override
    public void onBackPressedCancelTransaction() {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onBackPressedCancelTransaction()","onBackPressedCancelTransaction() Func. called\n");

        final String pumpName = saveInfoLocally.getPumpName();
        final String pumpAddr = saveInfoLocally.getPumpAddress() + ", " + saveInfoLocally.getStoreCity();
        final String p_name = pumpName + ", " + pumpAddr;

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onBackPressedCancelTransaction()","Values in Intent to Payment pump_name "+p_name+"\n");

        Intent in = new Intent(Payment.this, Payment.class);
        in.putExtra("pump_name", p_name);
        startActivity(in);

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onTransactionCancel()","onTransactionCancel() Func. called\n");

    }

}