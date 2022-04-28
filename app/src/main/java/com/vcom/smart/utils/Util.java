package com.vcom.smart.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;

import com.telink.bluetooth.LeBluetooth;
import com.telink.util.ContextUtil;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Util {

    public static boolean isBleOpen() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    public static boolean isGpsOPen(final Context context) {
        return ContextUtil.isLocationEnable(context);
    }

    public static void openBle(Context context) {
        if (LeBluetooth.getInstance().isSupport(context)) {
            LeBluetooth.getInstance().enable(context);
        }
    }

    public static void closeBle(Context context) {
        LeBluetooth.getInstance().disable(context);
    }

    public static boolean isLocationPermissionReady(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean accessFindLocation = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", context.getPackageName()));
        boolean accessCoarseLocation = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.ACCESS_COARSE_LOCATION", context.getPackageName()));
        return accessFindLocation && accessCoarseLocation;
    }

    public static byte[] byteMergerAll(byte[]... args) {
        int length_byte = 0;
        for (byte[] b : args) {
            length_byte += b.length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (byte[] b : args) {
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = StandardCharsets.UTF_8;
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }
}
