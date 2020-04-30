package com.younoq.noq;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListAdapterViewHolder> {

    List<Product> productList;
    Context context;
    private String TAG = "ProductListAdapter";

    ProductListAdapter(Context ctx, List<Product> list) {

        this.context = ctx;
        productList = list;

    }

    @NonNull
    @Override
    public ProductListAdapter.ProductListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_list_card, null);
        return new ProductListAdapter.ProductListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ProductListAdapterViewHolder holder, int position) {

        Product product = productList.get(position);

        final String product_name = product.getProduct_name();
        holder.tv_product_name.setText(product_name);

        final String product_price = "₹" + product.getRetailers_price();
        holder.tv_product_price.setText(product_price);

        final String quantity = product.getQuantity();
        if (quantity.equals("0")){
            holder.tv_product_status.setBackgroundColor(Color.RED);
            holder.tv_product_status.setTextColor(Color.WHITE);
            holder.tv_product_status.setText(R.string.pla_out_of_stock);
        } else {
            holder.tv_product_status.setBackgroundColor(Color.parseColor("#52DE97"));
            holder.tv_product_status.setTextColor(Color.BLACK);
            holder.tv_product_status.setText(R.string.pla_in_stock);
        }

        String res = product.hasImage();

        final boolean has_image = res.toLowerCase().equals("true");

        if (has_image) {

            String sid = product.getStore_id();
            String url;

            String img_name = product.getBarcode();
            img_name += ".png";

            if(sid.equals("3")){
                url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/school_images/"+img_name;
                Picasso.get()
                        .load(url)
                        .resize(300, 400)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.iv_prod_img);
            } else {
                url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/images/" + img_name;
                Picasso.get()
                        .load(url)
                        .resize(300, 400)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.iv_prod_img);
            }

        } else {

            holder.iv_prod_img.setImageResource(R.drawable.not_found);

        }


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductListAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_product_name, tv_product_price, tv_product_status;
        ImageView iv_add_to_basket, iv_prod_img;
        public int p_qty = 1;

        public ProductListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_product_name = itemView.findViewById(R.id.plc_p_name);
            tv_product_price = itemView.findViewById(R.id.plc_p_price);
            tv_product_status = itemView.findViewById(R.id.plc_status);
            iv_add_to_basket = itemView.findViewById(R.id.plc_add_to_basket);
            iv_prod_img = itemView.findViewById(R.id.plc_product_img);

            iv_add_to_basket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Product product = productList.get(getAdapterPosition());
                    final String quantity = product.getQuantity();

                    if(!quantity.equals("0")) {

                        List<String> prod = new ArrayList<>();
                        final String b_code = product.getBarcode();
                        final String p_name = product.getProduct_name();
                        final String first_msg = p_name + " Added - (" + p_qty + ")";

                        DBHelper dbHelper = new DBHelper(v.getContext());
                        SaveInfoLocally saveInfoLocally = new SaveInfoLocally(v.getContext());

                        prod.add(product.getStore_id());
                        prod.add(product.getBarcode());
                        prod.add(p_name);
                        prod.add(product.getMrp());
                        prod.add(product.getRetailers_price());
                        prod.add(product.getOur_price());
                        prod.add("0");
                        prod.add(product.hasImage());

                        final String sid = saveInfoLocally.get_store_id();
                        Log.d(TAG, "Store Id : "+sid);
                        boolean product_exists = false;

                        if(!b_code.equals(" ")){
                            product_exists = dbHelper.product_exists(b_code, sid);
//                        Log.d(TAG, "Product Exists : "+product_exists);
                        } else {
                            Toast.makeText(v.getContext(), "Some Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
                        }
                        if(product_exists){

                            boolean isUpdated = dbHelper.update_product(b_code, sid);
                            Log.d(TAG, "isUpdated : "+isUpdated);
                            if(isUpdated){
                                p_qty += 1;
                                final String msg = p_name + " Added - (" + p_qty + ")";
                                Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(v.getContext(), "Error!! Kindly Try Again..", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            boolean isInserted = dbHelper.insertProductData(prod, p_qty);
                            Log.d(TAG, "isInserted : "+isInserted);
                            if (isInserted){
                                Toast.makeText(v.getContext(), first_msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(v.getContext(), "Some Problem Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
                            }

                        }

                    } else {

                        Toast.makeText(v.getContext(), "This Item is Currently Out of Stock", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }
    }

}
