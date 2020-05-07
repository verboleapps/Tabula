package com.verbole.dcad.tabula;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dcad on 1/5/18.
 */

public class GestionSettings {
    Context mContext;
    ArrayList<BookMark> listeBookMarks;

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

    void setBookMarkList(ArrayList<String> lb) {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        Set<String> s = new HashSet<String>(lb);
        ed.clear();
        ed.putStringSet("Bookmarks", s);
        // Log.d(ActivitePrincipale2.TAG,"GS : " + lb);

        ed.apply();
    }
    ArrayList<String> getBookmarksStringList() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> s = new HashSet<String>();
        Set<String> bl = pref.getStringSet("Bookmarks",s);
        ArrayList<String> res = new ArrayList<>();
        for (String el : bl) {
            res.add(el);
        }

        return res;
    }
    void effaceTousBookmarks() {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        ed.clear();
        ed.apply();
    }

    String bookmarkRepr(BookMark bookmark) {
        return bookmark.indAuteur + ";" + bookmark.indPartie + ";" + bookmark.indChapitre + ";" + bookmark.scrollY + ";" + bookmark.orientation;
    }

    void ajouteBookmark(BookMark bookMark) {
        ArrayList<String> lb = getBookmarksStringList();
        ArrayList<String> res = new ArrayList<>();
        for (String b : lb) {
            String[] els = b.split(";");
            int aut = Integer.parseInt(els[0]);
            int part = Integer.parseInt(els[1]);
            int chap = Integer.parseInt(els[2]);
            int scr = Integer.parseInt(els[3]);
            String orient = "P";
            if (els.length == 5) {
                orient = els[4];
            }

            if (aut != bookMark.indAuteur || part != bookMark.indPartie || chap != bookMark.indChapitre) {
                String bb = aut + ";" + part + ";" + chap + ";" + scr + ";" + orient;
                res.add(bb);
            }
        }
        String newbm = bookmarkRepr(bookMark);
        //Log.d(ActivitePrincipale2.TAG,"GS new bookmark : " + " : " + newbm);
        res.add(newbm);
        setBookMarkList(res);
    }
    void enleveBookmark(BookMark bookMark) {
        ArrayList<String> lb = getBookmarksStringList();
        ArrayList<String> res = new ArrayList<>();
        for (String b : lb) {
            String[] els = b.split(";");
            int aut = Integer.parseInt(els[0]);
            int part = Integer.parseInt(els[1]);
            int chap = Integer.parseInt(els[2]);
            int scr = Integer.parseInt(els[3]);

            if (aut != bookMark.indAuteur || part != bookMark.indPartie || chap != bookMark.indChapitre) {
                String bb = String.valueOf(aut) + ";" + String.valueOf(part) + ";" + String.valueOf(chap) + ";" + String.valueOf(scr);
                res.add(bb);
            }
        }
        setBookMarkList(res);
    }
    ArrayList<BookMark> getBookmarksList() {
        ArrayList<String> lb = getBookmarksStringList();
        ArrayList<BookMark> res = new ArrayList<>();
        for (String b : lb) {
            String[] els = b.split(";");
            int aut = Integer.parseInt(els[0]);
            int part = Integer.parseInt(els[1]);
            int chap = Integer.parseInt(els[2]);
            int scr = Integer.parseInt(els[3]);
            String orient = "P";
            if (els.length == 5) {
                orient = els[4];
            }

            BookMark bb = new BookMark(aut,part,chap,scr);
            bb.orientation = orient;
            res.add(bb);
        }
        Collections.sort(res);
        return res;
    }

    boolean isBookmarkDansListe(BookMark bookmark) {
        ArrayList<String> lb = getBookmarksStringList();
        //Log.d(ActivitePrincipale2.TAG,"GS : " + lb + " - " + bookmarkRepr(bookmark));
        for (String b : lb) {
            String[] els = b.split(";");
            int aut = Integer.parseInt(els[0]);
            int part = Integer.parseInt(els[1]);
            int chap = Integer.parseInt(els[2]);
            //int scr = Integer.parseInt(els[3]);

            if (aut == bookmark.indAuteur && part == bookmark.indPartie && chap == bookmark.indChapitre) {
                return true;
            }
        }
        return false;
    }

    void setDefault() {
        PreferenceManager.setDefaultValues(mContext,R.xml.reglages,false);
        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        //String g = pref.getString("ordreDicts","");
        //Log.d(FragmentDictionnaire.TAG,"Gestion Settings set defaut or dict : " + getOrdreDicts() + " - taille pol : " + getTaillePolice());
    }
}
