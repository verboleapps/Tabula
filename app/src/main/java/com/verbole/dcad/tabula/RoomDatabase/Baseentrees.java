package com.verbole.dcad.tabula.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "BASEENTREES")
public class Baseentrees {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private int _id;
    @ColumnInfo(name = "num")
    private int num;
    @ColumnInfo(name = "entreemin")
    private String entreemin;
    @ColumnInfo(name = "entree")
    private String entree;
    @ColumnInfo(name = "dico")
    private String dico;

    public Baseentrees(int _id, int num, String entreemin, String entree, String dico) {
        this._id = _id;
        this.num = num;
        this.entreemin = entreemin;
        this.entree = entree;
        this.dico = dico;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getEntreemin() {
        return entreemin;
    }

    public void setEntreemin(String entreemin) {
        this.entreemin = entreemin;
    }

    public String getEntree() {
        return entree;
    }

    public void setEntree(String entree) {
        this.entree = entree;
    }

    public String getDico() {
        return dico;
    }

    public void setDico(String dico) {
        this.dico = dico;
    }
}
