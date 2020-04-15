package com.younoq.noq;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveInfoLocally {

    Context context;

    SaveInfoLocally(Context context) {
        this.context = context;
    }

    public void saveLoginDetails(String Phone) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Phone", Phone);
        editor.apply();

    }

    public void setPrevPhone(String phone){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Old_Phone", phone);
        editor.apply();

    }

    public String getPrevPhone(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Boolean result = sharedPreferences.contains("Old_Phone");

        if(result){
            return sharedPreferences.getString("Old_Phone", "");
        } else {
            return "";
        }

    }

    public void setSessionID(String sessionID){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Session_ID", sessionID).apply();

    }

    public String getSessionID(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String sid = sharedPreferences.getString("Session_ID", "");
        return sid;

    }

    public void set_store_id(String sid){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Store_id", sid).apply();

    }

    public String get_store_id(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String sid = sharedPreferences.getString("Store_id", "");
        return sid;

    }

    public String getPhone() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Boolean result = sharedPreferences.contains("Phone");

        if(result){
            return sharedPreferences.getString("Phone", "");
        } else {
            return "";
        }

    }

    public void removeNumber(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();

        editor.remove("Phone");
        editor.apply();

    }

    public boolean UserExists(String Phone) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isPhoneEmpty = sharedPreferences.getString("Phone", "").equals(Phone);
        return isPhoneEmpty;

    }

    public void setStoreName(String sname){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Store_Name", sname).apply();

    }

    public String getStoreName(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String sname = sharedPreferences.getString("Store_Name", "");
        return sname;

    }

    public void setUserName(String uname){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("User_Name", uname).apply();

    }

    public String getUserName(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String uname = sharedPreferences.getString("User_Name", "");
        return uname;

    }

    public void setEmail(String email) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("User_Email", email).apply();

    }

    public String getEmail() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("User_Email", "");
        return email;

    }

    public void setReferralNo(String no) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Referral_No", no).apply();

    }

    public String getReferralNo() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String r_no = sharedPreferences.getString("Referral_No", "");
        return r_no;

    }

    public void clear_all(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    public void setStoreAddress(String addr){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Store_Address", addr).apply();

    }

    public String getStoreAddress(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String s_addr = sharedPreferences.getString("Store_Address", "");
        return s_addr;

    }

    public void setReferralBalance(String ref_bal) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Referral_Balance", ref_bal).apply();

    }

    public String getReferralBalance(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String ref_bal = sharedPreferences.getString("Referral_Balance", "");
        return ref_bal;

    }

}
