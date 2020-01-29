package com.verbole.dcad.tabula;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.loader.content.CursorLoader;

/**
 * Created by dcad on 12/18/17.
 */

public class DictionaryLoader extends CursorLoader {
    Context mContext;
    MiseEnForme maMeF;

    public DictionaryLoader(Context context, MiseEnForme MeF) {
        super(context);
        mContext = context;
        maMeF = MeF;
    }

    @Override
    public Cursor loadInBackground() {

        Log.d("TABULA DictLoader","load in background");
        // new Mef ??
        //MiseEnForme MeF = new MiseEnForme(mContext);
        return maMeF.listeDesEntrees("");
    }


}