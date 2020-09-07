package com.younoq.noq_retailer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.younoq.noq_retailer.classes.Category;
import com.younoq.noq_retailer.views.ProductsList;
import com.younoq.noq_retailer.R;

import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryAdapterViewHolder> {

    List<Category> categoriesList;
    Context context;
    private String shoppingMethod;
    final String TAG = "CategoryAdapter";

    public CategoryAdapter(Context ctx, List<Category> cList, String shoppingMethod) {
        this.context = ctx;
        categoriesList = cList;
        this.shoppingMethod = shoppingMethod;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_card_item, parent, false);
        return new CategoryAdapter.CategoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryAdapterViewHolder holder, int position) {

        Category category = categoriesList.get(position);

        final String category_name = category.getCategory_name();
        holder.tv_category_name.setText(category_name);

        final String img_name = category.getImg_name();

        final String url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/images/product_category_images/"+img_name;

        Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.ic_city_pin)
                .into(holder.im_category_image);

    }

    @Override
    public int getItemCount() { return categoriesList.size(); }

    class CategoryAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category_name;
        ImageView im_category_image;

        public CategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_category_name = itemView.findViewById(R.id.cc_category_name);
            im_category_image = itemView.findViewById(R.id.cci_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Category category = categoriesList.get(getAdapterPosition());
                    final String category_name = category.getCategory_name();

                    Intent in = new Intent(v.getContext(), ProductsList.class);
                    in.putExtra("coming_from", "ProductsCategory");
                    in.putExtra("category_name", category_name);
                    in.putExtra("shoppingMethod", shoppingMethod);
                    v.getContext().startActivity(in);

                }
            });

        }
    }
}
