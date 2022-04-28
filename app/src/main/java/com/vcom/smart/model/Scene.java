package com.vcom.smart.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Lzz
 * @Date 2020/11/3 13:54
 */
public class Scene{

    private String sceneId;
    private String sceneName;
    private String sceneImg;
    private String spaceId;
    private String sceneMeshId;

    private List<Equip> userEquipList;

    private boolean isCheck = false;

    public Scene() {
    }

    public Scene(String sceneName, String sceneImg) {
        this.sceneName = sceneName;
        this.sceneImg = sceneImg;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneImg() {
        return sceneImg;
    }

    public void setSceneImg(String sceneImg) {
        this.sceneImg = sceneImg;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSceneMeshId() {
        return sceneMeshId;
    }

    public void setSceneMeshId(String sceneMeshId) {
        this.sceneMeshId = sceneMeshId;
    }

    public List<Equip> getUserEquipList() {
        return userEquipList;
    }

    public void setUserEquipList(List<Equip> userEquipList) {
        this.userEquipList = userEquipList;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
