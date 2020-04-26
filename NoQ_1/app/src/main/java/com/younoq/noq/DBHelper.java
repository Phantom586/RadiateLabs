package com.younoq.noq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "Temp_Basket.db";
    private static final String TABLE_PRODUCTS = "Products_Table";
//    private static final String TABLE_STORES = "Noq_Stores";
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, Store_ID TEXT, Barcode TEXT, Number_of_Items INTEGER," +
            " Product_Name TEXT, MRP TEXT, Total_Amount TEXT, Retailers_Price TEXT, Our_Price TEXT, Total_Discount TEXT, Has_Image TEXT)";
//    private static final String CREATE_TABLE_NOQ_STORES = "CREATE TABLE " + TABLE_STORES + " (Store_ID INTEGER PRIMARY KEY, Store_Name TEXT, Store_Address TEXT, Store_City TEXT, " +
//            " Pincode INTEGER, Store_State TEXT, Store_Country TEXT)";
    // Product Table's Columns
    private static final String prod_col_1 = "Store_ID";
    private static final String prod_col_2 = "Barcode";
    private static final String prod_col_3 = "Number_of_Items";
    private static final String prod_col_4 = "Product_Name";
    private static final String prod_col_5 = "MRP";
    private static final String prod_col_6 = "Total_Amount";
    private static final String prod_col_7 = "Retailers_Price";
    private static final String prod_col_8 = "Our_Price";
    private static final String prod_col_9 = "Total_Discount";
    private static final String prod_col_10 = "Has_Image";
    // NoQ_Store Table's  Columns
//    private static final String store_col_1 = "Store_ID";
//    private static final String store_col_2 = "Store_Name";
//    private static final String store_col_3 = "Store_Address";
//    private static final String store_col_5 = "Store_City";
//    private static final String store_col_6 = "Pincode";
//    private static final String store_col_7 = "Store_State";
//    private static final String store_col_8 = "Store_Country";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
//        db.execSQL(CREATE_TABLE_NOQ_STORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PRODUCTS);
//        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_STORES);
        onCreate(db);
    }

    public boolean insertData(String data, String sid, int p_qty){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Double tot_amt = 0.0;
        contentValues.put(prod_col_1, sid);

        try{

            JSONArray jsonArray = new JSONArray(data);
            JSONObject jobj = jsonArray.getJSONObject(1);
            contentValues.put(prod_col_2, jobj.getString("Barcode"));
            contentValues.put(prod_col_3, p_qty);
            contentValues.put(prod_col_4, jobj.getString("Product_Name"));
            tot_amt = p_qty*Double.parseDouble(jobj.getString("Our_Price"));
            contentValues.put(prod_col_5, jobj.getString("MRP"));
            contentValues.put(prod_col_6, String.valueOf(tot_amt));
            contentValues.put(prod_col_7, jobj.getString("Retailers_Price"));
            contentValues.put(prod_col_8, jobj.getString("Our_Price"));
            contentValues.put(prod_col_9, jobj.getString("Total_Discount"));
            contentValues.put(prod_col_10, jobj.getString("has_image"));

        }catch(Exception e){
            e.printStackTrace();
        }

        long res = db.insert(TABLE_PRODUCTS, null, contentValues);
        final String TAG = "DBHelper";
//        Log.d(TAG, "result of the Insert Query : "+res);
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean insertDataInStoreTable(String data){

        return true;

    }

    public Cursor retrieveData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS, null);
        return res;
    }

    public void Delete_all_rows(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_PRODUCTS);
    }

    public void DeleteData_by_id(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_PRODUCTS+" WHERE id = "+id);
    }

    public Boolean product_exists(String b_code, String sid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE Barcode="+b_code+" AND Store_ID="+sid, null);
//        Log.d(TAG, "Product_Exists : "+res.getCount());
        return res.getCount() > 0;
    }

    public Boolean update_product(String b_code, String sid){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL("UPDATE "+TABLE_PRODUCTS+" SET Number_of_Items = Number_of_Items + 1 WHERE Barcode = "+b_code+" AND Store_ID="+sid);
            db.execSQL("UPDATE "+TABLE_PRODUCTS+" SET Total_Amount = Number_of_Items * Our_Price WHERE Barcode = "+b_code+" AND Store_ID="+sid);
            return true;
        } catch(SQLException e) {
            return false;
        }
    }

}
