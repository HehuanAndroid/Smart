package com.vcom.smart.model;

public class SafeBox {
    private String deviceId;
    private String deviceName;
    private String city;
    private int showDetail;

    public int getShowDetail() {
        return showDetail;
    }

    public void setShowDetail(int showDetail) {
        this.showDetail = showDetail;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
