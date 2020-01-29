package com.verbole.dcad.tabula.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BaselatinDAO {

    @Insert
    void insertEnreg(Baselatin enreg);

    @Query("SELECT * FROM BASELATIN WHERE mot = :name")
    List<Baselatin> findEnreg(String name);

    @Query("DELETE FROM BASELATIN WHERE mot = :name")
    void deleteEnreg(String name);

    @Query("SELECT * FROM BASELATIN")
    LiveData<List<Baselatin>> getAllEnregs();
}
