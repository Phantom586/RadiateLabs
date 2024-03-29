package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.AwsBackgroundWorker;
import com.younoq.noqfuelstation.models.BackgroundWorker;
import com.younoq.noqfuelstation.models.Logger;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static android.view.View.GONE;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class UserCredentialsActivity extends AppCompatActivity {

    private Button regbtn;
    private EditText full_name, referral_no, email;
    private TextView tv, tv1;
    private ProgressBar progressBar;
    private String User_number;
    private Logger logger;

    private String verified = "false";

    private SaveInfoLocally save_data = new SaveInfoLocally(this);
    final String TAG = "UserCredentialsActivity";

    public static final String Name = "com.example.noq.NAME";
    public static final String Email = "com.example.noq.EMAIL";
    public static String Pno = "";
    public int bonus_amt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credentials);
        setupUI(findViewById(R.id.uca_constraint_layout));

        final String otp_success = "OTP Verified Successfully";

        regbtn = findViewById(R.id.reg_btn);

        full_name = findViewById(R.id.uca_full_name);
        full_name.setFocusable(true);
        full_name.setFocusableInTouchMode(true);
        full_name.requestFocus();

        referral_no = findViewById(R.id.uca_ref_no);
        email = findViewById(R.id.uca_email);
        logger = new Logger(this);

        progressBar = findViewById(R.id.spin_kit);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        progressBar.setVisibility(View.INVISIBLE);

        Intent in = getIntent();
        User_number = in.getStringExtra("Phone");
        Log.d(TAG, "Phone No in UCA : "+User_number);

        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()","User's phone no. in getIntent : "+User_number+"\n");

    }

    private void calculateAndAssignBonusAmt(String phone, String User_Name) {

        final String type = "retrieve_customer_count";
        try {

            final String res = new AwsBackgroundWorker(this).execute(type).get();
            Log.d(TAG, "Customer Count : "+res);
            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "calculateAndAssignBonusAmt()","BackgroundWorker 'retrieve_customer_count' called. Result -> "+res+"\n");

            final JSONObject jobj = new JSONObject(res);

            final int customer_count = Integer.parseInt(jobj.getString("customer_count"));

            /* Value to be updated in the place of customer_count. */
            int next_count = customer_count;

            if (customer_count >= 0 && customer_count <= 3) {
                next_count += 1;
                bonus_amt = 30;
            }
            else if (customer_count > 3 && customer_count <= 6) {
                next_count += 1;
                bonus_amt = 50;
            }
            else if (customer_count > 6 && customer_count <= 8) {
                next_count += 1;
                bonus_amt = 70;
            }
            else if (customer_count == 9) {
                next_count = 0;
                bonus_amt = 100;
            }

            Log.d(TAG, "Bonus Amt : "+bonus_amt+", Next count : "+next_count);
            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "calculateAndAssignBonusAmt()","Bonus Amt : "+bonus_amt+", Next count : "+next_count+"\n");

            final String type0 = "greet_user";
            String res0 = new AwsBackgroundWorker(this).execute(type0, phone, User_Name, String.valueOf(bonus_amt)).get();
            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "calculateAndAssignBonusAmt()","BackgroundWorker 'greet_user' called.Result -> "+res0+"\n");

            final String type1 = "update_bonus_amt";
            final String verify1 = new AwsBackgroundWorker(this).execute(type1, phone, String.valueOf(bonus_amt)).get();
            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "calculateAndAssignBonusAmt()","BackgroundWorker 'update_bonus_amt' called. Result -> "+verify1+"\n");

            final String type2 = "update_customer_count";
            final String res1 = new AwsBackgroundWorker(this).execute(type2, String.valueOf(next_count)).get();
            Log.d(TAG, "Update Customer Count Result : "+res);
            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "calculateAndAssignBonusAmt()","BackgroundWorker 'update_customer_count' called. Result -> "+res1+"\n");

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

    }

    public void Register(View view) throws ExecutionException, InterruptedException {

        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "Register()","User Clicked on Continue Button\n");
        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "Register()","Register() Func. called\n");

        /* Checking if the Register Button was pressed the firstTime or not. */
        final boolean isClickedFirstTime = save_data.isRegisterBtnClickedFirstTime();

        if (isClickedFirstTime) {

            final String f_name = full_name.getText().toString().trim();
            final String user_email = email.getText().toString().trim();
            Pno = referral_no.getText().toString().trim();

            full_name.setError(null);
            referral_no.setError(null);

            View focusView = null;

            Boolean flag = true;
            Boolean flag_phone = false;

            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "Register()","flag_phone = false, flag = true.\n");

            long id1;

            if (TextUtils.isEmpty(f_name)) {

                full_name.setError(getString(R.string.required));
                focusView = full_name;
                /* Storing Logs in the Logger. */
                logger.writeLog(TAG, "Register()","Full Name Entered by user is empty\n");

            } else {

                progressBar.setVisibility(View.VISIBLE);
                final Intent intent;

                if ( !TextUtils.isEmpty(Pno) ) {

                    Pno = "+91"+Pno;
                    /* Pno = "+44"+Pno; */

                    /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "Register()","Added the Country Code to the Entered Referral No. : "+Pno+"\n");

                    if (Pno.length() == 13 ) {

                        if(!User_number.equals(Pno)) {

                            /* Storing Logs in the Logger. */
                            logger.writeLog(TAG, "Register()","Phone No. Entered by the User and Referral No. aren't equal.\n");

                            final String type1 = "verify_user";

                            verified = new BackgroundWorker(this).execute(type1, Pno).get();
                            /* Storing Logs in the Logger. */
                            logger.writeLog(TAG, "Register()","BackgroundWorker 'verify_user' called.\n");

                            Boolean b = Boolean.parseBoolean(verified.trim());
                            Log.d(TAG, "before_Verification : result = " +verified.length());
                            /* Storing Logs in the Logger. */
                            logger.writeLog(TAG, "Register()", "before_Verification : result = " +verified.length()+".\n");

                            if ( b ) {

                                /* Storing Logs in the Logger. */
                                logger.writeLog(TAG, "Register()","Referral No. Entered by the User Exists in the ServerDB. Going to ReferralSuccessful Activity\n");

                                /* tv.setVisibility(View.VISIBLE); */
                                /* Storing Logs in the Logger. */
                                logger.writeLog(TAG, "Register()","flag_phone is set to true.\n");
                                /* Referee exists in the DB, so update Referral Details. */
                                flag_phone = true;

                                intent = new Intent(UserCredentialsActivity.this, ReferralSuccessfulActivity.class);

                            } else {

                                /* Storing Logs in the Logger. */
                                logger.writeLog(TAG, "Register()","Referral No. Entered by the User Doesn't Exists in the ServerDB. Going to ReferralUnsuccessful Activity\n");

                                intent = new Intent(UserCredentialsActivity.this, ReferralUnsuccessfulActivity.class);

                            }

                        } else {

                            /* Storing Logs in the Logger. */
                            logger.writeLog(TAG, "Register()","Phone No. Entered by the User and Referral No. are equal, set flag to false.\n");
                            referral_no.setError("Referral no. can't be same as your number!!");
                            flag = false;
                            intent = new Intent();

                        }

                    } else {

                        /* Setting the RegisterButtonClickedFirstTime to true.*/
                        save_data.setRegisterBtnClickedFirstTime(true);

                        /* Storing Logs in the Logger. */
                        logger.writeLog(TAG, "Register()","Phone No. Entered isn't equal to 13 digits, set flag to false.\n");
                        referral_no.setError(getString(R.string.invalid_phone_number));
                        focusView = referral_no;
                        intent = new Intent();
                        flag = false;

                    }

                } else {

                    /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "Register()","Referral No. wasn't entered, Going to Covid19 Activity\n");
                    /* intent = new Intent(UserCredentialsActivity.this, NoqStores.class); */
                    intent = new Intent(UserCredentialsActivity.this, Covid19.class);
                }

                if ( flag ) {

                    /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "Register()","Referral No. entered by the User is validated, so moving to further tasks.\n");

                    final String type2 = "store_user";
                    String verify = new BackgroundWorker(this).execute(type2, f_name, user_email, User_number, Pno).get();
                    /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "Register()","BackgroundWorker 'store_user' called. Result -> "+verify+"\n");

                    /* Calling Func. to calculate and assign the Bonus Amt. to the user. */
                    calculateAndAssignBonusAmt(User_number, f_name);

                    if ( flag_phone ) {

                        final String type = "update_ref";
                        String update = new BackgroundWorker(this).execute(type, User_number, Pno).get();
                        /* Storing Logs in the Logger. */
                        logger.writeLog(TAG, "Register()","BackgroundWorker 'update_ref' called on both the User's No. and the Referral No. Provided.\n");

                        /* Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the Referred No. */
                        final String type1 = "update_referral_balance";
                        final String isUpdated = new AwsBackgroundWorker(this).execute(type1, Pno).get();
                        Log.d(TAG, "Updated Referee's Referral_Balance : "+isUpdated);
                        /* Storing Logs in the Logger. */
                        logger.writeLog(TAG, "Register()","as the user has entered the referral no. so, BackgroundWorker 'update_bonus_amt' called on referral no. provided. -> Result : "+isUpdated+"\n");

                    /* Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the User No. */
                        /* final String isUpdated1 = new AwsBackgroundWorker(this).execute(type1, User_number).get(); */
                   /* Log.d(TAG, "Updated User's Referral_Balance : "+isUpdated1); */
                    }

                    /* Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the User No. */
                    final String type1 = "update_referral_balance";
                    final String isUpdated1 = new AwsBackgroundWorker(this).execute(type1, User_number).get();
                    Log.d(TAG, "Updated User's Referral_Balance : "+isUpdated1);
                    /* Setting the RegisterButtonClickedFirstTime to false. */
                    save_data.setRegisterBtnClickedFirstTime(false);
                    /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "Register()","BackgroundWorker 'update_bonus_amt' called on User no. -> Result : "+isUpdated1+"\n");

                    saveLoginDetails(User_number);
                    /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "Register()","Saved User Login Details in SharedPreferences.\n");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(GONE);
                            /* tv.setVisibility(GONE); */

                            Toast.makeText(UserCredentialsActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                            intent.putExtra(Name, f_name);
                            intent.putExtra(Email, user_email);
                            intent.putExtra("Phone", User_number);
                            intent.putExtra("activity", "UCA");
                            intent.putExtra("bonus_amt", String.valueOf(bonus_amt));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            /* Storing Logs in the Logger. */
                            logger.writeLog(TAG, "Register()","Values in Intent -> Name : "+f_name+", Email : "+user_email+", Phone_No. : "+User_number+", Activity : UCA" + ", bonus_amt : "+bonus_amt+"\n");
                            startActivity(intent);

                        }
                    }, 600);

                } else {

                    /* Setting the RegisterButtonClickedFirstTime to true. */
                    save_data.setRegisterBtnClickedFirstTime(true);

                    /* Storing Logs in the Logger. */
                    logger.writeLog(TAG, "Register()","Referral Entered By User is Invalid, hence don't go anywhere.\n");
                    progressBar.setVisibility(View.INVISIBLE);

                }

            }

        }

    }

    private void saveLoginDetails(String Phone) {

        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "saveLoginDetails()","saveLoginDetails() Func. called.\n");
        save_data.removeNumber();
        save_data.saveLoginDetails(Phone);

    }

    @Override
    public void onBackPressed() {
        /* Setting the OTPConfirmFirstTime to true. */
        save_data.setBoolean("OTPConfirmFirstTime", true);
        /* Setting the MainActivityFirstTime to true. */
        save_data.setBoolean("MainActivityFirstTime", true);
        super.onBackPressed();
    }

    public void setupUI(View view) {

        /* Set up touch listener for non-text box views to hide keyboard. */
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(UserCredentialsActivity.this);
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}