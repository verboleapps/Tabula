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

/**
 * Created by dcad on 3/7/16.
 */
public class dbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8; // 8 : nvelle version 06/11/19 - ancienne : 7
    private static final String DB_NAME = "latin.sqlite";
    private final Context myContext;
    private static String DB_PATH;
    //"/data/data/com.example.dcad.test/databases/";
    private SQLiteDatabase myDataBase;
    private boolean isBaseOuverte = false;
    private static final String TAG = dbHelper.class.getSimpleName();

    private static dbHelper sInstance;

    // ...

    public static synchronized dbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (sInstance == null) {
            sInstance = new dbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private dbHelper(Context context) {
        super(context, DB_NAME,null,DATABASE_VERSION);
        this.myContext = context;

      //  DB_PATH  = "/data/data/" + myContext.getPackageName() + "/databases/";
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
            Log.d(ActivitePrincipale2.TAG, "db existe");
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
            this.getWritableDatabase();

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
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
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
/*
    public void checkProcess() {
        ActivityManager activityManager = (ActivityManager) myContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo inf : infos) {
            if (myPid == inf.pid) {
                String pckn = myContext.getApplicationInfo().packageName;
                if (inf.processName.equals(pckn)) {

                }

            }

        }

    }
*/
    public void openDataBase() throws SQLiteException{
        //Open the database
        if (!isBaseOuverte) {
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            Log.d(ActivitePrincipale2.TAG,"open db");
        }

        isBaseOuverte = true;
    }

    public void preparePourQuery() {
        myDataBase.beginTransaction();
    }
    public void finQuery() {
        myDataBase.setTransactionSuccessful();
        myDataBase.endTransaction();
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null && isBaseOuverte)
        {
            myDataBase.close();
            Log.d(ActivitePrincipale2.TAG,"close db");
        }

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
        Log.d(ActivitePrincipale2.TAG,"ancienne vers : " + String.valueOf(oldVersion) + " - nvelle vers : " + String.valueOf(newVersion));
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
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(ActivitePrincipale2.TAG,"downgrade ...");
    }


    String upperBoundSearchString(String text) {

        int length = text.length();
        String baseString = "";
        String incrementedString = "";

        if (length < 1) {
            return text;
        }
        else if (length > 1) {
            baseString = text.substring(0,text.length() - 1);
        } else {
            baseString = "";
        }

        String dernChar = text.substring(text.length() - 1,text.length());

        Character lastChar = dernChar.charAt(0);

        int incrementedChar = -1;

        //https://developer.apple.com/library/content/samplecode/DerivedProperty/Listings/DerivedProperty_APLNormalizedStringTransformer_m.html
        // Don't do a simple lastChar + 1 operation here without taking into account
        // unicode surrogate characters (http://unicode.org/faq/utf_bom.html#34).

        if ((lastChar >= 0xD800) && (lastChar <= 0xDBFF)) {         // surrogate high character
            incrementedChar = (0xDBFF + 1);
        } else if ((lastChar >= 0xDC00) && (lastChar <= 0xDFFF)) {  // surrogate low character
            incrementedChar = (0xDFFF + 1);
        } else if (lastChar == 0xFFFF) {
            if (length > 1 ) {
                baseString = text;
                incrementedChar =  0x1;
            }
        } else {
            incrementedChar = lastChar + 1;
        }

        incrementedString = String.format("%s%c", baseString, incrementedChar);
        // print("incremented string : " + incrementedString)
        //incrementedString = String(format:"%C", incrementedChar)
        return incrementedString;
    }
/*
BASEENTREES Numero num entreemin entree dico

INFLEXION Numero pos decl1 decl2 cas genre type temps voix mode personne nombre
numrad term age freq
 */


    String setPredicateForFilterSearchText(String searchText) {
        String comp = "";
        String query = "SELECT _id, entreemin, entree FROM BASEENTREES ";
        // SELECT rowid _id,  un champ _id est necessaire sinon ca plante - utiliser le rowid cree par defaut

        String optionDic = getOrdreDicts();
        if (optionDic.equals("P")) {
            //comp = "WHERE (dico = 'P' OR dico = 'PG')";
            comp = "WHERE (dico = 'P' OR dico = 'S')";
        }
        if (optionDic.equals("G")) {
            comp = "WHERE (dico = 'G')";
        }
        if (optionDic.equals("GP") || optionDic.equals("PG")) {
            comp = "";
        }

        if (searchText.length() > 0) {
            String st =  searchText;
            st = st.replaceAll("([a-zA-Z]+)","");
            if (st.isEmpty()) {
                if (comp.isEmpty()) {
                    comp = "WHERE";
                }
                else {
                    comp += " AND";
                }
                String upp = upperBoundSearchString(searchText.toLowerCase());
                comp += " entreemin >= '" + searchText.toLowerCase() + "' AND entreemin < '" + upp + "'";
            }

        }
        else {
            if (comp.isEmpty()) {
                comp = "WHERE entreemin >= 'a' AND entreemin < 'abo'";
            }
            else {
                comp += " AND entreemin >= 'a' AND entreemin < 'abo'";
            }
        }

        query += comp + " GROUP BY entree ORDER BY entreemin"; // ORDER BY Numero
//Log.d(TAG,query);

        return query;
    }




    Cursor listeDesEntrees(String searchText) {
        openDataBase();

        String query =  setPredicateForFilterSearchText(searchText);

        return myDataBase.rawQuery(query, null);
    }

    EnregDico curseur2Entree(Cursor cursor) {
        EnregDico enreg = new EnregDico();
        enreg.mot = cursor.getString(2);
        return enreg;
    }

    EnregDico curseur2EnregDico(Cursor cursor) {
        EnregDico enreg = new EnregDico();
        enreg.Num = cursor.getInt(0);
        enreg.motOrig = cursor.getString(1);
        enreg.mot = cursor.getString(2);
        if (!(cursor.getString(3) == null)) {
            if (cursor.getString(3).equals(".")) {
                enreg.stem1 = "";
            }
            else {
                enreg.stem1 = cursor.getString(3);
            }
        }

        if (!(cursor.getString(4) == null)) {
            if (cursor.getString(4).equals(".")) {
                enreg.stem2 = "";
            }
            else {
                enreg.stem2 = cursor.getString(4);
            }
        }

        if (!(cursor.getString(5) == null)) {
            if (cursor.getString(5).equals(".")) {
                enreg.stem3 = "";
            }
            else {
                enreg.stem3 = cursor.getString(5);
            }
        }

        if (!(cursor.getString(6) == null)) {
            if (cursor.getString(6).equals(".")) {
                enreg.stem4 = "";
            }
            else {
                enreg.stem4 = cursor.getString(6);
            }
        }
        enreg.POS = cursor.getString(7);


        if (!(cursor.getString(8) == null)) {
            enreg.nombre = cursor.getString(8);
        }

        if (!(cursor.getString(9) == null)) {
            enreg.genre = cursor.getString(9);
        }

        enreg.decl1 = cursor.getInt(10);
        enreg.decl2 = cursor.getInt(11);


        if (!(cursor.getString(12) == null)) {
            enreg.type = cursor.getString(12);
        }

        enreg.AgeFreq = cursor.getString(13);

        if (!(cursor.getString(14) == null)) {
            enreg.refs = cursor.getString(14);
        }

        //         enreg.refs = cursor.getString(13);
        enreg.refVar = cursor.getInt(15);
        //String defc = rechercheDefDico(enreg.Num, enreg.POS);
        //enreg.def = defc;
        enreg.refPeigne = cursor.getInt(16);
        enreg.dico = cursor.getString(17);
        return enreg;
    }

    ArrayList<EnregDico> effectueRechercheEnreg(String rechQuery) {

        ArrayList<EnregDico> listeRes = new ArrayList<EnregDico>();
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
          //  openDataBase();
            Cursor cursor = myDataBase.rawQuery(rechQuery,null);
            while (cursor.moveToNext())
            {
                /*
                BASELATIN Numero mot mot1 motsimple motmin lemme1 lemme2 lemme3 lemme4
motminr lemme1r lemme2r lemme3r lemme4r pos nombre genre decl1 decl2 type agefreq
refs refvar numtableorig dico
        SELECT Numero, mot, motsimple, lemme1, lemme2, lemme3, lemme4, pos, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico

                 */
                EnregDico enreg = curseur2EnregDico(cursor);
                //enreg.maz();


                listeRes.add(enreg);


            }
            cursor.close();
          //  close();

        } catch(SQLiteException sqle){
            throw sqle;

        }

        return listeRes;

    }

    private String setQueryRechercheEntreesDsBase(String mot, String pos, String typeRecherche) {
        String rechQuery = "SELECT Numero, mot, motsimple, lemme1, lemme2, lemme3, lemme4, pos, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico FROM BASELATIN";

        rechQuery += " WHERE " + typeRecherche + " ='" + mot + "' AND (refs != 'X')";
        if (!pos.isEmpty()) {
            String comp = " AND pos = '" + pos + "'";
            if (pos.equals("PREP") || pos.equals("ADV") || pos.equals("INTERJ") || pos.equals("CONJ")) {
                comp = " AND (pos = 'PREP' OR pos = 'ADV' OR pos = 'INTERJ' OR pos = 'CONJ')";
            }
            if (pos.equals("ADJ") || pos.equals("VPAR") || pos.equals("PRON") || pos.equals("NUM")) {
                comp = " AND (pos = 'ADJ' OR pos = 'VPAR' OR pos = 'PRON' OR pos = 'NUM')";
            }

            if (mot.equals("necesse")) {
                comp = "";// !! nom ou adj ou adv indecl.
            }

            rechQuery += comp;
        }
        return rechQuery;
    }

    ArrayList<EnregDico> rechercheEntreesDsBase(String mot, String pos, String typeRecherche, String ordreDicts) {
        //BASELATIN Numero num mot mot1 motsimple motmin lemme1 lemme2 lemme3 lemme4
        //motminr lemme1r lemme2r lemme3r lemme4r pos nombre genre decl1 decl2 type agefreq
        //refs refvar numtableorig dico
        String rechQuery = setQueryRechercheEntreesDsBase(mot, pos, typeRecherche);

        ArrayList<EnregDico> listeRes = new ArrayList();
        if (ordreDicts.equals("P")) {
            rechQuery += " AND (dico = 'P' OR dico = 'S')";
            listeRes = effectueRechercheEnreg(rechQuery);
        }

        if (ordreDicts.equals("G")) {
            rechQuery += " AND (dico = 'G')";
            listeRes = effectueRechercheEnreg(rechQuery);
        }

        if (ordreDicts.equals("PG") || ordreDicts.equals("GP")) {
            String rq1 = rechQuery + " AND dico = 'P'";
            ArrayList<EnregDico> res1 = effectueRechercheEnreg(rq1);

            String rq2 = rechQuery + " AND dico = 'S'";
            ArrayList<EnregDico> res2 = effectueRechercheEnreg(rq2);

            String rq3 = rechQuery + " AND dico = 'G'";
            ArrayList<EnregDico> res3 = effectueRechercheEnreg(rq3);

            if (ordreDicts.equals("PG")) {
                for (EnregDico e : res1) {
                    listeRes.add(e);
                }
                for (EnregDico e : res2) {
                    listeRes.add(e);
                }
                for (EnregDico e : res3) {
                    listeRes.add(e);
                }
            }
            else {
                for (EnregDico e : res3) {
                    listeRes.add(e);
                }
                for (EnregDico e : res1) {
                    listeRes.add(e);
                }
                for (EnregDico e : res2) {
                    listeRes.add(e);
                }
            }
        }

        // on est sur qu'il y aura au max un enregistrement unique
        return listeRes;
    }




    EnregDico rechercheEntreeDico(int num) {

        String rechQuery = "SELECT Numero, mot, motsimple, lemme1, lemme2, lemme3, lemme4, POS, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico FROM BASELATIN";

        rechQuery += " WHERE num = ";
        rechQuery += String.valueOf(num);
        // on est sur qu'il y aura au max un enregistrement unique

        ArrayList res = effectueRechercheEnreg(rechQuery);
        if (res.size() > 0) {
            EnregDico ent0 = (EnregDico) res.get(0);
            //String def = rechercheDefinition_pour_entree(ent0.Num);
            //ent0.def = def;
            return ent0;
        }
        return new EnregDico();
    }

    ArrayList rechercheLemmeSpecial(String forme, String POS ) {
        ArrayList res = new ArrayList<EnregDico>();
        //let pred = "motmin = '" + forme + "'"
        String rechQuery = "SELECT Numero, mot, motsimple, lemme1, lemme2, lemme3, lemme4, pos, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico";
        rechQuery += " FROM BASELATIN ";
        rechQuery += " WHERE motsimple = '" + forme + "'";
        rechQuery += " AND POS = '" + POS + "'";
        rechQuery += " ORDER BY Numero";

        ArrayList<EnregDico> listeRes = effectueRechercheEnreg(rechQuery);
        // on est sur qu'il y aura au max un enregistrement unique
        for (EnregDico r : listeRes) {
            int ind = r.Num;
            res.add(ind);
        }

        return res;
    }

    EnregDico rechercheEntreeDicoParMotOrig(String mot) {

        EnregDico enreg = new EnregDico();
        String rechQuery = "SELECT Numero, mot, motsimple, lemme1, lemme2, lemme3, lemme4, POS, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico FROM BASELATIN";
        rechQuery += "WHERE mot = '" + mot + "'";

        // on est sur qu'il y aura au max un enregistrement unique
        ArrayList res = effectueRechercheEnreg(rechQuery);
        if (res.size() > 0) {
            EnregDico enr = (EnregDico) res.get(0);

            return (EnregDico) res.get(0);
        }
        return enreg;
    }


    ArrayList<EnregDico> rechercheEntreeDicoContientRefs(int refVar) {
        ArrayList<EnregDico> enregs = new ArrayList<EnregDico>();
        if (refVar == 0) { return enregs; }

        String rechQuery = "SELECT Numero, mot, motsimple, lemme1, lemme2, lemme3, lemme4, POS, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico FROM BASELATIN";

        rechQuery += " WHERE refs GLOB '[,]" + String.valueOf(refVar) + "[,]'";

        String comp = "";
        String optionDic = getOrdreDicts();
        if (optionDic.equals("P")) {
            comp = " AND (dico = 'P')";
        }
        if (optionDic.equals("G")) {
            comp = " AND (dico = 'G')";
        }

        rechQuery += comp + " ORDER BY Numero";

        return effectueRechercheEnreg(rechQuery);
    }

    EnregDico rechercheEntreeDicoParRefVar(int num, String pos) {

        EnregDico enreg = new EnregDico();
        String rechQuery = "SELECT Numero, mot, motsimple, lemme1, lemme2, lemme3, lemme4, POS, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico FROM BASELATIN";

        rechQuery += " WHERE POS ='" + pos + "'";
        rechQuery += " AND refVar =";
        rechQuery += String.valueOf(num);
        // on est sur qu'il y aura au max un enregistrement unique

        ArrayList res = effectueRechercheEnreg(rechQuery);
        if (res.size() > 0) {
            return (EnregDico) res.get(0);
        }
        else {
            return enreg;
        }
    }

    EnregDico rechercheEntreeDicoParMotOrigDico(String mot, String dico) {

        EnregDico enreg = new EnregDico();
        String rechQuery = "SELECT Numero, mot, motsimple, lemme1, lemme2, lemme3, lemme4, POS, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico FROM BASELATIN";

        rechQuery += " WHERE mot ='" + mot + "'";
        rechQuery += " AND dico ='" + dico + "'";

        // on est sur qu'il y aura au max un enregistrement unique

        ArrayList res = effectueRechercheEnreg(rechQuery);
        if (res.size() > 0) {
            return (EnregDico) res.get(0);
        }
        else {
            return enreg;
        }
    }

    //=========================== DECLINAISONS -================================================
    //===========================    NOMS ========================================================

    StemDesinence rechercheDesinenceNOM(EnregDico motDico, String cas , String nombre, boolean ToutesFormes) {
        StemDesinence res = new StemDesinence();
        String decl1 = String.valueOf(motDico.decl1);
        String decl2 = String.valueOf(motDico.decl2);
        String casT = cas;

        if (motDico.decl1 == 9) {
            res.stem = motDico.mot;
            return res;
        }
        if (motDico.decl1 == 3 && motDico.decl2 == 10) {
            if (nombre.equals("P")) {
                decl2 = "1"; // ??
            }
        }
        if (motDico.decl1 == 2 && motDico.decl2 == 7) {
            if (nombre.equals("S") && motDico.mot.endsWith("ys")) { // 29 noms
                if (cas.equals("GEN") || cas.equals("ACC")) {
                    decl2 = "71";
                }
            }
        }
        if (motDico.decl1 == 3 && motDico.decl2 == 9) {
            if (nombre.equals("S")) {
                if (cas.equals("NOM") || cas.equals("VOC") || cas.equals("DAT")) {
                    decl2 = "0"; // ??
                }
                if (cas.equals("ACC") && motDico.genre.equals("N")) {
                    decl2 = "2"; // ???? ds Whittaker : -in- avec genre X
                }

            }
            if (nombre.equals("P")) {
                if ((cas.equals("NOM") || cas.equals("VOC") || cas.equals("ACC")) && (motDico.genre.equals("M") || motDico.genre.equals("F") || motDico.genre.equals("C"))) {
                    decl2 = "0"; // ??
                }
                if ((cas.equals("ACC")) && (motDico.genre.equals("M") || motDico.genre.equals("F") || motDico.genre.equals("C"))) {
                    decl2 = "1"; // ??
                }

                if (cas.equals("DAT") || cas.equals("ABL")) {
                    decl2 = "0"; // ??
                }
            }
        }

        if (motDico.decl1 == 2 && motDico.genre.equals("N")) {
            if (motDico.decl2 != 1 && nombre.equals("P")) {
                if (cas.equals("NOM") || cas.equals("ACC") || cas.equals("VOC")) {
                    decl2 = "0";
                }
            }
            if (motDico.decl2 == 1 && nombre.equals("S")) {
                if (cas.equals("VOC")) {
                    casT = "NOM";
                    // vaudrait mieux changer ds Inflexion 2 1 S VOC X e en C et ajouter 2 1 S VOC N us (ou 0)
                }
            }
        }

        int indice = -10;
        String rechQuery = "SELECT POS, decl1, decl2, cas, nombre, genre, numRad, term, Age, Freq FROM INFLEXION WHERE decl1 =";
        rechQuery += decl1 + " AND decl2 <= " + decl2 + " AND cas ='" + casT + "' AND nombre ='" + nombre + "' AND POS = 'N' ORDER BY decl2";

        ArrayList<Desinence> resTemp = new ArrayList<Desinence>();
        //ArrayList resList = new ArrayList();

        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();

            Cursor cursor = myDataBase.rawQuery(rechQuery, null);
            int i = 0;

            while (cursor.moveToNext()) {

                if (correspondGenre(motDico.genre,cursor.getString(5))) {
                    if (cursor.getString(9).equals("A") || ToutesFormes) {
                        indice = i;
                    }
                }

                i += 1;
                Desinence des = new Desinence();
                des.numRad = cursor.getInt(6);

                //resTemp.add(cursor.getInt(6));
                if (cursor.getString(7) == null) {
                    //resTemp.add("");
                    des.term = "";
                }
                else {
                    //resTemp.add(cursor.getString(7));
                    des.term = cursor.getString(7);
                }
                resTemp.add(des);
                //resList.add(resTxt);
            }
            cursor.close();
            //     close();


        } catch(SQLiteException sqle){
            throw sqle;

        }

        if (indice <= -1) {
            indice = 0;
            //print("indice mis a 0 pour : nom " + cas + " " + nombre)
            //print(pred)
            res.stem = "";
            res.desinence = "";
            return res;
        }

        String[] listeStems = {motDico.stem1, motDico.stem2, motDico.stem3, motDico.stem4};
        Desinence d = resTemp.get(indice);
        int indiceStem = d.numRad - 1;
        //String bonstem = listeStems[indiceStem];

        res.stem = listeStems[indiceStem];
        res.desinence = d.term;
        res.desinence = ChangeDiphtongue.changeDiphtongue(res.desinence);

        String[] genP_UM = {"sestertius","denarius","modius","nummus","triumvir","decemvir"};
        for (String m : genP_UM) {
            if (m.equals(motDico.mot)) {
                if (cas.equals("GEN") && nombre.equals("P")) {
                    res.desinence = "um";
                }
                break;
            }
        }

        return res;
    }

    Boolean correspondGenre(String genreNom, String genreInflex) {
        if (genreNom.equals("M")) {
            if (genreInflex.equals("C") || genreInflex.equals("X") || genreInflex.equals("M")) {
                return true;
            }
            else {
                return false;
            }
        }

        if (genreNom.equals("N")) {
            if (genreInflex.equals("X") || genreInflex.equals("N")) {
                return true;
            }
            else {
                return false;
            }
        }

        if (genreNom.equals("F")) {
            if (genreInflex.equals("C") || genreInflex.equals("X") || genreInflex.equals("F")) {
                return true;
            }
            else {
                return false;
            }
        }

        if (genreNom.equals("C")) {
            if (genreInflex.equals("C") || genreInflex.equals("X")) {
                return true;
            }
            else {
                return false;
            }
        }

        return false;
    }



    //===========================    ADJ ========================================================
    StemDesinence rechercheDesinenceADJ(EnregDico motDico, ArrayList<Desinence> desinences, String cas, String nombre, String type, String genre, boolean ToutesFormes)  {
        StemDesinence res = new StemDesinence();
        int decl1 = motDico.decl1;
        int decl2 = motDico.decl2;
        //  let genre = motDico.genre

        if (type.equals("COMP") || type.equals("SUPER")) {
            decl1 = 0;
            decl2 = 0;
        }

        if (!motDico.genre.isEmpty()) {
            if (motDico.genre.equals("C")) {
                if (genre.equals("N")) {
                    return res;
                }
            }
            else {
                if (!motDico.genre.equals(genre)) {
                    return res;
                }
            }
        }
        if (motDico.nombre.equals("P")) {
            if (nombre.equals("S")) {
                return res;
            }
        }

        if (motDico.nombre.equals("S")) {
            if (nombre.equals("P")) {
                return res;
            }
        }
        String typeX = type;

        if (motDico.decl1 == 2) {
            if (nombre.equals("P")) {
                decl1 = 1;
                decl2 = 0;
                typeX = "POS";
            }
            if (nombre.equals("S")) {
                typeX = "X";
                if (motDico.decl2 == 1) { // os,e,on
                    if (genre.equals("M")) {
                        decl2 = 6;
                    }
                    if (genre.equals("N")) {
                        decl2 = 8;
                    }
                }
                if (motDico.decl2 == 2) { // os, a ,um
                    if (genre.equals("M")) {
                        decl2 = 7;
                    }
                    if (genre.equals("F")) {
                        typeX = "POS";
                    }
                    if (genre.equals("N")) {
                        decl1 = 1;
                        decl2 = 1; // ????
                        typeX = "POS";
                    }
                }
                if (motDico.decl2 == 6) { // os,os,on
                    if (genre.equals("N")) {
                        decl2 = 8;
                    }
                }
            }
        }
        if (motDico.decl1 == 3 && motDico.decl2 > 5) {
            if (nombre.equals("P")) {
                EnregDico motT = motDico.copie();
                motT.genre = "M";
                motT.POS = "N";
                motT.decl2 = 1;
                return rechercheDesinenceNOM(motT, cas, nombre,false);
            }
        }

        int indice = -1;
        int i = 0;

        while (i < desinences.size()) {
            Desinence desT = desinences.get(i);
            boolean test = (desT.decl1 == decl1) && (desT.decl2 <= decl2) && (desT.cas.equals(cas));
            test = test && (desT.nombre.equals(nombre)) && (desT.type.equals(typeX));
            if (test) {
                if (correspondGenre(genre,desT.genre)) {
                    if (desT.freq.equals("A") || ToutesFormes) {
                        indice = i;
                    }
                }
            }
            i += 1;
        }

        if (indice <= -1) {
            indice = 0;
            //print("indice mis a 0 pour : adj " + type + " " + cas + " " + nombre + " " + genre)
            //print(pred)
            res.stem = "";
            res.desinence = "";
            return res;
        }

        String[] listeStems = {motDico.stem1, motDico.stem2, motDico.stem3, motDico.stem4};
        Desinence d = desinences.get(indice);
        int indiceStem = d.numRad - 1;

        res.stem = listeStems[indiceStem];
        res.desinence = desinences.get(indice).term;

        if (type.endsWith("SUPER")) {
            if (res.stem.endsWith("issi")) {
                res.stem = res.stem.substring(0,res.stem.length() - 4);
                res.desinence = "issi" + res.desinence;
            }

        }
        if (type.endsWith("COMP")) {
            if (res.stem.endsWith("i")) {
                res.stem = res.stem.substring(0,res.stem.length() - 1);
                res.desinence = "i" + res.desinence;
            }

        }
        res.desinence = ChangeDiphtongue.changeDiphtongue(res.desinence);
        return res;

    }


    //===========================    VERBES ========================================================

    StemDesinence rechercheDesinenceVERBE(EnregDico motDico, ArrayList<Desinence> desinences, String temps, String voix, String nombre, int personne, String mode, boolean ToutesFormes) {
        StemDesinence res = new StemDesinence();
        //String decl1 = String.valueOf(motDico.decl1);
        //String decl2 = String.valueOf(motDico.decl2);
        int decl1 = motDico.decl1;
        int decl2 = motDico.decl2;

        if (motDico.decl1 < 7 || motDico.decl2 == 3) {
            if (temps.equals("PERF") && voix.equals("ACTIVE") && mode.equals("IND")) {
                decl1 = 0;
                decl2 = 0;
            }
        }

        if (temps.equals("PLUP") && voix.equals("ACTIVE") && mode.equals("IND")) {
            decl1 = 0;
            decl2 = 0;
        }
        if (temps.equals("FUTP") && voix.equals("ACTIVE") && mode.equals("IND")) {
            decl1 = 0;
            decl2 = 0;
        }
        if (temps.equals("PERF") && voix.equals("ACTIVE") && mode.equals("SUB")) {
            decl1 = 0;
            decl2 = 0;
        }
        if (temps.equals("PLUP") && voix.equals("ACTIVE") && mode.equals("SUB")) {
            decl1 = 0;
            decl2 = 0;
        }
        /*
        String n1 = "";
        String n2 = "";

        if (nombre.equals("P")) {
            n1 = "O";
            n2 = "Q";
        }
        else {
            n1 = "R";
            n2 = "T";
        }
        */
        //String voixT = voix.substring(0,voix.length() - 1);

        int j = 0;
        ArrayList<Desinence> resTemp = new ArrayList<Desinence>();
        while (j < desinences.size()) {
            Desinence desT = desinences.get(j);
            boolean test = (desT.decl1 == decl1) && (desT.decl2 <= decl2) && (desT.personne.equals(String.valueOf(personne))) && (desT.nombre.equals(nombre)) && (desT.temps.equals(temps));
            if (mode.equals("INF")) {
                test = (desT.decl1 == decl1) && (desT.decl2 <= decl2)  && (desT.voix.equals(voix));
                test = test && (desT.mode.equals("INF")) && (desT.temps.equals(temps)); //&& (desT.personne.equals("0")) && (desT.nombre.equals("X"));
            }
            if (test) {
                resTemp.add(desT);
            }
            j += 1;
        }

        int i = 0;
        int indice = -10;
        while (i < resTemp.size()) {
            if (motDico.decl1 == 5 && !motDico.mot.equals("sum") && motDico.decl2 == 1) {
                if (mode.equals("SUB") && temps.equals("IMPF") && indice >= 0) {
                    //break;
                }
            }
            if (resTemp.get(i).decl1 > 6) {  // a cause de edo ??

                if (motDico.decl2 == resTemp.get(i).decl2) {

                    if (resTemp.get(i).freq.equals("A") || ToutesFormes) {
                        indice = i;
                    }
                    if (resTemp.get(i).freq.equals("B") && motDico.decl1 == 7 && motDico.decl2 == 3) {
                        indice = i ;// edo, comedo, etc
                    }
                }

            }
            else {
                // cf declinaisons vb groupe 3 cas de la racine en c
                boolean condRaceT = true;
                boolean condRac1 = (temps.equals("PRES")) && (mode.equals("IMP")) && (personne == 2) && (nombre.equals("S")) && (voix.equals("ACTIVE")) && (motDico.decl1 == 3);
                boolean condRac2 = motDico.stem2.endsWith("sc") || motDico.stem2.endsWith("cc") || motDico.stem2.endsWith("rc") || motDico.stem1.endsWith("ici") || motDico.stem2.endsWith("fic") || motDico.stem2.endsWith("jac");

                if (condRac1) {
                    if (motDico.stem2.endsWith("c") && !condRac2 && resTemp.get(i).term.endsWith("e")) {
                        condRaceT = false;
                    }
                    if ((!motDico.stem2.endsWith("c") || condRac2) && resTemp.get(i).term.endsWith("")) {
                        condRaceT = false;
                    }
                }

                if ((resTemp.get(i).freq.equals("A") || ToutesFormes) && condRaceT) {
                    indice = i;
                }

            }

            i += 1;
        }

        if (indice < 0) {
            if (motDico.decl1 <= 6) {
                //print("indice mis a 0 pour : " + motDico.mot + " - " + mode + " " + temps + " " + voix + " " + String(personne) + " " + nombre)
                //print(pred)
            }

            res.stem = "";
            res.desinence = "";
            return res;

        }
        String listeStems[] = {motDico.stem1,motDico.stem2,motDico.stem3,motDico.stem4};
        int indiceStem = resTemp.get(indice).numRad - 1;
        res.stem = listeStems[indiceStem];
        res.desinence = resTemp.get(indice).term;

        if (motDico.mot.equals("sum")) { // le seul ??
            if (res.stem.isEmpty()) {
                res.stem = res.desinence;
                res.desinence = "";
            }
        }
        if (motDico.decl1 == 3 && motDico.decl2 == 2) { // composes de fero
            if (mode.equals("IMP") && temps.equals("PRES")) {
                if (nombre.equals("S") && personne == 2) {
                    res.desinence = "";
                }
            }
        }
        res.desinence = ChangeDiphtongue.changeDiphtongue(res.desinence);

        return res;
    }

    StemDesinence rechercheDesinencePART(EnregDico motDico, ArrayList<Desinence> desinences, String temps , String voix, String cas , String nombre, String genre, boolean ToutesFormes) {
        StemDesinence res = new StemDesinence();
        int decl1 = motDico.decl1;
        int decl2 = motDico.decl2;

        // perf passive : 0 - fut active : 0
        if (voix.equals("PASSIVE") && temps.equals("PERF")) {
            decl1 = 0;
            decl2 = 0;
        }
        if (voix.equals("ACTIVE") && temps.equals("FUT")) {
            decl1 = 0;
            decl2 = 0;
        }

        if (voix.equals("PASSIVE") && temps.equals("FUT")) {
            if (decl1 == 7) {
                decl1 = 3; // ???
                decl2 = 0;
                return res; // inutile car variantes decl de 3 verbes composes de edo
            }

        }

        int j = 0;
        ArrayList<Desinence> resTemp = new ArrayList<Desinence>();
        while (j < desinences.size()) {
            Desinence desT = desinences.get(j).copie();
            boolean test = (desT.decl1 == decl1) && (desT.decl2 <= decl2) && (desT.cas.equals(cas)) && (desT.nombre.equals(nombre));
            test = test && (desT.temps.equals(temps)) && (desT.voix.equals(voix));
            if (test) {
                resTemp.add(desT);
            }
            j += 1;
        }

        int indice = -1;
        int i = 0;

        while (i < resTemp.size()) {
            //resTemp[i].afficheDesinence()
            if (correspondGenre(genre, resTemp.get(i).genre)) {
                if (resTemp.get(i).freq.equals("A") || ToutesFormes) {
                    indice = i;
                }
            }
            i += 1;
        }
        if (indice < 0) {
            indice = 0;
            //Log.d(Dictionnaire.TAG, "indice mis a 0 pour : Part " + temps + " " + voix + " " + cas + " " + nombre);
            //print(pred)
            res.stem = "";
            res.desinence = "";
            return res;
        }

        String[] listeStems = {motDico.stem1, motDico.stem2, motDico.stem3, motDico.stem4};
        Desinence d = resTemp.get(indice);

        int indiceStem = d.numRad - 1;

        if (motDico.POS.equals("VPAR")) { // 1 radical ou 2 au max si composes de eo 6 1
            if (motDico.decl1 == 6 && motDico.decl2 == 1) {
                if (indiceStem == 0) {
                    indiceStem = 1;
                }
                else {
                    indiceStem = 0;
                }
            }
            else {
                indiceStem = 0;
            }

        }
        String bonstem = listeStems[indiceStem];

        res.stem = bonstem;
        res.desinence = resTemp.get(indice).term;
        res.desinence = ChangeDiphtongue.changeDiphtongue(res.desinence);

        if (motDico.mot.endsWith("morior") && temps.equals("FUT")) {
            res.stem = motDico.stem1 + "t";
        }

        return res;
    }


    StemDesinence rechercheDesinenceNUM(EnregDico motDico, String type, String cas, String nombre, String genre, Boolean ToutesFormes) {
        StemDesinence res = new StemDesinence();

        int decl1 = motDico.decl1;
        int decl2 = motDico.decl2;
        String typeT = type;

        if (type.equals("ORD") || type.equals("DIST")) {
            decl1 = 0;
            decl2 = 0;
        }
        if (type.equals("X")) {
            typeT = "CARD";
        }
        int indice = -10;

        //if (motDico.decl2 > 1 && nombre.equals("S")) {return "";}
        String rechQuery = "SELECT POS, decl1, decl2, cas, nombre, genre, type, numRad, term, Age, Freq FROM INFLEXION WHERE ";
        rechQuery += "decl1 =" + decl1 + " AND decl2 <= " + decl2 + " AND cas ='" + cas + "' AND nombre ='" + nombre;
        rechQuery += "' AND type ='" + typeT + "' AND POS = 'NUM' ORDER BY decl2";


        ArrayList<Desinence> resTemp = new ArrayList<Desinence>();

        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();

            Cursor cursor = myDataBase.rawQuery(rechQuery, null);
            int i = 0;

            while (cursor.moveToNext()) {

                if (correspondGenre(genre,cursor.getString(5))) {
                    if (cursor.getString(10).equals("A") || ToutesFormes) {
                        indice = i;
                    }
                }
                i += 1 ;

                Desinence des = new Desinence();
                des.numRad = cursor.getInt(7);

                if (cursor.getString(8) == null) {
                    des.term = "";
                }
                else {
                    des.term = cursor.getString(8);
                }
                resTemp.add(des);
            }
            cursor.close();
            //  close();


        } catch(SQLiteException sqle){
            throw sqle;
        }

        if (indice < 0) {
            indice = 0;
            //Log.d(TAG,"indice mis a 0 pour : nom " + cas + " " + nombre);
            //print(pred)
            res.stem = "";
            res.desinence = "";
            return res;
        }

        String[] listeStems = {motDico.stem1, motDico.stem2, motDico.stem3, motDico.stem4};


        Desinence d = resTemp.get(indice);
        int indiceStem = 0; //d.numRad - 1;
        //String bonstem = listeStems[indiceStem];

        res.stem = listeStems[indiceStem];
        res.desinence = d.term;
        res.desinence = ChangeDiphtongue.changeDiphtongue(res.desinence);
        //Log.d(TAG,"res : nom " + res.stem + " " + res.desinence);
        return res;
    }



    //===========================    PRON ========================================================
    StemDesinence rechercheDesinencePRON(EnregDico motDico, String cas, String nombre, String genre, Boolean ToutesFormes) {
        StemDesinence res = new StemDesinence();
        int decl1 = motDico.decl1;
        int decl2 = motDico.decl2;


        int indice = -10;
        String rechQuery = "SELECT POS, decl1, decl2, cas, nombre, genre, numRad, term, Age, Freq FROM INFLEXION WHERE ";
        rechQuery += "decl1 =" + decl1 + " AND decl2 <= " + decl2 + " AND cas ='" + cas + "' ";
        rechQuery += " AND nombre ='" + nombre + "' AND POS = 'PRON' ORDER BY decl2" ;  //AND genre ='" + genre + "'"

        ArrayList<Desinence> resTemp = new ArrayList<Desinence>();

        try {

            Cursor cursor = myDataBase.rawQuery(rechQuery, null);
            int i = 0;

            while (cursor.moveToNext()) {

                if (correspondGenre(genre,cursor.getString(5))) {
                    if (cursor.getString(9).equals("A") || ToutesFormes) {
                        indice = i;
                    }
                }
                i += 1 ;

                Desinence des = new Desinence();
                des.numRad = cursor.getInt(6);

                if (cursor.getString(7) == null) {
                    des.term = "";
                }
                else {
                    des.term = cursor.getString(7);
                }
                resTemp.add(des);
            }
            cursor.close();
            //  close();


        } catch(SQLiteException sqle){
            throw sqle;
        }

        if (indice < 0) {
            indice = 0;
            //print("indice mis a 0 pour : nom " + cas + " " + nombre)
            //print(pred)
            res.stem = "";
            res.desinence = "";
            return res;
        }

        String[] listeStems = {motDico.stem1, motDico.stem2, motDico.stem3, motDico.stem4};
        Desinence d = resTemp.get(indice);
        int indiceStem = d.numRad - 1;
        //String bonstem = listeStems[indiceStem];

        res.stem = listeStems[indiceStem];
        res.desinence = d.term;
        res.desinence = ChangeDiphtongue.changeDiphtongue(res.desinence);

        return res;
    }

    int indexScroll(String aPartirDe) {
// il y a des mots avec yo ds champ mot -> pbb .....
        String query = "SELECT _id,entreemin FROM BASEENTREES WHERE entreemin >= '" + aPartirDe  +  "' ORDER BY _id";

        // openDataBase();
        Cursor cursor = myDataBase.rawQuery(query, null);
        int i = 0;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            i = cursor.getInt(0);
        }

        cursor.close();
        //  close();
        return i;
    }

    ArrayList<Desinence> effectueRechercheDesinences(String query, boolean ToutesFormes) {
        ArrayList<Desinence> res = new ArrayList();
        try {
            Cursor cursor = myDataBase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Desinence des = new Desinence();
                //Numero pos decl1 decl2 cas genre type temps voix mode personne nombre numrad term age freq
                //String rechQuery = "SELECT POS, decl1, decl2, cas, nombre, genre, numRad, term, Age, Freq FROM LatinInfl_
                des.POS = cursor.getString(1);
                des.decl1 = cursor.getInt(2);
                des.decl2 = cursor.getInt(3);

                des.cas = cursor.getString(4);
                des.genre = cursor.getString(5);
                des.type = cursor.getString(6);
                des.temps = cursor.getString(7);
                des.voix = cursor.getString(8);
                des.mode = cursor.getString(9);
                des.personne = cursor.getString(10);
                des.nombre = cursor.getString(11);
                des.numRad = cursor.getInt(12);

                des.term = cursor.getString(13);
                des.age = cursor.getString(14);
                des.freq = cursor.getString(15);

                if (des.cas == null) {
                    des.cas = "";
                }
                if (des.genre == null) {
                    des.genre = "";
                }
                if (des.type == null) {
                    des.type = "";
                }
                if (des.temps == null) {
                    des.temps = "";
                }
                if (des.voix == null) {
                    des.voix = "";
                }
                if (des.mode == null) {
                    des.mode = "";
                }
                if (des.personne == null) {
                    des.personne = "";
                }
                if (des.nombre == null) {
                    des.nombre = "";
                }
                if (des.term == null) {
                    des.term = "";
                }
                if (des.age == null) {
                    des.age = "";
                }
                if (des.freq == null) {
                    des.freq = "";
                }
                //Log.d("dbHelper","dbHelp rech des : " + des.afficheDesinence());
                des.term = ChangeDiphtongue.changeDiphtongue(des.term);
                res.add(des);
            }
            cursor.close();

        } catch(SQLiteException sqle){
            throw sqle;

        }
        return res;
    }

    //================================== RECHERCHE ==========================================================
    //================================== RECHERCHE ==========================================================


    // methode : on cherche par POS des desinences qui pourraient correspondre a la fin de la forme recherchee
    // quand on en a trouve, on cherche si le reste du mot correspond a un lemme dans la table des lemmes
    // enfin, on verifie s'il n'y a pas d'incoherences (ex desinence de N pour lemme de V)

    // A voir :
    // suffixes que ne ve
    //pour pronoms : suffixes quam, dam
    // cas d'assimilation

    // ======= pour recherche
    ArrayList<Desinence> rechercheDesinences(ArrayList desinences,EnregDico lemme) {
        ArrayList<Desinence> listeRes = new ArrayList<Desinence>();
        if (desinences.size() == 0) {
            return listeRes;
        }

        String rechQuery = "SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, personne, nombre, numrad, term, age, freq FROM INFLEXION ";
        rechQuery += " WHERE (decl1 = " + String.valueOf(lemme.decl1) + " OR decl1 = 0) AND decl2 <= " + String.valueOf(lemme.decl2) + " ";
        //rechQuery += POS + " ORDER BY decl2";
        String POS = "AND pos = '" + lemme.POS + "'";
        if (lemme.POS.equals("V")) {
            POS = "AND (pos = 'V' OR pos = 'VPAR')";
        }
        if (lemme.POS.equals("PRON")) {
            POS = "AND (pos = 'PRON' OR pos = 'PRON_I')";
        }

        StringBuilder pTB = new StringBuilder("");

        for (int i = 0; i < desinences.size(); i++) {
            String d = (String) desinences.get(i);

            pTB.append("'" + d + "',");
        }
        String pT = pTB.toString();
        pT = pT.substring(0,pT.length() - 1);

        rechQuery += POS + " AND term IN (" + pT + ") ORDER BY decl2";

        try {
            Cursor cursor = myDataBase.rawQuery(rechQuery, null);
            while (cursor.moveToNext()) {
                Desinence des = new Desinence();
                //Numero pos decl1 decl2 cas genre type temps voix mode personne nombre numrad term age freq
                //String rechQuery = "SELECT POS, decl1, decl2, cas, nombre, genre, numRad, term, Age, Freq FROM LatinInfl_
                des.POS = cursor.getString(1);
                des.decl1 = cursor.getInt(2);
                des.decl2 = cursor.getInt(3);
                des.cas = cursor.getString(4);
                des.genre = cursor.getString(5);
                des.type = cursor.getString(6);
                des.temps = cursor.getString(7);
                des.voix = cursor.getString(8);
                des.mode = cursor.getString(9);
                des.personne = cursor.getString(10);
                des.nombre = cursor.getString(11);
                des.numRad = cursor.getInt(12);
                des.term = cursor.getString(13);
                des.age = cursor.getString(14);
                des.freq = cursor.getString(15);

                if (des.cas == null) {
                    des.cas = "";
                }
                if (des.genre == null) {
                    des.genre = "";
                }
                if (des.type == null) {
                    des.type = "";
                }
                if (des.temps == null) {
                    des.temps = "";
                }
                if (des.voix == null) {
                    des.voix = "";
                }
                if (des.mode == null) {
                    des.mode = "";
                }
                if (des.personne == null) {
                    des.personne = "";
                }
                if (des.nombre == null) {
                    des.nombre = "";
                }
                if (des.term == null) {
                    des.term = "";
                }
                if (des.age == null) {
                    des.age = "";
                }
                if (des.freq == null) {
                    des.freq = "";
                }

                if (lemme.type.equals("DEP") && des.POS.equals("VPAR")) {
                    if (des.voix.equals("PASSIVE") && des.temps.equals("FUT")) {
                        des.voix = "";
                        des.temps = "ADJVB";
                    }

                }

                listeRes.add(des);
            }
            cursor.close();

        } catch(SQLiteException sqle){
            throw sqle;

        }
        return listeRes;
    }

    private String getOrdreDicts() {
        GestionSettings gs = new GestionSettings(myContext);
        return gs.getOrdreDicts();
    }

    ArrayList<EnregDico> rechercheListeLemmeDsTable(ArrayList formes, int optionCasse) {
        //let formeRech = forme.lowercased()
        String rechQuery = "SELECT Numero, mot, motsimple, lemme1R, lemme2R, lemme3R, lemme4R, pos, nombre, genre, decl1, decl2, type, Agefreq, refs, " +
                "refVar, numtableorig, dico FROM BASELATIN ";

        //rechQuery += " WHERE " + typeRecherche + " ='" + mot + "' AND (refs != 'X')";
        //  if (optionDic == "PG") {comp = ""}
        String pred1 = "(motsimpleR IN (";
        String pred2 = "lemme1R IN (";
        String pred3 = "lemme2R IN (";
        String pred4 = "lemme3R IN (";
        String pred5 = "lemme4R IN (";

        StringBuilder pTB = new StringBuilder("");
        for (int i = 0; i < formes.size();i++) {
            String f = (String) formes.get(i);
            pTB.append("'" + f.toLowerCase() + "',");
        }
        String pT = pTB.toString();
        pT = pT.substring(0,pT.length() - 1) + ")";
        //enleveNdernieresLettresDuMot(mot: pT, nLettres: 1) + "}";
        pred1 += pT;
        pred2 += pT;
        pred3 += pT;
        pred4 += pT;
        pred5 += pT + ")";

        String comp = "";
        String optionDic = getOrdreDicts();
        if (optionDic.equals("P")) {
            comp = " AND (dico = 'P' OR dico = 'S')"; // seulement "P"
        }
        if (optionDic.equals("G")) {
            comp = " AND (dico = 'G')";  // seulement "P"
        }
        if (optionDic.equals("GP") || optionDic.equals("PG")) { // inutile
            comp = "";
        }

        rechQuery += "WHERE " + pred1 + " OR " + pred2 + " OR " + pred3 + " OR " + pred4 + " OR " + pred5 + comp + " ORDER BY Numero";
        ArrayList<EnregDico> listeRes = effectueRechercheEnreg(rechQuery);
        //Log.d(TAG,"db 1581 rech liste lemme ds table : " + rechQuery);
        ArrayList<EnregDico> listeResFinale = new ArrayList<EnregDico>();

        if (listeRes.size() > 0) {
            ArrayList<EnregDico> lG = new ArrayList<EnregDico>();
            ArrayList<EnregDico> lP = new ArrayList<EnregDico>();

            for (int i = 0; i < listeRes.size(); i++) {

                EnregDico enreg = listeRes.get(i);
                boolean enregOK = true;
                String mot = enreg.mot;
                //Log.d(TAG,"db 1593 rech liste lemme ds table : " + enreg.motOrig + " - opt casse : " + String.valueOf(optionCasse));
                String l1mot = mot.substring(0,1);

                if (optionCasse == 1) { // 1ere lettre min
                    if (!l1mot.equals(l1mot.toLowerCase())) {
                        enregOK = false;
                    }
                }
                if (optionCasse == 2) { // 1ere lettre maj
                    if (!l1mot.equals(l1mot.toUpperCase())) {
                        enregOK = false;
                    }
                }


                if (enregOK) {
                    if (enreg.POS.equals("NP") || enreg.POS.equals("NPP")) {
                        enreg.POS = "N";
                    }
                    if (enreg.POS.equals("ADJP")) {
                        enreg.POS = "ADJ";
                    }
                    if (enreg.POS.equals("ADVP")) {
                        enreg.POS = "ADV";
                    }
                    if (enreg.POS.equals("VPAR_ADJ")) {
                        enreg.POS = "ADJ";
                    }
                    if (enreg.dico.equals("P") || enreg.dico.equals("S")) {
                        lP.add(enreg);
                    }
                    else {
                        lG.add(enreg);
                    }
                    //Log.d(TAG,"db 1612 rech liste lemme ds table : " + enreg.motOrig);
                }
            }
            if (optionDic.equals("PG")) {
                for (EnregDico e : lP) {
                    listeResFinale.add(e);
                }
                for (EnregDico e : lG) {
                    listeResFinale.add(e);
                }
            }
            else {
                for (EnregDico e : lG) {
                    listeResFinale.add(e);
                }
                for (EnregDico e : lP) {
                    listeResFinale.add(e);
                }
            }
            // chercher aussi ds le dicoGaffiot car certains mots ont 1 entree ds Base Latin et d'autres seult ds Gaffiot
            // a remedier  !!!!
        }

        return listeResFinale;
    }

    //============================









}
