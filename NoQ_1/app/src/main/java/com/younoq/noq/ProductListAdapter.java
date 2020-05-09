package com.younoq.noq;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        final String product_name = product.getProduct_name();
        holder.tv_product_name.setText(product_name);

        final String product_price = "₹" + product.getRetailers_price();
        holder.tv_product_price.setText(product_price);

        // Retrieving the Available Quantity for the Product.
        holder.available_quantity = Integer.parseInt(product.getQuantity());

        String res = product.hasImage();

        final boolean has_image = res.toLowerCase().equals("true");
        final String b_code = product.getBarcode();

        String sid = product.getStore_id();

        if (has_image) {

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
                        .resize(170, 150)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.iv_prod_img);
            }

        } else {

            holder.iv_prod_img.setImageResource(R.drawable.not_found);

        }

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.p_qty < holder.available_quantity){
                    holder.p_qty += 1;
                    // displaying the Update msg to the USer.
                    holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
                } else {
                    Toast.makeText(v.getContext(), "You have reached the max. available qty for this product.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.p_qty > 1){
                    holder.p_qty -= 1;
                    // displaying the Update msg to the USer.
                    holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
                } else {
                    Toast.makeText(v.getContext(), "You have reached the minimum limit for this Item.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Retrieve Product's Quantity from Local Database if Present.
        final boolean prod_exists_in_db = holder.dbHelper.product_exists(b_code, sid);
        if(prod_exists_in_db){
            Log.d(TAG, product.getProduct_name()+" exists in local DB");
            String product_qty_in_db = "0";
            Cursor data = holder.dbHelper.getProductQuantity(sid, b_code);
            while(data.moveToNext()){
                product_qty_in_db = data.getString(3);
                Log.d(TAG, data.getString(4)+", Quantity : "+product_qty_in_db);
            }
            holder.tv_prod_display_qty.setText(product_qty_in_db);

        } else {
            Log.d(TAG, product.getProduct_name()+" Doesn't exists in local DB");
            holder.tv_prod_display_qty.setText("0");
        }

        // If Product's Quantity is more than one, then only show the options related to add to cart.
        final String quantity = product.getQuantity();
        if(quantity.equals("0")){
            holder.selective_linear_layout.setVisibility(View.GONE);
            holder.tv_prod_qty.setVisibility(View.GONE);
            holder.tv_prod_display_qty.setVisibility(View.GONE);
            holder.tv_product_status.setVisibility(View.VISIBLE);
        }
        else{
            holder.tv_product_status.setVisibility(View.GONE);
            holder.tv_prod_display_qty.setVisibility(View.VISIBLE);
            holder.tv_prod_qty.setVisibility(View.VISIBLE);
            holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
            holder.selective_linear_layout.setVisibility(View.VISIBLE);
        }

        holder.iv_add_to_basket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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
                    prod.add(product.getTotal_discount());
                    prod.add(product.hasImage());
                    prod.add(product.getCategory());
//                    Log.d(TAG, p_name+" Quantity Available : "+product.getQuantity());
                    prod.add(product.getQuantity());

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

                        boolean isUpdated = dbHelper.update_product(b_code, sid, holder.p_qty);
                        Log.d(TAG, "isUpdated : "+isUpdated);
                        if(isUpdated){
                            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                            // Resetting the Value of Product_Quantity as the Product has been added to basket.
                            holder.p_qty = 1;
                            holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
                        } else {
                            Toast.makeText(v.getContext(), "Error!! Kindly Try Again..", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        boolean isInserted = dbHelper.insertProductData(prod, holder.p_qty);
                        Log.d(TAG, "isInserted : "+isInserted);
                        if (isInserted){
                            Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
                            // Resetting the Value of Product_Quantity as the Product has been added to basket.
                            holder.p_qty = 1;
                            holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
                        }else{
                            Toast.makeText(v.getContext(), "Some Problem Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
                        }

                    }

                } else {

                    Toast.makeText(v.getContext(), "This Item is Currently Out of Stock", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductListAdapterViewHolder extends RecyclerView.ViewHolder {

        int p_qty = 1, available_quantity = 0;
        TextView tv_product_name, tv_product_price, tv_product_status, tv_prod_qty, tv_prod_display_qty;
        ImageView iv_add_to_basket, iv_prod_img, iv_add, iv_delete;
        LinearLayout selective_linear_layout;
        DBHelper dbHelper;

        public ProductListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_product_name = itemView.findViewById(R.id.plc_p_name);
            tv_product_price = itemView.findViewById(R.id.plc_p_price);
            tv_product_status = itemView.findViewById(R.id.plc_prod_status);
            tv_prod_qty = itemView.findViewById(R.id.plc_prod_qty);
            iv_add_to_basket = itemView.findViewById(R.id.plc_add_to_basket);
            iv_prod_img = itemView.findViewById(R.id.plc_product_img);
            selective_linear_layout = itemView.findViewById(R.id.plc_selective_linear_layout);
            iv_add = itemView.findViewById(R.id.plc_add);
            iv_delete = itemView.findViewById(R.id.plc_delete);
            tv_prod_display_qty = itemView.findViewById(R.id.plc_prod_display_qty);

            dbHelper = new DBHelper(context);

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
//                        prodDetails.add(String.valueOf(p_qty));
                        prodDetails.add(product.getQuantity());

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

//            iv_add_to_basket.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Product product = productList.get(getAdapterPosition());
//                    final String quantity = product.getQuantity();
//
//                    if(!quantity.equals("0")) {
//
//                        List<String> prod = new ArrayList<>();
//                        final String b_code = product.getBarcode();
//                        final String p_name = product.getProduct_name();
//                        final String msg = p_name + " Added";
//
//                        DBHelper dbHelper = new DBHelper(v.getContext());
//                        SaveInfoLocally saveInfoLocally = new SaveInfoLocally(v.getContext());
//
//                        prod.add(product.getStore_id());
//                        prod.add(product.getBarcode());
//                        prod.add(p_name);
//                        prod.add(product.getMrp());
//                        prod.add(product.getRetailers_price());
//                        prod.add(product.getOur_price());
//                        prod.add("0");
//                        prod.add(product.hasImage());
//
//                        final String sid = saveInfoLocally.get_store_id();
//                        Log.d(TAG, "Store Id : "+sid);
//                        boolean product_exists = false;
//
//                        if(!b_code.equals(" ")){
//                            product_exists = dbHelper.product_exists(b_code, sid);
////                        Log.d(TAG, "Product Exists : "+product_exists);
//                        } else {
//                            Toast.makeText(v.getContext(), "Some Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
//                        }
//                        if(product_exists){
//
//                            boolean isUpdated = dbHelper.update_product(b_code, sid);
//                            Log.d(TAG, "isUpdated : "+isUpdated);
//                            if(isUpdated){
//                                p_qty += 1;
//                                // displaying the Update msg to the USer.
//                                final String update_msg = msg + "(" + p_qty + ")";
////                                holder.tv_prod_qty.setText(String.valueOf(p_qty));
//                                Toast.makeText(v.getContext(), update_msg, Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(v.getContext(), "Error!! Kindly Try Again..", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } else {
//
//                            boolean isInserted = dbHelper.insertProductData(prod, p_qty+1);
//                            Log.d(TAG, "isInserted : "+isInserted);
//                            if (isInserted){
//                                p_qty += 1;
////                                holder.tv_prod_qty.setText(String.valueOf(p_qty));
//                                Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
//                            }else{
//                                Toast.makeText(v.getContext(), "Some Problem Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//
//                    } else {
//
//                        Toast.makeText(v.getContext(), "This Item is Currently Out of Stock", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                }
//            });

        }
    }

}
