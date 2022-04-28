package com.vcom.smart.ui;

import android.content.Intent;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivityAddSafeBoxBinding;
import com.vcom.smart.uivm.DeviceSafeBoxVM;

public class DeviceSafeBoxActivity extends BaseMvvmActivity<DeviceSafeBoxVM, ActivityAddSafeBoxBinding>
        implements DeviceSafeBoxVM.DeviceSafeBoxVMCallBack {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_safe_box;
    }

    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);
    }

    @Override
    protected void initDatum() {

    }

    @Override
    public void saveData() {
       finish();
    }

    @Override
    public void viewBack() {
        finish();
    }
}
