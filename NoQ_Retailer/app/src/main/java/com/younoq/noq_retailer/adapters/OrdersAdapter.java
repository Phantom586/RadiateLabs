package com.younoq.noq_retailer.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noq_retailer.R;
import com.younoq.noq_retailer.classes.Txn;
import com.younoq.noq_retailer.views.TxnDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersAdapterViewHolder> {

    private List<Txn> txnList;
    private Context context;

    public OrdersAdapter(Context ctx, List<Txn> txList) {

        this.context = ctx;
        txnList = txList;

    }

    @NonNull
    @Override
    public OrdersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.orders_card_item, parent, false);
        return new OrdersAdapter.OrdersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapterViewHolder holder, int position) {

        Txn txn = txnList.get(position);

        final String customer_no = txn.getCustomer_no();
        holder.tv_customer_no.setText(customer_no);

        final String amt_paid = "₹" + txn.getFinal_amt();
        holder.tv_amt_paid.setText(amt_paid);

        final String timestamp = txn.getTimestamp();
        try {

            Date date;
            // Converting String Date to Date Object.
            date = holder.inputDateFormat.parse(timestamp);

            final String month_date = holder.outputDateFormat.format(date) + ", " +holder.outputTimeFormat.format(date);
            holder.tv_timestamp.setText(month_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Setting the Total No. of Items in the Txn.
        holder.tv_no_of_items.setText(txn.getTotal_items());

    }

    @Override
    public int getItemCount() { return txnList.size(); }

    public class OrdersAdapterViewHolder extends RecyclerView.ViewHolder {

        SimpleDateFormat inputDateFormat, outputDateFormat, outputTimeFormat;
        TextView tv_amt_paid, tv_timestamp, tv_no_of_items, tv_customer_no;

        public OrdersAdapterViewHolder(@NonNull View itemView) {
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
                        TxnDetailList.add(txn.getReferral_used());
                        TxnDetailList.add(txn.getTimestamp());
                        TxnDetailList.add(txn.getPayment_mode());
                        TxnDetailList.add(txn.getStore_name());
                        TxnDetailList.add(txn.getStore_addr());
                        TxnDetailList.add(txn.getStore_city());
                        TxnDetailList.add(txn.getStore_state());
                        TxnDetailList.add(txn.getOrder_type());
                        TxnDetailList.add(String.valueOf(txn.getDelivery_duration()));
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

            tv_amt_paid = itemView.findViewById(R.id.oci_amt_paid);
            tv_timestamp = itemView.findViewById(R.id.oci_timestamp);
            tv_no_of_items = itemView.findViewById(R.id.oci_no_of_items);
            tv_customer_no = itemView.findViewById(R.id.oci_customer_no);

            inputDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
            outputDateFormat = new SimpleDateFormat("MMM dd");
            outputTimeFormat = new SimpleDateFormat("hh:mm a");

        }
    }

}
