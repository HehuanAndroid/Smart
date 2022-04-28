package com.vcom.smart.uivm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LeUpdateParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.bluetooth.light.UuidInformation;
import com.telink.util.Event;
import com.vcom.smart.R;
import com.vcom.smart.VcomApp;
import com.vcom.smart.model.DefaultMac;
import com.vcom.smart.model.Equip;
import com.vcom.smart.request.ApiLoader;
import com.vcom.smart.request.ApiObserver;
import com.vcom.smart.server.VcomService;
import com.vcom.smart.singleton.VcomSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.ResponseBody;

/**
 * @Author Lzz
 * @Date 2020/11/3 9:40
 */
public class ScanByBleVM extends AndroidViewModel {

    private final List<DeviceInfo> mScanDevList = new ArrayList<>();
    private final List<DeviceInfo> mSelectDevList = new ArrayList<>();

    private final AtomicBoolean isScanning = new AtomicBoolean(false);

    private final List<Integer> meshList = new ArrayList<>();

    private ScanByBleCallBack callBack;

    public ScanByBleVM(@NonNull Application application) {
        super(application);

        for (Equip userEquip : VcomSingleton.getInstance().getUserEquips()) {
            meshList.add(Integer.valueOf(userEquip.getMeshAddress()));
        }
    }

    public void setCallBack(ScanByBleCallBack callBack) {
        this.callBack = callBack;
    }

    public void performed(Event<String> event) {
        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                DeviceInfo deviceInfo = ((LeScanEvent) event).getArgs();
                if (!mScanDevList.contains(deviceInfo)) {
                    mScanDevList.add(deviceInfo);
                }
                refreshScanList(false, mScanDevList);
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                isScanning.set(false);
                Log.e("Test", "ble scan timeout.");
                callBack.viewIsScanning(isScanning.get());
                break;
            case LeScanEvent.LE_SCAN_COMPLETED:
                isScanning.set(false);
                Log.e("Test", "ble scan completed.");
                callBack.viewIsScanning(isScanning.get());
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
        }
    }

    private void onDeviceStatusChanged(DeviceEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
                Log.e("test", "update mesh failure.");
                executeBinding();
                break;
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED:
                Log.e("test", "update mesh completed. ");

                if (mSelectDevList.contains(deviceInfo)) {
                    meshList.add(deviceInfo.meshAddress);
                    saveBindEquip(deviceInfo);
                    mSelectDevList.remove(deviceInfo);
                    mScanDevList.remove(deviceInfo);
                }
                executeBinding();
                refreshScanList(mSelectDevList.size() == 0, mScanDevList);

                break;
        }
    }

    public void startScan() {
        if (isScanning.get()) {
            return;
        }

        boolean ble = VcomSingleton.getInstance().isBleReady.get();
//        boolean gps = VcomSingleton.getInstance().isGpsReady.get();
//        boolean permission = VcomSingleton.getInstance().isLocationReady.get();
        //banlap: 强制执行扫描设备（之后需要恢复原样）
        //|| !gps || !permission
        if (!ble ) {
            if (LeBluetooth.getInstance().isSupport(getApplication())) {
                LeBluetooth.getInstance().enable(getApplication());
            }
//            return;
        }



        mScanDevList.clear();
        mSelectDevList.clear();

        LeScanParameters params = LeScanParameters.create();
        params.setMeshName(VcomApp.DEFAULT_NAME);
        params.setOutOfMeshName(VcomApp.DEFAULT_NAME);
        params.setTimeoutSeconds(6);
        params.setScanMode(false);
        VcomService.getInstance().idleMode(true);
        VcomService.getInstance().startScan(params);

        isScanning.set(true);
        callBack.viewIsScanning(isScanning.get());
    }

    public void stopScan() {
        LeBluetooth.getInstance().stopScan();
        isScanning.set(false);
    }

    public void viewBindEquip() {
        callBack.viewBindEquip();
    }

    //banlap: 扫描设备时，显示设备列表的关键代码
    private void refreshScanList(boolean hideLoading, List<DeviceInfo> devices) {
        List<DeviceInfo> filterDevs = new ArrayList<>();
        for (DeviceInfo device : devices) {
            //banlap: 注释则强制显示蓝牙设备（之后需要恢复原样）
            for (DefaultMac defaultMac : VcomSingleton.getInstance().getDefaultMacs()) {
                if (defaultMac.getProductMac().equals(device.macAddress)) {
                    filterDevs.add(device);
                }
            }

            //filterDevs.add(device);
        }

        callBack.viewRefreshList(hideLoading, filterDevs);
    }

    public void bindEquips(List<DeviceInfo> deviceInfos) {
        mSelectDevList.clear();
        mSelectDevList.addAll(deviceInfos);

        if (mSelectDevList.size() > 0) {
            isScanning.set(false);
            callBack.viewIsScanning(isScanning.get());
            executeBinding();
        } else {
            Toast.makeText(getApplication(), getApplication().getString(R.string.toast_no_device_selected),Toast.LENGTH_SHORT).show();
        }
    }

    private void executeBinding() {

        if (mSelectDevList.size() == 0) {
           return;
        }

        int meshAddress = new Random().nextInt(255);
        for (Integer integer : meshList) {
            if (integer == meshAddress) {
                meshAddress = new Random().nextInt(255);
            }
        }
        mSelectDevList.get(0).meshAddress = meshAddress;

        List<DeviceInfo> newList = new ArrayList<>();
        newList.add(mSelectDevList.get(0));

        VcomService.getInstance().idleMode(false);
        LeUpdateParameters params = Parameters.createUpdateParameters();
        params.setOldMeshName(VcomApp.DEFAULT_NAME);
        params.setOldPassword(VcomApp.DEFAULT_PASSWORD);
        params.setNewMeshName(VcomApp.NEW_NAME);
        //banlap: 测试设置密码绑定蓝牙设备（之后需要恢复原样）
        //params.setNewPassword("654321");
        params.setNewPassword(VcomSingleton.getInstance().getLoginUser().getMeshPassword());
        params.setUpdateDeviceList(newList);
        VcomService.getInstance().updateMesh(params);
    }

    private void saveBindEquip(DeviceInfo device) {

        Equip equip = new Equip();
        equip.setMac(device.macAddress);
        equip.setMeshAddress(device.meshAddress + "");
        equip.setFirmwareVersion(device.firmwareRevision);
        equip.setEquipName(device.deviceName);
        equip.setProductUuid(device.productUUID + "");
        equip.setUserId(VcomSingleton.getInstance().getLoginUser().getUserId());

        ApiLoader.getApi().addSingleEquip(equip, new ApiObserver<ResponseBody>() {
            @Override
            protected void showMessage(String message) {
                //Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(String data) {
                callBack.saveBindEquipSuccess(equip);
            }

            @Override
            protected void onFailure() {
                callBack.saveBindEquipFailure();
            }

            @Override
            protected void onError() {
                callBack.saveBindEquipFailure();
            }
        });

    }

    public void back() {
        callBack.viewBack();
    }

    public interface ScanByBleCallBack {
        void viewBack();

        void viewRefreshList(boolean hideLoading, List<DeviceInfo> deviceInfos);

        void viewIsScanning(boolean isScan);

        void viewBindEquip();

        void saveBindEquipSuccess(Equip equip);
        void saveBindEquipFailure();

    }

}
