package com.example.noq_1;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveInfoLocally {

    Context context;

    SaveInfoLocally(Context context) {
        this.context = context;
    }

    public void saveLoginDetails(String Phone) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Phone", Phone);
        editor.apply();

    }

    public String getPhone() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Phone", "");

    }

    public boolean UserExists(String Phone) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isPhoneEmpty = sharedPreferences.getString("Phone", "").equals(Phone);
        return isPhoneEmpty;

    }

}
