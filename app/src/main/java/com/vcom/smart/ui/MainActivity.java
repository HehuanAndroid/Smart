package com.vcom.smart.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.ErrorReportEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.util.Event;
import com.telink.util.EventListener;
import com.vcom.smart.R;
import com.vcom.smart.VcomApp;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivityMainBinding;
import com.vcom.smart.dialog.RequestPermissionDialog;
import com.vcom.smart.fragment.DeviceFragment;
import com.vcom.smart.fragment.MainFragment;
import com.vcom.smart.fragment.MeFragment;
import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.server.VcomService;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.uivm.MainVM;
import com.vcom.smart.utils.ActivityUtil;
import com.vcom.smart.utils.Util;
import com.vcom.smart.widget.ConfirmDialogQuit;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends BaseMvvmActivity<MainVM, ActivityMainBinding> implements EventListener<String>, MainVM.MainVmCallBack {

    private RequestPermissionDialog request;
    private static final int WRITE_COARSE_LOCATION_REQUEST_CODE =1 ;
    private final int[] tab_icons = {R.drawable.selector_tab_main, R.drawable.selector_tab_device,
            R.drawable.selector_tab_me};

    private final AtomicBoolean isLoginSuccess = new AtomicBoolean(false);
    private ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                        } else {
                            // No location access granted.
                        }
                    }
            );
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        goCheckPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        VcomApp.getApp().doInit();

        VcomApp.getApp().addEventListener(DeviceEvent.STATUS_CHANGED, this);
        VcomApp.getApp().addEventListener(NotificationEvent.ONLINE_STATUS, this);
        VcomApp.getApp().addEventListener(NotificationEvent.GET_ALARM, this);
        VcomApp.getApp().addEventListener(NotificationEvent.GET_DEVICE_STATE, this);
        VcomApp.getApp().addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        VcomApp.getApp().addEventListener(MeshEvent.OFFLINE, this);
        VcomApp.getApp().addEventListener(ErrorReportEvent.ERROR_REPORT, this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        VcomService.getInstance().disableAutoRefreshNotify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
            getViewModel().stopAutoConnect();
            VcomApp.getApp().doDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void performed(Event<String> event) {
        getViewModel().mainVmPerformed(event);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {

        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);
        EventBus.getDefault().register(this);

        if (VcomSingleton.getInstance().isUserLogin()) {
            getViewModel().validateUser();
        } else {
            validateSuccess();
            goRefreshRegion();
        }
        if (!isLocationServiceEnable(this)){
            //如果用户手机没有打开位置信息，引导用户进入设置页面进行开启
            ConfirmDialogQuit confirmDialog = new ConfirmDialogQuit(this);
            confirmDialog.setOnDialogClickListener(new ConfirmDialogQuit.OnDialogClickListener() {
                @Override
                public void onOKClick() {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    confirmDialog.dismiss();
                }

                @Override
                public void onCancelClick() {
                    confirmDialog.dismiss();
                }
            });
            confirmDialog.setCancelable(false);//点击空白处不消失
            confirmDialog.show();
        }
    
//        if (!hasPermission(MainActivity.this)) {
//            //如果应用没有位置权限，进行申请
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
//                    WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
//        } else{
//            // to do something ！
//        }
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
        initTabs();
    }
    public static boolean isLocationServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }
    public boolean hasPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    protected void initDatum() {

        FragmentTransaction cleanFragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            cleanFragmentTransaction.remove(fragment);
        }
        cleanFragmentTransaction.commitAllowingStateLoss();

        Fragment main = new MainFragment();
        Fragment device = new DeviceFragment();
        Fragment me = new MeFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_fragment, device, "1");
        fragmentTransaction.add(R.id.main_fragment, me, "2");
        fragmentTransaction.add(R.id.main_fragment, main, "0");
        fragmentTransaction.commitAllowingStateLoss();

        new Handler().postDelayed(() -> showAndHideFragmentByTag("0"), 300);
    }

    private void initTabs() {

        //banlap: 显示下方菜单栏 菜单图标
        String icon_main_text = getString(R.string.main_icon_main);
        String icon_smart_text = getString(R.string.main_icon_smart);
        String icon_user_text = getString(R.string.main_icon_user);
        String[] tab_titles = new String[]{icon_main_text, icon_smart_text, icon_user_text};

        for (int i = 0; i < tab_titles.length; i++) {
            TabLayout.Tab tab = getViewDataBind().mainTab.newTab();
            View view = LayoutInflater.from(this).inflate(R.layout.item_main_menu, null);
            tab.setCustomView(view);

            TextView tvTitle = view.findViewById(R.id.item_main_menu_name);
            tvTitle.setText(tab_titles[i]);
            ImageView imgTab = view.findViewById(R.id.item_main_menu_icon);
            imgTab.setImageResource(tab_icons[i]);
            getViewDataBind().mainTab.addTab(tab);
        }

        getViewDataBind().mainTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment.getTag() != null) {
                        if (Integer.parseInt(fragment.getTag()) == tab.getPosition()) {
                            showAndHideFragmentByTag(fragment.getTag());
                        }
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void showAndHideFragmentByTag(String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment.getTag() != null && fragment.getTag().equals(tag)) {
                transaction.show(fragment);
                TabLayout.Tab tab = getViewDataBind().mainTab.getTabAt(Integer.parseInt(tag));
                getViewDataBind().mainTab.selectTab(tab);
            } else {
                transaction.hide(fragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    public void goRefreshRegion() {
        getViewModel().getNewRegion();
        getViewModel().getDefaultMac();
    }

    public void goRefreshEquip() {
        getViewModel().getUserAllEquip();
    }

    private void goCheckPermission() {

        if (VcomSingleton.getInstance().isUserLogin()) {
            return;
        }

        if (!isLoginSuccess.get()) {
            return;
        }

        VcomSingleton.getInstance().isBleReady.set(Util.isBleOpen());
//        VcomSingleton.getInstance().isGpsReady.set(Util.isGpsOPen(this));
//        VcomSingleton.getInstance().isLocationReady.set(Util.isLocationPermissionReady(VcomApp.getApp()));

        boolean isBleReady = VcomSingleton.getInstance().isBleReady.get();
//        boolean isGpsReady = VcomSingleton.getInstance().isGpsReady.get();
//        boolean isPermissionReady = VcomSingleton.getInstance().isLocationReady.get();
//        if (!isBleReady || !isGpsReady || !isPermissionReady) {
//            showRequestPermissionDialog();
//        }
        if (!isBleReady) {
            showRequestPermissionDialog();
        } else {
            Util.openBle(this);
            //banlap: 发现bug 使用下面自动连接方法会出现首次登录成功后报错bug，禁用后目前没任何影响
            //new Handler().postDelayed(() -> getViewModel().startAutoConnect(), 300);
        }
    }

    private void showRequestPermissionDialog() {
        if (!isForeground) {
            return;
        }

        if (request == null) {
            request = new RequestPermissionDialog();
        }
        if (request.isVisible()) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!request.isAdded()) {
            transaction.add(request, "8");
        }
        transaction.show(request);
        transaction.commitAllowingStateLoss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.msgCode) {
            case MessageEvent.permissionCode:
                if (request != null) {
                    request.refreshParams();
                }
//                if (VcomSingleton.getInstance().isBleReady.get() && VcomSingleton.getInstance().isGpsReady.get()
//                        && VcomSingleton.getInstance().isLocationReady.get()) {
                if (VcomSingleton.getInstance().isBleReady.get() ) {
                    if (request != null && request.isVisible()) {
                        request.dismissAllowingStateLoss();
                    }
                } else {
                    showRequestPermissionDialog();
                }
                break;
            case MessageEvent.refreshRegion:
                goRefreshRegion();
                break;
            case MessageEvent.switchFragment:
                showAndHideFragmentByTag(event.msg);
                break;
            case MessageEvent.refreshEquip:
                goRefreshEquip();
                break;
            case MessageEvent.equipReady:
                new Handler().postDelayed(() -> getViewModel().startAutoConnect(), 300);
                break;
        }
    }

    @Override
    public void validateFailure() {
        /*
         * banlap: 调试 无网络无账号登录主界面（之后需要恢复原样）
         * */
        Toast.makeText(getApplication(),getString(R.string.toast_authentication_error),Toast.LENGTH_SHORT).show();

        isLoginSuccess.set(false);
        Intent goLogin = new Intent(this, LoginActivity.class);
        startActivity(goLogin);
        ActivityUtil.finishAllActivity();

        //调试
       /* isLoginSuccess.set(true);
        goCheckPermission();
        getViewModel().getProductInfo();
        //getViewModel().getAllDevType();
        getViewModel().getDefaultMac();
        getViewModel().getUserAllEquip();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.loginSuccess));*/
    }

    @Override
    public void validateSuccess() {
        Toast.makeText(getApplication(),getString(R.string.toast_authentication_success),Toast.LENGTH_SHORT).show();
        isLoginSuccess.set(true);
        goCheckPermission();
        getViewModel().getProductInfo();
        //getViewModel().getAllDevType();
        getViewModel().getDefaultMac();
        getViewModel().getUserAllEquip();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.loginSuccess));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}