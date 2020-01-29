package com.verbole.dcad.tabula;

public class ChangeDiphtongue {
    /*
    static String changeDiphtongue(String txt ) {

        String res = txt;
        res = res.replaceAll("AE","Æ");
        // res = res.replaceAll("ae","æ");
        res = res.replaceAll( "OE", "Œ");
        if (res.length() > 2) {
            if (res.substring(0,2).equals("oe")) {
                String t = res.substring(2,res.length());
                res = "œ" + t;
            }
        }

        //  res = res.replacingOccurrences(of: "oe", with: "œ")
        return res;
    }
*/
    static boolean teste_AEOE(String mot) {
        boolean res = false;

        String[] liste = {"Acusagonoe","aedon","Aello","aena","aenatores","aeneator","aeneatus","aeneolus","aeneus", "aenipes","Aennum",
                "Aenobarbus","aenulum","aenum","aenus","aer","2 aereus","aereus 2","Aeria","Aerias","aeriensis","aerinus","aerius","aerivagus",
                "aerizusa","aeroides","aeromantia","aeromantis","Aerope","aerophobus","Aeropus","Aetion","aetites","Aetius","aetoma",
                "Alachroes","Alcathoe","Alcithoe","Alexirhoe","aloe","Aloeus","Ampeloessa","Anennoetus","Antinoeus","apoproegmena",
                "1 Arsinoe","2 Arsinoe","Arsinoeum","atizoe","Autonoe","Beroe","Boethius","Boethuntes","1 boethus","2 Boethus",
                "1 cacoethes","2 cacoethes","Callirhoe","Chloe","Chosdroena","coegi","coelectus","coelementatus","coemendatus",
                "coemitto","coemo","coemptio","coemptionalis","coemptionator","coemptor","coemptus","coeo","coepiscopatus","coepiscopus",
                "coepulonus","coepulor","coerceo","coercio","coercitio","coercitor","coercitus","coerctio","coerro","coertio",
                "coessentialis","coevangelista","coio","coiens","coex","coexcito","coexercitatus","coexercito","coexsisto","coexstinctus","coexsulo",
                "coexsulto","coextendo","contraeo","Cymothoe","Danae","dialoes","dodecaeteris","Donacoessa","Donoessa","Dosithoe",
                "duoetvicesimani","duoetvicesimus","ennaeteris","enneadecaeteris","enneaeteris","ennoematicus","Eunoe","euoe","evoe",
                "goetia","Hecaerge","Hexaemeron","hippophaes","hoe","Ichthyoessa","incoercitus","introeo","Iphinoe","Ismael","isoetes",
                "Israel","Israeliticus","Israelitis","Joel","Laerta","Laertiades","1 Laertius","2 Laertius","Leuconoe","Leucothoe",
                "Lysinoe","Lysithoe","Medoe","melanaetos","Meloessa","Meroe","Michael","Michaelium","Midoe","Moxoene","Noe","Noega",
                "Noemi","Noemon","noerus","Noeta","Noetus","octaedros","octaeteris","Ocyrhoe","oloes","Oloessa","Onocoetes","Osdroena",
                "Osroene","Oxyrhoe","Pasiphaeia","pentaetericus","pentaeteris","periboetos","Phaethon","Phaethontiades","Phaethusa",
                "Phemonoe","Pholoe","phthoe","poema","poematium","poesis","poeta","poetica","poetice","poeticus","poeto","poetor",
                "poetria","poetris","Prinoessa","proegmena","proemineo","Prothoenor","pseudoepiscopus","Ptoembani","Raphael","retroegi",
                "retroeo","Scioessa","semipoeta","subintroeo","Tauroentum","tetraeteris","Thelxinoe","Theonoe","theopnoe","Therothoes",
                "Tithoes","Troes","Troezene","Typhoeus","unaetvicesima legio","unaetvicesimani","Zoe","Zoellus"};

        //"Phæmonoe","Zoelæ" ??

        for (String m : liste) {
            if (mot.equals(m)) {
                res = true;
            }
        }

        return res;


    }

    static EnregDico changeDiphtonguesEnregDict(EnregDico enreg) {
        EnregDico res = enreg.copie();
        boolean test = teste_AEOE(res.motOrig);
        if (!test) {
            if (res.motOrig.equals("Phæmonoe") || res.motOrig.equals("Zoelæ")) {
                res.mot = res.mot.replace("ae", "æ");
                res.motOrig = res.motOrig.replace("ae", "æ");
                res.stem1 = res.stem1.replace("ae", "æ");
                res.stem2 = res.stem2.replace("ae", "æ");
                res.stem3 = res.stem3.replace("ae", "æ");
                res.stem4 = res.stem4.replace("ae", "æ");
            }
            else {
                res.mot = changeDiphtongue(res.mot);
                res.motOrig = changeDiphtongue(res.motOrig);
                res.stem1 = changeDiphtongue(res.stem1);
                res.stem2 = changeDiphtongue(res.stem2);
                res.stem3 = changeDiphtongue(res.stem3);
                res.stem4 = changeDiphtongue( res.stem4);
            }

        }

        return res;
    }

    static String changeDiphtonguesListe(String mot) {
        String res = mot;
        boolean test = teste_AEOE(mot);
        if (!test) {
            if (res.equals("Phaemonoe") || res.equals("Zoelae")) {
                res = res.replace("ae", "æ");
            }
            else {
                res = changeDiphtongue(mot);
            }
        }

        return res;
    }

    static String changeDiphtongueInverse(String txt) {

        String res = txt;
        res = res.replace("Æ", "AE");
        res = res.replace("æ", "ae");
        res = res.replace("Œ","OE");
        res = res.replace("œ","oe");
        /*
         if (recupereNpremieresLettresDuMot(mot: res, nLettres: 2) == "oe") {
         let t = enleveNpremieresLettresDuMot(mot: res, nLettres: 2)
         res = "œ" + t
         }
         */

        return res;
    }

    static String changeDiphtongue(String txt) {

        String res = txt;
        res = res.replace("AE", "Æ");
        res = res.replace("ae", "æ");
        res = res.replace("OE", "Œ");
        res = res.replace("oe", "œ");
        /*
        if (recupereNpremieresLettresDuMot(mot: res, nLettres: 2) == "oe") {
            let t = enleveNpremieresLettresDuMot(mot: res, nLettres: 2)
            res = "œ" + t
        }
         */

        return res;
    }
    static String changeDiphtongueSimple(String txt) {

        String res = txt;
        res = res.replace("AE", "Æ");
        //res = res.replacingOccurrences(of: "ae", with: "æ")
        res = res.replace("OE", "Œ");
        String l2 = res.substring(0,2);
        if (l2.equals("oe")) {
            String t = res.substring(2,res.length());//enleveNpremieresLettresDuMot(mot: res, nLettres: 2)
            res = "œ" + t;
        }
        //  res = res.replacingOccurrences(of: "oe", with: "œ")
        return res;
    }
}
