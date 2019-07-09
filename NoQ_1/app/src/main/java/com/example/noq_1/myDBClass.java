package com.example.noq_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class myDBClass {

    myDBHelper myhelper;
    public myDBClass(Context context) {

        myhelper = new myDBHelper(context);

    }

    public long insertData(String full_name, String email, String pno, String ref_no) {

        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDBHelper.FULL_NAME, full_name);
        contentValues.put(myDBHelper.EMAIL, email);
        contentValues.put(myDBHelper.PHONE, pno);
        contentValues.put(myDBHelper.REFERRER_NO, ref_no);
        long id = dbb.insert(myDBHelper.TABLE_NAME, null, contentValues);
        return id;

    }

    public String queryData(String phone) {

        SQLiteDatabase db = myhelper.getWritableDatabase();
//        String[] columns = {myDBHelper.UID, myDBHelper.FULL_NAME, myDBHelper.EMAIL, myDBHelper.PHONE, myDBHelper.REFERRER_NO};
        String[] selectArgs = {phone};
//        Cursor cursor = db.query(myDBHelper.TABLE_NAME, columns, null, null, null, null, null);
        StringBuilder buffer = new StringBuilder();
//        while ( cursor.moveToNext() ) {
//
//            int cid = cursor.getInt(cursor.getColumnIndex(myDBHelper.UID));
//            String f_name = cursor.getString(cursor.getColumnIndex(myDBHelper.FULL_NAME));
//            String email = cursor.getString(cursor.getColumnIndex(myDBHelper.EMAIL));
//            String p_no = cursor.getString(cursor.getColumnIndex(myDBHelper.PHONE));
//            String ref_no = cursor.getString(cursor.getColumnIndex(myDBHelper.REFERRER_NO));
//            buffer.append(cid+ " " + f_name + " " + email + " " + p_no + " " + ref_no + "\n");
//
//        }
//        cursor.close();

        final String query = "SELECT * FROM "+ myDBHelper.TABLE_NAME + " WHERE PHONE = ?";

        Cursor cursor = db.rawQuery(query, selectArgs);

        while ( cursor.moveToNext() ) {

            int cid = cursor.getInt(0);
            String f_name = cursor.getString(1);
            String email = cursor.getString(2);
            String p_no = cursor.getString(3);
            String ref_no = cursor.getString(4);
            buffer.append(cid+ " " + f_name + " " + email + " " + p_no + " " + ref_no + "\n");

        }
        cursor.close();

        return buffer.toString();

    }

    public int delete(String f_name) {

        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs = {f_name};

        int count = db.delete(myDBHelper.TABLE_NAME, myDBHelper.FULL_NAME+" = ?", whereArgs);
        return count;

    }

    public int updateDB(String[] old_data, String[] new_data) {

        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if ( old_data[0].length() > 0 ) {

            contentValues.put(myDBHelper.FULL_NAME, new_data[0]);

        }
        else if ( old_data[0].length() > 0 && old_data[1].length() > 0 ) {

            contentValues.put(myDBHelper.FULL_NAME, new_data[0]);
            contentValues.put(myDBHelper.EMAIL, new_data[1]);

        }

        String[] whereArgs = {old_data[0]};
        int count = db.update(myDBHelper.TABLE_NAME, contentValues, myDBHelper.FULL_NAME+" = ?", whereArgs);
        return count;

    }

    static class myDBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "UserDetails";
        private static final String TABLE_NAME = "UserInfo";
        private static final int DATABASE_Version = 1;
        private static final String UID = "_id";
        private static final String FULL_NAME = "FULL_NAME";
        private static final String EMAIL = "EMAIL";
        private static final String PHONE = "PHONE";
        private static final String REFERRER_NO = "REFERRER_NO";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                     FULL_NAME + " VARCHAR(255) ,"+
                     EMAIL + " VARCHAR(255), "+
                     PHONE + " INTEGER(12) ,"+
                     REFERRER_NO + " INTEGER(12));";

        private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        private Context context;

        public myDBHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;

        }

        public void onCreate(SQLiteDatabase db) {

            try{

                db.execSQL(CREATE_TABLE);

            } catch (Exception e) {

                Log.i("MyActivity", ""+e);

            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            try{

                Log.i("OnUpgrade", "Upgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);

            } catch (Exception e) {

                Log.i("OnUpgrade", ""+e);

            }

        }
    }

}
