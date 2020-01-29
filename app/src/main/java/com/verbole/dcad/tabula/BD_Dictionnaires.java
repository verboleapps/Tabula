package com.verbole.dcad.tabula;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dcad on 12/21/17.
 */
interface EcouteInstall {
    void progression(int nombre);
}

public class BD_Dictionnaires  extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DB_NAME = "Dictionnaires.sqlite";
    private final Context myContext;
    private static String DB_PATH;

    private SQLiteDatabase myDataBase;
    private boolean isBaseOuverte = false;
    private static final String TAG = BD_Dictionnaires.class.getSimpleName();
    public EcouteInstall ecouteInstalle;

    private static BD_Dictionnaires sInstance;

    // ...

    public static synchronized BD_Dictionnaires getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new BD_Dictionnaires(context.getApplicationContext());
        }
        return sInstance;
    }


    boolean effaceVieilleBase = false;
    /*
        private static BD_Dictionnaires instance = null;
        //protected BD_Dictionnaires() {
            // Exists only to defeat instantiation.
       // }
        public static BD_Dictionnaires getInstance() {
            if(instance == null) {
                instance = new BD_Dictionnaires(Context context);
            }
            return instance;
        }
    */

    private BD_Dictionnaires(Context context) {
        super(context, DB_NAME,null,DATABASE_VERSION);
        this.myContext = context;
Log.d(TAG,"versionDB : " + String.valueOf(DATABASE_VERSION));
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

        if (effaceVieilleBase) {
            Log.d(TAG,"efface bases Peigne ds base Gaffiot");
            dropTable("PeigneInd");
            dropTable("Peigne");
            myDataBase.execSQL("VACUUM");
            effaceVieilleBase = false;
        }
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
        if (oldVersion == 1) {
            effaceVieilleBase = true;
        }
    }

    private void createVirtualTable(String nomTable) {
        // let txtQuery = "create virtual table if not exists \(nomTable) using fts4 (num, mot, motsimple, def)"
        String txtQuery = "create virtual table if not exists " + nomTable + " using fts4 (mot, motsimple, def)";
        myDataBase.execSQL(txtQuery);
        //print("query cre vt \(txtQuery)")
        // changer version number ??
    }

    private void createTable(String nomTable) {
        String txtQuery = "create table if not exists " + nomTable + " (num integer primary key autoincrement,";
        txtQuery += " mot text, motsimple text, def text)";
        myDataBase.execSQL(txtQuery);
    }

    void dropTable(String nomTable) {
        String txtQuery = "drop table if exists " + nomTable;
        myDataBase.execSQL(txtQuery);
        // changer version number ??
    }




    String getDefinitionGaffiot(String mot, String typeRecherche) {
        String res = "";
        // let query = "select mot, def from GaffiotInd where \(typeRecherche) = '\(mot)'"
        // ne trouve rien car syntaxe pour Virtual table differente : il faut mettre tous les champs
        String motT = mot;
        int distance = 2;
        if (motT.contains("'")) {
            motT = motT.replace("'", "");
            distance = 0;
        }

        String query = "select * from GaffiotInd where " + typeRecherche + " MATCH '" + motT + "'"; //

        if (motT.contains("-")) {
            //motT = motT.replace("-", "");
            distance = 2;
            query = "select * from GaffiotInd where " + typeRecherche + " = '" + motT + "'"; //
        }
        if (motT.contains("*")) {
            //motT = motT.replace("-", "");
            distance = 2;
            query = "select * from GaffiotInd where " + typeRecherche + " = '" + motT + "'"; //
        }
        if (motT.equals("ve")) {
            distance = 3;
            query = "select * from GaffiotInd where " + typeRecherche + " = 've'";
            query += " OR " + typeRecherche + " = -'ve'";
        }
        if (motT.equals("ce")) {
            distance = 2;
            query = "select * from GaffiotInd where " + typeRecherche + " = '-ce'"; //
        }


        // !! MATCH trouve aussi les entrees dont mot CONTIENT mot (ex. cherche ager -> renvoie ager, Albiona ager, etc..)
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            //  openDataBase();
            Cursor cursor = myDataBase.rawQuery(query,null);
            while (cursor.moveToNext())
            {
                String entr = cursor.getString(1);

                if ((entr.length() <= mot.length() + distance) && (entr.length() >= mot.length() - distance)) {
                    String def = cursor.getString(2);
                    res += def + "</BR>";
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

        res = getDefinitionGaffiot(mot, typeRecherche);

        ParseGaffiot pg = new ParseGaffiot();
        res = pg.parseDefSimple(res);



        return res;
    }

    String rechercheDefinitionCensureGaffiot(String mot, String typeRecherche) {
        //typeRecherche : String = "motsimple"
        String res = getDefinitionGaffiot(mot,typeRecherche);
        ParseGaffiot pg = new ParseGaffiot();
        res = pg.parseDefCensure(res);

        return res;
    }

    String rechercheDefinitionCensure_pour_enregDict(EnregDico enreg, String typeRecherche) {
        //typeRecherche : String = "motsimple"
        String res = "";

        //String dsDic = enreg.dico;
        //let mot = enreg.mot
        String mot = "";
        if (typeRecherche.equals("motsimple")) {
            mot = enreg.mot;
        }
        else {
            mot = enreg.motOrig;
        }
        res = rechercheDefinitionCensureGaffiot(mot, typeRecherche);

        return res;
    }

    public List<ResultatFTS> rechercheEnregParDefinition(String motRecherche) {
        return fullTextSearchTable(motRecherche,"GaffiotInd");
    }

    private List<ResultatFTS> fullTextSearchTable(String motRecherche, String nomTable) {
        List<ResultatFTS> res = new ArrayList<>();
        String query = "select mot, motsimple, def from " + nomTable + " where def MATCH '" + motRecherche + "'";
        if (motRecherche.contains("-")) {
            query = "select mot, motsimple, def from " + nomTable + " where def MATCH '\"" + motRecherche + "\"'";
        }
        if (motRecherche.contains("'")) {
            query = "select mot, motsimple, def from " + nomTable + " where def MATCH \"" + motRecherche + "\"";
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
                String motsimple = cursor.getString(1);
                String def = cursor.getString(2);
              //  def = def.replaceAll("([ ,.;->])" + motRecherche + "([ ,.;:?!-<])","$1<span class=\"highlight\">" + motRecherche + "</span>$2");
               // Log.d(TAG,"bd dict 429 mot : " + mot);
                if (!def.isEmpty()) {
                    ResultatFTS resfts = new ResultatFTS(mot,motsimple,motRecherche,def,"G");
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

       // Log.d(TAG,"BDdictionnaire FTS Gaffiot : " + motRecherche + " - trouves : " + String.valueOf(compte));
        //print("fullTSPeigne : \(res.count) trouves")

        return res;
    }

    boolean installeGaffiotFromBufferedReader(BufferedReader br, String nomTable) {
        boolean resultat = false;

        int compte = 0;
        //https://docs.oracle.com/javase/1.5.0/docs/api/java/util/Scanner.html ??
        try {

            dropTable(nomTable);
            createVirtualTable(nomTable);

            myDataBase.beginTransaction();
            String query = "insert into '" + nomTable +  "' (mot,motsimple,def) values (?,?,?) ";
            SQLiteStatement statement = myDataBase.compileStatement(query);


            int compteMemeMot = 0;
            String entreeAnterieureMot = "";
            String entreeAnterieureMotMin = "";
            String entreeAnterieureMotsimple = "";
            String entreeAnterieureDef = "";
            boolean finFichier = false;

            String mot = "";
            String entreeMot = "";

            String entreeMotsimple = "";
            StringBuilder entreeDef = new StringBuilder("");
            StringBuilder ligneBuild = new StringBuilder("");
            String ligne = "";
            String entreeMotT;
            String ligneVide;

            String pat = "%*\\\\sansqu\\{([(a-zA-Z0-9?.'\\-)*! ]+)\\}";
            Pattern patternEntr = Pattern.compile(pat);

            Matcher matcher; // = patternEntr.matcher(value);

            if ((ligne = br.readLine()) != null) {
                //ligneBuild.append(ligne);
            }
            else {
                return false;
            }
            while (!finFichier) {

                mot = "";
                entreeMot = "";
                entreeMotsimple = "";
                entreeDef.setLength(0);
                //  ligne = stddef;
                matcher = patternEntr.matcher(ligne);

                if (matcher.matches()) {
                    mot = matcher.group(1);

                    if ((ligne = br.readLine()) != null) {
                        if (ligne.isEmpty()) {
                            ligne = "</BR>";
                        }
                        ligneBuild.setLength(0);
                        ligneBuild.append(ligne);
                    }
                    else {
                        break;
                    }

                    ligneVide = "";

                    while (!ligneBuild.toString().contains("\\sansqu")) { // car certains mots ont une suite apres 2 lignes vides
                        // ex. securis
                        if (!ligneBuild.toString().isEmpty() && !ligneVide.isEmpty()) {
                            ligneBuild.insert(0,ligneVide);
                            //ligne = ligneVide + ligne;
                            ligneVide = "";
                        }

                        if (ligneBuild.toString().contains("\\entree")) {
                            entreeMot = mot;
                            entreeDef.append(ligne);
                            //entreeDef = entreeDef + ligne;
                            entreeMotsimple = entreeMot;
                            if (entreeMotsimple.length() > 2) {
                                if (entreeMotsimple.substring(1,2).equals(" ")) {
                                    entreeMotsimple = entreeMotsimple.substring(2);
                                }
                            }
                        }
                        else {
                            entreeDef.append(ligne);
                            //entreeDef = entreeDef + ligne;
                        }


                        if ((ligne = br.readLine()) != null) {
                            if (ligne.isEmpty()) {
                                ligneVide += "</BR>";
                            }
                            ligneBuild.setLength(0);
                            ligneBuild.append(ligne);
                        }
                        else {
                            //std::cout << "ici2" << std::endl;
                            //puts(cligne);
                            finFichier = true;
                            break;
                        }
                    }

                    if (entreeMot.equals("1 saeptus") || entreeMot.equals("2 saeptus")) {
                        entreeMot = "saeptus";
                    }

                    if (entreeMot.toLowerCase().equals(entreeAnterieureMot.toLowerCase())) {

                        entreeMotT = String.valueOf(compteMemeMot + 1) + " " + entreeAnterieureMot;
                        statement.clearBindings();
                        statement.bindString(1,entreeMotT);
                        statement.bindString(2,entreeAnterieureMotsimple);
                        statement.bindString(3,entreeAnterieureDef);
                        statement.executeInsert();

                        compteMemeMot += 1;
                    }
                    else {
                        if (compteMemeMot > 0) {

                            entreeMotT = String.valueOf(compteMemeMot + 1) + " " + entreeAnterieureMot;
                            statement.clearBindings();
                            statement.bindString(1,entreeMotT);
                            statement.bindString(2,entreeAnterieureMotsimple);
                            statement.bindString(3,entreeAnterieureDef);
                            statement.executeInsert();

                        }
                        else {
                            if (compte > 0) {
                                statement.clearBindings();
                                statement.bindString(1,entreeAnterieureMot);
                                statement.bindString(2,entreeAnterieureMotsimple);
                                statement.bindString(3,entreeAnterieureDef);
                                statement.executeInsert();

                            }
                        }

                        compteMemeMot = 0;
                    }
                    entreeAnterieureMot = entreeMot;
                    entreeAnterieureMotsimple = entreeMotsimple;
                    entreeAnterieureDef = entreeDef.toString();

                    compte += 1;

                    if (compte % 1000 == 0) {
                        //Log.d(TAG,"compte : " + String.valueOf(compte));
                        ecouteInstalle.progression(compte);
                    }
                }
                else {

                    if ((ligne = br.readLine()) != null) {
                        ligneBuild.setLength(0);
                        ligneBuild.append(ligne);
                    }
                    else {
                        //std::cout << "ici3" << std::endl;
                        //puts(cligne);
                        finFichier = true;
                        break;
                    }
                }

            }
            statement.clearBindings();
            statement.bindString(1,entreeAnterieureMot);
            statement.bindString(2,entreeAnterieureMotsimple);
            statement.bindString(3,entreeAnterieureDef);
            statement.executeInsert();


            myDataBase.setTransactionSuccessful();
            myDataBase.endTransaction();
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        } finally {

        }
        Log.d(TAG,String.valueOf(compte) + " entrees");

        if (compte > 70000) {
            ecouteInstalle.progression(compte);
            resultat = true;
        }


        // on efface le fichier temporaire
        //NSLog(@"path delete %@",filePath);
        //[[NSFileManager defaultManager] removeItemAtPath: filePath error: nil];

        return resultat;
    }


    boolean installeGaffiot(File file, String nomTable) {
        // voir ce post http://stackoverflow.com/questions/1711631/improve-insert-per-second-performance-of-sqlite
        // pour optimisation
        boolean resultat = false;
        try {
            BufferedReader br = new BufferedReader(
                    //new FileReader(file));
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(file)
                            )

                    )
            );
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        } finally {

        }

        int compte = 0;

//https://docs.oracle.com/javase/1.5.0/docs/api/java/util/Scanner.html ??
        try {
            BufferedReader br = new BufferedReader(
                    //new FileReader(file));
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(file)
                            )

                    )
            );

            dropTable(nomTable);
            createVirtualTable(nomTable);

            myDataBase.beginTransaction();
            String query = "insert into '" + nomTable +  "' (mot,motsimple,def) values (?,?,?) ";
            SQLiteStatement statement = myDataBase.compileStatement(query);


            int compteMemeMot = 0;
            String entreeAnterieureMot = "";
            String entreeAnterieureMotMin = "";
            String entreeAnterieureMotsimple = "";
            String entreeAnterieureDef = "";
            boolean finFichier = false;

            String mot = "";
            String entreeMot = "";

            String entreeMotsimple = "";
            StringBuilder entreeDef = new StringBuilder("");
            StringBuilder ligneBuild = new StringBuilder("");
            String ligne = "";
            String entreeMotT;
            String ligneVide;

            String pat = "%*\\\\sansqu\\{([(a-zA-Z0-9?.'\\-)*! ]+)\\}";
            Pattern patternEntr = Pattern.compile(pat);

            Matcher matcher; // = patternEntr.matcher(value);

            if ((ligne = br.readLine()) != null) {
                //ligneBuild.append(ligne);
            }
            else {
                return false;
            }
            while (!finFichier) {

                mot = "";
                entreeMot = "";
                entreeMotsimple = "";
                entreeDef.setLength(0);
                //  ligne = stddef;
                matcher = patternEntr.matcher(ligne);

                if (matcher.matches()) {
                    mot = matcher.group(1);

                    if ((ligne = br.readLine()) != null) {
                        if (ligne.isEmpty()) {
                            ligne = "</BR>";
                        }
                        ligneBuild.setLength(0);
                        ligneBuild.append(ligne);
                    }
                    else {
                        break;
                    }

                    ligneVide = "";

                    while (!ligneBuild.toString().contains("\\sansqu")) { // car certains mots ont une suite apres 2 lignes vides
                        // ex. securis
                        if (!ligneBuild.toString().isEmpty() && !ligneVide.isEmpty()) {
                            ligneBuild.insert(0,ligneVide);
                            //ligne = ligneVide + ligne;
                            ligneVide = "";
                        }

                        if (ligneBuild.toString().contains("\\entree")) {
                            entreeMot = mot;
                            entreeDef.append(ligne);
                            //entreeDef = entreeDef + ligne;
                            entreeMotsimple = entreeMot;
                            if (entreeMotsimple.length() > 2) {
                                if (entreeMotsimple.substring(1,2).equals(" ")) {
                                    entreeMotsimple = entreeMotsimple.substring(2);
                                }
                            }
                        }
                        else {
                            entreeDef.append(ligne);
                            //entreeDef = entreeDef + ligne;
                        }


                        if ((ligne = br.readLine()) != null) {
                            if (ligne.isEmpty()) {
                                ligneVide += "</BR>";
                            }
                            ligneBuild.setLength(0);
                            ligneBuild.append(ligne);
                        }
                        else {
                            //std::cout << "ici2" << std::endl;
                            //puts(cligne);
                            finFichier = true;
                            break;
                        }
                    }

                    if (entreeMot.equals("1 saeptus") || entreeMot.equals("2 saeptus")) {
                        entreeMot = "saeptus";
                    }

                    if (entreeMot.toLowerCase().equals(entreeAnterieureMot.toLowerCase())) {

                        entreeMotT = String.valueOf(compteMemeMot + 1) + " " + entreeAnterieureMot;
                        statement.clearBindings();
                        statement.bindString(1,entreeMotT);
                        statement.bindString(2,entreeAnterieureMotsimple);
                        statement.bindString(3,entreeAnterieureDef);
                        statement.executeInsert();

                        compteMemeMot += 1;
                    }
                    else {
                        if (compteMemeMot > 0) {

                            entreeMotT = String.valueOf(compteMemeMot + 1) + " " + entreeAnterieureMot;
                            statement.clearBindings();
                            statement.bindString(1,entreeMotT);
                            statement.bindString(2,entreeAnterieureMotsimple);
                            statement.bindString(3,entreeAnterieureDef);
                            statement.executeInsert();

                        }
                        else {
                            if (compte > 0) {
                                statement.clearBindings();
                                statement.bindString(1,entreeAnterieureMot);
                                statement.bindString(2,entreeAnterieureMotsimple);
                                statement.bindString(3,entreeAnterieureDef);
                                statement.executeInsert();

                            }
                        }

                        compteMemeMot = 0;
                    }
                    entreeAnterieureMot = entreeMot;
                    entreeAnterieureMotsimple = entreeMotsimple;
                    entreeAnterieureDef = entreeDef.toString();

                    compte += 1;

                    if (compte % 1000 == 0) {
                        //Log.d(TAG,"compte : " + String.valueOf(compte));
                        ecouteInstalle.progression(compte);
                    }
                }
                else {

                    if ((ligne = br.readLine()) != null) {
                        ligneBuild.setLength(0);
                        ligneBuild.append(ligne);
                    }
                    else {
                        //std::cout << "ici3" << std::endl;
                        //puts(cligne);
                        finFichier = true;
                        break;
                    }
                }

            }
            statement.clearBindings();
            statement.bindString(1,entreeAnterieureMot);
            statement.bindString(2,entreeAnterieureMotsimple);
            statement.bindString(3,entreeAnterieureDef);
            statement.executeInsert();


            myDataBase.setTransactionSuccessful();
            myDataBase.endTransaction();
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        } finally {

        }
        
        Log.d(TAG,String.valueOf(compte) + " entrees");

        if (compte > 70000) {
            ecouteInstalle.progression(compte);
            resultat = true;
        }


        // on efface le fichier temporaire
        //NSLog(@"path delete %@",filePath);
        //[[NSFileManager defaultManager] removeItemAtPath: filePath error: nil];


        return resultat;
    }
}
