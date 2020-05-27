package com.younoq.noq.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noq.classes.Category;
import com.younoq.noq.classes.ImageAssets;
import com.younoq.noq.views.ProductsList;
import com.younoq.noq.R;

import java.util.HashMap;
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

        final String imagename = category_name.replace(" ", "").toLowerCase();
        Log.d(TAG, imagename +" : "+holder.imAssets.get(imagename));
        holder.im_category_image.setImageResource(holder.imAssets.get(imagename));

    }

    @Override
    public int getItemCount() { return categoriesList.size(); }

    class CategoryAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category_name;
        ImageView im_category_image;
        ImageAssets imageAssets;
        HashMap<String, Integer> imAssets;

        public CategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_category_name = itemView.findViewById(R.id.cc_category_name);
            im_category_image = itemView.findViewById(R.id.cci_image);

            imAssets = new HashMap<>();
            imageAssets = new ImageAssets();
            imageAssets.setImageAssets();
            imAssets = imageAssets.getImageAssets();
            Log.d(TAG, "Image Assets : "+imAssets);

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
