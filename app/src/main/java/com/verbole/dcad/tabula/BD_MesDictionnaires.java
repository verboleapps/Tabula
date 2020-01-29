package com.verbole.dcad.tabula;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BD_MesDictionnaires extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3; // 18/10/19
    private static final String DB_NAME = "MesDictionnaires.db";
    private final Context myContext;
    private static String DB_PATH;
    //"/data/data/com.example.dcad.test/databases/";
    private SQLiteDatabase myDataBase;
    private boolean isBaseOuverte = false;
    private static final String TAG = BD_Dictionnaires.class.getSimpleName();
    public EcouteInstall ecouteInstalle;

    private static BD_MesDictionnaires sInstance;

    // ...

    public static synchronized BD_MesDictionnaires getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new BD_MesDictionnaires(context.getApplicationContext());
        }
        return sInstance;
    }

    private BD_MesDictionnaires(Context context) {
        super(context, DB_NAME,null,DATABASE_VERSION);
        this.myContext = context;
        Log.d(TAG,"version MesDB : " + String.valueOf(DATABASE_VERSION));
        //   DB_PATH  = "/data/data/" + myContext.getPackageName() + "/databases/";
        DB_PATH  = myContext.getFilesDir().getPath() + "/databases/";
        try {
            createDataBase();
        } catch (IOException ioe) {

            throw new Error("Unable to create database");
        }
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        // Log.d(TAG, "create db");
        if(dbExist){
            //do nothing - database already exist
            //Log.d(TAG, "db existe");
            getWritableDatabase(); // !!! important sinon ne controle pas la version de la DB
            close();
        }
        else{
            //By calling this method and empty database will be created
            // of your application so we are gonna be able to overwrite that
            File f = new File(DB_PATH);
            if (!f.exists()) {
                f.mkdir();
            }
            this.getReadableDatabase();
            this.close(); // ???
            GestionFichiers.copyDataBase_Bad(myContext,DB_NAME);

            /*
            try {
               // Log.d(TAG,"a copie db");
                this.close(); // ???
                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copying database");
            }

             */
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch(SQLiteException e){
//database does't exist yet.
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null;
    }
/*
    private void copyDataBase() throws IOException{
        //Open your local db as the input stream

        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream

        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;


        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();

        myInput.close();
    }
*/
    public void openDataBase() throws SQLiteException{
        //Open the database
        if (!isBaseOuverte) {
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            // Log.d(TAG,"open db");
        }

        isBaseOuverte = true;
    }

    private void preparePourQuery() {
        myDataBase.beginTransaction();
    }
    private void finQuery() {
        myDataBase.setTransactionSuccessful();
        myDataBase.endTransaction();
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null && isBaseOuverte)
            myDataBase.close();

        super.close();
        isBaseOuverte = false;
        // Log.d(TAG,"close db");
    }
    // Add your public helper methods to access and get content from the // You could return cursors by doing "return myDataBase.query(....)" // to you to create adapters for your views.

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        Log.d(TAG,"BD_MesDictionnaires on upgrade from : " + String.valueOf(oldVersion) + " to : " + String.valueOf(newVersion));
        if (newVersion > oldVersion) {
            GestionFichiers.copyDataBase_Bad(myContext,DB_NAME);
            /*
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }

             */
        }
    }


    String getDefinitionPeigne(int indice) {
        String res = "";
        String query = "select num, mot, def from PeigneInd where num = " + String.valueOf(indice);
        // let query = "select num, mot, def from Peigne where num = \(indice)"

        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();
            Cursor cursor = myDataBase.rawQuery(query,null);

            while (cursor.moveToNext())
            {
                String mot = cursor.getString(1);
                String def = cursor.getString(2);
                mot = ChangeDiphtongue.changeDiphtonguesListe(mot);
                res += "<span class = \"entreeDicTabula\">" + mot + "</span>, " + def + "</BR>";
            }
            cursor.close();
            //  close();

        } catch(SQLiteException sqle){
            throw sqle;

        }

        return res;
    }


    String getDefinitionSuckau(String mot, String typeRecherche) {
        String res = "";
        // let query = "select mot, def from GaffiotInd where \(typeRecherche) = '\(mot)'"
        // ne trouve rien car syntaxe pour Virtual table differente : il faut mettre tous les champs
        String motT = mot;
        int distance = 2;
        if (motT.contains("'")) {
            motT = motT.replace("'", "");
            distance = 0;
        }
        if (motT.contains("-")) {
            //motT = motT.replace("-", "");
            distance = 0;
        }
        String query = "select * from SuckauInd where " + typeRecherche + " MATCH '" + motT + "'"; //


        // !! MATCH trouve aussi les entrees dont mot CONTIENT mot (ex. cherche ager -> renvoie ager, Albiona ager, etc..)
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();
            Cursor cursor = myDataBase.rawQuery(query,null);
            while (cursor.moveToNext())
            {
                String entr = cursor.getString(1);

                if ((entr.length() <= mot.length() + distance) && (entr.length() >= mot.length() - distance)) {
                    String entree = cursor.getString(1);
                    entree = ChangeDiphtongue.changeDiphtonguesListe(entree);
                    String def = cursor.getString(3);
                    res += "<span class = \"entreeDicTabula\">" + entree + "</span>" + def + "</BR>";

                }
            }
            cursor.close();
            //  close();

        } catch(SQLiteException sqle){
            throw sqle;
        }

        //if (!res.isEmpty()) {//res = "</BR>Gaffiot</BR>" + res}
        return res;
    }



    String parseDefinitionSuckau(String def) {
        String res = def;

        res = res.replace("<citlat>","<b>");
        res = res.replace("</citlat>","</b>");
        res = res.replace("<citfr>","<i>");
        res = res.replace("</citfr>","</i>");
        res = res.replace("<motlat>","<b>");
        res = res.replace("</motlat>","</b>");

        return res;
    }



    String rechercheDefinition_pour_entree(EnregDico entree, String typeRecherche) {
        String res = "";

        String dsDic = entree.dico;

        String mot = "";
        if (typeRecherche.equals("motsimple")) {
            //mot = objet.value(forKey: "motsimple") as! String
            mot = entree.mot;
        }
        else {
            //mot = objet.value(forKey: "mot") as! String
            mot = entree.motOrig; // ??
        }

        if (dsDic.equals("P")) {
            int refPeigne = entree.refPeigne;
            //  res = "</BR>Tabula</BR>"
            res = getDefinitionPeigne(refPeigne);

        }
        if (dsDic.equals("S")) {

            //  res = "</BR>Tabula</BR>"
            res = getDefinitionSuckau(mot, typeRecherche);
            res = parseDefinitionSuckau(res);
        }

        return res;
    }



    String rechercheDefinitionCensure_pour_enregDict(EnregDico enreg, String typeRecherche) {
        //typeRecherche : String = "motsimple"
        String res = "";

        String dsDic = enreg.dico;

        if (dsDic.equals("P")) {
            int refPeigne = enreg.refPeigne;

            res = getDefinitionPeigne(refPeigne);

        }
        if (dsDic.equals("S")) {
            String mot = enreg.motOrig;
            if (typeRecherche.equals("motsimple")) {
                mot = enreg.mot;
            }

            res = getDefinitionSuckau(mot, typeRecherche);
            res = censureDefinition(res);
        }

        return res;
    }

    String censureDefinition(String def) {
        String res = def;
        res = res.replaceAll("<citlat>[a-zA-Z0-9,;: ]+</citlat>"," - - -");
        res = res.replaceAll("<mottlat>[a-zA-Z0-9,;: ]+</motlat>"," - - -");

        return res;
    }

    public List<ResultatFTS> rechercheEnregParDefinition(String motRecherche) {
        List<ResultatFTS> res = new ArrayList<>();

        res = fullTextSearchSuckau(motRecherche, true);
        if (res.size() < 3) {
            res.clear();
            List<ResultatFTS> r1 = fullTextSearchSuckau(motRecherche,false);
            List<ResultatFTS> r2 = fullTextSearchPeigne(motRecherche);
            for (ResultatFTS rt : r1) {
                res.add(rt);
            }
            for (ResultatFTS rt : r2) {
                res.add(rt);
            }
        }

        return res;
    }

    private List<ResultatFTS> fullTextSearchPeigne(String motRecherche) {
        List<ResultatFTS> res = new ArrayList<>();
        if (motRecherche.isEmpty()) {
            return res;
        }

        String mr = motRecherche;
        if (motRecherche.equals("oeil") || motRecherche.equals("œil")) {
            mr = "Œil";
        }
        Boolean estMaj = false;
        String l1 = motRecherche.substring(0,1);
        if (l1.equals(l1.toUpperCase())) {
            estMaj = true;
        }

        String query = "select mot, def from PeigneInd where def MATCH '" + mr + "'";
        if (motRecherche.contains("-")) {
            query = "select mot, motsimple, def from PeigneInd where def MATCH '\"" + mr + "\"'";
        }
        if (motRecherche.contains("'")) {
            query = "select mot, motsimple, def from PeigneInd where def MATCH \"" + mr + "\"";
        }
        preparePourQuery();

        //print("GDicSQL FTS Peigne l249")
        int compte = 0;
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();
            Cursor cursor = myDataBase.rawQuery(query,null);
            while (cursor.moveToNext())
            {
                String mot = cursor.getString(0);
                String def = cursor.getString(1);
                String motR2 = motRecherche.substring(0,1).toUpperCase() + motRecherche.substring(1);

                if ((motR2.equals(motRecherche) && estMaj) || (!motR2.equals(motRecherche) && !estMaj)) {
                    //def = def.replaceAll("([ ,.;->])" + motRecherche + "([ ,.;:?!-<])","$1<span class=\"highlight\">" + motRecherche + "</span>$2");
                    //def = def.replaceAll("([ ,.;->])" + motR2 + "([ ,.;:?!-<])","$1<span class=\"highlight\">" + motR2 + "</span>$2");

                    if (!def.isEmpty()) {
                        ResultatFTS resfts = new ResultatFTS(mot,mr,def,"P");
                        res.add(resfts);
                        compte += 1;
                    /*
                    if (compte > 100) {
                        break;
                    }
                    */
                    }
                }


            }
            cursor.close();
            finQuery();

        } catch(SQLiteException sqle){
            throw sqle;
        }

        //Log.d(TAG,"BDdictionnaire FTS Peigne : " + motRecherche + " - trouves : " + String.valueOf(compte));
        //print("fullTSPeigne : \(res.count) trouves")

        return res;
    }

    private List<ResultatFTS> fullTextSearchSuckau(String motRecherche, Boolean defCourte) {
        List<ResultatFTS> res = new ArrayList<>();
        String mr = motRecherche;
        if (motRecherche.equals("œil")) {
            mr = "oeil";
        }

        String defString = "defcourte";
        if (!defCourte) {
            defString = "def";
        }

        //String query = "select mot, motsimple, def from SuckauInd where defcourte MATCH '" + mr + "'";
       // if (!defCourte) { }
        String query = "select mot, motsimple, def from SuckauInd where " + defString + " MATCH '" + mr + "'";

        //https://stackoverflow.com/questions/21596069/sqlite-full-text-search-queries-with-hyphens
        if (motRecherche.contains("-")) {
            query = "select mot, motsimple, def from SuckauInd where " + defString + " MATCH '\"" + mr + "\"'";
        }
        if (motRecherche.contains("'")) {
            query = "select mot, motsimple, def from SuckauInd where " + defString + " MATCH \"" + mr + "\"";
        }
        preparePourQuery();
        int compte = 0;

        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();
            Cursor cursor = myDataBase.rawQuery(query,null);
            while (cursor.moveToNext())
            {
                String mot = cursor.getString(0);
                String def = cursor.getString(2);
                def = parseDefinitionSuckau(def);

                // Log.d(TAG,"bd dict 429 mot : " + mot);
                if (!def.isEmpty()) {
                    ResultatFTS resfts = new ResultatFTS(mot,motRecherche,def,"S");
                    res.add(resfts);
                    compte += 1;
                    /*
                    if (compte > 150) {
                        break;
                    }
                    */
                }
            }
            cursor.close();
            finQuery();

        } catch(SQLiteException sqle){
            throw sqle;
        }



        return res;
    }


}
