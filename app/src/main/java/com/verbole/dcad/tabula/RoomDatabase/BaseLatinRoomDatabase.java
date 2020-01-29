package com.verbole.dcad.tabula.RoomDatabase;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.verbole.dcad.tabula.GestionFichiers;

@Database(entities = {Baselatin.class, Baseentrees.class, Inflexion.class}, version = 2) // 9 si migre
public abstract class BaseLatinRoomDatabase extends RoomDatabase {
    public abstract BaseentreesDAO baseentreesDAO();
    public abstract BaselatinDAO baselatinDAO();
    public abstract InflexionDAO inflexionDAO();

    private SupportSQLiteOpenHelper mDbHelper;


    private static BaseLatinRoomDatabase INSTANCE;

// !!!!!!!! il faut que la db soit dans le repertoire /databases de l'app et non ds files ====
    static BaseLatinRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            GestionFichiers.copyAssetDatabase(context,"latin.sqlite");
            synchronized (BaseLatinRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    BaseLatinRoomDatabase.class,
                                    "latin.sqlite").addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return INSTANCE;
    }



    static final Migration MIGRATION_1_2
= new Migration(1, 2) {
        @Override public void migrate(
                SupportSQLiteDatabase database) {
            Log.d("REPO","migre ?");
            // Since we didn’t alter the table, there’s nothing else
            // to do here.

/*
            String query1 = "CREATE TABLE BASEENTREES1 (_id	INTEGER NOT NULL, num INTEGER, entreemin TEXT, entree TEXT, dico TEXT, PRIMARY KEY(_id))";

            String querycopy1 = "INSERT OR REPLACE INTO BASEENTREES1 (_id, num , entreemin , entree , dico)";
            querycopy1 += " SELECT _id, num, entreemin, entree, dico FROM BASEENTREES";


            String query2 = "CREATE TABLE BASELATIN1 (Numero INTEGER NOT NULL,\n" +
                    " num INTEGER, mot TEXT, mot1 TEXT, motsimple TEXT NOT NULL DEFAULT \"\" ,\n" +
                    " motmin TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " lemme1 TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " lemme2 TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " lemme3 TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " lemme4 TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " motsimpleR TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " lemme1R TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " lemme2R TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " lemme3R TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " lemme4R TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " POS TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " nombre TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " genre TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " decl1 INTEGER,\n" +
                    " decl2 INTEGER,\n" +
                    " type TEXT NOT NULL  DEFAULT \"\" ,\n" +
                    " Agefreq TEXT,\n" +
                    " refs TEXT,\n" +
                    " refVar INTEGER,\n" +
                    " NumTableOrig INTEGER,\n" +
                    " dico TEXT,\n" +
                    " PRIMARY KEY(Numero))";

            String querycopy2 = "INSERT OR REPLACE INTO BASELATIN1 (Numero, num , mot , mot1 , motsimple, motmin, ";
            querycopy2 += " lemme1, lemme2, lemme3, lemme4, motsimpleR, lemme1R, lemme2R, lemme3R, lemme4R, ";
            querycopy2 += " POS, nombre, genre, decl1, decl2, type, Agefreq, refs, refVar, NumTableOrig, dico) ";
            querycopy2 += "SELECT Numero, num , mot , mot1 , motsimple, motmin, lemme1, lemme2, lemme3, lemme4, motsimpleR, lemme1R, lemme2R, lemme3R, lemme4R, ";
            querycopy2 += "POS, nombre, genre, decl1, decl2, type, Agefreq, refs, refVar, NumTableOrig, dico FROM BASELATIN";

            String query3 = "CREATE TABLE INFLEXION1 (Numero  INTEGER NOT NULL, pos  VARCHAR, decl1  INTEGER, decl2  INTEGER,\n" +
                    "  cas  VARCHAR, genre  VARCHAR, type  VARCHAR, temps  VARCHAR, voix  VARCHAR, mode  VARCHAR,\n" +
                    "  personne  VARCHAR, nombre  VARCHAR, numrad  INTEGER, term  VARCHAR, age  VARCHAR, freq  VARCHAR,\n" +
                    " PRIMARY KEY( Numero ))\n";

            String querycopy3 = "INSERT OR REPLACE INTO INFLEXION1 (Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode,\n" +
                    "  personne, nombre, numrad, term, age, freq) SELECT Numero, pos, decl1, decl2, cas, genre, type, temps, voix, mode, ";
            querycopy3 +=  " personne, nombre, numrad, term, age, freq FROM INFLEXION";

            database.execSQL(query1);
            database.execSQL(querycopy1);

            database.execSQL(query2);
            database.execSQL(querycopy2);

            database.execSQL(query3);
            database.execSQL(querycopy3);

            database.execSQL("DROP TABLE BASEENTREES");
            database.execSQL("ALTER TABLE BASEENTREES1 RENAME TO BASEENTREES");

            database.execSQL("DROP TABLE BASELATIN");
            database.execSQL("ALTER TABLE BASELATIN1 RENAME TO BASELATIN");

            database.execSQL("DROP TABLE INFLEXION");
            database.execSQL("ALTER TABLE INFLEXION1 RENAME TO INFLEXION");
*/
            /*
            val addIndex = """
      CREATE INDEX IF NOT EXISTS index_Product_idProduct_skuId ON Product(idProduct, skuId)
"""
database.execSQL(addIndex)
             */

        }
    };




}
