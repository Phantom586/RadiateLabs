package com.younoq.noq.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class SaveInfoLocally {

    Context context;

    public SaveInfoLocally(Context context) {
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

    public void setRetailer_Phone_No(String phone_no){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("Retailer_Phone_No", phone_no).apply();

    }

    public String getRetailer_Phone_No(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("Retailer_Phone_No", "");
        return phone;

    }

    public void setIs_InStore(boolean in_store){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_in_store", in_store).apply();

    }

    public boolean getIs_InStore(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean res = sharedPreferences.getBoolean("is_in_store", false);
        return res;

    }

    public void setIs_Takeaway(boolean in_store){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_takeaway", in_store).apply();

    }

    public boolean getIs_Takeaway(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean res = sharedPreferences.getBoolean("is_takeaway", false);
        return res;

    }

    public void setIs_Home_Delivery(boolean in_store){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_home_delivery", in_store).apply();

    }

    public boolean getIs_Home_Delivery(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean res = sharedPreferences.getBoolean("is_home_delivery", false);
        return res;

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

    public void setHasFinishedIntro() {

        SharedPreferences pref = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putBoolean("hasFinishedIntro", true).apply();

    }

    public boolean hasFinishedIntro() {

        SharedPreferences pref = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean res = pref.getBoolean("hasFinishedIntro", false);
        return res;

    }

    public void setShoppingMethod(String sMethod) {

        SharedPreferences pref = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString("shoppingMethod", sMethod).apply();

    }

    public String getShoppingMethod() {

        SharedPreferences pref = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String res = pref.getString("shoppingMethod", "");
        return res;

    }

    public void setStoreCity(String city) {

        SharedPreferences pref = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString("storeCity", city).apply();

    }

    public String getStoreCity() {

        SharedPreferences pref = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String res = pref.getString("storeCity", "");
        return res;

    }

    public void setCategoryStores(String stores) {

        SharedPreferences pref = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString("categoryStores", stores).apply();

    }

    public String getCategoryStores() {

        SharedPreferences pref = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String res = pref.getString("categoryStores", "");
        return res;

    }

}
