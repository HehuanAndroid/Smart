package com.vcom.smart.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.singleton.VcomSingleton;

import org.greenrobot.eventbus.EventBus;

public class VcomReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (state) {
                        case BluetoothAdapter.STATE_ON:
                            VcomSingleton.getInstance().isBleReady.set(true);
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            VcomSingleton.getInstance().isBleReady.set(false);
                            break;
                    }
                    break;
                case LocationManager.MODE_CHANGED_ACTION:
                case LocationManager.PROVIDERS_CHANGED_ACTION:
//                    LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//                    boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                    VcomSingleton.getInstance().isGpsReady.set(gpsEnabled);
                    break;
            }
            EventBus.getDefault().post(new MessageEvent(MessageEvent.permissionCode));
        }
    }
}
