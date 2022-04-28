package com.vcom.smart.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.vcom.smart.database.entity.SceneSwitchDB;

/**
 * @Author Lzz
 * @Date 2020/10/24 16:37
 */

@Dao
public interface SceneSwitchDao {

    @Insert
    void insertSceneSwitch(SceneSwitchDB... scene);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSceneSwitch(SceneSwitchDB scene);

    @Query("select * from sceneswitchdb where meshAddress==:meshAddress and sceneId==:sceneId")
    SceneSwitchDB getSceneSwitchByMesh(int meshAddress, int sceneId);

}
