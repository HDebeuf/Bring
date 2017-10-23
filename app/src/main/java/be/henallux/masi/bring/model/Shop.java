package be.henallux.masi.bring.model;

import android.net.Uri;

/**
 * Created by hendrikdebeuf on 10/10/17.
 */

public class Shop {

    //Variables
    private int _id;
    private String _shop_name;
    private Uri _img_url;

    // Empty constructor
    public Shop(){

    }
    //Constructor
    public Shop(int id, String shop_name, Uri img_url){
        this._id = id;
        this._shop_name = shop_name;
        this._img_url = img_url;
    }

    // Getters
    public int getShopID(){
        return this._id;
    }
    public String getShopName(){
        return this._shop_name;
    }
    public Uri getImg_URL(){
        return this._img_url;
    }

    // Setters
    public void setShopID(int id){
        this._id = id;
    }
    public void setShopName(String shop_name){
        this._shop_name = shop_name;
    }
    public void setImg_URL(Uri img_url){
        this._img_url = img_url;
    }
}


