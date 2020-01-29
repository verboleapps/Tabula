package com.verbole.dcad.tabula;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by dcad on 12/16/17.
 */

public class StyleHtml {
    static String TAG = "TABULA StyleHTML";
    String nomPolice = "Avenir";
    String couleurLiens = "#3300CC";
    String couleurTxt = "#000000";
    //   let couleurTerm = "#CC3333"
    String bleuFonce = "#0000AA";

    int tailleGrandsBoutons;
    int taillePetitsBoutons;
    int taillePolicePetitInt;
    int tailleAssezGrande;

    static float dp2pixel(float dp, Activity act) {
        DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();

        return dp * displayMetrics.density;
    }

    static float conversionLargeur_enDP(float width, Activity act) {
        DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
        //float h = displayMetrics.heightPixels / displayMetrics.density;
        float w = width / displayMetrics.density;
        return w;
    }
    static float conversionHauteur_enDP(float height, Activity act) {
        DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
        float h = height / displayMetrics.density;
        //float w = width / displayMetrics.density;
        return h;
    }

    static float largeurFenetrePixels(Activity act) {
        Point size = new Point();
        Display display = act.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        //int height = size.y;

        return width;
    }
    static float hauteurFenetrePixels(Activity act) {
        Point size = new Point();
        Display display = act.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        //int width = size.x;
        int height = size.y;

        return height;
    }
    static float largeurFenetre(Activity act) {
        Point size = new Point();
        Display display = act.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //DisplayMetrics displayMetrics = new DisplayMetrics();
        // WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
        float h = displayMetrics.heightPixels / displayMetrics.density;
        float w = displayMetrics.widthPixels / displayMetrics.density;
/* // !!!! density est du type 0.75, 1, etc... -> density 1 = 160 dpi
        Log.d(TAG,"meth1 fenetre : w " + String.valueOf(width) + " - h " + String.valueOf(height));
        Log.d(TAG,"meth 2_0 fenetre : w " + displayMetrics.widthPixels + " - h " + displayMetrics.heightPixels + " - density : " + displayMetrics.density);
        Log.d(TAG,"meth2 fenetre : w " + String.valueOf(w) + " - h " + String.valueOf(h));

 */
        return w;
    }
    static float hauteurFenetre(Activity act) {
        Point size = new Point();
        Display display = act.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        //int width = size.x;

        DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
        float h = displayMetrics.heightPixels / displayMetrics.density;

        int height = size.y;
        return h;
    }

    static boolean verifieEcranEstTablette(Activity act) {
        return act.getResources().getBoolean(R.bool.is_tablet);

        /*
        float larg = StyleHtml.largeurFenetre(act);
        if (larg > 580) {
            return true;
        }
        return false;

         */
    }

    static boolean verifieEcranInferieur400dp(Activity act) {
        return act.getResources().getBoolean(R.bool.is_inferieur400dp);
    }

    static int policeBase(Activity act) {
        int tp = 14;
        float lg = StyleHtml.largeurFenetre(act);
        if (lg > 380 && lg < 600) {
            tp = 16;
        }
        if (lg > 600) {
            tp = 18;
        }

        return tp;
    }

    void adaptePolicesAEcran(float  largeurFenetre, int taillePoliceBase) {

        if (largeurFenetre >= 350) {
            taillePolicePetitInt = taillePoliceBase;
            tailleGrandsBoutons = taillePolicePetitInt + 6;
            tailleAssezGrande = taillePolicePetitInt + 5;
            taillePetitsBoutons = taillePolicePetitInt + 4;
        }
        if (largeurFenetre < 350) {
            taillePolicePetitInt = taillePoliceBase;
            tailleGrandsBoutons = taillePolicePetitInt + 4;
            tailleAssezGrande = taillePolicePetitInt + 3;
            taillePetitsBoutons = taillePolicePetitInt + 2;
        }

    }

