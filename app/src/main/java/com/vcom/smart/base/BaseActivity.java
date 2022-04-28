package com.vcom.smart.base;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.vcom.smart.utils.ActivityUtil;

/**
 * @Author Lzz
 * @Date 2020/10/27 16:12
 */
public abstract class BaseActivity<VDB extends ViewDataBinding> extends AppCompatActivity {

    protected VDB mViewDataBind;

    protected boolean isForeground = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        isForeground = true;
        ActivityUtil.addActivity(this);

        mViewDataBind = DataBindingUtil.setContentView(this, getLayoutId());
        mViewDataBind.setLifecycleOwner(this);
        afterInit();

        initDatum();
        initViews();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.removeActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @LayoutRes
    protected abstract int getLayoutId();

    public VDB getViewDataBind() {
        return mViewDataBind;
    }

    protected abstract void afterInit();

    protected abstract void initViews();

    protected abstract void initDatum();

}
