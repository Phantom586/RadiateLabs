package com.example.noq_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";
    public static final String DATABASE_NAME = "Temp_Basket.db";
    public static final String TABLE_NAME = "Products_Table";
    public static final String col_1 = "Store_ID";
    public static final String col_2 = "Barcode";
    public static final String col_3 = "Number_of_Items";
    public static final String col_4 = "Product_Name";
    public static final String col_5 = "MRP";
    public static final String col_6 = "Total_Amount";
    public static final String col_7 = "Retailers_Price";
    public static final String col_8 = "Our_Price";
    public static final String col_9 = "Total_Discount";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String query = "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, Store_ID TEXT, Barcode TEXT, Number_of_Items INTEGER," +
                " Product_Name TEXT, MRP TEXT, Total_Amount INTEGER, Retailers_Price TEXT, Our_Price TEXT, Total_Discount TEXT)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String data, String sid, int p_qty){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int tot_amt = 0;
        contentValues.put(col_1, sid);

        try{

            JSONArray jsonArray = new JSONArray(data);
            JSONObject jobj = jsonArray.getJSONObject(1);
            contentValues.put(col_2, jobj.getString("Barcode"));
            contentValues.put(col_3, p_qty);
            contentValues.put(col_4, jobj.getString("Product_Name"));
            tot_amt = p_qty*Integer.parseInt(jobj.getString("MRP"));
            contentValues.put(col_5, jobj.getString("MRP"));
            contentValues.put(col_6, tot_amt);
            contentValues.put(col_7, jobj.getString("Retailers_Price"));
            contentValues.put(col_8, jobj.getString("Our_Price"));
            contentValues.put(col_9, jobj.getString("Total_Discount"));

        }catch(Exception e){
            e.printStackTrace();
        }

        long res = db.insert(TABLE_NAME, null, contentValues);
        final String TAG = "DBHelper";
//        Log.d(TAG, "result of the Insert Query : "+res);
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }

    public Cursor retrieveData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        return res;
    }

    public void DeleteData_by_id(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE id = "+id);
    }

    public Boolean product_exists(String b_code){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE Barcode="+b_code, null);
//        Log.d(TAG, "Product_Exists : "+res.getCount());
        return res.getCount() > 0;
    }

    public Boolean update_product(String b_code){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL("UPDATE "+TABLE_NAME+" SET Number_of_Items = Number_of_Items + 1 WHERE Barcode = "+b_code);
            db.execSQL("UPDATE "+TABLE_NAME+" SET Total_Amount = Number_of_Items * MRP WHERE Barcode = "+b_code);
            return true;
        } catch(SQLException e) {
            return false;
        }
    }

}
