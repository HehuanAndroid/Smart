package com.vcom.smart.dvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.vcom.smart.utils.Util;

/**
 * @Author Lzz
 * @Date 2020/10/28 13:51
 */
public class RequestPermissionDVM extends AndroidViewModel {


    private RequestCallBack callBack;

    public RequestPermissionDVM(@NonNull Application application) {
        super(application);
    }

    public void openBle() {
        Util.openBle(getApplication());
    }

    public void openGps() {
        callBack.openGPS();
    }

    public void requestPermission() {
        callBack.requestPermission();
    }

    public void setCallBack(RequestCallBack callBack) {
        this.callBack = callBack;
    }

    public interface RequestCallBack {
        void openGPS();
        void requestPermission();
    }


}
