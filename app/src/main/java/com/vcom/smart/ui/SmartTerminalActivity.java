package com.vcom.smart.ui;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseBindingAdapter;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivitySmartTerminalBinding;
import com.vcom.smart.databinding.ItemSafeBoxDeviceRouteBinding;
import com.vcom.smart.model.SafeBox;
import com.vcom.smart.model.SafeBoxElectric;
import com.vcom.smart.uivm.SmartTerminalVM;

import java.util.ArrayList;
import java.util.List;

public class SmartTerminalActivity extends BaseMvvmActivity<SmartTerminalVM, ActivitySmartTerminalBinding>
        implements SmartTerminalVM.SmartTerminalVMCallBack {

    private SafeBoxDeviceRouteAdapter safeBoxDeviceRouteAdapter;
    private final List<SafeBoxElectric> safeBoxElectrics = new ArrayList<>();   //安全电箱的路线list
    @Override
    protected int getLayoutId() {
        return R.layout.activity_smart_terminal;
    }

    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);
        //banlap: 智能控开的线路点击开关
        getViewDataBind().tvSmartTerminalDetailRouteSwitch.setOnClickListener(v->{
            getViewDataBind().tvSmartTerminalDetailRouteSwitch.setBackgroundResource(R.drawable.shape_round_yellow);
            getViewDataBind().tvSmartTerminalDetailRouteSwitch.setText("开");
        });
    }

    @Override
    protected void initDatum() {
        safeBoxDeviceRouteAdapter = new SafeBoxDeviceRouteAdapter(this);
        getViewDataBind().rvSafeBoxDeviceRouteList.setLayoutManager(new GridLayoutManager(this,2, RecyclerView.HORIZONTAL,false));
        getViewDataBind().rvSafeBoxDeviceRouteList.setAdapter(safeBoxDeviceRouteAdapter);

        for(int i=0; i<5; i++) {
            SafeBoxElectric safeBoxElectric = new SafeBoxElectric();
            safeBoxElectric.setDeviceName("电箱1");
            safeBoxElectric.setDeviceRoute("线路"+i);
            safeBoxElectrics.add(safeBoxElectric);
        }
        safeBoxDeviceRouteAdapter.setItems(safeBoxElectrics);
    }

    @Override
    public void viewBack() {
        finish();
    }

    private class SafeBoxDeviceRouteAdapter extends BaseBindingAdapter<SafeBoxElectric, ItemSafeBoxDeviceRouteBinding>{

        public SafeBoxDeviceRouteAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected int getLayoutId(int layoutId) {
            return R.layout.item_safe_box_device_route;
        }

        @Override
        protected void onBindItem(ItemSafeBoxDeviceRouteBinding itemSafeBoxDeviceRouteBinding, SafeBoxElectric item, int i) {

            itemSafeBoxDeviceRouteBinding.tvSafeBoxDeviceRoute.setText(item.getDeviceRoute());
            //banlap: 电箱中选择不同线路
            itemSafeBoxDeviceRouteBinding.clSafeBoxDeviceRoute.setOnClickListener(v->{
                itemSafeBoxDeviceRouteBinding.ivSafeBoxDeviceRouteSelect.setBackgroundResource(R.drawable.ic_select_yes);
            });


        }
    }
}
