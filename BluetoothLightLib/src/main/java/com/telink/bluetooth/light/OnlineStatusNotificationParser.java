package com.telink.bluetooth.light;

import android.util.Log;

/**
 * @Author Lzz
 * @Date 2020/10/26 13:17
 */
public class OnlineStatusNotificationParser extends NotificationParser<byte[]> {

    private OnlineStatusNotificationParser() {
    }

    public static OnlineStatusNotificationParser create() {
        return new OnlineStatusNotificationParser();
    }

    @Override
    public byte opcode() {
        return Opcode.BLE_GATT_OP_CTRL_DC.getValue();
    }

    @Override
    public byte[] parse(NotificationInfo notifyInfo) {
        return notifyInfo.params;
    }

}
