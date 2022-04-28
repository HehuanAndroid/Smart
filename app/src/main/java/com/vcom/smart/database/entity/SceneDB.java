package com.vcom.smart.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class SceneDB implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int sceneId;

    @ColumnInfo
    private String sceneName;

    @Ignore
    private boolean isChecked = false;

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "SceneDB{" +
                "sceneId=" + sceneId +
                ", sceneName='" + sceneName + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
