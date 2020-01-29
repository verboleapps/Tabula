package com.verbole.dcad.tabula;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dcad on 1/1/18.
 */


public class GestionTextes {
    List<String> listeAuteurs = new ArrayList<String>(Arrays.asList("Salluste","Jules César","Cornelius Nepos","St Augustin","Tacite","St Jérôme","Virgile"));
    List<OeuvreClassique> listeOeuvresClassiques = new ArrayList<OeuvreClassique>(); //Arrays.asList();
    List<String> listeNomsFichiers = new ArrayList<String>();
    List<String> listeURL = new ArrayList<String>(); //Arrays.asList();
    List<Boolean> listeVisibiliteSections = new ArrayList<Boolean>();
    Context myContext;
    String dirName = "Tabula";
    GestionFichiers GF;

    public GestionTextes(Context context) {
        myContext = context;
        GF = new GestionFichiers(context);

        OeuvreClassique oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = "Salluste";
        oeuvreClassique.titre = "Guerre de Jugurtha";
        oeuvreClassique.fichiers = Arrays.asList("Salluste_GJugurtha");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",114));
                //.init(auteur: "Salluste", titre: "Guerre de Jugurtha", fichiers: ["Salluste_GJugurtha"], parties: [("I",114)])
        listeOeuvresClassiques.add(oeuvreClassique);

        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = "Salluste";
        oeuvreClassique.titre = "Conjuration de Catilina";
        oeuvreClassique.fichiers = Arrays.asList("Bellum_Catilinae");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",61));
        //.init(auteur: "Salluste", titre: "Guerre de Jugurtha", fichiers: ["Salluste_GJugurtha"], parties: [("I",114)])
        listeOeuvresClassiques.add(oeuvreClassique);

        String titreGen = "Guerre des Gaules";
        String auteur = "Jules César";
        int[] chaps1 = {54,35,29,38,58,44,90,55};

        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.titre = titreGen;
        oeuvreClassique.auteur = auteur;

        for (int i = 1; i < 9; i++) {
            String rom = convertToRoman(i);
            String titre = "Guerre des Gaules " + rom;
            String fichier = "Caesar_BellumGallicum_" + rom;
            oeuvreClassique.fichiers.add(fichier);
            oeuvreClassique.parties.add(new Partie().init(titre,chaps1[i - 1]));
        }
        listeOeuvresClassiques.add(oeuvreClassique);

        titreGen = "Guerre Civile";
        int[] chaps2 = {87,44,112};
        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.titre = titreGen;
        oeuvreClassique.auteur = auteur;

        for (int i = 1; i < 4; i++) {
            String rom = convertToRoman(i);
            String titre = "Guerre Civile " + rom;
            String fichier = "Caesar_BelloCivili_" + rom;
            oeuvreClassique.fichiers.add(fichier);
            oeuvreClassique.parties.add(new Partie().init(titre,chaps2[i - 1]));
        }
        listeOeuvresClassiques.add(oeuvreClassique);


        //================ Nepos
        titreGen = "Vies des grands capitaines";
        auteur = "Cornelius Nepos";
        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.titre = titreGen;
        oeuvreClassique.auteur = auteur;
        String[] vies = {"Preface","Miltiade","Themistocle","Aristide","Pausanias","Cimon","Lysandre","Alcibiade",
                "Thrasybule","Conon","Dion","Iphicrate","Chabrias","Timothee","Datame","Epaminondas","Pelopidas","Agesilas",
                "Eumene","Phocion","Timoleon","Des Rois","Hamilcar","Hannibal","Porcius Caton","Pomponius Atticus"};
        int[] chaps3 = {0,8,10,3,5,4,4,11,4,5,10,3,4,4,11,10,5,8,13,4,5,3,4,13,3,22};

        for (int i = 1; i < vies.length; i++) {
            String v = vies[i - 1];
            String titre = "Vie de " + v;
            if (v.startsWith("A") || v.startsWith("E") || v.startsWith("I")) {
                titre = "Vie d'" + v;
            }
            if (v.equals("Des Rois") || v.equals("Preface")) {
                titre = v;
            }
            String fichier = "Nepos/" + v;
            oeuvreClassique.fichiers.add(fichier);
            oeuvreClassique.parties.add(new Partie().init(titre,chaps3[i - 1]));
        }
        listeOeuvresClassiques.add(oeuvreClassique);


        //================ Tacite
        auteur = "Tacite";

        titreGen = "Annales";
        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.titre = titreGen;
        oeuvreClassique.auteur = auteur;

        int[] chaps5 = {81,88,76,75,11,51,38,69,58,65,74,35};

        for (int i = 1; i < 13; i++) {
            int numR = i;
            if (i > 6) {
                numR = i + 4;
            }
            String rom = convertToRoman(numR);
            String titre = "Annales " + rom;
            String fichier = "Tacite/Annales_Tacite_" + rom;
            oeuvreClassique.fichiers.add(fichier);
            oeuvreClassique.parties.add(new Partie().init(titre,chaps5[i - 1]));
        }
        listeOeuvresClassiques.add(oeuvreClassique);

        titreGen = "Histoires";
        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.titre = titreGen;
        oeuvreClassique.auteur = auteur;

        int[] chaps4 = {90,101,86,86,26};

        for (int i = 1; i < 6; i++) {
            String rom = convertToRoman(i);
            String titre = "Histoires " + rom;
            String fichier = "Tacite/Histoires_Tacite_" + rom;
            oeuvreClassique.fichiers.add(fichier);
            oeuvreClassique.parties.add(new Partie().init(titre,chaps4[i - 1]));
        }
        listeOeuvresClassiques.add(oeuvreClassique);


        //================ St Augustin
        titreGen = "Confessions";
        auteur = "St Augustin";
        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.titre = titreGen;
        oeuvreClassique.auteur = auteur;

        int[] chaps6 = {31,18,21,31,25,26,27,30,37,70,41,43,53};

        for (int i = 1; i < 14; i++) {
            String rom = convertToRoman(i);
            String titre = "Confessions " + rom;
            String fichier = "StAugustin/Confessions_StAugustin_" + rom;
            oeuvreClassique.fichiers.add(fichier);
            oeuvreClassique.parties.add(new Partie().init(titre,chaps6[i - 1]));
        }
        listeOeuvresClassiques.add(oeuvreClassique);

        //================ St Jerome
        auteur = "St Jérôme";

        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = auteur;
        oeuvreClassique.titre = "Psaumes";
        oeuvreClassique.fichiers = Arrays.asList("StJerome/Vulgate_Psaumes");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",150));
        listeOeuvresClassiques.add(oeuvreClassique);

        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = auteur;
        oeuvreClassique.titre = "St Matthieu";
        oeuvreClassique.fichiers = Arrays.asList("StJerome/Vulgate_Matthieu");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",28));
        listeOeuvresClassiques.add(oeuvreClassique);

        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = auteur;
        oeuvreClassique.titre = "St Marc";
        oeuvreClassique.fichiers = Arrays.asList("StJerome/Vulgate_Marc");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",16));
        listeOeuvresClassiques.add(oeuvreClassique);

        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = auteur;
        oeuvreClassique.titre = "St Luc";
        oeuvreClassique.fichiers = Arrays.asList("StJerome/Vulgate_Luc");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",24));
        listeOeuvresClassiques.add(oeuvreClassique);

        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = auteur;
        oeuvreClassique.titre = "St Jean";
        oeuvreClassique.fichiers = Arrays.asList("StJerome/Vulgate_Jean");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",21));
        listeOeuvresClassiques.add(oeuvreClassique);

        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = auteur;
        oeuvreClassique.titre = "Actes des Apôtres";
        oeuvreClassique.fichiers = Arrays.asList("StJerome/Vulgate_Actes");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",28));
        listeOeuvresClassiques.add(oeuvreClassique);

        //================ Virgile

        titreGen = "Enéide";
        auteur = "Virgile";

        for (int i = 1; i < 13; i++) {

            oeuvreClassique = new OeuvreClassique();
            oeuvreClassique.auteur = auteur;
            String rom = convertToRoman(i);
            oeuvreClassique.titre = "Enéide chant " + rom;
            oeuvreClassique.fichiers = Arrays.asList("Virgile/Virgile_Eneide" + i);
            oeuvreClassique.parties = Arrays.asList(new Partie().init("I",1));
            listeOeuvresClassiques.add(oeuvreClassique);

        }
/*
        oeuvreClassique = new OeuvreClassique();
        oeuvreClassique.auteur = "Virgile";
        oeuvreClassique.titre = "Enéide";
        oeuvreClassique.fichiers = Arrays.asList("Virgile/Virgile_Eneide");
        oeuvreClassique.parties = Arrays.asList(new Partie().init("I",12));
        listeOeuvresClassiques.add(oeuvreClassique);
*/


        for (int i = 0; i < listeAuteurs.size(); i++) {
            listeVisibiliteSections.add(false);
        }
    }

