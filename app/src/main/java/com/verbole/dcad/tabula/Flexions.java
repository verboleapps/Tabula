package com.verbole.dcad.tabula;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dcad on 3/7/16.
 */
public class Flexions {
    dbHelper db;
    Context cont;
    BD_Dictionnaires bdDics;
    BD_MesDictionnaires bdDicsInternes;

    public Flexions(Context context) {
        db = dbHelper.getInstance(context); //new dbHelper(context);
        /*
        try {
            db.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        */
        cont = context;

        bdDics = BD_Dictionnaires.getInstance(context); //new BD_Dictionnaires(context);
        bdDicsInternes = BD_MesDictionnaires.getInstance(context); //new BD_MesDictionnaires(context);
        ouvreDBS();
    }

    void fermeDBs() {
        bdDics.close();
        bdDicsInternes.close();
        db.close();
    }
    void ouvreDBS() {
        db.openDataBase();
        bdDics.openDataBase();
        bdDicsInternes.openDataBase();
    }

/*
    ArrayList<String[]> tableListeMots() {
        String mot;
        ArrayList<String[]> tablm = db.listeDesEntrees();
        return tablm;
    }
*/

    private String getOrdreDicts() {
        GestionSettings gs = new GestionSettings(cont);
        return gs.getOrdreDicts();
    }


    ArrayList<EnregDico> rechercheEntreesDsBase(String mot,String POS, String typeRecherche) {
        String ordreDics = getOrdreDicts();
        ArrayList<EnregDico> res = db.rechercheEntreesDsBase(mot,POS,typeRecherche,ordreDics);
        for (EnregDico enr : res) {
            /*
            if (enr.dico.equals("G") && ordreDics.contains("G")) {
                enr.def = bdDics.rechercheDefinition_pour_entree(enr,"mot");
            }
            if (!enr.dico.equals("G") && ordreDics.contains("P")) {
                enr.def = bdDicsInternes.rechercheDefinition_pour_entree(enr,"mot");
            }
            */

            if (enr.dico.equals("G")) {
                enr.def = bdDics.rechercheDefinition_pour_entree(enr,"mot");
            }
            else {
                enr.def = bdDicsInternes.rechercheDefinition_pour_entree(enr,"mot");
            }
            if (!ChangeDiphtongue.teste_AEOE(enr.motOrig)) {
                enr.changeDiphtongues();
            }


        }

        return trieEnregDict(res);
    }
    ArrayList<EnregDico> rechercheEntreesDsBaseAvecDefCensure(String mot,String POS, String typeRecherche) {
        String ordreDics = getOrdreDicts();
        ArrayList<EnregDico> res = db.rechercheEntreesDsBase(mot,POS,typeRecherche,ordreDics);
        for (EnregDico enr : res) {

            if (enr.dico.equals("G")) {
                enr.def = bdDics.rechercheDefinitionCensure_pour_enregDict(enr,"mot");
            }
            else {
                enr.def = bdDicsInternes.rechercheDefinitionCensure_pour_enregDict(enr,"mot");
            }

        }

        return trieEnregDict(res);
    }

    ArrayList<EnregDico> rechercheEntreesDsBasePourCarte(String mot, String pos, String typeRecherche ) {
        String ordreDics = getOrdreDicts();
        ArrayList<EnregDico> res = db.rechercheEntreesDsBase(mot,pos,typeRecherche,ordreDics);
        for (EnregDico enr : res) {
            /*
            if (ordreDics.contains("G")) {
                enr.def = bdDicsInternes.rechercheDefinition_pour_entree(enr,"mot");
                enr.def += "<BR><BR>" + bdDics.rechercheDefinition_pour_entree(enr,"mot");
            }
            else {
                enr.def = bdDicsInternes.rechercheDefinition_pour_entree(enr,"mot");
            }
            */
            if (enr.dico.equals("G")) {
                enr.def = bdDics.rechercheDefinition_pour_entree(enr,"mot");
            }
            else {
                enr.def = bdDicsInternes.rechercheDefinition_pour_entree(enr,"mot");
            }

            if (!ChangeDiphtongue.teste_AEOE(enr.motOrig)) {
                enr.changeDiphtongues();
            }
        }

        return trieEnregDict(res);
    }

    ArrayList<EnregDico> rechercheEntreesDsBasePourAjouterCartes(String mot, String pos, String typeRecherche) {
        String ordreDics = getOrdreDicts();
        ArrayList<EnregDico> res = db.rechercheEntreesDsBase(mot,pos,typeRecherche,ordreDics);
        /* ???
        for (EnregDico enr : res) {
            if (!ChangeDiphtongue.teste_AEOE(enr.motOrig)) {
                enr.changeDiphtongues();
            }
        }
        */
        return trieEnregDict(res);
    }

    private ArrayList<EnregDico> trieEnregDict2(ArrayList<EnregDico> liste) {

        ArrayList<EnregDico> res = new ArrayList<EnregDico>();

        for (EnregDico enr : liste) {
            enr.def = enteteDict(enr.dico) + enr.def;
            res.add(enr);
        }
        return res;
    }

    private ArrayList<EnregDico> trieEnregDict(ArrayList<EnregDico> liste) {

        ArrayList<EnregDico> res = new ArrayList<EnregDico>();

        List<String> listeCommuns = new ArrayList<String>();

        for (EnregDico enr : liste) {

            EnregDico enrT = enr.copie();

            String sign = enr.signatureEnreg();

            int c = 0;
            if (!listeCommuns.contains(sign)) {


                enrT.def = enteteDict(enr.dico) + enr.def;

                /*
                if (c < liste.count - 1) {
                    enrT.def += "</BR>"
                }
                */
                enrT.def += "</BR>";
                listeCommuns.add(sign);
                res.add(enrT);
            }
            else {
                int i = 0;
                while (i < res.size()) {

                    if (res.get(i).signatureEnreg().equals(sign)) {
                        if (!enr.dico.equals(res.get(i).dico)) {

                            res.get(i).def += enteteDict(enr.dico) + enr.def; //+ "</BR>"
                            res.get(i).dico = enr.dico;


                            res.get(i).def += "</BR>";

                        }
                        else {
                            res.get(i).def += enrT.def + "</BR>";


                        }
                        /*
                        if (i < res.count - 1) {
                            res[i].def += "</BR>"
                        }
                        */
                        break;
                    }
                    i += 1;
                }
            }
            c += 1;
        }
        return res;
    }

    private String enteteDict(String nomDict) {
        StyleHtml style = new StyleHtml();
        return style.enteteDict(nomDict) + "</BR>"; //"</BR>" +
    }

    //=========================== DECLINAISONS -================================================
    ArrayList declineMotGeneral(EnregDico motTrouve, String voix) {

      //  db.preparePourQuery();

        ArrayList flex1 = declineMot(motTrouve, voix);

        if (!motTrouve.refs.isEmpty()) {

            ArrayList flexbis = rechercheVariantesFlexionsMot(motTrouve, voix); //requetesMultiples
            ArrayList<String> listeDes = new ArrayList<String>();
            ArrayList listeDesTemp = new ArrayList();
            ArrayList<String> listeComparaison = new ArrayList<String>(); // compare en enlevant les span

            if (flexbis.size() > 0) {
                for (int i = 0; i < flex1.size(); i++) {
                    listeDesTemp.clear();
                    listeComparaison.clear();
                    String desComp = (String) flex1.get(i);
                    listeDesTemp.add(flex1.get(i));
                    listeComparaison.add(desComp);
                    ArrayList<String> temp = new ArrayList<String>();

                    for (int j = 0; j < flexbis.size(); j++) {
                        temp = (ArrayList) flexbis.get(j);
                        if (!temp.isEmpty()) {
                            desComp = (String) temp.get(i);

                            if (!listeComparaison.contains(desComp) && !desComp.isEmpty() && !desComp.equals("X")) {

                                listeComparaison.add(temp.get(i));
                                listeDesTemp.add(temp.get(i));
                            }
                        }
                    }
                    StringBuilder lades = new StringBuilder("");
                    for (int k = 0 ; k < listeDesTemp.size() ; k++) {
                        String stringTemp = (String) listeDesTemp.get(k);
                        lades.append(stringTemp);
                        if (k != listeDesTemp.size() - 1 && !stringTemp.isEmpty()) {
                            lades.append(",</br>");
                        }
                    }
                    listeDes.add(lades.toString());
                }
                return listeDes;
            }

/*
            if (flexbis.size() > 0) {
                ArrayList<String> temp = (ArrayList<String>) flexbis.get(0);
                if (temp.size() > 0) {
                    if (!temp.get(0).equals("X")) {
                        for (int i = 0; i < flex1.size(); i++) {
                            listeDesTemp.clear();
                            listeComparaison.clear();

                            listeDesTemp.add(flex1.get(i));

                            String desComp = (String) flex1.get(i);
                            listeComparaison.add(desComp);

                            for (int j = 0; j < flexbis.size(); j++) {
                                temp = (ArrayList) flexbis.get(j);
                                if (!temp.isEmpty()) {
                                    desComp = (String) temp.get(i);
                                    Log.d("Flexions244"," flex1 -" + desComp + "- descomp : -" + (String) temp.get(i) + "-");
                                    if (!listeComparaison.contains(desComp) && !desComp.isEmpty()) {
                                        Log.d("Flexions244"," ajoute : -" + (String) temp.get(i) + "-");
                                        listeComparaison.add(temp.get(i));
                                        listeDesTemp.add(temp.get(i));
                                    }
                                    else {
                                        //desComp = (String) temp.get(i);
                                        //listeDesTemp.set(0,desComp);
                                    }
                                }
                            }
                            StringBuilder lades = new StringBuilder("");
                            for (int k = 0 ; k < listeDesTemp.size() ; k++) {
                                String stringTemp = (String) listeDesTemp.get(k);
                                lades.append(stringTemp);
                                if (k != listeDesTemp.size() - 1 && !stringTemp.isEmpty()) {
                                    lades.append(",</br>");
                                }
                            }
                            listeDes.add(lades.toString());
                        }
                        return listeDes;
                    }
                }

            } // fin if (flexbis.count > 0)
        */
        }

        return flex1;
    }

    private ArrayList<String> declineMot(EnregDico motTrouve, String voix ) {

        // if (!requetesMultiples) { db.openDataBase(); db.preparePourQuery();}
        String POS = motTrouve.POS;
        ArrayList<String> res = new ArrayList<String>();

        if (POS.equals("N")|| POS.equals("NP") || POS.equals("NPP")) {
            res = rechercheFlexionsNom( motTrouve);
        }
        if (POS.equals("ADJ") || POS.equals("ADJP") || POS.equals("VPAR_ADJ")) {
            res = rechercheFlexionsADJ(motTrouve);
        }

        if (POS.equals("PRON")) {
            res = rechercheFlexionsPRON(motTrouve);
        }

        if (POS.equals("VPAR")) {
            res = rechercheFlexionsPARTsimple( motTrouve, false);
        }

        if (POS.equals("NUM")) {
            res = rechercheFlexionsNUM(motTrouve);
        }

        if (POS.equals("V")) {
            res = rechercheFlexionsVerbe( motTrouve,  voix);
        }


        if (POS.equals("ADV")|| POS.equals("ADVP")) {
            res = new ArrayList<String>();
        }
        if (POS.equals("INV")) {
            res = new ArrayList<String>();
        }

        if (POS.equals("PREP")) {
            res = new ArrayList<String>();
        }


        //if (!requetesMultiples) {db.fermeDataBase();}

        return res;
    }


    private ArrayList<String> rechercheVariantesFlexionsMot(EnregDico motDico , String voix) {
        ArrayList<String> res = new ArrayList<String>();
        String resVide = "X";
        if (motDico.refs.equals("") || motDico.refs.equals("X")) {
            return res;
        }
        ArrayList resultats = new ArrayList();

        String[] references = motDico.refs.split(",");
        //let references = motDico.refs.components(separatedBy: ",")

        for (String ref : references) {
            int refN = Integer.valueOf(ref);
            //Log.d("rechvar flex mot",String.valueOf(requetesMultiples));
            EnregDico motTemp = db.rechercheEntreeDicoParRefVar(refN, motDico.POS);
            if (!ChangeDiphtongue.teste_AEOE(motTemp.motOrig)) {
                motTemp.changeDiphtongues();
            }

            ArrayList<String> tabflex = declineMot(motTemp, voix);
            resultats.add(tabflex);
        }

        return resultats;
    }

    ArrayList<String> rechercheFlexionsNom(EnregDico motDico) {

        ArrayList<String> res = new ArrayList<String>();
        String listeCas[] = {"NOM","VOC","ACC","GEN","DAT","ABL"};
        String txt = "";

        if (motDico.mot.equals("respublica")) {

            ArrayList respu1 = db.rechercheLemmeSpecial("res","N");
            int ind = (int) respu1.get(0);
            EnregDico re = db.rechercheEntreeDico(ind);

            ArrayList respu2 = db.rechercheLemmeSpecial("publicus","ADJ");
            EnregDico pu = new EnregDico();
            for (int i = 0; i < respu2.size(); i++) {
                int esst = (int) respu2.get(i);
                EnregDico publ = db.rechercheEntreeDico(esst);
                if (!publ.mot.isEmpty()) {pu = publ;}
            }
            /*
            String rechQuery = "SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, personne, nombre, numrad, term, age, freq FROM INFLEXION WHERE ";
            rechQuery += "( decl1 = " + String.valueOf(motDico.decl1) + " OR decl1 = 0) AND decl2 <= " + String.valueOf(motDico.decl2) + " AND pos = 'ADJ' ORDER BY decl2 ";

            ArrayList<Desinence> listeDes = db.effectueRechercheDesinences(rechQuery,false);
            */
            ArrayList<Desinence> listeDes = rechercheDesinencesNOM(motDico);


            for (String cas : listeCas) {
                txt = rechercheFlexionNom(re, cas, "S", false) + rechercheFlexionADJ(pu, listeDes, cas, "S", "POS", "F",false);
                res.add(txt);
            }
            for (String cas : listeCas) {
                txt = rechercheFlexionNom(re, cas, "P", false) + rechercheFlexionADJ(pu, listeDes, cas, "P", "POS", "F",false);
                res.add(txt);
            }
            return res;
        }

        if (motDico.mot.equals("jusjurandum")) {
            ArrayList jusN = db.rechercheLemmeSpecial("jus","N");
            ArrayList randumN = db.rechercheLemmeSpecial("jurandum","N");
            int esst = (int) jusN.get(0);
            EnregDico jus = db.rechercheEntreeDico(esst);
            esst = (int) randumN.get(0);
            EnregDico randum = db.rechercheEntreeDico(esst);
            for (String cas : listeCas) {
                txt = rechercheFlexionNom(jus, cas, "S", false) + rechercheFlexionNom(randum, cas, "S",false);
                res.add(txt);
            }
            for (String cas : listeCas) {
                txt = rechercheFlexionNom(jus, cas, "P", false) + rechercheFlexionNom(randum, cas, "P",false);
                res.add(txt);
            }
            return res;
        }
        for (String cas : listeCas) {
            txt = rechercheFlexionNom(motDico, cas, "S", false);
            res.add(txt);
        }

        for (String cas : listeCas) {
            txt = rechercheFlexionNom(motDico, cas, "P", false);

            if (motDico.mot.equals("locus")) {
                if (cas.equals("NOM") || cas.equals("VOC") || cas.equals("ACC")) {
                    EnregDico m2 = motDico.copie();
                    m2.genre = "N";
                    m2.decl2 = 2;
                    txt += ",</BR>" + rechercheFlexionNom(m2, cas, "P",false);
                }
            }

            res.add(txt);
        }


        return res;
    }

