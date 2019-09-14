package com.example.noq_1;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context ctx;
    private List<Product> ProductList;
    public static final String TAG = "ProductAdapter";

    public ProductAdapter(Context ctx, List<Product> productList) {
        this.ctx = ctx;
        ProductList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.card_item, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = ProductList.get(position);

        String img_name = product.getBarcode();
        img_name += ".png";
        String url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/images/"+img_name;
//        Log.d(TAG, "Image URL : "+url);
//        Glide.with(ctx)
//                .load(url)
//                .centerCrop()
//                .placeholder(R.drawable.ic_launcher_foreground)
//                .into(holder.im);
        Picasso.get()
                .load(url)
                .resize(300, 400)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.im);
        holder.tv1.setText(product.getProduct_name());
//        Log.d(TAG, "Product Name : "+product.getProduct_name());
        final String mrp = "₹"+product.getMrp();
        holder.tv2.setText(mrp);
//        Log.d(TAG, "MRP : "+product.getMrp());
        holder.tv3.setText(product.getCurrent_qty());
//        Log.d(TAG, "Current Quantity : "+product.getCurrent_qty());
        final int tot = Integer.parseInt(product.getCurrent_qty())*Integer.parseInt(product.getMrp());
        final String tot1 = "₹" + tot;
        holder.tv5.setText(tot1);

    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView im;
        TextView tv1, tv2, tv3, tv5;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            im = itemView.findViewById(R.id.c_im);
            tv1 = itemView.findViewById(R.id.c_tv1);
            tv2 = itemView.findViewById(R.id.c_tv2);
            tv3 = itemView.findViewById(R.id.c_tv3);
            tv5 = itemView.findViewById(R.id.c_tv5);

        }
    }

}