    String getNomFichierFromPath(String chemin) {
        return GF.getNomFichierFromPath(chemin);
    }

    List<String> getListeNomsFichiers() {
        return GF.getListeNomsFichiers();
    }

    List<String> getListePaths() {
        return GF.getListePaths();
    }

    void metAJourListesFichiersEtURL() {
        listeNomsFichiers = GF.getListeNomsFichiers();
        listeURL = GF.getListePaths();
    }

    //================== Oeuvres classiques
    List<String> listeTitresOeuvres() {
        List<String> res = new ArrayList<String>();
        for (OeuvreClassique o : listeOeuvresClassiques) {
            res.add(o.titre);
        }
        return res;
    }

    List<String> getListeAuteurs() {
        List<String> res = new ArrayList<String>();
        for (String a : listeAuteurs) {
            res.add(a);
        }
        return res;
    }

    List<OeuvreClassique> listeDesOeuvresAuteur(String auteur) {
        List<OeuvreClassique> res = new ArrayList<OeuvreClassique>();
        for (OeuvreClassique oeuv : listeOeuvresClassiques) {
            if (oeuv.auteur.equals(auteur)) {
                res.add(oeuv);

            }
        }
        return res;
    }

    // =========== Tablette
    List<String> listeDesPartiesOeuvresAuteur(String auteur) {
        List<String> res = new ArrayList<String>();

        for (OeuvreClassique oeuv : listeOeuvresClassiques) {
            if (oeuv.auteur.equals(auteur)) {

                String nomOeuvre = oeuv.titre;
                for (Partie p : oeuv.parties) {
                    if (p.partie.equals("I")) {
                        res.add(nomOeuvre);
                    }
                    else {
                        res.add(p.partie);
                    }
                }
            }
        }
        return res;
    }
/*
    List<String> listeDesFichiersOeuvresAuteur(String auteur) {
        List<String> res = new ArrayList<String>();
        for (OeuvreClassique oeuv : listeOeuvresClassiques) {
            if (oeuv.auteur.equals(auteur)) {
                for (String f : oeuv.fichiers) {
                    res.add(f);
                }
            }
        }
        return res;
    }

    int nombreFichiersOeuvresAuteur(String auteur) {
        return listeDesPartiesOeuvresAuteur(auteur).size();
    }
    */
    // =========== Tablette

