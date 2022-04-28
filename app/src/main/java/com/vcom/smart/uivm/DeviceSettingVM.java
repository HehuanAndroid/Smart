package com.vcom.smart.uivm;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.gson.reflect.TypeToken;
import com.vcom.smart.R;
import com.vcom.smart.model.TouchSwitch;
import com.vcom.smart.request.ApiLoader;
import com.vcom.smart.request.ApiObserver;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.utils.GsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * @Author Lzz
 * @Date 2020/11/6 13:33
 */
public class DeviceSettingVM extends AndroidViewModel {

    private DeviceSettingVmCallback callback;

    public DeviceSettingVM(@NonNull Application application) {
        super(application);
    }

    public void setCallback(DeviceSettingVmCallback callback) {
        this.callback = callback;
    }

    public void viewBack() {
        callback.viewBack();
    }

    public void updateTouchSwitchScene(String equipId, int index, String sceneId) {

        Map<String, Object> map = new HashMap<>();
        map.put("userEquipId", equipId);
        map.put("sceneId", sceneId);
        map.put("sequence", index);
        map.put("userId", VcomSingleton.getInstance().getLoginUser().getUserId());

        ApiLoader.getApi().updateSceneSwitch(GsonUtil.getInstance().toJson(map), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                Toast.makeText(getApplication(), getApplication().getString(R.string.toast_update_success),Toast.LENGTH_SHORT).show();
                getTouchSwitchSceneList(equipId);
            }

            @Override
            protected void onFailure() {
                Toast.makeText(getApplication(), getApplication().getString(R.string.toast_update_error),Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onError() {
                Toast.makeText(getApplication(), getApplication().getString(R.string.toast_update_error),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTouchSwitchSceneList(String equipId) {
        ApiLoader.getApi().getSceneSwitch(VcomSingleton.getInstance().getLoginUser().getUserId(), equipId,
                new ApiObserver<ResponseBody>() {
                    @Override
                    protected void showMessage(String message) {
                        //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onSuccess(String data) {
                        Toast.makeText(getApplication(), getApplication().getString(R.string.toast_query_success),Toast.LENGTH_SHORT).show();
                        callback.refreshTouchSceneList(GsonUtil.getInstance().json2List(data, new TypeToken<List<TouchSwitch>>() {
                        }.getType()));
                    }

                    @Override
                    protected void onFailure() {
                        Toast.makeText(getApplication(), getApplication().getString(R.string.toast_query_error),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onError() {
                        Toast.makeText(getApplication(), getApplication().getString(R.string.toast_query_error),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface DeviceSettingVmCallback {
        void viewBack();

        void refreshTouchSceneList(List<TouchSwitch> list);
    }

}
