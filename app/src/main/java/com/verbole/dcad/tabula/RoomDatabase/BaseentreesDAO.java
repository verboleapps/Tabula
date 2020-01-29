package com.verbole.dcad.tabula.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BaseentreesDAO {
    @Insert
    void insertEnreg(Inflexion inflex);

    @Query("SELECT * FROM BASEENTREES WHERE _id = :indice")
    List<Baseentrees> findEntree(int indice);

    @Query("DELETE FROM BASEENTREES WHERE entree = :name")
    void deleteEntree(String name);

    @Query("SELECT * FROM BASEENTREES")
    LiveData<List<Baseentrees>> getAllEntrees();
}