    private String rechercheFlexionNom(EnregDico motDico,String cas, String nombre, Boolean ToutesFormes) {
        String res = "";
        EnregDico motDicoT = motDico.copie();

        if (motDico.decl1 == 9) {return motDico.mot;}

        //=============Exceptions =================================

        if (motDico.mot.equals("mille")) {
            if (nombre.equals("S")) {
                return metLesSpan("mille", "");
            }
        }

        if (motDico.mot.equals("vesper")) {
            if (nombre.equals("P") && motDico.decl1 == 2) {
                return ""; // apparemment formes 3 1 seulement au plur.
            }
        }

        if (motDico.mot.equals("domus")) {
            String exc = declineDomus(cas, nombre);
            if (!exc.equals("")) {return exc;}
            if (motDico.decl1 == 2) { // mis seulement pour la recherche ??
                return "";
            }
        }

        if (motDico.mot.equals("bos")) {
            String exc = declineBos(cas, nombre);
            if (!exc.equals("")) {return exc;}
        }

        if (motDico.mot.equals("vis")) {
            String exc = declineVis(cas, nombre);
            if (!exc.equals("")) {return exc;}
        }


        if (motDico.mot.equals("Juppiter")) {
            String exc = declineJuppiter(cas, nombre);
            if (!exc.isEmpty()) {return exc;}
        }
        if (motDico.mot.equals("Jupiter")) {
            String exc = declineJupiter(cas, nombre);
            if (!exc.isEmpty()) {return exc;}
        }

        if (motDico.mot.equals("Jesus")) {return declineJesus("Jes", cas, nombre);}
        if (motDico.mot.equals("Iesus")) {return declineJesus("Ies", cas, nombre);}

        if (motDico.mot.equals("genu")) {
            if (nombre.equals("S")) {
                return metLesSpan("genu","");
            }
        }
        if (motDico.mot.equals("seps")) {
            if (nombre.equals("S") && cas.equals("ACC")) {
                return metLesSpan("sep", "a");
            }
        }

        if (motDico.mot.equals("fors")) {
            if (nombre.equals("P")) {
                return metLesSpan("", "");
            }
        }
        if (motDico.mot.equals("chaos")) {
            if (nombre.equals("P")) {
                return metLesSpan("", "");
            }
        }
        if (motDico.mot.equals("Hypatius")) {
            if (nombre.equals("P")) {
                return metLesSpan("", "");
            }
        }
        if (motDico.mot.equals("echo")) {
            if (nombre.equals("S")) {
                if (cas.equals("ACC")) {
                    return metLesSpan("ech", "on"); // ou : motDicoT.decl2 = 6
                }
            }
            if (nombre.equals("P")) {
                return metLesSpan("", ""); // ds Perseus trouve aucune forme en -is, -ibus, os, etc...
            }
        }
        if (motDico.mot.equals("Pan")) {
            if (nombre.equals("P") && cas.equals("ACC")) {
                return metLesSpan("Pan", "as");
            }
        }
        if (motDico.mot.equals("pelagus") || motDico.mot.equals("virus") || motDico.mot.equals("vulgus") || motDico.mot.equals("volgus")) {
            if (nombre.equals("P")) {
                return metLesSpan("", "");
            }
        }
        if (motDico.mot.equals("dogarius")) {
            if (nombre.equals("P")) {
                return metLesSpan("", "");
            }
        }
        if (motDico.mot.equals("prytaneum")) {
            if (nombre.equals("P")) {
                return metLesSpan("", "");
            }
        }
        if (motDico.mot.equals("aer") || motDico.mot.equals("ær")) {
            if (nombre.equals("S") && cas.equals("ACC")) {
                motDicoT.decl2 = 6;
            }
        }
        if (motDico.mot.equals("cachry")) {
            if (nombre.equals("S")) {
                if (cas.equals("GEN")) {
                    return metLesSpan("cachry", "os");
                }
                if (cas.equals("ACC")) {
                    return metLesSpan("cachry", "m");
                }
                if (cas.equals("ABL") || cas.equals("DAT")) { // ????
                    return metLesSpan("cachry", "");
                }
            }
            if (nombre.equals("P")) {
                return metLesSpan("", ""); // pas trouve de formes ds Perseus
            }
        }

        if (motDico.mot.equals("dorx")) {
            if (nombre.equals("P")) {
                if (cas.equals("ACC")) {
                    return metLesSpan( "dorc", "as");
                }
            }
        }

        if (motDico.mot.equals("Tegestraei") || motDico.mot.equals("Tegestræi")) {
            if (nombre.equals("P") && cas.equals("GEN")) {
                return metLesSpan("Tegestræ", "on");
            }
        }
        if (motDico.mot.equals("mula")) {
            if (nombre.equals("P")) {
                if (cas.equals("DAT") || cas.equals("ABL")) {
                    return metLesSpan( "mul", "abus");
                }
            }
        }


        //============ pluriel noms propres grecs debut=============
        boolean pluriel = true;
        if (motDico.decl2 > 5 && !motDico.POS.equals("NPP")) { // mots d'origine grecque ?? -> generaliser ??
            pluriel = false;
        }
        if (motDico.decl1 == 3) {
            if (!motDico.mot.endsWith("es") && !motDico.mot.endsWith("is") && !motDico.POS.equals("NPP")) {
                pluriel = false;
            }
        }
        // 5e decl
        if (motDico.decl1 == 5) {
            pluriel = false;
        }

        if (motDico.mot.equals("Ibis")) {
            pluriel = false;
        }
        if (motDico.mot.equals("Pan")) {
            pluriel = true; // a un pluriel
            if (nombre.equals("S")) {
                if (cas.equals("ACC")) {
                    return metLesSpan("Pan", "a");
                }
            }
            if (nombre.equals("P")) {
                motDicoT.decl2 = 1;
                if (cas.equals("ACC")) {
                    return metLesSpan("Pan", "as");
                }
            }

        }
        if (motDico.mot.equals("Titan")) {
            pluriel = true;
            if (nombre.equals("S")) {
                if (cas.equals("ACC")) {
                    return metLesSpan("Titan", "a"); // ou : motDicoT.decl2 = 6
                }
            }
            if (nombre.equals("P")) {
                motDicoT.decl2 = 1;
                if (cas.equals("ACC")) {
                    return metLesSpan("Titan", "as"); // ou : motDicoT.decl2 = 6 mais Freq : A1
                }
            }
        }

        if (motDico.mot.equals("Titanes")) {
            if (nombre.equals("P")) {
                if (cas.equals("ACC")) {
                    return metLesSpan("Titan", "as");
                }
            }
        }

        if (motDico.mot.equals("halteres")) {
            if (nombre.equals("P")) {
                if (cas.equals("ACC")) {
                    return metLesSpan("halter", "as");
                }
            }
        }
        if (motDico.mot.equals("nebris")) {
            if (nombre.equals("S")) {
                if (cas.equals("ACC")) {
                    return metLesSpan(motDico.stem2, "a");
                }
            }
            if (nombre.equals("P")) {
                if (cas.equals("ACC")) {
                    return metLesSpan(motDico.stem2, "as");
                }
            }
        }
        if (motDico.mot.equals("syrinx")) {
            if (nombre.equals("S")) {
                if (cas.equals("ACC")) {
                    return metLesSpan(motDico.stem2, "a");
                }
            }
        }
        if (motDico.mot.equals("cosmoe") || motDico.mot.equals("cosmœ")) { // pluriel de cosmos
            if (nombre.equals("P")) {
                if (cas.equals("NOM") ||cas.equals("VOC") ) {
                    return metLesSpan("cosm", "œ");
                }
            }
        }
        // 2 7
        if (motDico.mot.equals("Erinnys")) {
            if (nombre.equals("P")) {
                if (cas.equals("NOM") || cas.equals("VOC")) {
                    return metLesSpan("Erinny", "es");
                }
                else {
                    return metLesSpan("", ""); // ????
                }
            }
        }
        if (motDico.mot.equals("Capys")) {
            if (nombre.equals("S")) {
                if (cas.equals("ABL") || cas.equals("DAT")) {
                    return metLesSpan("capy", "e");
                }
                if (cas.equals("ACC")) {
                    // return metLesSpan(stem: "capy", terminaison: "n") // inutile ???
                }
            }
        }
        if (motDico.mot.equals("capys")) {
            if (nombre.equals("S")) {
                if (cas.equals("ABL") || cas.equals("DAT")) {
                    return metLesSpan("capy", "e");
                }
            }
            if (nombre.equals("P")) {
                if (cas.equals("ACC")) {
                    return metLesSpan("capy", "as");
                }
            }
        }
        if (motDico.decl1 == 2 && motDico.decl2 == 7) {
            if (nombre.equals("P")) {
                return metLesSpan("", "");
                // impossible de trouver des infos sur ces formes, si elles existent
            }
        }

        if (motDico.mot.equals("Siren")) {
            pluriel = true;// a un pluriel
        }

        if (motDico.mot.equals("apodixis") || motDico.mot.equals("crisis") || motDico.mot.equals("poesis") || motDico.mot.equals("trochis")) {
            if (nombre.equals("S") && cas.equals("ACC")) {
                return metLesSpan(motDico.stem2, "in");
            }
        }
        if (motDico.mot.equals("andronitis")) { // car stem2 en -id ...
            if (nombre.equals("S") && cas.equals("ACC")) {
                return metLesSpan("andronit", "in");
            }
        }
        if (motDico.mot.equals("drepanis")) { // car stem2 en -id ...
            if (nombre.equals("S") && cas.equals("ACC")) {
                return metLesSpan("drepan", "in");
            }
        }
        if (motDico.mot.equals("Aornis") || motDico.mot.equals("Atargatis")) {
            if (nombre.equals("S") && cas.equals("ACC")) {
                return metLesSpan(motDico.stem2, "in");
            }
        }
        if (motDico.mot.equals("Themis")) { // car stem2 en -id ...
            if (nombre.equals("S") && cas.equals("ACC")) {
                return metLesSpan("Them", "in");
            }
        }
        if (motDico.mot.equals("iris") && motDico.motOrig.contains("4")) { // 4 entrees ...
            if (nombre.equals("S") && cas.equals("ACC")) {
                return metLesSpan("ir", "im");
            }
        }
        if (motDico.mot.equals("balis") || motDico.mot.equals("sideritis") || motDico.mot.equals("leucographitis") ||
                motDico.mot.equals("marmaritis") || motDico.mot.equals("Zizais") || motDico.mot.equals("Naucratis")) {
            if (nombre.equals("S") && cas.equals("ACC")) {
                return metLesSpan(motDico.stem2, "im");
            }
        }

        if (motDico.mot.equals("vas") && motDico.genre.equals("N")) { // 2 entrees vas
            if (nombre.equals("P")) {
                motDicoT.decl1 = 2;
                motDicoT.decl2 = 2;
            }
        }

        if (motDico.POS.equals("NP")) {
            pluriel = false;
        }
        if (motDico.POS.equals("NPP")) {
            pluriel = true;
        }
        if (motDico.decl1 == 2 && motDico.decl2 == 1 && motDico.genre.equals("N")) {
            pluriel = false;
        }

        String l1 = motDico.mot.substring(0,1);
        //let l1 = recupereNpremieresLettresDuMot(mot: motDico.mot, nLettres: 1)
        if (motDico.nombre.equals("S") && (l1.equals(l1.toUpperCase())) && nombre.equals("P")) {
            if (!pluriel) { // autres ??
                return metLesSpan("", "");
            }

        }
        //============ pluriel noms grecs fin=============

        if (motDico.decl1 == 3 && motDico.decl2 == 2 && nombre.equals("P")) {
            if (motDico.mot.endsWith("a") && !motDico.nombre.equals("P")) {
                if (cas.equals("DAT") || cas.equals("ABL")) {
                    return metLesSpan(motDico.stem2, "is"); // ex. poema
                    //mais Gramm Fr dit : aussi formes -ibus (3e decl)
                    // et GEN : formes orum (2e decl) et um (3e decl)
                }
            }

        }

        // 4e decl
        if (motDico.mot.equals("artus") || motDico.mot.equals("lacus") || motDico.mot.equals("quercus") || motDico.mot.equals("tribus")) {
            if (nombre.equals("P") && (cas.equals("DAT") || cas.equals("ABL"))) {
                return metLesSpan(motDico.stem2, "ubus");
            }
        }

        //=============fin Exceptions =================================
        if (motDico.nombre.equals("P") && nombre.equals("S")) {return metLesSpan("", "");}

        StemDesinence stemDes = db.rechercheDesinenceNOM(motDicoT,cas,nombre,false);
        res = metLesSpan(stemDes.stem,stemDes.desinence);

        return res;
    }

