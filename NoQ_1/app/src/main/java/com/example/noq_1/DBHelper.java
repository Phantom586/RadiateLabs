package com.example.noq_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Temp_Basket.db";
    public static final String TABLE_NAME = "Products_Table";
    public static final String col_0 = "Store_ID";
    public static final String col_1 = "Barcode";
    public static final String col_2 = "Number_of_Items";
    public static final String col_3 = "Product_Name";
    public static final String col_4 = "MRP";
    public static final String col_5 = "Retailers_Price";
    public static final String col_6 = "Our_Price";
    public static final String col_7 = "Total_Discount";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String query = "CREATE TABLE " + TABLE_NAME + " (Store_ID TEXT, Barcode TEXT, Number_of_Items TEXT," +
                " Product_Name TEXT, MRP TEXT, Retailers_Price TEXT, Our_Price TEXT, Total_Discount TEXT)";
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
        contentValues.put(col_0, sid);

        try{

            JSONArray jsonArray = new JSONArray(data);
            JSONObject jobj = jsonArray.getJSONObject(1);
            contentValues.put(col_1, jobj.getString("Barcode"));
            contentValues.put(col_2, String.valueOf(p_qty));
            contentValues.put(col_3, jobj.getString("Product_Name"));
            tot_amt = p_qty*Integer.parseInt(jobj.getString("MRP"));
            contentValues.put(col_4, jobj.getString("MRP"));
            contentValues.put(col_5, jobj.getString("Retailers_Price"));
            contentValues.put(col_6, jobj.getString("Our_Price"));
            contentValues.put(col_7, jobj.getString("Total_Discount"));

        }catch(Exception e){
            e.printStackTrace();
        }

        long res = db.insert(TABLE_NAME, null, contentValues);
        final String TAG = "DBHelper";
        Log.d(TAG, "result of the Insert Query : "+res);
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
}
