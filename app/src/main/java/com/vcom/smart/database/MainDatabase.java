package com.vcom.smart.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.vcom.smart.database.dao.DeviceDao;
import com.vcom.smart.database.dao.SceneDao;
import com.vcom.smart.database.dao.SceneLightDao;
import com.vcom.smart.database.dao.SceneSwitchDao;
import com.vcom.smart.database.dao.SceneTouchDao;
import com.vcom.smart.database.entity.DeviceDB;
import com.vcom.smart.database.entity.SceneDB;
import com.vcom.smart.database.entity.SceneLightDB;
import com.vcom.smart.database.entity.SceneSwitchDB;
import com.vcom.smart.database.entity.SceneTouchDB;

@Database(entities = {DeviceDB.class, SceneDB.class, SceneLightDB.class, SceneTouchDB.class, SceneSwitchDB.class}, version = 2, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase {

    public abstract DeviceDao getDeviceDao();

    public abstract SceneDao getSceneDao();

    public abstract SceneLightDao getSceneLightDao();

    public abstract SceneTouchDao getSceneTouchDao();

    public abstract SceneSwitchDao getSceneSwitchDao();

}
