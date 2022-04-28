package com.vcom.smart.server;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.LightService;

public class VcomService extends LightService {

    private static VcomService service;

    public static VcomService getInstance() {
        return service;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new LocalBinder();
        }
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        if (mAdapter == null) {
            mAdapter = new LightAdapter();
        }
        mAdapter.start(this);
    }

    public class LocalBinder extends Binder {
        public VcomService getService() {
            return VcomService.this;
        }
    }

}
