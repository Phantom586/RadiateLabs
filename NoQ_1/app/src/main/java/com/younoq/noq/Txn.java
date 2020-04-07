package com.younoq.noq;

import org.json.JSONArray;

public class Txn {

    private String receipt_no, payment_mode, timestamp, total_items, final_amt, store_name, store_addr, store_city, store_state;
    private JSONArray products;

    Txn(String receipt_no, String payment_mode, String timestamp, String total_items, String final_amt, String store_addr, String store_name, String store_city, String store_state, JSONArray products){

        this.receipt_no = receipt_no;
        this.payment_mode = payment_mode;
        this.timestamp = timestamp;
        this.total_items = total_items;
        this.final_amt = final_amt;
        this.store_addr = store_addr;
        this.store_name = store_name;
        this.store_city = store_city;
        this.store_state = store_state;
        this.products = products;

    }

    public String getReceipt_no() { return receipt_no; }

    public String getPayment_mode() { return payment_mode; }

    public void setPayment_mode(String payment_mode) { this.payment_mode = payment_mode; }

    public void setReceipt_no(String receipt_no) { this.receipt_no = receipt_no; }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getTotal_items() { return total_items; }

    public void setTotal_items(String total_items) { this.total_items = total_items; }

    public String getFinal_amt() { return final_amt; }

    public void setFinal_amt(String final_amt) { this.final_amt = final_amt; }

    public String getStore_name() { return store_name; }

    public void setStore_name(String store_name) { this.store_name = store_name; }

    public String getStore_addr() { return store_addr; }

    public void setStore_addr(String store_addr) { this.store_addr = store_addr; }

    public String getStore_city() { return store_city; }

    public void setStore_city(String store_city) { this.store_city = store_city; }

    public String getStore_state() { return store_state; }

    public void setStore_state(String store_state) { this.store_state = store_state; }

    public JSONArray getProducts() { return products; }

    public void setProducts(JSONArray products) { this.products = products; }
}
