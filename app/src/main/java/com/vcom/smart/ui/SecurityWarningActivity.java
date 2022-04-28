package com.vcom.smart.ui;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivitySecurityWarningBinding;
import com.vcom.smart.uivm.SecurityWarningVM;

/**
 * @author Banlap on 2021/1/19
 */
public class SecurityWarningActivity extends BaseMvvmActivity<SecurityWarningVM, ActivitySecurityWarningBinding>
        implements SecurityWarningVM.SecurityWarningVMCallBack {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_security_warning;
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
