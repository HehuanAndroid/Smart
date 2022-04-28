package com.vcom.smart.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * @author Banlap on 2021/1/19
 */
public class SecurityWarningVM extends AndroidViewModel {
    private SecurityWarningVMCallBack callBack;

    public SecurityWarningVM(@NonNull Application application) {
        super(application);
    }
    public void setCallBack(SecurityWarningVMCallBack callBack) { this.callBack = callBack;}

    public void back(){
        callBack.viewBack();
    }

    public interface SecurityWarningVMCallBack {
        void viewBack();
    }
}
