package com.younoq.noq;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BottomSheetCategoryAdapter extends RecyclerView.Adapter<BottomSheetCategoryAdapter.BottomSheetCategoryAdapterViewHolder> {

    List<Category> categoriesList;
    Context context;
    private String shoppingMethod;

    BottomSheetCategoryAdapter(Context ctx, List<Category> cList, String shoppingMethod) {
        this.context = ctx;
        categoriesList = cList;
        this.shoppingMethod = shoppingMethod;
    }

    @NonNull
    @Override
    public BottomSheetCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_card_item, parent, false);
        return new BottomSheetCategoryAdapter.BottomSheetCategoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetCategoryAdapterViewHolder holder, int position) {

        Category category = categoriesList.get(position);

        final String category_name = category.getCategory_name();
        Log.d("BottomSheetCategory", "Category Name : "+category_name);
        holder.tv_category_name.setText(category_name);

    }

    @Override
    public int getItemCount() { return categoriesList.size(); }

    class BottomSheetCategoryAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category_name;

        public BottomSheetCategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_category_name = itemView.findViewById(R.id.cc_category_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Category category = categoriesList.get(getAdapterPosition());
                    final String category_name = category.getCategory_name();

                    Intent in = new Intent(v.getContext(), ProductsList.class);
                    in.putExtra("coming_from", "ProductsList");
                    in.putExtra("category_name", category_name);
                    in.putExtra("shoppingMethod", shoppingMethod);
                    v.getContext().startActivity(in);

                }
            });

        }
    }
}
