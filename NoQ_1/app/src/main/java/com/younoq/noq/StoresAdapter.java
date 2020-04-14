package com.younoq.noq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.StoresViewHolder> {

    private Context ctx;
    private List<Store> StoreList;
    public static final String TAG = "ProductAdapter";

    public StoresAdapter(Context ctx, List<Store> stList) {
        this.ctx = ctx;
        StoreList = stList;
    }

    @NonNull
    @Override
    public StoresAdapter.StoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.noq_stores_card, null);
        return new StoresAdapter.StoresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresAdapter.StoresViewHolder holder, int position) {
        Store store = StoreList.get(position);

        final String s_name = store.getStore_name();
        holder.tv_store_name.setText(s_name);
        final String s_addr = store.getStore_address() + ", " + store.getStore_city();
        holder.tv_store_addr.setText(s_addr);
        final String s_loc = store.getStore_state() + ", " + store.getStore_country();
        holder.tv_store_loc.setText(s_loc);
//        final String pin = store.getPincode();
//        holder.tv_pin.setText(pin);


    }

    @Override
    public int getItemCount() {
        return StoreList.size();
    }

    class StoresViewHolder extends RecyclerView.ViewHolder {

        TextView tv_store_name, tv_store_addr, tv_store_loc, tv_pin;

        public StoresViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    Store store = StoreList.get(position);

                    final String s_id = store.getStore_id();
                    if (s_id.equals("1")) {

//                        Bundle txnData = new Bundle();
//                        ArrayList<String> TxnDetailList = new ArrayList<>();
//
//                        TxnDetailList.add(store.getStore_name());
//                        TxnDetailList.add(store.getStore_address());
//                        TxnDetailList.add(store.getStore_city());
//                        TxnDetailList.add(store.getStore_state());
//                        TxnDetailList.add(store.getStore_country());
//
//                        txnData.putStringArrayList("storeData", TxnDetailList);

//                        Intent in = new Intent(v.getContext(), ShopDetails.class);
                        SaveInfoLocally saveInfoLocally = new SaveInfoLocally(v.getContext());
                        saveInfoLocally.set_store_id(store.getStore_id());
                        saveInfoLocally.setStoreName(store.getStore_name());
                        saveInfoLocally.setStoreAddress(store.getStore_address());
                        Intent in = new Intent(v.getContext(), BarcodeScannerActivity.class);
                        in.putExtra("Type", "Product_Scan");
//                        in.putExtra("result", s_id);
//                        in.putExtra("barcode", s_id);
//                        in.putExtras(txnData);
                        v.getContext().startActivity(in);
                    } else {
                        Toast.makeText(v.getContext(), "Service is Temporarily Unavailable in this Store", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            tv_store_name = itemView.findViewById(R.id.ns_store_name);
            tv_store_addr = itemView.findViewById(R.id.ns_store_addr);
            tv_store_loc = itemView.findViewById(R.id.ns_store_loc);
//            tv_pin = itemView.findViewById(R.id.ns_store_pin);

        }

    }

}
