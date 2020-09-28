package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.younoq.noq.R;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.SaveInfoLocally;

import android.os.Bundle;
import android.view.View;

import java.util.concurrent.ExecutionException;

public class ReturnAndRefund extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_and_refund);

        constraintLayout = findViewById(R.id.rar_constraint_layout);
        saveInfoLocally = new SaveInfoLocally(this);

    }

    public void onContinue(View view) {

        final String type = "sendRefundAndReturnSms";
        final String phone = saveInfoLocally.getPhone();
        final String name = saveInfoLocally.getUserName();
        final String sent_to = "+917007502265";

        try {

            final String msg = name + ", Phone No : " + phone + ", has applied for the Refund & Return, kindly get in touch with them asap.";
            final String res = new AwsBackgroundWorker(this).execute(type, msg, sent_to).get();

            Snackbar snackbar = Snackbar.make(constraintLayout, "Your request has been recorded, we will get back to you soon!", Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.BLUE));
            snackbar.show();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void go_back(View view) { super.onBackPressed(); }
}