    // =========== Phone
    int nombreOeuvresAuteur(String auteur) {
        return listeDesOeuvresAuteur(auteur).size();
    }

    int nombrePartiesOeuvre(String auteur, String titre) {
        int compte = 0;
        for (OeuvreClassique oeuv : listeOeuvresClassiques) {
            if (oeuv.auteur.equals(auteur) && titre.equals(oeuv.titre)) {
                compte += 1;
            }
        }
        return compte;
    }

    List<Partie> listePartiesOeuvre(String auteur, String titre) {
        List<Partie> res = new ArrayList<Partie>();
        for (OeuvreClassique oeuv : listeOeuvresClassiques) {
            if (oeuv.auteur.equals(auteur) && titre.equals(oeuv.titre)) {
                return oeuv.parties;
            }
        }
        return res;
    }
    List<String> listeFichiersPartiesOeuvre(String auteur, String titre) {
        List<String> res = new ArrayList<String>();
        for (OeuvreClassique oeuv : listeOeuvresClassiques) {
            if (oeuv.auteur.equals(auteur) && titre.equals(oeuv.titre)) {
                return oeuv.fichiers;
            }
        }
        return res;
    }

    String titrePartieOeuvreParSectionIndice(int section, int indice) {
        int ind = 0;
        String auteur = this.listeAuteurs.get(section);
        List<OeuvreClassique> lo = listeDesOeuvresAuteur(auteur);
        Partie partie = new Partie();
        String oeuvre = "";
        while (ind <= indice) {
            for (OeuvreClassique o : lo) {
                for (Partie p : o.parties) {
                    if (ind == indice) {
                        partie = p;
                        oeuvre = o.titre;
                    }
                    ind += 1;
                }
            }
        }
        if (partie.partie.equals("I")) {
            partie.partie = oeuvre;
        }

        return partie.partie;
    }