    String nomDictionnaire(String nomDict) {
//Dictionnaire Classique Latin-Français M.-A Peigné
//Nouveau Dictionnaire Latin-Français E. de Suckau
        String nomComplet = "";
        if (nomDict.equals("P")) {
            nomComplet = "M.-A Peigné" ;//"Dictionnaire Classique";
        }
        if (nomDict.equals("S")) {
            nomComplet = "E. de Suckau"; //"Nouveau Dictionnaire";
        }
        if (nomDict.equals("G")) {
            nomComplet = "F. Gaffiot";
        }

        return nomComplet;
    }

    String enteteDict(String nomDict) {
//Dictionnaire Classique Latin-Français M.-A Peigné
//Nouveau Dictionnaire Latin-Français E. de Suckau
        String nomComplet = nomDictionnaire(nomDict);

        String sp = "<span class =\"nomDict\">" + nomComplet + "</span>";

        return sp;
    }

    String styleNomDictHtml(int taillePolice) {
        return ".nomDict { font-family: " + nomPolice + "; font-size: " + tailleAssezGrande + "px;text-decoration: underline;color:" + couleurTxt + "} ";
    }

    // Cree l'en-tete <head> meta et le style de la page html =========================
    String styleHtml(float  largeurFenetre, int taillePoliceBase) {
        adaptePolicesAEcran(largeurFenetre, taillePoliceBase);
        //int taillePolicePetitInt = adaptePoliceRetina(taillePolice : taillePoliceBase);


        // couleurs : bleu fonce : #3300CC   -  noir : #000000 : - rouge : #CC3333
        // gris tres leger : #F8F8F8 - plus sombre : #E0E0E0 - vert : #006633
        String res = "";//"<!DOCTYPE html> ";
       // res += "<head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=" + largeurFenetre + "\"> </head> ";

        res += "<head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=device-width\"> </head> ";


        res += "<style type=\"text/css\"> ";

        res += "*{-webkit-touch-callout: none; -webkit-user-select: none; } ";
        //-webkit-touch-callout: none;
        //-webkit-user-select: none;  /* Disable selection/copy in UIWebView */

        res += ".titreTableau { font-family:" + nomPolice + "; font-size: " + taillePetitsBoutons + "px; " + "text-decoration: none; line-height:30px;";
        res += "color:" + couleurTxt + ";} ";
        //res += ";background-color : #D3D3D3;} ";

        res += ".cache {display:none} ";

        res += ".highlight {background-color :#BFF9FD} ";

        res += ".sectionDef { text-align:justify; font-family:" + nomPolice + "; font-size: " + String.valueOf(taillePolicePetitInt + 2) + "px;";
        // res += "px; padding:8px ;"
        res += "color:" + couleurTxt + "} ";

        res += ".sectionEnTeteMot { text-align:center; font-family:" + nomPolice +"; font-size: ";
        res += String.valueOf(taillePolicePetitInt + 2) + "px;";
        res += "color:" + couleurTxt + "} ";

        res += ".motRecherche { font-family:" + nomPolice + "; font-size: ";
        res += tailleAssezGrande + "px; font-weight:bold; ";
        res += "color:" + couleurTxt + "} ";

        res += ".terme { font-family:" + nomPolice + "; font-weight:bold; color:" + couleurTxt + "} ";

        res += ".motlatin {color:#0000AA; }";

        res += ".navig { display :inline; width:100% ; margin:0px; padding:0px;border-bottom:1px solid black; } ";
        res += "ul { display :inline;list-style-type: none; margin:0px; padding:0px;  width:100%; } ";
        res += "li {line-height : 30px;height : 30px;padding-bottom : 5px;padding-top:5px}";

        res += ".voix {margin:auto;text-align:center;} ";

        res += ".boutonVoix {font-size: " + String.valueOf(taillePetitsBoutons) + "px ; border:1px solid black ;";
        res += "text-decoration: none; color:" + couleurTxt;
        res += " ; border-radius:5px; text-align:center;font-family:" + nomPolice + "} ";

        res += ".conjdef1G {display:inline-block; width:50% ;text-align:center;margin:-1px ;";
        res += " border-top:1px solid black; border-bottom:1px solid white; background-color :#FFFFFF ;padding:0px;font-family:" + nomPolice + "} ";

        res += ".conjdef2D {display:inline-block; width:50% ;text-align:center; border-left:1px solid black;border-bottom:1px solid black;" ;
        res += " ;padding:0px; background-color :#FFFFFF ;font-family:" + nomPolice + "} ";  //#F8F8F8

        res += ".conjdef2G {display:inline-block; width:50% ;text-align:center;margin-left:-1px; ";
        res += " border-bottom:1px solid black; background-color :#FFFFFF ;padding:0px;font-family:" + nomPolice + "} ";

        res += ".conjdef1D {display:inline-block; width:50% ;text-align:center; border-left:1px solid black;border-bottom:1px solid white;" ;
        res += " border-top:1px solid black;margin:-1px ;padding:0px; background-color :#FFFFFF ;font-family:" + nomPolice + "} ";
        res += ".boutonConjDef {font-size: " + String.valueOf(tailleGrandsBoutons) + "px; text-decoration: none; font-family:" + nomPolice + "} ";

        res += styleNomDictHtml(taillePolicePetitInt);

        res += ".paragraphe {text-align:justify; margin-left:20px; margin-right:20px;} ";

        res += ".entreeDic { font-family: " + nomPolice + "; font-size: " + taillePetitsBoutons + "px; " + "text-decoration: none; font-weight:bold;} ";
        res += ".entreeDicTabula { font-family: " + nomPolice + "; font-size: " + String.valueOf(taillePolicePetitInt + 2) + "px; " + "text-decoration: none; font-weight:bold;} ";

        res += ".termCoul {color:#CC3333; } ";

        res += ".formestrouvees {margin-left:5px; text-align:left; font-family: " + nomPolice + "; font-size: " + String.valueOf(taillePolicePetitInt + 2) + "px;} ";

        res += ".chiffreblanc {color:#FFFFFF;} ";
        res += "p {text-align:center;} ";
//Log.d("StyleHtml","tableauFlex displayblock ?????"); //.tableauFlex {display:block ;
        res += ".tableauFlex { color:" + couleurTxt + "; text-align:center; border-collapse:collapse; ";
        res += "font-size: " + String.valueOf(taillePolicePetitInt + 2) + "px;} ";

        res += "td,th {border:2px solid gray ; } ";
        res += "td.bord {border:none; } ";

        res += "a {text-decoration: none;} ";  // par defaut : couleur bleue !!
        res += "hr {width : 75%; border:none;height:2px; background-color: black;} ";

        res += "</style>";

        return res;

    }

