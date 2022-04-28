package com.vcom.smart.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.telink.bluetooth.light.ConnectionStatus;

import java.io.Serializable;

@Entity
public class DeviceDB implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int devId;

    @ColumnInfo
    private String devName;
    @ColumnInfo
    private String devMac;
    @ColumnInfo
    private int meshAddress;
    @ColumnInfo
    private int productUuid;
    @ColumnInfo
    private String firmwareVersion;

    @ColumnInfo
    private int mode;
    @ColumnInfo
    private byte brightness;
    @ColumnInfo
    private byte temperature;
    @ColumnInfo
    private byte red;
    @ColumnInfo
    private byte green;
    @ColumnInfo
    private byte blue;

    @Ignore
    private ConnectionStatus connectionStatus;
    @Ignore
    private byte[] longTermKey = new byte[16];
    @Ignore
    private byte[] switchStatus;

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevMac() {
        return devMac;
    }

    public void setDevMac(String devMac) {
        this.devMac = devMac;
    }

    public int getMeshAddress() {
        return meshAddress;
    }

    public void setMeshAddress(int meshAddress) {
        this.meshAddress = meshAddress;
    }

    public int getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(int productUuid) {
        this.productUuid = productUuid;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
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

    public byte[] getLongTermKey() {
        return longTermKey;
    }

    public void setLongTermKey(byte[] longTermKey) {
        this.longTermKey = longTermKey;
    }

    public byte[] getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(byte[] switchStatus) {
        this.switchStatus = switchStatus;
    }
}
