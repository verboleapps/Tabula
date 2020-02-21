package com.verbole.dcad.tabula;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GestionFichiers {
    Context myContext;
    String dirName = "Tabula";

    static String TAG = "GestionFichiers ";

    public GestionFichiers(Context context) {
        myContext = context;
    }

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

    File getExternalStorage(Activity act) {
        return act.getExternalFilesDir(null);

    }

    void creeDirectoryStorage(Activity act) {

        File sdcard = act.getExternalFilesDir(null);  //storageFile();
        File myDir = new File(sdcard, dirName);
        Log.d(ActivitePrincipale2.TAG,TAG + "cree direct stock ? : " + myDir.toString());
        if(!myDir.exists())
            myDir.mkdirs();
    }

    File getDirectoryStorage(Activity act) {
        File stor = getExternalStorage(act); //myContext.getFilesDir();//storageFile();
        File myDir = new File(stor, "Tabula");
Log.d(ActivitePrincipale2.TAG,TAG + "directory storage " + myDir.getAbsolutePath());
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
            /*
            File listeFich[] = myDir.listFiles();
            if (listeFich != null) {
                for (int i = 0; i <listeFich.length ; i++) {
                    res.add(listeFich[i].getName());
                    Log.d("==path=", listeFich[i].getAbsolutePath());
                }
            }
            */

        }

        return res;
    }

    String getNomFichierFromPath(String chemin) {
        String nf = "";
        if (chemin != null) {
            if (!chemin.isEmpty()) {
                File f = new File(chemin);
                return f.getName();
            }
            else {
                Log.d(ActivitePrincipale2.TAG,TAG + "chemin vide");
            }
        }
        else {
            Log.d(ActivitePrincipale2.TAG,TAG + "chemin null");
        }


        return nf;
    }

    private static void getFilesRecursive(File pFile, List<String> res)
    {
        if (pFile.canRead()) {
            for(File file : pFile.listFiles())
            {
                if(file.isDirectory())
                {
                    getFilesRecursive(file,res);
                }
                else
                {
                    res.add(file.getAbsolutePath());

                }
            }
        }

    }
    private static void getFileNamesRecursive(File pFile, List<String> res)
    {
        if (pFile.canRead()) {
            for(File file : pFile.listFiles())
            {
                if(file.isDirectory())
                {
                    getFileNamesRecursive(file,res);
                }
                else
                {
                    res.add(file.getName());
                }
            }
        }
        else {
            Log.d(ActivitePrincipale2.TAG,TAG + "peut pas lire ...");
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
            /*
            File listeFich[] = myDir.listFiles();
            if (listeFich != null) {

                for (int i = 0; i<listeFich.length ; i++) {
                    res.add(listeFich[i].getAbsolutePath());
                    //Log.d("==path=", listeFich[i].getAbsolutePath());
                }
            }
            */
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

    String chargeFichiersAssets(String nomDossier, String nomFichier, String typeFichier) {
        String res = "";
        AssetManager asmatt = myContext.getAssets();
        InputStream input;
        //String path = nomDossier + "/" + nomFichier;
        try {
            input = asmatt.open(nomDossier + "/" + nomFichier + "." + typeFichier);
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

    String readStringFromInputStream(Activity act, Uri uri) {
        String res = "";
        try {
            StringBuffer sb = new StringBuffer();
            InputStream inputStream = act.getContentResolver().openInputStream(uri);
            int sizeAvailable = inputStream.available(); //fis.available();
            int size = Math.min(sizeAvailable,8192);
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();
            sb.append(buffer);
            res = sb.toString(); //new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    String readStringFromPath(String pathFichier) {
        String cf = "";
        try {
            FileInputStream fis = new FileInputStream (new File(pathFichier));
            int sizeAvailable = fis.available(); //fis.available();
            int size = Math.min(sizeAvailable,8192);
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            cf = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cf;
    }

    /**
     *
     * @param context
     * @param databaseName
     *
     * CORRECT
     * copie la base de donnes du dossier Asset vers le dossier database (et non le dossier files)
     * de l'application dans le device
     */
    static public void copyAssetDatabase(Context context, String databaseName) {
        final File dbPath = context.getDatabasePath(databaseName);

        Log.d(ActivitePrincipale2.TAG,TAG + "bon path : " + dbPath);
        // If the database already exists, return
        if (dbPath.exists()) {
            return;
        }

        // Make sure we have a path to the file
        dbPath.getParentFile().mkdirs();

        // Try to copy database file
        try {

            // parfois ds Assets dossier database -> l'ajouter ds path
            final InputStream inputStream = context.getAssets().open(databaseName);
            final OutputStream output = new FileOutputStream(dbPath);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            inputStream.close();
        }
        catch (IOException e) {
            Log.d(ActivitePrincipale2.TAG, TAG + "Failed to open file", e);
            e.printStackTrace();
        }
    }

    /**
     *
     * @param context
     * @param databaseName
     * MAUVAIS !!!
     * copie la base de donnes du dossier Asset vers le dossier files
     *    de l'application dans le device
     */

    static public void copyDataBase_Bad(Context context, String databaseName) {
        //Open your local db as the input stream
        final String dbPath = context.getFilesDir().getPath() + "/databases/";
        Log.d(ActivitePrincipale2.TAG,TAG + "mauvais path pour db : " + dbPath);

        try {
            InputStream myInput = context.getAssets().open(databaseName);

            // Path to the just created empty db
            String outFileName = dbPath + databaseName;
            //Open the empty db as the output stream

            OutputStream myOutput = new FileOutputStream(outFileName);
            //transfer bytes from the inputfile to the outputfile
            //https://stackoverflow.com/questions/236861/how-do-you-determine-the-ideal-buffer-size-when-using-fileinputstream
            byte[] buffer = new byte[8192]; // old : 1024
            int length;


            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();

            myInput.close();
        }
        catch (IOException e) {
            Log.d(ActivitePrincipale2.TAG, TAG + "Failed to open file", e);
            e.printStackTrace();
        }

    }
}
