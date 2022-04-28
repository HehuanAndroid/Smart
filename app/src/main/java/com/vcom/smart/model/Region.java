package com.vcom.smart.model;

import java.util.List;

/**
 * @Author Lzz
 * @Date 2020/11/3 11:48
 */
public class Region {

    private String spaceId;
    private String spaceName;

    private List<Scene> sceneList;

    public Region(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public List<Scene> getSceneList() {
        return sceneList;
    }

    public void setSceneList(List<Scene> sceneList) {
        this.sceneList = sceneList;
    }

}