    private ArrayList<String> rechercheFlexionsADJ(EnregDico motTrouve) {

        ArrayList<String> res = new ArrayList<String>();
        String listeCas[] = {"NOM","VOC","ACC","GEN","DAT","ABL"};
        String listeGenres[] = {"M","F","N"};
        String txt = "";

        List<String> compDecimus = Arrays.asList("nonusdecimus","octavusdecimus","quartusdecimus","quintusdecimus","septimusdecimus","sextusdecimus","tertiusdecimus");
        if (compDecimus.contains(motTrouve.mot)) {
            ArrayList decimusADJ = db.rechercheLemmeSpecial("decimus","ADJ");
            int esst = (int) decimusADJ.get(0);
            EnregDico decm = db.rechercheEntreeDico(esst);

            EnregDico n1 = new EnregDico();
            if (motTrouve.mot.startsWith("nonus")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("nonus","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }
            if (motTrouve.mot.startsWith("octavus")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("octavus","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }
            if (motTrouve.mot.startsWith("quartus")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("quartus","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }
            if (motTrouve.mot.startsWith("quintus")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("quintus","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }
            if (motTrouve.mot.startsWith("septimus")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("septimus","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }
            if (motTrouve.mot.startsWith("sextus")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("sextus","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }
            if (motTrouve.mot.startsWith("tertius")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("tertius","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    ArrayList<Desinence> arld = rechercheDesinencesADJ(motTrouve);
                    txt = rechercheFlexionADJ(n1,arld,cas,"S","POS",genre,false) + rechercheFlexionADJ(decm,arld,cas,"S","POS",genre,false);
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    ArrayList<Desinence> arld = rechercheDesinencesADJ(motTrouve);
                    txt = rechercheFlexionADJ(n1,arld,cas,"P","POS",genre,false) + rechercheFlexionADJ(decm,arld,cas,"P","POS",genre,false);
                    res.add(txt);
                }
            }
            res.add("XXX");
            return res;
        }
        if (motTrouve.mot.equals("sextus decimus") || motTrouve.mot.equals("septimus decimus")) {
            ArrayList decimusADJ = db.rechercheLemmeSpecial("decimus","ADJ");
            int esst = (int) decimusADJ.get(0);
            EnregDico decm = db.rechercheEntreeDico(esst);

            EnregDico n1 = new EnregDico();

            if (motTrouve.mot.startsWith("septimus")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("septimus","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }
            if (motTrouve.mot.startsWith("sextus")) {
                ArrayList n1ADJ = db.rechercheLemmeSpecial("sextus","ADJ");
                esst = (int) n1ADJ.get(0);
                n1 = db.rechercheEntreeDico(esst);
            }

            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    ArrayList<Desinence> arld = rechercheDesinencesADJ(motTrouve);
                    txt = rechercheFlexionADJ(n1,arld,cas,"S","POS",genre,false) + " " + rechercheFlexionADJ(decm,arld,cas,"S","POS",genre,false);
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    ArrayList<Desinence> arld = rechercheDesinencesADJ(motTrouve);
                    txt = rechercheFlexionADJ(n1,arld,cas,"P","POS",genre,false) + " " + rechercheFlexionADJ(decm,arld,cas,"P","POS",genre,false);
                    res.add(txt);
                }
            }
            res.add("XXX");
            return res;
        }

        if (motTrouve.mot.equals("tertiusvicesimus")) {
            ArrayList decimusADJ = db.rechercheLemmeSpecial("vicesimus","ADJ");
            int esst = (int) decimusADJ.get(0);
            EnregDico decm = db.rechercheEntreeDico(esst);

            EnregDico n1 = new EnregDico();

            ArrayList n1ADJ = db.rechercheLemmeSpecial("tertius","ADJ");
            esst = (int) n1ADJ.get(0);
            n1 = db.rechercheEntreeDico(esst);

            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    ArrayList<Desinence> arld = rechercheDesinencesADJ(motTrouve);
                    txt = rechercheFlexionADJ(n1,arld,cas,"S","POS",genre,false) + rechercheFlexionADJ(decm,arld,cas,"S","POS",genre,false);
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    ArrayList<Desinence> arld = rechercheDesinencesADJ(motTrouve);
                    txt = rechercheFlexionADJ(n1,arld,cas,"P","POS",genre,false) + rechercheFlexionADJ(decm,arld,cas,"P","POS",genre,false);
                    res.add(txt);
                }
            }
            res.add("XXX");
            return res;
        }
        /*
        String rechQuery = "SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, personne, nombre, numrad, term, age, freq FROM INFLEXION WHERE ";
        String comp = "( decl1 = " + String.valueOf(motTrouve.decl1) + " OR decl1 = 0) AND decl2 <= " + String.valueOf(motTrouve.decl2) + " AND pos = 'ADJ' ORDER BY decl2 ";
        if (motTrouve.decl1 == 2) {
            comp = " decl1 <= " + String.valueOf(motTrouve.decl1) + " AND decl2 <= 8 AND pos = 'ADJ' ORDER BY decl2 ";
        }
        rechQuery += comp;
        ArrayList<Desinence> listeDes = db.effectueRechercheDesinences(rechQuery,false);
        */
        ArrayList<Desinence> listeDes = rechercheDesinencesADJ(motTrouve);

        if (motTrouve.type.equals("COMP")) {
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"S","COMP",genre,false);
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"P","COMP",genre,false);
                    res.add(txt);
                }
            }

            if (motTrouve.stem4.equals("")) {
                for (String genre : listeGenres) {
                    for (String cas : listeCas) {
                        txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"S","SUPER",genre,false);
                        res.add(txt);
                    }
                    for (String cas : listeCas) {
                        txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"P","SUPER",genre,false);
                        res.add(txt);
                    }
                }
            }
            res.add("XXX");
            return res;
        }
        if (motTrouve.type.equals("SUPER")) {
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"S","SUPER",genre,false);
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"P","SUPER",genre,false);
                    res.add(txt);
                }
            }

            res.add("XXX");
            return res;
        }

        for (String genre : listeGenres) {
            for (String cas : listeCas) {
                txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"S","POS",genre,false);
                res.add(txt);
            }
            for (String cas : listeCas) {
                txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"P","POS",genre,false);
                res.add(txt);
            }
        }

        if (motTrouve.type.equals("X")) {
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"S","COMP",genre,false);
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"P","COMP",genre,false);
                    res.add(txt);
                }
            }
            if (motTrouve.stem4.equals("")) {
                res.add("XXX");
            }
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"S","SUPER",genre,false);
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    txt = rechercheFlexionADJ(motTrouve,listeDes,cas,"P","SUPER",genre,false);
                    res.add(txt);
                }
            }

            return res;
        }
        else {
            res.add("XXX");
        }
        return res;
    }

    private String rechercheFlexionADJ(EnregDico motDico, ArrayList<Desinence> desinences, String cas , String nombre , String type , String genre, boolean ToutesFormes )  {
        String res = "";
        if (motDico.decl1 >= 9) {
            return motDico.mot;
        }

        //========== Exceptions
        if (motDico.mot.equals("hebes") || motDico.mot.equals("dis")) {
            if (nombre.equals("S") && cas.equals("ABL")) {
                if (!type.equals("COMP") && !type.equals("SUPER")) {
                    return metLesSpan(motDico.stem2, "i");
                }

            }
        }

        if (motDico.mot.equals("vetus") || motDico.mot.equals("compos") || motDico.mot.equals("particeps") || motDico.mot.equals("pauper")  || motDico.mot.equals("princeps") || motDico.mot.equals("sospes") || motDico.mot.equals("superstes")) {
            if (!type.equals("COMP") && !type.equals("SUPER")) {
                if (nombre.equals("S")) {
                    if (cas.equals("ABL")) {
                        return metLesSpan(motDico.stem2, "e");
                    }
                }
                if (nombre.equals("P")) {
                    if (cas.equals("GEN")) {
                        return metLesSpan(motDico.stem2, "um");
                    }
                    if (genre.equals("N")) {
                        if (cas.equals("NOM") || cas.equals("VOC") || cas.equals("ACC")) {
                            return metLesSpan(motDico.stem2, "a");
                        }
                    }
                }
            }
        }
        if (motDico.mot.endsWith("plures")) {
            if (!type.equals("COMP") && !type.equals("SUPER")) {
                if (nombre.equals("S")) {
                    return metLesSpan( "", "");

                }
                if (nombre.equals("P")) {
                    if (genre.equals("N")) {
                        if (cas.equals("NOM") || cas.equals("VOC") || cas.equals("ACC")) {
                            return metLesSpan(motDico.stem2,  "a");
                        }
                    }

                }
            }

        }
        /*
        if (motDico.mot.equals("dis")) { cas general ...
            if (nombre.equals("S") && cas.equals("ABL")) {
                return metLesSpan(stem: motDico.stem2, terminaison: "i")
            }

        }
        */
        if (motDico.mot.equals("dives")) {
            if (!type.equals("COMP") && !type.equals("SUPER")) {
                if (nombre.equals("P")) {
                    if (cas.equals("GEN")) {
                        return metLesSpan(motDico.stem2, "um");
                    }
                }
            }
        }

        if (motDico.mot.equals("meus")) {
            if (nombre.equals("S") && cas.equals("VOC") && genre.equals("M")) {
                return metLesSpan( "m",  "i");
            }
        }
        if (motDico.mot.equals("alius")) {
            if (nombre.equals("S") && cas.equals("GEN")) {
                return metLesSpan("al", "ius");
            }
        }
        //=========================

        StemDesinence stemDes = db.rechercheDesinenceADJ(motDico, desinences, cas, nombre, type, genre, ToutesFormes);
        res = metLesSpan(stemDes.stem, stemDes.desinence);
        String[] suffs = {"cumque","nam","libet","dem"};

        for (String s : suffs) {
            if (motDico.mot.endsWith(s)) {
                res += metLesSpan(s, "");
                break;
            }
        }
        if (motDico.mot.endsWith("que") && !motDico.mot.endsWith("cumque")) {
            res += metLesSpan("que", "");
        }
        if (motDico.mot.endsWith("vis") && motDico.decl1 == 1) { // utervis
            res += metLesSpan("vis", "");
        }
        //suffixes en -nam -que ou -cumque ou -dem ou -vis ou -libet


        if (motDico.mot.equals("nullusdum")) {
            res += metLesSpan( "dum",  "");
        }
        if (motDico.decl1 == 3 && motDico.mot.endsWith("ns")) { // participes presents
            if (nombre.equals("S") && cas.equals("ABL") && (type.equals("POS") || type.equals("X"))) {
                res += ",</BR>" + metLesSpan( motDico.stem2,  "e");
            }
        }
        return res;
    }



    private ArrayList<String> rechercheFlexionsVerbe(EnregDico motDico,String voix) {

        String voixQ = voix;
        if (voix.isEmpty()) {
            voixQ = "ACTIVE";
        }
        if (motDico.type.equals("DEP")) {
            voixQ = "PASSIVE";
        }

        String rechQuery = "SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, personne, nombre, numrad, term, age, freq FROM INFLEXION WHERE ";
        rechQuery += "( decl1 = " + String.valueOf(motDico.decl1) + " OR decl1 = 0) AND decl2 <= " + String.valueOf(motDico.decl2) + " AND pos = 'V' ORDER BY decl2 ";
        ArrayList<Desinence> listeDes = db.effectueRechercheDesinences(rechQuery,false);

        rechQuery = "SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, personne, nombre, numrad, term, age, freq FROM INFLEXION WHERE ";
        rechQuery += "( decl1 = " + String.valueOf(motDico.decl1) + " OR decl1 = 0) AND decl2 <= " + String.valueOf(motDico.decl2) + " AND pos = 'VPAR' ";
        rechQuery += "AND cas = 'NOM' AND (genre = 'M' OR genre = 'N' OR genre = 'X')  ORDER BY decl2 ";
        ArrayList<Desinence> listeDesPart = db.effectueRechercheDesinences(rechQuery, false);
        int nbTemps = 6;
        String listeTemps[] = {"PRES","FUT","IMPF","PERF","PLUP","FUTP"};
        if (motDico.type.equals("DEP") || motDico.type.equals("SEMIDEP") || voixQ.equals("PASSIVE")) {
            nbTemps = 3;
        }

        ArrayList<String> res = new ArrayList<String>();
        String txt = "";

        ArrayList<Desinence> resTemp = new ArrayList<Desinence>();

        // =========================  INDICATIF ===================

        for (int tp = 0; tp < nbTemps; tp++) {
            String temps = listeTemps[tp];
            int j = 0;
            while (j < listeDes.size()) {
                Desinence desT = listeDes.get(j);
                if ((desT.voix.equals(voixQ)) && (desT.temps.equals(temps)) && (desT.mode.equals("IND"))) {
                    resTemp.add(desT);

                }
                j += 1;
            }
            for (int i = 1; i < 4; i++) {
                txt = rechercheFlexionVerbe(motDico, resTemp, temps, voixQ, "S", i,"IND", false);
                res.add(txt);

            }
            for (int i = 1; i < 4; i++) {
                txt = rechercheFlexionVerbe(motDico, resTemp, temps, voixQ, "P", i,"IND", false);
                res.add(txt);
            }
        }
        String partp = rechercheFlexionPART(motDico, listeDesPart, "PERF", "PASSIVE", "NOM", "S", "M",false);
        String partpPlur = rechercheFlexionPART(motDico, listeDesPart, "PERF", "PASSIVE", "NOM", "P", "M",false);
        String partpN = rechercheFlexionPART(motDico, listeDesPart, "PERF", "PASSIVE", "NOM", "S", "N",false);

        if (motDico.type.equals("DEP") || motDico.type.equals("SEMIDEP") || voix.equals("PASSIVE")) {

            String SumPres[] = {"sum","es","est","sumus","estis","sunt"};
            String SumFut[] = {"ero","eris","erit","erimus","eritis","erunt"};
            String SumImp[] = {"eram","eras","erat","eramus","eratis","erant"};

            if (motDico.type.equals("IMPERS")) {

                if (!partp.equals("") && !partp.equals("zzz")) {
                    res.add("");
                    res.add("");

                    txt = partpN + " " + metLesSpan(SumPres[2],"");
                    res.add(txt);

                    for (int i = 3; i < 6; i++) {
                        res.add("");
                    }
                    res.add("");
                    res.add("");
                    txt = partpN + " " + metLesSpan(SumImp[2],"");
                    res.add(txt);
                    for (int i = 3; i < 6; i++) {
                        res.add("");
                    }
                    for (int i = 0; i < 3; i++) {
                        res.add("");
                    }
                    for (int i = 3; i < 6; i++) {
                        res.add("");
                    }
                }
                else {
                    for (int i = 0; i < 18; i++) {
                        res.add("");
                    }
                }
            }
            else {
                if (!partp.equals("") && !partp.equals("zzz")) {
                    for (int i = 0; i < 3; i++) {
                        txt = partp + " " + metLesSpan(SumPres[i],"");
                        res.add(txt);
                    }
                    for (int i = 3; i < 6; i++) {
                        txt = partpPlur + " " + metLesSpan(SumPres[i],"");
                        res.add(txt);
                    }
                    for (int i = 0; i < 3; i++) {
                        txt = partp + " " + metLesSpan(SumImp[i],"");
                        res.add(txt);
                    }
                    for (int i = 3; i < 6; i++) {
                        txt = partpPlur + " " + metLesSpan(SumImp[i],"");
                        res.add(txt);
                    }
                    for (int i = 0; i < 3; i++) {
                        txt = partp + " " + metLesSpan(SumFut[i],"");
                        res.add(txt);
                    }
                    for (int i = 3; i < 6; i++) {
                        txt = partpPlur + " " + metLesSpan(SumFut[i],"");
                        res.add(txt);
                    }
                }
                else {
                    for (int i = 0; i < 18; i++) {
                        res.add("");
                    }

                }
            }

        }

        // =========================  SUBJONCTIF ===================
        resTemp.clear();
        String listeTempsSubj[] = {"PRES","IMPF","PERF","PLUP"};
        nbTemps = 4;
        if (motDico.type.equals("DEP") || motDico.type.equals("SEMIDEP") || voixQ.equals("PASSIVE")) {
            nbTemps = 2;
        }
        for (int it = 0; it < nbTemps; it++) {
            String temps = listeTempsSubj[it];
            //resTemp = new ArrayList<Desinence>();
            resTemp.clear();
            int j = 0;
            while (j < listeDes.size()) {
                Desinence desT = listeDes.get(j);
                if ((desT.voix.equals(voixQ)) && (desT.temps.equals(temps)) && (desT.mode.equals("SUB"))) {
                    resTemp.add(desT);
                }
                j += 1;
            }

            for (int i = 1; i < 4; i++) {
                txt = rechercheFlexionVerbe(motDico,resTemp,temps,voixQ,"S",i,"SUB",false);
                res.add(txt);
            }
            for (int i = 1; i < 4; i++) {
                txt = rechercheFlexionVerbe(motDico,resTemp,temps,voixQ,"P",i,"SUB",false);
                res.add(txt);
            }
        }

        if (motDico.type.equals("DEP") || motDico.type.equals("SEMIDEP") || voix.equals("PASSIVE")) {
            //String partp = rechercheFlexionPART(motDico, listeDesPart, "PERF", "PASSIVE", "NOM", "S", "M",false);
            //String partpPlur = rechercheFlexionPART(motDico, listeDesPart, "PERF", "PASSIVE", "NOM", "P", "M",false);

            String SumSubjPres[] = {"sim","sis","sit","simus","sitis","sint"};
            String SumSubjImp[] = {"essem","esses","esset","essemus","essetis","essent"};

            if (motDico.type.equals("IMPERS")) {
                if (!partp.equals("") && !partp.equals("zzz")) {
                    if (!partp.equals("") && !partp.equals("zzz")) {
                        res.add("");
                        res.add("");

                        txt = partpN + " " + metLesSpan(SumSubjPres[2],"");
                        res.add(txt);

                        for (int i = 3; i < 6; i++) {
                            res.add("");
                        }
                        res.add("");
                        res.add("");
                        txt = partpN + " " + metLesSpan(SumSubjImp[2],"");
                        res.add(txt);
                        for (int i = 3; i < 6; i++) {
                            res.add("");
                        }
                        for (int i = 0; i < 3; i++) {
                            res.add("");
                        }
                        for (int i = 3; i < 6; i++) {
                            res.add("");
                        }
                    }
                    else {
                        for (int i = 0; i < 18; i++) {
                            res.add("");
                        }
                    }
                }
            }
            else {
                if (!partp.equals("") && !partp.equals("zzz")) {
                    for (int i = 0; i < 3; i++) {
                        txt = partp + " " + metLesSpan(SumSubjPres[i],"");
                        res.add(txt);
                    }
                    for (int i = 3; i < 6; i++) {
                        txt = partpPlur + " " + metLesSpan(SumSubjPres[i],"");
                        res.add(txt);
                    }
                    for (int i = 0; i < 3; i++) {
                        txt = partp + " " + metLesSpan(SumSubjImp[i],"");
                        res.add(txt);
                    }
                    for (int i = 3; i < 6; i++) {
                        txt = partpPlur + " " + metLesSpan(SumSubjImp[i],"");
                        res.add(txt);
                    }
                }
                else {
                    for (int i = 0; i < 12; i++) {
                        res.add("");
                    }
                }
            }
        }

        // =========================  IMPERATIF ===================
        resTemp.clear();
        int j = 0;
        while (j < listeDes.size()) {
            Desinence desT = listeDes.get(j);
            if ((desT.voix.equals(voixQ)) && (desT.temps.equals("PRES")) && (desT.mode.equals("IMP"))) {
                resTemp.add(desT);
            }
            j += 1;
        }

        txt = rechercheFlexionVerbe(motDico, resTemp, "PRES", voixQ, "S", 2, "IMP", false);
        res.add(txt);
        txt = rechercheFlexionVerbe(motDico, resTemp,"PRES", voixQ, "P", 2, "IMP", false);
        res.add(txt);

        resTemp.clear();
        j = 0;
        while (j < listeDes.size()) {
            Desinence desT = listeDes.get(j);
            if ((desT.voix.equals(voixQ)) && (desT.temps.equals("FUT")) && (desT.mode.equals("IMP"))) {
                resTemp.add(desT);
            }
            j += 1;
        }

        txt = rechercheFlexionVerbe(motDico, resTemp,"FUT", voixQ, "S", 2, "IMP", false);
        res.add(txt);
        txt = rechercheFlexionVerbe(motDico, resTemp,"FUT", voixQ, "S", 3, "IMP", false);
        res.add(txt);

        if (voixQ.equals("PASSIVE")) {
            txt = "";
        }
        else {
            txt = rechercheFlexionVerbe(motDico, resTemp,"FUT", voixQ, "P", 2, "IMP", false);
        }
        res.add(txt);

        txt = rechercheFlexionVerbe(motDico, resTemp,"FUT", voixQ, "P", 3, "IMP", false);
        res.add(txt);

        // =========================  INFINITIF ===================
        resTemp.clear();
        int k = 0;
        while (k < listeDes.size()) {
            Desinence desT = listeDes.get(k);
            if ((desT.voix.equals(voixQ)) && (desT.mode.equals("INF"))) {
                resTemp.add(desT);
            }
            k += 1;
        }
        txt = rechercheFlexionVerbe(motDico, resTemp, "PRES", voixQ, "X", 0, "INF", false);
        res.add(txt);
        if (voixQ.equals("ACTIVE") && !motDico.type.equals("SEMIDEP")) {
            txt = rechercheFlexionVerbe(motDico, resTemp, "PERF", voixQ, "X", 0, "INF", false);
            res.add(txt);
        }
        else {

            txt = rechercheFlexionPART(motDico, listeDesPart, "PERF", "PASSIVE", "NOM", "S", "N", false);
            if (!txt.equals("zzz") && !txt.equals("")) {
                txt += " " + metLesSpan("esse","");
            }
            else {txt = "";}
            res.add(txt);

        }

        // =========================  PARTICIPE ===================
        if (voixQ.equals("ACTIVE")) {
            txt = rechercheFlexionPART(motDico, listeDesPart, "PRES", "ACTIVE", "NOM", "S", "M", false);
            res.add(txt);
        }
        else {
            txt = rechercheFlexionPART(motDico, listeDesPart, "PERF", "PASSIVE", "NOM", "S", "M", false);
            res.add(txt);

        }
        if (voixQ.equals("ACTIVE") || motDico.type.equals("DEP")) {
            txt = rechercheFlexionPART(motDico, listeDesPart, "FUT", "ACTIVE", "NOM", "S", "M", false);
            res.add(txt);
        }
        else {
            txt = rechercheFlexionPART(motDico, listeDesPart, "FUT", "PASSIVE", "NOM", "S", "M", false);
            res.add(txt);
        }

        return res;
    }

    private String rechercheFlexionVerbe(EnregDico motDico, ArrayList<Desinence> desinences, String temps, String voix, String nombre, int personne, String mode, Boolean ToutesFormes) {

        String res = "";
        if (motDico.mot.equals("sum")) {
            String essaiSum = flexionSUMVariante(temps, "ACTIVE", nombre, personne, mode);
            if (!essaiSum.equals("")) {
                return metLesSpan(essaiSum,"");
            }
        }

        if (motDico.mot.equals("possum")) {
            if (mode.equals("IMP")) {
                return "";
            }
        }
        if (motDico.mot.equals("salveo")) {
            if (!mode.equals("IMP") && !mode.equals("INF")) {
                return "";
            }
        }
        if (motDico.mot.endsWith("sto") && voix.equals("PASSIVE")) {
            return "";
        }
        if (motDico.mot.endsWith("jaceo") && voix.equals("PASSIVE")) {
            return "";
        }
        if (motDico.mot.equals("quaeso") || motDico.mot.equals("quæso")) {
            if (!mode.equals("IND")) {
                return "";
            }
            if (mode.equals("IND")) {
                if (temps.equals("PRES") || temps.equals("PERF")) {
                    if (temps.equals("PRES")) {
                        if (personne == 1 && nombre.equals("P")) {
                            return metLesSpan("quæs", "umus");
                        }
                        if (personne != 1) {
                            return "";
                        }
                    }
                }
                else {
                    return "";
                }
            }
        }
        if (motDico.mot.equals("memini")) {
            if (mode.equals("IMP") && temps.equals("PRES")) {
                if (personne == 2 && nombre.equals("S")) {
                    return metLesSpan("memen", "to");
                }
                if (personne == 2 && nombre.equals("P")) {
                    return metLesSpan("memen", "tote");
                }
            }
        }

        if (motDico.type.equals("IMPERS")) {
            if (personne != 3) {
                return "";
            }
            else {
                if (nombre.equals("P")) {
                    return "";
                }
            }
        }
        if (motDico.mot.equals("malo") && mode.equals("PART")) {return "";}

        if (motDico.mot.equals("volo") && motDico.decl1 == 6) {
            String vol = conjugVOLO(temps, personne, mode, voix, nombre);
            if (!vol.equals("x")) {return metLesSpan(vol,"");}
        }
        if (motDico.mot.equals("nolo") && motDico.decl1 == 6) {
            String nol = conjugNOLO(temps, personne, mode, voix, nombre);
            if (!nol.equals("x")) {return metLesSpan(nol,"");}
        }

        if (motDico.mot.equals("malo") && motDico.decl1 == 6) {
            String malo = conjugMALO(temps, personne, mode, voix, nombre);
            if (!malo.equals("x")) {return metLesSpan(malo,"");}
        }

        StemDesinence stemDes = db.rechercheDesinenceVERBE(motDico, desinences, temps, voix, nombre, personne, mode, ToutesFormes);
        res = metLesSpan(stemDes.stem, stemDes.desinence);

        return res;
    }



    //===========================    PARTICIPE ========================================================
    private String rechercheFlexionPART(EnregDico motDico, ArrayList<Desinence> desinences, String temps, String voix, String cas, String nombre, String genre, Boolean ToutesFormes) {
        if (motDico.mot.equals("malo") && motDico.decl1 == 6) {
            return "";
        }
        if (motDico.decl1 == 3 && motDico.decl2 == 3) { //composes de fio
            if (temps.equals("PRES") || temps.equals("FUT")) {
                return "";
            }
        }
        if (motDico.mot.equals("quaeso") || motDico.mot.equals("quæso")) {
            return "";
        }
        if (motDico.mot.equals("salveo")) {
            return "";
        }
        if (motDico.mot.equals("abnuo")) {
            if (temps.equals("PERF")) {
                return "";
            }
        }
        if (motDico.mot.equals("jaceo")) {
            if (temps.equals("PERF")) {
                return "";
            }
        }
        if (motDico.mot.equals("luo")) {
            if (temps.equals("PERF")) {
                return "";
            }
        }
        if (motDico.decl1 == 5) {
            if (!motDico.mot.equals("absum") && !motDico.mot.equals("praesum") && !motDico.mot.equals("præsum")) {
                if (temps.equals("PRES")) {return "";}
            }
        }
        StemDesinence stemDes = db.rechercheDesinencePART(motDico, desinences, temps, voix, cas, nombre, genre, ToutesFormes);

        String res = metLesSpan(stemDes.stem, stemDes.desinence);
        if (motDico.mot.equals("absum")) {
            if (temps.equals("PRES")) {
                return metLesSpan(motDico.stem1, stemDes.desinence);
            }
        }
        if (motDico.mot.equals("ens")) {
            if (temps.equals("PRES")) {
                return metLesSpan(".", stemDes.desinence); // sinon metlesSpan renvoie ""
            }
        }

        return res;

    }

