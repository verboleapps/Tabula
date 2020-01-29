package com.verbole.dcad.tabula;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dcad on 12/18/17.
 */


public class MiseEnForme {
    Context cont;
    Flexions flex;
    String motCourant = ""; // stocker le mot courant analyse
    // correspond a enregDico.motYo
    // utilise ds le cas ou on cherche la definition d'une paire aspectuelle car le motanalyse n'a pas
    // pu etre mis a jour ds la classe dictionnaire (ipad ou iphone)
    String POSselectionne = "";

    int indiceCourant = 0;


    public MiseEnForme(Context context) {
        cont = context;
        flex = new Flexions(cont);
    }


    Cursor listeDesEntrees(String searchText) {
        return flex.db.listeDesEntrees(searchText);
    }

/*
    ArrayList listeMotsDsHistory() {
        BaseDictHelper DICS = new BaseDictHelper(cont);
        ArrayList listnum = DICS.listeNumsInTable("History");
        return flex.listeMotsAPartirDeListeNum(listnum);
    }

    void addMotDsHistory(int mot) {
        BaseDictHelper DICS = new BaseDictHelper(cont);
        DICS.addMotInTable(mot,"History");

    }
    void deleteHistory() {
        BaseDictHelper DICS = new BaseDictHelper(cont);
        DICS.deleteHistory();

    }
*/
    List<ResultatFTS> rechercheEnregParDefinition(String motRecherche) {
        GestionSettings gs = new GestionSettings(cont);
        return flex.rechercheEnregParDefinition(motRecherche,gs.getOrdreDicts());
    }

    String metEnformeMotDsListe(String mot)  {
        return StyleEntreesListe.metEnformeMotDsListe(mot);
    }
    // fonction utilisee seulement pour liste flashcards
    SpannableString metEnformeMotDsListe(String mot,String pos,String def,int taillePolice) {
        String motDiph = ChangeDiphtongue.changeDiphtonguesListe(mot);
        String txtlab = motDiph;
        if (!pos.isEmpty()) {
            EnregDico er = new EnregDico();
            er.POS = pos;
            String posm = er.tr_POS();
            txtlab += " (" + posm + ")";
        }

        SpannableString res;
        res = new SpannableString(txtlab);
        res.setSpan(new RelativeSizeSpan(0.75f),motDiph.length(),txtlab.length(),0);

        return res;
    }

    // utilise qd recherche en FR
    SpannableString metEnformeMotDsListeAvecTermeRecherche(ResultatFTS entree, int taillePolice)  {
        return StyleEntreesListe.metEnformeMotDsListeAvecTermeRecherche(entree,taillePolice);
    }

    String traiteUrlScheme(String url ,float largeurFenetre , int taillePoliceBase)  {
        String res = "";
        // String urlScheme = "";
        int index1 = 0;
        String txt = "";
        return res;
    }

    /*
    AFAIK the files in the assets directory don't get unpacked. Instead, they are read directly from the APK (ZIP) file.
So, you really can't make stuff that expects a file accept an asset 'file'.
Instead, you'll have to extract the asset and write it to a seperate file
https://stackoverflow.com/questions/8474821/how-to-get-the-android-path-string-to-a-file-on-assets-folder
     */
    String scriptJQuery() {
        return "<script type=\"text/javascript\" src=\"file:///android_asset/scripts/jquery341.min.js\"></script>";
    }

