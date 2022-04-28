package com.vcom.smart.uivm;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.vcom.smart.model.User;
import com.vcom.smart.request.ApiLoader;
import com.vcom.smart.request.ApiObserver;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.utils.GsonUtil;
import com.vcom.smart.utils.SPUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * @Author Lzz
 * @Date 2020/10/27 14:14
 */

public class LoginVM extends AndroidViewModel {

    private MutableLiveData<String> userName = new MutableLiveData<>();
    private MutableLiveData<String> userPassword = new MutableLiveData<>();

    private LoginVmCallBack callBack;

    public LoginVM(Application application) {
        super(application);
    }

    public void setCallBack(LoginVmCallBack callBack) {
        this.callBack = callBack;
    }

    public void showTreaty(View view) {
        String tag = (String) view.getTag();
        callBack.showTreaty(tag);
    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.postValue(userName);
    }

    public MutableLiveData<String> getUserPassword() {
        return userPassword;
    }

    public synchronized void userLogin() {
        if (TextUtils.isEmpty(userName.getValue()) || TextUtils.isEmpty(userPassword.getValue())) {
            return;
        }
        callBack.startLogin();

        Map<String, Object> map = new HashMap<>();
        map.put("userName", userName.getValue());
        map.put("userPassword", userPassword.getValue());

        ApiLoader.getApi().userLogin(map, new ApiObserver<ResponseBody>() {

            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                SPUtil.setValues(getApplication(), "user_param", data);
                VcomSingleton.getInstance().setLoginUser(GsonUtil.getInstance().json2Bean(data, User.class));
                callBack.loginSuccess();
            }

            @Override
            protected void onFailure() {
                callBack.loginFailure();
            }

            @Override
            protected void onError() {
                callBack.loginFailure();
            }
        });
    }

    public void goAppHelp() {
        callBack.goAppHelp();
    }

    public interface LoginVmCallBack {
        void startLogin();

        void loginSuccess();

        void loginFailure();

        void showTreaty(String tag);

        void goAppHelp();
    }

}
