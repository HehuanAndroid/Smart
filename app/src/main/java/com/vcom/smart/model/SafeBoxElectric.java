package com.vcom.smart.model;

/**
 * @author Banlap on 2021/1/19
 */
public class SafeBoxElectric {
    private String deviceName;
    private String deviceRoute;
  /*
    private String routeAllPower;
    private String routeLeakage;
    private String routeTemperature;
    private String routeElectric;
    private String routeVoltage;
    private String routePower;*/

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceRoute() {
        return deviceRoute;
    }

    public void setDeviceRoute(String deviceRoute) {
        this.deviceRoute = deviceRoute;
    }
}