    String styleHtmlRecherche(float largeurFenetre , int taillePoliceBase ) {

        adaptePolicesAEcran(largeurFenetre, taillePoliceBase);

        String res = "<head> <meta charset=\"UTF-8\"> ";
        //res += "<meta name=\"viewport\" content=\"width=" + String.valueOf(largeurFenetre) + "\"</head> ";
        res += "<meta name=\"viewport\" content=\"width=device-width\"> </head> ";


        res += "<style type=\"text/css\"> ";

        res += "*{-webkit-touch-callout: none; -webkit-user-select: none; } ";
        //-webkit-touch-callout: none; -webkit-text-size-adjust: 100%
        //-webkit-user-select: none; /* Disable selection/copy in UIWebView */

        res += ".cache {display:none} ";

        res += ".sectionDef { text-align:justify; font-family:" + nomPolice + "; font-size: " + String.valueOf(taillePolicePetitInt + 2) + "px;";
        res += "color:" + couleurTxt + "} ";

        res += ".sectionEnTeteMot { text-align:center; font-family: " + nomPolice + "; font-size: ";
        res += String.valueOf(taillePolicePetitInt + 2) + "px;";
        res += "color:" + couleurTxt + "} ";

        res += ".motRecherche { font-family: " + nomPolice + "; font-size: ";
        res += tailleAssezGrande + "px; font-weight:bold; ";
        res += "color:" + couleurTxt + "} ";

        res += ".motlatin {color:#0000AA; }";

        res += ".navig { display :inline; width:100% ; margin:0px; padding:0px;border-bottom:1px solid black; } ";
        res += "ul { display :inline;list-style-type: none; margin:0px; padding:0px;  width:100%; } ";
        res += "li {line-height : 30px;height : 30px;padding-bottom : 5px;padding-top:5px} ";

        res += ".defanalyse1G {display:inline-block; width:50% ;text-align:center;margin:-1px ;";
        res += " border-top:1px solid black; border-bottom:1px solid white; background-color :#FFFFFF ;padding:0px;font-family: " + nomPolice + ";} ";

        res += ".defanalyse2D {display:inline-block; width:50% ;text-align:center; border-left:1px solid black;border-bottom:1px solid black;" ;
        res += " margin:0px ;padding:0px; background-color :#FFFFFF ;font-family: " + nomPolice + ";} ";  //#F8F8F8

        res += ".defanalyse2G {display:inline-block; width:50% ;text-align:center;margin:0px ;margin-left:-1px; ";
        res += " border-bottom:1px solid black; background-color :#FFFFFF ;padding:0px;font-family: " + nomPolice + ";} ";

        res += ".defanalyse1D {display:inline-block; width:50% ;text-align:center; border-left:1px solid black;border-bottom:1px solid white;" ;
        res += " border-top:1px solid black;margin:-1px ;padding:0px; background-color :#FFFFFF ;font-family: " + nomPolice + ";} ";  //#F8F8F8

        res += ".boutondefanalyse {font-size: " + tailleGrandsBoutons + "px; text-decoration: none; color:" + couleurTxt + ";} ";

        res += styleNomDictHtml(taillePolicePetitInt);

        res += ".paragraphe {text-align:justify; margin-left:20px; margin-right:20px;} ";

        res += ".entreeDic { font-family: " + nomPolice + "; font-size: " + taillePetitsBoutons + "px; " + "text-decoration: none; font-weight:bold;} ";
        res += ".entreeDicTabula { font-family: " + nomPolice + "; font-size: " + String.valueOf(taillePolicePetitInt + 2) + "px; " + "text-decoration: none; font-weight:bold;} ";

        res += ".termCoul {color:#CC3333; } ";

        res += ".formestrouvees {margin-left:5px; text-align:left; font-family: " + nomPolice + "; font-size: " + String.valueOf(taillePolicePetitInt + 2) + "px;} ";

        res += ".petit { font-family: " + nomPolice + "; font-size: " + taillePetitsBoutons + "px; ";
        res += "text-decoration: none; line-height:20px; color:" + couleurTxt + "} ";

        res += "a {text-decoration: none;} ";  // par defaut : couleur bleue !!

        res += "</style>";


        return res;

    }

