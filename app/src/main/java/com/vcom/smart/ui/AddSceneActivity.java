package com.vcom.smart.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylanc.activityresult.launcher.StartActivityLauncher;
import com.telink.bluetooth.light.ConnectionStatus;
import com.vcom.smart.R;
import com.vcom.smart.adapter.SelectIconAdapter;
import com.vcom.smart.base.BaseBindingAdapter;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivityAddSceneBinding;
import com.vcom.smart.databinding.DialogSceneAddEquipBinding;
import com.vcom.smart.databinding.DialogSelectIconBinding;
import com.vcom.smart.databinding.DialogSelectScenesBinding;
import com.vcom.smart.databinding.DialogSelectTimingBinding;
import com.vcom.smart.databinding.ItemScanDeviceBinding;
import com.vcom.smart.databinding.ItemSceneIconBinding;
import com.vcom.smart.databinding.ItemSceneManagerEquipBinding;
import com.vcom.smart.model.Equip;
import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.model.Product;
import com.vcom.smart.model.Region;
import com.vcom.smart.model.Scene;
import com.vcom.smart.server.VcomService;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.uivm.AddSceneVM;
import com.vcom.smart.utils.GsonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Banlap on 2021/1/28
 */
public class AddSceneActivity extends BaseMvvmActivity<AddSceneVM, ActivityAddSceneBinding>
        implements AddSceneVM.AddSceneVMCallBack {

    private String mRegionId;               //新增场景的区域id
    private String mDefaultSceneName;       //新增场景默认名称
    private Scene mScene = null;            //新增场景信息
    private List<Scene> mCurrentSceneList;  //当前区域的场景列表（不包括当前新增场景）

    private int tagMeshAddress = -1;
    private byte[] tagParam = null;

    private AlertDialog mAlertDialog;
    private AddSceneAdapter mAdapter;
    private final List<Equip> mSceneEquipsList = new ArrayList<>();  //新增场景下添加的设备列表
    private final List<Equip> mEquipList = VcomSingleton.getInstance().getUserEquips();
    private final List<Region> mRegionList = VcomSingleton.getInstance().getUserRegion();

    public static int mSelectIndex = 0;    //选择场景图标位置, 默认为 0

    private boolean isAcceptAdd = false;  //修复在多个新增场景中点击增加设备，会出现sceneId NULL报错
    private final StartActivityLauncher startActivityLauncher = new StartActivityLauncher(this);
    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_scene;
    }

    private AtomicInteger hour;
    private AtomicInteger min;

    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);
        //banlap: 注册EventBus
        EventBus.getDefault().register(this);

        mAdapter = new AddSceneAdapter(this);
        mAdapter.setItems(mSceneEquipsList);
        getViewDataBind().rvAddSceneDevice.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBind().rvAddSceneDevice.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        //selectIconAdapter = new SelectIconAdapter(this, mapList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initDatum() {
        //banlap: 初始化变量 场景图标选择
        mSelectIndex = 0;
        if (getIntent().getExtras() == null) {
            finish();
        }

        mDefaultSceneName = getIntent().getStringExtra("NewSceneDefaultName");
        getViewDataBind().etAddSceneName.setText(mDefaultSceneName);

        //banlap: 获取当前区域id
        mRegionId = getIntent().getStringExtra("CurrentRegionId");
        if(mRegionList.size()>0){
           for (Region region : mRegionList){
               if(region.getSpaceId().equals(mRegionId)) {
                   //banlap: 获取当前新增场景的list
                   mCurrentSceneList = region.getSceneList();
               }
           }

        }
        //banlap: 添加设备时设置加载gif
        getViewDataBind().prLoading2.setVisibility(View.VISIBLE);
        getViewDataBind().tvAddScene.setVisibility(View.GONE);
        //Toast.makeText(this, "cRegionId: " + mRegionId, Toast.LENGTH_SHORT).show();
    }

    /*
    * banlap: EventBus 事件订阅响应方法
    * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.msgCode == MessageEvent.regionReady) {
            //banlap: 刷新当前新增场景的list
            if(mScene!=null){
                for (Region region : VcomSingleton.getInstance().getUserRegion()) {
                    for (Scene scene : region.getSceneList()) {
                        if (scene.getSceneId().equals(mScene.getSceneId())) {
                            mSceneEquipsList.clear();
                            mSceneEquipsList.addAll(scene.getUserEquipList());
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            //banlap: 当收到regionReady通知后可在场景中添加设备
            getViewDataBind().prLoading2.setVisibility(View.GONE);
            getViewDataBind().tvAddScene.setVisibility(View.VISIBLE);
            isAcceptAdd = true;
        }
    }


    /*
    * banlap: 点击添加设备
    * */
    @Override
    public void viewGoAddNewEquip() {

        if(isAcceptAdd) {
            mAlertDialog = null;
            if (mScene == null) {
                findNewScene();
            }

            //banlap: 判断当前账号是否有绑定的设备
            if (mEquipList.size() > 0) {

                List<Equip> newEquipList = new ArrayList<>();

                for (Equip equip : mEquipList) {
                    //banlap: 场景中添加设备列表时 除去21508 智能场景开关
                    if (!equip.getProductUuid().equals("21508")) {
                        equip.setCheck(false);
                        newEquipList.add(equip);
                    }
                }

                //banlap: 弹框 显示当前账号绑定的列表
                DialogSceneAddEquipBinding addEquipBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_scene_add_equip,
                        null, false);
                mAlertDialog = new AlertDialog.Builder(this)
                        .setView(addEquipBinding.getRoot())
                        .create();

                AddSceneActivity.ScanAddEquipAdapter deviceAdapter = new AddSceneActivity.ScanAddEquipAdapter(this);
                addEquipBinding.dialogSceneAddEquipRecycler.setLayoutManager(new LinearLayoutManager(this));
                addEquipBinding.dialogSceneAddEquipRecycler.setAdapter(deviceAdapter);
                deviceAdapter.setItems(newEquipList);
                deviceAdapter.notifyDataSetChanged();

                addEquipBinding.dialogSceneAddEquipCancel.setOnClickListener(v -> mAlertDialog.dismiss());
                //banlap: 选择列表中的设备
                addEquipBinding.dialogSceneAddEquipNext.setOnClickListener(v -> {
                    for (Equip equip : mEquipList) {
                        if (equip.isCheck()) {
                            getParamsForActivity(equip);
                            mAlertDialog.dismiss();
                            break;
                        }
                    }
                });

            } else {
                mAlertDialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.dialog_title))
                        .setMessage(getString(R.string.dialog_message_add_device_into_empty_list))
                        .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(getString(R.string.dialog_confirm), ((dialog, which) -> {
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.switchFragment, "1"));
                            dialog.dismiss();
                            finish();
                        }))
                        .create();
            }
            mAlertDialog.show();
        }
    }


    /*
    * banlap: 不保存返回列表
    * */
    @Override
    public void viewBack() {
        /*
        * banlap: 返回时 删除当前创建的空场景
        * 需要找到当前创建空场景的sceneId
        * 需要查询当前区域才能查询到这个区域下的所有场景列表
        * */
       /* mScene = null;
        findNewScene();
        //banlap: 执行删除场景
        if(mScene!=null){
            getViewModel().deleteScene(mScene);
        }*/
        finish();
    }



    /*
     * banlap: 选择场景图标
     * */
    @Override
    public void viewSelectSceneIcon() {
        DialogSelectIconBinding selectIconBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_select_icon, null, false);
        mAlertDialog = null;
        mAlertDialog = new AlertDialog.Builder(this)
                .setView(selectIconBinding.getRoot())
                .create();

        Integer[] icons = {R.drawable.ic_scene_0, R.drawable.ic_scene_1, R.drawable.ic_scene_2, R.drawable.ic_scene_3};
        Integer[] iconSelected = {R.drawable.ic_scene_selected_0, R.drawable.ic_scene_selected_1, R.drawable.ic_scene_selected_2, R.drawable.ic_scene_selected_3};

        SceneIconAdapter sceneIconAdapter = new SceneIconAdapter(this, icons, iconSelected);
        selectIconBinding.rvSelectIconList.setLayoutManager(new GridLayoutManager(this, 2));
        selectIconBinding.rvSelectIconList.setAdapter(sceneIconAdapter);
        selectIconBinding.btSelectIconCancel.setOnClickListener(v->{mAlertDialog.dismiss();});
        sceneIconAdapter.notifyDataSetChanged();

        //banlap: 点击确认选择该场景图标
        selectIconBinding.btSelectIconCommit.setOnClickListener(v->{
            mAlertDialog.dismiss();
            getViewDataBind().ivSelectSceneIcon.setBackgroundResource(iconSelected[mSelectIndex]);
        });

        mAlertDialog.show();
    }

    /*
     * banlap: 选择定时
     * */
    @Override
    public void viewSelectSceneTiming() {
        DialogSelectTimingBinding dialogSelectTimingBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_select_timing, null, false);
        mAlertDialog = new AlertDialog.Builder(this)
                .setView(dialogSelectTimingBinding.getRoot())
                .create();

        hour = new AtomicInteger();
        min = new AtomicInteger();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour.set(dialogSelectTimingBinding.tpSelectTime.getHour());
            min.set(dialogSelectTimingBinding.tpSelectTime.getMinute());
        } else {
            hour.set(dialogSelectTimingBinding.tpSelectTime.getCurrentHour());
            min.set(dialogSelectTimingBinding.tpSelectTime.getCurrentMinute());
        }

        //banlap: 选择时间监听
        dialogSelectTimingBinding.tpSelectTime.setIs24HourView(true);
        dialogSelectTimingBinding.tpSelectTime.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            hour.set(hourOfDay);
            min.set(minute);
        });

        dialogSelectTimingBinding.btSelectIconCancel.setOnClickListener(v-> {
            mAlertDialog.dismiss();
        });

        dialogSelectTimingBinding.btSelectIconCommit.setOnClickListener(v-> {
            String setMin = "" + Integer.parseInt(String.valueOf(min));
            if(Integer.parseInt(String.valueOf(min))<10) {
                setMin = "0" + setMin;
            }
            String showTiming = Integer.parseInt(String.valueOf(hour)) + ":" + setMin;
            getViewDataBind().tvShowTiming.setText(showTiming);
            mAlertDialog.dismiss();
        });

        mAlertDialog.show();
    }

    /*
     * banlap: 更新并保存场景
     * */
    @Override
    public void viewGoUpdateScene() {
        if(mScene == null){
            findNewScene();
        }

        String newSceneName = getViewDataBind().etAddSceneName.getText().toString();
        if (TextUtils.isEmpty(newSceneName)) {
            return;
        }

        if(mSceneEquipsList.size() == 0) {
            Toast.makeText(this, getString(R.string.toast_add_device),Toast.LENGTH_SHORT).show();
            return;
        }

        mScene.setSceneName(newSceneName);
        //banlap: 更新场景图标
        mScene.setSceneImg(String.valueOf(mSelectIndex));
        getViewModel().updateScene(mScene);
    }

    @Override
    public void viewUpdateSceneSuccess() {
        Toast.makeText(getApplication(),getString(R.string.toast_update_success),Toast.LENGTH_SHORT).show();

        if(hour!=null && min !=null) {
            setTime(Integer.parseInt(String.valueOf(hour)), Integer.parseInt(String.valueOf(min)));
        }

        finish();
    }

    /*
     * banlap: 设备添加当前时间
     * */
    public void setTime(int hour, int min) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy,M,dd,HH,mm,ss");
        String currentTime = simpleDateFormat.format(new Date());
        String[] str =currentTime.split(",");
        int year=0, month=0, day=0, hour1=0, min1=0, sec=0;
        for(int i=0; i<str.length; i++) {
            switch (i) {
                case 0:
                    year = Integer.parseInt(str[i]);
                    break;
                case 1:
                    month = Integer.parseInt(str[i]);
                    break;
                case 2:
                    day = Integer.parseInt(str[i]);
                    break;
                case 3:
                    hour1 = Integer.parseInt(str[i]);
                    break;
                case 4:
                    min1 = Integer.parseInt(str[i]);
                    break;
                case 5:
                    sec = Integer.parseInt(str[i]);
                    break;
            }
        }
        int yearMsb = (year&0xff00)>>8;
        int yearLsb = year&0xff;

       /* Toast.makeText(this, "当前时间: "
                + year + " | " + month + " | " + day + " | "
                + hour + " | " + min + " | " + sec + " | "
                + yearLsb + " | " + yearMsb, Toast.LENGTH_LONG).show();
*/
        byte opcode = (byte) 0xE4;     //设置灯的时间 操作码
        byte[] param = new byte[] {(byte) yearLsb, (byte) yearMsb,  (byte) month, (byte) day,  (byte) hour1, (byte) min1, (byte) sec};

        for(int i=0; i < mSceneEquipsList.size(); i++) {
            if (Integer.parseInt(mSceneEquipsList.get(i).getMeshAddress()) != -1) {
                VcomService.getInstance().sendCommandNoResponse(opcode, Integer.parseInt(mSceneEquipsList.get(i).getMeshAddress()), param);
            }
        }

        setTiming(hour, min);
    }
    /*
     * banlap: 设备添加定时
     * */
    public void setTiming(int hour, int min) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M,dd");
        String currentTime = simpleDateFormat.format(new Date());
        String[] str =currentTime.split(",");
        int month=0, day=0;
        for(int i=0; i<str.length; i++) {
            switch (i) {
                case 0:
                    month = Integer.parseInt(str[i]);
                    break;
                case 1:
                    day = Integer.parseInt(str[i]);
                    break;
            }
        }

        //Toast.makeText(this, "date: " + month + "." + day+ "time: " + hour + ":" + min, Toast.LENGTH_SHORT).show();

        byte opcode = (byte) 0xE5;     //闹钟操作码
        byte alarmType = (byte) 0x00;  //添加闹钟
        int index = 1; //闹钟索引
        byte[] param = new byte[] {alarmType, (byte) index, (byte) 0x82,
                (byte) month, (byte) day, (byte) hour, (byte) min, (byte) 0x00, (byte) Integer.parseInt(mScene.getSceneMeshId())};

        for(int i=0; i < mSceneEquipsList.size(); i++) {
            if(Integer.parseInt(mSceneEquipsList.get(i).getMeshAddress()) != -1) {
                VcomService.getInstance().sendCommandNoResponse(opcode, Integer.parseInt(mSceneEquipsList.get(i).getMeshAddress()), param);
            }
        }

    }



    /*
    * banlap: 查询场景id
    * */
    public void findNewScene(){
        List<Region> newRegionList = VcomSingleton.getInstance().getUserRegion();
        List<Scene> newSceneList = new ArrayList<>();
        if(newRegionList.size() >0){
            for (Region region : newRegionList) {
                if (region.getSpaceId().equals(mRegionId)) {
                    newSceneList = region.getSceneList();
                }
            }
            //banlap: 场景列表数据
            if(newSceneList.size()>0) {
                if(mCurrentSceneList.size()>0){
                    for(Scene newScene : newSceneList) {
                        //banlap: 标记 是否查询到相同场景id
                        boolean isFind = false;
                        for(Scene scene : mCurrentSceneList) {
                            if(newScene.getSceneId().equals(scene.getSceneId())){
                                isFind = true;
                                break;
                            }
                        }
                        //banlap: 没查询到则为新增的场景
                        if(!isFind){
                            mScene = newScene;
                        }
                    }
                } else {
                    mScene = newSceneList.get(0);
                }
            }

        }
    }


    /*
    * banlap: 添加设备到场景里面
    * */
    private void getParamsForActivity(Equip equip) {
        tagParam = null;
        tagMeshAddress = -1;
        Intent intent = new Intent(this, DeviceSettingActivity.class);
        intent.putExtra("sceneId", mScene.getSceneId());
        intent.putExtra("equipId", equip.getUserEquipId());
        startActivityLauncher.launch(intent,result -> {
            if (result.getResultCode() == 0x1A) {
                if (result.getData() != null) {
                    String jsonData = result.getData().getStringExtra("save_param");

                    byte cmd = result.getData().getByteExtra("cmd", (byte) 0);
                    tagParam = result.getData().getByteArrayExtra("equip_param");
                    tagMeshAddress = Integer.parseInt(equip.getMeshAddress());

                    Map<String, Object> data = new HashMap<>();
                    if (!TextUtils.isEmpty(jsonData)) {
                        data = GsonUtil.getInstance().json2Map(jsonData);
                    }

                    if (cmd == 0x02) {
                        data.put("equipType", 1);//三位开关
                    } else {
                        data.put("equipType", 0);
                    }

                    data.put("userEquipId", equip.getUserEquipId());
                    data.put("userId", VcomSingleton.getInstance().getLoginUser().getUserId());
                    data.put("sceneId", mScene.getSceneId());
                    data.put("spaceId", mScene.getSpaceId());

                    goAddSceneEquipData(mScene.getSceneId(), equip.getUserEquipId(), GsonUtil.getInstance().toJson(data));

                }
            }
        });
    }

    public void goAddSceneEquipData(String sceneId, String userEquipId, String data) {
        //getViewModel().addEquipForScene(sceneId, mRegionId, userEquipId, data);
        getViewModel().addSceneEquip(data);
    }


    @Override
    public void viewAddEquipForSceneSuccess(String data) {
        getViewModel().addSceneEquip(data);
    }
    @Override
    public void viewAddEquipForSceneFailure() { Toast.makeText(this, getString(R.string.toast_add_error), Toast.LENGTH_SHORT).show(); }

    /*
     * banlap: 新增设备到该场景成功 (保存到记录表)
     * */
    @Override
    public void viewAddSceneEquipSuccess() {
        Toast.makeText(getApplication(),getString(R.string.toast_add_success),Toast.LENGTH_SHORT).show();

        if (tagMeshAddress == -1) {
            return;
        }
        if (tagParam == null) {
            return;
        }
        byte opcode = (byte) 0xEE;
        VcomService.getInstance().sendCommandNoResponse(opcode, tagMeshAddress,
                tagParam);
    }
    /*
     * banlap: 新增设备到该场景失败
     * */
    @Override
    public void viewAddSceneEquipFailure() {
        Toast.makeText(getApplication(),getString(R.string.toast_add_error),Toast.LENGTH_SHORT).show();
    }

    /*
    * banlap: 删除场景里面的设备
    * */
    private void deleteSceneEquip(Equip equip) {
        tagMeshAddress = -1;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message_delete_scene_device))
                .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> {
                    tagMeshAddress = Integer.parseInt(equip.getMeshAddress());
                    getViewModel().deleteSceneEquip(mScene, VcomSingleton.getInstance().getLoginUser().getUserId(), mRegionId, mScene.getSceneId(), equip.getUserEquipId());
                    dialog.dismiss();
                })
                .create()
                .show();
    }
    /*
     * banlap: 删除场景成功后执行
     * */
    @Override
    public void viewDeleteSceneSuccess(Scene scene) {
        Toast.makeText(getApplication(),getString(R.string.toast_delete_success),Toast.LENGTH_SHORT).show();

        if (tagMeshAddress == -1) {
            return;
        }

        byte opcode = (byte) 0xEE;
        byte[] param = {0x00, (byte) Integer.parseInt(scene.getSceneMeshId())};
        VcomService.getInstance().sendCommandNoResponse(opcode, tagMeshAddress, param);
    }
    /*
     * banlap: 删除场景失败后执行
     * */
    @Override
    public void viewDeleteSceneFailure() {
        Toast.makeText(getApplication(),getString(R.string.toast_delete_error),Toast.LENGTH_SHORT).show();
    }
    /*
    * banlap: 新增场景的设备列表
    * */
    private class AddSceneAdapter extends BaseBindingAdapter<Equip, ItemSceneManagerEquipBinding> {

        public AddSceneAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected int getLayoutId(int layoutId) {
            return R.layout.item_scene_manager_equip;
        }

        @Override
        protected void onBindItem(ItemSceneManagerEquipBinding itemSceneManagerEquipBinding, Equip item, int i) {
            Product mProduct = null;
            for (Product product : VcomSingleton.getInstance().getUserProduct()) {
                for (Product.EquipInfo equipInfo : product.getEquipInfoList()) {
                    if (item.getProductUuid().equals(equipInfo.getEquipInfoPid())) {
                        mProduct = product;
                        break;
                    }
                }
            }

            //banlap: 获取当前系统语言;
            String localeLanguage = Locale.getDefault().getLanguage();

            int resId = 0;
            if (mProduct != null) {
                //banlap: 判断当前手机系统是否为en英语
                if(localeLanguage.equals("en")){
                    itemSceneManagerEquipBinding.itemSceneManagerName.setText(mProduct.getEquiNickName());
                } else {
                    itemSceneManagerEquipBinding.itemSceneManagerName.setText(mProduct.getEquiName());
                }
                resId = mContext.getResources().getIdentifier("ic_" + item.getProductUuid() + "_on", "drawable", mContext.getPackageName());
            }
            if (resId > 0) {
                itemSceneManagerEquipBinding.itemSceneManagerIcon.setBackgroundResource(resId);
            } else {
                itemSceneManagerEquipBinding.itemSceneManagerIcon.setBackgroundResource(R.drawable.ic_lamp_default_on);
            }

            itemSceneManagerEquipBinding.rlSceneSetting.setOnClickListener(v -> getParamsForActivity(item));
            //itemSceneManagerEquipBinding.itemDeviceRemove.setOnClickListener(v -> deleteSceneEquip(item));
            itemSceneManagerEquipBinding.flDelete.setOnClickListener(v -> deleteSceneEquip(item));
        }
    }

    //banlap: 扫描当前用户绑定的设备
    private static class ScanAddEquipAdapter extends BaseBindingAdapter<Equip, ItemScanDeviceBinding> {

        public ScanAddEquipAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected int getLayoutId(int layoutId) {
            return R.layout.item_scan_device;
        }

        @Override
        protected void onBindItem(ItemScanDeviceBinding itemScanDeviceBinding, Equip item, int i) {

            Product mProduct = null;
            for(Product product : VcomSingleton.getInstance().getUserProduct()) {
                for(Product.EquipInfo equipInfo : product.getEquipInfoList()) {
                    if (item.getProductUuid().equals(equipInfo.getEquipInfoPid())) {
                        mProduct = product;
                        break;
                    }
                }
            }
            //banlap: 获取当前系统语言;
            String localeLanguage = Locale.getDefault().getLanguage();

            int resId = 0;
            if (mProduct != null) {
                //banlap: 判断当前手机系统是否为en英语
                if(localeLanguage.equals("en")){
                    itemScanDeviceBinding.itemScanDeviceName.setText(mProduct.getEquiNickName());
                } else {
                    itemScanDeviceBinding.itemScanDeviceName.setText(mProduct.getEquiName());
                }

                String drawableName = "ic_" + item.getProductUuid() + (item.getConnectionStatus()==null?  "_off" : "_on");
                if(item.getConnectionStatus()!=null) {
                    drawableName = "ic_" + item.getProductUuid() + (item.getConnectionStatus().equals(ConnectionStatus.OFFLINE)?  "_off" : "_on");
                }
                resId = mContext.getResources().getIdentifier(drawableName, "drawable", mContext.getPackageName());
            }
            if (resId > 0) {
                itemScanDeviceBinding.itemScanDeviceIcon.setBackgroundResource(resId);
            } else {
                itemScanDeviceBinding.itemScanDeviceIcon.setBackgroundResource(R.drawable.ic_lamp_default_on);
            }

            if (item.isCheck()) {
                itemScanDeviceBinding.itemScanDeviceSelection.setVisibility(View.VISIBLE);
                itemScanDeviceBinding.itemScanDeviceSelection.setBackgroundResource(R.drawable.ic_select_yes);
            } else {
                itemScanDeviceBinding.itemScanDeviceSelection.setVisibility(View.VISIBLE);
                itemScanDeviceBinding.itemScanDeviceSelection.setBackgroundResource(R.drawable.ic_select_no_gray);

                itemScanDeviceBinding.itemScanDeviceSelection.setVisibility(item.getConnectionStatus() !=null? View.VISIBLE:View.GONE);
                if(item.getConnectionStatus()!=null) {
                    itemScanDeviceBinding.itemScanDeviceSelection.setVisibility(item.getConnectionStatus().equals(ConnectionStatus.OFFLINE)? View.GONE : View.VISIBLE);
                }
            }

            itemScanDeviceBinding.getRoot().setOnClickListener(v -> {
                //banlap：判断当前设备不在线时 不能在场景中设置调控参数
                if(item.getConnectionStatus() != null) {
                    if (!item.getConnectionStatus().equals(ConnectionStatus.OFFLINE)) {
                        allNotCheck();
                        item.setCheck(!item.isCheck());
                        notifyDataSetChanged();
                    }
                }
            });


        }

        private void allNotCheck() {
            for (Equip item : items) {
                item.setCheck(false);
            }
            notifyDataSetChanged();
        }
    }


    //banlap: 场景选择图标 icon
    private static class SceneIconViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;

        public SceneIconViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_scene_icon);
        }
    }

    private static class SceneIconAdapter extends RecyclerView.Adapter<SceneIconViewHolder> {

        private Context mContext;
        private final Integer[] data;
        private final Integer[] dataSelected;

        private int selectIndex = 0;

        public SceneIconAdapter(Context context, Integer[] arg0, Integer[] arg1) {
            this.mContext = context;
            this.data = arg0;
            this.dataSelected = arg1;
        }

        public void setSelectIndex(int selectIndex) {
            this.selectIndex = selectIndex;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SceneIconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_scene_new_icon, parent, false);
            return new SceneIconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SceneIconViewHolder holder, int position) {
            if(selectIndex == position) {
                holder.icon.setBackgroundResource(dataSelected[position]);
            } else {
                holder.icon.setBackgroundResource(data[position]);
            }
            holder.itemView.setOnClickListener(v->{
                setSelectIndex(position);
                mSelectIndex = position;
            });
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }

}
