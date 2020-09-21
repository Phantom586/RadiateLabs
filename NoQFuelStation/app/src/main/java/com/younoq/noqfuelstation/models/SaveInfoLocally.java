package com.younoq.noqfuelstation.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class SaveInfoLocally {

    private Context context;

    public SaveInfoLocally(Context ctx) { this.context = ctx; }

    public void saveLoginDetails(String Phone) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Phone", Phone);
        editor.apply();

    }

    public boolean isFirstLogin() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        boolean res = sharedPreferences.getBoolean("firstLogin", true);
        return res;

    }

    public void setFirstLoginStatus(boolean status) {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("firstLogin", status).apply();

    }

    public void setHasFinishedIntro() {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("hasFinishedIntro", true).apply();

    }

    public boolean hasFinishedIntro() {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        boolean res = pref.getBoolean("hasFinishedIntro", false);
        return res;

    }

    public String getPhone() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        Boolean result = sharedPreferences.contains("Phone");

        if(result){
            return sharedPreferences.getString("Phone", "");
        } else {
            return "";
        }

    }

    public void setLogFilePath(String fileName) {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("logFileName", fileName).apply();

    }

    public String getLogFilePath() {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        String res = pref.getString("logFileName", "");
        return res;

    }

    public void setPrevPhone(String phone){

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Old_Phone", phone);
        editor.apply();

    }

    public String getPrevPhone(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        Boolean result = sharedPreferences.contains("Old_Phone");

        if(result){
            return sharedPreferences.getString("Old_Phone", "");
        } else {
            return "";
        }

    }

    public void removeNumber(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("Phone");
        editor.apply();

    }

    public void saveFuelStationDetails(String Phone) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Phone", Phone);
        editor.apply();

    }

    public void setStoreCity(String city) {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("storeCity", city).apply();

    }

    public String getStoreCity() {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        String res = pref.getString("storeCity", "");
        return res;

    }

    public void setStoreCityArea(String city) {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("storeCityArea", city).apply();

    }

    public String getStoreCityArea() {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        String res = pref.getString("storeCityArea", "");
        return res;

    }

    public void clear_all(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    public void setUserName(String uname){

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User_Name", uname).apply();

    }

    public String getUserName(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        String uname = sharedPreferences.getString("User_Name", "");
        return uname;

    }

    public void setEmail(String email) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User_Email", email).apply();

    }

    public String getEmail() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("User_Email", "");
        return email;

    }

    public void setReferralNo(String no) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Referral_No", no).apply();

    }

    public String getReferralNo() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        String r_no = sharedPreferences.getString("Referral_No", "");
        return r_no;

    }
    public void setUserAddress(String addr) {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_address", addr).apply();

    }

    public String getUserAddress() {

        SharedPreferences pref = context.getSharedPreferences("FuelStationDetails", Context.MODE_PRIVATE);
        String res = pref.getString("user_address", "");
        return res;

    }

    public void setReferralBalance(String ref_bal) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Referral_Balance", ref_bal).apply();

    }

    public String getReferralBalance(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        String ref_bal = sharedPreferences.getString("Referral_Balance", "");
        return ref_bal;

    }



}
