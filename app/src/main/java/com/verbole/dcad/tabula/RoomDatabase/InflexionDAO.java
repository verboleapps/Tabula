package com.verbole.dcad.tabula.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InflexionDAO {
    @Insert
    void insertEnreg(Inflexion inflex);

    @Query("SELECT * FROM INFLEXION WHERE pos = :name")
    List<Inflexion> findInflex(String name);

    @Query("DELETE FROM INFLEXION WHERE pos = :name")
    void deleteInflex(String name);

    @Query("SELECT * FROM INFLEXION")
    LiveData<List<Inflexion>> getAllInflex();
}
