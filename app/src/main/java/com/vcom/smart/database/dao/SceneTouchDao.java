package com.vcom.smart.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.vcom.smart.database.entity.SceneTouchDB;

import java.util.List;

@Dao
public interface SceneTouchDao {

    @Insert
    void insertSceneSwitch(SceneTouchDB... scene);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSceneSwitch(SceneTouchDB scene);

    @Delete
    void deleteSceneSwitch(SceneTouchDB scene);

    @Query("select * from scenetouchdb where meshAddress==:meshAddress")
    List<SceneTouchDB> getSceneSwitchByMesh(int meshAddress);

    @Query("delete from scenetouchdb")
    void cleanData();

}
