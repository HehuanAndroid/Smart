package com.vcom.smart.fvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * @Author Lzz
 * @Date 2020/10/27 18:45
 */
public class SceneFVM extends AndroidViewModel {

    private  SceneFvmCallBack callBack;

    public SceneFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(SceneFvmCallBack callBack) {
        this.callBack = callBack;
    }

    public void goGuideScene(){
        callBack.goGuideScene();
    }

    public void addScene(){
        callBack.addScene();
    }

    public interface SceneFvmCallBack{
        void goGuideScene();
        void addScene();
    }
}
