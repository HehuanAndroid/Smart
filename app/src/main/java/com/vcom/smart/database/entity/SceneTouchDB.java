package com.vcom.smart.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class SceneTouchDB implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int touchSceneId;

    @ColumnInfo
    private int meshAddress;
    @ColumnInfo
    private int sceneId;
    @ColumnInfo
    private int touchSceneIndex;

    public int getTouchSceneId() {
        return touchSceneId;
    }

    public void setTouchSceneId(int touchSceneId) {
        this.touchSceneId = touchSceneId;
    }

    public int getMeshAddress() {
        return meshAddress;
    }

    public void setMeshAddress(int meshAddress) {
        this.meshAddress = meshAddress;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getTouchSceneIndex() {
        return touchSceneIndex;
    }

    public void setTouchSceneIndex(int touchSceneIndex) {
        this.touchSceneIndex = touchSceneIndex;
    }
}
