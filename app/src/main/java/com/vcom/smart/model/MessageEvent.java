package com.vcom.smart.model;

import com.telink.bluetooth.event.DeviceEvent;

/**
 * @Author Lzz
 * @Date 2020/11/3 14:46
 */
public class MessageEvent {

    public static final int permissionCode = 0x11F;
    public static final int regionReady = 0x12F;
    public static final int refreshRegion = 0x13F;
    public static final int switchFragment = 0x14F;
    public static final int loginSuccess = 0x15F;
    public static final int defaultMacReady = 0x16F;
    public static final int devTypeReady = 0x17F;
    public static final int getProductReady = 0x18F;
    public static final int refreshEquip = 0x19F;
    public static final int equipReady = 0x20F;

    public static final int deviceStatusChanged = 0x88F;
    public static final int onlineStatusNotify = 0x89F;

    public int msgCode;
    public String msg;
    public byte[] data;
    public DeviceEvent event;

    public MessageEvent(int msgCode) {
        this.msgCode = msgCode;
    }

    public MessageEvent(int msgCode, String msg) {
        this.msgCode = msgCode;
        this.msg = msg;
    }

    public MessageEvent(int msgCode, byte[] data) {
        this.msgCode = msgCode;
        this.data = data;
    }

    public MessageEvent(int msgCode, DeviceEvent event) {
        this.msgCode = msgCode;
        this.event = event;
    }
}
