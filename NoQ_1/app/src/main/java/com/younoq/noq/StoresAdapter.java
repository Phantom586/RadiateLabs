package com.younoq.noq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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

            tv_store_name = itemView.findViewById(R.id.ns_store_name);
            tv_store_addr = itemView.findViewById(R.id.ns_store_addr);
            tv_store_loc = itemView.findViewById(R.id.ns_store_loc);
//            tv_pin = itemView.findViewById(R.id.ns_store_pin);

        }

    }

}
