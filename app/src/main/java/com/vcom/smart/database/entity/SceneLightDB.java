package com.vcom.smart.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class SceneLightDB implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int lightSceneId;

    @ColumnInfo
    private int sceneId;
    @ColumnInfo
    private int meshAddress;

    @ColumnInfo
    private int mode = 1;
    @ColumnInfo
    private byte brightness = 50;
    @ColumnInfo
    private byte temperature = 50;
    @ColumnInfo
    private byte red = 0;
    @ColumnInfo
    private byte green = 0;
    @ColumnInfo
    private byte blue = 0;

    public int getLightSceneId() {
        return lightSceneId;
    }

    public void setLightSceneId(int lightSceneId) {
        this.lightSceneId = lightSceneId;
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
}
