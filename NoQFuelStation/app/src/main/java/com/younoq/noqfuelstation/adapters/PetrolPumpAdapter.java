package com.younoq.noqfuelstation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.younoq.noqfuelstation.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noqfuelstation.classes.PetrolPump;
import com.younoq.noqfuelstation.views.Payment;

import java.util.List;

public class PetrolPumpAdapter extends RecyclerView.Adapter<PetrolPumpAdapter.PetrolPumpViewHolder> {

    private List<PetrolPump> petrolPumpList;
    private Context context;

    public PetrolPumpAdapter(Context ctx, List<PetrolPump> ppList) {

        this.context = ctx;
        petrolPumpList = ppList;

    }

    @NonNull
    @Override
    public PetrolPumpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.petrol_pump_card_item, parent, false);
        return new PetrolPumpAdapter.PetrolPumpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetrolPumpViewHolder holder, int position) {

        final PetrolPump petrolPump = petrolPumpList.get(holder.getAdapterPosition());

        final String img_name = petrolPump.getImage();

        final String url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/images/store_images/"+img_name;

        Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.ic_city_pin)
                .into(holder.im_store_image);

        final String s_name = petrolPump.getName();
        holder.tv_store_name.setText(s_name);
        final String s_addr = petrolPump.getAddress() + ", " + petrolPump.getCity();
        holder.tv_store_addr.setText(s_addr);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(v.getContext(), Payment.class);
                final String p_name = petrolPump.getName() + ", " + s_addr;
                in.putExtra("pump_name", p_name);
                v.getContext().startActivity(in);

            }
        });

    }

    @Override
    public int getItemCount() { return petrolPumpList.size(); }

    public class PetrolPumpViewHolder extends RecyclerView.ViewHolder {

        TextView tv_store_name, tv_store_addr;
        ImageView im_store_image;

        public PetrolPumpViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_store_name = itemView.findViewById(R.id.ppci_store_name);
            tv_store_addr = itemView.findViewById(R.id.ppci_store_addr);
            im_store_image = itemView.findViewById(R.id.ppci_store_img);

        }
    }

}