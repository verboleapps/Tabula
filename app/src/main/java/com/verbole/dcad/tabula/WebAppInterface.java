package com.verbole.dcad.tabula;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by dcad on 3/24/16.
 */
interface WebAppInterfaceCallback {
    void recupereTexte(String texte);
    void recuperePages(int pageCourante, int nbPages);
    void recupereChapitreCourant(int chapitre);
    void debug(String texte);
    /*
    void dismissPopUp();
    void montrePopUp();
    */
}

public class WebAppInterface {
    Context mContext;
    WebAppInterfaceCallback callback;
    String ancientTexte = "";

    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void getText(String text) {
        // put selected text into clipdata

        if (text.length() > 0) {
            if (!text.equals("X")) {

               // if (!text.equals(ancientTexte)) {
                    callback.recupereTexte(text);
                    ancientTexte = text;
               // }
                /*
                ClipboardManager clipboard = (ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text",text);
                clipboard.setPrimaryClip(clip);
                */
            }
        }
    }

    @JavascriptInterface
    public void getPages(int pageCourante, int nbPages) {
        callback.recuperePages(pageCourante, nbPages);
    }

    @JavascriptInterface
    public void debugJS(String text) {
        callback.debug(text);
    }

    @JavascriptInterface
    public void getChapitre(int chapitreCourant) {

        callback.recupereChapitreCourant(chapitreCourant);
    }
    /*
    @JavascriptInterface
    public void dismissFen() {
        callback.dismissPopUp();
    }

    @JavascriptInterface
    public void montreFen() {
        callback.montrePopUp();
    }
    */
}