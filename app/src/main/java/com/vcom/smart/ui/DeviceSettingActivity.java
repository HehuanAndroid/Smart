package com.vcom.smart.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseBindingAdapter;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivityDeviceSettingBinding;
import com.vcom.smart.databinding.DialogSelectScenesBinding;
import com.vcom.smart.databinding.ItemSelectSceneBinding;
import com.vcom.smart.databinding.ItemTouchSwitchBinding;
import com.vcom.smart.model.Equip;
import com.vcom.smart.model.Product;
import com.vcom.smart.model.Region;
import com.vcom.smart.model.Scene;
import com.vcom.smart.model.TouchSwitch;
import com.vcom.smart.server.VcomService;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.uivm.DeviceSettingVM;
import com.vcom.smart.utils.GsonUtil;
import com.vcom.smart.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeviceSettingActivity extends BaseMvvmActivity<DeviceSettingVM, ActivityDeviceSettingBinding>
        implements DeviceSettingVM.DeviceSettingVmCallback {

    private Scene mScene;
    private Equip mEquip;
    private Product mProduct;

    private List<TouchSwitch> touchSceneList;
    private TouchSwitchAdapter adapter;
    private AlertDialog selectSceneDialog;

    private final AtomicBoolean isGetParams = new AtomicBoolean(false);

    private boolean isOn = true;

    private int mode = 0;
    private byte red = 0;
    private byte green = 0;
    private byte blue = 0;
    private byte brightness = 50;
    private byte temperature = 0;

    private byte operate = 0;

    private char[] status = null;

    private byte cmd = 0;
    private int devType = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallback(this);

        if (!isGetParams.get()) {
            getViewDataBind().deviceSettingSave.setVisibility(View.GONE);
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTitle.setVisibility(View.GONE);

        }
        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeViewTemperature.setVisibility(View.GONE);
        int addr = Integer.parseInt(mEquip.getMeshAddress());

        //banlap: 电机窗帘设置
        if(mEquip.getProductUuid().equals("20484")){
            devType = 2;
            cmd = 0x01;
            getViewDataBind().deviceSettingIncludeCurtainSwitchView.setVisibility(View.VISIBLE);

            //banlap: 电机窗帘打开
            getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainOpen.setOnClickListener(v->{
                operate = (byte) 0x00;
                //banlap: 场景中设置选择功能，当前不执行该功能
                if (isGetParams.get()) {
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainOpen.setBackground(getDrawable(R.drawable.shape_button_selected_green));
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainStop.setBackground(getDrawable(R.drawable.selector_button_green));
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainClose.setBackground(getDrawable(R.drawable.selector_button_green));
                    return;
                }
                byte opcode = (byte) 0xF3;
                byte[] params = new byte[]{0x00, operate};
                VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);
            });
            //banlap: 电机窗帘停止
            getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainStop.setOnClickListener(v->{
                operate = (byte) 0x02;
                //banlap: 场景中设置选择功能，当前不执行该功能
                if (isGetParams.get()) {
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainOpen.setBackground(getDrawable(R.drawable.selector_button_green));
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainStop.setBackground(getDrawable(R.drawable.shape_button_selected_green));
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainClose.setBackground(getDrawable(R.drawable.selector_button_green));
                    return;
                }
                byte opcode = (byte) 0xF3;
                byte[] params = new byte[]{0x00, operate};
                VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);
            });
            //banlap: 电机窗帘关闭
            getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainClose.setOnClickListener(v->{
                operate = (byte) 0x01;
                //banlap: 场景中设置选择功能，当前不执行该功能
                if (isGetParams.get()) {
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainOpen.setBackground(getDrawable(R.drawable.selector_button_green));
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainStop.setBackground(getDrawable(R.drawable.selector_button_green));
                    getViewDataBind().deviceSettingIncludeCurtainSwitch.clCurtainClose.setBackground(getDrawable(R.drawable.shape_button_selected_green));
                    return;
                }
                byte opcode = (byte) 0xF3;
                byte[] params = new byte[]{0x00, operate};
                VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);
            });
            return;
        }
        //banlap: 三位开关设置
        if (mEquip.getProductUuid().equals("22532")) {
            devType = 1;
            cmd = 0x02;
            getViewDataBind().deviceSettingIncludeThreeSwitchView.setVisibility(View.VISIBLE);

            getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOneIcon.setBackgroundResource(R.drawable.ic_power_on);
            getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwoIcon.setBackgroundResource(R.drawable.ic_power_on);
            getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThreeIcon.setBackgroundResource(R.drawable.ic_power_on);

            if (!isGetParams.get()) {
                if (mEquip.getSwitchStatus().charAt(0) == '1') {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOneIcon.setBackgroundResource(R.drawable.ic_power_on_w);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setTag("1");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setBackgroundResource(R.drawable.shape_switch_bg_green);
                } else {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOneIcon.setBackgroundResource(R.drawable.ic_power_on);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setTag("0");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setBackgroundResource(R.drawable.shape_switch_bg_gray);
                }

                if (mEquip.getSwitchStatus().charAt(1) == '1') {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwoIcon.setBackgroundResource(R.drawable.ic_power_on_w);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setTag("1");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setBackgroundResource(R.drawable.shape_switch_bg_green);
                } else {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwoIcon.setBackgroundResource(R.drawable.ic_power_on);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setTag("0");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setBackgroundResource(R.drawable.shape_switch_bg_gray);
                }

                if (mEquip.getSwitchStatus().charAt(2) == '1') {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThreeIcon.setBackgroundResource(R.drawable.ic_power_on_w);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setTag("1");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setBackgroundResource(R.drawable.shape_switch_bg_green);
                } else {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThreeIcon.setBackgroundResource(R.drawable.ic_power_on);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setTag("0");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setBackgroundResource(R.drawable.shape_switch_bg_gray);
                }

                status = mEquip.getSwitchStatus().toCharArray();
            } else {
                status = new char[]{'0', '0', '0'};
            }
            getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setOnClickListener(v -> {
                //banlap: 当保存到场景时，三位开关控制位置做调整，否则保存参数会出现问题
                if (isGetParams.get()) {
                    switchOption(mEquip, 2);
                } else {
                    switchOption(mEquip, 0);
                }


                if ("0".equals(getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.getTag().toString())) {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOneIcon.setBackgroundResource(R.drawable.ic_power_on_w);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setTag("1");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setBackgroundResource(R.drawable.shape_switch_bg_green);
                } else {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOneIcon.setBackgroundResource(R.drawable.ic_power_on);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setTag("0");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchOne.setBackgroundResource(R.drawable.shape_switch_bg_gray);
                }
            });

            getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setOnClickListener(v -> {
                switchOption(mEquip, 1);

                if ("0".equals(getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.getTag().toString())) {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwoIcon.setBackgroundResource(R.drawable.ic_power_on_w);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setTag("1");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setBackgroundResource(R.drawable.shape_switch_bg_green);
                } else {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwoIcon.setBackgroundResource(R.drawable.ic_power_on);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setTag("0");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchTwo.setBackgroundResource(R.drawable.shape_switch_bg_gray);
                }
            });

            getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setOnClickListener(v -> {
                //banlap: 当保存到场景时，三位开关控制位置做调整，否则保存参数会出现问题
                if (isGetParams.get()) {
                    switchOption(mEquip, 0);
                } else {
                    switchOption(mEquip, 2);
                }

                if ("0".equals(getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.getTag().toString())) {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThreeIcon.setBackgroundResource(R.drawable.ic_power_on_w);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setTag("1");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setBackgroundResource(R.drawable.shape_switch_bg_green);
                } else {
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThreeIcon.setBackgroundResource(R.drawable.ic_power_on);
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setTag("0");
                    getViewDataBind().deviceSettingIncludeThreeSwitch.deviceSettingIncludeThreeSwitchThree.setBackgroundResource(R.drawable.shape_switch_bg_gray);
                }
            });
            return;
        }
        //banlap: 触摸场景开关
        if (mEquip.getProductUuid().equals("21508")) {
            getViewDataBind().deviceSettingIncludeTouchSwitchView.setVisibility(View.VISIBLE);
            touchSceneList = new ArrayList<>();
            adapter = new TouchSwitchAdapter(this);
            adapter.setItems(touchSceneList);
            getViewDataBind().deviceSettingIncludeTouchSwitch.deviceSettingTouchSwitchRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            getViewDataBind().deviceSettingIncludeTouchSwitch.deviceSettingTouchSwitchRecycler.setAdapter(adapter);
            getViewModel().getTouchSwitchSceneList(mEquip.getUserEquipId());
            return;
        }

        if (mEquip.getProductUuid().equals("273")) {
            cmd = 0x03;
        }

       /* if (mEquip.getProductUuid().equals("258")) {
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTitle.setVisibility(View.GONE);
        }*/

        //banlap: 当为 0x901（2305）杀菌教室灯时，隐藏色温调控
        if(mEquip.getProductUuid().equals("2305")) {
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeViewTemperature.setVisibility(View.GONE);
        }

        getViewDataBind().deviceSettingIncludeLampView.setVisibility(View.VISIBLE);
        getViewDataBind().deviceSettingIncludeLamp.deviceSettingIncludeLampSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    isOn = isChecked;
                    getViewDataBind().deviceSettingIncludeLamp.deviceSettingIncludeLampViewSwitch.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
        );

        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTxtCold.setOnClickListener(v -> {
            temperature = (byte) 90;
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeSbTemperature.setProgress(temperature);
            if (isGetParams.get()) {
                red=0;
                blue=0;
                green=0;
                return;
            }
            byte opcode = (byte) 0xE2;
            byte[] params = new byte[]{0x05, temperature};
            VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);

            red=0;
            blue=0;
            green=0;
        });
        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTxtNormal.setOnClickListener(v -> {
            temperature = (byte) 50;
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeSbTemperature.setProgress(temperature);
            if (isGetParams.get()) {
                red=0;
                blue=0;
                green=0;
                return;
            }
            byte opcode = (byte) 0xE2;
            byte[] params = new byte[]{0x05, temperature};
            VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);

            red=0;
            blue=0;
            green=0;
        });
        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTxtWarm.setOnClickListener(v -> {
            temperature = (byte) 10;
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeSbTemperature.setProgress(temperature);
            if (isGetParams.get()) {
                red=0;
                blue=0;
                green=0;
                return;
            }
            byte opcode = (byte) 0xE2;
            byte[] params = new byte[]{0x05, temperature};
            VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);

            red=0;
            blue=0;
            green=0;
        });

        //banlap: 设备调节亮度监听
        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTxtBrightnessLength.setText("55%");
        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = (byte) (progress + 10);
                getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTxtBrightnessLength.setText(brightness + "%");
                if (isGetParams.get()) {
                    //red=0;
                    //blue=0;
                    //green=0;
                    return;
                }

                byte opcode = (byte) 0xD2;
                byte[] params = new byte[]{brightness};
                VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);

                //red=0;
                //blue=0;
                //green=0;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //banlap: 设备调节色温监听
        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTxtTemperatureLength.setText("4600K");
        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeSbTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temperature = (byte) progress;
                getViewDataBind().deviceSettingIncludeLamp.deviceIncludeTxtTemperatureLength.setText(((65 - 27) * progress) + 2700 + "K");
                if (isGetParams.get()) {
                    red=0;
                    blue=0;
                    green=0;
                    return;
                }
                byte opcode = (byte) 0xE2;
                byte[] params = new byte[]{0x05, temperature};
                VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);

                red=0;
                blue=0;
                green=0;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //banlap: 设备调节色彩监听
        getViewDataBind().deviceSettingIncludeLamp.deviceIncludeColorPicker.setListener(color -> {
            red = (byte) (color >> 16 & 0xFF);
            green = (byte) (color >> 8 & 0xFF);
            blue = (byte) (color & 0xFF);

            if (isGetParams.get()) {
                return;
            }

            byte opcode = (byte) 0xE2;
            byte[] params = new byte[]{0x04, red, green, blue};

            VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);
        });

        int lampMode = 0;
        for (Product.EquipInfo equipInfo : mProduct.getEquipInfoList()) {
            if (equipInfo.getEquipInfoPid().equals(mEquip.getEquipInfoPid())) {
                lampMode = Integer.parseInt(equipInfo.getEquipInfoType());
            }
        }
        if (lampMode == 0) {
            finish();
        }

        if (lampMode >= 1) {
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeViewBrightness.setVisibility(View.VISIBLE);
            //banlap: 特殊情况 杀菌教室灯 为 2305, lampMode为 1 只有在mode为2时才能调光 （可能是默认调节色温当作调光，可以与zdd api对接问题）
            if(mEquip.getProductUuid().equals("2305")) {
                mode = 2;
            }
        }

        if (lampMode >= 2) {
            mode = 2;
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeViewTemperature.setVisibility(View.VISIBLE);
        }

        if (lampMode == 3) {
            mode = 1;
            getViewDataBind().deviceSettingIncludeLamp.deviceIncludeColorPicker.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void initDatum() {
        if (getIntent().getExtras() == null) {
            finish();
        }
        String equipId = getIntent().getStringExtra("equipId");
        if (TextUtils.isEmpty(equipId)) {
            finish();
        }
        String sceneId = getIntent().getStringExtra("sceneId");
        if (!TextUtils.isEmpty(sceneId)) {
            isGetParams.set(true);
        }

        if (isGetParams.get()) {
            for (Region region : VcomSingleton.getInstance().getUserRegion()) {
                for (Scene scene : region.getSceneList()) {
                    if (scene.getSceneId().equals(sceneId)) {
                        mScene = scene;
                    }
                }
            }
            if (mScene == null) {
                finish();
            }
        }

        for (Equip userEquip : VcomSingleton.getInstance().getUserEquips()) {
            if (userEquip.getUserEquipId().equals(equipId)) {
                mEquip = userEquip;
            }
        }
        if (mEquip == null) {
            finish();
        }

        for (Product product : VcomSingleton.getInstance().getUserProduct()) {
            for (Product.EquipInfo equipInfo : product.getEquipInfoList()) {
                if (mEquip.getProductUuid().equals(equipInfo.getEquipInfoPid())) {
                    mProduct = product;
                    break;
                }
            }
        }

        if (mProduct == null) {
            finish();
        }
    }

    /*
    * banlap: 点击保存 设备参数
    * */
    @Override
    public void viewBack() {
        if (isGetParams.get()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title))
                    .setMessage(getString(R.string.dialog_message_save_settings))
                    .setPositiveButton(getString(R.string.dialog_save), (dialog, which) -> {
                        String json = null;
                        Intent intent = new Intent();
                        intent.putExtra("cmd", cmd);

                        Map<String, Object> map = new HashMap<>();

                        if (devType == 0) {
                            if (!isOn) {
                                brightness = 0;
                                temperature = 0;
                                mode = 2;
                            }
                            //banlap: 灯存在可以调rgb和冷暖时，mode需要切换， mode=1为rgb，mode=2为冷暖
                            if (red==0 && blue==0 && green ==0 ) {
                                mode = 2;
                                if(temperature==0){
                                    temperature = 2;
                                }
                            } else {
                                mode = 1;
                            }
                            byte[] params = new byte[]{0x01, cmd, (byte) Integer.parseInt(mScene.getSceneMeshId()), (byte) mode, brightness};
                            byte[] sceneByte = new byte[]{red, green, blue, temperature, 0};

                            intent.putExtra("equip_param", Util.byteMergerAll(params, sceneByte));

                            map.put("mode", mode);
                            map.put("red", red);
                            map.put("blue", blue);
                            map.put("green", green);
                            map.put("brightness", brightness);
                            map.put("temperature", temperature);
                        } else if (devType == 2) {

                            map.put("operate", operate);
                            byte[] params = new byte[]{0x01, cmd, (byte) Integer.parseInt(mScene.getSceneMeshId()), operate};
                            intent.putExtra("equip_param", params);

                        } else {

                            int on = 0;
                            int off = 0;
                            if (status != null) {
                                char[] offStr = new char[status.length];
                                for (int i = 0; i < status.length; i++) {
                                    if (status[i] == '0') {
                                        offStr[i] = '1';
                                    } else {
                                        offStr[i] = '0';
                                    }
                                }
                                on = Integer.parseInt(String.valueOf(status), 2);
                                off = Integer.parseInt(String.valueOf(offStr), 2);
                            }
                            byte[] params = new byte[]{0x01, cmd, (byte) Integer.parseInt(mScene.getSceneMeshId()), (byte) on, (byte) off};
                            intent.putExtra("equip_param", params);

                            map.put("threeSwitch", on);
                        }

                        json = GsonUtil.getInstance().toJson(map);
                        intent.putExtra("save_param", json);
                        setResult(0x1A, intent);
                        finish();
                    })
                    .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> finish())
                    .create();
            alertDialog.show();
        } else {
            finish();
        }
    }

    //banlap: 三位开关选项
    private void switchOption(Equip equip, int index) {

        byte opcode = (byte) 0xF4;

        if (status[index] == '1') {
            status[index] = '0';
        } else {
            status[index] = '1';
        }

        if (isGetParams.get()) {
            return;
        }
        mEquip.setSwitchStatus(String.valueOf(status));

        byte arg = (byte) (1 << index);
        byte[] cmd = new byte[]{0x00, 0, 0};
        if (status[index] == '0') {
            cmd[1] = 1;
        }
        cmd[2] = arg;
        VcomService.getInstance().sendCommandNoResponse(opcode, Integer.parseInt(equip.getMeshAddress()), cmd);
    }

    @Override
    public void refreshTouchSceneList(List<TouchSwitch> list) {
        touchSceneList.clear();
        touchSceneList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void showSceneListDialog(TouchSwitch touchSwitch) {
        if (selectSceneDialog != null && selectSceneDialog.isShowing()) {
            return;
        }

        List<Scene> newSceneList = new ArrayList<>();
        for (Region region : VcomSingleton.getInstance().getUserRegion()) {
            for (Scene scene : region.getSceneList()) {
                Scene newScene = new Scene();
                newScene.setSceneName(region.getSpaceName() + " -> " + scene.getSceneName());
                newScene.setSceneId(scene.getSceneId());
                newScene.setSceneImg(scene.getSceneImg());
                newScene.setSceneMeshId(scene.getSceneMeshId());
                newSceneList.add(newScene);
            }
        }

        DialogSelectScenesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_select_scenes,
                null, false);
        selectSceneDialog = new AlertDialog.Builder(this)
                .setView(binding.getRoot())
                .create();
        selectSceneDialog.show();

        binding.dialogSelectSceneCancel.setOnClickListener(v -> selectSceneDialog.dismiss());
        binding.dialogSelectSceneCommit.setOnClickListener(v -> {
            for (Scene scene : newSceneList) {
                if (scene.isCheck()) {
                    byte opcode = (byte) 0xF6;
                    int address = Integer.parseInt(mEquip.getMeshAddress());
                    byte[] params = new byte[]{0x00, (byte) touchSwitch.getSequence(), (byte) Integer.parseInt(scene.getSceneMeshId())};
                    VcomService.getInstance().sendCommandNoResponse(opcode, address, params);
                    getViewModel().updateTouchSwitchScene(touchSwitch.getUserEquipId(), touchSwitch.getSequence(), scene.getSceneId());
                    selectSceneDialog.dismiss();
                    break;
                }
            }
        });

        SelectSceneAdapter selectSceneAdapter = new SelectSceneAdapter(this);
        selectSceneAdapter.setItems(newSceneList);
        binding.dialogSelectSceneRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.dialogSelectSceneRecycler.setAdapter(selectSceneAdapter);
        selectSceneAdapter.notifyDataSetChanged();

    }

    private class TouchSwitchAdapter extends BaseBindingAdapter<TouchSwitch, ItemTouchSwitchBinding> {

        public TouchSwitchAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected int getLayoutId(int layoutId) {
            return R.layout.item_touch_switch;
        }

        @Override
        protected void onBindItem(ItemTouchSwitchBinding binding, TouchSwitch item, int i) {

            if (TextUtils.isEmpty(item.getSceneName())) {
                binding.itemTouchAddIcon.setVisibility(View.VISIBLE);
                binding.itemTouchLine.setVisibility(View.GONE);
                binding.itemTouchSpaceName.setVisibility(View.GONE);
                binding.itemTouchSceneName.setText(R.string.Configuration_scene);
                binding.itemTouchSceneName.setTextColor(getResources().getColor(R.color.green));
                binding.itemTouchSceneView.setBackgroundResource(R.drawable.shape_radius_touch_bg_green_hollow);
            } else {
                binding.itemTouchAddIcon.setVisibility(View.GONE);
                binding.itemTouchLine.setVisibility(View.VISIBLE);
                binding.itemTouchSpaceName.setVisibility(View.VISIBLE);
                binding.itemTouchSpaceName.setText(item.getSpaceName());
                binding.itemTouchSceneName.setText(item.getSceneName());
                binding.itemTouchSceneName.setTextColor(getResources().getColor(R.color.white));
                binding.itemTouchSceneView.setBackgroundResource(R.drawable.shape_radius_touch_bg_green);
            }

            binding.itemTouchSceneView.setOnClickListener(v -> showSceneListDialog(item));
        }
    }

    private static class SelectSceneAdapter extends BaseBindingAdapter<Scene, ItemSelectSceneBinding> {

        public SelectSceneAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected int getLayoutId(int layoutId) {
            return R.layout.item_select_scene;
        }

        @Override
        protected void onBindItem(ItemSelectSceneBinding binding, Scene item, int i) {
            binding.itemSelectSceneName.setText(item.getSceneName());
            binding.itemSelectSceneIcon.setBackgroundResource(mContext.getResources()
                    .getIdentifier("ic_scene_selected_" + item.getSceneImg(), "drawable", mContext.getPackageName()));

            if (item.isCheck()) {
                binding.itemSelectSceneSelection.setVisibility(View.VISIBLE);
                binding.itemSelectSceneSelection.setBackgroundResource(R.drawable.ic_select_yes);
            } else {
                binding.itemSelectSceneSelection.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                allNotCheck();
                item.setCheck(!item.isCheck());
                notifyDataSetChanged();
            });
        }

        private void allNotCheck() {
            for (Scene item : items) {
                item.setCheck(false);
            }
            notifyDataSetChanged();
        }
    }
}