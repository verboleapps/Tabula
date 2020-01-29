package com.verbole.dcad.tabula;

import java.util.ArrayList;

/**
 * Created by dcad on 3/7/16.
 */
public class Desinence {

    String POS = "";
    int decl1 = 0;
    int decl2 = 0;
    String temps = "";
    String voix = "";
    String mode = "";
    String personne = "";
    String cas = "";
    String nombre = "";
    String genre = "";
    int numRad = 0;
    String term = "";
    String type = "";
    String age = "";
    String freq = "";

    void maz() {
        POS = "";
        decl1 = 0;
        decl2 = 0;
        temps = "";
        voix = "";
        mode = "";
        personne = "";
        cas = "";
        nombre = "";
        genre = "";
        numRad = 0;
        term = "";
        type = "";
        age = "";
        freq = "";
    }

    Desinence copie() {
        Desinence res = new Desinence();
        res.POS = this.POS;
        res.decl1 = this.decl1;
        res.decl2 = this.decl2;
        res.temps = this.temps;
        res.voix = this.voix;
        res.mode = this.mode;
        res.personne = this.personne;
        res.cas = this.cas;
        res.nombre = this.nombre;
        res.genre = this.genre;
        res.numRad = this.numRad;
        res.term = this.term;
        res.type = this.type;
        res.age = this.age;
        res.freq = this.freq;

        return res;
    }

    String tr_type_NUM() {
        String res = "";
        /*
        if (numRad == 1) {
            res = "";
        }
        if (numRad == 2) {
            res = "(ordinal)";
        }
        if (numRad == 3) {
            res = "(distributif)";
        }
        if (numRad == 4) {
            res = "";
        }
        */
        return res;
    }

    String tr_Cas() {
        String casF = "";
        switch (cas) {
            case "NOM":
                casF = "nominatif";
                break;

            case "ACC":
                casF = "accusatif";
                break;
            case "ABL":
                casF = "ablatif";
                break;
            case "GEN":
                casF = "génitif";
                break;

            case "VOC":
                casF = "vocatif";
                break;
            case "DAT":
                casF = "datif";
                break;
            case "LOC":
                casF = "locatif";
                break;

            case "ACC,ABL":
                casF = "accusatif - ablatif";
                break;

            case "GEN,ABL":
                casF = "génitif - ablatif";
                break;

            default:
                break;
        }
        return casF;
    }

    String tr_Personne() {
        String resF = "";
        switch (personne) {
            case "1":
                resF = "1ère personne";
                break;

            case "2":
                resF = "2ème personne";
                break;
            case "3":
                resF = "3ème personne";
                break;

            default:
                break;
        }
        return resF;
    }

    String tr_Nombre() {
        String resF = "";
        switch (nombre) {
            case "S":
                resF = "singulier";
                break;

            case "P":
                resF = "pluriel";
                break;

            default:
                break;
        }

        return resF;
    }

    String tr_Mode() {
        String resF = "";
        switch (mode) {
            case "IND":
                resF = "Indicatif";
                break;

            case "SUB":
                resF = "Subjonctif";
                break;
            case "IMP":
                resF = "Impératif";
                break;
            case "INF":
                resF = "Infinitif";
                break;

            default:
                break;
        }

        return resF;
    }

    String tr_Temps() {
        String resF = "";
        switch (temps) {
            case "PRES":
                resF = "présent";
                break;

            case "IMPF":
                resF = "imparfait";
                break;
            case "FUT":
                resF = "futur";
                break;
            case "PERF":
                resF = "parfait";
                break;

            case "PLUP":
                resF = "plus-que-parfait";
                break;
            case "FUTP":
                resF = "futur antérieur";
                break;
            case "ADJVB":
                resF = "(adjectif verbal)";
                break;
            default:
                break;
        }
        return resF;
    }

    String tr_Genre() {
        String resF = "";
        switch (genre) {
            case "M":
                resF = "masculin";
                break;

            case "F":
                resF = "féminin";
                break;
            case "N":
                resF = "neutre";
                break;
            case "C":
                resF = "masculin-féminin";
                break;
            case "X":
                resF = "";
                break;
            default:
                break;
        }
        return resF;

    }

    String tr_POS() {
        String resF = "";
        switch (POS) {
            case "N":
                resF = "nom";
                break;
            case "V":
                resF = "verbe";
                break;
            case "ADJ":
                resF = "adjectif";
                break;

            case "PRON":
                resF = "pronom";
                break;
            case "NUM":
                resF = "numéral";
                break;

            case "PREP":
                resF = "préposition";
                break;
            case "VPAR":
                resF = "participe";
                break;
            case "CONJ":
                resF = "conjonction";
                break;
            case "INTERJ":
                resF = "interjection";
                break;
            case "enclitique":
                resF = "enclitique";
                break;

            default:
                break;
        }
        return resF;
    }

    String tr_Voix() {
        String resF = "";

        switch (voix) {
            case "ACTIVE":
                if (POS.equals("V")) {
                    resF = "active";
                }
                if (POS.equals("VPAR")) {
                    resF = "actif";
                }
                break;

            case "PASSIVE":
                if (POS.equals("V")) {
                    resF = "passive";
                }
                if (POS.equals("VPAR")) {
                    resF = "passif";
                }
                break;

            default:
                break;
        }

        return resF;

    }

