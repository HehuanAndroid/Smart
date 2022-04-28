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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;

/**
 * @Author Lzz
 * @Date 2020/10/28 13:52
 */
public abstract class BaseMvvmDialogFragment<VM extends AndroidViewModel, VDB extends ViewDataBinding> extends DialogFragment {

    protected VM mViewModel;
    protected VDB mViewDataBinding;

    public BaseMvvmDialogFragment() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mViewDataBinding.setLifecycleOwner(this);

        Class<VM> vmClass = (Class<VM>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        mViewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(vmClass);

        getDialog().setCancelable(false);

        initDatum();
        initViews();

        return mViewDataBinding.getRoot();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initDatum();

    protected abstract void initViews();

    public VM getViewModel() {
        return mViewModel;
    }

    public VDB getViewDataBinding() {
        return mViewDataBinding;
    }

}
