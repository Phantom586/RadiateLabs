package com.example.noq_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context ctx;
    private List<Product> ProductList;
    public static final String TAG = "ProductAdapter";
    public static int qyt = 0;
    public static int tot = 0;
    private onItemClickListener mListener;
    SharedPreferences sharedPreferences;

    public interface  onItemClickListener{
//        void onItemClick(int position);
        void onDeleteClick(int position, int id, int price);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

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

        String url;

//        if(sid.equals("3")){
//            url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/school_images/"+img_name;
//        } else {
//            url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/images/" + img_name;
//        }

        url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/images/"+img_name;
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
//        holder.tv3.setText(product.getCurrent_qty());
        qyt = Integer.parseInt(product.getCurrent_qty());
        Log.d(TAG, " Product : "+product.getProduct_name()+" current_qty : "+qyt);
//        Log.d(TAG, "Current Quantity : "+product.getCurrent_qty());
        tot = Integer.parseInt(product.getCurrent_qty())*Integer.parseInt(product.getMrp());
        final String tot1 = "₹" + tot;
        holder.tv5.setText(tot1);
        holder.tv6.setText(product.getCurrent_qty());

//        holder.im1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                qyt -= 1;
//                final String qty = Integer.toString(qyt);
//                holder.tv3.setText(qty);
//            }
//        });

//        holder.im2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                qyt += 1;
//                final String qty = Integer.toString(qyt);
//                holder.tv3.setText(qty);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView im, im1, im2;
        Button del;
        TextView tv1, tv2, tv3, tv5, tv6;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            im = itemView.findViewById(R.id.c_im);
//            im1 = itemView.findViewById(R.id.c_im1);
//            im2 = itemView.findViewById(R.id.c_im2);
            tv1 = itemView.findViewById(R.id.c_tv1);
            tv2 = itemView.findViewById(R.id.c_tv2);
//            tv3 = itemView.findViewById(R.id.c_tv3);
            tv5 = itemView.findViewById(R.id.c_tv5);
            tv6 = itemView.findViewById(R.id.c_tv6);
            del = itemView.findViewById(R.id.c_delete);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(mListener != null){
//                        int position = getAdapterPosition();
//                        if(position != RecyclerView.NO_POSITION){
//                            mListener.onItemClick(position);
//                        }
//                    }
//                }
//            });

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            Product product = ProductList.get(position);
                            mListener.onDeleteClick(position, product.getId(), tot);
                        }
                    }
                }
            });

        }
    }

}
