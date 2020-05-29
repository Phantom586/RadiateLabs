package com.younoq.noq.classes;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class Store {

    private String store_id;
    private String store_name;
    private String store_address;
    private String store_city;
    private String pincode;
    private String store_state;
    private String store_country, retailer_phone_no;
    private boolean in_store, takeaway, home_delivery;

    public Store(String s_id, String s_name, String s_addr, String s_city, String pcode, String s_state, String s_ctry, String phone_no, boolean in_store, boolean takeaway, boolean home_delivery){

        this.store_id = s_id;
        this.store_name = s_name;
        this.store_address = s_addr;
        this.store_city = s_city;
        this.pincode = pcode;
        this.store_state = s_state;
        this.store_country = s_ctry;
        this.retailer_phone_no = phone_no;
        this.in_store = in_store;
        this.takeaway = takeaway;
        this.home_delivery = home_delivery;

    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_address() {
        return store_address;
    }

    public String getStore_city() {
        return store_city;
    }

    public String getPincode() {
        return pincode;
    }

    public String getStore_state() {
        return store_state;
    }

    public String getStore_country() {
        return store_country;
    }

    public String getRetailer_phone_no() { return retailer_phone_no; }

    public boolean isIn_store() { return in_store; }

    public boolean isTakeaway() { return takeaway; }

    public boolean isHome_delivery() { return home_delivery; }
}