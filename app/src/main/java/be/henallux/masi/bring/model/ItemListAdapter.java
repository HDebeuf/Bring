package be.henallux.masi.bring.model;

import android.content.ContentValues;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import be.henallux.masi.bring.MainActivity;
import be.henallux.masi.bring.R;
import be.henallux.masi.bring.data_access.DatabaseHandler;
import static be.henallux.masi.bring.R.*;

/**
 * Created by hendrikdebeuf on 12/10/17.
 *
 * Sources used:
 * Recycler View inspiration source: https://www.android-examples.com/android-recyclerview-with-gridview-gridlayoutmanager/
 * Database Handler inspiration source: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * Card View Image path: http://square.github.io/picasso/
 */

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder>{

    //Variables
    private List<Item> values;
    private Context context;

    //Constructeur
    public ItemListAdapter(Context context2, List<Item> values2) {
        values = values2;
        context = context2;
    }

    //Création du layout
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemlistunit, parent, false);
        return new ItemListAdapter.ViewHolder(v);
    }

    //Composer chaque vue d'article
    @Override
    public void onBindViewHolder(ItemListAdapter.ViewHolder Vholder, int position) {
        //définir le nom du produit à afficher
        final String itemName = values.get(position).getItemName();
        final int itemQty = values.get(position).getItemQty();

        //afficher les éléments
        Vholder.textView.setText(itemName);
        Vholder.textView2.setText(String.valueOf(itemQty));;
    }

    //Obtenir le nombre de d'articles
    @Override
    public int getItemCount() {
        return values.size();
    }

    //Vérifier si un champ est vide
    private boolean isEmpty(EditText textfield) {
        return textfield.getText().toString().trim().length() == 0;
    }

    //Supprimer un article de la liste
    private void removeItem(int position){
        values.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        //Éléments d'une vue
        private TextView textView;
        private TextView textView2;
        private CheckBox removeitem;

        //Intialiser une vue
        private ViewHolder(View v) {
            super(v);

            //Indiquer chaque champ
            textView = (TextView) v.findViewById(id.itemname);
            textView2 = (TextView) v.findViewById(id.itemqty);
            ImageButton edititemstart = (ImageButton) v.findViewById(id.edititemstart);
            removeitem = (CheckBox) v.findViewById(id.removeitem);

            //Supprimer un article
            removeitem.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    final Item itemClicked = values.get(getAdapterPosition());
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(itemClicked);
                    removeItem(values.lastIndexOf(itemClicked));
                    MainActivity.recyclerView_Adapter.notifyDataSetChanged();
                }
            });

            //Lancer le dialog d'édition d'item
            edititemstart.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick (View v){

                    //Récupérer l'article cliqué
                    final Item itemClicked = values.get(getAdapterPosition());

                    //Charger le layout
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View dialog2 = inflater.inflate(layout.activity_add_item, null);
                    final android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(context);
                    builder2.setView(dialog2);
                    final android.app.AlertDialog A = builder2.create();

                    //Initialiser les champs
                    final Toolbar edititle = (Toolbar) dialog2.findViewById(id.additemtitle);
                    final EditText edititemname = (EditText) dialog2.findViewById(id.additemname);
                    final EditText edititemqty = (EditText) dialog2.findViewById(id.additemqty);
                    final Button canceledititem = (Button) dialog2.findViewById(id.additemcancel);
                    final Button saveedititem = (Button) dialog2.findViewById(id.additemsubmit);
                    final TextInputLayout edititemnamelayout = (TextInputLayout) dialog2.findViewById(id.itemnamelayout);
                    final TextInputLayout edititemqtylayout = (TextInputLayout) dialog2.findViewById(id.itemqtylayout);

                    //Ouvrir le clavier lors de l'ajout d'un article
                    edititemname.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


                    //Affichage du titre et champs du dialog
                    edititle.setTitle(R.string.edit_item_title);
                    edititemname.setText(textView.getText().toString());
                    edititemqty.setText(textView2.getText().toString());
                    saveedititem.setText(R.string.save);

                    //Boutton pour annuler l'ajout
                    canceledititem.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            A.dismiss();

                            //Fermer le clavier
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    });

                    //Enregistrement de l'article
                    saveedititem.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick (View v){

                            if (!isEmpty(edititemname) && !isEmpty(edititemqty)){
                               //Variables à modifier
                               String newitemname = edititemname.getText().toString();
                               int newitemqty = Integer.parseInt(edititemqty.getText().toString());

                                //Préparation de l'update
                                Item editedItem = new Item(itemClicked.getShopID(),newitemqty,newitemname,itemClicked.getItemID(),itemClicked.getItemListID());

                                //Update en db et affichage
                                DatabaseHandler db = new DatabaseHandler(context);

                                ContentValues output = db.updateItem(editedItem);
                                boolean already_exists = Boolean.parseBoolean(output.get("already_exists").toString());
                                int newitemid = Integer.parseInt(output.get("item_id").toString());
                                int newitemlistid = Integer.parseInt(output.get("item_list_id").toString());

                                //Si le produits n'existe pas, il est ajouté. Sinon il est mis à jour
                                if (already_exists) {
                                    int toremove = -1;
                                    for (Item item : values) {
                                        //Mise à jour de la quantité de l'autre article
                                        if (item.getItemName().equals(newitemname)){
                                            Toast.makeText(context, context.getString(string.you_have_changed) + " " + item.getItemQty() + " " + newitemname + " " + context.getString(string.to) + " " + newitemqty + " " + newitemname,
                                                    Toast.LENGTH_LONG).show();
                                            item.setItemQty(newitemqty);
                                            item.setItemID(newitemid);
                                            item.setItemListID(newitemlistid);
                                        } else { //Suppression de l'article original
                                            if (item.getItemListID() == itemClicked.getItemListID()){
                                                toremove = values.lastIndexOf(itemClicked);
                                            }
                                        }
                                    }
                                    if (toremove != -1){
                                        removeItem(toremove);
                                    }
                                } else if (!false) { //Mise à jour de l'article original
                                    for (Item item : values) {
                                        if (item.getItemListID() == itemClicked.getItemListID()) {
                                            Toast.makeText(context, context.getString(string.you_have_changed) + " " + item.getItemQty() + " " + item.getItemName() + " " + context.getString(string.to) + " " + + newitemqty + " " + newitemname,
                                                    Toast.LENGTH_LONG).show();
                                            item.setItemName(newitemname);
                                            item.setItemQty(newitemqty);
                                            item.setItemID(newitemid);
                                            item.setItemListID(newitemlistid);
                                        }
                                    }
                                }

                                //Update de la recycler view et fermeture du dialogue
                                notifyDataSetChanged();
                                MainActivity.recyclerView_Adapter.notifyDataSetChanged();
                                A.dismiss();
                            } else { //gestion des erreurs de champ
                                edititemqtylayout.setErrorEnabled(true);
                                edititemnamelayout.setErrorEnabled(true);
                                if (isEmpty(edititemname) && isEmpty(edititemqty)){
                                    edititemqtylayout.setError(context.getString(R.string.please_insert_quantity));
                                    edititemnamelayout.setError(context.getString(R.string.please_insert_item_name));
                                } else {
                                    if (isEmpty(edititemqty)) {
                                        edititemqtylayout.setError(context.getString(R.string.please_insert_quantity));
                                        edititemnamelayout.setErrorEnabled(false);
                                    }
                                    if (isEmpty(edititemname)) {
                                        edititemnamelayout.setError(context.getString(R.string.please_insert_item_name));
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
        }
    }
};
