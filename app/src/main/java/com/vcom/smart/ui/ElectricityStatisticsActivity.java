package com.vcom.smart.ui;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivityElectricityStatisticsBinding;
import com.vcom.smart.uivm.ElectricityStatisticsVM;

/**
 * @author Banlap on 2021/1/19
 */
public class ElectricityStatisticsActivity extends BaseMvvmActivity<ElectricityStatisticsVM, ActivityElectricityStatisticsBinding>
        implements ElectricityStatisticsVM.ElectricityStatisticsCallBack {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_electricity_statistics;
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
    public void viewBack() {
        finish();
    }
}
