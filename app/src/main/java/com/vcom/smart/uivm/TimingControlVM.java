package com.vcom.smart.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * @author Banlap on 2021/1/19
 */
public class TimingControlVM extends AndroidViewModel {
    private TimingControlVMCallBack callBack;

    public TimingControlVM(@NonNull Application application) {
        super(application);
    }
    public void setCallBack(TimingControlVMCallBack callBack) { this.callBack = callBack;}

    public void back() {
        callBack.viewBack();
    }

    public interface TimingControlVMCallBack {
        void viewBack();
    }
}
