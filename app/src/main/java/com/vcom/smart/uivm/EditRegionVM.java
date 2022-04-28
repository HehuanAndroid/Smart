package com.vcom.smart.uivm;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.gson.reflect.TypeToken;
import com.vcom.smart.model.Region;
import com.vcom.smart.model.User;
import com.vcom.smart.request.ApiLoader;
import com.vcom.smart.request.ApiObserver;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * @Author Lzz
 * @Date 2020/10/30 14:38
 */
public class EditRegionVM extends AndroidViewModel {

    private EditRegionVmCallBack callBack;

    private final User user = VcomSingleton.getInstance().getLoginUser();

    public EditRegionVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(EditRegionVmCallBack callBack) {
        this.callBack = callBack;
    }

    public void requestAddRegion(String regionName) {
        User user = VcomSingleton.getInstance().getLoginUser();

        ApiLoader.getApi().regionalManagement(user.getUserId(), regionName, new ApiObserver<ResponseBody>() {

            @Override
            protected void showMessage(String message) {
                Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.requestSuccess();
            }

            @Override
            protected void onFailure() {
                callBack.requestFailure();
            }

            @Override
            protected void onError() {
                callBack.requestFailure();
            }
        });
    }

    public void addRegionList(List<Region> regions) {
        User user = VcomSingleton.getInstance().getLoginUser();
        String json = GsonUtil.getInstance().toJson(regions);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("spaceName", json);
        map.put("spaceImg", 0);

        ApiLoader.getApi().addRegionList(map, new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.requestSuccess();
            }

            @Override
            protected void onFailure() {
                callBack.requestFailure();
            }

            @Override
            protected void onError() {
                callBack.requestFailure();
            }
        });

    }

    public void updateRegion(Region region){
        ApiLoader.getApi().modifyArea(user.getUserId(), region.getSpaceId(), region.getSpaceName(), "0", new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.requestSuccess();
            }

            @Override
            protected void onFailure() {
                callBack.requestFailure();
            }

            @Override
            protected void onError() {
                callBack.requestFailure();
            }
        });
    }

    public void deleteRegion(Region region){
        ApiLoader.getApi().deleteArea(user.getUserId(), region.getSpaceId(), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.deleteSuccess(region);
            }

            @Override
            protected void onFailure() {
                callBack.deleteFailure();
            }

            @Override
            protected void onError() {
                callBack.deleteFailure();
            }
        });
    }

    public void getRegion() {
        List<Region> regions = new ArrayList<>();
        ApiLoader.getApi().queryRegion(user.getUserId(), new ApiObserver<ResponseBody>() {

            @Override
            protected void showMessage(String message) {
            }

            @Override
            protected void onSuccess(String data) {
                regions.addAll(GsonUtil.getInstance().json2List(data, new TypeToken<List<Region>>() {}.getType()));
                callBack.newRegionData(regions);
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

            }
        });
    }

    public void goBack() {
        callBack.back();
    }

    public void goAddRegion() {
        callBack.addRegion();
    }

    public interface EditRegionVmCallBack {
        void back();

        void addRegion();

        void requestSuccess();
        void requestFailure();

        void deleteSuccess(Region region);
        void deleteFailure();

        void newRegionData(List<Region> regions);
    }
}
