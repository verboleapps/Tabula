package com.verbole.dcad.tabula.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "BASELATIN")
public class Baselatin {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Numero")
    private int Numero;
    @ColumnInfo(name = "num")
    private int num;
    @ColumnInfo(name = "mot")
    private String mot;
    @ColumnInfo(name = "mot1")
    private String mot1;
    @NonNull
    @ColumnInfo(name = "motsimple")
    private String motsimple;
    @NonNull
    @ColumnInfo(name = "motmin")
    private String motmin;
    @NonNull
    @ColumnInfo(name = "lemme1")
    private String lemme1;
    @NonNull
    @ColumnInfo(name = "lemme2")
    private String lemme2;
    @NonNull
    @ColumnInfo(name = "lemme3")
    private String lemme3;
    @NonNull
    @ColumnInfo(name = "lemme4")
    private String lemme4;
    @NonNull
    @ColumnInfo(name = "motsimpleR")
    private String motsimpleR;
    @NonNull
    @ColumnInfo(name = "lemme1R")
    private String lemme1R;
    @NonNull
    @ColumnInfo(name = "lemme2R")
    private String lemme2R;
    @NonNull
    @ColumnInfo(name = "lemme3R")
    private String lemme3R;
    @NonNull
    @ColumnInfo(name = "lemme4R")
    private String lemme4R;
    @NonNull
    @ColumnInfo(name = "POS")
    private String POS;
    @NonNull
    @ColumnInfo(name = "nombre")
    private String nombre;
    @NonNull
    @ColumnInfo(name = "genre")
    private String genre;
    @ColumnInfo(name = "decl1")
    private int decl1;
    @ColumnInfo(name = "decl2")
    private int decl2;
    @NonNull
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "AgeFreq")
    private String AgeFreq;
    @ColumnInfo(name = "refs")
    private String refs;
    @ColumnInfo(name = "refVar")
    private int refVar;
    @ColumnInfo(name = "NumTableOrig")
    private int NumTableOrig;
    @ColumnInfo(name = "dico")
    private String dico;

    public Baselatin(int Numero, int num, String mot, String mot1, String motsimple, String motmin, String lemme1, String lemme2, String lemme3, String lemme4, String motsimpleR, String lemme1R, String lemme2R, String lemme3R, String lemme4R, String POS, String nombre, String genre, int decl1, int decl2, String type, String AgeFreq, String refs, int refVar, int NumTableOrig, String dico) {
        this.Numero = Numero;
        this.num = num;
        this.mot = mot;
        this.mot1 = mot1;
        this.motsimple = motsimple;
        this.motmin = motmin;
        this.lemme1 = lemme1;
        this.lemme2 = lemme2;
        this.lemme3 = lemme3;
        this.lemme4 = lemme4;
        this.motsimpleR = motsimpleR;
        this.lemme1R = lemme1R;
        this.lemme2R = lemme2R;
        this.lemme3R = lemme3R;
        this.lemme4R = lemme4R;
        this.POS = POS;
        this.nombre = nombre;
        this.genre = genre;
        this.decl1 = decl1;
        this.decl2 = decl2;
        this.type = type;
        this.AgeFreq = AgeFreq;
        this.refs = refs;
        this.refVar = refVar;
        this.NumTableOrig = NumTableOrig;
        this.dico = dico;
    }

    public int getNumero() {
        return Numero;
    }

    public void setNumero(int numero) {
        Numero = numero;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMot() {
        return mot;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public String getMot1() {
        return mot1;
    }

    public void setMot1(String mot1) {
        this.mot1 = mot1;
    }

    public String getMotsimple() {
        return motsimple;
    }

    public void setMotsimple(String motsimple) {
        this.motsimple = motsimple;
    }

    public String getMotmin() {
        return motmin;
    }

    public void setMotmin(String motmin) {
        this.motmin = motmin;
    }

    public String getLemme1() {
        return lemme1;
    }

    public void setLemme1(String lemme1) {
        this.lemme1 = lemme1;
    }

    public String getLemme2() {
        return lemme2;
    }

    public void setLemme2(String lemme2) {
        this.lemme2 = lemme2;
    }

    public String getLemme3() {
        return lemme3;
    }

    public void setLemme3(String lemme3) {
        this.lemme3 = lemme3;
    }

    public String getLemme4() {
        return lemme4;
    }

    public void setLemme4(String lemme4) {
        this.lemme4 = lemme4;
    }

    public String getMotsimpleR() {
        return motsimpleR;
    }

    public void setMotsimpleR(String motsimpleR) {
        this.motsimpleR = motsimpleR;
    }

    public String getLemme1R() {
        return lemme1R;
    }

    public void setLemme1R(String lemme1R) {
        this.lemme1R = lemme1R;
    }

    public String getLemme2R() {
        return lemme2R;
    }

    public void setLemme2R(String lemme2R) {
        this.lemme2R = lemme2R;
    }

    public String getLemme3R() {
        return lemme3R;
    }

    public void setLemme3R(String lemme3R) {
        this.lemme3R = lemme3R;
    }

    public String getLemme4R() {
        return lemme4R;
    }

    public void setLemme4R(String lemme4R) {
        this.lemme4R = lemme4R;
    }

    public String getPOS() {
        return POS;
    }

    public void setPOS(String POS) {
        this.POS = POS;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDecl1() {
        return decl1;
    }

    public void setDecl1(int decl1) {
        this.decl1 = decl1;
    }

    public int getDecl2() {
        return decl2;
    }

    public void setDecl2(int decl2) {
        this.decl2 = decl2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAgeFreq() {
        return AgeFreq;
    }

    public void setAgeFreq(String ageFreq) {
        ageFreq = ageFreq;
    }

    public String getRefs() {
        return refs;
    }

    public void setRefs(String refs) {
        this.refs = refs;
    }

    public int getRefVar() {
        return refVar;
    }

    public void setRefVar(int refVar) {
        this.refVar = refVar;
    }

    public int getNumTableOrig() {
        return NumTableOrig;
    }

    public void setNumTableOrig(int numTableOrig) {
        this.NumTableOrig = numTableOrig;
    }

    public String getDico() {
        return dico;
    }

    public void setDico(String dico) {
        this.dico = dico;
    }
}
