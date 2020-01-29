package com.verbole.dcad.tabula;

/**
 * Created by dcad on 3/7/16.
 */
public class EnregDico {
    int Num = -10;

    String mot = "";
    String stem1 = "";
    String stem2 ="";
    String stem3 = "";
    String stem4 = "";
    String POS = "";
    String nombre = "";
    String genre = "";
    String type = "";
    int decl1 = 0;
    int decl2 = 0;
    String AgeFreq = "";
    String def = "";
    String refs = "";
    int refVar = 0;
    String motOrig = "";
    String dico = "";
    int refPeigne = -1;


    EnregDico copie() {
        EnregDico res = new EnregDico();
        res.AgeFreq = this.AgeFreq;
        res.decl1 = this.decl1;
        res.decl2 = this.decl2;
        res.def = this.def;
        res.genre = this.genre;
        res.mot = this.mot;
        res.nombre = this.nombre;
        res.stem1 = this.stem1;
        res.stem2 = this.stem2;
        res.stem3 = this.stem3;
        res.stem4 = this.stem4;
        res.POS = this.POS;
        res.type = this.type;
        res.refs = this.refs;
        res.refVar = this.refVar;
        res.Num = this.Num;
        res.motOrig = this.motOrig;
        res.dico = this.dico;
        res.refPeigne = this.refPeigne;
        return res;
    }

    String signatureEnreg() {
        String signPOS = this.POS;
        if (this.POS.equals("NP") || this.POS.equals("NPP")) {
            signPOS = "N";
        }

        String res = signPOS + this.stem1 + this.stem2 + this.stem3 + this.stem4 + this.nombre + this.genre;
        //String res = this.POS + this.stem1 + this.stem2 + this.stem3 + this.stem4 + this.nombre + this.genre; // enreg.type
        res += String.valueOf(this.decl1) + String.valueOf(this.decl2);

        if (this.decl1 == 0 && this.decl2 == 0) { res = this.POS + this.mot; }

        if (this.POS.equals("PREP") || this.POS.equals("ADV") || this.POS.equals("INTERJ") || this.POS.equals("CONJ")) {
            signPOS = "INV";
            res = signPOS + this.mot;
        }
        if (this.decl1 >= 9 && this.decl2 >= 9) {
            res = this.POS + this.mot;
        }

        if (this.POS.equals("ADJ") || this.POS.equals("ADJP") || this.POS.equals("VPAR") || this.POS.equals("VPAR_ADJ")) {
            signPOS = "ADJ";
            res = signPOS + this.mot + String.valueOf(this.decl1) + String.valueOf(this.decl2); //String(self.decl1) + String(self.decl2);
        }

        return res;
    }
    /*
    func signatureEnreg()->String {
        var signPOS = self.POS
        var res = signPOS + self.stem1 + self.stem2 + self.stem3 + self.stem4 + self.nombre + self.genre // enreg.type
        res += String(self.decl1) + String(self.decl2)

        if (self.POS == "PREP" || self.POS == "ADV" || self.POS == "INTERJ" || self.POS == "CONJ") {
            signPOS = "INV"
            res = signPOS + self.mot
        }

        if (self.decl1 >= 9 && self.decl2 >= 9) {
            res = signPOS + self.mot
        }
        if (self.POS == "ADJ" || self.POS == "VPAR") {
            signPOS = "INV"
            res = signPOS + self.mot
        }

        return res
    }
     */

