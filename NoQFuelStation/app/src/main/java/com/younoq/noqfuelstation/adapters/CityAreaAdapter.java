package com.younoq.noqfuelstation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.younoq.noqfuelstation.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noqfuelstation.classes.CityArea;
import com.younoq.noqfuelstation.models.SaveInfoLocally;
import com.younoq.noqfuelstation.views.PetrolPumpsNoq;

import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class CityAreaAdapter extends RecyclerView.Adapter<CityAreaAdapter.CityAreaViewHolder> {

    private SaveInfoLocally saveInfoLocally;
    private List<CityArea> cityAreaList;
    private String city_name, phone_no;
    private Context context;

    public CityAreaAdapter(Context ctx, List<CityArea> caList, String city_name, String p_no) {
        this.context = ctx;
        this.saveInfoLocally = new SaveInfoLocally(ctx);
        this.city_name = city_name;
        phone_no = p_no;
        cityAreaList = caList;
    }

    @NonNull
    @Override
    public CityAreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.city_area_card_item, parent, false);
        return new CityAreaAdapter.CityAreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAreaViewHolder holder, int position) {

        final CityArea cityArea = cityAreaList.get(holder.getAdapterPosition());

        final String cityarea = cityArea.getCityArea();

        if (!cityarea.equals(""))
            holder.tv_city_area.setText(cityarea);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfoLocally.setStoreCity(city_name);
                saveInfoLocally.setStoreCityArea(cityArea.getCityArea());

                Intent in = new Intent(v.getContext(), PetrolPumpsNoq.class);
                in.putExtra("Phone", phone_no);
                in.putExtra("isDirectLogin", false);
                v.getContext().startActivity(in);
            }
        });

    }

    @Override
    public int getItemCount() { return cityAreaList.size(); }

    public class CityAreaViewHolder extends RecyclerView.ViewHolder {

        TextView tv_city_area;

        public CityAreaViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_city_area = itemView.findViewById(R.id.caci_city_area);

        }
    }

}
