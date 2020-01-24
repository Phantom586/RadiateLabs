package com.younoq.noq;

public class Store {

    private String store_id;
    private String store_name;
    private String store_address;
    private String store_city;
    private String pincode;
    private String store_state;
    private String store_country;

    public Store(String s_id, String s_name, String s_addr, String s_city, String pcode, String s_state, String s_ctry){

        this.store_id = s_id;
        this.store_name = s_name;
        this.store_address = s_addr;
        this.store_city = s_city;
        this.pincode = pcode;
        this.store_state = s_state;
        this.store_country = s_ctry;

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

    public void setStore_address(String store_address) {
        this.store_address = store_address;
    }

    public String getStore_city() {
        return store_city;
    }

    public void setStore_city(String store_city) {
        this.store_city = store_city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getStore_state() {
        return store_state;
    }

    public void setStore_state(String store_state) {
        this.store_state = store_state;
    }

    public String getStore_country() {
        return store_country;
    }

    public void setStore_country(String store_country) {
        this.store_country = store_country;
    }
}
