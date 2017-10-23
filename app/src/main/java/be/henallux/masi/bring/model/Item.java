package be.henallux.masi.bring.model;

/**
 * Created by hendrikdebeuf on 12/10/17.
 *
 * Sources used:
 * Recycler View inspiration source: https://www.android-examples.com/android-recyclerview-with-gridview-gridlayoutmanager/
 * Database Handler inspiration source: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * Card View Image path: http://square.github.io/picasso/
 */

public class Item {

    //Variables
    private int _item_list_id;
    private int _item_id;
    private String _item_name;
    private int _item_qty;
    private int _shop_id;

    // Constructeur vide
    public Item(){
    }

    //Constructeur
    public Item(int _shop_id, int _item_qty, String _item_name, int _item_id, int _item_list_id) {
        this._item_list_id = _item_list_id;
        this._item_id = _item_id;
        this._item_name = _item_name;
        this._item_qty = _item_qty;
        this._shop_id = _shop_id;
    }

    //Getters
    public int getItemListID() {
        return _item_list_id;
    }
    public int getItemID() {
        return _item_id;
    }
    public String getItemName() {
        return _item_name;
    }
    public int getItemQty() {
        return _item_qty;
    }
    public int getShopID() {
        return _shop_id;
    }

    //Setters
    public void setItemListID(int item_list_id) {
        this._item_list_id = item_list_id;
    }
    public void setItemID(int _item_id) {
        this._item_id = _item_id;
    }
    public void setItemName(String item_name) {
        this._item_name = item_name;
    }
    public void setItemQty(int item_qty) {
        this._item_qty = item_qty;
    }
    public void setShopID(int shop_id) {
        this._shop_id = shop_id;
    }

    //Return textuel
    @Override
    public String toString() {
        return "Item{" +
                "_item_list_id=" + _item_list_id +
                ", _item_id=" + _item_id +
                ", _item_name='" + _item_name + '\'' +
                ", _item_qty=" + _item_qty +
                ", _shop_id=" + _shop_id +
                '}';
    }
}