    String styleHtmlRechercheDsTexte(float largeurFenetre, int taillePoliceBase) {
        adaptePolicesAEcran(largeurFenetre, taillePoliceBase);

        //   let couleurTerm = "#CC3333"

        // couleurs : bleu fonce : #3300CC   -  noir : #000000 : - rouge : #CC3333
        // gris tres leger : #F8F8F8 - plus sombre : #E0E0E0 - vert : #006633
        String res = "";//"<!DOCTYPE html> ";
        res += "<head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=" +
                largeurFenetre + "\"> </head> ";

        res += "<style type=\"text/css\"> ";

        res += "*{-webkit-touch-callout: none; -webkit-user-select: none; }";
        //-webkit-touch-callout: none;
        //-webkit-user-select: none;  /* Disable selection/copy in UIWebView */

        res += ".sectionDef { text-align:left; font-family:" + nomPolice + "; font-size: " + (taillePolicePetitInt + 2) + "px; ";
        res += "text-decoration: none;  color:" + couleurTxt + "} ";

        res += ".sectionEnTeteMot { text-align:center; font-family:" + nomPolice + "; font-size: ";
        res += (taillePolicePetitInt + 2) + "px;";
        res += "color:" + couleurTxt + "} ";

        res += ".motRecherche { font-family:" + nomPolice + "; font-size: ";
        res += (taillePolicePetitInt + 5) + "px; font-weight:bold; ";
        res += "color:" + couleurTxt + "} ";

        res += ".motlatin {color:#0000AA; }";
        //line-height:20px;

        res += styleNomDictHtml(taillePolicePetitInt);

        res += ".entreeDic { font-family:" + nomPolice + "; font-size: " + (taillePolicePetitInt + 4) + "px; ";
        res += "text-decoration: none; font-weight:bold;} ";

        res += ".entreeDicTabula { font-family:" + nomPolice + "; font-size: " + (taillePolicePetitInt + 2) + "px; ";
        res += "text-decoration: none; font-weight:bold;} ";

        res += ".termCoul {color:#CC3333; } ";

        res += ".formestrouvees {margin-left:5px; text-align:left; font-family:" + nomPolice + "; font-size: "
                + (taillePolicePetitInt + 2) + "px;} ";


        //res += "p {text-align:center;} ";

        res += "a {text-decoration: none;} " ; // par defaut : couleur bleue !!

        //res += "a {color:" + couleurLiens + "; text-decoration: none; border-width: 1px ; border-style:solid ; border-color: #E0E0E0 ; border-radius:5px ; background-color:#F8F8F8} "

        res += "</style>";

        return res;

    }


