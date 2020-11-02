package com.younoq.noqfuelstation.classes;

import org.json.JSONArray;

public class Txn {

    private String receipt_no, payment_mode, referral_used, timestamp, tot_retailer_amt, final_amt,
            store_name, store_addr, store_city, store_state, order_type;

    public Txn(String receipt_no, String payment_mode, String referral_used, String timestamp,
               String retailer_amt, String final_amt, String store_addr, String store_name,
               String store_city, String store_state, String order_type){

        this.receipt_no = receipt_no;
        this.payment_mode = payment_mode;
        this.referral_used = referral_used;
        this.timestamp = timestamp;
        this.tot_retailer_amt = retailer_amt;
        this.final_amt = final_amt;
        this.store_addr = store_addr;
        this.store_name = store_name;
        this.store_city = store_city;
        this.store_state = store_state;
        this.order_type = order_type;

    }

    public String getReceipt_no() { return receipt_no; }

    public String getPayment_mode() { return payment_mode; }

    public String getReferral_used() { return referral_used; }

    public String getTimestamp() { return timestamp; }

    public String getTot_retailer_amt() { return tot_retailer_amt; }

    public String getFinal_amt() { return final_amt; }

    public String getStore_name() { return store_name; }

    public void setStore_name(String store_name) { this.store_name = store_name; }

    public String getStore_addr() { return store_addr; }

    public String getStore_city() { return store_city; }

    public String getStore_state() { return store_state; }

    public String getOrder_type() { return order_type; }

}
