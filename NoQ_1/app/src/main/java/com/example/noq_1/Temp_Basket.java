package com.example.noq_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Temp_Basket {

    public static final String PREFS_NAME = "Temp_Basket";
    public static final String PREFS_PRODUCT_NAME_KEY = "Product_Key";
    public static final String PREFS_PRODUCT_BARCODE = "Barcode";
    public static final String PREFS_PRODUCT_MRP_KEY = "MRP";
    public static final String PREFS_PRODUCT_RETAILER_PRICE_KEY = "Retailer_Price";
    public static final String PREFS_PRODUCT_OUR_PRICE_KEY = "Our_Price";
    public static final String PREFS_PRODUCT_TOTAL_DISCOUNT_KEY = "Total_Discount";

    Context context;

    Temp_Basket(Context context) {
        this.context = context;
    }

    public void add_to_temp_basket(String[] data){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();

        editor.putString(PREFS_PRODUCT_BARCODE, data[0]);
        editor.putString(PREFS_PRODUCT_NAME_KEY, data[1]);
        editor.putString(PREFS_PRODUCT_MRP_KEY, data[2]);
        editor.putString(PREFS_PRODUCT_RETAILER_PRICE_KEY, data[3]);
        editor.putString(PREFS_PRODUCT_OUR_PRICE_KEY, data[4]);
        editor.putString(PREFS_PRODUCT_TOTAL_DISCOUNT_KEY, data[5]);

        editor.apply();
    }

    public String[] get_from_temp_basket(){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String[] data = new String[6];
        data[0] = sharedPreferences.getString(PREFS_PRODUCT_BARCODE, "");
        data[0] = sharedPreferences.getString(PREFS_PRODUCT_NAME_KEY, "");
        data[0] = sharedPreferences.getString(PREFS_PRODUCT_MRP_KEY, "");
        data[0] = sharedPreferences.getString(PREFS_PRODUCT_RETAILER_PRICE_KEY, "");
        data[0] = sharedPreferences.getString(PREFS_PRODUCT_OUR_PRICE_KEY, "");
        data[0] = sharedPreferences.getString(PREFS_PRODUCT_TOTAL_DISCOUNT_KEY, "");

        return data;

    }

}
