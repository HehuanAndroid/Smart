package com.vcom.smart.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class DeviceSafeBoxVM extends AndroidViewModel {
    private DeviceSafeBoxVMCallBack callBack;

    public DeviceSafeBoxVM(@NonNull Application application) {
        super(application);
    }
    public void setCallBack(DeviceSafeBoxVMCallBack callBack) { this.callBack = callBack;}

    public void save(){
        callBack.saveData();
    }

    public void back() {
        callBack.viewBack();
    }

    public interface DeviceSafeBoxVMCallBack {
        void viewBack();
        void saveData();
    }
}