    List<String> listeChapitresOeuvreParSectionIndice(int section, int indice) {
        int ind = 0;
        String auteur = this.listeAuteurs.get(section);
        Partie partie = new Partie();
        List<OeuvreClassique> lo = listeDesOeuvresAuteur(auteur);
        while (ind <= indice) {
            for (OeuvreClassique o : lo) {
                for (Partie p : o.parties) {
                    if (ind == indice) {
                        partie = p;
                    }
                    ind += 1;
                }
            }
        }
        List<String> res = new ArrayList<String>();

        if (auteur.equals("Cornelius Nepos") && partie.partie.equals("Preface")) {
            res.add("I");
            return res;
        }

        if (auteur.equals("Jules César") && partie.partie.equals("Guerre des Gaules VIII")) {
            res.add("Préface");
        }
        for (int i = 1; i <= partie.numero; i++) {
            String rom = convertToRoman(i);
            res.add(rom);
        }

        return res;
    }

    String fichierPartieOeuvreParSectionIndice(int section, int indice) {
        int ind = 0;
        String auteur = this.listeAuteurs.get(section);
        List<OeuvreClassique> lo = listeDesOeuvresAuteur(auteur);
        while (ind <= indice) {
            for (OeuvreClassique o : lo) {
                int indF = 0;
                for (Partie p : o.parties) {
                    if (ind == indice) {
                        return o.fichiers.get(indF);
                    }
                    ind += 1;
                    indF += 1;
                }
            }
        }
        return "";
    }

    // complique mais si preface : decalage ...
    int pagePourChapitreOeuvreParSectionIndice(int section, int indice, int page) {
        int ind = 0;
        String auteur = this.listeAuteurs.get(section);
        Partie partie = new Partie();
        List<OeuvreClassique> lo = listeDesOeuvresAuteur(auteur);
        while (ind <= indice) {
            for (OeuvreClassique o : lo) {
                for (Partie p : o.parties) {
                    if (ind == indice) {
                        partie = p;
                    }
                    ind += 1;
                }
            }
        }
        if (auteur.equals("Jules César") && partie.partie.equals("Guerre des Gaules VIII")) {
            return page;
        }
        else {
            return page + 1;
        }
    }

    String scriptJS(String nomScript) {
        //"<script type=\"text/javascript\" src=\"file:///android_asset/scripts/jquery341.min.js\"></script>";
        String script = "file:///android_asset/scripts/" + nomScript;
        return script;
    }

    String loadTexteAuteur(String nomFichierTexte, float largeurFenetre, int taillePolice) {

        String res = "";
        res = styleHtmlTextesAuteurs(largeurFenetre, taillePolice);
        res += "<body>";
        res += "<script src=\"" + scriptJS("jquery341.min.js") + "\"></script>"; // !!! placer avant l'autre script

        String typeFich = "txt"; //"html";
        /*
        if (nomFichierTexte.contains("Tacite") || nomFichierTexte.contains("Augustin")|| nomFichierTexte.contains("Vulgate")) {
            typeFich = "txt";
        }
        */
        res += GF.chargeFichiersAssets("textesAuteurs",nomFichierTexte,typeFich);
        res += "<script src=\"" + scriptJS("SelectTextScriptINSIDE.js") + "\"></script>"; // !!! placer avant l'autre script
        res += "<script>" + scriptGetChapitre() + "</script>";
        res += "</body>";

        //Log.d("yo",res);
        return res;
    }

    String scriptGetChapitre() {
        String sc = "function getChapitreCourant() {";
        sc += "JSInterface.getChapitre(numCourant);";
        sc += " };";

        return sc;
    }



    String styleHtmlTextesAuteurs(float largeurFenetre, int taillePolice) {
        StyleHtml st = new StyleHtml();
        return st.styleHtmlTextesAuteurs(largeurFenetre, taillePolice);
    }

    //=============================================================

    private class OeuvreClassique {
        String auteur = "";
        String titre = "";
        List<String> fichiers = new ArrayList<String>();
        List<Partie> parties = new ArrayList<Partie>();
    }

    private class Partie {
        String partie = "";
        int numero = -1;

        Partie init(String partie, int numero) {
            Partie res = new Partie();
            res.numero = numero;
            res.partie = partie;
            return res;
        }
    }


