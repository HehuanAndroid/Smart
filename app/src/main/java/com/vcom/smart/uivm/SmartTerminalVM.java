package com.vcom.smart.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class SmartTerminalVM extends AndroidViewModel {
    private SmartTerminalVMCallBack callBack;

    public SmartTerminalVM(@NonNull Application application) {
        super(application);
    }
    public void setCallBack(SmartTerminalVMCallBack callBack) { this.callBack = callBack; }

    public void back() {
        callBack.viewBack();
    }

    public interface SmartTerminalVMCallBack {
        void viewBack();
    }
}
