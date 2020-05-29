package com.younoq.noq.classes;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

import com.younoq.noq.R;

import java.util.HashMap;

public class ImageAssets {

    HashMap<String, Integer> imageAssets;
    HashMap<String, Integer> storeImageAssets;

    public ImageAssets(){
        this.imageAssets = new HashMap<>();
        this.storeImageAssets = new HashMap<>();
    }

    public HashMap<String, Integer> getImageAssets() {
        return imageAssets;
    }

    public void setImageAssets() {
        imageAssets.put("bakery", R.drawable.icon_bakery);
        imageAssets.put("batter", R.drawable.icon_batter);
        imageAssets.put("desserts", R.drawable.icon_dessert);
        imageAssets.put("paneer", R.drawable.icon_paneer);
        imageAssets.put("frozenvegetables", R.drawable.icon_frozen);
        imageAssets.put("eggs", R.drawable.icon_egg);
        imageAssets.put("ghee", R.drawable.icon_ghee);
        imageAssets.put("lassi&chass", R.drawable.icon_lassi);
        imageAssets.put("milk", R.drawable.icon_milk);
        imageAssets.put("premiummilk", R.drawable.icon_premium_milk);
        imageAssets.put("yoghurt", R.drawable.icon_yoghurt);
        imageAssets.put("whiskey", R.drawable.icon_whiskey);
        imageAssets.put("vodka", R.drawable.icon_vodka);
        imageAssets.put("beer", R.drawable.icon_beer);
        imageAssets.put("wine", R.drawable.icon_wine);
        imageAssets.put("chicken", R.drawable.icon_chicken);
        imageAssets.put("fish", R.drawable.icon_fish);
        imageAssets.put("schoolpayments", R.drawable.school);
    }

    public HashMap<String, Integer> getStoreImageAssets() {
        return storeImageAssets;
    }

    public void setStoreImageAssets() {
        storeImageAssets.put("1", R.drawable.icon_desi_farms);
        storeImageAssets.put("2", R.drawable.icon_desi_farms);
        storeImageAssets.put("3", R.drawable.icon_school_store);
        storeImageAssets.put("4", R.drawable.icon_alcohol_store);
        storeImageAssets.put("5", R.drawable.icon_chicken_store);
        storeImageAssets.put("6", R.drawable.icon_bookstore);
    }

}