package com.younoq.noq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.younoq.noq.classes.City;
import com.younoq.noq.views.MyProfile;
import com.younoq.noq.R;
import com.younoq.noq.models.SaveInfoLocally;

import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityAdapterViewHolder> {

    private List<City> citiesList;
    private Context context;
    private String phoneNo, isDirectLogin;

    public CityAdapter(Context ctx, List<City> cList, String phoneNo, String isDirectLogin) {
        this.context = ctx;
        citiesList = cList;
        this.phoneNo = phoneNo;
        this.isDirectLogin = isDirectLogin;
    }

    @NonNull
    @Override
    public CityAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.city_card, parent, false);
        return new CityAdapter.CityAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapterViewHolder holder, int position) {

        City city = citiesList.get(holder.getAdapterPosition());

        final String city_name = city.getCity_name();
        holder.tv_city_name.setText(city_name);

        final String cityname = city_name.toLowerCase() + ".png";
        final String url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/images/store_city_images/"+cityname;

        Picasso.get()
                .load(url)
                .resize(100, 90)
                .placeholder(R.drawable.ic_city_pin)
                .into(holder.im_city_image);

        final boolean city_exists = city.getExists().toLowerCase().equals("true");
        if(city_exists)
            holder.tv_city_exists.setVisibility(View.INVISIBLE);
        else
            holder.tv_city_exists.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() { return citiesList.size(); }

    class CityAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_city_name, tv_city_exists;
        ImageView im_city_image;
        SaveInfoLocally saveInfoLocally;

        public CityAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_city_name = itemView.findViewById(R.id.city_c_name);
            tv_city_exists = itemView.findViewById(R.id.city_exists);
            im_city_image = itemView.findViewById(R.id.city_image);

            saveInfoLocally = new SaveInfoLocally(itemView.getContext());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    City city = citiesList.get(getAdapterPosition());

                    // If city Exists then only Route to MyProfile.
                    if(city.getExists().toLowerCase().equals("true")){

                        // Storing the City in the SharedPreferences
                        saveInfoLocally.setStoreCity(city.getCity_name());

                        Intent in = new Intent(v.getContext(), MyProfile.class);
                        in.putExtra("Phone", phoneNo);
                        in.putExtra("City", city.getCity_name());
                        in.putExtra("isDirectLogin", false);
                        v.getContext().startActivity(in);

                    } else {
                        Toast.makeText(v.getContext(), "This city isn't available yet", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }
}