    String styleHtmlGrammaire(int largeurFenetre, int taillePoliceBase) {
        adaptePolicesAEcran(largeurFenetre, taillePoliceBase);

        //   let couleurTerm = "#CC3333"
        String res = "";//"<!DOCTYPE html> ";
        res += "<head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=" +
                largeurFenetre + "\"> </head> ";

        res += "<style type=\"text/css\"> ";

        res += "*{-webkit-touch-callout: none; -webkit-user-select: none; }";
        //-webkit-touch-callout: none;
        //-webkit-user-select: none;  /* Disable selection/copy in UIWebView */


        res += ".petit { font-family: \"Helvetica\"; font-size: " + taillePolicePetitInt + "px; " + "text-decoration: none; " +
                "text-align:justify; line-height:20px; color:" + couleurTxt + "} ";

        res += "h1 {font-family: \"Helvetica\" ;font-size:" + (taillePolicePetitInt + 4) + "px}";
        res += "h2 {font-family: \"Helvetica\" ;font-size:" + (taillePolicePetitInt + 3) + "px}";
        res += "h3 { font-family: \"Helvetica\" ; font-size:" + (taillePolicePetitInt + 2) + "px}";
        res += "h4 {font-family: \"Helvetica\" ; font-size:" + (taillePolicePetitInt + 1) + "px}";

        //  res += ".titre5 {font-size:17px ; font-family: \"Helvetica\"}"

        res += ".tabmat1 { color:#0000FF; font-size:" + (taillePolicePetitInt + 4) + "px}";
        res += ".tabmat2 {color:#0000FF; font-size:" + (taillePolicePetitInt + 3) + "px}";
        res += ".tabmat3 {color:#0000FF; font-size:" + (taillePolicePetitInt + 2) + "px}";
        res += ".tabmat4 {color:#0000FF; font-size:" + (taillePolicePetitInt + 1) + "px}";
        res += ".tabmat5 {margin-left:30px; font-size:" + (taillePolicePetitInt) + "px}";

        res += ".titreTableau { font-family: \"Helvetica\"; text-decoration: none; line-height:30px; " +
                "color:#000000; font-size:" + (taillePolicePetitInt + 2) + "px}";

        res += ".terme { font-family: \"Helvetica\"; text-decoration: none;color:#000000 ;font-size:" + (taillePolicePetitInt + 5) + "px}";

        res += ".tableauFlex { font-family: \"Helvetica\"; display:block ; color:";
        res += couleurTxt + "; text-align:left; border-collapse:collapse ;font-size:" + taillePolicePetitInt + "px}";

        res += ".tableauNorm {border:1px solid gray ; border-collapse:collapse } ";
        res += ".tableauNorm td,th {border:1px solid gray ; border-collapse:collapse }";

        res += ".to {border-left:1px solid black}";
        res += ".tmilieu {border-left:1px solid black; padding-left: 2px; padding-right:2px;}";
        res += ".tinvisible {border-left:1px hidden;  padding-left: 2px; padding-right:2px;}";
        res += ".tp {border-top:1px solid black}";
        res += ".tb {border-bottom:1px solid black}";
        res += ".td {border-right:1px solid black}";

        res += ".metValeurLettre {color:#CC3333; }";
        res += ".motrouge {color:#CC3333; }";
        res += ".motbleu {color:#0000FF; }";
        res += ".motvert {color:#229954; }";
        res += ".motlatin {color:#0000AA; }";
        res += ".terminaison{color:#AA1111;}";

        res += ".retoursommaire { border-radius:8px;position: fixed ;left:20%;width: 50% ;" +
                " height:20px; bottom:10px; border:1px solid gray; background-color:#EEEFFF;text-align:center}";
        //padding-top: 2px;

        res += "a {text-decoration: none;} ";  // par defaut : couleur bleue !!
        res += "a:visited{color:#0000FF;} ";
        res += "p {text-align:center;} ";


        res += "</style>";

        return res;
    }

