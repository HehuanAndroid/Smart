package com.vcom.smart.ui;

import android.view.View;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivityAppHelpBinding;
import com.vcom.smart.uivm.AppHelpVM;

/**
 * @author Banlap on 2021/5/8
 */
public class AppHelpActivity extends BaseMvvmActivity<AppHelpVM, ActivityAppHelpBinding>
        implements AppHelpVM.AppHelpCallBack {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_app_help;
    }

    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);

        boolean isAdvance = getIntent().getBooleanExtra("Advance", false);
        if(isAdvance) {
            getViewDataBind().llShowMoreQuestion.setVisibility(View.VISIBLE);
        } else {
            getViewDataBind().llShowMoreQuestion.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initDatum() {

    }

    @Override
    public void viewBack() {
        finish();
    }
}