//===========================   NUM ========================================================

    private ArrayList<String> rechercheFlexionsNUM(EnregDico motTrouve) {

        ArrayList<String> res = new ArrayList<String>();
        String listeCas[] = {"NOM","VOC","ACC","GEN","DAT","ABL"};
        String listeGenres[] = {"M","F","N"};

        String txt = "";

        String typeT = motTrouve.type;
        if (typeT.equals("X")) {
            typeT = "CARD" ;  // ????????????????
        }

        for (String genre : listeGenres) {
            for (String cas : listeCas) {
                txt = rechercheFlexionNUM(motTrouve,typeT ,cas, "S", genre, false);
                res.add(txt);
            }
            for (String cas : listeCas) {
                txt = rechercheFlexionNUM(motTrouve,typeT ,cas, "P", genre, false);
                res.add(txt);
            }
        }

        return res;
    }


    private String rechercheFlexionNUM(EnregDico motDico, String type, String cas, String nombre, String genre, Boolean ToutesFormes) {
        String res = "";
        if (motDico.decl1 == 9) {
            res = metLesSpan(motDico.mot,"");
            //return  motDico.mot;
        }

        if (type.equals("X") || type.equals("CARD")) {
            if (nombre.equals("S") && !motDico.nombre.equals("1")) {
                //res = metLesSpan("","");
                return "";
            }
        }

        if (type.equals("DIST")) {
            if (nombre.equals("S") && motDico.mot.endsWith("i")) {
                //res = metLesSpan("","");
                return "";
            }
        }
        if (res.isEmpty()) {
            StemDesinence stemDes = db.rechercheDesinenceNUM(motDico,type,cas,nombre,genre,ToutesFormes);
            res = metLesSpan(stemDes.stem,stemDes.desinence);
        }


        return res;

    }

    private ArrayList<String> rechercheFlexionsPRON(EnregDico motDico) {
        ArrayList<String> res = new ArrayList<String>();
        String listeCas[] = {"NOM", "VOC", "ACC","GEN","DAT","ABL"};
        String listeGenres[] = {"M","F","N"};
        //   let listeTypes = ["POS","COMP","SUP"]
        String txt = "";

        if (motDico.mot.equals("unusquisque")) {

            ArrayList resun = db.rechercheLemmeSpecial("unus", "ADJ");
            int indun = (int) resun.get(0);
            EnregDico unus = db.rechercheEntreeDico(indun); //(indun, "ADJ");

            ArrayList resquis = db.rechercheLemmeSpecial("quis","PRON");
            EnregDico quis = new EnregDico();
            for (int i = 0;i < resquis.size(); i++) {
                int esst = (int) resquis.get(i);
                EnregDico q = db.rechercheEntreeDico(esst);
                if (!q.mot.isEmpty()) {quis = q;}
            }

            ArrayList tabUnus = rechercheFlexionsADJ(unus);
            ArrayList tabQuis = rechercheFlexionsPRON(quis);
            int ind = 0;
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    if (cas.equals("VOC")) {
                        txt = "";
                    }
                    else {
                        txt = (String) tabUnus.get(ind) + (String) tabQuis.get(ind) + metLesSpan("que", ""); //
                        if (genre.equals("N") && (cas.equals("NOM") || cas.equals("ACC"))) {
                            txt += ",</BR>" + (String) tabUnus.get(ind) + metLesSpan("qu", "od") + metLesSpan("que", "");
                        }
                    }
                    ind += 1;
                    res.add(txt);
                }

                for (String cas : listeCas) {
                    if (cas.equals("VOC")) {
                        txt = "";
                    }
                    else {
                        txt = (String) tabUnus.get(ind) + (String) tabQuis.get(ind) + metLesSpan("que", "");
                    }
                    ind += 1;
                    res.add(txt);
                }
            }
            res.add("XXX");
            return res;
        }

        EnregDico motDicoT = motDico.copie();
        if (motDico.decl1 == 1) {
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    String genreT = genre;
                    if (cas.equals("NOM")) {
                        if (genre.equals("M")) {
                            if (motDico.mot.equals("qui") || motDico.mot.equals("quinam") || motDico.mot.equals("ecqui")) {
                                motDicoT.decl2 = 1;
                            }
                            if (motDico.mot.equals("aliqui") || motDico.mot.equals("aliquicumque") || motDico.mot.equals("aliquilibet")) {
                                motDicoT.decl2 = 1;
                            }
                            if (motDico.mot.equals("quivis") || motDico.mot.equals("quilibet") || motDico.mot.equals("quidam") || motDico.mot.equals("quicumque") || motDico.mot.equals("quicunque") || motDico.mot.equals("quiviscumque")) {
                                motDicoT.decl2 = 1;
                            }
                            if (motDico.mot.equals("quis") || motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis") || motDico.mot.equals("quisquam") || motDico.mot.equals("quispiam") || motDico.mot.equals("quisque")) {
                                motDicoT.decl2 = 2;
                            }
                            if (motDico.mot.equals("aliquis") || motDico.mot.equals("aliquispiam") || motDico.mot.equals("aliquisvis")) {
                                motDicoT.decl2 = 2;
                            }
                        }
                        if (genre.equals("F")) {
                            if (motDico.mot.equals("aliqui") || motDico.mot.equals("aliquicumque") || motDico.mot.equals("aliquilibet")) {
                                motDicoT.decl2 = 3;
                            }
                            if (motDico.mot.equals("qui") || motDico.mot.equals("quinam") || motDico.mot.equals("ecqui")) {
                                motDicoT.decl2 = 4; //qui ou 3 si adj
                            }
                            if (motDico.mot.equals("quivis") || motDico.mot.equals("quilibet") || motDico.mot.equals("quidam") || motDico.mot.equals("quicumque") || motDico.mot.equals("quicunque") || motDico.mot.equals("quiviscumque")) {
                                motDicoT.decl2 = 4; //qui ou 3 si adj
                            }
                            if (motDico.mot.equals("quis") || motDico.mot.equals("quisquam") || motDico.mot.equals("quispiam") || motDico.mot.equals("quisque")) {
                                motDicoT.decl2 = 4; // gramm FR mais Bennet : quisnam, quidnam
                            }
                            /*
                            if (motDico.mot.equals("quis") || motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis") || motDico.mot.equals("quisquam") || motDico.mot.equals("quispiam") || motDico.mot.equals("quisque")) {
                                motDicoT.genre = "M"
                                motDicoT.decl2 = 2
                            }
                            if (motDico.mot.equals("aliquis") || motDico.mot.equals("aliquispiam") || motDico.mot.equals("aliquisvis")) {
                                motDicoT.genre = "M"
                                motDicoT.decl2 = 2
                            }
                            */
                            if (motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis")) {
                                motDicoT.genre = "M";
                                motDicoT.decl2 = 2;
                            }
                            if (motDico.mot.equals("aliquis") || motDico.mot.equals("aliquispiam") || motDico.mot.equals("aliquisvis")) {
                                motDicoT.decl2 = 3;
                            }
                        }
                        if (genre.equals("N")) {
                            if (motDico.mot.equals("quis") || motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis") || motDico.mot.equals("quisquam") || motDico.mot.equals("quispiam") || motDico.mot.equals("quisque")) {
                                motDicoT.decl2 = 6;
                            }
                            if (motDico.mot.equals("aliquis") || motDico.mot.equals("aliquispiam") || motDico.mot.equals("aliquisvis")) {
                                motDicoT.decl2 = 6;
                            }
                            if (motDico.mot.equals("quivis") || motDico.mot.equals("quilibet") || motDico.mot.equals("quidam") || motDico.mot.equals("quiviscumque")) {
                                motDicoT.decl2 = 6;
                            }
                            if (motDico.mot.equals("qui") || motDico.mot.equals("quinam") || motDico.mot.equals("ecqui") || motDico.mot.equals("quicumque") || motDico.mot.equals("quicunque")) {
                                motDicoT.decl2 = 7;
                            }
                            if (motDico.mot.equals("aliqui") || motDico.mot.equals("aliquicumque") || motDico.mot.equals("aliquilibet")) {
                                motDicoT.decl2 = 7;
                            }
                        }
                    }
                    if (cas.equals("ACC") && genre.equals("F")) {
                        /*
                            if (motDico.mot.equals("quis") || motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis") || motDico.mot.equals("aliquis") || motDico.mot.equals("quisquam") || motDico.mot.equals("quispiam") || motDico.mot.equals("quisque"))
                        */
                        if (motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis")) {
                            genreT = "M";
                        }

                    }
                    if (cas.equals("ACC") && genre.equals("N")) {
                        if (motDico.mot.equals("quis") || motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis") || motDico.mot.equals("quisquam") || motDico.mot.equals("quispiam") || motDico.mot.equals("quisque")) {
                            motDicoT.decl2 = 6;
                        }
                        if (motDico.mot.equals("aliquis") || motDico.mot.equals("aliquispiam") || motDico.mot.equals("aliquisvis")) {
                            motDicoT.decl2 = 6;
                        }
                        if (motDico.mot.equals("quivis") || motDico.mot.equals("quilibet") || motDico.mot.equals("quidam") || motDico.mot.equals("quiviscumque")) {
                            motDicoT.decl2 = 6;
                        }
                        if (motDico.mot.equals("qui") || motDico.mot.equals("quinam") || motDico.mot.equals("ecqui") || motDico.mot.equals("quicumque") || motDico.mot.equals("quicunque")) {
                            motDicoT.decl2 = 7;
                        }
                        if (motDico.mot.equals("aliqui") || motDico.mot.equals("aliquicumque") || motDico.mot.equals("aliquilibet")) {
                            motDicoT.decl2 = 7;
                        }
                    }
                    if (cas.equals("ABL") && genre.equals("F")) {
                        if (motDico.mot.equals("aliqui") || motDico.mot.equals("aliquicumque") || motDico.mot.equals("aliquilibet")) {
                            motDicoT.decl2 = 3;
                        }
                        if (motDico.mot.equals("qui") || motDico.mot.equals("quinam") || motDico.mot.equals("ecqui")) {
                            motDicoT.decl2 = 4; //4 pron ou 3 si adj
                        }
                        if (motDico.mot.equals("quivis") || motDico.mot.equals("quilibet") || motDico.mot.equals("quidam") || motDico.mot.equals("quicumque") || motDico.mot.equals("quicunque") || motDico.mot.equals("quiviscumque")) {
                            motDicoT.decl2 = 4; //4 pron ou 3 si adj
                        }
                        if (motDico.mot.equals("quis") || motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis") || motDico.mot.equals("quisquam") || motDico.mot.equals("quispiam") || motDico.mot.equals("quisque")) {
                            motDicoT.decl2 = 3; //5 selon Bennett ??
                        }
                        if (motDico.mot.equals("aliquis") || motDico.mot.equals("aliquispiam") || motDico.mot.equals("aliquisvis")) {
                            motDicoT.decl2 = 3; //5 selon Bennett ??
                        }
                    }
                    txt = rechercheFlexionPRON(motDicoT,cas, "S", genreT,false);

                    if (genre.equals("F")) {
                        if (cas.equals("NOM")) {
                            if (motDico.mot.equals("quinam") || motDico.mot.equals("quivis") || motDico.mot.equals("quilibet") || motDico.mot.equals("quidam")) {
                                motDicoT.decl2 = 3; //4 pron ou 3 si adj
                                txt += " (Pron.),</BR>" + rechercheFlexionPRON(motDicoT,cas, "S", genre, false) + " (Adj.)";
                            }
                            if (motDico.mot.equals("qui") || motDico.mot.equals("ecqui")) {
                                motDicoT.decl2 = 3; //4 pron ou 3 si adj
                                txt += ",</BR>" + rechercheFlexionPRON(motDicoT,cas, "S", genre, false);
                            }
                        }
                        /*
                        if (cas.equals("NOM") || cas.equals("ABL")) {
                            if (motDico.mot.equals("quispiam") || motDico.mot.equals("quisque")) {
                                motDicoT.decl2 = 4 //= quis ou 4 (quae) si adj
                                txt += " (Pron.),</BR>" + rechercheFlexionPRON(motDico: motDicoT,cas: cas, nombre: "S", genre: genre, ToutesFormes : ToutesFormes) + " (Adj.)"
                            } // gramm FR : pas de variante
                        }
                        */
                    }

                    if (genre.equals("N")) {
                        if (cas.equals("NOM") || cas.equals("ACC")) {
                            if (motDico.mot.equals("quispiam") || motDico.mot.equals("quisque") || motDico.mot.equals("quivis") || motDico.mot.equals("quilibet") || motDico.mot.equals("quidam")) {
                                motDicoT.decl2 = 7; //6 pron ou 7 si adj
                                txt += " (Pron.),</BR>" + rechercheFlexionPRON(motDicoT,cas, "S", genre, false) + " (Adj.)";
                            }
                        }
                    }
                    res.add(txt);
                }

                for (String cas : listeCas) {
                    if (genre.equals("N")) {
                        if (cas.equals("NOM") || cas.equals("ACC")) {
                            if (motDico.mot.equals("qui") || motDico.mot.equals("quinam") || motDico.mot.equals("ecqui") || motDico.mot.equals("quicumque") || motDico.mot.equals("quicunque")) {
                                motDicoT.decl2 = 8; //8 pron ou 9 si adj
                            }
                            if (motDico.mot.equals("quis") || motDico.mot.equals("quisnam") || motDico.mot.equals("ecquis")) {
                                motDicoT.decl2 = 8;
                            }
                            if (motDico.mot.equals("quisquam") || motDico.mot.equals("quispiam") || motDico.mot.equals("quisque")) { // ??
                                motDicoT.decl2 = 8;
                            }
                            if (motDico.mot.equals("aliqui") || motDico.mot.equals("aliquicumque") || motDico.mot.equals("aliquilibet") || motDico.mot.equals("aliquis") || motDico.mot.equals("aliquispiam") || motDico.mot.equals("aliquisvis")) { // aliquis ???
                                motDicoT.decl2 = 9;
                            }

                            if (motDico.mot.equals("quivis") || motDico.mot.equals("quilibet") || motDico.mot.equals("quidam") || motDico.mot.equals("quiviscumque")) {
                                motDicoT.decl2 = 8;
                            }

                        }
                    }
                    txt = rechercheFlexionPRON(motDicoT, cas, "P", genre, false);


                    res.add(txt);
                }

            }

        }
        else {
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    txt = rechercheFlexionPRON(motDico,cas, "S", genre, false);
                    res.add(txt);
                }

                for (String cas : listeCas) {
                    txt = rechercheFlexionPRON(motDico ,cas, "P", genre, false);
                    res.add(txt);
                }

            }
        }
        return res;
    }

    private String rechercheFlexionPRON(EnregDico motDico, String cas, String nombre, String genre, Boolean ToutesFormes) {
        String res = "";
        if (motDico.decl1 == 0) {return motDico.mot;}

        /*
         if (motDico.mot.equals("hic")) {
         if (cas.equals("GEN") && nombre.equals("S")) {
         return "hu<span class=\"termCoul\">jus</span>" // sinon, ius ????
         }
         }
         */

        // is ???

        if (motDico.mot.equals("ego") || motDico.mot.equals("tu") || motDico.mot.equals("sui")) {
            if (genre.equals("N") || nombre.equals("P")) {
                return "";
                //return "&nbsp;&nbsp;-&nbsp;&nbsp;";
            }
        }
        if (motDico.mot.equals("egomet") || motDico.mot.equals("tute") || motDico.mot.equals("suimet")) {
            if (genre.equals("N") || nombre.equals("P")) {
                return ""; // &nbsp;&nbsp;-&nbsp;&nbsp;
            }
        }
        if (motDico.mot.equals("nos") || motDico.mot.equals("vos") || motDico.mot.equals("nosmet") || motDico.mot.equals("vosmet")) {
            if (genre.equals("N") || nombre.equals("S")) {
                return "";
            }
        }
        if (motDico.mot.equals("quisquam")) {
            if (nombre.equals("P")) {
                return "";
            }
        }
        if (motDico.decl1 != 5 && cas.equals("VOC")) {
            return "";
        }
        //quisquis declines both but has only quisquis, quidquid, quōquō, in common use.
        if (motDico.mot.equals("quisquis") || motDico.mot.equals("quisquislibet")) {
            String stem = "";
            if (nombre.equals("S")) {
                if (cas.equals("ABL")) {
                    stem = "quoquo";
                    //return metLesSpan(stem: stem, terminaison: "")
                }
                if (cas.equals("NOM")) {
                    if (genre.equals("N")) {
                        stem = "quidquid";
                        //return metLesSpan(stem: "quidquid", terminaison: "")
                    }
                    else {
                        stem = "quisquis";
                        //return metLesSpan(stem: "quisquis", terminaison: "")
                    }
                }
                if (cas.equals("ACC")) {
                    if (genre.equals("N")) {
                        stem = "quidquid";
                        //return metLesSpan(stem: "quidquid", terminaison: "")
                    }
                }
            }
            if (motDico.mot.equals("quisquislibet") && !stem.isEmpty()) {
                stem += "libet";
            }
            return "";
        }

        StemDesinence stemDes = db.rechercheDesinencePRON(motDico, cas, nombre, genre, false);
        res = metLesSpan(stemDes.stem, stemDes.desinence);
        if (motDico.mot.equals("nos") || motDico.mot.equals("vos")) {
            if (cas.equals("GEN") && nombre.equals("P")) {
                String f2 = metLesSpan(motDico.stem2, "um");
                res += ",</BR>" + f2;
            }
        }

        String listeSuffPron[] = {"quam","dam","piam","nam","dem","libet","vis","viscumque","met"}; // que
        for (String suff : listeSuffPron) {
            if (motDico.mot.endsWith(suff)) {
                // int indInt = motDico.mot.length() - listeSuffPron[s].length() + 1;
                res += metLesSpan(suff,"");
                break;
            }
        }


        if (motDico.mot.endsWith("cumque") && !motDico.mot.endsWith("viscumque")) {
            res += metLesSpan("cumque", "");
        }
        if (motDico.mot.endsWith("cunque") && !motDico.mot.endsWith("viscunque")) {
            res += metLesSpan("cunque", "");
        }
        if (motDico.mot.endsWith("que") && !motDico.mot.endsWith("cumque") && !motDico.mot.endsWith("cunque")) {
            res += metLesSpan("que", "");
        }
        if (motDico.mot.equals("tute")) {
            res += metLesSpan("te", "");
        }
        /*
         quispiam (subst) 1 + 0,2,5,6 !!!! -> pas mis ajouter variante quispiam (adj) 1 + 0,2,4,7
         quisque (subst) 1 + 0,2,5,6 !!!! -> pas mis ajouter variante quisque (adj) 1 + 0,2,4,7

         quivis (subst) 1 + 0,1,4,6 !!!! -> pas mis ajouter variante quivis (adj) 1 + 0,1,4,7
         quilibet (subst) 1 + 0,1,4,6 !!!! -> pas mis ajouter variantequilibet (adj) 1 + 0,1,4,7
         quidam (subst) 1 + 0,1,4,6 !!!! -> pas mis ajouter variante quidam (adj) 1 + 0,1,4,7

         */

        return res;
    }


    private ArrayList<String> rechercheFlexionsPARTsimple(EnregDico motDico, boolean ToutesFormes) {

        ArrayList<String> res = new ArrayList<String>();
        String listeCas[] = {"NOM","VOC","ACC","GEN","DAT","ABL"};
        String listeGenres[] = {"M","F","N"};
        //   let listeTypes = ["POS","COMP","SUP"]
        String txt = "";
        String voix = "";

        String rechQuery = "SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, personne, nombre, numrad, term, age, freq FROM INFLEXION WHERE ";
        rechQuery += "( decl1 = " + String.valueOf(motDico.decl1) + " OR decl1 = 0) AND decl2 <= " + String.valueOf(motDico.decl2) + " AND pos = 'VPAR' ORDER BY decl2 ";

        ArrayList listeDes = db.effectueRechercheDesinences(rechQuery,false);

        if (motDico.type.equals("PERF_PASSIVE")) {
            voix = "PASSIVE";
        }
        else {
            voix = "ACTIVE";
        }
        if (voix.equals("ACTIVE")) {
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    txt = rechercheFlexionPART(motDico, listeDes, "PRES", voix, cas, "S", genre, false);
                    if (cas.equals("ABL") && motDico.decl1 != 6) {
                        if (motDico.mot.equals("ens")) {
                            txt += ",</BR>" + metLesSpan(".", "enti");
                        }
                        else {
                            txt += ",</BR>" + metLesSpan(motDico.stem1, "enti");
                        }

                    }
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    txt = rechercheFlexionPART(motDico, listeDes, "PRES", voix, cas, "P", genre, false);
                    res.add(txt);
                }
            }
        }
        else {
            for (String genre : listeGenres) {
                for (String cas : listeCas) {
                    txt = rechercheFlexionPART(motDico, listeDes, "PERF", voix, cas, "S", genre, false);
                    res.add(txt);
                }
                for (String cas : listeCas) {
                    txt = rechercheFlexionPART(motDico, listeDes, "PERF", voix, cas, "P", genre, false);
                    res.add(txt);
                }
            }
        }

        return res;
    }
