package com.younoq.noq;

public class Product {

    private int id;
    private String store_id;
    private String barcode;
    private String product_name;
    private String mrp;
    private String tot_amt;
    private String retailers_price;
    private String our_price;
    private String total_discount;
    private String current_qty;
    private boolean isExpanded;
    private String hasImage;

    public Product(int id,String store_id, String barcode, String product_name, String mrp, String tot_amt, String retailers_price, String our_price, String total_discount, String current_qty, String hasImage) {
        this.id = id;
        this.store_id = store_id;
        this.barcode = barcode;
        this.product_name = product_name;
        this.mrp = mrp;
        this.tot_amt = tot_amt;
        this.retailers_price = retailers_price;
        this.our_price = our_price;
        this.total_discount = total_discount;
        this.current_qty = current_qty;
        this.isExpanded = false;
        this.hasImage = hasImage;
    }

    public int getId(){ return id; }

    public String getBarcode() {
        return barcode;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getMrp() {
        return mrp;
    }

    public String getStore_id() { return store_id; }

    public String getRetailers_price() {
        return retailers_price;
    }

    public String getOur_price() {
        return our_price;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public String getCurrent_qty() {
        return current_qty;
    }

    public String getTot_amt() { return tot_amt; }

    public boolean isExpanded() { return isExpanded; }

    public void setExpanded(boolean expanded) { isExpanded = expanded; }

    public String hasImage() { return hasImage; }
}
