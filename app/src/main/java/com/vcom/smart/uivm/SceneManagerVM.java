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
 * @Author Lzz
 * @Date 2020/11/4 9:37
 */
public class SceneManagerVM extends AndroidViewModel {

    private SceneManagerVmCallBack callBack;

    public SceneManagerVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(SceneManagerVmCallBack callBack) {
        this.callBack = callBack;
    }

    public void viewBack() {
        callBack.viewBack();
    }

    public void addEquipToScene() {
        callBack.viewGoAddEquip();
    }

    public void goAddEquip() {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.switchFragment, "1"));
    }

    public void goSelectSceneIcon() {
        callBack.viewSelectSceneIcon();
    }

    public void goSelectSceneTiming() {
        callBack.viewSelectSceneTiming();
    }

    public void goDeleteScene() {
        callBack.viewGoDeleteEquip();
    }

    public void goUpdateScene() {
        callBack.viewGoUpdateScene();
    }

    public void updateScene(Scene scene) {

        Map<String, Object> param = new HashMap<>();
        param.put("spaceId", scene.getSpaceId());
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
                callBack.updateSceneSuccess();
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

    public void deleteScene(String sceneId) {

        ApiLoader.getApi().deleteScene(sceneId, new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
                //callBack.deleteSceneEquipSuccess();
                callBack.deleteSceneSuccess();
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

            }
        });
    }

    public void deleteSceneEquip(String userId, String spaceId, String sceneId, String equipId ) {
        ApiLoader.getApi().deleteSceneEquip(userId, spaceId, sceneId, equipId, new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
                //callBack.deleteSceneSuccess();
                callBack.deleteSceneEquipSuccess();
            }

            @Override
            protected void onFailure() {
                callBack.deleteSceneEquipFailure();
            }

            @Override
            protected void onError() {
                callBack.deleteSceneEquipFailure();
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
                callBack.addSceneEquipSuccess();
            }

            @Override
            protected void onFailure() {
                callBack.addSceneEquipFailure();
            }

            @Override
            protected void onError() {
                callBack.addSceneEquipFailure();
            }
        });

    }

    public interface SceneManagerVmCallBack {
        void viewBack();
        void viewGoAddEquip();
        void viewSelectSceneIcon();
        void viewSelectSceneTiming();
        void viewGoDeleteEquip();
        void viewGoUpdateScene();
        void updateSceneSuccess();
        void addSceneEquipSuccess();
        void addSceneEquipFailure();
        void deleteSceneSuccess();
        void deleteSceneEquipSuccess();
        void deleteSceneEquipFailure();

    }

}
