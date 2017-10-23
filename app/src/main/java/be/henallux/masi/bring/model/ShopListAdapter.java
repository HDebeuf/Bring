package be.henallux.masi.bring.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.henallux.masi.bring.ItemListActivity;
import be.henallux.masi.bring.MainActivity;
import be.henallux.masi.bring.R;
import be.henallux.masi.bring.data_access.DatabaseHandler;

/**
 * Created by hendrikdebeuf on 12/10/17.
 *
 * Sources used:
 * Recycler View inspiration source: https://www.android-examples.com/android-recyclerview-with-gridview-gridlayoutmanager/
 * Database Handler inspiration source: https://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 * Card View Image path: http://square.github.io/picasso/
 */

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> {

    //Variables
    private List<Shop> values;
    private Context context;
    private List<Item> itemlistunits;

    //Contructeur
    protected ShopListAdapter(Context context2, List<Shop> values2) {
        values = values2;
        context = context2;
    }

    //Création du layout
    @Override
    public ShopListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.shopunit, parent, false);
        return new ViewHolder(view1);
    }

    //Composer chaque vue de magasin
    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position) {

        //Définir le nom du magasin à afficher
        String shopName = values.get(position).getShopName();

        //Afficher le nombre d'articles dans une liste
        String itemsInList = shopItemUnits(context,values.get(position).getShopID()) + " " + context.getString(R.string.articles_in_list);

        Picasso.with(context).load(values.get(position).getImg_URL()).into(Vholder.shoplogoView);
        Vholder.textView.setText(shopName);
        Vholder.iteminlist.setText(itemsInList);
    }

    //Obtenir le nombre de magasins
    @Override
    public int getItemCount() {
        return values.size();
    }

    //Obtenir le nombre d'items d'un shop
    private int shopItemUnits(Context context, int shop_id){
        DatabaseHandler db = new DatabaseHandler(context);
        return db.getAllListItems(shop_id).size();
    }

    //Obtenir la liste d'articles
    private List<Item> shopItemLists(Context context, int shop_id){
        DatabaseHandler db = new DatabaseHandler(context);
        itemlistunits = db.getAllListItems(shop_id);
        return itemlistunits;
    }

    //Mettre à jour l'affichage des produits lors d'un update
    private void updateData(Item itemUpdated) {
        for (Item item : itemlistunits) {
            if (item.getItemName().equals(itemUpdated.getItemName())) {
                item.setItemID(itemUpdated.getItemID());
                item.setItemQty(itemUpdated.getItemQty());
                item.setItemListID(itemUpdated.getItemListID());
            }
        }
    }

    //Vérifier si un champ est vide
    private boolean isEmpty(EditText textfield) {
        return textfield.getText().toString().trim().length() == 0;
    }

    //Gestion des data de chaque magasin
    public class ViewHolder extends RecyclerView.ViewHolder {

        //Éléments d'une vue
        private TextView textView;
        private TextView iteminlist;
        private ImageView shoplogoView;
        private Button add;
        private Button list;

        //Intialiser une vue
        private ViewHolder(View v) {
            super(v);

            //Indiquer chaque champ
            textView = (TextView) v.findViewById(R.id.shopname);
            iteminlist = (TextView) v.findViewById(R.id.itemsinlist);
            shoplogoView = (ImageView) v.findViewById(R.id.shoplogo);
            list = (Button) v.findViewById(R.id.list);
            add = (Button) v.findViewById(R.id.add);

            //Click sur la vue pour ouvrir la liste d'articles
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){

                    final Shop shopClicked = values.get(getAdapterPosition());

                    //Indication de l'activité suivante et envoi de l'intent
                    Intent intent = new Intent(v.getContext(), ItemListActivity.class);
                    intent.putExtra("intent_shop_name",shopClicked.getShopName());
                    intent.putExtra("intent_shop_id",shopClicked.getShopID());
                    v.getContext().startActivity(intent);
                }
            });

            //Ouverture de l'activité liste
            list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){

                    final Shop shopClicked;
                    shopClicked = values.get(getAdapterPosition());

                    //Indication de l'activité suivante et envoi de l'intent
                    Intent intent = new Intent(v.getContext(), ItemListActivity.class);
                    intent.putExtra("intent_shop_name",textView.getText());
                    intent.putExtra("intent_shop_id",shopClicked.getShopID());
                    v.getContext().startActivity(intent);
                }
            });

            //Click sur le bouton ajouter un article
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Enregistrer le magasin cliqué
                    final Shop shopClicked;
                    shopClicked = values.get(getAdapterPosition());

                    //Récupérer la liste des articles du magasin
                    itemlistunits = shopItemLists(context,shopClicked.getShopID());

                    //Initialisation du design
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View Dialog = inflater.inflate(R.layout.activity_add_item, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(Dialog);
                    final AlertDialog A = builder.create();

                    //Champs à personnaliser
                    Toolbar edittitle = (Toolbar) Dialog.findViewById(R.id.additemtitle);
                    final EditText edititemname = (EditText) Dialog.findViewById(R.id.additemname);
                    final EditText edititemqty = (EditText) Dialog.findViewById(R.id.additemqty);
                    final Button canceledititem = (Button) Dialog.findViewById(R.id.additemcancel);
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

                            //Gestion des erreurs des champs
                            if (!isEmpty(edititemname) && !isEmpty(edititemqty)){
                                //Récupérer les champs
                                String newitemname = edititemname.getText().toString();
                                String newitemqty = edititemqty.getText().toString();

                                //Préparation des data
                                Item newItem = new Item(shopClicked.getShopID(), Integer.parseInt(newitemqty), newitemname, -1, -1);

                                DatabaseHandler db = new DatabaseHandler(context);
                                ContentValues output = db.addItem(newItem);
                                boolean is_added = Boolean.parseBoolean(output.get("is_added").toString());
                                int newitemid = Integer.parseInt(output.get("item_id").toString());
                                int newitemlistid = Integer.parseInt(output.get("item_list_id").toString());

                                //Objet final à ajouter
                                newItem = new Item(shopClicked.getShopID(), Integer.parseInt(newitemqty), newitemname, newitemid, newitemlistid);

                                //Ajout ou Mise à jour d'un article
                                if (is_added == true) {
                                    itemlistunits.add(newItem);
                                    Toast.makeText(context, context.getString(R.string.you_have_added) + " " + newitemqty  + " " + newitemname + " " + context.getString(R.string.in) + " " + shopClicked.getShopName(),
                                            Toast.LENGTH_LONG).show();
                                } else if (is_added == false) {
                                    updateData(newItem);
                                    Toast.makeText(context, context.getString(R.string.you_have_updated) + " " + newitemname + " " + context.getString(R.string.with) + " " + newitemqty + " " + context.getString(R.string.units_in) + " " + shopClicked.getShopName(),
                                            Toast.LENGTH_LONG).show();
                                }

                                //Mise à jour et quitter dialog
                                notifyDataSetChanged();
                                MainActivity.recyclerView_Adapter.notifyDataSetChanged();
                                A.dismiss();
                            } else { //Gestion des erreurs des champs
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
}
