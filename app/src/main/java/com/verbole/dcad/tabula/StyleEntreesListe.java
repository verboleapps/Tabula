package com.verbole.dcad.tabula;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

/**
 * Created by dcad on 1/5/18.
 */

public class StyleEntreesListe {


    static String metEnformeMotDsListe(String mot)  {
        if (mot.equals("aereus")) {
            return mot;
        }
        return ChangeDiphtongue.changeDiphtonguesListe(mot);
    }

    // utilise qd recherche en FR
    static SpannableString metEnformeMotDsListeAvecTermeRecherche(ResultatFTS entree, int taillePolice)  {
        String mot = entree.mot;
        mot = ChangeDiphtongue.changeDiphtongue(mot);
        String defCourte = entree.defCourte;
        String dico = entree.dico;
        StyleHtml Stl = new StyleHtml();
        String nomDict = Stl.nomDictionnaire(dico);
        if (dico.equals("G")) {
            ParseGaffiot pg = new ParseGaffiot();
            //if (defCourte.length() > 100) {}
            defCourte = pg.parseDefSimple2(defCourte);
        }
        defCourte = defCourte.replaceAll("<[a-z /=\"]+>","");
        defCourte = defCourte.replaceAll("<[bi/]+>","");


        if (defCourte.length() > 100) {
            //defCourte = defCourte.substring(0,100);
        }
        String txtlab = mot;


        SpannableString res;
        if (!defCourte.isEmpty()) {
            if (entree.dico.equals("G")) {
                txtlab = mot + " (" + nomDict + ") " + defCourte; //"\n" + defCourte
            }
            if (entree.dico.equals("S")) {
                txtlab = mot + defCourte;
            }
            if (entree.dico.equals("P")) {
                txtlab = mot + ", " + defCourte;
            }
            if (defCourte.length() < entree.def.length()) {
                //txtlab += "...";
            }
          //  txtlab = txtlab.replaceAll("<[a-z]*...","...");
            //ss1.setSpan(new RelativeSizeSpan(2f), 0,5, 0); // set size
            //ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color
            res = new SpannableString(txtlab);
            //res.setSpan(new RelativeSizeSpan(1.25f),0,mot.length(),0);
            res.setSpan(new RelativeSizeSpan(0.75f),mot.length(),txtlab.length(),0);
        }
        else {
            res = new SpannableString(txtlab);
        }

        return res;
    }
}
