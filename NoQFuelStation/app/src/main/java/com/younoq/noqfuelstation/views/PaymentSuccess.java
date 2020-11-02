package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.AwsBackgroundWorker;
import com.younoq.noqfuelstation.models.Logger;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PaymentSuccess extends AppCompatActivity {

    private TextView tv1, tv_receipt_no, tv_final_amt, tv_you_saved, tv_shop_details, tv_timestamp,
            tv_pay_method, tv_thanks, tv_total_amt;
    SimpleDateFormat inputDateFormat, outputDateFormat, timeFormat;
    private String TAG = "PaymentSuccessActivity";
    private SaveInfoLocally saveInfoLocally;
    private ArrayList<String> txnData;
    private String ref_bal_used;
    private Bundle txnReceipt;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        saveInfoLocally = new SaveInfoLocally(this);

        inputDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.ENGLISH);
        outputDateFormat = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        tv_shop_details = findViewById(R.id.ps_shop_name);
        tv_pay_method = findViewById(R.id.ps_pay_method);
        tv_receipt_no = findViewById(R.id.ps_receipt_no);
        tv_you_saved = findViewById(R.id.ps_you_saved);
        tv_final_amt = findViewById(R.id.ps_final_amt);
        tv_timestamp = findViewById(R.id.ps_timestamp);
        tv_total_amt = findViewById(R.id.ps_total_amt);
        tv_thanks = findViewById(R.id.tv_p_thanks);
        tv1 = findViewById(R.id.tv_succ);
        logger = new Logger(this);
        txnData = new ArrayList<>();
        txnReceipt = new Bundle();

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","onCreate() Func. called\n");

        Intent in = getIntent();
        ref_bal_used = in.getStringExtra("referral_balance_used");
        txnReceipt = in.getExtras();
        txnData = txnReceipt.getStringArrayList("txnReceipt");

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","Values received from Intent ref_bal_used : "+ref_bal_used+", txnReceipt "+txnReceipt.toString()+"\n");

        pushUpdatesToDatabase();

        showPaymentDetails();

    }

    private void pushUpdatesToDatabase() {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "pushUpdatesToDatabase()","pushUpdatesToDatabase() Func. called\n");

        final String phone = saveInfoLocally.getPhone();
        try {
            /* Pushing the Referral_Amount_Used to Users_Table. */
            final String type = "update_referral_used";
            final String res = new AwsBackgroundWorker(this).execute(type, phone, ref_bal_used).get();
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "pushUpdatesToDatabase()","BackgroundWorker 'update_referral_used' called. Result : "+res+"\n");

            /* Now, when the Referral_Amount_Used is updated, now we can calculate the Referral_Amount_Balance. */
            final String type2 = "update_referral_balance";
            final String res2 = new AwsBackgroundWorker(this).execute(type2, phone).get();
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "pushUpdatesToDatabase()","BackgroundWorker 'update_referral_balance' called. Result : "+res2+"\n");

        } catch (Exception e) {
            e.printStackTrace();
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "pushUpdatesToDatabase()",e.getMessage());
        }

    }

    private void showPaymentDetails() {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "showPaymentDetails()","showPaymentDetails() Func. called\n");

        /* Setting TxnDetails */
        final String pumpAddr = saveInfoLocally.getPumpName() + ", " + saveInfoLocally.getPumpAddress();
        tv_shop_details.setText(pumpAddr);

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "showPaymentDetails()","Pump Address Retrieved from SharedPreferences : "+pumpAddr+"\n");

        final String time = txnData.get(6);
        try {
            Date date = inputDateFormat.parse(time);

            final String timestamp = outputDateFormat.format(date) + ", " + timeFormat.format(date);
            tv_timestamp.setText(timestamp);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_receipt_no.setText(txnData.get(0));

        final String tot_amt = "₹ " + txnData.get(2);
        tv_total_amt.setText(tot_amt);

        final String savings_by_us = "₹ " + txnData.get(9);
        tv_you_saved.setText(savings_by_us);
        final String final_amt = "₹" + txnData.get(5);
        tv_final_amt.setText(final_amt);

        String pay_method = txnData.get(7);
        if(pay_method.equals("[Referral_Used]"))
            pay_method = "NoQ Cash";
        tv_pay_method.setText(pay_method);

    }

    public void lastfivetxns(View view) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "lastfivetxns()","lastfivetxns() Func. called\n");

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "lastfivetxns()","Routing the User to LastFiveTxns.\n");
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, LastFiveTxns.class);
        in.putExtra("Phone", phone);
        startActivity(in);

    }

    public void Go_to_Home(View view) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "Go_to_Home()","Go_to_Home() Func. called\n");

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "lastfivetxns()","Routing the User to PetrolPumpsNoq.\n");
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentSuccess.this, PetrolPumpsNoq.class);
        in.putExtra("Phone", phone);
        startActivity(in);

    }

    @Override
    public void onBackPressed() {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onBackPressed()","onBackPressed() Func. called\n");
        /* Generating new Session ID, if User Clicks on Continue button */
        final String phone = saveInfoLocally.getPhone();
        try {
            final String sess = toHexString(getSHA(getRandomString()+phone+getRandomString()));
            Log.d(TAG, "Session Id : "+sess);
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "onBackPressed()","generated new SessionID : "+sess+"\n");
            saveInfoLocally.setSessionID(sess);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "onBackPressed()",e.getMessage());
        }

        final String pumpName = saveInfoLocally.getPumpName();
        final String pumpAddr = saveInfoLocally.getPumpAddress() + ", " + saveInfoLocally.getStoreCity();
        final String p_name = pumpName + ", " + pumpAddr;

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onBackPressed()","Routing the User to Payment, pump_name : "+p_name+"\n");
        Intent in = new Intent(PaymentSuccess.this, Payment.class);
        in.putExtra("coming_from", "PaymentSuccess");
        in.putExtra("pump_name", p_name);
        startActivity(in);

    }

    public void Go_to_Payment(View view) {

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "Go_to_Payment()","Go_to_Payment() Func. called\n");
        /* Generating new Session ID, if User Clicks on Continue button */
        final String phone = saveInfoLocally.getPhone();
        try {
            final String sess = toHexString(getSHA(getRandomString()+phone+getRandomString()));
            Log.d(TAG, "Session Id : "+sess);
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "onBackPressed()","generated new SessionID : "+sess+"\n");
            saveInfoLocally.setSessionID(sess);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
           /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "onBackPressed()",e.getMessage());
        }

        final String pumpName = saveInfoLocally.getPumpName();
        final String pumpAddr = saveInfoLocally.getPumpAddress() + ", " + saveInfoLocally.getStoreCity();
        final String p_name = pumpName + ", " + pumpAddr;

       /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onBackPressed()","Routing the User to Payment, pump_name : "+p_name+"\n");
        Intent in = new Intent(PaymentSuccess.this, Payment.class);
        in.putExtra("coming_from", "PaymentSuccess");
        in.putExtra("pump_name", p_name);
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

    public void goToPlayStore(View view) {

        final String appPackageName = "com.younoq.noq";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }
}