package com.vcom.smart.ui;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseActivity;
import com.vcom.smart.databinding.ActivityCustomErrorBinding;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class CustomErrorActivity extends BaseActivity<ActivityCustomErrorBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_error;
    }

    @Override
    protected void afterInit() {

    }

    @Override
    protected void initViews() {

        getViewDataBind().errorDetails.setText(CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));

        CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        if (config == null) {
            finish();
            return;
        }

        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            getViewDataBind().restartButton.setOnClickListener(v -> CustomActivityOnCrash.restartApplication(CustomErrorActivity.this, config));
        } else {
            getViewDataBind().restartButton.setOnClickListener(v -> CustomActivityOnCrash.closeApplication(CustomErrorActivity.this, config));
        }
    }

    @Override
    protected void initDatum() {

    }
}