    int[] romanNumb = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
    String[] romanStr = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};

    String convertToRoman(int num) {
        if (num == 0) {
            return "";
        }
        for (int i = 0; i < romanNumb.length; i++)  {
            if (num >= romanNumb[i]) {
                return romanStr[i] + convertToRoman(num - romanNumb[i]);
            }
        }
        return "";
    }

    String abregeTitre(String titre) {
        String res = titre;
        if (titre.contains("Guerre de Jugurtha")) {
            res = res.replace("Guerre de Jugurtha","Jug.");
        }
        if (titre.contains("Conjuration de Catilina")) {
            res = res.replace("Conjuration de Catilina","Cat.");
        }
        if (titre.contains("Guerre des Gaules")) {
            res = res.replace("Guerre des Gaules","G.G.");
        }
        if (titre.contains("Guerre Civile")) {
            res = res.replace("Guerre Civile","G.C.");
        }
        if (titre.contains("Vie de ")) {
            res = res.replace("Vie de ","");
        }
        if (titre.contains("Confessions")) {
            res = res.replace("Confessions","Conf.");
        }
        if (titre.contains("Annales")) {
            res = res.replace("Annales","Ann.");
        }
        if (titre.contains("Histoires")) {
            res = res.replace("Histoires","Hist.");
        }
        return res;
    }

    /*
    String requestUrlFromFile(String nomFichier) {
        String res = "file:///android_asset/textesAuteurs/" + nomFichier;
        return res;
    }

    File storageFile() {
        // File myDir = new File("sdcard", dirName);
        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"
        boolean sd = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d("Test", "sdcard mounted and writable");
            sd = true;
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("Test", "sdcard mounted readonly");
            sd = true;
        }
        else {
            Log.d("Test", "sdcard state: " + state);
        }

        File sdcard;
        if (sd) {
            sdcard = Environment.getExternalStorageDirectory();
            // test si dossier Tabula deja cree ds InternalStorage (ex. avant avoir rajoute SDCard ??)
            File st = myContext.getFilesDir();
            File myDir = new File(st, dirName);
            if(myDir.exists()) {
                sdcard = myContext.getFilesDir();
            }

        }
        else {
            sdcard = myContext.getFilesDir();
        }
        return sdcard;
    }

    void creeDirectoryStorage() {
        File sdcard = storageFile();
        File myDir = new File(sdcard, dirName);
        if(!myDir.exists())
            myDir.mkdirs();
    }

    File DirectoryStorage() {
        File sdcard = storageFile();
        File myDir = new File(sdcard, dirName);
        if(!myDir.exists())
            myDir.mkdirs();
        return myDir;
    }

    List<String> getListeNomsFichiers() {
        //voir repertoires interieurs ?
        List<String> res = new ArrayList<>();

        File sdcard = storageFile();
        File myDir = new File(sdcard, dirName);
        if(!myDir.exists()) {

            myDir.mkdirs();
        }
        else {
            getFileNamesRecursive(myDir,res);


        }

        return res;
    }

    private static void getFilesRecursive(File pFile, List<String> res)
    {
        for(File files : pFile.listFiles())
        {
            if(files.isDirectory())
            {
                getFilesRecursive(files,res);
            }
            else
            {
                res.add(files.getAbsolutePath());
            }
        }
    }
    private static void getFileNamesRecursive(File pFile, List<String> res)
    {
        for(File files : pFile.listFiles())
        {
            if(files.isDirectory())
            {
                getFileNamesRecursive(files,res);
            }
            else
            {
                res.add(files.getName());
            }
        }
    }

    List<String> getListePaths() {
        List<String> res = new ArrayList<>();
        //File myDir = DirectoryStorage();

        File sdcard = storageFile();
        File myDir = new File(sdcard, dirName);
        if(!myDir.exists()) {
            myDir.mkdirs();
        }
        else {
            getFilesRecursive(myDir,res);

        }

        return res;
    }

    void effaceFichier(int numero) {

        //File myDir = DirectoryStorage();
        File sdcard = storageFile();
        File myDir = new File(sdcard, dirName);

        if(myDir.exists()) {
            File listeFich[] = myDir.listFiles();

            if (listeFich.length > numero) {
                listeFich[numero].delete();
            }
        }
    }
        */

    /*
    String chargeFichierHtmlFromDirectory(String chemin) {
        String res = "";

        return res;
    }

    String chargeFichiersAssets(String nomDossier, String nomFichier, String typeFichier) {
        String res = "";
        AssetManager asmatt = myContext.getAssets();
        InputStream input;
        String path = nomDossier + "/" + nomFichier;
        try {
            input = asmatt.open("textesAuteurs/" + nomFichier + "." + typeFichier);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            res += new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    // marche pas permissions ?
    public void appendLog(String text, String nomFile)
    {
        File logFile = new File("sdcard/" + nomFile + ".txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
*/

}
