package com.younoq.noq;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
    private String shoppingMethod;
    private int p_qty = 0;

    ProductListAdapter(Context ctx, List<Product> list, String sMethod) {

        this.context = ctx;
        productList = list;
        shoppingMethod = sMethod;

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

//        if(p_qty == 0)
//            holder.tv_prod_qty.setVisibility(View.INVISIBLE);
//        else
//            holder.tv_prod_qty.setVisibility(View.VISIBLE);

        final String product_name = product.getProduct_name();
        holder.tv_product_name.setText(product_name);

        final String product_price = "₹" + product.getRetailers_price();
        holder.tv_product_price.setText(product_price);

//        if(p_qty > 0)
        holder.tv_prod_qty.setText(String.valueOf(p_qty));

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

        holder.iv_add_to_basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Product product = productList.get(getAdapterPosition());
                final String quantity = product.getQuantity();

                if(!quantity.equals("0")) {

                    List<String> prod = new ArrayList<>();
                    final String b_code = product.getBarcode();
                    final String p_name = product.getProduct_name();
                    final String msg = p_name + " Added";

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
                            holder.tv_prod_qty.setText(String.valueOf(p_qty));
                            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Error!! Kindly Try Again..", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        boolean isInserted = dbHelper.insertProductData(prod, p_qty+1);
                        Log.d(TAG, "isInserted : "+isInserted);
                        if (isInserted){
                            p_qty += 1;
                            holder.tv_prod_qty.setText(String.valueOf(p_qty));
                            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductListAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_product_name, tv_product_price, tv_product_status, tv_prod_qty;
        ImageView iv_add_to_basket, iv_prod_img;

        public ProductListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_product_name = itemView.findViewById(R.id.plc_p_name);
            tv_product_price = itemView.findViewById(R.id.plc_p_price);
//            tv_product_status = itemView.findViewById(R.id.plc_status);
            tv_prod_qty = itemView.findViewById(R.id.plc_prod_qty);
            iv_add_to_basket = itemView.findViewById(R.id.plc_add_to_basket);
            iv_prod_img = itemView.findViewById(R.id.plc_product_img);

            iv_prod_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int position = getAdapterPosition();
                    Bundle prodData = new Bundle();
                    ArrayList<String> prodDetails = new ArrayList<>();

                    Product product = productList.get(position);

                    if(position != RecyclerView.NO_POSITION) {

                        prodDetails.add(product.getStore_id());
                        prodDetails.add(product.getBarcode());
                        prodDetails.add(product.getProduct_name());
                        prodDetails.add(product.getMrp());
                        prodDetails.add(product.getRetailers_price());
                        prodDetails.add(product.getOur_price());
                        prodDetails.add(product.getTotal_discount());
                        prodDetails.add(product.hasImage());
                        prodDetails.add(product.getCategory());
                        prodDetails.add(String.valueOf(p_qty));

                        prodData.putStringArrayList("productDetails", prodDetails);

                    }

                    Intent in = new Intent(v.getContext(), ProductDetails.class);
                    in.putExtra("comingFrom", "ProductList");
                    in.putExtra("shoppingMethod", shoppingMethod);
                    // Making Sure there are no Issues regarding this in Future.
                    if(prodData.size() > 0)
                        in.putExtras(prodData);
                    v.getContext().startActivity(in);

                }
            });



        }
    }

}
