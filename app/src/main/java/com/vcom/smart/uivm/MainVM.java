package com.vcom.smart.uivm;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.gson.reflect.TypeToken;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.vcom.smart.VcomApp;
import com.vcom.smart.model.DefaultMac;
import com.vcom.smart.model.DeviceType;
import com.vcom.smart.model.Equip;
import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.model.Product;
import com.vcom.smart.model.Region;
import com.vcom.smart.model.User;
import com.vcom.smart.request.ApiLoader;
import com.vcom.smart.request.ApiObserver;
import com.vcom.smart.server.VcomService;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.utils.GsonUtil;
import com.vcom.smart.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * @Author Lzz
 * @Date 2020/10/27 11:44
 */
public class MainVM extends AndroidViewModel {

    private MainVmCallBack callBack;

    private User loginUser = VcomSingleton.getInstance().getLoginUser();

    public MainVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MainVmCallBack callBack) {
        this.callBack = callBack;
    }

    public void validateUser() {
        if (VcomSingleton.getInstance().isUserLogin()) {
            String loginData = SPUtil.getValue(getApplication(), "user_param");
            if (TextUtils.isEmpty(loginData)) {
                callBack.validateFailure();
            } else {
                User user = GsonUtil.getInstance().json2Bean(loginData, User.class);
                if (user == null) {
                    callBack.validateFailure();
                    return;
                }
                validateUserStatus(user);
            }
        }
    }

    private void validateUserStatus(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("userPassword", user.getUserPassword());

        ApiLoader.getApi().validateLogin(map, new ApiObserver<ResponseBody>() {

            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                SPUtil.setValues(getApplication(), "user_param", data);
                loginUser = GsonUtil.getInstance().json2Bean(data, User.class);
                VcomSingleton.getInstance().setLoginUser(loginUser);
                getNewRegion();
                callBack.validateSuccess();
            }

            @Override
            protected void onFailure() {
                callBack.validateFailure();
            }

            @Override
            protected void onError() {
                callBack.validateFailure();
            }
        });
    }

    public void getNewRegion() {
        User user = VcomSingleton.getInstance().getLoginUser();
        ApiLoader.getApi().selectAllData(user.getUserId(), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {

            }

            @Override
            protected void onSuccess(String data) {
                Log.e("ROGIN","onSuccess");
                VcomSingleton.getInstance().setUserRegion(GsonUtil.getInstance().json2List(data, new TypeToken<List<Region>>() {}.getType()));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.regionReady));
            }

            @Override
            protected void onFailure() {
                Log.e("ROGIN","onFailure");
            }

            @Override
            protected void onError() {
                Log.e("ROGIN","onError");
            }
        });
    }

    public void getProductInfo() {
        ApiLoader.getApi().getProductInfo(new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
            }

            @Override
            protected void onSuccess(String data) {
                VcomSingleton.getInstance().setUserProduct(GsonUtil.getInstance().json2List(data, new TypeToken<List<Product>>() {
                }.getType()));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.getProductReady));
            }

            @Override
            protected void onFailure() {
            }

            @Override
            protected void onError() {
            }
        });
    }

    public void getDefaultMac() {
        Log.e("test","getDefaultMac");
        ApiLoader.getApi().getDefaultMac(loginUser.getUserId(), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
            }

            @Override
            protected void onSuccess(String data) {
                VcomSingleton.getInstance().setDefaultMacs(GsonUtil.getInstance().json2List(data, new TypeToken<List<DefaultMac>>() {
                }.getType()));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.defaultMacReady));
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

            }
        });
    }

    public void getAllDevType() {
        ApiLoader.getApi().getAllDevType(new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {

            }

            @Override
            protected void onSuccess(String data) {
                VcomSingleton.getInstance().setDeviceTypes(GsonUtil.getInstance().json2List(data, new TypeToken<List<DeviceType>>() {
                }.getType()));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.devTypeReady));
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

            }
        });
    }

    public void getUserAllEquip() {
        ApiLoader.getApi().getAllEquips(loginUser.getUserId(), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
            }

            @Override
            protected void onSuccess(String data) {
                VcomSingleton.getInstance().setUserEquips(GsonUtil.getInstance().json2List(data, new TypeToken<List<Equip>>() {
                }.getType()));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.equipReady));
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

            }
        });
    }



    public void startAutoConnect() {

        //自动连接参数
        LeAutoConnectParameters connectParams = LeAutoConnectParameters.createAutoConnectParameters();
        connectParams.setMeshName(VcomApp.NEW_NAME);
        connectParams.setPassword(VcomSingleton.getInstance().getLoginUser().getMeshPassword());
        connectParams.autoEnableNotification(true);
        connectParams.set(Parameters.PARAM_OFFLINE_TIMEOUT_SECONDS, 40);
        VcomService.getInstance().autoConnect(connectParams);


        //刷新Notify参数, 重新回到主页时不刷新
        LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
        refreshNotifyParams.setRefreshRepeatCount(2);
        refreshNotifyParams.setRefreshInterval(2000);
        //开启自动刷新Notify
        VcomService.getInstance().autoRefreshNotify(refreshNotifyParams);

    }

    public void stopAutoConnect() {
        LeBluetooth.getInstance().stopScan();
    }

    public void mainVmPerformed(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
        }
    }

    private synchronized void onOnlineStatusNotify(NotificationEvent event) {
        byte[] data = (byte[]) event.parse();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.onlineStatusNotify, data));
    }

    private void onDeviceStatusChanged(DeviceEvent event) {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.onlineStatusNotify, event));
    }

    public interface MainVmCallBack {
        void validateSuccess();

        void validateFailure();

    }

}