    String debug() {
        String res = this.mot + " " + this.POS + " " + this.decl1 + "-" + this.decl2;
        return res;
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

    // ? ADJ ADJFORM ADV ADVSUP COMME COMME_N COMME_V CONJ ENCLIT EXPR INTERJ N NCOMPOS NFORM
    // NP NPP NUM NUMFORM ONOM PARTICULE PREFIX PREP PRON PRONFORM V VFORM VOIR VOIR_ADJ VOIR_N VOIR_PRON
    // VOIR_V VPAR VPAR_ADJ VPARFORM

    String tr_POS() {
        String resF = "";
        switch (POS) {
            case "N":
                resF = "nom";
                break;
            case "NP":
                resF = "nom";
                break;
            case "NPP":
                resF = "nom";
                break;
            case "V":
                resF = "verbe";
                break;
            case "ADJ":
                resF = "adjectif";
                break;
            case "ADJP":
                resF = "adjectif";
                break;
            case "ADV":
                resF = "adverbe";
                break;
            case "ADVP":
                resF = "adverbe";
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
            case "VPAR_ADJ":
                resF = "participe";
                break;
            case "CONJ":
                resF = "conjonction";
                break;
            case "INTERJ":
                resF = "interjection";
                break;
            case "SUFF":
                resF = "suffixe";
                break;
            case "enclitique":
                resF = "enclitique";
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
                    resF = "" ;// ?????????????
                }
                if (type.equals("X")) {
                    resF = "" ;// ?????????????
                }
                break;
            case "PRON":

                if (type.equals("REL")) {
                    resF = "relatif";
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
                if (type.equals("REFLEX")) {
                    resF = "réflexif";
                }
                if (type.equals("POS")) {
                    resF = ""; // ?????????????
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

                if (type.equals("DAT")) {
                    resF = "" ;// ?????????????
                }
                if (type.equals("FREQ")) {
                    resF = "" ;
                }

                if (type.equals("X")) {
                    resF = "" ;// ?????????????
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

            case "PREP":
                resF = tr_Cas(type);
                break;

            default:
                break;
        }
        return resF;

    }

    String tr_Cas(String cas) {
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

    EnregDico metEnFormeMot() {
        EnregDico res = this.copie();
        res.decl1 = decl1;
        res.POS = POS;
        res.genre = tr_Genre();
        res.type = tr_Type();
        res.POS = tr_POS(); // !!!

        res.AgeFreq = AgeFreq;
        res.decl2 = decl2;
        res.def = def;
        res.mot = mot;
        res.nombre = nombre;
        res.refs = refs;
        res.refVar = refVar;
        //..... inutile
        return res;
    }

    String donneGroupeFlex() {

        String res = "";
        if (POS.equals("N")) {
            if (decl1 == 1 ){ //&& decl2 == "1"
                if (decl2 < 6) {
                    return "1ère déclinaison";
                }
                else {
                    return "nom grec";
                }

            }

            if (decl1 == 2){  // && decl2 == "1"
                return "2ème déclinaison";
            }

            if (decl1 == 3){  // && decl2 == "1"
                return "3ème déclinaison";
            }
            if (decl1 == 4){  // && decl2 == "1"
                return "4ème déclinaison";
            }
            if (decl1 == 5){  // && decl2 == "1"
                return "5ème déclinaison";
            }
        }

        if (POS.equals("ADJ")) {
            if (decl1 == 1 ){ //&& decl2 == "1"
                return "1ère et 2ème déclinaison";
            }

            //  if (decl1 == 2){  // && decl2 == 1
            //      return "2ème déclinaison"
            //  }

            if (decl1 == 3){  // && decl2 == "1"
                return "3ème déclinaison";
            }
            if (decl1 == 4){  // && decl2 == "1"
                return "4ème déclinaison";
            }
        }

        if (POS.equals("V")) {
            if (decl1 == 1 ){ //&& decl2 == "1"
                return "1ère conjugaison";
            }

            if (decl1 == 2){  // && decl2 == "1"
                return "2ème conjugaison";
            }

            if (decl1 == 3 && decl2 == 1){
                return "3ème conjugaison";
            }

            if (decl1 == 3 && decl2 == 2){
                return "irrégulier";
            }
            if (decl1 == 3 && decl2 == 3){
                return "irrégulier";
            }

            if (decl1 == 3 && decl2 == 4){
                return "4ème conjugaison";
            }

            if (decl1 == 5){
                return "dérivé de être";
            }

            if (decl1 >= 6){
                return "irrégulier";
            }
        }
        return res;
    }

    void changeDiphtongues() {
        if (motOrig.equals("Phæmonoe") || motOrig.equals("Zoelæ")) {
            mot = mot.replace("ae", "æ");
            motOrig = motOrig.replace("ae", "æ");
            stem1 = stem1.replace("ae", "æ");
            stem2 = stem2.replace("ae", "æ");
            stem3 = stem3.replace("ae", "æ");
            stem4 = stem4.replace("ae", "æ");
        }
        else {
            mot = ChangeDiphtongue.changeDiphtongue(mot);
            motOrig = ChangeDiphtongue.changeDiphtongue(motOrig);
            stem1 = ChangeDiphtongue.changeDiphtongue(stem1);
            stem2 = ChangeDiphtongue.changeDiphtongue(stem2);
            stem3 = ChangeDiphtongue.changeDiphtongue(stem3);
            stem4 = ChangeDiphtongue.changeDiphtongue( stem4);
        }
    }


}



/*
        struct ResultatRecherche {
        var mot : enregDico
        var desinence : Desinence

        init() {
        self.mot = enregDico()
        self.desinence = Desinence()
        }
        }

        struct ResultatPropre {
        var mot : enregDico
        var desinences : [Desinence]
        var comment : String

        init() {
        self.mot = enregDico()
        self.desinences = []
        self.comment = ""
        }
        }*/