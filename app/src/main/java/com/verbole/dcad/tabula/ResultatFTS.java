package com.verbole.dcad.tabula;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dcad on 1/5/18.
 */

public class ResultatFTS //implements Comparable<ResultatFTS>
{
    String mot;
    String motsimple;
    String def;
    String motRecherche;
    String POS;
    String defCourte;
    String classement;
    String dico;

    public ResultatFTS() {
        this.mot = "";
        this.motsimple = "";
        this.def = "";
        this.motRecherche = "";
        this.POS = "";
        defCourte = "";
        classement = "";
        this.dico = "";
    }

    public ResultatFTS(String mot, String motRecherche, String def, String dico) {
        this.mot = mot;
        this.motsimple = mot;
        this.def = def;
        this.motRecherche = motRecherche;
        this.POS = "";
        defCourte = "";
        classement = "";
        this.dico = dico;
    }

    public ResultatFTS(String mot, String motsimple, String motRecherche, String def, String dico) {
        this.mot = mot;
        this.motsimple = motsimple;
        this.def = def;
        this.motRecherche = motRecherche;
        this.POS = "";
        defCourte = "";
        classement = "";
        this.dico = dico;
    }

    ResultatFTS copie() {
        ResultatFTS res = new ResultatFTS();
        res.POS = this.POS;
        res.def = this.def;
        res.dico = this.dico;
        res.classement = this.classement;
        res.defCourte = this.defCourte;
        res.mot = this.mot;
        res.motsimple = this.motsimple;
        res.motRecherche = this.motRecherche;
        return res;
    }

    int positionMotRechercheDsDefAvecPoint() {
        int res = -1;
        String re = " " + motRecherche + ".";
        if (def.toLowerCase().contains(re.toLowerCase())) {
            res = def.toLowerCase().indexOf(re.toLowerCase());
        }
        else {
            return -1;
        }
        return res;
    }
    int positionMotRechercheDsDef() {

        int res = -1;
        if (def.toLowerCase().contains(motRecherche.toLowerCase())) {
            String re = " " + motRecherche + "[ .,;]";
            String t = def.toLowerCase().replaceFirst(re,"XXXX");
            res = t.indexOf("XXXX");
        }
        else {
            return -1;
        }
        return res;
    }

    int positionMotRechercheDsDefCourte(String defCourte) {
        int res = -1;
       // Pattern pattern = Pattern.compile("\\w+");

      //  pattern matcher ??
        if (defCourte.toLowerCase().contains(motRecherche.toLowerCase())) {
            String re = "[ }]" + motRecherche.toLowerCase() + "[ .,:;]";
            String t = defCourte.toLowerCase().replaceFirst(re,"XXXX");
            res = t.indexOf("XXXX");
            //Log.d("ResultatFTS","posMotrech - " + defCourte);
        }
        else {
            //Log.d("ResultatFTS","posMotrech - " + defCourte);
            return -1;
        }
        return res;
    }

    static void trieResultatsFullTextSearch(List<ResultatFTS> liste) {
        Collections.sort(liste, new Comparator<ResultatFTS>() {
            @Override
            public int compare(ResultatFTS o1, ResultatFTS o2) {
                String mot1 = o1.motRecherche;
                String def1 = o1.classement.toLowerCase();
                String mot2 = o2.motRecherche;
                String def2 = o2.classement.toLowerCase();

                return getRangeOfMotDsTxt(mot1,def1) - getRangeOfMotDsTxt(mot2,def2);
            }
        });
    }

    static int getRangeOfMotDsTxt(String mot,String txt) {
        //int res = 1000;
        int res = txt.indexOf(mot);
        if (res >= 0) {
            return res;
        }

        return 10000;
    }
/*
    @Override
    public int compareTo(@NonNull ResultatFTS o) {
        String mot1 = motRecherche;
        String def1 = classement.toLowerCase();
        String mot2 = o.motRecherche;
        String def2 = o.classement.toLowerCase();

        return getRangeOfMotDsTxt(def1,mot1) - getRangeOfMotDsTxt(def2,mot2);

    }
    */
}
