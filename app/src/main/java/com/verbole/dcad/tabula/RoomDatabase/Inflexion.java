package com.verbole.dcad.tabula.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "INFLEXION")
public class Inflexion {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Numero")
    private int Numero;
    @ColumnInfo(name = "pos")
    private String pos;
    @ColumnInfo(name = "decl1")
    private int decl1;
    @ColumnInfo(name = "decl2")
    private int decl2;
    @ColumnInfo(name = "cas")
    private String cas;
    @ColumnInfo(name = "genre")
    private String genre;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "temps")
    private String temps;
    @ColumnInfo(name = "voix")
    private String voix;
    @ColumnInfo(name = "mode")
    private String mode;
    @ColumnInfo(name = "personne")
    private String personne;
    @ColumnInfo(name = "nombre")
    private String nombre;
    @ColumnInfo(name = "numrad")
    private int numrad;
    @ColumnInfo(name = "terme")
    private String terme;
    @ColumnInfo(name = "age")
    private String age;
    @ColumnInfo(name = "freq")
    private String freq;

    public Inflexion(int Numero, String pos, int decl1, int decl2, String cas, String genre, String type, String temps, String voix, String mode, String personne, String nombre, int numrad, String terme, String age, String freq) {
        Numero = Numero;
        this.pos = pos;
        this.decl1 = decl1;
        this.decl2 = decl2;
        this.cas = cas;
        this.genre = genre;
        this.type = type;
        this.temps = temps;
        this.voix = voix;
        this.mode = mode;
        this.personne = personne;
        this.nombre = nombre;
        this.numrad = numrad;
        this.terme = terme;
        this.age = age;
        this.freq = freq;
    }

    public int getNumero() {
        return Numero;
    }

    public String getPos() {
        return pos;
    }

    public int getDecl1() {
        return decl1;
    }

    public int getDecl2() {
        return decl2;
    }

    public String getCas() {
        return cas;
    }

    public String getGenre() {
        return genre;
    }

    public String getType() {
        return type;
    }

    public String getTemps() {
        return temps;
    }

    public String getVoix() {
        return voix;
    }

    public String getMode() {
        return mode;
    }

    public String getPersonne() {
        return personne;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNumrad() {
        return numrad;
    }

    public String getTerme() {
        return terme;
    }

    public String getAge() {
        return age;
    }

    public String getFreq() {
        return freq;
    }

    public void setNumero(int numero) {
        Numero = numero;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setDecl1(int decl1) {
        this.decl1 = decl1;
    }

    public void setDecl2(int decl2) {
        this.decl2 = decl2;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }

    public void setVoix(String voix) {
        this.voix = voix;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setPersonne(String personne) {
        this.personne = personne;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNumrad(int numrad) {
        this.numrad = numrad;
    }

    public void setTerme(String terme) {
        this.terme = terme;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }
}
