package com.verbole.dcad.tabula;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by dcad on 1/3/18.
 */

public class ListeEntreesAdapter extends ArrayAdapter<ResultatFTS> {

    Context mContext;
    List<ResultatFTS> entrees;
    int taillePolice;


    public ListeEntreesAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<ResultatFTS> objects, int taillePolice) {
        super(context, textViewResourceId, objects);
        mContext = context;
        entrees = objects;
        this.taillePolice = taillePolice;
    }

    @Override
    public @Nullable
    ResultatFTS getItem(int position) {
        return entrees.get(position);
    }

    @Override
    public int getCount() {
        return entrees.size();
    }

    /*
    @Override
    public long getItemId(int position) {
        return position;
    }
    */

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_row_texte, null);
        }
        ResultatFTS obj = getItem(position);
        //Log.d(Dictionnaire.TAG,"get v : " + String.valueOf(position) + " " + obj.mot + obj.dico);
        SpannableString sp = StyleEntreesListe.metEnformeMotDsListeAvecTermeRecherche(obj,taillePolice);

        TextView tv = v.findViewById(R.id.listRowVue);
        tv.setMaxLines(2);
        tv.setText(sp);

        return v;
    }

}
