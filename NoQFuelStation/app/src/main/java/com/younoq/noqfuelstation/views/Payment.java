package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Payment extends AppCompatActivity {

    private TextView tv_ref_disp_amt, tv_tot_amt, tv_ref_amt, tv_final_amt, tv_rs50, tv_rs300, tv_rs500, tv_rs1000, tv_pump_name;
    private final String TAG = "PaymentActivity";
    private SaveInfoLocally saveInfoLocally;
    private String pump_name, ref_bal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        saveInfoLocally = new SaveInfoLocally(this);
        tv_ref_disp_amt = findViewById(R.id.p_disp_referral_amt);
        tv_tot_amt = findViewById(R.id.p_total_amt);
        tv_ref_amt = findViewById(R.id.p_referral_amt);
        tv_final_amt = findViewById(R.id.p_final_amt);
        tv_pump_name = findViewById(R.id.p_pp_name);
        tv_rs50 = findViewById(R.id.p_rs50);
        tv_rs300 = findViewById(R.id.p_rs300);
        tv_rs500 = findViewById(R.id.p_rs500);
        tv_rs1000 = findViewById(R.id.p_rs1000);

        Intent in = getIntent();
        pump_name = in.getStringExtra("pump_name");
        tv_pump_name.setText(pump_name);

        ref_bal = saveInfoLocally.getReferralBalance();
        final String r_disp_bal = "₹" + ref_bal;
        tv_ref_disp_amt.setText(r_disp_bal);


    }

    public void onContinue(View view) {
    }
}