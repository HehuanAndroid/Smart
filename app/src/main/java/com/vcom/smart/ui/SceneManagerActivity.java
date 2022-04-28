package com.vcom.smart.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylanc.activityresult.launcher.StartActivityLauncher;
import com.telink.bluetooth.light.ConnectionStatus;
import com.vcom.smart.R;
import com.vcom.smart.base.BaseBindingAdapter;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivitySceneManagerBinding;
import com.vcom.smart.databinding.DialogSceneAddEquipBinding;
import com.vcom.smart.databinding.DialogSelectIconBinding;
import com.vcom.smart.databinding.DialogSelectTimingBinding;
import com.vcom.smart.databinding.ItemScanDeviceBinding;
import com.vcom.smart.databinding.ItemSceneManagerEquipBinding;
import com.vcom.smart.model.Equip;
import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.model.Product;
import com.vcom.smart.model.Region;
import com.vcom.smart.model.Scene;
import com.vcom.smart.server.VcomService;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.uivm.SceneManagerVM;
import com.vcom.smart.utils.GsonUtil;
import com.vcom.smart.utils.Util;

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

public class SceneManagerActivity extends BaseMvvmActivity<SceneManagerVM, ActivitySceneManagerBinding>
        implements SceneManagerVM.SceneManagerVmCallBack {

    private Scene mScene;
    private String sceneId;
    private String mRegionId;

    private AlertDialog alertDialog;
    private SceneManagerAdapter adapter;
    private final List<Equip> sceneEquips = new ArrayList<>();
    private final List<Equip> equipList = VcomSingleton.getInstance().getUserEquips();
    private final StartActivityLauncher startActivityLauncher = new StartActivityLauncher(this);
    private int tagMeshAddress = -1;
    private byte[] tagParam = null;

    //默认场景图标
    private Integer[] mIcons = {R.drawable.ic_scene_0, R.drawable.ic_scene_1, R.drawable.ic_scene_2, R.drawable.ic_scene_3};
    private Integer[] mIconSelected = {R.drawable.ic_scene_selected_0, R.drawable.ic_scene_selected_1, R.drawable.ic_scene_selected_2, R.drawable.ic_scene_selected_3};

    public static int mSelectIndex = 0;    //选择场景图标位置, 默认为 0

    private AtomicInteger hour;
    private AtomicInteger min;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_manager;
    }

    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);
        EventBus.getDefault().register(this);

        //banlap: 判断mScene不为null时 传入相应值
        if(mScene!=null) {
            getViewDataBind().sceneManagerName.setText(mScene.getSceneName());
            getViewDataBind().sceneManagerName.setSelection(mScene.getSceneName().length());
            //banlap: 传递场景图标
            int iconCount = Integer.parseInt(mScene.getSceneImg().trim());
            getViewDataBind().ivSelectSceneIcon.setBackgroundResource(mIconSelected[iconCount]);
            mSelectIndex = iconCount;
        }

        adapter = new SceneManagerAdapter(this);
        adapter.setItems(sceneEquips);
        getViewDataBind().sceneManagerRecycler.setLayoutManager(new LinearLayoutManager(this));
        getViewDataBind().sceneManagerRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.msgCode == MessageEvent.regionReady) {
            for (Region region : VcomSingleton.getInstance().getUserRegion()) {
                for (Scene scene : region.getSceneList()) {
                    if (scene.getSceneId().equals(sceneId)) {
                        mScene = scene;
                        break;
                    }
                }
            }
            if (mScene == null) {
                finish();
            }
            sceneEquips.clear();
            sceneEquips.addAll(mScene.getUserEquipList());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initDatum() {
        if (getIntent().getExtras() == null) {
            finish();
        }

        mRegionId = getIntent().getStringExtra("regionId");
        sceneId = getIntent().getStringExtra("sceneId");
        for (Region region : VcomSingleton.getInstance().getUserRegion()) {
            for (Scene scene : region.getSceneList()) {
                if (scene.getSceneId().equals(sceneId)) {
                    mScene = scene;
                    break;
                }
            }
        }

        if (mScene == null) {
            finish();
        } else {
            sceneEquips.addAll(mScene.getUserEquipList());
        }

    }

    @Override
    public void viewBack() {
        finish();
    }

    @Override
    public void viewGoAddEquip() {
        alertDialog = null;
        if (equipList.size() > 0) {

            List<Equip> newEquipList = new ArrayList<>();

            for (Equip equip : equipList) {
                //banlap: 场景中添加设备列表时 除去21508 智能场景开关
                if (!equip.getProductUuid().equals("21508")) {
                    equip.setCheck(false);
                    newEquipList.add(equip);
                }
            }

            for (Equip sceneEquip : sceneEquips) {
                for (Equip equip : newEquipList) {
                    if (sceneEquip.getMac().equals(equip.getMac())) {
                        newEquipList.remove(equip);
                        break;
                    }
                }
            }

            DialogSceneAddEquipBinding addEquipBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_scene_add_equip,
                    null, false);
            alertDialog = new AlertDialog.Builder(this)
                    .setView(addEquipBinding.getRoot())
                    .create();
            ScanAddEquipAdapter deviceAdapter = new ScanAddEquipAdapter(this);
            addEquipBinding.dialogSceneAddEquipRecycler.setLayoutManager(new LinearLayoutManager(this));
            addEquipBinding.dialogSceneAddEquipRecycler.setAdapter(deviceAdapter);
            deviceAdapter.setItems(newEquipList);
            deviceAdapter.notifyDataSetChanged();

            addEquipBinding.dialogSceneAddEquipCancel.setOnClickListener(v -> alertDialog.dismiss());
            addEquipBinding.dialogSceneAddEquipNext.setOnClickListener(v -> {
                for (Equip equip : newEquipList) {
                    if (equip.isCheck()) {
                        getParamsForActivity(equip);
                        alertDialog.dismiss();
                        break;
                    }
                }
            });

        } else {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title))
                    .setMessage(getString(R.string.dialog_message_add_device_into_empty_list))
                    .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> {
                        getViewModel().goAddEquip();
                        dialog.dismiss();
                        finish();
                    })
                    .create();
        }
        alertDialog.show();
    }

    /*
     * banlap: 选择场景图标
     * */
    @Override
    public void viewSelectSceneIcon() {
        DialogSelectIconBinding selectIconBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_select_icon, null, false);
        alertDialog = new AlertDialog.Builder(this)
                .setView(selectIconBinding.getRoot())
                .create();


        SceneManagerActivity.SceneIconAdapter sceneIconAdapter = new SceneManagerActivity.SceneIconAdapter(this, mIcons, mIconSelected);
        selectIconBinding.rvSelectIconList.setLayoutManager(new GridLayoutManager(this, 2));
        selectIconBinding.rvSelectIconList.setAdapter(sceneIconAdapter);
        selectIconBinding.btSelectIconCancel.setOnClickListener(v->{alertDialog.dismiss();});
        sceneIconAdapter.notifyDataSetChanged();

        //banlap: 点击确认选择该场景图标
        selectIconBinding.btSelectIconCommit.setOnClickListener(v->{
            alertDialog.dismiss();
            getViewDataBind().ivSelectSceneIcon.setBackgroundResource(mIconSelected[mSelectIndex]);
        });

        alertDialog.show();
    }

    /*
    * banlap: 选择定时
    * */
    @Override
    public void viewSelectSceneTiming() {
        DialogSelectTimingBinding dialogSelectTimingBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_select_timing, null, false);
        alertDialog = new AlertDialog.Builder(this)
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
            //setTime(Integer.parseInt(String.valueOf(hour)), Integer.parseInt(String.valueOf(min)));
            alertDialog.dismiss();
        });

        dialogSelectTimingBinding.btSelectIconCommit.setOnClickListener(v-> {
            //setTiming(Integer.parseInt(String.valueOf(hour)), Integer.parseInt(String.valueOf(min)));
            String setMin = "" + Integer.parseInt(String.valueOf(min));
            if(Integer.parseInt(String.valueOf(min))<10) {
                setMin = "0" + setMin;
            }
            String showTiming = Integer.parseInt(String.valueOf(hour)) + ":" + setMin;
            getViewDataBind().tvShowTiming.setText(showTiming);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }



    @Override
    public void viewGoDeleteEquip() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message_delete_scene))
                .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> {
                    //getViewModel().deleteScene(mScene.getSceneId());
                    deleteSceneEquip(mScene);
                    finish();
                })
                .create()
                .show();
    }

    //banlap: 先对设备删除场景id 后删除场景
    public void deleteSceneEquip(Scene scene) {
        if(scene.getUserEquipList().size()>0) {
            for (int i=0; i<scene.getUserEquipList().size(); i++) {
                if(Integer.parseInt(scene.getUserEquipList().get(i).getMeshAddress()) != -1) {
                    int meshId = Integer.parseInt(scene.getUserEquipList().get(i).getMeshAddress());
                    byte opcode = (byte) 0xEE;
                    byte[] param = {0x00, (byte) Integer.parseInt(scene.getSceneMeshId())};
                    VcomService.getInstance().sendCommandNoResponse(opcode, meshId, param);
                }

            }
        }
        getViewModel().deleteScene(scene.getSceneId());
    }
    @Override
    public void deleteSceneSuccess() {
        Toast.makeText(getApplication(),getString(R.string.toast_delete_success),Toast.LENGTH_SHORT).show();
    }

    /*
    * banlap: 更新场景信息
    * */
    @Override
    public void viewGoUpdateScene() {
        String newSceneName = getViewDataBind().sceneManagerName.getText().toString();
        if (TextUtils.isEmpty(newSceneName)) {
            return;
        }
        mScene.setSceneName(newSceneName);
        //banlap: 更新场景图标
        mScene.setSceneImg(String.valueOf(mSelectIndex));
        getViewModel().updateScene(mScene);
    }

    @Override
    public void updateSceneSuccess() {
        Toast.makeText(getApplication(),getString(R.string.toast_update_success) ,Toast.LENGTH_SHORT).show();

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

      /*  Toast.makeText(this, "当前时间: "
                + year + " | " + month + " | " + day + " | "
                + hour + " | " + min + " | " + sec + " | "
                + yearLsb + " | " + yearMsb, Toast.LENGTH_LONG).show();*/

        byte opcode = (byte) 0xE4;     //设置灯的时间 操作码
        byte[] param = new byte[] {(byte) yearLsb, (byte) yearMsb,  (byte) month, (byte) day,  (byte) hour1, (byte) min1, (byte) sec};

        for(int i=0; i < sceneEquips.size(); i++) {
            if (Integer.parseInt(sceneEquips.get(i).getMeshAddress()) != -1) {
                VcomService.getInstance().sendCommandNoResponse(opcode, Integer.parseInt(sceneEquips.get(i).getMeshAddress()), param);
            }
        }

        setTiming(hour, min);
    }
    /*
    * banlap: 设备更新定时
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
        byte alarmType = (byte) 0x02;  //更新闹钟
        int index = 1; //闹钟索引
        byte[] param = new byte[] {alarmType, (byte) index, (byte) 0x82,
                (byte) month, (byte) day, (byte) hour, (byte) min, (byte) 0x00, (byte) Integer.parseInt(mScene.getSceneMeshId())};

        for(int i=0; i < sceneEquips.size(); i++) {
            if(Integer.parseInt(sceneEquips.get(i).getMeshAddress()) != -1) {
                VcomService.getInstance().sendCommandNoResponse(opcode, Integer.parseInt(sceneEquips.get(i).getMeshAddress()), param);
            }
        }

    }


    /*
    * banlap: 在此场景下删除设备 提示
    * */
    private void deleteSceneEquip(Equip equip) {
        tagMeshAddress = -1;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message_delete_scene_device))
                .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> {
                    tagMeshAddress = Integer.parseInt(equip.getMeshAddress());
                    getViewModel().deleteSceneEquip(VcomSingleton.getInstance().getLoginUser().getUserId(), mRegionId, mScene.getSceneId(), equip.getUserEquipId());
                    dialog.dismiss();
                })
                .create()
                .show();
    }
    /*
     * banlap: 修改场景页面 - 删除设备
     * */
    @Override
    public void deleteSceneEquipSuccess() {
        Toast.makeText(this, getString(R.string.toast_delete_success),Toast.LENGTH_SHORT).show();

        if (tagMeshAddress == -1) {
            return;
        }
        byte opcode = (byte) 0xEE;
        byte[] param = {0x00, (byte) Integer.parseInt(mScene.getSceneMeshId())};
        VcomService.getInstance().sendCommandNoResponse(opcode, tagMeshAddress, param);
    }
    @Override
    public void deleteSceneEquipFailure() {
        Toast.makeText(this, getString(R.string.toast_delete_error),Toast.LENGTH_SHORT).show();
    }


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

                    getViewModel().addSceneEquip(GsonUtil.getInstance().toJson(data));
                }
            }
        });
    }

    @Override
    public void addSceneEquipSuccess() {
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

    @Override
    public void addSceneEquipFailure() {
        Toast.makeText(getApplication(),getString(R.string.toast_add_error),Toast.LENGTH_SHORT).show();
    }

    private class SceneManagerAdapter extends BaseBindingAdapter<Equip, ItemSceneManagerEquipBinding> {

        public SceneManagerAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected int getLayoutId(int layoutId) {
            return R.layout.item_scene_manager_equip;
        }

        @Override
        protected void onBindItem(ItemSceneManagerEquipBinding binding, Equip item, int i) {
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
                    binding.itemSceneManagerName.setText(mProduct.getEquiNickName());
                } else {
                    binding.itemSceneManagerName.setText(mProduct.getEquiName());
                }
                resId = mContext.getResources().getIdentifier("ic_" + item.getProductUuid() + "_on", "drawable", mContext.getPackageName());
            }
            if (resId > 0) {
                binding.itemSceneManagerIcon.setBackgroundResource(resId);
            } else {
                binding.itemSceneManagerIcon.setBackgroundResource(R.drawable.ic_lamp_default_on);
            }

            binding.rlSceneSetting.setOnClickListener(v -> getParamsForActivity(item));
            //binding.itemDeviceRemove.setOnClickListener(v -> deleteSceneEquip(item));
            binding.flDelete.setOnClickListener(v -> deleteSceneEquip(item));
        }
    }

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

    private static class SceneIconAdapter extends RecyclerView.Adapter<SceneManagerActivity.SceneIconViewHolder> {

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
        public SceneManagerActivity.SceneIconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_scene_new_icon, parent, false);
            return new SceneManagerActivity.SceneIconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SceneManagerActivity.SceneIconViewHolder holder, int position) {
            //banlap: 传递当前场景的icon值到selectIndex
            selectIndex = mSelectIndex;
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