    String getTextScript(String nomScript) {
        String sc = "";
        AssetManager asmatt = cont.getAssets();
        InputStream input;
        try {
            input = asmatt.open("scripts/" + nomScript + ".js");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            sc = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sc;
    }
/*
    String javascr(String nomScript) {
        String texte = "";

        AssetManager asmatt = cont.getAssets();
        InputStream input;
        try {
            input = asmatt.open(nomScript);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            texte = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return texte;
    }
    */

    // appelee par le ViewController
    String chargeDef_et_Flexions(String mot, Float largeurFenetre, int taillePoliceBase) {
        String res = "";

        // les enregistrements communs a Peigne et Gaffiot sont mis ensemble ds gestionData.rechercheEntreesDsBase

        ArrayList listeEntrees = flex.rechercheEntreesDsBase(mot, "","motsimple");
        //flex.db.rechercheEntreesDsBase(mot, "motsimple");

        if (listeEntrees.size() > 0) {
            res = chargeListeEntrees(listeEntrees, largeurFenetre,taillePoliceBase);
        }

        //tri

        return res;
    }

    String chargeDef_et_FlexionsFlashCard(String mot, String pos,Float largeurFenetre, int taillePoliceBase) {
        String res = "";

        // les enregistrements communs a Peigne et Gaffiot sont mis ensemble ds gestionData.rechercheEntreesDsBase
        String motT = ChangeDiphtongue.changeDiphtongueInverse(mot);
        ArrayList listeEntrees = flex.rechercheEntreesDsBasePourCarte(motT, pos,"motsimple");
        //flex.db.rechercheEntreesDsBase(mot, "motsimple");

        if (listeEntrees.size() > 0) {
            res = chargeListeEntrees(listeEntrees, largeurFenetre,taillePoliceBase);
        }

        //tri

        return res;
    }

    String chargeDefsFlashCard(String mot, String POS, float largeurFenetre, int taillePoliceBase) {
        String res = "";
        String motT = ChangeDiphtongue.changeDiphtongueInverse(mot); // car certains mots en æ, ...

        ArrayList<EnregDico>  listeEntrees = flex.rechercheEntreesDsBase(motT,POS,"motSimple");
        if (listeEntrees.size() > 0) {
            res = chargeListeEntreesFlashCards(listeEntrees, largeurFenetre,taillePoliceBase);
        }
        return res;
    }
    String chargeListeEntreesFlashCards(ArrayList<EnregDico> listeEntrees, float largeurFenetre, int taillePoliceBase) {
        StringBuilder res = new StringBuilder("");

        StyleHtml styleHtml = new StyleHtml();
        res.append(styleHtml.styleHtml(largeurFenetre,taillePoliceBase));
        res.append("<body >");

        // !!! placer avant l'autre script
        res.append(scriptJQuery());

        int compte = 0;
        for (EnregDico entree : listeEntrees) {
            String textF = declineInvariableHtml(entree,largeurFenetre,taillePoliceBase);//chargeDef_Entree(entree, compte, largeurFenetre, taillePoliceBase);
            res.append(textF + "</BR>");
            compte += 1;
            if (compte < listeEntrees.size()) {
                res.append("<hr/></BR>");
            }

        }

        res.append("</body>");
        // print(res)
        return res.toString();
    }



    String chargeDefsFlashCard_pourDemande(String mot, String POS,  float largeurFenetre, int taillePoliceBase) {
        //taillePoliceBase: Int = 16
        String res = "";
        String motT = ChangeDiphtongue.changeDiphtongueInverse(mot); // car certains mots en æ, ...

        ArrayList<EnregDico> listeEntrees = flex.rechercheEntreesDsBaseAvecDefCensure(motT,POS, "motsimple");


        if (listeEntrees.size() > 0) {
            res = chargeListeEntreesFlashCards_pourDemande(listeEntrees, largeurFenetre,taillePoliceBase);
        }


        return res;
    }

    String chargeListeEntreesFlashCards_pourDemande(ArrayList<EnregDico> listeEntrees, float largeurFenetre, int taillePoliceBase) {
        //int taillePoliceBase: Int = 16
        StringBuilder res = new StringBuilder("");
        StyleHtml styleHtml = new StyleHtml();
        res.append(styleHtml.styleHtml(largeurFenetre,taillePoliceBase));
        res.append("<body >");
        res.append(scriptJQuery());


        int compte = 0;
        for (EnregDico entree : listeEntrees) {
            //let textF = chargeDefEntree(entree: entree, numero : compte, largeurFenetre: largeurFenetre, taillePoliceBase: taillePoliceBase)
            //Log.d("===","mot : " + entree.motOrig + " - def : " + entree.def);
            String  textF = "";
            textF += "<p class = \"sectionDef\">";
            textF += censureDefinition(entree);

            textF += "</p>";


            res.append(textF + "</BR>");
            compte += 1;
            if (compte < listeEntrees.size()) {
                res.append("<hr/></BR>");
            }

        }
        res.append("</body>");
        //Log.d("===","entree : " + res);
        return res.toString();
    }

/*
    func metEnformeMotDsListeFlashCards(mot : String, pos : String, def : String, taillePolice : CGFloat) -> NSAttributedString {
        let posm = tr_POS(pos: pos)
        return metEnformeMotDsListe(mot: mot, pos: posm, def: "", taillePolice: taillePolice)
    }
     */

    String censureDefinition(EnregDico enreg) {
        String res = enreg.def;
        String stringReplace = " - - - - ";
        String pattern = "<span class = \\\"entreeDicTabula\\\">[a-zA-Z0-9]+</span>";
        res = res.replaceAll(pattern,stringReplace);
        return res;
    }
    String metEnFormeCustomDef(String customDef, String textHtml) {
        String res = textHtml;
        String s1 = "<CUSTOMDEF>";
        String enteteDict = "<span class = \"titreDico\"><strong>Card definition</strong></span></BR>";
        String s2 = enteteDict + customDef + "<BR/><BR/>";
        res = res.replace(s1, s2);
        res = res.replace("No definition found", "");
        return res;
    }

    String chargeDef_et_FlexionsA_Partir_Lien(String mot,float largeurFenetre, int taillePoliceBase) {
        String res = "";
        // les enregistrements communs a Peigne et Gaffiot sont mis ensemble ds gestionData.rechercheEntreesDsBase
        ArrayList listeEntrees;
        String tr = "motsimple";
        if (mot.contains(" ")) {
            tr = "mot";
        }

        listeEntrees = flex.rechercheEntreesDsBase(mot, "", tr);
        //flex.db.rechercheEntreesDsBase(mot, tr);

        if (listeEntrees.size() > 0) {
            res = chargeListeEntrees(listeEntrees, largeurFenetre,taillePoliceBase);
            res += "</body>";
        }

        //print(res)
        return res;
    }

    String chargeListeEntrees(ArrayList<EnregDico> listeEntrees, float largeurFenetre, int taillePoliceBase) {
        StringBuilder res = new StringBuilder("");

        StyleHtml styleHtml = new StyleHtml();
        res.append(styleHtml.styleHtml(largeurFenetre,taillePoliceBase));
        res.append("<body >");

         // !!! placer avant l'autre script
        res.append(scriptJQuery());
        boolean unique = true;
        if (listeEntrees.size() > 1) {
            unique = false;
        }
        int compte = 0;

        StringBuilder scrpt = new StringBuilder();

        for (EnregDico entree : listeEntrees) {

            String textF = chargeDef_et_Flexion_Entree(entree, compte, largeurFenetre, taillePoliceBase, unique);
            res.append(textF + "</BR>");

            compte += 1;
            if (compte < listeEntrees.size()) {
                res.append("<hr/></BR>");
            }

        }

        res.append("</body>");
        // print(res)
       // Log.d("MEF",res.toString());

        return res.toString();
    }

    String chargeDef_et_Flexion_Entree(EnregDico entree, int numero , float largeurFenetre, int taillePoliceBase, boolean unique) {
        String textF = "";
        String POS = entree.POS;
        //String strConjDef = "";
        boolean isBouton = false;
        int maxChar = 1000;
        if (largeurFenetre < 700) {
            maxChar = 500;
        }
        if (entree.def.length() > maxChar) {
            isBouton = true;
        }

        if (!unique) {
            isBouton = true;
        }

        if (entree.decl1 == 0 || entree.decl1 >= 9) {
            isBouton = false;
            //return declineInvariableHtml(entree, largeurFenetre, taillePoliceBase);
        }
        if (entree.mot.contains(" ")) {
            isBouton = false;
            //return declineInvariableHtml(entree, largeurFenetre, taillePoliceBase);
        }

        if (entree.POS.equals("NUM")) {
            if (entree.decl1 == 2 && (entree.type.equals("X") || entree.type.equals("CARD"))) { // invariable
                isBouton = false;
                //return declineInvariableHtml(entree, largeurFenetre, taillePoliceBase);

            }
        }

        textF += enTeteHtml(entree,isBouton);
        textF = textF.replaceAll("<DEFINITION>", entree.def);
        textF = textF.replaceAll("defX", "def" + String.valueOf(numero));
        textF = textF.replaceAll("declX", "decl" + String.valueOf(numero));

        String espaceT = "";
        String scriptConjDef = "";

        if (isBouton) {
            String strConjDef = boutonConjDefHtml(numero, entree.POS);
            textF = textF.replace("<CONJDEF>", strConjDef);


            scriptConjDef = getTextScript("boutonConjDef");
            scriptConjDef = scriptConjDef.replaceAll("X", String.valueOf(numero));

            //textF = textF.replace("<SCRIPTCONJDEF>","<script>" + scriptConjDef + "</script>");


            espaceT = "<p class=\"petit\" style=\"margin-top:20px\"></p>";
        }
        else { }


        if (POS.equals("V")) {
            //textF = enTeteVerbeHtml(entree, largeurFenetre,  taillePoliceBase);

            String conj = conjugueEntree(entree, numero, largeurFenetre,  taillePoliceBase);

            textF += espaceT + conj;
            //textF = textF.replaceAll( "defX", "def" + String.valueOf(numero));
            //textF = textF.replaceAll( "declX", "decl" + String.valueOf(numero));
            textF = textF.replaceAll( "voixActiveX", "voixActive" + String.valueOf(numero));
            textF = textF.replaceAll( "voixPassiveX", "voixPassive" + String.valueOf(numero));
            textF = textF.replaceAll( "boutonVoixActiveX", "boutonVoixActive" + String.valueOf(numero));
            textF = textF.replaceAll( "boutonVoixPassiveX", "boutonVoixPassive" + String.valueOf(numero));

        }
        else {

            if (POS.equals("N") || POS.equals("NP") || POS.equals("NPP") || POS.equals("ADJ") || POS.equals("ADJP") || POS.equals("PRON") || POS.equals("NUM") || POS.equals("VPAR") || POS.equals("VPAR_ADJ")) {

                String conj = declineEntree(entree, largeurFenetre,  taillePoliceBase);
                textF += espaceT + conj;
            }
            /*
            else {
                EnregDico entInv = entree.copie();
                entInv.decl1 = 0;
                textF = declineEntree(entInv, largeurFenetre,  taillePoliceBase);
            }
            */
        }

        textF += "</section>";
        textF += "<script>" + scriptConjDef + "</script>";
        return textF;
    }

    String boutonConjDefHtml(int numero, String POS) {
        String strConjDef = "";
        String labelStr = "Déclinaison";
        if (POS.equals("V")) {
            labelStr = "Conjugaison";
        }
        strConjDef += "<div class = \"navig\">";
        strConjDef += "<ul>";  //<li class="active"
        strConjDef += "<li id = \"definition" + String.valueOf(numero) + "\" class = \"conjdef1G\">";
        strConjDef += "<span class = \"boutonConjDef\" id =\"boutonDef" + String.valueOf(numero) + "\">Définition</span></li><!--";

        strConjDef += "--><li id = \"conjugaison" + String.valueOf(numero) + "\" class = \"conjdef2D\">";
        strConjDef += "<span class = \"boutonConjDef\" id =\"boutonConj" + String.valueOf(numero) + "\">" + labelStr + "</span></li> ";
        strConjDef += "</ul>";
        strConjDef += "</div>  "; //</div>
        strConjDef += "<BR/>";

        //Log.d("MEF",strConjDef);

        return strConjDef;
    }

    String conjugueEntree(EnregDico entree, int numero, float largeurFenetre, int taillePoliceBase) {
        String textF  = "";
        //String POS = entree.POS;
        boolean bool1 = entree.type.equals("TRANS"); // || entree.type == "X"
        boolean bool2 = (!entree.stem4.isEmpty() && !entree.stem4.equals("zzz")); //&& entree.type == "X" ???

        boolean bool3 = entree.type.equals("DEP") || entree.type.equals("SEMIDEP") || entree.decl1 == 5;
        boolean bool4 = entree.mot.equals("volo") || entree.mot.equals("nolo") || entree.mot.equals("malo") || entree.mot.equals("aio") || entree.mot.equals("inquio");

        if ((bool1 || bool2) && !bool3 && !bool4) {
            // peuvent avoir actif/passif
            // !! apparemment il faut placer le script apres un div ?? <> IOS ???

            /*
            String scriptBoutonVoix = getTextScript("boutonVoix");
            scriptBoutonVoix = "<script>" + scriptBoutonVoix.replaceAll( "X", String.valueOf(numero)) + "</script>";


            textF += "</BR><div class=\"voix\"><VOIX></div>" + scriptBoutonVoix;

            ArrayList listeDesAct = flex.declineMotGeneral(entree, "ACTIVE"); //voix
            textF += "<div id=\"voixActiveX\">" + declineVerbeHtml(entree, "ACTIVE", listeDesAct, largeurFenetre, taillePoliceBase) + "</div>";

            ArrayList listeDesPass = flex.declineMotGeneral(entree, "PASSIVE"); //voix
            textF += "<div id=\"voixPassiveX\">" + declineVerbeHtml(entree, "PASSIVE", listeDesPass, largeurFenetre, taillePoliceBase) + "</div>";

            String str = "<span id=\"boutonVoixActiveX\" class=\"boutonVoix\">&nbsp;Voix Active&nbsp;</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            str += "<span id=\"boutonVoixPassiveX\" class=\"boutonVoix\">&nbsp;Voix Passive&nbsp;</span></br>";


            textF = textF.replaceAll( "<VOIX>", str);

             */
            String VACT = "<div class=\"voix\"><span class=\"titreTableau\"><b>VOIX ACTIVE</b></span></div>";
            String VPASS = "<br><br><div class=\"voix\"><span class=\"titreTableau\"><b>VOIX PASSIVE</b></span></div>";

            String scriptBoutonVoix = getTextScript("boutonVoix");
            scriptBoutonVoix = "<script>" + scriptBoutonVoix.replaceAll( "X", String.valueOf(numero)) + "</script>";
            scriptBoutonVoix = "";
            //str += "<script>" + scriptBoutonVoix + "</script>";

            textF += "</BR><div class=\"voix\"><VOIX></div>" + scriptBoutonVoix;

            ArrayList listeDesAct = flex.declineMotGeneral(entree, "ACTIVE"); //voix

            textF += VACT;
            textF += "<div id=\"voixActiveX\">" + declineVerbeHtml(entree, "ACTIVE", listeDesAct, largeurFenetre, taillePoliceBase) + "</div>";

            ArrayList listeDesPass = flex.declineMotGeneral(entree, "PASSIVE"); //voix

            textF += VPASS;
            textF += "<div id=\"voixPassiveX\">" + declineVerbeHtml(entree, "PASSIVE", listeDesPass, largeurFenetre, taillePoliceBase) + "</div>";

        }
        else {
            ArrayList listeDes  = flex.declineMotGeneral(entree, "ACTIVE");
            textF = declineVerbeHtml(entree, "ACTIVE", listeDes, largeurFenetre, taillePoliceBase);
        }
        /*
         if ((bool1 || bool2) && !bool3 && !bool4) {

         if (voix == "ACTIVE") {
         str = " <a href=\"voixactive://voixactive/" + String(index) + "\"><span class=\"voix1\">&nbsp;Voix Active&nbsp;</span></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
         str += " <a href=\"voixpassive://voixpassive/" + String(index) + "\"><span class=\"voix2\">&nbsp;Voix Passive&nbsp;</span></a> </BR>"
         }
         else {
         str = " <a href=\"voixactive://voixactive/" + String(index) + "\"><span class=\"voix2\">&nbsp;Voix Active&nbsp;</span></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
         str += " <a href=\"voixpassive://voixpassive/" + String(index) + "\"><span class=\"voix1\">&nbsp;Voix Passive&nbsp;</span></a> </BR>"
         }


         textF = textF.replaceAll( "<VOIX>", with: str, options: NSString.CompareOptions.regularExpression, range: nil)
         }
         */

        return textF;
    }

    String declineEntree(EnregDico entree, float largeurFenetre, int taillePoliceBase) {
        String POS = entree.POS;

        String textF = "";

        //let trouve2 = metEnFormeMot(mot: trouve)
        if (entree.decl1 == 0 || entree.decl1 >= 9) {
            //textF = declineInvariableHtml(entree, largeurFenetre, taillePoliceBase);
        }
        else {
            ArrayList listeDes = flex.declineMotGeneral(entree, "ACTIVE"); //voix
            if (POS.equals("N") || POS.equals("NP") || POS.equals("NPP")) {
                textF = declineNomHtml(entree, listeDes, largeurFenetre, taillePoliceBase);
            }
            if (POS.equals("ADJ") || POS.equals("ADJP") || POS.equals("VPAR_ADJ")) {
                textF = declineADJHtml(entree, listeDes, largeurFenetre, taillePoliceBase);
            }

            if (POS.equals("PRON")) {
                textF = declinePRONHtml(entree, listeDes, largeurFenetre, taillePoliceBase);
            }

            if (POS.equals("NUM")) {
                textF = declineNUMHtml(entree, listeDes, largeurFenetre, taillePoliceBase);
            }


            if (POS.equals("VPAR")) {
                textF = declineVPARHtml(entree, listeDes, largeurFenetre, taillePoliceBase);
            }
        }

        /*
        if (POS == "ADV") {
            textF = declineInvariableHtml(nomTrouve: entree, largeurFenetre: largeurFenetre, taillePoliceBase: taillePoliceBase)
        }

        if (POS == "PREP") {
            textF = declineInvariableHtml(nomTrouve: entree, largeurFenetre: largeurFenetre, taillePoliceBase: taillePoliceBase)
        }
        if (POS == "INV" || entree.decl1 == 0) {
            textF = declineInvariableHtml(nomTrouve: entree, largeurFenetre: largeurFenetre, taillePoliceBase: taillePoliceBase)
        }
        */
        return textF;
    }

    // ============ pour definition a partir de fullTextSearch =============================
    String chargeMot_Definition(ResultatFTS entree, Float largeurFenetre, int taillePoliceBase) {

        // let trouve = metEnFormeMot(nomTrouve)
        StyleHtml styleHtml = new StyleHtml();
        String res = styleHtml.styleHtml(largeurFenetre,taillePoliceBase);
        res += "<body >";
        res += " <p  class=\"sectionDef\"; style=\"margin-top:10px\"> ";

        if (entree.dico.equals("G")) {
            ParseGaffiot PG = new ParseGaffiot();
            res += PG.parseDefSimple(entree.def) + "</p>";
        }
        else {
            res += entree.def + "</p>";
        }

        return res;
    }

    String enteteMot(EnregDico motTrouve) {
        String res = "";
        String span1 = "<span class=\"motRecherche\">";
        String span2 = "</span>";

        EnregDico trouve = motTrouve.metEnFormeMot();
        if (motTrouve.POS.equals("N")) {
            res = span1 + trouve.mot + span2;
            res += " : " + trouve.POS + " " + trouve.genre;

            String groupeFlex = motTrouve.donneGroupeFlex();
            if (!groupeFlex.isEmpty()) {
                res += " (" + motTrouve.donneGroupeFlex() + ")";
            }
        }
        if (motTrouve.POS.equals("V")) {
            res = span1 + trouve.mot + span2;
            res += " : " + trouve.POS + " " + trouve.type;

            String groupeFlex = motTrouve.donneGroupeFlex();
            if (!groupeFlex.isEmpty()) {
                res += " (" + motTrouve.donneGroupeFlex() + ")";
            }

        }
        if (motTrouve.POS.equals("ADJ")) {
            res = span1 + trouve.mot + span2;
            res += " : " + trouve.POS + " " + trouve.type;

            String groupeFlex = motTrouve.donneGroupeFlex();
            if (!groupeFlex.isEmpty()) {
                res += " (" + motTrouve.donneGroupeFlex() + ")";
            }
        }
        if (motTrouve.POS.equals("VPAR")) {
            res = span1 + trouve.mot + span2;
            res += " : " + trouve.POS + " " + trouve.type;
            /*
            let groupeFlex = donneGroupeFlex(pos: nomTrouve.POS, decl1: nomTrouve.decl1, decl2: nomTrouve.decl2)
            if (groupeFlex != "") {
                res += trouve.type + " (" + donneGroupeFlex(pos: nomTrouve.POS, decl1: nomTrouve.decl1, decl2: nomTrouve.decl2) + ")"
            }
            */
        }
        if (motTrouve.POS.equals("NUM")) {
            res = span1 + trouve.mot + span2;
            res += " : " + trouve.POS + " " + trouve.type;
        }
        if (motTrouve.POS.equals("PRON")) {
            res = span1 + trouve.mot + span2;
            res += " : " + trouve.POS + " " + trouve.type;
        }
        if (motTrouve.POS.equals("ADV")) {
            res = span1 + trouve.mot + span2;
            res += " : " + trouve.POS;
        }
        if (motTrouve.POS.equals("PREP")) {
            res = span1 + trouve.mot + span2;
            res += " : " + trouve.POS + " - " + trouve.type;
        }
        if (res.isEmpty()) {
            res = span1 + trouve.mot + span2;
            if (!trouve.POS.isEmpty()) {
                res += " : " + trouve.POS;
            }

        }

        return res;
    }

    String enTeteHtml(EnregDico nomTrouve, boolean isBouton) {

        String textF = "";

        textF = "<p  class=\"sectionEnTeteMot\"; style=\"margin-top:10px\">";
        textF += "<LEMOT></BR></BR></p>";

        textF += "<CONJDEF><section id=\"defX\">";

        if (isBouton) {
            textF += "<p  class=\"sectionDef\"; style=\"margin-top:10px\">";
        }
        else {

            textF += "<p  class=\"sectionDef\">";
        }

        //textF += "<p  class=\"sectionDef\"; style=\"margin-top:10px\">"
        textF += "<CUSTOMDEF><DEFINITION></p></section>";
        textF += "<section id=\"declX\">";

        String str = enteteMot(nomTrouve);

        if (nomTrouve.POS.equals("ADV")) {
            if (!nomTrouve.stem2.isEmpty()) {
                str += "</br> comparatif : <span class=\"terme\">" + nomTrouve.stem2 + "</span>";
                if (!nomTrouve.stem3.isEmpty()) {
                    str += " - superlatif : <span class=\"terme\">" + nomTrouve.stem3 + "</span>";
                }
            }
        }

        textF = textF.replace("<LEMOT>", str);
        str = nomTrouve.def;
        if (str.endsWith("<BR/><BR/>")) {
            str = str.substring(0,str.length()-5);
        }
        textF = textF.replace("<DEFINITION>", str);

        return textF;
    }

    String declineNomHtml(EnregDico nomTrouve, ArrayList desinences, float largeurFenetre, int taillePoliceBase) {

        String textF = "";

        String texte = "";

        AssetManager asmatt = cont.getAssets();
        InputStream input;
        try {
            input = asmatt.open("vues/declNom.html");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            texte = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        textF = texte;
        String str = "";
        /*
        String str = enteteMot(nomTrouve);
        textF = textF.replaceAll("</LEMOT>",str);
        str = nomTrouve.def;
        textF = textF.replaceAll("</DEFINITION>",str);
        */
        String listeCasHtml[] = {"nominatif","vocatif","accusatif","genitif","datif","ablatif" };
        String casT = "";
        int indice = 0;
        for (String cas : listeCasHtml) {
            casT = cas + "S";
            str = "&nbsp;" + desinences.get(indice) + "&nbsp;";

            textF = textF.replaceAll(casT,str);
            indice += 1;

        }
        for (String cas : listeCasHtml) {
            casT = cas + "P";
            str = "&nbsp;" + desinences.get(indice) + "&nbsp;";
            textF = textF.replaceAll(casT,str);
            indice += 1;
        }

        return textF;
    }

    String declineADJHtml(EnregDico nomTrouve,ArrayList desinences, float largeurFenetre, int taillePoliceBase) {

        String textF = "";

        String texte = "";

        AssetManager asmatt = cont.getAssets();
        InputStream input;
        try {
            input = asmatt.open("vues/declAdj.html");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            texte = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        textF = texte;
        String str = "";
        /*
        String str = enteteMot(nomTrouve);
        textF = textF.replace("</LEMOT>",str);
        str = nomTrouve.def;
        textF = textF.replaceAll("</DEFINITION>",str);
        */

        // str = yo[0]
        String listeCasHtml[] = {"nominatif","vocatif","accusatif","genitif","datif","ablatif" };
        String listeGenreNombre[] = {"MS","MP","FS","FP","NS","NP"};

        String casT = "";
        int indice = 0;

        for (String genrenombre : listeGenreNombre) {
            for (String cas : listeCasHtml) {
                casT = cas + genrenombre;
                str = "&nbsp;" + desinences.get(indice) + "&nbsp;";
                textF = textF.replaceAll(casT,str);
                indice += 1;
            }
        }

        if (!desinences.get(indice).equals("XXX")) {

            try {
                input = asmatt.open("vues/declAdjCOMP.html");
                int size = input.available();
                byte[] buffer = new byte[size];
                input.read(buffer);
                input.close();

                texte = new String(buffer);

            } catch (IOException e) {
                e.printStackTrace();
            }

            textF += texte;
            for (String genrenombre : listeGenreNombre) {
                for (String cas : listeCasHtml) {
                    casT = cas + genrenombre + "-COMP";
                    str = "&nbsp;" + desinences.get(indice) + "&nbsp;";
                    textF = textF.replaceAll(casT,str);
                    indice += 1;
                }
            }

            if (!desinences.get(indice).equals("XXX")) {

                try {
                    input = asmatt.open("vues/declAdjSUPER.html");
                    int size = input.available();
                    byte[] buffer = new byte[size];
                    input.read(buffer);
                    input.close();

                    texte = new String(buffer);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                textF += texte;
                for (String genrenombre : listeGenreNombre) {
                    for (String cas : listeCasHtml) {
                        casT = cas + genrenombre + "-SUP";
                        str = "&nbsp;" + desinences.get(indice) + "&nbsp;";
                        textF = textF.replaceAll(casT, str);
                        indice += 1;
                    }
                }
            }

            if (nomTrouve.type.equals("COMP")) {
                textF = textF.replaceAll("COMPARATIF</td>", "SUPERLATIF</td>");
                textF = textF.replaceAll( "POSITIF</td>", "COMPARATIF</td>");
            }
            if (nomTrouve.type.equals("SUPER")) {
                textF = textF.replaceAll( "POSITIF</td>", "</td>");
            }
        }


        textF += " </tbody> </table>";
        textF += "</section>";


        //chargeHtml(textF)
        return textF;
    }
/*
    String enTeteVerbeHtml(EnregDico nomTrouve, Float largeurFenetre,int taillePoliceBase) {

        String textF = "";
        textF = "<p  class=\"sectionEnTeteMot\"; style=\"margin-top:10px\">";
        textF += "</LEMOT></BR></BR></p>";

        textF += "</CONJDEF><section id=\"defX\">";
        textF += "<p  class=\"sectionDef\"; style=\"margin-top:10px\">";
        textF += "</DEFINITION></p></section>";
        textF += "<section id=\"declX\">";

        String str = enteteMot(nomTrouve);
        textF = textF.replaceAll("</LEMOT>", str);
        str = nomTrouve.def;
        textF = textF.replaceAll("</DEFINITION>", str);

        return textF;
    }
*/
    String declineVerbeHtml(EnregDico nomTrouve,String voix, ArrayList desinences, float largeurFenetre, int taillePoliceBase) {

        String textF = "";

        String texte = "";
        if (desinences.size() == 0) {
            return "";
        }

        AssetManager asmatt = cont.getAssets();
        InputStream input;
        try {
            input = asmatt.open("vues/declVerb.html");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            texte = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        textF = texte;


        String listeTempsHtml[] = {"present","futur","imparfait","parfait","plusqueparfait","futurpasse"};
        String remp = "";
        int indiceTemps = 0;
        String str = "";
        // ============INDICATIF===========
        for (String temps : listeTempsHtml) {
            for (int i = 0; i < 3 ; i++) {
                str =  "&nbsp;" + desinences.get(indiceTemps + i) + "&nbsp;";
                remp = "indicatif" + temps + (i+1) + "S";
                textF = textF.replace(remp, str);
            }
            for (int i = 0; i < 3 ; i++) {
                str =  "&nbsp;" + desinences.get(indiceTemps + 3 + i) + "&nbsp;";
                remp = "indicatif" + temps + (i+1) + "P";
                textF = textF.replace(remp, str);
            }
            indiceTemps += 6;
        }

        // ============SUBJONCTIF===========
        String[] listeTempsHtmlSubj = {"present","imparfait","parfait","plusqueparfait"};
        for (String temps : listeTempsHtmlSubj) {
            for (int i = 0; i < 3 ; i++) {
                str =  "&nbsp;" + desinences.get(indiceTemps + i) + "&nbsp;";
                remp = "subjonctif" + temps + (i+1) + "S";
                textF = textF.replace(remp, str);
            }
            for (int i = 0; i < 3 ; i++) {
                str =  "&nbsp;" + desinences.get(indiceTemps + 3 + i) + "&nbsp;";
                remp = "subjonctif" + temps + (i+1) + "P";
                textF = textF.replace(remp, str);
            }
            indiceTemps += 6;
        }

        // ============IMPERATIF===========
        String listeImperHtml[] = {"imperatifpresent2S","imperatifpresent2P","imperatiffutur2S","imperatiffutur3S","imperatiffutur2P","imperatiffutur3P"};
        for (int i = 0 ; i < 6 ; i++) {
            //indiceTemps += i
            str =  "&nbsp;" + desinences.get(indiceTemps + i) + "&nbsp;";
            remp = listeImperHtml[i];
            textF = textF.replace(remp, str);
        }
        indiceTemps += 6;

        // ============ INFINITIF ===========
        str =  "&nbsp;" + desinences.get(indiceTemps) + "&nbsp;";
        indiceTemps += 1;
        remp = "infinitifpresent";
        textF = textF.replace(remp, str);

        str =  "&nbsp;" + desinences.get(indiceTemps) + "&nbsp;";
        indiceTemps += 1;
        remp = "infinitifparfait";
        textF = textF.replace(remp, str);

        // ============ PARTICIPE ===========
        if (voix.equals("ACTIVE") && (!nomTrouve.type.equals("DEP"))) {
            str =  "&nbsp;" + desinences.get(indiceTemps) + "&nbsp;";
            indiceTemps += 1;
            remp = "participe1";
            textF = textF.replace(remp, str);
            textF = textF.replace("ppeprésent", "présent");
            textF = textF.replace("ppefutur", "futur");

            remp = "participe2";
            str =  "&nbsp;" + desinences.get(indiceTemps) + "&nbsp;";
            indiceTemps += 1;
            textF = textF.replace(remp, str);

        }
        else {
            str =  "&nbsp;" + desinences.get(indiceTemps) + "&nbsp;";
            indiceTemps += 1;
            remp = "participe1";
            textF = textF.replace(remp, str);
            textF = textF.replace("ppeprésent", "passé");

            remp = "participe2";
            str =  "&nbsp;" + desinences.get(indiceTemps) + "&nbsp;";
            textF = textF.replace(remp, str);
            textF = textF.replace("ppefutur", "futur");
        }

        return textF;
    }

    String declineVPARHtml(EnregDico nomTrouve,ArrayList desinences, float largeurFenetre, int taillePoliceBase) {

        String textF = "";

        String texte = "";

        AssetManager asmatt = cont.getAssets();
        InputStream input;
        try {
            input = asmatt.open("vues/declVPAR.html");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            texte = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        textF = texte;
        String str = "";
        /*
        String str = enteteMot(nomTrouve);
        textF = textF.replace("</LEMOT>",str);
        str = nomTrouve.def;
        textF = textF.replaceAll("</DEFINITION>",str);
         */

        // str = yo[0]
        String listeCasHtml[] = {"nominatif","vocatif","accusatif","genitif","datif","ablatif" };
        String listeGenreNombre[] = {"MS","MP","FS","FP","NS","NP"};

        String casT = "";
        int indice = 0;

        for (String genrenombre : listeGenreNombre) {
            for (String cas : listeCasHtml) {
                casT = cas + genrenombre;
                str = "&nbsp;" + desinences.get(indice) + "&nbsp;";
                textF = textF.replaceAll(casT,str);
                indice += 1;
            }
        }


        return textF;
    }

    String declineNUMHtml(EnregDico nomTrouve,ArrayList desinences, float largeurFenetre, int taillePoliceBase) {

        if (nomTrouve.decl1 == 2 && (nomTrouve.type.equals("X") || nomTrouve.type.equals("CARD"))) { // invariable
            return "";
        }

        String textF = "";
        String texte = "";

        AssetManager asmatt = cont.getAssets();
        InputStream input;

        try {
            input = asmatt.open("vues/declNum.html");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            texte = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        textF = texte;
        String str ="";
        /*
        String str = enteteMot(nomTrouve);
        textF = textF.replace("</LEMOT>",str);
        str = nomTrouve.def;
        textF = textF.replaceAll("</DEFINITION>",str);
        */
        // str = yo[0]
        String listeCasHtml[] = {"nominatif","vocatif","accusatif","genitif","datif","ablatif" };
        String listeGenreNombre[] = {"MS","MP","FS","FP","NS","NP"};

        int indice = 0;
        String casT = "";

        for (String genrenombre : listeGenreNombre) {
            for (String cas : listeCasHtml) {
                casT = cas + genrenombre;
                str = "&nbsp;" + desinences.get(indice) + "&nbsp;";
                textF = textF.replaceAll(casT,str);
                indice += 1;

            }

        }
        return textF;

    }

    String declinePRONHtml(EnregDico nomTrouve, ArrayList desinences, float largeurFenetre, int taillePoliceBase) {

        String textF = "";

        String texte = "";

        AssetManager asmatt = cont.getAssets();
        InputStream input;

        if (nomTrouve.decl1 == 5) {
            try {
                input = asmatt.open("vues/declNom.html");
                int size = input.available();
                byte[] buffer = new byte[size];
                input.read(buffer);
                input.close();

                texte = new String(buffer);

            } catch (IOException e) {
                e.printStackTrace();
            }

            textF = texte;
            String str ="";
            /*
            String str = enteteMot(nomTrouve);
            textF = textF.replace("</LEMOT>",str);
            str = nomTrouve.def;
            textF = textF.replaceAll("</DEFINITION>",str);
            */
            // str = yo[0]
            String listeCasHtml[] = {"nominatif","vocatif","accusatif","genitif","datif","ablatif" };
            String listeGenreNombre[] = {"S","P","S","P","S","P"};

            int indice = 0;
            String casT = "";

            for (String genrenombre : listeGenreNombre) {
                for (String cas : listeCasHtml) {
                    casT = cas + genrenombre;
                    str = "&nbsp;" + desinences.get(indice) + "&nbsp;";
                    textF = textF.replaceAll(casT,str);
                    indice += 1;
                }
            }
        }
        else {
            try {
                input = asmatt.open("vues/declPronom.html");
                int size = input.available();
                byte[] buffer = new byte[size];
                input.read(buffer);
                input.close();

                texte = new String(buffer);

            } catch (IOException e) {
                e.printStackTrace();
            }

            textF = texte;

            String str = enteteMot(nomTrouve);
            textF = textF.replace("</LEMOT>",str);

            str = nomTrouve.def;
            textF = textF.replaceAll("</DEFINITION>",str);

            // str = yo[0]
            String listeCasHtml[] = {"nominatif","vocatif","accusatif","genitif","datif","ablatif" };
            String listeGenreNombre[] = {"MS","MP","FS","FP","NS","NP"};

            int indice = 0;
            String casT = "";

            for (String genrenombre : listeGenreNombre) {
                for (String cas : listeCasHtml) {
                    casT = cas + genrenombre;
                    str = "&nbsp;" + desinences.get(indice) + "&nbsp;";
                    textF = textF.replaceAll(casT,str);
                    indice += 1;
                }
            }
        }

        return textF;
    }
/*
    String declinePREPHtml(EnregDico nomTrouve, float largeurFenetre, int taillePoliceBase) {
        String textF = "";

        textF = " <p  class=\"sectionEnTeteMot\"; style=\"margin-top:10px\"> ";

        String str = enteteMot(nomTrouve);

        textF += str;
        textF += "</p>";
        textF += "<p class = \"sectionDef\">";
        str = nomTrouve.def;

        textF += str + "</p>";

        return textF;

    }
    String declineADVHtml(EnregDico nomTrouve, float largeurFenetre, int taillePoliceBase) {
        String textF = "";

        textF = " <p  class=\"sectionEnTeteMot\"; style=\"margin-top:10px\"> ";

        String str = enteteMot(nomTrouve);

        if (!nomTrouve.stem2.isEmpty()) {
            str += "</br> comparatif : <span class=\"terme\">" + nomTrouve.stem2 + "</span>";
            if (!nomTrouve.stem3.isEmpty()) {
                str += " - superlatif : <span class=\"terme\">" + nomTrouve.stem3 + "</span>";
            }
        }
        textF += str;
        textF += "</p>";
        textF += "<p class = \"sectionDef\">";
        str = nomTrouve.def;

        textF += str + "</p>";

        //str = nomTrouve.type;
        return textF;

    }
    */
    String declineInvariableHtml(EnregDico nomTrouve, float largeurFenetre, int taillePoliceBase) {
        String textF = "";

        textF = " <p  class=\"sectionEnTeteMot\"; style=\"margin-top:10px\"> ";

        String str = enteteMot(nomTrouve);

        textF += str;
        textF += "</p>";
        textF += "<p class = \"sectionDef\">";
        str = nomTrouve.def;

        textF += str + "</p>";

        return textF;
    }


    String getMeF_MotCourant() {
        return motCourant;
    }
    int getMeF_IndiceCourant() {
        return indiceCourant;
    }


    //=============================================== HTML =========================================================




// ===========================================================================================
// ============================== RECHERCHE ==================================================

    String rechercheHtml(String forme , ArrayList<ResultatPropre> resultats , float largeurFenetre ,int taillePoliceBase ) {
        String textF = "";

        //let resourcepath = NSBundle.mainBundle().pathForResource("resultats", ofType: "html")
        //if (resourcepath != nil) {
        StyleHtml style = new StyleHtml();
        textF = style.styleHtmlRecherche(largeurFenetre, taillePoliceBase);

        textF += "<body>";
        textF += scriptJQuery(); // !!! placer avant l'autre script

        String str = "";
        String txtMorceau = "";

        if (resultats.size() > 0) {
            int compteRes = 0;
            for (ResultatPropre res : resultats) {

                boolean montreBoutonNav = false;
                if ((res.mot.POS.equals("pronom") && res.mot.decl1 != 0) || (res.mot.POS.equals("adjectif") && res.mot.decl1 != 9) ||
                        (res.mot.POS.equals("nom") && res.mot.decl1 != 9) || res.mot.POS.equals("verbe")  || res.mot.POS.equals("participe") ||
                        (res.mot.POS.equals("numéral") && (res.mot.decl1 != 2 || res.mot.type.equals("distributif") || res.mot.type.equals("ordinal")))) {
                    montreBoutonNav = true;
                }
                else {
                    if (res.mot.POS.equals("adverbe") && !res.desinences.get(0).type.isEmpty()) {
                        montreBoutonNav = true;
                    }

                }

                txtMorceau = "<p  class=\"sectionEnTeteMot\"; style=\"margin-top:10px\">";

                String span1 = "<span class=\"motRecherche\">";
                String span2 = "</span>";

                txtMorceau += span1 + res.mot.mot + span2;
                if (!res.mot.POS.isEmpty()) {
                    txtMorceau += " : " + res.mot.POS;
                }
                if (!res.mot.genre.isEmpty()) {
                    txtMorceau += " " + res.mot.genre;
                }
                if (!res.mot.type.isEmpty()) {
                    txtMorceau += " " + res.mot.type;
                }

                txtMorceau += "</br></br></p>";

                if (montreBoutonNav) {

                    //=================
                    txtMorceau += "<div class = \"navig\">";
                    txtMorceau += "<ul>";  //<li class="active"
                    txtMorceau += "<li id = \"definition" + String.valueOf(compteRes) + "\" class = \"defanalyse1G\">";
                    txtMorceau += "<span class = \"boutondefanalyse\" id =\"boutonDef" + String.valueOf(compteRes) + "\">Définition</span></li><!--";

                    txtMorceau += "--><li id = \"analyse" + String.valueOf(compteRes) + "\" class = \"defanalyse2D\">";
                    txtMorceau += "<span class = \"boutondefanalyse\" id =\"boutonAnalyse" + String.valueOf(compteRes) + "\">Analyse</span></li> ";
                    txtMorceau += "</ul>";
                    txtMorceau += "</div>  ";

                    //==============

                    txtMorceau += "<section id=\"def" + String.valueOf(compteRes) + "\">";
                }


                txtMorceau += "<p  class=\"sectionDef\"; style=\"margin-top:10px\">";

                txtMorceau += res.mot.def;  //res.mot.mot.uppercased() + ", " +
                txtMorceau += "</p>";

                if (montreBoutonNav) {
                    txtMorceau += "</section>";
                    txtMorceau += "<section id=\"ana" + String.valueOf(compteRes) + "\">";

                    if (res.desinences.size() > 0) {
                        txtMorceau += "<p  class=\"formestrouvees\"; style=\"margin-top:10px\"> ";
                        txtMorceau += "</br><b>Formes possibles</b> :</br>";

                        int compte = 0;

                        for (Desinence des : res.desinences) {
                            compte += 1;
                            str = stringDesinence(res.mot, des);
                            txtMorceau += str + " " + "</BR>";
                        }

                        String scriptDefAnalyse = getTextScript("boutonDefAnalyse");
                        scriptDefAnalyse = scriptDefAnalyse.replaceAll("X", String.valueOf(compteRes));
                        txtMorceau += " </section>";
                        txtMorceau += "<script>" + scriptDefAnalyse + "</script>";
                    }
                    else {
                        //Log.d(Dictionnaire.TAG,"MiseEnForme rechercheHtml desinences vide ???");
                        txtMorceau += " </section>";
                    }



                }
                if (!montreBoutonNav) {
                    txtMorceau += " </p>";
                }

                textF += txtMorceau;
                compteRes += 1;
                if (compteRes < resultats.size()) {
                    textF += " <hr style=\"height:1px;border-width:0;background-color:gray;width:100%\">  ";
                }

                // textF += "</p>"
            }
        }

        else {
            textF += "<p  class=\"sectionDef\"; style=\"margin-top:10px\"> ";
            textF += "La recherche n'a donné aucun résultat </p>";
        }
        textF += "</body>";

        return textF;
    }


    String rechercheHtmlDsTexte(String forme , ArrayList<ResultatPropre> resultats , float largeurFenetre , int taillePoliceBase ) {
        String textF = "";

        StyleHtml style = new StyleHtml();
        textF = style.styleHtmlRechercheDsTexte(largeurFenetre, taillePoliceBase);
        textF += "<body>";
        String str = "";
        String txtMorceau = "";

        if (resultats.size() > 0) {
            int compteRes = 0;
            for (ResultatPropre res : resultats) {
                compteRes += 1;

                txtMorceau = "<p  class=\"sectionDef\"; style=\"margin-top:0px\">";
                txtMorceau += res.mot.def;  //res.mot.mot.uppercased() + ", " +

                boolean montreFormes = false;
                if ((res.mot.POS.equals("pronom") && res.mot.decl1 != 0) || (res.mot.POS.equals("adjectif") && res.mot.decl1 != 9) ||
                        (res.mot.POS.equals("nom") && res.mot.decl1 != 9) || res.mot.POS.equals("verbe")  || res.mot.POS.equals("participe") ||
                        (res.mot.POS.equals("numéral") && (res.mot.decl1 != 2 || res.mot.type.equals("distributif") || res.mot.type.equals("ordinal")))) {
                    montreFormes = true;
                }
                else {
                    if (res.mot.POS.equals("adverbe") && !res.desinences.get(0).type.isEmpty()) {
                        //txtMorceau += " </p> "
                        montreFormes = true;
                    }
                    // txtMorceau += " </p> "
                }
                if (res.desinences.size() == 0) {
                    montreFormes = false;
                }

                if (montreFormes) {
                    txtMorceau += "<span  class=\"formestrouvees\"; > ";
                    txtMorceau += "<b>Formes possibles</b> :</br>";

                    int compte = 0;

                    for (Desinence des : res.desinences) {
                        compte += 1;
                        str = stringDesinence(res.mot, des);
                        txtMorceau += str + " " + "</BR>";
                    }
                    txtMorceau += "</span> ";
                }

                txtMorceau += "</p>";


                // textF += "<p  class=\"sectionDef\"; style=\"margin-top:30px\"> Resultat "
                //  textF += "<p  >  " + txtMorceau + "</p>"
                textF +=  txtMorceau;
                if (compteRes < resultats.size()) {
                    textF += " <hr style=\"height:1px;border-width:0;background-color:gray;width:100%\">  ";
                }
            }
        }

        else {
            textF += "<p  class=\"sectionDef\"; style=\"margin-top:2px\"> ";
            textF += "La recherche n'a donné aucun résultat </p>";
        }
        textF += "</body>";
        return textF;
    }

    String stringDesinence(EnregDico mot, Desinence des) {
        String str = "";
        if (des.POS.equals("participe")) {
            str = "- participe " + des.temps + " " + des.voix + ", "; //+ "</br>"
            str +=  des.cas + " " + des.genre + " " + des.nombre; //+ " </BR>"
        }

        if (mot.POS.equals("verbe") && !des.POS.equals("participe")) {
            if (!(des.temps + des.mode).isEmpty()) {
                str = "- " + des.mode + " " + des.temps;
                str += " " + String.valueOf(des.personne) + " " + des.nombre;
                str += " (voix " + des.voix + ")"; //+ " </BR>"
            }

        }

        if (mot.POS.equals("adjectif")) {
            str = "- " + des.cas + " " + des.genre + " " + des.nombre + " " + des.type; //+ " </BR>"
        }

        if (mot.POS.equals("adverbe") &&  !des.type.isEmpty()) {
            str = "- " + des.type; //+ " </BR>"
        }


        if (mot.POS.equals("pronom")) {
            str = "- " + des.cas + " " + des.genre + " " + des.nombre; //+ " </BR>"
        }

        if (mot.POS.equals("numéral")) {
            if (mot.decl1 != 2 || mot.type.equals("distributif") || mot.type.equals("ordinal")) { //+ " </BR>"
                str = "- " + des.cas + " " + des.genre + " " + des.nombre; //+ " (" + des.type + ")"
            }
        }

        if (mot.POS.equals("nom")) {
            str = "- " + des.cas + " " + des.nombre; //+ " </BR>"
        }

        return str;
    }

    ArrayList analyseForme(String forme ) {
        ArrayList listeRes = flex.rechercheForme(forme,false,"");

        return trieListeResultats(listeRes);
    }


    /**
     fonction qui blablabla ...
     :param: liste
     :returns: ResultatPropre
     */
    ArrayList<ResultatPropre> trieListeResultats(ArrayList<ResultatRecherche> liste ) {
        ArrayList<ResultatPropre> listeFinale = new ArrayList<ResultatPropre>();

        ArrayList listemots = new ArrayList();

        // ici tester les variantes !!!!!!!!!!!!!
        for (ResultatRecherche resT : liste) {
            String prov = resT.mot.def;
            if (prov.length() > 60) {
                prov = prov.substring(prov.length() - 59,prov.length());
            }

            if (!listemots.contains(prov) && !resT.mot.mot.isEmpty() ) { //&& !resT.mot.refs.equals("x")
                listemots.add(prov);
                ResultatPropre yo = new ResultatPropre();
                yo.mot = resT.mot.metEnFormeMot();
                listeFinale.add(yo);
            }
        }

        ArrayList listeDes = new ArrayList();

        if (listeFinale.size() > 0) {
            for (int i = 0; i < listeFinale.size(); i++) {
                for (ResultatRecherche resT : liste) {
                    if (resT.mot.Num == listeFinale.get(i).mot.Num || (resT.mot.mot.equals(listeFinale.get(i).mot.mot) && resT.mot.refs.equals("X")  && resT.mot.POS.equals(listeFinale.get(i).mot.POS))) {
                        String prov = resT.mot.mot + resT.desinence.cas + resT.desinence.nombre + resT.desinence.personne + String.valueOf(resT.mot.Num);
                        prov += resT.desinence.temps + resT.desinence.voix + resT.desinence.mode  + resT.desinence.type;

                        if (!listeDes.contains(prov)) {
                            listeDes.add(prov);

                            if (resT.desinence.freq.equals("A") || resT.desinence.freq.equals("A1") || resT.desinence.freq.isEmpty()) {
                                Desinence despropre = resT.desinence.copie();
                                despropre.metEnFormeDesinence();
                                listeFinale.get(i).desinences.add(despropre);

                            }
                            if (resT.desinence.freq.equals("B") && resT.desinence.decl1 == 3 && resT.desinence.decl2 == 3) { // pour formes ACC im ??
                                Desinence despropre = resT.desinence.copie();
                                despropre.metEnFormeDesinence();
                                listeFinale.get(i).desinences.add(despropre);
                            }
                            if (resT.desinence.freq.equals("B") && resT.desinence.decl1 > 6) {
                                Desinence despropre = resT.desinence.copie();
                                despropre.metEnFormeDesinence();
                                listeFinale.get(i).desinences.add(despropre);
                            }
                        }
                        else {

                        }
                    }
                }
            }

        }
        //ArrayList<ResultatPropre> listeFinaleTriee = flex.trieResultatsPropres(listeFinale);
        return trieResultatsPropres(listeFinale);
    }

    String enteteDict(String nomDict) {

        StyleHtml style = new StyleHtml();
        return style.enteteDict(nomDict) + "</BR>"; //"</BR>" +
    }

    EnregDico resultatFTS2Enregico(ResultatFTS resultat) {
        EnregDico res = new EnregDico();
        res.mot = resultat.mot;
        res.def = resultat.def;
        res.POS = resultat.POS;
        return res;
    }

    ArrayList<ResultatPropre> trieResultatsPropres(ArrayList<ResultatPropre> liste) {
        ArrayList<ResultatPropre> res = new ArrayList<ResultatPropre>();

        ArrayList listeCommuns = new ArrayList();

        for (ResultatPropre enr : liste) {

            ResultatPropre enrT = new ResultatPropre();
            enrT.mot = enr.mot.copie();
            //enrT.desinences = enr.desinences;
            for (Desinence d : enr.desinences) {
                enrT.desinences.add(d);
            }
            String sign = enr.mot.signatureEnreg();
            int c = 0;
            if (!listeCommuns.contains(sign)) {
                listeCommuns.add(sign);
                enrT.mot.def = enteteDict(enr.mot.dico) + enr.mot.def;

                /*
                if (c < liste.count - 1) {
                    enrT.mot.def += "</BR>"
                }
                */
                enrT.mot.def += "</BR>";
                res.add(enrT);

            }

            int i = 0;
            while (i < res.size()) {

                if (res.get(i).mot.Num != enr.mot.Num) {

                    if (res.get(i).mot.signatureEnreg().equals(sign) && Desinence.egaliteListeDesinences(res.get(i).desinences, enr.desinences)) {
                        if (!enr.mot.dico.equals(res.get(i).mot.dico)) {
                            res.get(i).mot.def += enteteDict(enr.mot.dico) + enr.mot.def;
                            res.get(i).mot.dico = enr.mot.dico;
                            /*
                            if (enr.mot.dico.equals("P")) {
                                res.get(i).mot.def += enteteDict("Tabula") + enr.mot.def;
                                res.get(i).mot.dico = "P";
                            }
                            if (enr.mot.dico.equals("G")) {
                                res.get(i).mot.def += enteteDict("Gaffiot") + enr.mot.def;
                                res.get(i).mot.dico = "G";
                            }
                            */
                        }
                        else {
                            res.get(i).mot.def += enrT.mot.def;

                        }
                        /*
                         if (i < res.count - 1) {
                         res[i].mot.def += "</BR>"
                         }
                         */
                        res.get(i).mot.def += "</BR>";
                        break;
                    }
                    else {
                        //print("pas egal ? : sign : \(res[i].mot.signatureEnreg() == sign) - des \(egaliteListeDesinences(des1: res[i].desinences, des2: enr.desinences))")
                    }
                }

                i += 1;
            }
            c += 1;
        }
        return res;
    }

    static void afficheGrosLog(String grosLog, String tag) {
        int maxLogSize = 1000;
        for(int i = 0; i <= grosLog.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > grosLog.length() ? grosLog.length() : end;
            Log.v(tag, grosLog.substring(start, end));
        }
    }

}