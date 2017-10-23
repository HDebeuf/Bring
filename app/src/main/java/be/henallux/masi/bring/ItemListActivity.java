package be.henallux.masi.bring;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import be.henallux.masi.bring.data_access.DatabaseHandler;
import be.henallux.masi.bring.model.ItemListAdapter;
import be.henallux.masi.bring.model.Item;

/**
 * Created by hendrikdebeuf on 12/10/17.
 *
 * Sources used:
 * Recycler View inspiration source: https://www.android-examples.com/android-recyclerview-with-gridview-gridlayoutmanager/
 * Database Handler inspiration source: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * Card View Image path: http://square.github.io/picasso/
 */

public class ItemListActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public Context context;
    public static RecyclerView.Adapter recyclerView_Adapter2;
    public RecyclerView.LayoutManager recyclerViewLayoutManager2;
    public FloatingActionButton additemstart;
    public List<Item> items;
    public ImageButton deleteall;
    public ImageButton quit;

    //Mettre à jour des datas à afficher
    public void updateData(Item itemUpdated) {
        for (Item item : items) {
            if (item.getItemName().equals(itemUpdated.getItemName())) {
                item.setItemID(itemUpdated.getItemID());
                item.setItemQty(itemUpdated.getItemQty());
                item.setItemListID(itemUpdated.getItemListID());
            }
        }
    }

    //Vérification si un champ est vide
    private boolean isEmpty(EditText textfield) {
        return textfield.getText().toString().trim().length() == 0;
    }

    //Début de l'activité
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //Récupérer l'intent du magasin
        String shop_title = getIntent().getStringExtra("intent_shop_name");
        final int shop_id = getIntent().getIntExtra("intent_shop_id", 0);

        //Rjouter le nom du magasin dans la barre principale
        Toolbar toolbar = (Toolbar) findViewById(R.id.item_list_title);
        toolbar.setTitle(shop_title);

        context = ItemListActivity.this;

        //Sélection des articles du magasin sélectionné
        DatabaseHandler db = new DatabaseHandler(this);
        items = db.getAllListItems(shop_id);

        //Préparer la recycler view
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_items);
        recyclerViewLayoutManager2 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager2);
        recyclerView_Adapter2 = new ItemListAdapter(this, items);
        recyclerView.setAdapter(recyclerView_Adapter2);

        //Indiquer chaque élément du layout
        additemstart = (FloatingActionButton) findViewById(R.id.additemstart);
        deleteall = (ImageButton) findViewById(R.id.deleteall);
        quit = (ImageButton) findViewById(R.id.quit);

        //Ouvrir le dialogue pour ajouter un article
        additemstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Initialisation design du dialogue
                LayoutInflater inflater = LayoutInflater.from(context);
                final View Dialog = inflater.inflate(R.layout.activity_add_item, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(Dialog);
                final AlertDialog A = builder.create();

                //Champs à personnaliser
                Toolbar edittitle = (Toolbar) Dialog.findViewById(R.id.additemtitle);
                final EditText edititemname = (EditText) Dialog.findViewById(R.id.additemname);
                final EditText edititemqty = (EditText) Dialog.findViewById(R.id.additemqty);
                Button canceledititem = (Button) Dialog.findViewById(R.id.additemcancel);
                Button saveedititem = (Button) Dialog.findViewById(R.id.additemsubmit);
                final TextInputLayout edititemnamelayout = (TextInputLayout) Dialog.findViewById(R.id.itemnamelayout);
                final TextInputLayout edititemqtylayout = (TextInputLayout) Dialog.findViewById(R.id.itemqtylayout);

                //Ouvrir le clavier lors de l'ajout d'un article
                edititemname.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                //Affichage du texte des boutons et titre
                edittitle.setTitle(R.string.add_to_my_list);
                saveedititem.setText(R.string.save);

                //Annuler l'ajout d'un article
                canceledititem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        A.dismiss();

                        //Fermer le clavier
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                //Ajouter un article
                saveedititem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!isEmpty(edititemname) && !isEmpty(edititemqty)){
                            //Récupérer les champs
                            String newitemname = edititemname.getText().toString();
                            String newitemqty = edititemqty.getText().toString();

                            //Préparation des data
                            Item newItem = new Item(shop_id, Integer.parseInt(newitemqty), newitemname, -1, -1);

                            //Ajout des data en db et récupération des ids
                            DatabaseHandler db = new DatabaseHandler(context);
                            ContentValues output = db.addItem(newItem);
                            boolean is_added = Boolean.parseBoolean(output.get("is_added").toString());
                            int newitemid = Integer.parseInt(output.get("item_id").toString());
                            int newitemlistid = Integer.parseInt(output.get("item_list_id").toString());

                            //Objet final à ajouter
                            newItem = new Item(shop_id, Integer.parseInt(newitemqty), newitemname, newitemid, newitemlistid);

                            //Si le produits n'existait pas, il est ajouté. Sinon il est mis à jour
                            if (is_added == true) {
                                items.add(newItem);
                                Toast.makeText(context, newitemqty + " " + newitemname + " " + getString(R.string.have_been_added),
                                        Toast.LENGTH_LONG).show();
                            } else if (is_added == false) {
                                updateData(newItem);
                                Toast.makeText(context, getString(R.string.you_have_updated) + " " + newitemname + " "  + getString(R.string.with) + " " + newitemqty + " " + getString(R.string.units),
                                        Toast.LENGTH_LONG).show();
                            }

                            //Mise à jour et quitter dialog
                            recyclerView_Adapter2.notifyDataSetChanged();
                            MainActivity.recyclerView_Adapter.notifyDataSetChanged();
                            A.dismiss();
                        } else {//Gestion des erreurs des champs
                            edititemqtylayout.setErrorEnabled(true);
                            edititemnamelayout.setErrorEnabled(true);
                            if (isEmpty(edititemname) && isEmpty(edititemqty)){
                                edititemqtylayout.setError(getString(R.string.please_insert_quantity));
                                edititemnamelayout.setError(getString(R.string.please_insert_item_name));
                            } else {
                                if (isEmpty(edititemqty)) {
                                    edititemqtylayout.setError(getString(R.string.please_insert_quantity));
                                    edititemnamelayout.setErrorEnabled(false);
                                }
                                if (isEmpty(edititemname)) {
                                    edititemnamelayout.setError(getString(R.string.please_insert_item_name));
                                    edititemqtylayout.setErrorEnabled(false);
                                }
                            }
                        }

                        //Fermer le clavier
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });
                A.show();
            }

        });

        //Vider la liste d'articles
        deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(context);
                db.deleteItems(shop_id);
                items.clear();
                recyclerView_Adapter2.notifyDataSetChanged();
                MainActivity.recyclerView_Adapter.notifyDataSetChanged();
            }

        });

        //Quitter l'application
        quit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit", true);
                startActivity(intent);
                finish();
                MainActivity.recyclerView_Adapter.notifyDataSetChanged();
            }
        });
    }
}
