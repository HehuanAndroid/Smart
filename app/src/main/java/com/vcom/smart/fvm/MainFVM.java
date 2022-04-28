package com.vcom.smart.fvm;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.model.Region;
import com.vcom.smart.model.Scene;
import com.vcom.smart.model.User;
import com.vcom.smart.request.ApiLoader;
import com.vcom.smart.request.ApiObserver;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.utils.GsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * @Author Lzz
 * @Date 2020/10/27 18:45
 */
public class MainFVM extends AndroidViewModel {

    private MainFvmCallBack callBack;

    public MainFVM(@NonNull Application application) {
        super(application);
    }

    public void setCallBack(MainFvmCallBack callBack) {
        this.callBack = callBack;
    }

    public void goEditRegion() {
        callBack.goEditRegion();
    }

    public void goGuideRegion() {
        callBack.goGuideRegion();
    }

    public void goGuideScene() {
        callBack.goGuideScene();
    }

    public void goAddScene() {
        callBack.goAddScene();
    }

    public void goAddNewScene() {
        callBack.goAddNewScene();
    }

    public void goSceneManger() {
        callBack.goSceneManger();
    }

    public void addSingleScene(String spaceId, Scene scene) {

        ApiLoader.getApi().addScene(spaceId, scene.getSceneName(), scene.getSceneImg(), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.refreshData();
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

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
                callBack.addRegionSuccess();
                //callBack.refreshData();
            }

            @Override
            protected void onFailure() {
                callBack.addRegionFailure();
            }

            @Override
            protected void onError() {
                callBack.addRegionFailure();
            }
        });
    }

    public void addSceneList(String spaceId, List<Scene> scenes) {
        User user = VcomSingleton.getInstance().getLoginUser();
        String json = GsonUtil.getInstance().toJson(scenes);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("spaceId", spaceId);
        map.put("sceneImg", scenes.get(0).getSceneImg());
        map.put("sceneName", json);

        ApiLoader.getApi().addSceneList(map, new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.addSceneSuccess(scenes);
                //callBack.refreshData();
            }

            @Override
            protected void onFailure() {
                callBack.addSceneFailure();
            }

            @Override
            protected void onError() {
                callBack.addSceneFailure();
            }
        });

    }

    public void deleteScene(Scene scene){
        ApiLoader.getApi().deleteScene(scene.getSceneId(), new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
                callBack.goDeleteSceneSuccess();
            }

            @Override
            protected void onFailure() {

            }

            @Override
            protected void onError() {

            }
        });
    }


    public interface MainFvmCallBack {

        void goEditRegion();

        void goGuideRegion();

        void goGuideScene();

        void goAddScene();

        void goAddNewScene();

        void addRegionSuccess();
        void addRegionFailure();
        void addSceneSuccess(List<Scene> scenes);
        void addSceneFailure();

        void goSceneManger();

        void refreshData();

        void goDeleteSceneSuccess();

    }

}
