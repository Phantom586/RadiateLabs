package com.younoq.noq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TxnAdapter extends RecyclerView.Adapter<TxnAdapter.TxnViewHolder> {

    private Context context;
    private List<Txn> txnList;

    public TxnAdapter(Context ctx, List<Txn> txList) {
        this.context = ctx;
        txnList = txList;
    }

    @NonNull
    @Override
    public TxnAdapter.TxnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.txn_card, null);
        return new TxnAdapter.TxnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TxnAdapter.TxnViewHolder holder, int position) {

        Txn txn = txnList.get(position);

        final String store_name = "Paid for " + txn.getStore_name();
        holder.tv_paid_for.setText(store_name);
//        System.out.println("Store Name : "+store_name);

        final String amt_paid = "₹" + txn.getFinal_amt();
        holder.tv_amt_paid.setText(amt_paid);
//        System.out.println("Amount Paid : "+amt_paid);

        final String timestamp = txn.getTimestamp();
        try {

            Date date;
            // Converting String Date to Date Object.
            date = holder.inputDateFormat.parse(timestamp);

            final String month_date = holder.outputDateFormat.format(date);
            holder.tv_month_date.setText(month_date);
//            System.out.println("Month Date : "+month_date);

            final String time = holder.outputTimeFormat.format(date);
            holder.tv_time.setText(time);
//            System.out.println("Time : "+time+"\n");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Setting the Total No. of Items in the Txn.
        holder.tv_no_of_items.setText(txn.getTotal_items());


    }

    @Override
    public int getItemCount() { return txnList.size(); }

    class TxnViewHolder extends RecyclerView.ViewHolder {

        SimpleDateFormat inputDateFormat, outputDateFormat, outputTimeFormat;
        TextView tv_paid_for, tv_amt_paid, tv_month_date, tv_time, tv_no_of_items;

        public TxnViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle txnData = new Bundle();
                    ArrayList<String> TxnDetailList = new ArrayList<>();

                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        Txn txn = txnList.get(position);
                        TxnDetailList.add(txn.getReceipt_no());
                        TxnDetailList.add(txn.getFinal_amt());
                        TxnDetailList.add(txn.getTimestamp());
                        TxnDetailList.add(txn.getPayment_mode());
                        TxnDetailList.add(txn.getStore_name());
                        TxnDetailList.add(txn.getStore_addr());
                        TxnDetailList.add(txn.getStore_city());
                        TxnDetailList.add(txn.getStore_state());

                        //Adding TxnDetail List to txnData Bundle.
                        txnData.putStringArrayList("txnDetail", TxnDetailList);
                        // Adding JSONArray as String to txnData Bundle.
                        txnData.putString("txnProductArray", txn.getProducts().toString());
                    }

                    Intent in = new Intent(v.getContext(), TxnDetails.class);
                    // Adding the txnData Bundle to Intent.
                    in.putExtras(txnData);
                    v.getContext().startActivity(in);

                }
            });

            tv_paid_for = itemView.findViewById(R.id.tc_paid_for);
            tv_amt_paid = itemView.findViewById(R.id.tc_amt_paid);
            tv_month_date = itemView.findViewById(R.id.tc_month_date);
            tv_time = itemView.findViewById(R.id.tc_time);
            tv_no_of_items = itemView.findViewById(R.id.tc_no_of_items);

            inputDateFormat = new SimpleDateFormat("yyyy-mm-d HH:mm:ss");
            outputDateFormat = new SimpleDateFormat("MMM dd");
            outputTimeFormat = new SimpleDateFormat("HH:mm a");

        }

    }
}
