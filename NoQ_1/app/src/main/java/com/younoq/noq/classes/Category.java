package com.younoq.noq.classes;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class Category {

    String store_id;
    String category_name;
    int times_purchased;

    public Category(String store_id, String category_name, int times_purchased) {

        this.store_id = store_id;
        this.category_name = category_name;
        this.times_purchased = times_purchased;

    }

    public String getStore_id() { return store_id; }

    public String getCategory_name() { return category_name; }

    public int getTimes_purchased() { return times_purchased; }
}
