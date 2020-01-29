package com.verbole.dcad.tabula;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by dcad on 1/5/18.
 */

public class GestionSettings {
    Context mContext;
    public GestionSettings(Context context) {
        mContext = context.getApplicationContext();
    }

    String getOrdreDicts() {
        //SharedPreferences mSettings = mContext.getSharedPreferences("Reglages", Context.MODE_PRIVATE);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String g = pref.getString("ordreDicts","P");
        //Log.d(FragmentDictionnaire.TAG,"Gestion Settings : " + g);
        return g;

    }
    int getTaillePolice() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String t = pref.getString("taillePolice","14");
        //Log.d(FragmentDictionnaire.TAG,"taille police : " + t);
        return Integer.parseInt(t);
    }
    void setTaillePolice(int taillePolice) {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        ed.putString("taillePolice",String.valueOf(taillePolice));
        ed.apply();
    }

    void setDicts(String dictionnaires) {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        ed.putString("ordreDicts",dictionnaires);
        //SharedPreferences mSettings;
        //mSettings =  mContext.getSharedPreferences("Reglages",Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = mSettings.edit();
        //editor.putString("Dictionnaires",dictionnaires);
        ed.apply();
    }

    void setDefault() {
        PreferenceManager.setDefaultValues(mContext,R.xml.reglages,false);
        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        //String g = pref.getString("ordreDicts","");
        //Log.d(FragmentDictionnaire.TAG,"Gestion Settings set defaut or dict : " + getOrdreDicts() + " - taille pol : " + getTaillePolice());
    }
}