/*
    ArrayList rechercheVariantesFlexionsVerbe(EnregDico motDico, String voix) {
        ArrayList resultats = new ArrayList();

        String resVide = "X";
        if (motDico.refs.equals("") || motDico.refs.equals("X")) {
            ArrayList temp = new ArrayList();
            temp.add(resVide);
            resultats.add(temp);
            return resultats;
        }
        String references[] = motDico.refs.split(",");

        for (int r = 0; r < references.length ; r++) {
            int val = Integer.parseInt(references[r]);
            db.openDataBase();
            EnregDico motTemp = db.rechercheEntreeDicoParRefVar(val,motDico.POS);
            db.close();

            ArrayList tabflex = rechercheFlexionsVerbe(motTemp, voix);
            resultats.add(tabflex);

        }
        return resultats;
    }
*/

    ArrayList<Desinence> rechercheDesinencesNOM(EnregDico motTrouve) {
        String rechQuery = "SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, personne, nombre, numrad, term, age, freq FROM INFLEXION WHERE ";
        rechQuery += "( decl1 = " + String.valueOf(motTrouve.decl1) + " OR decl1 = 0) AND decl2 <= " + String.valueOf(motTrouve.decl2) + " AND pos = 'ADJ' ORDER BY decl2 ";

        return db.effectueRechercheDesinences(rechQuery,false);
    }

    ArrayList<Desinence> rechercheDesinencesADJ(EnregDico motTrouve) {
        String rechQuery = "SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, personne, nombre, numrad, term, age, freq FROM INFLEXION WHERE ";
        String comp = "( decl1 = " + String.valueOf(motTrouve.decl1) + " OR decl1 = 0) AND decl2 <= " + String.valueOf(motTrouve.decl2) + " AND pos = 'ADJ' ORDER BY decl2 ";
        if (motTrouve.decl1 == 2) {
            comp = " decl1 <= " + String.valueOf(motTrouve.decl1) + " AND decl2 <= 8 AND pos = 'ADJ' ORDER BY decl2 ";
        }
        rechQuery += comp;
        return db.effectueRechercheDesinences(rechQuery,false);
    }

    //=================================EXCEPTIONS ========================================
    private String declineJesus(String radical, String cas, String nombre) {
        String radJesus = radical;//"Jes";
        String res = "";
        if (nombre.equals("S")) {
            if (cas.equals("NOM")) {
                res = metLesSpan(radJesus,"us");
            }
            if (cas.equals("VOC") || cas.equals("GEN") || cas.equals("DAT") || cas.equals("ABL")) {
                res = metLesSpan(radJesus,"u");
            }
            if (cas.equals("ACC")) {
                res = metLesSpan(radJesus,"um");
            }
        }
        return res;
    }

    private String declineBos(String cas, String nombre) {
        String res = "";
        if (cas.equals("GEN") && nombre.equals("S")) {
            res = metLesSpan("boum","");
        }
        if (cas.equals("DAT") && nombre.equals("P")) {
            res = metLesSpan("bubus, bobus","");
        }
        if (cas.equals("ABL") && nombre.equals("P")) {
            res = metLesSpan("bubus, bobus","");
        }

        return res;
    }

    private String declineVis(String cas, String nombre) {
        String res = "";
        if (nombre.equals("S")) {
            if (cas.equals("ACC")) {
                res = metLesSpan("v","im");
            }
            if (cas.equals("ABL")) {
                res = metLesSpan("v","i");
            }

        }
        if (nombre.equals("P")) {
            if (cas.equals("NOM") || cas.equals("VOC") || cas.equals("ACC")) {
                res = metLesSpan("vir","es");
            }
            if (cas.equals("GEN")) {
                res = metLesSpan("vir","ium");
            }
            if (cas.equals("DAT") || cas.equals("ABL")) {
                res = metLesSpan("vir","ibus");
            }

        }
        return res;
    }

    private String declineDomus(String cas, String nombre) {
        String radDomus = "dom";
        String res = "";
        if (cas.equals("GEN")) {
            if (nombre.equals("S")) {
                res = metLesSpan(radDomus, "us");
                res += ",</br>";
                res += metLesSpan(radDomus, "i");
            }
            if (nombre.equals("P")) {
                res = metLesSpan(radDomus, "uum");
                res += ",</br>";
                res += metLesSpan(radDomus, "orum");
            }
        }

        if (cas.equals("DAT")) {
            if (nombre.equals("S")) {
                res = metLesSpan(radDomus, "ui");
                res += ",</br>";
                res += metLesSpan(radDomus, "o");
            }
        }

        if (cas.equals("ACC")) {
            if (nombre.equals("P")) {
                res = metLesSpan(radDomus, "os");
                res += ",</br>";
                res += metLesSpan(radDomus, "us");
            }
        }

        if (cas.equals("ABL")) {
            if (nombre.equals("S")) {
                res = metLesSpan(radDomus, "o");
                res += ",</br>";
                res += metLesSpan(radDomus, "u");
            }
        }
        return res;
    }

    private String declineJuppiter(String cas ,String nombre) {
        String res  = "";
        if (nombre.equals("S")) {
            if (cas.equals("GEN")) {
                res = metLesSpan("Jov", "is") + ",</BR>" + metLesSpan("Juppiter", "is");
                res += ",</BR>" + metLesSpan("Juppitr", "is");
            }

            if (cas.equals("DAT")) {
                res = metLesSpan("Jov", "i");
            }
            if (cas.equals("ABL")) {
                res = metLesSpan("Jov","e");
            }
        }
        if (nombre.equals("P")) {
            if (cas.equals("GEN")) {
                res = metLesSpan("Jov",  "um") + ",</BR>" + metLesSpan("Jov",  "ium");
            }
        }

        return res;
    }
    private String declineJupiter(String cas ,String nombre) {
        String res  = "";
        if (nombre.equals("S")) {
            if (cas.equals("GEN")) {
                res = metLesSpan("Jov", "is") + ",</BR>" + metLesSpan("Jupiter", "is");
                res += ",</BR>" + metLesSpan("Jupitr", "is");
            }

            if (cas.equals("DAT")) {
                res = metLesSpan("Jov", "i");
            }
            if (cas.equals("ABL")) {
                res = metLesSpan("Jov","e");
            }
        }
        if (nombre.equals("P")) {
            if (cas.equals("GEN")) {
                res = metLesSpan("Jov",  "um") + ",</BR>" + metLesSpan("Jov",  "ium");
            }
        }

        return res;
    }

    private String conjugVOLO(String temps, int pers, String mode, String voix, String nombre) {
        if (mode.equals("IND") && temps.equals("PRES") && voix.equals("ACTIVE")) {
            if (nombre.equals("S") && pers == 2) {return "vis";}
            if (nombre.equals("S") && pers == 3) {return "vult";}
            if (nombre.equals("P") && pers == 2) {return "vultis";}

        }
        if (mode.equals("IMP")) {return "";}
        return "x";
    }

    private String conjugNOLO(String temps, int pers, String mode, String voix, String nombre) {
        if (mode.equals("IND") && temps.equals("PRES") && voix.equals("ACTIVE")) {
            if (nombre.equals("S") && pers == 2) {return "non vis";}
            if (nombre.equals("S") && pers == 3) {return "non vult";}
            if (nombre.equals("P") && pers == 2) {return "non vultis";}

        }

        return "x";
    }

    private String conjugMALO(String temps, int pers, String mode, String voix, String nombre) {
        if (mode.equals("IND") && temps.equals("PRES") && voix.equals("ACTIVE")) {
            if (nombre.equals("S") && pers == 2) {return "mavis";}
            if (nombre.equals("S") && pers == 3) {return "mavult";}
            if (nombre.equals("P") && pers == 2) {return "mavultis";}

        }
        if (mode.equals("IMP")) {return "";}


        return "x";
    }

    private EnregDico enregEtre(String dico) {
        EnregDico res = new EnregDico();
        res.mot = "sum";
        if (dico.equals("G")) {
            res.motOrig = "1 sum";
            res.Num = 999900;
        }
        else {
            res.motOrig = "sum";
            res.refPeigne = 24200;
            //res.Num = 5545
            res.Num = 999901;
        }
        res.AgeFreq = "X X X A X";
        res.decl1 = 5;
        res.decl2 = 1;
        res.dico = dico;
        res.POS = "V";
        res.stem1 = "s";
        res.stem2 = ".";
        res.stem3 = "fu";
        res.stem4 = "fut";
        res.type = "TO_BEING";

        return res;

    }

    private String flexionSUMVariante(String temps, String voix, String nombre, int personne, String mode) {
        String TermSumSubjImp[] = {"ssem","sses","sset","ssemus","ssetis","ssent"};
        if (mode.equals("SUB") && voix.equals("ACTIVE") && temps.equals("IMPF")) {
            if (nombre.equals("S")) {
                return "e" + TermSumSubjImp[personne - 1];
            }
            else {
                return "e" + TermSumSubjImp[personne + 2];
            }
        }
        return "";
        //pqpf ind - pqpf sub - fut ant - inf parf
    }


    private String metLesSpan(String stem, String terminaison) {
        //print("ste : \(stem) - term \(terminaison)")
        String spanMot1 = "<span class=\"motLatin\">";
        String spanMot2 = "</span>";

        String spanTxt1 = "<span class=\"termCoul\">";
        String spanTxt2 = "</span>";

        if (stem.equals("zzz") || stem.equals("")) {
            return "";
        }
        if (stem.equals(".")) {
            return spanMot1 + terminaison + spanMot2;
        }
        if (terminaison.equals("")) {
            return spanMot1 + stem + spanMot2;
        }
        //String res = spanMot1 + stem + spanMot2  + spanTxt1 + terminaison + spanTxt2;
        return spanMot1 + stem + spanMot2  + spanTxt1 + terminaison + spanTxt2;
    }

    // ============================== RECHERCHE ================================

    // ============================== met en forme pour recherche intertexte ================================

    ArrayList<ResultatRecherche> rechercheForme(String motRech , boolean formeSansSuffixe, String suffixe) {
        String motRecherche = motRech;
        int optionCasse = 0; // on cherche le mot avec 1ere lettre maj ou min
        String l1 = motRech.substring(0,1);
        if (l1.equals("?")) {
            motRecherche = motRecherche.toLowerCase().substring(1,motRecherche.length());
        }
        else {
            if (l1.equals(l1.toLowerCase())) {
                optionCasse = 1; // avec 1ere lettre min
            }
            else {
                optionCasse = 2; // avec 1ere lettre maj
            }
        }
        motRecherche = motRecherche.replace("æ", "ae");
        motRecherche = motRecherche.replace("œ", "oe");
        motRecherche = motRecherche.replace("j", "i");
        motRecherche = motRecherche.replace("v", "u");
        motRecherche = motRecherche.replace("nb", "mb");
        motRecherche = motRecherche.replace("np", "mp");

        ArrayList<ResultatRecherche> resultatsFinal = new ArrayList<ResultatRecherche>();


        //======== recherche si forme du verbe etre sans radical
        ArrayList<ResultatRecherche> estEtre = verifieFormeEtre(motRecherche);
        if (estEtre.size() > 0) {
            //return estEtre // pas la peine d'aller plus loin
            for (ResultatRecherche f : estEtre) {
                resultatsFinal.add(f);
            }

        }

        if (motRecherche.equals("uis") || motRecherche.equals("uult") || motRecherche.equals("uultis")) {
            ResultatRecherche resultatTemp = new ResultatRecherche();
            Desinence desr = new Desinence();
            ArrayList essai = db.rechercheLemmeSpecial("volo","V");
            EnregDico motvolo = new EnregDico();
            for (int i = 0; i < essai.size(); i++) {
                int resess = (int) essai.get(i);
                EnregDico essvolo = db.rechercheEntreeDico(resess);
                if (essvolo.decl1 == 6) {motvolo = essvolo;}
            }

            desr.decl1 = 6;
            desr.freq = "A";
            desr.mode = "IND";
            desr.nombre = "S";
            desr.POS = "V";
            desr.temps = "PRES";
            desr.voix = "ACTIVE";

            if (motRecherche.equals("uult")) {
                desr.personne = "3";
                resultatTemp.mot = motvolo;
                resultatTemp.desinence = desr;
                resultatsFinal.add(resultatTemp);
                return resultatsFinal;
            }

            if (motRecherche.equals("uis")) {
                desr.personne = "2";
                resultatTemp.mot = motvolo;
                resultatTemp.desinence = desr;
                resultatsFinal.add(resultatTemp);
                //return resultatsFinal - non il y a d'autres possibilites
            }

            if (motRecherche.equals("uultis")) {
                desr.personne = "2";
                desr.nombre = "P";
                resultatTemp.mot = motvolo;
                resultatTemp.desinence = desr;
                resultatsFinal.add(resultatTemp);
                //return resultatsFinal - non il y a d'autres possibilites
            }

        }

        if (motRecherche.equals("coepere")) {
            ResultatRecherche resultatTemp = new ResultatRecherche();
            Desinence desr = new Desinence();
            ArrayList essai = db.rechercheLemmeSpecial("coepi","V");
            EnregDico motcoepi = new EnregDico();
            for (int i = 0; i < essai.size(); i++) {
                int resess = (int) essai.get(i);
                motcoepi = db.rechercheEntreeDico(resess);
            }


            desr.decl1 = 3;
            desr.freq = "A";
            desr.mode = "INF";
            desr.nombre = "S";
            desr.POS = "V";
            desr.temps = "PRES";
            desr.voix = "ACTIVE";

            desr.personne = "0";
            resultatTemp.mot = motcoepi;
            resultatTemp.desinence = desr;
            resultatsFinal.add(resultatTemp);
            return resultatsFinal;

        }


        if (motRecherche.equals("mauis") || motRecherche.equals("mauult") || motRecherche.equals("mauultis")) {
            ResultatRecherche resultatTemp = new ResultatRecherche();
            Desinence desr = new Desinence();
            ArrayList essai = db.rechercheLemmeSpecial("malo","V");
            EnregDico motmalo = new EnregDico();
            for (int i = 0; i < essai.size(); i++) {
                int resess = (int) essai.get(i);
                EnregDico essmalo = db.rechercheEntreeDico(resess);
                if (essmalo.decl1 == 6) {motmalo = essmalo;}
            }

            desr.decl1 = 6;
            desr.freq = "A";
            desr.mode = "IND";
            desr.nombre = "S";
            desr.POS = "V";
            desr.temps = "PRES";
            desr.voix = "ACTIVE";

            if (motRecherche.equals("mauult")) {
                desr.personne = "3";
                resultatTemp.mot = motmalo;
                resultatTemp.desinence = desr;
                resultatsFinal.add(resultatTemp);
                return resultatsFinal;
            }

            if (motRecherche.equals("mauis")) {
                desr.personne = "2";
                resultatTemp.mot = motmalo;
                resultatTemp.desinence = desr;
                resultatsFinal.add(resultatTemp);
                //return resultatsFinal - non il y a d'autres possibilites
            }
            if (motRecherche.equals("mauultis")) {
                desr.personne = "2";
                desr.nombre = "P";
                resultatTemp.mot = motmalo;
                resultatTemp.desinence = desr;
                resultatsFinal.add(resultatTemp);
                //return resultatsFinal - non il y a d'autres possibilites
            }
        }

        ArrayList listeDesPossibles = trouveDesinencesPossibles(motRecherche);
        ArrayList<EnregDico> ListeDesLemmesResultats = trouveLemmesPossibles(motRecherche, listeDesPossibles, optionCasse);
        ArrayList listeDesinences = tableDesinences(ListeDesLemmesResultats, listeDesPossibles, motRecherche);

        //debugListeEnregDico(ListeDesLemmesResultats);

        int ind = 0;
        while (ind < ListeDesLemmesResultats.size()) {
            EnregDico lemmeT = ListeDesLemmesResultats.get(ind);
            ResultatRecherche resultatTemp = new ResultatRecherche();

            boolean aj = false;
            if (lemmeT.stem1.equals(motRecherche)) {

                Desinence desT = new Desinence();
                List<String> listePosProv = Arrays.asList("ADVSUP","C_N","C_V","COMME","ENCLIT","EXPR","N_COMPOS","ONOM","PARTICULE","PREFIX","SEMIDEP","VF","VOIR","VREF");

                if (listePosProv.contains(lemmeT.POS)) {
                    resultatTemp.mot = lemmeT;
                    resultatTemp.desinence = desT;
                    aj = true;
                }
                if (lemmeT.POS.contains("VOIR") || lemmeT.POS.contains("FORM")) {
                    resultatTemp.mot = lemmeT;
                    resultatTemp.desinence = desT;
                    aj = true;
                }

                if (lemmeT.POS.equals("PREP") || lemmeT.POS.equals("INTERJ") || lemmeT.POS.equals("CONJ")) {
                    resultatTemp.mot = lemmeT;
                    resultatTemp.desinence = desT;
                    aj = true;
                }

                if (lemmeT.POS.equals("NUM") && lemmeT.decl1 == 2) { // num indeclinables
                    desT.type = ""; //"CARD"
                    resultatTemp.mot = lemmeT;
                    resultatTemp.desinence = desT;
                    aj = true;
                }

                if (lemmeT.POS.equals("N") && lemmeT.decl1 >= 9) { // nom indeclinables
                    desT.type = "";
                    resultatTemp.mot = lemmeT;
                    resultatTemp.desinence = desT;
                    aj = true;
                }

                if (lemmeT.POS.equals("ADJ") && lemmeT.decl1 >= 9) {
                    resultatTemp.mot = lemmeT ;//yo
                    resultatTemp.desinence = desT;
                    aj = true;
                }

                if (lemmeT.POS.equals("PRON") && lemmeT.decl1 == 0) { // indeclinables
                    resultatTemp.mot = lemmeT;
                    resultatTemp.desinence = desT;
                    aj = true;
                }

                if (lemmeT.POS.equals("ADV")) {
                    if (motRecherche.equals(lemmeT.stem1)) {
                        desT.type = "";//"POS"
                        resultatTemp.mot = lemmeT; //yo

                        resultatTemp.desinence = desT;
                        resultatTemp.desinence.POS = "ADV";
                        resultatTemp.desinence.type = "POS";
                        resultatTemp.desinence.freq = "A";

                        aj = true;
                    }
                    if (motRecherche.equals(lemmeT.stem2)) {
                        desT.type = "COMP";
                        resultatTemp.mot = lemmeT; //yo
                        resultatTemp.desinence = desT;
                        resultatTemp.desinence.POS = "ADV";
                        resultatTemp.desinence.type = "COMP";
                        resultatTemp.desinence.freq = "A";
                        aj = true;
                    }
                    if (motRecherche.equals(lemmeT.stem3)) {
                        desT.type = "SUP";
                        resultatTemp.mot = lemmeT; //yo
                        resultatTemp.desinence = desT;
                        resultatTemp.desinence.POS = "ADV";
                        resultatTemp.desinence.type = "SUPER";
                        resultatTemp.desinence.freq = "A";
                        aj = true;
                    }

                }
                if (aj) {
                    resultatsFinal.add(resultatTemp);
                    //listeDesinences.remove(at: ind)
                    //ListeDesListesDeLemmesResultats.remove(at: ind)
                }

            }// lemmeT.mot == motRecherche

            if (!aj) {
                ArrayList des = (ArrayList) listeDesinences.get(ind);
                ArrayList<Desinence> desinencesResultats = rechercheDesinencesPourLemme(des, lemmeT,false);

                //debugListeDesinences(desinencesResultats);

                boolean estDecl2Exacte = false;
                ArrayList<Desinence> listeMeilleuresDes = new ArrayList<Desinence>();
                for (Desinence enregDes : desinencesResultats) {

                    if (compareLemmeTrouveAvecDesinencePossible(motRecherche, lemmeT, enregDes, formeSansSuffixe, suffixe,optionCasse)) {

                        if (lemmeT.decl2 == enregDes.decl2) {
                            if (!estDecl2Exacte) {
                                estDecl2Exacte = true;
                                listeMeilleuresDes.clear();
                            }
                            listeMeilleuresDes.add(enregDes.copie());
                        }
                        else {
                            if (!estDecl2Exacte) {
                                listeMeilleuresDes.add(enregDes.copie());
                            }
                        }
                    }
                    else {
                        //Log.d("TABULA Flexions2343"," lemme : " + lemmeT.debug() + " des : " + enregDes.afficheDesinence());
                    }
                }

                for (Desinence meilleureDes : listeMeilleuresDes) {
                    ResultatRecherche resultatTemp2 = new ResultatRecherche();
                    resultatTemp2.mot = lemmeT; //yo
                    resultatTemp2.desinence = meilleureDes;
                    resultatsFinal.add(resultatTemp2);
                }
            }

            ind += 1;

        } // fin boucle while


        for (int i = 0; i < resultatsFinal.size(); i++) {
            if (resultatsFinal.get(i).mot.refs.equals("X")) {
                ArrayList<EnregDico> motsT = db.rechercheEntreeDicoContientRefs(resultatsFinal.get(i).mot.refVar);
                if (motsT.size() > 0) {
                    for (EnregDico m : motsT) {
                        ResultatRecherche resR = new ResultatRecherche();
                        resR.mot = m;
                        resR.desinence = resultatsFinal.get(i).desinence;
                        resultatsFinal.add(resR);
                    }

                    resultatsFinal.remove(i);
                }
            }
        }

        //debugListeResultatsFinal(resultatsFinal);

        // ================ suffixes divers
        if (resultatsFinal.size() == 0 && !formeSansSuffixe) {
            List<String> liste1Suff = Arrays.asList("que","ve","cum");
            // separer enclitiques des suffixes de pronoms ??

            String suff = "";
            for (String suffT : liste1Suff) {
                if (motRecherche.endsWith(suffT)) {
                    suff = suffT;
                    int indSint = motRecherche.length() - suff.length();
                    //let indS = motRecherche.characters.index(motRecherche.characters.startIndex, offsetBy: indSint)
                    //motRecherche.characters.startIndex.advancedBy(indSint)
                    String lemmePosS = motRecherche.substring(0,indSint);
                    ArrayList<ResultatRecherche> rechercheSansSuff = rechercheForme(lemmePosS, true, suff);
                    if (rechercheSansSuff.size() > 0) {
                        boolean ajSuff = true;
                        for (ResultatRecherche elem : rechercheSansSuff) {
                            //print(elem)
                            if (elem.mot.mot.endsWith(suff)) {
                                resultatsFinal.add(elem);
                                ajSuff = false;
                            }
                            else {
                                resultatsFinal.add(elem);
                                ajSuff = true;
                            }
                        }
                        if (ajSuff) {
                            resultatsFinal.add(ajoutDuSuffixeAuxResultats(suff));
                        }

                    }
                }
            }
            //,"ne" traite a part car cas des noms 3e decl avec radical en n
            suff = "ne";
            if (motRecherche.endsWith(suff)) {

                int indSint = motRecherche.length() - 2;
                //let indS = motRecherche.characters.index(motRecherche.characters.startIndex, offsetBy: indSint)
                String lemmePosS = motRecherche.substring(0,indSint);
                ArrayList<ResultatRecherche> rechercheSansSuff = rechercheForme(lemmePosS, true, suff);
                if (rechercheSansSuff.size() > 0) {
                    boolean elemposs = false;
                    for (ResultatRecherche elem : rechercheSansSuff) {
                        //print(elem)
                        if (elem.mot.POS.equals("N") && elem.mot.decl1 == 3) {

                        }
                        else {
                            resultatsFinal.add(elem);
                            elemposs = true;
                        }

                    }
                    if (elemposs) {
                        resultatsFinal.add(ajoutDuSuffixeAuxResultats(suff));
                    }

                }
            }

            List<String> listeSuffPron = Arrays.asList("quam","dam","piam","nam","dem","libet","uis");
            for (String suffT : listeSuffPron) {
                if (motRecherche.endsWith(suffT)) {
                    suff = suffT;

                    int indSint = motRecherche.length() - suff.length();
                    //let indS = motRecherche.characters.index(motRecherche.characters.startIndex, offsetBy: indSint)
                    String lemmePosS = motRecherche.substring(0,indSint);
                    ArrayList<ResultatRecherche> rechercheSansSuff = rechercheForme(lemmePosS, true, suff);
                    if (rechercheSansSuff.size() > 0) {
                        for (ResultatRecherche elem : rechercheSansSuff) {
                            // rechercher si le pronom avec suffixe existe tel quel ????

                            //================================
                            if (elem.mot.POS.equals("PRON")) {
                                //print("elem \(elem.mot.mot) - suff \(suff)")
                                if (suff.equals("uis")) {
                                    if (elem.mot.mot.endsWith("vis") && !elem.mot.mot.equals("quis") && suff.equals("uis")) {
                                        resultatsFinal.add(elem);
                                    }
                                }
                                else {
                                    if (elem.mot.mot.endsWith(suff)) {
                                        resultatsFinal.add(elem);
                                    }
                                }
                            }
                        }
                        //resultatsFinal.append(ajoutDuSuffixeAuxResultats(suff)) -> c'est pas un suffixe
                    }
                }
            }
        }

        for (int i = 0; i < resultatsFinal.size(); i++) {
            String def = "";
            if (resultatsFinal.get(i).mot.dico.equals("G")) {
                def = bdDics.rechercheDefinition_pour_entree(resultatsFinal.get(i).mot,"mot");
            }
            else {
                def = bdDicsInternes.rechercheDefinition_pour_entree(resultatsFinal.get(i).mot,"mot");
            }

            //def = ""
            resultatsFinal.get(i).mot.def = def;
        }

        return resultatsFinal;
    }

    ArrayList trouveDesinencesPossibles(String motRecherche) {
        ArrayList res = new ArrayList();
        for (int lg = 0; lg < 8; lg++) {
            int indInt = motRecherche.length() - lg;
            if (indInt < 0) {
                break;
            }

            //let index = motRecherche.characters.index(motRecherche.characters.startIndex, offsetBy: indInt)
            String desPossible = motRecherche.substring(indInt,motRecherche.length());
            String lemmePossible = motRecherche.substring(0,indInt);
            boolean isDesPossible = isDesinencePossible(desPossible);
            //print("-\(lemmePossible)-\(desPossible)-\(isDesPossible)")
            if (isDesPossible) {
                if (!lemmePossible.isEmpty()) {
                    res.add(desPossible);
                }
            }
        }
        return res;
    }

    ArrayList<EnregDico> trouveLemmesPossibles(String motRecherche, ArrayList listeDesPossibles, int optionCasse) {
        ArrayList<EnregDico> ListeDesLemmesResultats = new ArrayList<EnregDico>();

        boolean isDesPossible = true;
        ArrayList listeLemmesProv = new ArrayList();
        for (int lg = 0; lg < 8; lg++) {
            int indInt = motRecherche.length() - lg;
            if (indInt < 0) {
                break;
            }
            //let index = motRecherche.characters.index(motRecherche.characters.startIndex, offsetBy: indInt)
            // let index = motRecherche.characters.startIndex.advancedBy(indInt)
            String lemmePossible = motRecherche.substring(0,indInt);

            String desPossible = motRecherche.substring(indInt,motRecherche.length());
            isDesPossible = listeDesPossibles.contains(desPossible);

            if (isDesPossible) {
                if (!lemmePossible.isEmpty()) {
                    listeLemmesProv.add(lemmePossible);
                }
            }
        }
        ListeDesLemmesResultats = db.rechercheListeLemmeDsTable(listeLemmesProv, optionCasse);

        return ListeDesLemmesResultats;
    }

    // pour chaque enregDict trouve, cree une lilte de desinences possibles
    ArrayList tableDesinences(ArrayList<EnregDico> enregs , ArrayList listeDesPossibles, String motRecherche) {
        ArrayList res = new ArrayList();

        String motRechT = motRecherche.toLowerCase();
        for (EnregDico enr : enregs) {
            ArrayList resT = new ArrayList();
            int longueur = -1;
            String des = "";
            boolean test = false;

            longueur = motRecherche.length() - enr.stem1.length();
            if (longueur >= 0) {
                des = motRecherche.substring(enr.stem1.length(),motRecherche.length());
                //recupereNdernieresLettresDuMot(mot: motRecherche, nLettres: long)
                //Log.d("yo","flex 2593 " + enr.stem1.toLowerCase() + des + " = " + motRechT + " - " + String.valueOf(listeDesPossibles.contains(des)) + " - " + String.valueOf(resT.contains(des)));
                if ((enr.stem1.toLowerCase() + des).equals(motRechT) && listeDesPossibles.contains(des) && !resT.contains(des)) {
                    resT.add(des);
                    test = true;
                }
            }

            longueur = motRecherche.length() - enr.stem2.length();
            if (longueur >= 0) {
                des = motRecherche.substring(enr.stem2.length(),motRecherche.length());

                if ((enr.stem2.toLowerCase() + des).equals(motRechT) && listeDesPossibles.contains(des) && !resT.contains(des)) {
                    resT.add(des);
                    test = true;
                }
            }

            longueur = motRecherche.length() - enr.stem3.length();
            if (longueur >= 0) {
                des = motRecherche.substring(enr.stem3.length(),motRecherche.length());
                if ((enr.stem3.toLowerCase() + des).equals(motRechT) && listeDesPossibles.contains(des) && !resT.contains(des)) {
                    resT.add(des);
                    test = true;
                }
            }

            longueur = motRecherche.length() - enr.stem4.length();
            if (longueur >= 0) {
                des = motRecherche.substring(enr.stem4.length(),motRecherche.length());
                if ((enr.stem4.toLowerCase() + des).equals(motRechT) && listeDesPossibles.contains(des) && !resT.contains(des)) {
                    resT.add(des);
                    test = true;
                }
            }

            if (test) {
                res.add(resT);
            }
            else {
                res.add(new ArrayList());
            }

        }

        return res;
    }

    boolean isDesinencePossible(String desPossible) {
        boolean isDesPossible = true;
        if (desPossible.isEmpty()) {return true;}
        //a b d (1 lettre) e f i j(jus) l m o r s t u
        //  let premieresLettres = ["a","b", "d", "e", "f", "i", "j", "l","m", "o","r" ,"s", "t", "u"]
        List<String> premieresLettres = Arrays.asList("c","g", "h", "k", "n", "p", "q", "v","w", "x","y" ,"z");

        // d'abord faire une liste des lemmes en coupant de 0 a 7 caracteres (7 = longueur max des declinaisons)

        String premLettre = desPossible.substring(0,1);
        isDesPossible = true;


        for (String l : premieresLettres) {
            if (premLettre.equals(l)) {
                return false;
            }
        }

        if (desPossible.length() == 7) {
            if (premLettre.equals("a")) {
                if (desPossible.startsWith("an") || desPossible.startsWith("ab") || desPossible.startsWith("ar")) {
                    return true;
                }
            }
            if (premLettre.equals("e")) {
                if (desPossible.startsWith("eb") || desPossible.startsWith("en") || desPossible.startsWith("er")  || desPossible.startsWith("es")) {
                    return true;
                }

            }
            if (premLettre.equals("f")) {
                if (desPossible.equals("foremus") || desPossible.equals("foretis")) {
                    return true;
                }
            }
            if (premLettre.equals("i")) {
                if (desPossible.startsWith("ir") || desPossible.startsWith("iss")) {
                    return true;
                }
            }
            if (premLettre.equals("u")) {
                if (desPossible.startsWith("und") || desPossible.startsWith("unt")) {
                    return true;
                }
            }

            return false;
        }
        if (desPossible.length() == 6) {
            if (premLettre.equals("a") || premLettre.equals("b") || premLettre.equals("e") || premLettre.equals("f") || premLettre.equals("i") || premLettre.equals("o") || premLettre.equals("r") || premLettre.equals("u")) {
                return true;
            }
            return false;
        }

        if (premLettre.equals("d")) {
            if (desPossible.length() == 1) {
                return true;
            }
            else {return false;}
        }



        if (premLettre.equals("j")) {
            if (desPossible.equals("jus")) {
                return true;
            }
            else {return false;}
        }

        return isDesPossible;
    }

    ArrayList<Desinence> rechercheDesinencesPourLemme(ArrayList des, EnregDico lemme, boolean ToutesFormes) {
        if (lemme.POS.equals("PRON") && lemme.decl1 == 1) {
            return desinencesPRON_1(des, lemme);
        }

        return db.rechercheDesinences(des, lemme);

    }

    boolean compareLemmeTrouveAvecDesinencePossible(String formeCherchee, EnregDico lemme, Desinence desinence, boolean formeSansSuffixe,
                                                    String suffixe, int optionDeCasse) {

        if (!desinence.freq.equals("A") && !desinence.freq.equals("A1") && !desinence.freq.equals("B")) {
            return false; // ???
        }

        if (!lemme.POS.equals(desinence.POS)) {
            if ((lemme.POS.equals("V") && desinence.POS.equals("VPAR"))) {

            }
            else {
                return false;
            }
        }

        if (lemme.decl1 != desinence.decl1) {
            if (desinence.decl1 != 0) {
                return false;
            }
        }

        EnregDico nomEnreg = lemme.copie();
        int bonRad = desinence.numRad;
        if (lemme.POS.equals("VPAR")) {
            bonRad = 1; // ????????????
        }

        String listeStems[] = {nomEnreg.mot, nomEnreg.stem1, nomEnreg.stem2, nomEnreg.stem3, nomEnreg.stem4};
        String essaiForme = listeStems[bonRad] + desinence.term;
        boolean testAvecCasse = essaiForme.toLowerCase().equals(formeCherchee.toLowerCase());
        //Log.d("==","numR " + String.valueOf(bonRad) + "-" + listeStems[0] + "-" + listeStems[1] + "-" + listeStems[2]);
        //Log.d("==","test " + essaiForme + "-" + formeCherchee);
        //if (optionDeCasse == 0) {} OK
        if (optionDeCasse == 1) {
            if (!lemme.mot.substring(0,1).equals(lemme.mot.substring(0,1).toLowerCase())) {
                testAvecCasse = false;
            }
        }
        if (optionDeCasse == 2) {
            if (!lemme.mot.substring(0,1).equals(lemme.mot.substring(0,1).toUpperCase())) {
                testAvecCasse = false;
            }
        }
        if (!testAvecCasse) {return false;}

        if (nomEnreg.POS.equals("N")) {

            if (!desinence.freq.equals("A")) {
                if (desinence.freq.equals("B") && desinence.decl1 == 3 && desinence.decl2 == 3) {

                }
                else {
                    return false;
                }
            }
            if (db.correspondGenre(nomEnreg.genre, desinence.genre)) {
                if (nomEnreg.decl2 == 10 && desinence.nombre.equals("P")) { // 3 10 seult NP
                    return false;
                }
                if (nomEnreg.decl2 == desinence.decl2 || desinence.decl2 == 0) {
                    return true;
                }
                else {
                    if (nomEnreg.decl1 == 2 && nomEnreg.decl2 >= 6) {
                        return true;
                        // ??
                    }
                    if (nomEnreg.decl2 >= 9) {
                        return true;
                        // tres peu de mots 3 9 ou 3 10 : d'origine grecque
                    }
                }

            }
            return false;
        }

        if (nomEnreg.POS.equals("ADJ")) {
//Log.d("TABULA Flex2740","compare des pos " + desinence.afficheDesinence());
            if (essaiForme.toLowerCase().equals(formeCherchee.toLowerCase())) {
                if (desinence.type.equals("COMP") || desinence.type.equals("SUPER")) {
                    return true;
                }
                if (nomEnreg.decl2 == desinence.decl2 || desinence.decl2 == 0) {
                    return true;
                }
            }
            return false;
        }

        if (nomEnreg.POS.equals("PRON")) {

            if (!formeSansSuffixe) {
                if (nomEnreg.mot.endsWith("dem") && !formeCherchee.endsWith("dem")) {return false;}
                if (nomEnreg.mot.endsWith("dam") && !formeCherchee.endsWith("dam")) {return false;}
                if (nomEnreg.mot.endsWith("quam") && !formeCherchee.endsWith("quam")) {return false;}
                if (nomEnreg.mot.endsWith("nam") && !formeCherchee.endsWith("nam")) {return false;}
                if (nomEnreg.mot.endsWith("piam") && !formeCherchee.endsWith("piam")) {return false;}
                if (nomEnreg.mot.endsWith("libet") && !formeCherchee.endsWith("libet")) {return false;}
                if (nomEnreg.mot.endsWith("que") && !formeCherchee.endsWith("que")) {return false;}
                if (nomEnreg.mot.endsWith("vis") && !formeCherchee.endsWith("uis")) {return false;}
                if (nomEnreg.mot.endsWith("ne") && !formeCherchee.endsWith("ne")) {return false;}
                //["quam","dam","piam","nam"]
            }
            if (formeSansSuffixe) {
                if (suffixe.equals("cum") || suffixe.equals("ne")) { // && suffixe != "que" ??
                    String[] suffs = {"quam","dam","piam","nam","dem","libet","vis"};
                    for (String s : suffs) {
                        if (nomEnreg.mot.endsWith(s)) {
                            return false;
                        }
                    }
                }

            }

            if (nomEnreg.decl2 == desinence.decl2 || desinence.decl2 == 0) {
                return true;
            }
            return false;
        }

        if (nomEnreg.POS.equals("NUM")) {
            if (desinence.type.equals("ORD") || desinence.type.equals("DIST")) {
                if (lemme.type.equals(desinence.type)) {
                    return true;
                }
                //return true
            }
            else {
                if (desinence.nombre.equals("S")) {return true;}
                else {
                    if (nomEnreg.decl2 == desinence.decl2 || desinence.decl2 == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        if (lemme.POS.equals("VPAR")) {
            boolean bool1 = (desinence.voix.equals("PASSIVE") && desinence.temps.equals("PERF"));
            boolean bool2 = (desinence.voix.equals("ACTIVE") && desinence.temps.equals("FUT"));

            if (bool1 || bool2) {
                return true;
            }

            if (nomEnreg.decl2 == desinence.decl2 || desinence.decl2 == 0) {
                return true;
            }
            return false;
        }


        if (nomEnreg.POS.equals("V")) {
            if (desinence.POS.equals("VPAR")) {
                boolean bool01 = (desinence.voix.equals("PASSIVE") && desinence.temps.equals("PERF"));
                boolean bool02 = (desinence.voix.equals("ACTIVE") && desinence.temps.equals("FUT"));
                boolean bool03 = (desinence.voix.equals("ACTIVE") && nomEnreg.type.equals("DEP"));
                if (bool01 || bool02 || bool03) {return true;}
            }

            if (desinence.voix.equals("ACTIVE") && nomEnreg.type.equals("DEP")) {return false;}
            boolean bool1 = (desinence.temps.equals("PERF") && desinence.voix.equals("ACTIVE") && desinence.mode.equals("IND") && desinence.decl1 < 7);
            boolean bool2 = (desinence.temps.equals("PLUP") && desinence.voix.equals("ACTIVE") && desinence.mode.equals("IND"));
            boolean bool3 = (desinence.temps.equals("FUTP") && desinence.voix.equals("ACTIVE") && desinence.mode.equals("IND") && desinence.decl1 < 8);
            boolean bool4 = (desinence.temps.equals("PERF") && desinence.voix.equals("ACTIVE") && desinence.mode.equals("SUB") && desinence.decl1 < 8);
            boolean bool5 = (desinence.temps.equals("PLUP") && desinence.voix.equals("ACTIVE") && desinence.mode.equals("SUB") && desinence.decl1 < 8);
            if (bool1 || bool2 || bool3 || bool4 || bool5) {return true;}

            if (nomEnreg.decl1 == 5) {
                if (desinence.mode.equals("IND")) {
                    return true;
                }
                if (desinence.mode.equals("SUB")) {
                    if (desinence.temps.equals("PRES")) { return true; }
                }
                if (desinence.mode.equals("IMP")) { return true;} // ??????????
                if (desinence.mode.equals("INF")) {
                    if (desinence.temps.equals("FUT")) {
                        return true;
                    }
                }

            }
            if (nomEnreg.decl1 == 6) {
                if (desinence.voix.equals("PASSIVE")) {
                    return true;
                }
                if (desinence.mode.equals("IND")) {
                    if (desinence.temps.equals("PRES")) { return true; }
                }
            }
            // cas des deponents ou decl voix active ??
            if (nomEnreg.decl2 == desinence.decl2 || desinence.decl2 == 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    ResultatRecherche ajoutDuSuffixeAuxResultats(String suffixe) {
        ArrayList<EnregDico> ent = rechercheEntreesDsBase(suffixe,"","motsimple");
        ResultatRecherche res = new ResultatRecherche();
        if (ent.size() > 0) {
            res.mot = ent.get(0);
        }

        return res;
    }

    public List<ResultatFTS> rechercheEnregParDefinition(String motRecherche, String optionDic) {

        List<ResultatFTS> res = new ArrayList<>();

        if (optionDic.equals("P")) {
            res = bdDicsInternes.rechercheEnregParDefinition(motRecherche);
        }
        if (optionDic.equals("G")) {
            res = bdDics.rechercheEnregParDefinition(motRecherche);
        }
        if (optionDic.equals("PG")) {
            res = bdDicsInternes.rechercheEnregParDefinition(motRecherche);
            List<ResultatFTS> res2 = bdDics.rechercheEnregParDefinition(motRecherche);
            if (res2.size() > 0) {
                for (ResultatFTS r2 : res2) {
                    res.add(r2);
                }
            }
        }
        if (optionDic.equals("GP")) {
            res = bdDics.rechercheEnregParDefinition(motRecherche);
            List<ResultatFTS> res2 = bdDicsInternes.rechercheEnregParDefinition(motRecherche);
            if (res2.size() > 0) {
                for (ResultatFTS r2 : res2) {
                    res.add(r2);
                }
            }
        }
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
                enrT.mot.def = enteteDict(enr.mot.dico) + enr.mot.def; //+ "</BR>"

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

                    if (res.get(i).mot.signatureEnreg().equals(sign) && egaliteListeDesinences(res.get(i).desinences, enr.desinences)) {
                        if (!enr.mot.dico.equals(res.get(i).mot.dico)) {

                            res.get(i).mot.def += enteteDict(enr.mot.dico) + enr.mot.def;
                            res.get(i).mot.dico = enr.mot.dico;

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

    boolean egaliteListeDesinences(ArrayList<Desinence> des1, ArrayList<Desinence> des2) {
        if (des1.size() != des2.size()) {
            return false;
        }
        if (des1.size() == 0) {
            //return false
        }
        for (int i = 0; i < des1.size(); i++) {
            if (!des1.get(i).egaliteDesinence(des2.get(i))) {
                return false;
            }
        }

        return true;
    }

    ArrayList<Desinence> desinencesPRON_1(ArrayList terminaisons, EnregDico entree) {

        List<String> grQui = Arrays.asList("qui","quinam","ecqui","quivis","quilibet","quidam");
        List<String> grQuis = Arrays.asList("quis","quisque","quispiam");
        List<String> grEcquis = Arrays.asList("ecquis","quisnam");
        List<String> grAliqui = Arrays.asList("aliqui","aliquicumque","aliquilibet");
        List<String> grAliquis = Arrays.asList("aliquis","aliquispiam","aliquisvis");
        // quicumque
        //quisquam

        ArrayList<Desinence> res = new ArrayList<Desinence>();
        Desinence des = new Desinence();
        des.POS = "PRON";
        for (int i = 0; i < terminaisons.size(); i++) {
            // qui quinam ecqui aliqui quivis quilibet quidam quicumque aliquicumque aliquilibet NOM M S 1 1 : i
            String term = (String) terminaisons.get(i);
            if (term.equals("i")) {
                des.cas = "NOM";
                des.genre = "M";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "i";
                des.freq = "A";
                des.age = "X";
                des.type = "ADJ"; // ?

                if (grQui.contains(entree.mot) || grAliqui.contains(entree.mot) || entree.mot.equals("quicumque")) {
                    res.add(des);
                }
                Desinence des2 = des.copie();
                des2.numRad = 2;
                des2.cas = "DAT";
                res.add(des2);

                Desinence des3 = des.copie();
                des3.cas = "NOM";
                des3.nombre = "P";
                res.add(des3);
            }

            //quis quisnam ecquis quisquam quispiam quisque aliquis aliquispiam aliquisvis NOM M S 1 2 : is
            //quisnam ecquis NOM F (-> M) S 1 2 : is
            if (term.equals("is")) {
                des.cas = "NOM";
                des.genre = "M";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "is";
                des.freq = "A";
                des.age = "X";
                des.type = "PRON"; // ?

                if (grQuis.contains(entree.mot) || grAliquis.contains(entree.mot) || grEcquis.contains(entree.mot)) {
                    res.add(des);
                }
                if (grEcquis.contains(entree.mot)) {
                    Desinence des2 = des.copie();
                    des2.genre = "F";
                    res.add(des2);
                }
            }
            //aliqui aliquicumque aliquilibet aliquis aliquispiam aliquisvis NOM F S 1 3 : a
            //(variante)
            //qui quinam ecqui quivis quilibet quidam NOM F S 1 3 : a
            if (term.equals("a")) {
                des.cas = "NOM";
                des.genre = "F";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "a";
                des.freq = "A";
                des.age = "X";
                des.type = "PRON"; // ?

                if (grAliquis.contains(entree.mot) || grAliqui.contains(entree.mot)) {
                    res.add(des);
                }
                if (grQui.contains(entree.mot)) {
                    res.add(des);
                }
                //aliqui aliquicumque aliquilibet ABL F S 1 3 : a
                //qui quinam ecqui quivis quilibet quidam quicumque ABL F S 1 4 : a
                //quis quisnam ecquis quisquam quispiam quisque aliquis aliquispiam aliquisvis ABL F S 1 3 : a
                Desinence des2 = des.copie();
                des2.cas = "ABL";
                if (grAliquis.contains(entree.mot) || grAliqui.contains(entree.mot) || entree.mot.equals("quisquam")) {
                    res.add(des2);
                }
                if (grQuis.contains(entree.mot) || grQui.contains(entree.mot) || grEcquis.contains(entree.mot) || entree.mot.equals("quisquam")) {
                    res.add(des2);
                }

                //aliqui aliquicumque aliquilibet aliquis aliquispiam aliquisvis NOM N P 1 9 : a
                //aliqui aliquicumque aliquilibet aliquis aliquispiam aliquisvis ACC N P 1 9 : a
                Desinence des3 = des.copie();
                des3.cas = "NOM";
                des3.genre = "N";
                des3.nombre = "P";
                if (grAliquis.contains(entree.mot) || grAliqui.contains(entree.mot)) {
                    res.add(des3);
                }
                Desinence des4 = des3.copie();
                des4.cas = "ACC";
                if (grAliquis.contains(entree.mot) || grAliqui.contains(entree.mot)) {
                    res.add(des4);
                }
            }
            //qui quinam ecqui quivis quilibet quidam quicumque quis quisquam quispiam quisque NOM F S 1 4 : ae
            //qui quinam ecqui quicumque quis quisnam ecquis quispiam NOM N P 1 8 : ae "æ"
            //qui quinam ecqui quicumque quis quisnam ecquis quispiam ACC N P 1 8 : ae
            if (term.equals("ae")) {
                des.cas = "NOM";
                des.genre = "F";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "ae";
                des.freq = "A";
                des.age = "X";
                des.type = "PRON"; // ?

                if (grQuis.contains(entree.mot) || grQui.contains(entree.mot)) {
                    res.add(des);
                }
                Desinence des2 = des.copie();
                des2.nombre = "P";
                des2.genre = "N";
                if (grQuis.contains(entree.mot) || grQui.contains(entree.mot) || grEcquis.contains(entree.mot) || entree.mot.equals("quisquam")) {
                    res.add(des2);
                }
                Desinence des3 = des2.copie();
                des3.cas = "ACC";
                if (grQuis.contains(entree.mot) || grQui.contains(entree.mot) || grEcquis.contains(entree.mot) || entree.mot.equals("quisquam")) {
                    res.add(des3);
                }
                Desinence des4 = des2.copie();
                des4.genre = "F";
                //des2.nombre = "P"
                res.add(des4);
            }
            if (term.equals("æ")) {
                des.cas = "NOM";
                des.genre = "F";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "æ";
                des.freq = "A";
                des.age = "X";
                des.type = "PRON"; // ?

                if (grQuis.contains(entree.mot) || grQui.contains(entree.mot)) {
                    res.add(des);
                }
                Desinence des2 = des.copie();
                des2.nombre = "P";
                des2.genre = "N";
                if (grQuis.contains(entree.mot) || grQui.contains(entree.mot) || grEcquis.contains(entree.mot) || entree.mot.equals("quisquam")) {
                    res.add(des2);
                }
                Desinence des3 = des2.copie();
                des3.cas = "ACC";
                if (grQuis.contains(entree.mot) || grQui.contains(entree.mot) || grEcquis.contains(entree.mot) || entree.mot.equals("quisquam")) {
                    res.add(des3);
                }
                Desinence des4 = des2.copie();
                des4.genre = "F";
                //des2.nombre = "P"
                res.add(des4);
            }
            //quis quisnam ecquis quisquam aliquis aliquispiam aliquisvis NOM N S 1 6 : id
            //quis quisnam ecquis quisquam aliquis aliquispiam aliquisvis ACC N S 1 6 : id
            //quicumque quinam NOM N S 1 6 : id
            //quicumque quinam ACC N S 1 6 : id

            //(variante pron)
            //quispiam quisque quivis quilibet quidam NOM N S 1 6 : id
            //quispiam quisque quivis quilibet quidam ACC N S 1 6 : id
            if (term.equals("id")) {
                des.cas = "NOM";
                des.genre = "N";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "id";
                des.freq = "A";
                des.age = "X";
                des.type = "PRON"; // ?

                if (grQuis.contains(entree.mot) || grAliquis.contains(entree.mot) || grEcquis.contains(entree.mot) || entree.mot.equals("quicumque") || entree.mot.equals("quisquam")) {
                    res.add(des);
                }
                Desinence des2 = des.copie();
                des2.cas = "ACC";
                if (grQuis.contains(entree.mot) || grAliquis.contains(entree.mot) || grEcquis.contains(entree.mot) || entree.mot.equals("quicumque") || entree.mot.equals("quisquam")) {
                    res.add(des2);
                }
            }
            //(variante adj)
            //quispiam quisque quivis quilibet quidam NOM N S 1 7 : od
            //quispiam quisque quivis quilibet quidam ACC N S 1 7 : od

            //qui ecqui aliqui aliquicumque aliquilibet NOM N S 1 7 : od
            if (term.equals("od")) {
                des.cas = "NOM";
                des.genre = "N";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "od";
                des.freq = "A";
                des.age = "X";
                des.type = "ADJ"; // ?

                if (grQuis.contains(entree.mot) || grEcquis.contains(entree.mot) || entree.mot.equals("quisquam")) {
                    res.add(des);
                }
                if (grAliquis.contains(entree.mot) || grAliqui.contains(entree.mot)) {
                    res.add(des);
                }
            }

            if (term.equals("ius")) {
                des.cas = "GEN";
                des.genre = "X";
                des.nombre = "S";
                des.numRad = 2;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "ius";
                des.freq = "A";
                des.age = "X";
                des.type = ""; // ?
                res.add(des);
            }
            if (term.equals("ibus")) {
                des.cas = "DAT";
                des.genre = "X";
                des.nombre = "P";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "ibus";
                des.freq = "A";
                des.age = "X";
                des.type = "";// ?
                res.add(des);

                Desinence des2 = des.copie();
                des2.cas = "ABL";
                res.add(des2);
            }
            if (term.equals("o")) {
                des.cas = "ABL";
                des.genre = "M";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "o";
                des.freq = "A";
                des.age = "X";
                des.type = "";// ?
                res.add(des);

                Desinence des2 = des.copie();
                des2.genre = "N";
                res.add(des2);
            }
            if (term.equals("orum")) {
                des.cas = "GEN";
                des.genre = "M";
                des.nombre = "P";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "orum";
                des.freq = "A";
                des.age = "X";
                des.type = ""; // ?
                res.add(des);

                Desinence des2 = des.copie();
                des2.genre = "N";
                res.add(des2);
            }
            if (term.equals("arum")) {
                des.cas = "GEN";
                des.genre = "F";
                des.nombre = "P";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "orum";
                des.freq = "A";
                des.age = "X";
                des.type = ""; // ?
                res.add(des);
            }
            if (term.equals("am")) {
                des.cas = "ACC";
                des.genre = "F";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "am";
                des.freq = "A";
                des.age = "X";
                des.type = ""; // ?
                res.add(des);
            }
            if (term.equals("em")) {
                des.cas = "ACC";
                des.genre = "M";
                des.nombre = "S";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "em";
                des.freq = "A";
                des.age = "X";
                des.type = ""; // ?
                res.add(des);

                //quisnam ecquis ACC F (-> M) : em ??
                if (grEcquis.contains(entree.mot)) {
                    Desinence des2 = des.copie();
                    des2.genre = "F";
                    res.add(des2);
                }
            }
            if (term.equals("as")) {
                des.cas = "ACC";
                des.genre = "F";
                des.nombre = "P";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "as";
                des.freq = "A";
                des.age = "X";
                des.type = ""; // ?
                res.add(des);
            }
            if (term.equals("os")) {
                des.cas = "ACC";
                des.genre = "M";
                des.nombre = "P";
                des.numRad = 1;
                des.decl1 = 1;
                des.decl2 = 0;
                des.term = "as";
                des.freq = "A";
                des.age = "X";
                des.type = ""; // ?
                res.add(des);
            }
        }
        return res;

    }

    ArrayList<ResultatRecherche> verifieFormeEtre(String formeCherchee) {
        ArrayList<ResultatRecherche> listeres = new ArrayList<ResultatRecherche>();
        ResultatRecherche res = new ResultatRecherche();
        String SumPres[] = {"sum","es","est","sumus","estis","sunt"};
        String SumFut[] = {"ero","eris","erit","erimus","eritis","erunt"};
        String SumImp[] = {"eram","eras","erat","eramus","eratis","erant"};
        String ImperatifPres[] = {"es","este"};
        String ImperatifFut[] = {"esto","esto","estote","sunto"};
        String SumSubjImp[] = {"essem","esses","esset","essemus","essetis","essent"};
        String SumSubjImpVar[] = {"forem","fores","foret","","","forent"};
        // let SumSubjPres = ["sim","sis","sit","simus","sitis","sint"]

        for (int i = 0; i < SumPres.length; i++) {
            if (SumPres[i].equals(formeCherchee)) {
                res.mot = enregEtre("P");
                if (i < 3) {
                    res.desinence.nombre = "S";
                    res.desinence.personne = String.format("%d",i + 1);
                }
                else {
                    res.desinence.nombre = "P";
                    res.desinence.personne = String.format("%d",i -2);
                }
                res.desinence.age = "X";
                res.desinence.freq = "A";
                res.desinence.decl1 = 5;
                res.desinence.decl2 = 1;
                res.desinence.mode = "IND";
                res.desinence.voix = "ACTIVE";
                res.desinence.temps = "PRES";

                listeres.add(res);
            }

        }

        for (int i = 0; i < SumFut.length; i++) {
            if (SumFut[i].equals(formeCherchee)) {
                res.mot = enregEtre("P");
                if (i < 3) {
                    res.desinence.nombre = "S";
                    res.desinence.personne = String.format("%d",i + 1);
                }
                else {
                    res.desinence.nombre = "P";
                    res.desinence.personne = String.format("%d",i -2);
                }
                res.desinence.age = "X";
                res.desinence.freq = "A";
                res.desinence.decl1 = 5;
                res.desinence.decl2 = 1;
                res.desinence.mode = "IND";
                res.desinence.voix = "ACTIVE";
                res.desinence.temps = "FUT";

                listeres.add(res);
            }

        }

        for (int i = 0; i < SumImp.length; i++) {
            if (SumImp[i].equals(formeCherchee)) {
                res.mot = enregEtre("P");
                if (i < 3) {
                    res.desinence.nombre = "S";
                    res.desinence.personne = String.format("%d",i + 1);
                }
                else {
                    res.desinence.nombre = "P";
                    res.desinence.personne = String.format("%d",i -2);
                }
                res.desinence.age = "X";
                res.desinence.freq = "A";
                res.desinence.decl1 = 5;
                res.desinence.decl2 = 1;
                res.desinence.mode = "IND";
                res.desinence.voix = "ACTIVE";
                res.desinence.temps = "IMPF";

                listeres.add(res);
            }

        }
        for (int i = 0; i < SumSubjImp.length; i++) {
            if (SumSubjImp[i].equals(formeCherchee)) {
                res.mot = enregEtre("P");
                if (i < 3) {
                    res.desinence.nombre = "S";
                    res.desinence.personne = String.format("%d",i + 1);
                }
                else {
                    res.desinence.nombre = "P";
                    res.desinence.personne = String.format("%d",i -2);
                }
                res.desinence.age = "X";
                res.desinence.freq = "A";
                res.desinence.decl1 = 5;
                res.desinence.decl2 = 1;
                res.desinence.mode = "SUB";
                res.desinence.voix = "ACTIVE";
                res.desinence.temps = "IMPF";

                listeres.add(res);
            }

        }
        for (int i = 0; i < SumSubjImpVar.length; i++) {
            if (SumSubjImpVar[i].equals(formeCherchee)) {
                res.mot = enregEtre("P");
                if (i < 3) {
                    res.desinence.nombre = "S";
                    res.desinence.personne = String.format("%d",i + 1);
                }
                else {
                    res.desinence.nombre = "P";
                    res.desinence.personne = String.format("%d",i -2);
                }
                res.desinence.age = "X";
                res.desinence.freq = "A";
                res.desinence.decl1 = 5;
                res.desinence.decl2 = 1;
                res.desinence.mode = "SUB";
                res.desinence.voix = "ACTIVE";
                res.desinence.temps = "IMPF";

                listeres.add(res);
            }

        }
        for (int i = 0; i < ImperatifPres.length; i++) {
            if (ImperatifPres[i].equals(formeCherchee)) {
                res.mot = enregEtre("P");
                if (i == 0) {
                    res.desinence.nombre = "S";
                    res.desinence.personne = "2";
                }
                else {
                    res.desinence.nombre = "P";
                    res.desinence.personne = "2";
                }
                res.desinence.age = "X";
                res.desinence.freq = "A";
                res.desinence.decl1 = 5;
                res.desinence.decl2 = 1;
                res.desinence.mode = "IMP";
                res.desinence.voix = "ACTIVE";
                res.desinence.temps = "PRES";

                listeres.add(res);
            }

        }
        for (int i = 0; i < ImperatifFut.length; i++) {
            if (ImperatifFut[i].equals(formeCherchee)) {
                res.mot = enregEtre("P");
                if (i < 2) {
                    res.desinence.nombre = "S";
                    res.desinence.personne = String.format("%d",i + 2);
                }
                else {
                    res.desinence.nombre = "P";
                    res.desinence.personne = String.format("%d",i + 3);
                }
                res.desinence.age = "X";
                res.desinence.freq = "A";
                res.desinence.decl1 = 5;
                res.desinence.decl2 = 1;
                res.desinence.mode = "IMP";
                res.desinence.voix = "ACTIVE";
                res.desinence.temps = "FUT";

                listeres.add(res);
            }

        }

        if (formeCherchee.equals("esse")) {
            res.mot = enregEtre("P");
            res.desinence.nombre = "S";
            res.desinence.personne = "0";
            res.desinence.age = "X";
            res.desinence.freq = "A";
            res.desinence.decl1 = 5;
            res.desinence.decl2 = 1;
            res.desinence.mode = "INF";
            res.desinence.voix = "ACTIVE";
            res.desinence.temps = "PRES";

            listeres.add(res);

        }
        return listeres;
    }

    private void debugListeEnregDico(ArrayList<EnregDico> liste) {
        for (EnregDico enr : liste) {
            Log.d("TABULA Flexions","debuglisteres : " + enr.debug());
        }
    }
    private void debugListeResultatsFinal(ArrayList<ResultatRecherche> liste) {
        for (ResultatRecherche r : liste) {
            Log.d("TABULA Flexions","debuglisteresFinale : " + r.mot.debug() + " des : " + r.desinence.afficheDesinence());

        }
    }
    private void debugListeDesinences(ArrayList<Desinence> liste) {
        for (Desinence r : liste) {
            Log.d("TABULA Flexions","debuglisteDes : " + r.afficheDesinence());

        }
        if (liste.size() == 0) {
            Log.d("TABULA Flexions","debuglisteDes : vide ...");
        }
    }

}
