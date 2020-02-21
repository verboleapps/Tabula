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

public class FlashCardsDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "flashCards.db";
    private final Context myContext;
    private static String DB_PATH;
    private boolean isBaseOuverte = false;

    private SQLiteDatabase myDataBase;

    private static final String TAG = FlashCardsDB.class.getSimpleName();

    private static FlashCardsDB sInstance;

    // ...

    public static synchronized FlashCardsDB getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new FlashCardsDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private FlashCardsDB(Context context) {
        super(context, DB_NAME,null,DATABASE_VERSION);
        this.myContext = context;
        Log.d(ActivitePrincipale2.TAG,TAG + " versionDB : " + String.valueOf(DATABASE_VERSION));
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
        Log.d(TAG,"BD_Dictionnaires on upgrade from : " + String.valueOf(oldVersion) + " to : " + String.valueOf(newVersion));
    }

    ArrayList<String> listeListes(int type) {
        // type - 0 : nomTable - 1 : nomListe
        ArrayList<String> res = new ArrayList<String>();
        String query = "select nomTable, nomListe from listeTables ";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();

            Cursor cursor = myDataBase.rawQuery(query,null);

            while (cursor.moveToNext())
            {
                String et = cursor.getString(type);
                res.add(et);
                Log.d(TAG,et);
            }

            cursor.close();
            //  close();

        } catch(SQLiteException | IllegalStateException exc){
            //throw sqle;
            Log.d(ActivitePrincipale2.TAG,TAG + " listelistes - " + exc.toString());
        }

        return res;
    }

    void ajouteCarteAListe(Carte carte, String nomListe) {
        String nomTable = getNomTable(nomListe);
        String query = "insert into '" + nomTable + "' (entree,customDef,pos) values ('" + carte.entree + "','" + carte.customDef + "','" + carte.pos + "')";
        myDataBase.execSQL(query);

        // query update qd customDef change ...
    }

    void deleteCarteFromListe(Carte carte, String nomListe) {
        String nomTable = getNomTable(nomListe);
        String query = "delete from '" + nomTable + "' where entree = '" + carte.entree + "' and pos ='" + carte.pos + "'";
        myDataBase.execSQL(query);
    }

    void renameListeFC(String ancienNom,String nouveauNom) {

        String query = "update 'listeTables' set nomListe = '" + nouveauNom + "' where nomListe = '" + ancienNom + "'";
        myDataBase.execSQL(query);
    }

    void addListeFC(String nomListe) {
        String nt = nomListe.replaceAll("[^A-Za-z0-9]", "_");

        ArrayList<String> lt = listeListes(0);
        int i = 1;
        while (lt.contains(nt)) {
            nt = nt + "_" + String.valueOf(i);
            i += 1;
        }

        String query = "insert into 'listeTables' (nomTable,nomListe) values ('" + nt + "','" + nomListe + "')";
        myDataBase.execSQL(query);
        createTable(nt);

    }
    void supprimeListe(String nomListe) {
        String nomTable = getNomTable(nomListe);
        dropTable(nomTable);

    }
    private void createTable(String nomTable) {
        //CREATE TABLE "BaseVoc" ( `numId` INTEGER NOT NULL, `entree` TEXT, `customDef` TEXT, `pos` TEXT, PRIMARY KEY(`numId`) )
        String txtQuery = "create table if not exists " + nomTable + " (numId integer not null,";
        txtQuery += " entree text, customDef text, pos text, primary key('numId'))";
        myDataBase.execSQL(txtQuery);
    }

    private void dropTable(String nomTable) {

        //Log.d("==","drop table : " + nomTable + " nomT : " + nomTable);
        String txtQuery = "drop table if exists '" + nomTable + "'";
        myDataBase.execSQL(txtQuery);
        // changer version number ??
        String query2 = "DELETE FROM listeTables WHERE nomTable= '" + nomTable + "'";

        myDataBase.execSQL(query2);
    }

    String getNomTable(String nomListe) {
        String res = "";

        String query = "select nomTable, nomListe from listeTables where nomListe = '" + nomListe + "'" ;
        try {

            Cursor cursor = myDataBase.rawQuery(query,null);
            while (cursor.moveToNext())
            {
                res = cursor.getString(0);
            }

            cursor.close();
            //  close();

        } catch(SQLiteException | IllegalStateException exc){
            //throw sqle;
            Log.d(ActivitePrincipale2.TAG,TAG + " getnomTable " +  nomListe + " - " + exc.toString());

        }
        Log.d(ActivitePrincipale2.TAG,TAG + " getnomTable : " + nomListe + " -> " + res);
        return res;
    }

    ArrayList<Carte> getListeCartes(String nomListe) {
        ArrayList<Carte> res = new ArrayList<>();

        String nomTable = getNomTable(nomListe);
        if (nomTable.isEmpty()) {
            return res;
        }

        String query = "select entree,customDef,pos from " + nomTable + " order by entree";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();

            Cursor cursor = myDataBase.rawQuery(query,null);
            int compte = 0;

            while (cursor.moveToNext())
            {
                Carte temp = new Carte();
                temp.entree = cursor.getString(0);
                temp.customDef = cursor.getString(1);
                temp.pos = cursor.getString(2);
                res.add(temp);

                compte += 1;
                //if (compte == 2) { Log.d("===","carte : " + temp.entree + " " + temp.pos); }
            }

            cursor.close();
            //  close();

        } catch(SQLiteException | IllegalStateException exc){
            //throw sqle;
            Log.d(ActivitePrincipale2.TAG,TAG + " getliste cartes " +  nomListe + " - " + exc.toString());

        }

        return res;
    }






}