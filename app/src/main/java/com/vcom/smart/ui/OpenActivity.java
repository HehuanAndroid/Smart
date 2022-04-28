package com.vcom.smart.ui;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseActivity;
import com.vcom.smart.databinding.ActivityOpenBinding;
import com.vcom.smart.utils.SPUtil;

public class OpenActivity extends BaseActivity<ActivityOpenBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open;
    }

    @Override
    protected void afterInit() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initDatum() {
        new Handler().postDelayed(() -> {

            if (TextUtils.isEmpty(SPUtil.getValue(this, "user_param"))) {
                Intent goLogin = new Intent(this, LoginActivity.class);
                startActivity(goLogin);
            } else {
                Intent goMain = new Intent(this, MainActivity.class);
                startActivity(goMain);
            }
            finish();
        }, 1000);

    }
}