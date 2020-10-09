package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PaymentFailed extends AppCompatActivity {

    private TextView tv_shop_details, tv_timestamp, tv_pay_method, tv_error_msg;
    private SimpleDateFormat inputDateFormat, outputDateFormat, timeFormat;
    private String payment_mode, order_time, error_msg;
    private SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failed);

        saveInfoLocally = new SaveInfoLocally(this);
        tv_shop_details = findViewById(R.id.pf_shop_name);
        tv_error_msg = findViewById(R.id.pf_error_msg);
        tv_timestamp = findViewById(R.id.pf_timestamp);
        tv_pay_method = findViewById(R.id.pf_pay_method);

        inputDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.ENGLISH);
        outputDateFormat = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        Intent in = getIntent();
        error_msg = in.getStringExtra("Error_MSG");
        order_time = in.getStringExtra("Order_Time");
        payment_mode = in.getStringExtra("Payment_Mode");

        setPaymentDetails();

    }

    private void setPaymentDetails() {

        tv_error_msg.setText(error_msg);

        // Setting TxnDetails.
        final String store_addr = saveInfoLocally.getPumpName() + ", " + saveInfoLocally.getPumpAddress();
        tv_shop_details.setText(store_addr);

        final String time = order_time;
        try {
            Date date = inputDateFormat.parse(time);

            final String timestamp = outputDateFormat.format(date) + ", " + timeFormat.format(date);
            tv_timestamp.setText(timestamp);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_pay_method.setText(payment_mode);

    }

    public void Go_to_Home(View view) {

        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentFailed.this, PetrolPumpsNoq.class);
        in.putExtra("Phone", phone);
        startActivity(in);

    }

    public void Go_to_Payment(View view) {

        super.onBackPressed();

    }
}