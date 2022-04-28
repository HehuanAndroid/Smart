package com.vcom.smart.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vcom.smart.model.DefaultMac;
import com.vcom.smart.ui.MainActivity;

import java.lang.reflect.ParameterizedType;

public abstract class BaseMvvmFragment<VM extends ViewModel, VDB extends ViewDataBinding> extends Fragment {

    protected VDB mViewDataBind;
    protected VM mViewModel;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDataBind = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mViewDataBind.setLifecycleOwner(this);

        Class<VM> vmClass = (Class<VM>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        ViewModelProvider provider = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()));
        mViewModel = provider.get(vmClass);

        initDatum();
        initViews();
        DefaultMac defaultMac=new DefaultMac();
        try {
            defaultMac.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return mViewDataBind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        afterCreate();

    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void afterCreate();

    protected abstract void initViews();

    protected abstract void initDatum();

    public VDB getViewDataBind() {
        return mViewDataBind;
    }

    public VM getViewModel() {
        return mViewModel;
    }
}
