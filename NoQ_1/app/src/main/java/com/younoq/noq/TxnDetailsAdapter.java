package com.younoq.noq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TxnDetailsAdapter extends RecyclerView.Adapter<TxnDetailsAdapter.TxnDetailViewHolder> {

    Context context;
    List<Product> productList;

    public TxnDetailsAdapter(Context ctx, List<Product> prodList) {

        this.context = ctx;
        productList = prodList;

    }


    @NonNull
    @Override
    public TxnDetailsAdapter.TxnDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.txn_details_card, null);
        return new TxnDetailsAdapter.TxnDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TxnDetailsAdapter.TxnDetailViewHolder holder, int position) {

        Product product = productList.get(position);

        boolean isExpanded = product.isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        final String p_name = product.getProduct_name();
        holder.tv_p_name.setText(p_name);

        String tot_amt = "";

        if (isExpanded) {
            holder.separator.setVisibility(View.VISIBLE);
            tot_amt = "₹" + product.getTot_amt();
        } else {
            holder.separator.setVisibility(View.GONE);
            tot_amt = "₹" + product.getOur_price() + " x " + product.getCurrent_qty();
        }

        holder.tv_tot_amt.setText(tot_amt);

        final String barcode = product.getBarcode();
        holder.tv_barcode.setText(barcode);

        final String no_of_items = product.getCurrent_qty();
        holder.tv_no_of_items.setText(no_of_items);

        final String retailer_price = "₹" + product.getRetailers_price();
        holder.tv_retailer_price.setText(retailer_price);

        final String our_price = "₹" + product.getOur_price();
        holder.tv_our_price.setText(our_price);

        final String total_discount = product.getTotal_discount();
        holder.tv_total_discount.setText(total_discount);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class TxnDetailViewHolder extends RecyclerView.ViewHolder {

        TextView tv_p_name, tv_tot_amt, tv_barcode, tv_no_of_items, tv_retailer_price, tv_our_price, tv_total_discount;
        ConstraintLayout expandableLayout;
        LinearLayout linearLayout;
        View separator;

        public TxnDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            expandableLayout = itemView.findViewById(R.id.tdc_expandable_constraint_layout);
            linearLayout = itemView.findViewById(R.id.tdc_linear0);
            separator = itemView.findViewById(R.id.tcd_separator);

            tv_p_name = itemView.findViewById(R.id.tdc_p_name);
            tv_tot_amt = itemView.findViewById(R.id.tdc_tot_amt);
            tv_barcode = itemView.findViewById(R.id.tdc_bcode);
            tv_no_of_items = itemView.findViewById(R.id.tdc_no_of_items);
            tv_retailer_price = itemView.findViewById(R.id.tdc_retailer_price);
            tv_our_price = itemView.findViewById(R.id.tdc_our_price);
            tv_total_discount = itemView.findViewById(R.id.tdc_total_discount);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = productList.get(getAdapterPosition());
                    product.setExpanded(!product.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

    }
}