    String styleHtmlTextesAuteurs(float largeurFenetre, int taillePoliceBase) {
        adaptePolicesAEcran(largeurFenetre, taillePoliceBase);
        String res = "<!DOCTYPE html><html>";
        res += "<head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=" + String.valueOf(largeurFenetre) + "\"> </head> ";

        res += "<style type=\"text/css\"> ";

        //    res += "*{-webkit-touch-callout: none; -webkit-user-select: none; } "
        res += ".paragraphe {font-family: \"" + nomPolice + "\";text-align:justify; margin-left:20px; margin-right:20px;} ";

        res += ".marge {font-family: \"" + nomPolice + "\";font-size:" + String.valueOf(taillePolicePetitInt - 1) + "px; text-align:left; color:blue;} ";
        res += ".verset {font-size:1px} ";

        res += ".corpsTexte {margin-top:30px;}";

        res += "a {text-decoration: none;color:blue;margin-left:5px;margin-right:5px;} ";  // par defaut : couleur bleue !!

        res += "section {font-family: \"" + nomPolice + "\";text-align:justify;font-size: " + String.valueOf(taillePolicePetitInt) + "px;} ";

        res += "p {text-align:left;font-family: \"" + nomPolice + "\";font-size: " + String.valueOf(taillePolicePetitInt) + "px} ";

        res += ".cache {display:none}";
        //  res += "a {color:" + couleurLiens + "; text-decoration: none; border-width: 1px ; border-style:solid ; border-color: #E0E0E0 ; border-radius:5px ; background-color:#F8F8F8;margin-left:5px;margin-right:5px;} "

        //res += ".langue { border-radius:8px; text-align: center;padding-top: 10px;position: fixed ;width: 20% ;height:20px; right:30% ; top:5px; border:1px solid gray; background-color:#EEEFFF ;font-size: 20px }"

        res += "</style> ";

        return res;
    }

    String styleHtmlAide(int largeurFenetre, int taillePoliceBase) {
        adaptePolicesAEcran(largeurFenetre, taillePoliceBase);

        String res = "<!DOCTYPE html> ";
        res += "<head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=" + largeurFenetre + "\"> </head> ";

        res += "<style type=\"text/css\"> ";

        res += ".petit { font-family: \"" + nomPolice + "\"; font-size: " + taillePolicePetitInt + "px; " + "text-decoration: " +
                "none; text-align:justify;text-justify: distribute; line-height:20px; color:" + couleurTxt + "} ";

        res += ".soustitre {font-family: \"" + nomPolice + "\"; font-weight:bold; font-size: " + (taillePolicePetitInt + 4) + "px; } ";
        res += ".soussoustitre {font-family: \"" + nomPolice + "\";font-weight:bold; font-size: " + (taillePolicePetitInt + 2) + "px; } ";

        res += ".metValeurLettre {color:#CC3333; }";

        res += ".metvaleur {color:#5555DD;}";
        res += ".nomlivre {color:#0000BB; }";
        res += ".invis{color:#FFFFFF}";

        res += "p {text-align:justify; } ";
        res += "a {text-decoration: none;} ";

        res += "</style>";

        return res;
    }
}
