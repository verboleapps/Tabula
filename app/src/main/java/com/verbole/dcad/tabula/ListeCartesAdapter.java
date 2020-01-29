package com.verbole.dcad.tabula;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListeCartesAdapter extends ArrayAdapter<Carte> {
    Context mContext;
    List<Carte> entrees;
    int taillePolice;


    public ListeCartesAdapter(Context context, int textViewResourceId, List<Carte> objects, int taillePolice) {
        super(context, textViewResourceId, objects);
        mContext = context;
        entrees = objects;
        this.taillePolice = taillePolice;
    }

    @Override
    public @Nullable
    Carte getItem(int position) {
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
        Carte obj = getItem(position);

        //SpannableString sp = StyleEntreesListe.metEnformeMotDsListeAvecTermeRecherche(obj,taillePolice);
        String sp = obj.entree;

        TextView tv = v.findViewById(R.id.listRowVue);
       // tv.setMaxLines(2);
        tv.setText(sp);

        return v;
    }
}
