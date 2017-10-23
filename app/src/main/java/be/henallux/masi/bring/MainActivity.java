package be.henallux.masi.bring;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.List;

import be.henallux.masi.bring.data_access.DatabaseHandler;
import be.henallux.masi.bring.model.Shop;
import be.henallux.masi.bring.model.ShopListAdapter;

/**
 * Created by hendrikdebeuf on 12/10/17.
 *
 * Sources used:
 * Recycler View inspiration source: https://www.android-examples.com/android-recyclerview-with-gridview-gridlayoutmanager/
 * Database Handler inspiration source: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * Card View Image path: http://square.github.io/picasso/
 */

public class MainActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public static RecyclerView.Adapter recyclerView_Adapter;
    public RecyclerView.LayoutManager recyclerViewLayoutManager;
    public Context context;
    public ImageButton quit;
    public List<Shop> shops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Lancement de l'activité
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context = this;

        //Récupération de l'intent permettant la fermeture de l'app
        if( getIntent().getBooleanExtra("Exit", false)){
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }

        //Récupération de la liste de magasins
        DatabaseHandler db = new DatabaseHandler(this);
        shops = db.getAllShops();

        //Initialisation des magasins à l'installation
        if (shops.size() < 1) {
            db.addShop(new Shop(1, "Action", Uri.parse("android.resource://be.henallux.masi.bring/drawable/action")));
            db.addShop(new Shop(2, "Aldi", Uri.parse("android.resource://be.henallux.masi.bring/drawable/aldi")));
            db.addShop(new Shop(3, "Carrefour", Uri.parse("android.resource://be.henallux.masi.bring/drawable/carrefour")));
            db.addShop(new Shop(4, "Colruyt", Uri.parse("android.resource://be.henallux.masi.bring/drawable/colruyt")));
            db.addShop(new Shop(5, "Cora", Uri.parse("android.resource://be.henallux.masi.bring/drawable/cora")));
            db.addShop(new Shop(6, "Delhaize", Uri.parse("android.resource://be.henallux.masi.bring/drawable/delhaize")));
            db.addShop(new Shop(7, "Intermarché", Uri.parse("android.resource://be.henallux.masi.bring/drawable/intermarche")));
            db.addShop(new Shop(8, "Lidl", Uri.parse("android.resource://be.henallux.masi.bring/drawable/lidl")));
            db.addShop(new Shop(9, "Match", Uri.parse("android.resource://be.henallux.masi.bring/drawable/match")));
            db.addShop(new Shop(10, "Spar", Uri.parse("android.resource://be.henallux.masi.bring/drawable/spar")));
            shops = db.getAllShops();
        }

        //Préparation de la recycler view
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_shops);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView_Adapter = new ShopListAdapter(this, shops) {
        };
        recyclerView.setAdapter(recyclerView_Adapter);

        //boutton quitter l'application
        quit = (ImageButton) findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }



}

