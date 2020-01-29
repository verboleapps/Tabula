package com.verbole.dcad.tabula;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dcad on 12/16/17.
 */

public class ParseGaffiot {


    String parseDebutFichier(String cheminFichier) {

        String version = "";
        //int ind = 0;
        int compteComm = 0;
        File file = new File(cheminFichier);
        try {
            BufferedReader br = new BufferedReader(
                    //new FileReader(file));
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(file)
                            )

                    )
            );
            String ligne = "";
            while (true) {
                if ((ligne = br.readLine()) != null) {
                    if (ligne.startsWith("%")) {
                        if (ligne.startsWith("% version")) {
                            version = ligne.replace("%","");
                            Log.d("","version ? :" + version);
                            break;
                        }
                        compteComm += 1;
                    }
                    //ind += 1;
                    if (!ligne.startsWith("%") && (compteComm > 0) && !ligne.isEmpty()) {
                        // fin en-tete
                        Log.d("","fin en-tete");
                        break;
                    }
                }
                else {
                    break;
                }
            }


        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        } finally {

        }
        return version;
    }
/*
    String parseDebutFichier2(String txtFichier) {
        String res = "";
        String version = "";
        String[] mystr = txtFichier.split("&");
        int ind = 0;
        int compteComm = 0;

        while (ind < mystr.length) {
            String ligne = mystr[ind];
            Log.d("","ligne : -" + ligne + "-");
            if (ligne.startsWith("%")) {
                if (ligne.startsWith("% version")) {
                    version = ligne.replace("%","");
                    Log.d("","version ? :" + version);
                    break;
                }
                compteComm += 1;
            }
            ind += 1;
            if (!ligne.startsWith("%") && (compteComm > 0) && !ligne.equals("")) {
                // fin en-tete
                Log.d("","fin en-tete");
                break;
            }

        }
        res = version;
        return res;
    }
*/
    String parseChamp(String champ, String champSuivant) {

        String nomChampSuivant = "";
        boolean ajouteEspace = true;
        if (champSuivant.contains("{")) {
            int indNomSuivant = champSuivant.indexOf("{");
            nomChampSuivant = champSuivant.substring(0,indNomSuivant);
        }
        if (nomChampSuivant.equals("up")) {
            ajouteEspace = false;
        }
        if (champSuivant.startsWith(".") || champSuivant.startsWith(")") || champSuivant.startsWith("]")) {
            ajouteEspace = false;
        }

        String res = champ;
        // print("parseChamp : \(champ)")
        //if (champ == "par") { // = paragraphe : avec \par\rub{ ... ou avec \par\F
        //    return ""
        //}
        if (res.contains("dixpc")) {
            res = res.replace( "dixpc", "dixpc{");
        }
        if (res.contains("arabe")) {
            res = res.replace( "arabe", "arabe{"); // 1 cas !!
        }

        if (res.contains("neufrm")) {
            //print("resorig : \(res)")
            res = res.replace( "neufrm [}", "[");
            res = res.replace( "neufrm ]}", "]");
            res = res.replace( "neufrm]}", "]");
            res = res.replace( "neufrm[}", "[");
            res = res.replace( "neufrm]", "]");
            res = res.replace( "{", "");
        }
        if (res.contains("{")) {
            int indNom = res.indexOf("{");
            String nomChamp = res.substring(0,indNom);

            String p1 = res.substring(indNom,res.length());
            String p2 = "";
            if (p1.contains("}")) {  //if (res.contains("}")) {
                int indFin1 = p1.indexOf("}");
                p2 = p1;
                p2 = p2.substring(indFin1,p2.length());
               // p2 = p2.substring(0,p2.length());
                p1 = p1.substring(0,indFin1);

                if (!p2.startsWith(" ") && !p1.endsWith(" ")) {
                    //p2 += " "


                }
                if (p2.isEmpty()) {
                    if (ajouteEspace) {
                        p2 = " ";
                    }

                }
                else {
                    if (!p2.endsWith(" ") && !p2.startsWith("(")) { // et startsWith(".") ??
                        if (ajouteEspace) {
                            p2 += " ";
                        }
                    }

                    if (p2.startsWith(" ") || p2.startsWith(".") || p2.startsWith(")") || p1.endsWith(" ")) {
                        ajouteEspace = false;
                    }

                    if (ajouteEspace) {
                        p2 = " " + p2;
                    }
                }
            }

            switch (nomChamp) {
                //case "arabe": break


                case "entree":
                    p1 = "<span class = \"entreeDic\">" + p1 + "</span>";
                    break;
                case "aut":
                    break;
                case "cl":
                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "etyml":
                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "oeuv":
                    p1 = "<em>" + p1 + "</em>";
                    break;

                case "refch":
                    if (ajouteEspace) {
                        p1 += " ";
                    }

                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "refchp":
                    if (ajouteEspace) {
                        p1 += " ";
                    }
                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "comm":
                    p1 = "";
                    break;
                case "ital":
                    p1 = "<em>" + p1 + "</em>";
                    break;

                case "F":
                    p1 = "&#8608;" + p1; // -> &#8594;
                    break;

                case "S":
                    p1 = "§" + p1;
                    break;

                case "par":
                    p1 = "<BR/>" + p1;
                    break;
                case "rub":
                    p1 = "<b>" + p1 + "</b>";
                    break;
                case "kkz":
                    p1 = "<b>" + p1 + "</b>";
                    break;
                case "pp":
                    p1 = "&para; <b>" + p1 + "</b>"; // &#182; : signe paragraphe
                    break;

                case "qq":
                    p1 = "<b>" + p1 + "</b>";
                    break;
                case "pc":
                    p1 = "<b>" + p1 + "</b>";
                    break;
                case "qqng":
                    //p1 = "</BR>  " + p1
                    break;
                case "lat":
                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "latc":
                    if (p1.contains(",")) {
                    /* non ...
                    let ms = p1.components(separatedBy: ",")
                    var pT = ""
                    for m in ms {
                        if (!m.contains("etc.")) {
                            pT += "<a href=\"voir://voir/\(m)\"> \(m)</a>, "
                        }
                        else {
                            pT += m + "  "
                        }
                    }
                    p1 = enleveNdernieresLettresDuMot(mot: pT, nLettres: 2)
                    */
                    }
                    else {
                        String p1T = p1.replace("æ","ae");
                        p1T = p1T.replace("œ", "oe");
                        p1 = "<a href=\"voir://voir/" + p1T + "\">" + p1 + "</a>";
                    }
                    break;
                case "latv":
                    if (p1.contains(",")) {
                    /*
                    let ms = p1.components(separatedBy: ",")
                    var pT = ""
                    for m in ms {
                        if (!m.contains("etc.")) {
                            pT += "<a href=\"voir://voir/\(m)\"> \(m)</a>, "
                        }
                        else {
                            pT += m + "  "
                        }
                    }
                    p1 = enleveNdernieresLettresDuMot(mot: pT, nLettres: 2)
                    */
                    }
                    else {

                        String p1T = p1.replace("æ","ae");
                        p1T = p1T.replace("œ", "oe");
                        p1 = "<a href=\"voir://voir/" + p1T + "\">" + p1 + "</a>";
                    }

                    break;

                case "up":
                    p1 = "<sup>" + p1 + "</sup>";
                    break;
                case "dixpc":
                    p1 = p1.toUpperCase();
                    break;
                default:
                    break;
            }

            res = p1 + p2;
        }

        res = res.replace("{","");
        res = res.replace("}","");
        res = res.replace("%","");

        return res;
    }

    String parseDefSimple(String def) {

        String defT = def;
        // let charsPb = ["\\raise", "\\overline", "\\rlap", "\\smash", "\\hyphenation", "\\kern", "\\hbox", "\\rlap", "\\up", "\\break", "\\desv"] // desv ????
        String pattern = "\\\\kern[0-9.-]+em\\.?";
        defT = defT.replaceAll(pattern,"");

        pattern = "\\\\raise-[0-9.-]+ex";
        defT = defT.replaceAll(pattern,"");

        pattern = "\\$\\\\overline\\{\\\\hbox\\{([|a-zA-Z]+)\\}\\}\\$";
        defT = defT.replaceAll(pattern,"<font style = \"text-decoration:overline\">$1</font>");

        defT = defT.replace("\\F","&#8594;");
        defT = defT.replace("\\S","§ ");
        defT = defT.replace("\\par","<BR/>");
        defT = defT.replace("\\break","");
        defT = defT.replace("---","&mdash;");
        defT = defT.replace("<BR/><BR/><BR/>","<BR/>");
        defT = defT.replace("<BR/><BR/>","<BR/>");


        StringBuilder res = new StringBuilder("");
        if (defT.contains("\\")) {
            String[] parties = defT.split("\\\\");
            for (int i = 0; i <parties.length; i++) {
                if (i < parties.length - 1) {
                    res.append(parseChamp(parties[i], parties[i + 1]));
                }
                else {
                    res.append(parseChamp(parties[i], ""));
                }
            }
            /*
            for p in parties {
                res += parseChamp(champ: p)
            }
            */
        }
        else {
            //print("debug : parseGaffiot l.230 \(def)")
            res = new StringBuilder(def); // ????
        }
        String resF = res.toString();

        resF = resF.replace("~", " ");
        resF = resF.replace( " .", ".");
        resF = resF.replace( " ,", ",");
        resF = resF.replace( "( ", "(");
        resF = resF.replace( " )", ")");

        return resF;
    }

    String epureChamp(String champ, boolean limite ) {
        //print("parsechamp radical : \(champ)")
        String res = champ;

        String resultat = "";
        String nomChamp = "";
        if (res.contains("{")) {
            int indNom = res.indexOf("{");
            nomChamp = res.substring(0, indNom);

            String p1 = res.substring(indNom,res.length());
            String p2 = "";
            if (p1.contains("}")) { // res.contains("}")
                int indFin1 = p1.indexOf("}");
                p2 = p1;
                p2 = p2.substring(indFin1,p2.length());
                //p2 = p2.substring(0,p2.length());
                p1 = p1.substring(0,indFin1);

            }

            switch (nomChamp) {

                case "aut":
                    return resultat;
                case "cl":
                    return resultat;
                /*
                case "etyml":
                    p1 = "";
                    break;
                    //return resultat;
                */
                case "oeuv":
                    return resultat;
                case "refch":
                    return resultat;
                case "refchp":
                    return resultat;
                case "comm":
                    return resultat;
/*
                case "entree":
                    //p1 = "<span class = \"entreeDic\">" + p1 + "</span>"
                    break;
                case "ital":
                    break;
                case "F":
                    break;
                case "S":
                    break;
                case "<BR>":
                    break;
                case "par":
                    break;
                case "rub":
                    break;
                case "pp":
                    break;
                case "qq":
                    break;
                case "gen":
                    break;
                case "es":
                    break;
                case "des":
                    break;
*/
                case "lat":
                    if (limite) {
                        return resultat;
                    }
                    p1 = "";
                    break;
                case "latc":
                    if (limite) {
                        return resultat;
                    }
                    p1 = "";
                    break;
                case "latv":
                    if (limite) {
                        return resultat;
                    }
                    p1 = "";
                    break;
                    /*
                case "up":
                    return resultat;
   */
                default:
                    p1 = "";
                    break;
            }

            res = p1 + p2;
        }
        res = res.replace( "{", "");
        res = res.replace( "}", "");
        resultat = res;
        return resultat;
    }

    String parseDefEpure(String def, boolean limite) {

        String res = "";
        String pattern;
        // enlever les crochets ?
        //pattern = "\\[[a-z0-9A-Z.]+\\]";
        //def = def.replaceAll(pattern,"");

        pattern = "[.,;'?!]";
        def = def.replaceAll(pattern," ");

        if (def.contains("\\")) {
            String[] parties = def.split( "\\\\");
            //var nomChampPrecedent = ""
            StringBuilder rt = new StringBuilder("");
            for (int i = 0; i < parties.length; i++) {
                //let resT = epureChamp(champ: parties[i])
                // if (nomChampPrecedent != "refch") {
                rt.append(epureChamp(parties[i],limite));
                //  }
                //nomChampPrecedent = resT.0
            }
            res = rt.toString();
        }

        return res;
    }

    String parseChampSimple2(String champ, String champSuivant) {

        String nomChampSuivant = "";
        boolean ajouteEspace = true;
        if (champSuivant.contains("{")) {
            int indNomSuivant = champSuivant.indexOf("{");
            nomChampSuivant = champSuivant.substring(0,indNomSuivant);
        }
        if (nomChampSuivant.equals("up")) {
            ajouteEspace = false;
        }
        if (champSuivant.startsWith(".") || champSuivant.startsWith(")") || champSuivant.startsWith("]")) {
            ajouteEspace = false;
        }

        String res = champ;
        if (res.contains("neufrm")) {
            //print("resorig : \(res)")
            res = res.replace( "neufrm [}", "[");
            res = res.replace( "neufrm ]}", "]");
            res = res.replace( "neufrm]}",  "]");
            res = res.replace( "neufrm[}",  "[");
            res = res.replace( "neufrm]",  "]");
            res = res.replace( "{", "");
        }
        if (res.contains("{")) {
            int indNom = res.indexOf("{");
            String nomChamp = res.substring(0,indNom);

            String p1 = res.substring(indNom,res.length());
            String p2 = "";
            if (res.contains("}")) {
                int indFin1 = p1.indexOf("}");
                p2 = p1;
                p2 = p2.substring(indFin1,p2.length());
                //p2 = p2.substring(0,p2.length());
                p1 = p1.substring(0,indFin1);

                if (!p2.startsWith(" ") && !p1.endsWith(" ")) {
                    //p2 += " "


                }
                if (p2.equals("")) {
                    if (ajouteEspace) {
                        p2 = " ";
                    }

                }
                else {
                    if (!p2.endsWith(" ") && !p2.startsWith("(")) { // et startsWith(".") ??
                        if (ajouteEspace) {
                            p2 += " ";
                        }
                    }

                    if (p2.startsWith(" ") || p2.startsWith(".") || p2.startsWith(")") || p1.endsWith(" ")) {
                        ajouteEspace = false;
                    }

                    if (ajouteEspace) {
                        p2 = " " + p2;
                    }
                }
            }

            switch (nomChamp) {

                case "entree":
                    break;
                case "aut":
                    break;
                case "cl":
                    break;
                case "etyml":
                    break;
                case "oeuv":
                    break;
                case "refch":
                    if (ajouteEspace) {
                        p1 += " ";
                    }
                    break;
                case "refchp":
                    if (ajouteEspace) {
                        p1 += " ";
                    }
                    break;
                case "comm":
                    p1 = "";
                    break;
                case "ital":
                    break;

                case "F":
                    p1 = "->" + p1;
                    break;

                case "S":
                    p1 = "§" + p1;
                    break;

                case "<BR>":
                    break;
                case "par":
                    break;
                case "rub":
                    break;
                case "kkz":
                    break;
                case "pp":
                    break;

                case "qq":
                    break;
                case "qqng":
                    break;
                case "lat":
                    break;
                case "latc":

                    break;
                case "latv":

                    break;

                case "up":
                    break;
                default:
                    break;
            }

            res = p1 + p2;
        }
        res = res.replace("{", "");
        res = res.replace("}", "");
        res = res.replace("%", "");
        return res;
    }

    // pour affichage liste a partir fullTextSearch -> pas de bornes, etc.
    String parseDefSimple2(String def) {

        String defT = def;
        String pattern = "\\\\kern[0-9.-]+em\\.?";
        defT = defT.replaceAll(pattern,"");

        pattern = "\\\\raise-[0-9.-]+ex";
        defT = defT.replaceAll(pattern,"");

        pattern = "\\$\\\\overline\\{\\\\hbox\\{([|a-zA-Z]+)\\}\\}\\$";
        defT = defT.replaceAll(pattern,"<font style = \"text-decoration:overline\">$1</font>");

        /*
        defT = defT.replaceAll("\\F", " → ")
        defT = defT.replaceAll("\\par", "</BR>")
        defT = defT.replaceAll("\\S", " § ")
        defT = defT.replaceAll("\\break", "")
        */
        String res = "";
        StringBuilder rt = new StringBuilder("");
        if (defT.contains("\\")) {
            String[] parties = defT.split("\\\\");
            for (int i = 0; i < parties.length; i++) {
                if (i < parties.length - 1) {
                    rt.append(parseChampSimple2(parties[i], parties[i + 1]));
                }
                else {
                    rt.append(parseChampSimple2(parties[i], ""));
                }
            }
            res = rt.toString();
        }
        else {
            //print("debug : parseGaffiot l.571 \(def)")
            res = def; // ????
        }

        res = res.replace("~", " ");

        return res;
    }

    String parseDefCensure(String def) {

        String defT = def;
        String pattern = "\\\\kern[0-9.-]+em\\.?";
        defT = defT.replaceAll(pattern,"");

        pattern = "\\\\raise-[0-9.-]+ex";
        defT = defT.replaceAll(pattern,"");

        pattern = "\\$\\\\overline\\{\\\\hbox\\{([|a-zA-Z]+)\\}\\}\\$";
        defT = defT.replaceAll(pattern,"<font style = \"text-decoration:overline\">$1</font>");




        defT = defT.replace(" (\\string?)", "");

        defT = defT.replace("\\F",  "&#8594;");
        defT = defT.replace("\\par",  "<BR/>");
        defT = defT.replace("\\S",  "§ ");
        defT = defT.replace( "\\break",  "");
        defT = defT.replace( "---", "&mdash;");

        defT = defT.replace( "<BR/><BR/><BR/>", "<BR/>");
        defT = defT.replace( "<BR/><BR/>", "<BR/>");

        String res = "";
        if (defT.contains("\\")) {
            String[] parties = defT.split("\\\\");

            for (int i = 0; i <parties.length; i++) {
                if (i < parties.length - 1) {
                    res += parseChampCensure(parties[i], parties[i + 1]);
                }
                else {
                    res += parseChampCensure(parties[i], "");
                }
            }

        }
        else {
            //print("debug : parseGaffiot l.230 \(def)")
            res = def; // ????
        }
        res = res.replace("~",  " ");
        res = res.replace(" .",  ".");
        res = res.replace( " ,",  ",");
        res = res.replace( "( ", "(");
        res = res.replace(" )", ")");

        return res;
    }
    String parseChampCensure(String champ, String champSuivant) {

        String nomChampSuivant = "";
        boolean ajouteEspace = true;
        if (champSuivant.contains("{")) {
            int indNomSuivant = champSuivant.indexOf("{");
            nomChampSuivant = champSuivant.substring(0,indNomSuivant);
        }
        if (nomChampSuivant == "up") {
            ajouteEspace = false;
        }
        if (champSuivant.startsWith(".") || champSuivant.startsWith(")") || champSuivant.startsWith("]")) {
            ajouteEspace = false;
        }

        String res = champ;

        if (res.contains("dixpc")) {
            res = res.replace("dixpc", "dixpc{");
        }
        if (res.contains("arabe")) {
            res = res.replace("arabe", "arabe{"); // 1 cas !!
        }

        if (res.contains("neufrm")) {
            //print("resorig : \(res)")
            res = res.replace("neufrm [}", "[");
            res = res.replace("neufrm ]}", "]");
            res = res.replace("neufrm]}", "]");
            res = res.replace("neufrm[}", "[");
            res = res.replace("neufrm]", "]");
            res = res.replace("{", "");
        }
        if (res.contains("{")) {
            int indNom = res.indexOf("{");

            String nomChamp = res.substring(0,indNom);

            String p1 = res.substring(indNom,res.length());
            String p2 = "";
            if (p1.contains("}")) {  //res.contains("}"

                int indFin1 = p1.indexOf("}");
                p2 = p1;
                p2 = p2.substring(indFin1,p2.length());
                // p2 = p2.substring(to: p2.endIndex)
                p1 = p1.substring(0,indFin1);

                if (!p2.startsWith(" ") && !p1.endsWith(" ")) {
                    //p2 += " "


                }
                if (p2.isEmpty()) {
                    if (ajouteEspace) {
                        p2 = " ";
                    }

                }
                else {
                    if (!p2.endsWith(" ") && !p2.startsWith("(")) { // et hasPrefix(".") ??
                        if (ajouteEspace) {
                            p2 += " ";
                        }
                    }

                    if (p2.startsWith(" ") || p2.startsWith(".") || p2.startsWith(")") || p1.endsWith(" ")) {
                        ajouteEspace = false;
                    }

                    if (ajouteEspace) {
                        p2 = " " + p2;
                    }
                }
            }
            String stringReplace = " - - - - ";

            switch (nomChamp) {

                case "entree":
                    p1 = stringReplace;
                    break;
                case "cl":
                    p1 = stringReplace;
                    break;
                case "es":
                    p1 = stringReplace;
                    break;
                case "el":
                    p1 = stringReplace;
                    break;
                case "aut":
                    break;

                case "etyml":
                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "oeuv":
                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "refch":
                    if (ajouteEspace) {
                        p1 += " ";
                    }

                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "refchp":
                    if (ajouteEspace) {
                        p1 += " ";
                    }
                    p1 = "<em>" + p1 + "</em>";
                    break;
                case "comm":
                    p1 = "";
                    break;
                case "ital":
                    p1 = "<em>" + p1 + "</em>";
                    break;

                case "F":
                    p1 = "&#8608;" + p1 ;// -> &#8594;
                    break;

                case "S":
                    p1 = "§" + p1;
                    break;

                case "par":
                    p1 = "<BR/>" + p1;
                    break;
                case "rub":
                    p1 = "<b>" + p1 + "</b>";
                    break;
                case "kkz":
                    p1 = "<b>" + p1 + "</b>";
                    break;
                case "pp":
                    p1 = "&para; <b>" + p1 + "</b>"; // &#182; : signe paragraphe
                    break;

                case "qq":
                    p1 = "<b>" + p1 + "</b>";
                    break;
                case "pc":
                    p1 = "<b>" + p1 + "</b>";
                    break;
                case "qqng":
                    //p1 = "<BR/>  " + p1
                    break;
                case "lat":
                    p1 = stringReplace;
                    break;
                case "latc":
                    p1 = stringReplace;
                    break;
                case "latv":
                    p1 = stringReplace;
                    break;

                case "up":
                    p1 = "<sup>" + p1 + "</sup>";
                    break;
                case "dixpc":
                    p1 = p1.toUpperCase();
                    break;
                default:
                    break;
            }

            res = p1 + p2;
        }
        res = res.replace("{", "");
        res = res.replace("}", "");
        res = res.replace("%", "");
        return res;
    }

}


