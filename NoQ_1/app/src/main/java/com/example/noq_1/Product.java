package com.example.noq_1;

public class Product {

    private int id;
    private String barcode;
    private String product_name;
    private String mrp;
    private String retailers_price;
    private String our_price;
    private String total_discount;
    private String current_qty;

    public Product(int id, String barcode, String product_name, String mrp, String retailers_price, String our_price, String total_discount, String current_qty) {
        this.id = id;
        this.barcode = barcode;
        this.product_name = product_name;
        this.mrp = mrp;
        this.retailers_price = retailers_price;
        this.our_price = our_price;
        this.total_discount = total_discount;
        this.current_qty = current_qty;
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
}
