package com.vcom.smart.model;

/**
 * @Author Lzz
 * @Date 2020/11/11 15:44
 */
public class TouchSwitch {

    private String sceneSwitchId;
    private String userId;
    private String sceneId;
    private String sceneName;
    private String spaceName;
    private int sequence;
    private String productMac;
    private String productPid;
    private String userEquipId;

    public String getSceneSwitchId() {
        return sceneSwitchId;
    }

    public void setSceneSwitchId(String sceneSwitchId) {
        this.sceneSwitchId = sceneSwitchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getProductMac() {
        return productMac;
    }

    public void setProductMac(String productMac) {
        this.productMac = productMac;
    }

    public String getProductPid() {
        return productPid;
    }

    public void setProductPid(String productPid) {
        this.productPid = productPid;
    }

    public String getUserEquipId() {
        return userEquipId;
    }

    public void setUserEquipId(String userEquipId) {
        this.userEquipId = userEquipId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }
}
