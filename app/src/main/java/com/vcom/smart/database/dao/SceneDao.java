package com.vcom.smart.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.vcom.smart.database.entity.SceneDB;

import java.util.List;

@Dao
public interface SceneDao {

    @Insert
    void insertScene(SceneDB... scenes);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateScene(SceneDB scene);

    @Delete
    void deleteScene(SceneDB scene);

    @Query("select * from scenedb")
    List<SceneDB> getAllLocalScene();

    @Query("select * from scenedb where sceneId==:index")
    SceneDB getSceneByIndex(int index);

    @Query("select count(*) from scenedb ")
    int getSceneCount();

}
