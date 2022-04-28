package com.vcom.smart.uivm;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.model.Scene;
import com.vcom.smart.request.ApiLoader;
import com.vcom.smart.request.ApiObserver;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.utils.GsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * @author Banlap on 2021/1/28
 */
public class AddSceneVM extends AndroidViewModel {

    private AddSceneVMCallBack callBack;

    public AddSceneVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(AddSceneVMCallBack callBack) {
        this.callBack = callBack;
    }


    public void viewBack() {
        callBack.viewBack();
    }

    public void goAddEquipToNewScene() {
        callBack.viewGoAddNewEquip();
    }

    public void addEquipForScene(String sceneId, String spaceId, String userEquipId, String param) {
        ApiLoader.getApi().addEquipForScene(sceneId, spaceId, userEquipId, new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.viewAddEquipForSceneSuccess(param);
            }

            @Override
            protected void onFailure() {
                callBack.viewAddEquipForSceneFailure();
            }

            @Override
            protected void onError() {
                callBack.viewAddEquipForSceneFailure();
            }
        });
    }

    public void addSceneEquip(String data) {
        ApiLoader.getApi().addSceneEquip(data, new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
                callBack.viewAddSceneEquipSuccess();
            }

            @Override
            protected void onFailure() {
                callBack.viewAddSceneEquipFailure();
            }

            @Override
            protected void onError() {
                callBack.viewAddSceneEquipFailure();
            }
        });

    }

    public void goUpdateScene() {
        callBack.viewGoUpdateScene();
    }

    public void goSelectSceneIcon() {
        callBack.viewSelectSceneIcon();
    }

    public void goSelectSceneTiming() {
        callBack.viewSelectSceneTiming();
    }

    public void updateScene(Scene scene) {
        Map<String, Object> param = new HashMap<>();
        param.put("sceneId", scene.getSceneId());
        param.put("sceneName", scene.getSceneName());
        param.put("sceneImg", scene.getSceneImg());
        param.put("userId", VcomSingleton.getInstance().getLoginUser().getUserId());

        ApiLoader.getApi().updateScene(GsonUtil.getInstance().toJson(param), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.viewUpdateSceneSuccess();
                EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

            }
        });
    }

    public void deleteScene(Scene scene1) {

        ApiLoader.getApi().deleteScene(scene1.getSceneId(), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
                callBack.viewDeleteSceneSuccess(scene1);
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

            }
        });
    }


    public void deleteSceneEquip(Scene scene, String userId, String spaceId, String sceneId, String equipId) {
        ApiLoader.getApi().deleteSceneEquip(userId, spaceId, sceneId, equipId, new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
                callBack.viewDeleteSceneSuccess(scene);
            }

            @Override
            protected void onFailure() {
                callBack.viewDeleteSceneFailure();
            }

            @Override
            protected void onError() {
                callBack.viewDeleteSceneFailure();
            }
        });
    }

    public interface AddSceneVMCallBack {
        void viewBack();
        void viewGoAddNewEquip();
        void viewAddEquipForSceneSuccess(String data);
        void viewAddEquipForSceneFailure();
        void viewAddSceneEquipSuccess();
        void viewAddSceneEquipFailure();
        void viewGoUpdateScene();
        void viewUpdateSceneSuccess();
        void viewDeleteSceneSuccess(Scene scene);
        void viewDeleteSceneFailure();
        void viewSelectSceneIcon();
        void viewSelectSceneTiming();

    }
}
