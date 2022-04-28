package com.vcom.smart.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @Author Lzz
 * @Date 2020/10/24 16:38
 */

@Entity
public class SceneSwitchDB {

    @PrimaryKey(autoGenerate = true)
    private int switchSceneId;

    @ColumnInfo
    private int sceneId;
    @ColumnInfo
    private int meshAddress;
    @ColumnInfo
    private boolean status;

    public int getSwitchSceneId() {
        return switchSceneId;
    }

    public void setSwitchSceneId(int switchSceneId) {
        this.switchSceneId = switchSceneId;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getMeshAddress() {
        return meshAddress;
    }

    public void setMeshAddress(int meshAddress) {
        this.meshAddress = meshAddress;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
