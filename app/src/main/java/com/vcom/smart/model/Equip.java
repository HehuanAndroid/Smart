package com.vcom.smart.model;

import com.telink.bluetooth.light.ConnectionStatus;

/**
 * @Author Lzz
 * @Date 2020/11/3 13:54
 */
public class Equip {

    private String userEquipId;
    private String userId;
    private String mac;
    private String equipInfoPid;
    private String createTime;
    private String equipName;
    private String meshAddress;
    private String productUuid;
    private String firmwareVersion;

    private boolean isCheck = false;
    private int mode;
    private byte brightness;
    private byte temperature;
    private byte red;
    private byte green;
    private byte blue;

    private ConnectionStatus connectionStatus;
    private String switchStatus;

    public String getUserEquipId() {
        return userEquipId;
    }

    public void setUserEquipId(String userEquipId) {
        this.userEquipId = userEquipId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getEquipInfoPid() {
        return equipInfoPid;
    }

    public void setEquipInfoPid(String equipInfoPid) {
        this.equipInfoPid = equipInfoPid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEquipName() {
        return equipName;
    }

    public void setEquipName(String equipName) {
        this.equipName = equipName;
    }

    public String getMeshAddress() {
        return meshAddress;
    }

    public void setMeshAddress(String meshAddress) {
        this.meshAddress = meshAddress;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public byte getBrightness() {
        return brightness;
    }

    public void setBrightness(byte brightness) {
        this.brightness = brightness;
    }

    public byte getTemperature() {
        return temperature;
    }

    public void setTemperature(byte temperature) {
        this.temperature = temperature;
    }

    public byte getRed() {
        return red;
    }

    public void setRed(byte red) {
        this.red = red;
    }

    public byte getGreen() {
        return green;
    }

    public void setGreen(byte green) {
        this.green = green;
    }

    public byte getBlue() {
        return blue;
    }

    public void setBlue(byte blue) {
        this.blue = blue;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(String switchStatus) {
        this.switchStatus = switchStatus;
    }
}
