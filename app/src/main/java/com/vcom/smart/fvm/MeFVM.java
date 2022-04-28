package com.vcom.smart.fvm;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.ui.LoginActivity;
import com.vcom.smart.utils.ActivityUtil;
import com.vcom.smart.utils.SPUtil;

/**
 * @Author Lzz
 * @Date 2020/10/27 18:45
 */
public class MeFVM extends AndroidViewModel {

    private MeFvmCallBack callBack;

    public MeFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MeFvmCallBack callBack) {
        this.callBack = callBack;
    }

    public void loginOut() {
        SPUtil.setValues(getApplication(), "user_param", "");
        VcomSingleton.getInstance().setLoginUser(null);
        ActivityUtil.finishAllActivity();
        Intent goLoginUI = new Intent(getApplication(), LoginActivity.class);
        goLoginUI.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(goLoginUI);
    }

    public void goWeb(View view) {
        String tag = (String) view.getTag();
        callBack.viewGoWeb(tag);
    }

    public void goAppHelp() {
        callBack.viewGoAppHelp();
    }

    public interface MeFvmCallBack {
        void viewGoWeb(String tag);
        void viewGoAppHelp();
    }

}
