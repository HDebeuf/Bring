package be.henallux.masi.bring.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import be.henallux.masi.bring.model.Item;
import be.henallux.masi.bring.model.Shop;

/**
 * Created by hendrikdebeuf on 12/10/17.
 *
 * Sources used:
 * Recycler View inspiration source: https://www.android-examples.com/android-recyclerview-with-gridview-gridlayoutmanager/
 * Database Handler inspiration source: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * Card View Image path: http://square.github.io/picasso/
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    //Version de base de données
    private static final int DATABASE_VERSION = 1;

    //Nom Base de données
    private static final String DATABASE_NAME = "bring.db";

    //Nom des tables
    private static final String TABLE_SHOPS = "shops";
    private static final String TABLE_ITEM_LISTS = "item_lists";
    private static final String TABLE_ITEMS = "items";

    //Nom de colonnes
    private static final String KEY_SHOP_ID = "shop_id";
    private static final String KEY_SHOP_NAME = "shop_name";
    private static final String KEY_IMG_URL = "img_url";
    private static final String KEY_ITEM_ID = "item_id";
    private static final String KEY_ITEM_NAME = "item_name";
    private static final String KEY_ITEM_LIST_ID = "item_list_id";
    private static final String KEY_LIST_SHOP_ID = "list_shop_id";
    private static final String KEY_LIST_ITEM_ID = "list_item_id";
    private static final String KEY_ITEM_QTY = "item_qty";

    //Constructeur
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Création des tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SHOPS_TABLE = "CREATE TABLE " + TABLE_SHOPS + "("
                + KEY_SHOP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," + KEY_SHOP_NAME + " TEXT UNIQUE,"
                + KEY_IMG_URL + " TEXT" + ")";
        db.execSQL(CREATE_SHOPS_TABLE);

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," + KEY_ITEM_NAME + " TEXT UNIQUE"
                + ")";
        db.execSQL(CREATE_ITEMS_TABLE);

        String CREATE_ITEM_LIST_TABLE = "CREATE TABLE " + TABLE_ITEM_LISTS + "("
                + KEY_ITEM_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,"
                + KEY_LIST_SHOP_ID + " INTEGER,"
                + KEY_ITEM_QTY + " INTEGER,"
                + KEY_LIST_ITEM_ID + " INTEGER,"
                + " FOREIGN KEY(" + KEY_LIST_SHOP_ID + ") REFERENCES " + TABLE_SHOPS + "(" + KEY_SHOP_ID + "),"
                + " FOREIGN KEY(" + KEY_LIST_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + KEY_ITEM_ID + ")"
                + ")";
        db.execSQL(CREATE_ITEM_LIST_TABLE);
    }

    //Upgrade base de données
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);

        // Create tables again
        onCreate(db);
    }

    /**
     * GESTION MAGASINS
     */

    //AJOUT NOUVEAU MAGASIN
    public void addShop(Shop shop) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Récupération des valeurs d'un magasin
        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_NAME, shop.getShopName());
        values.put(KEY_IMG_URL, shop.getImg_URL().toString());

        //Ajout du magasin
        db.insert(TABLE_SHOPS, null, values);
        db.close();
    }

    // RÉCUPÉRATION DE TOUS LES MAGASINS
    public List<Shop> getAllShops() {

        //Variable
        List<Shop> shopList = new ArrayList<Shop>();

        // Requete en base de données
        String selectQuery = "SELECT  * FROM " + TABLE_SHOPS;

        //Permettre l'écriture de la base de données
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Récupérer chaque ligne dans la table
        if (cursor.moveToFirst()) {
            do {
                Shop shop = new Shop();
                shop.setShopID(cursor.getInt(0));
                shop.setShopName(cursor.getString(1));
                shop.setImg_URL(Uri.parse(cursor.getString(2)));

                // Ajout de chaque magasin dans une liste
                shopList.add(shop);
            } while (cursor.moveToNext());
        }

        //Fermeture de la base de données
        db.close();

        //Output de la liste de magasins
        return shopList;
    }

    /**
     * GESTION ARTICLES
     */

    //AJOUT NOUVEL ARTICLE
    public ContentValues addItem(Item item) {

        //Variables
        boolean is_added = true;
        long item_id = -1;
        long item_list_id = -1;
        ContentValues output = new ContentValues();
        ContentValues itemvalues = new ContentValues();
        ContentValues listvalues = new ContentValues();

        //Permettre l'écriture de la base de données
        SQLiteDatabase db = this.getWritableDatabase();

        //Récupération de l'article
        itemvalues.put(KEY_ITEM_NAME, item.getItemName());

        //Recherche de l'id du produit si déjà présent ou non
        Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID,
                     KEY_ITEM_NAME}, KEY_ITEM_NAME + "=?",
                new String[] { String.valueOf(item.getItemName())}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                //Préparation ajout d'un nouvel article
                item_id = db.insert(TABLE_ITEMS, null, itemvalues);
                itemvalues.put(KEY_ITEM_ID, item_id);
            }else if(cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ID)) != 0){
                //Préparation mise à jour de l'article
                itemvalues.put(KEY_ITEM_ID, cursor.getInt(0));
                db.update(TABLE_ITEMS, itemvalues, KEY_ITEM_ID + " = ?",
                        new String[] { String.valueOf(cursor.getInt(0)) });
                item_id =cursor.getInt(0);
            }
        }

        //Récupération des éléments pour l'ajout à la liste
        listvalues.put(KEY_LIST_SHOP_ID, item.getShopID());
        listvalues.put(KEY_LIST_ITEM_ID, item_id);
        listvalues.put(KEY_ITEM_QTY, item.getItemQty());

        //Recherche de l'id du shop et article dans les listes si déjà présent ou non
        Cursor cursor2 = db.query(TABLE_ITEM_LISTS, new String[] { KEY_ITEM_LIST_ID,
                        KEY_LIST_SHOP_ID,KEY_LIST_ITEM_ID}, KEY_LIST_SHOP_ID + "= ? AND " + KEY_LIST_ITEM_ID + "= ? ",
                new String[] { String.valueOf(item.getShopID()), String.valueOf(item_id) }, null, null, null);
        if (cursor2 != null) {
            cursor2.moveToFirst();
            if (cursor2.getCount() == 0) {
                //Préparation ajout d'un nouvel article à la liste
                item_list_id = db.insert(TABLE_ITEM_LISTS, null, listvalues);
                is_added = true;
            } else if(cursor2.getInt(cursor2.getColumnIndex(KEY_ITEM_LIST_ID)) != 0){
                //Préparation mise à jour de l'article dans la liste
                item_list_id = cursor2.getInt(0);
                listvalues.put(KEY_ITEM_LIST_ID, item_list_id);
                db.update(TABLE_ITEM_LISTS, listvalues, KEY_ITEM_LIST_ID + " = ?",
                        new String[] { String.valueOf(item_list_id) });
                is_added = false;
            }

            //Fermeture de la base de données
            db.close();
        }

        //Gestionnaire de l'output
        output.put("is_added",is_added);
        output.put("item_id",item_id);
        output.put("item_list_id",item_list_id);
        return output;
    }

    // RÉCUPÉRATION DE TOUS LES ARTICLES D'UN MAGASIN
    public List<Item> getAllListItems(int shop_id) {

        //Variable
        List<Item> itemList = new ArrayList<Item>();

        //Permettre la lecture de la base de données
        SQLiteDatabase db = this.getWritableDatabase();

        // Requete en base de données
        String selectQuery = "SELECT "
                + KEY_ITEM_LIST_ID + ", "
                + KEY_LIST_SHOP_ID + ", "
                + KEY_ITEM_QTY + ", "
                + TABLE_ITEMS + "." + KEY_ITEM_NAME + ", "
                + KEY_LIST_ITEM_ID
                + " FROM " + TABLE_ITEM_LISTS + ", " + TABLE_ITEMS
                + " WHERE " + TABLE_ITEM_LISTS + "." + KEY_LIST_SHOP_ID + "=" + shop_id
                + " AND "
                + TABLE_ITEMS + "." + KEY_ITEM_ID + "=" + TABLE_ITEM_LISTS + "." + KEY_LIST_ITEM_ID;

        // Récupérer chaque ligne dans les tables jointes
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Item listitem = new Item();
                listitem.setItemListID(cursor.getInt(0));
                listitem.setShopID(cursor.getInt(1));
                listitem.setItemQty(cursor.getInt(2));
                listitem.setItemName(cursor.getString(3));
                listitem.setItemID(cursor.getInt(4));
                // Ajout de chaque article dans une liste
                itemList.add(listitem);
            } while (cursor.moveToNext());
        }

        //Fermeture de la base de données
        db.close();

        // Output de la liste d'articles
        return itemList;
    }

    // MISE À JOUR D'UN ARTICLE
    public ContentValues updateItem(Item item) {

        //Variables
        boolean already_exists = false;
        long item_id = -1;
        long item_list_id = -1;
        ContentValues output = new ContentValues();
        ContentValues values2 = new ContentValues();
        ContentValues listvalues = new ContentValues();

        //Permettre l'écriture de la base de données
        SQLiteDatabase db = this.getWritableDatabase();

        // Préparer les données input
        values2.put(KEY_ITEM_NAME, item.getItemName());
        listvalues.put(KEY_LIST_SHOP_ID, item.getShopID());
        listvalues.put(KEY_ITEM_QTY, item.getItemQty());

        // Vérifier si le nouveau nom existe déjà
        Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ITEM_ID,
                        KEY_ITEM_NAME}, KEY_ITEM_NAME + "=?",
                new String[] { String.valueOf(item.getItemName())}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                //Ajouter le nouveau nom d'article et obtenir son id
                item_id = db.insert(TABLE_ITEMS, null, values2);
                listvalues.put(KEY_LIST_ITEM_ID, item_id);
            }else if(cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ID)) != 0){
                //Si le nouveau nom existe déjà, récupérer son id
                item_id = cursor.getInt(0);
                listvalues.put(KEY_LIST_ITEM_ID, item_id);
            }
        }

        //Recherche de l'id du shop et article dans les listes si déjà présent ou non
        Cursor cursor2 = db.query(TABLE_ITEM_LISTS, new String[] { KEY_ITEM_LIST_ID,
                        KEY_LIST_SHOP_ID,KEY_LIST_ITEM_ID}, KEY_LIST_SHOP_ID + "= ? AND " + KEY_LIST_ITEM_ID + "= ? ",
                new String[] { String.valueOf(item.getShopID()), String.valueOf(item_id) }, null, null, null);
        if (cursor2 != null) {
            cursor2.moveToFirst();
            if (cursor2.getCount() == 0) {
                //Si l'article n'existe pas encore dans la liste, update l'élément original
                item_list_id = item.getItemListID();
                listvalues.put(KEY_ITEM_LIST_ID,item_list_id);
                db.update(TABLE_ITEM_LISTS, listvalues, KEY_ITEM_LIST_ID + " = ?",
                        new String[] { String.valueOf(item.getItemListID()) });
                already_exists = false;
            } else if(cursor2.getInt(cursor2.getColumnIndex(KEY_ITEM_LIST_ID)) != 0){
                //Si l'article existe dans la liste, supprimer l'article original et modifier l'article déjà présent
                item_list_id = cursor2.getInt(0);
                listvalues.put(KEY_ITEM_LIST_ID, item_list_id);
                db.update(TABLE_ITEM_LISTS, listvalues, KEY_ITEM_LIST_ID + " = ?",
                        new String[] { String.valueOf(item_list_id) });
                db.delete(TABLE_ITEM_LISTS, KEY_ITEM_LIST_ID + " = ?",
                        new String[] { String.valueOf(item.getItemListID()) });
                already_exists = true;
            }

            //Fermeture de la base de données
            db.close();
        }

        //Gestionnaire de l'output
        output.put("already_exists",already_exists);
        output.put("item_id",item_id);
        output.put("item_list_id",item_list_id);
        return output;
    }


    // SUPPRESSION D'UN ARTICLE
    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEM_LISTS, KEY_ITEM_LIST_ID + " = ?",
                new String[] { String.valueOf(item.getItemListID()) });
        db.close();
    }

    // SUPPRESSION DE TOUS LES ARTICLES DAN UNE LISTE
    public void deleteItems(int shop_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEM_LISTS, KEY_LIST_SHOP_ID + " = ?",
                new String[] { String.valueOf(shop_id) });
        db.close();
    }
}