    String tr_Type() {
        String resF = type;
        switch (POS) {

            case "N":
                resF = "";
                break;

            case "ADJ":
                if (type.equals("COMP")) {
                    resF = "comparatif";
                }
                if (type.equals("POS")) {
                    //resF = "positif"
                    resF = "";
                }
                if (type.equals("SUPER")) {
                    resF = "superlatif";
                }

                if (type.equals("X")) {
                    resF = ""; // ?????????????
                }
                break;

            case "ADV":
                if (type.equals("COMP")) {
                    resF = "comparatif";
                }
                if (type.equals("POS")) {
                    resF = ""; //"positif"
                }
                if (type.equals("SUPER")) {
                    resF = "superlatif";
                }
                if (type.equals("X")) {
                    resF = ""; // ?????????????
                }
                break;

            case "NUM":
                if (type.equals("CARD")) {
                    resF = "cardinal";
                }
                if (type.equals("ORD")) {
                    resF = "ordinal";
                }
                if (type.equals("DIST")) {
                    resF = "distributif" ;
                }
                if (type.equals("X")) {
                    resF = "" ;// ?????????????
                }
                break;

            case "PRON":

                if (type.equals("REL")) {
                    resF = "relatif";
                }
                if (type.equals("REFLEX")) {
                    resF = "réfléchi";
                }
                if (type.equals("PERS")) {
                    resF = "personnel";
                }
                if (type.equals("INDEF")) {
                    resF = "indéfini";
                }
                if (type.equals("INTERR")) {
                    resF = "interrogatif"; // ?????????????
                }
                if (type.equals("ADJECT")) {
                    resF = "adjectif" ;// ?????????????
                }
                if (type.equals("DEMONS")) {
                    resF = "démonstratif"; // ?????????????
                }

                if (type.equals("X")) {
                    resF = ""; // ?????????????
                }
                break;

            case "V":
                if (type.equals("TRANS")) {
                    resF = "transitif";
                }
                if (type.equals("INTRANS")) {
                    resF = "intransitif";
                }
                if (type.equals("DEP")) {
                    resF = "déponent";
                }
                if (type.equals("SEMIDEP")) {
                    resF = "semi déponent";
                }

                if (type.equals("TO_BEING")) {
                    resF = "";
                }

                if (type.equals("X")) {
                    resF = "" ;// ?????????????
                }
                if (type.equals("DAT")) {
                    resF = ""; // ?????????????
                }
                if (type.equals("PERFDEF")) {
                    resF = "défectif" ;// ?????????????
                }

                if (type.equals("IMPERS")) {
                    resF = "impersonnel"; // ?????????????
                }
                break;

            case "VPAR":
                if (type.equals("PERF_PASSIVE")) {
                    resF = "parfait passif";
                }
                if (type.equals("PRES_ACTIVE")) {
                    resF = "présent actif";
                }
                break;

            default:
                break;
        }
        return resF;

    }

    void metEnFormeDesinence() {
        cas = tr_Cas();
        genre = tr_Genre();
        personne = tr_Personne();
        nombre = tr_Nombre();
        voix = tr_Voix();
        temps = tr_Temps();
        mode = tr_Mode();
        type = tr_Type();
        POS = tr_POS();

    }

    boolean egaliteDesinence(Desinence des2) {
    /*
    if (des1.age != des2.age) {
        return false
    }
 */
        if (!this.cas.equals(des2.cas)) {
            return false;
        }
        if (this.decl1 != des2.decl1) {
            return false;
        }
        if (this.decl2 != des2.decl2) {
            return false;
        }
    /*
    if (des1.freq != des2.freq) {
        return false
    }
 */
        if (!this.genre.equals(des2.genre)) {
            return false;
        }
        if (!this.mode.equals(des2.mode)) {
            return false;
        }
        if (!this.nombre.equals(des2.nombre)) {
            return false;
        }
        if (this.numRad != des2.numRad) {
            return false;
        }
        if (!this.personne.equals(des2.personne)) {
            return false;
        }
        if (!this.POS.equals(des2.POS)) {
            return false;
        }
        if (!this.temps.equals(des2.temps)) {
            return false;
        }
        if (!this.term.equals(des2.term)) {
            return false;
        }
        if (!this.type.equals(des2.type)) {
            return false;
        }
        if (!this.voix.equals(des2.voix)) {
            return false;
        }
        return true;
    }

    public static boolean egaliteListeDesinences(ArrayList<Desinence> des1, ArrayList<Desinence> des2) {
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

    String afficheDesinence() {
        String st = "";
        if (this.POS.equals("V") || this.POS.equals("verbe")) {
            st = this.POS + " - " + this.decl1 + " - " + this.decl2 + " - " + this.temps + " - " + this.voix + " - " + this.mode + " - " + this.personne + " - ";
            st +=  " - " + this.nombre + " - " + this.term;
        }
        if (this.POS.equals("N") || this.POS.equals("nom")) {
            st = this.POS + " - " + this.decl1 + " - " + this.decl2 + " - " + this.genre + " - " + this.cas + " - ";
            st += " - " + this.nombre + " - " + this.term+ " - " + this.freq;
        }
        if (this.POS.equals("ADJ") || this.POS.equals("adjectif")) {
            st = this.POS + " - " + this.decl1 + " - " + this.decl2 + " - " + this.genre + " - " + this.cas + " - " + this.nombre;
            st += " - " + this.type + " - " + this.term;
        }
        if (this.POS.equals("PRON") || this.POS.equals("pronom")) {
            st = this.POS + " - " + this.decl1 + " - " + this.decl2 + " - " + this.genre + " - " + this.cas + " - " + this.nombre;
            st += " - " +this.type + " - " + this.term;
        }
        if (this.POS.equals("VPAR") || this.POS.equals("participe")) {
            st = this.POS + " - " + this.decl1 + " - " + this.decl2 + " - " + this.temps + " - " + this.voix + " - " + this.mode;
            st += " - " + this.cas + " - " + this.nombre + " - " + this.term + " - " + this.numRad;
        }
        return st;
    }


}

