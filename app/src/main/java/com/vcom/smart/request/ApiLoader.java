package com.vcom.smart.request;

import com.google.gson.Gson;
import com.vcom.smart.model.Equip;
import com.vcom.smart.model.Scene;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ApiLoader extends ObjectLoader {

    private static ApiService apiService = null;

    private static final ApiLoader api = new ApiLoader();

    public ApiLoader() {
        apiService = RetrofitServiceManager.getInstance().create(ApiService.class);
    }

    public static ApiLoader getApi() {
        return api;
    }

    public void userLogin(Map<String, Object> params, Observer<ResponseBody> observer) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create(new Gson().toJson(params), mediaType);
        setSubscribe(apiService.userLogin(body), observer);
    }

    public void validateLogin(Map<String, Object> params, Observer<ResponseBody> observer) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create(new Gson().toJson(params), mediaType);
        setSubscribe(apiService.validateLogin(body), observer);
    }

    public void queryRegion(String userId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.queryRegion(userId), observer);
    }

    public void regionalManagement(String userId, String spaceName, Observer<ResponseBody> observer) {
        setSubscribe(apiService.regionalManagement(userId, spaceName), observer);
    }

    public void deleteArea(String userId, String spaceId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.deleteArea(userId, spaceId), observer);
    }

    public void modifyArea(String userId, String spaceId, String spaceName, String spaceImg, Observer<ResponseBody> observer) {
        setSubscribe(apiService.modifyArea(userId, spaceId, spaceName, spaceImg), observer);
    }

    public void addEquipForScene(String sceneId, String equips, Observer<ResponseBody> observer) {
        setSubscribe(apiService.addEquipForScene(sceneId, equips), observer);
    }

    public void addScene(String spaceId, String sceneName, String sceneImg, Observer<ResponseBody> observer) {
        setSubscribe(apiService.addScene(spaceId, sceneName, sceneImg), observer);
    }

    public void deleteScene(String sceneId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.deleteScene(sceneId), observer);
    }

    public void selectListScene(String spaceId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.selectListScene(spaceId), observer);
    }

    public void addRegionList(Map<String, Object> params, Observer<ResponseBody> observer) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create(new Gson().toJson(params), mediaType);
        setSubscribe(apiService.addRegionList(body), observer);
    }

    public void addSceneList(Map<String, Object> params, Observer<ResponseBody> observer) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create(new Gson().toJson(params), mediaType);
        setSubscribe(apiService.addSceneList(body), observer);
    }

    public void selectAllData(String userID, Observer<ResponseBody> observer) {
        setSubscribe(apiService.selectAllData(userID), observer);
    }

    public void getProductInfo(Observer<ResponseBody> observer) {
        setSubscribe(apiService.getProductInfo(), observer);
    }

    public void getDefaultMac(String userId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.getDefaultMac(userId), observer);
    }

    public void getAllDevType(Observer<ResponseBody> observer) {
        setSubscribe(apiService.getAllDevType(), observer);
    }

    public void getAllEquips(String userId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.getAllEquips(userId), observer);
    }

    public void addSingleEquip(Equip equip, Observer<ResponseBody> observer) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create(new Gson().toJson(equip), mediaType);
        setSubscribe(apiService.addSingleEquip(body), observer);
    }

    public void deleteEquip(String userId, String equipId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.deleteEquip(userId, equipId), observer);
    }

    public void addEquipForScene(String sceneId, String spaceId, String userEquipId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.addEquipForScene(sceneId, spaceId, userEquipId), observer);
    }
    public void addSceneEquip(String data, Observer<ResponseBody> observer) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create(data, mediaType);
        setSubscribe(apiService.addSceneEquip(body), observer);
    }

    public void deleteSceneEquip(String userId, String spaceId, String sceneId, String equipId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.deleteSceneEquip(userId, spaceId, sceneId, equipId), observer);
    }

    public void updateScene(String data, Observer<ResponseBody> observer) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create(data, mediaType);
        setSubscribe(apiService.updateScene(body), observer);
    }

    public void getSceneSwitch(String userId, String equipId, Observer<ResponseBody> observer) {
        setSubscribe(apiService.getSceneSwitch(userId, equipId), observer);
    }

    public void updateSceneSwitch(String data, Observer<ResponseBody> observer) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.Companion.create(data, mediaType);
        setSubscribe(apiService.updateSceneSwitch(body), observer);
    }

    private interface ApiService {
        @POST("user/userLogin")
        Observable<ResponseBody> userLogin(@Body RequestBody data);

        @POST("user/checkUser")
        Observable<ResponseBody> validateLogin(@Body RequestBody data);

        @GET("homePage/queryArea")
        Observable<ResponseBody> queryRegion(@Query("userId") String userId);

        @GET("homePage/regionalManagement")
        Observable<ResponseBody> regionalManagement(@Query("userId") String userId, @Query("spaceName") String spaceName);

        @GET("homePage/deleteArea")
        Observable<ResponseBody> deleteArea(@Query("userId") String userId, @Query("spaceId") String spaceId);

        @GET("homePage/modifyArea")
        Observable<ResponseBody> modifyArea(@Query("userId") String userId, @Query("spaceId") String spaceId, @Query("spaceName") String spaceName, @Query("spaceImg") String spaceImg);

        @GET("homePage/addEquipForScene")
        Observable<ResponseBody> addEquipForScene(@Query("sceneId") String sceneId, @Query("equips") String equips);

        @GET("homePage/addScene")
        Observable<ResponseBody> addScene(@Query("spaceId") String spaceId, @Query("sceneName") String sceneName, @Query("sceneImg") String sceneImg);

        @GET("homePage/deleteSceneEquip")
        Observable<ResponseBody> deleteScene(@Query("sceneId") String sceneId);

        @GET("homePage/selectListScene")
        Observable<ResponseBody> selectListScene(@Query("spaceId") String spaceId);

        @POST("space/batchSpace")
        Observable<ResponseBody> addRegionList(@Body RequestBody data);

        @POST("scene/batchScenes")
        Observable<ResponseBody> addSceneList(@Body RequestBody data);

        @GET("homePage/selectAllData")
        Observable<ResponseBody> selectAllData(@Query("userId") String userId);

        @GET("equi/selectAllInfo")
        Observable<ResponseBody> getProductInfo();

        @GET("position/selectPositions")
        Observable<ResponseBody> getDefaultMac(@Query("userId") String userId);

        @GET("dictionary/selectDictionary")
        Observable<ResponseBody> getAllDevType();

        @GET("homePage/selectListEquip")
        Observable<ResponseBody> getAllEquips(@Query("userId") String userId);

        @POST("homePage/addSingleEquip")
        Observable<ResponseBody> addSingleEquip(@Body RequestBody data);

        @GET("homePage/deleteUserEquip")
        Observable<ResponseBody> deleteEquip(@Query("userId") String userId, @Query("userEquipId") String equipId);

        @GET("homePage/addEquipForScene")
        Observable<ResponseBody> addEquipForScene(@Query("sceneId") String sceneId, @Query("spaceId") String spaceId, @Query("equips") String userEquipId);

        @POST("record/addRecord")
        Observable<ResponseBody> addSceneEquip(@Body RequestBody data);

        @GET("homePage/deleteSceneEquipForOne")
        Observable<ResponseBody> deleteSceneEquip(@Query("userId") String userId, @Query("spaceId") String spaceId, @Query("sceneId") String sceneId, @Query("userEquipId") String userEquipId);

        @POST("scene/updateScene")
        Observable<ResponseBody> updateScene(@Body RequestBody data);

        @GET("homePage/getSceneSwitch")
        Observable<ResponseBody> getSceneSwitch(@Query("userId") String userId, @Query("userEquipId") String userEquipId);

        @POST("homePage/updateSceneSwitch")
        Observable<ResponseBody> updateSceneSwitch(@Body RequestBody data);

    }

}
