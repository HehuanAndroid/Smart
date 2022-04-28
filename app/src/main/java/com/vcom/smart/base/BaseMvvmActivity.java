package com.vcom.smart.base;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;

public abstract class BaseMvvmActivity<VM extends AndroidViewModel, VDB extends ViewDataBinding> extends BaseActivity<VDB> {

    protected VM mViewModel;

    @SuppressWarnings("unchecked")
    @Override
    protected void afterInit() {
        Class<VM> vmClass = (Class<VM>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        mViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(vmClass);
    }

    public VM getViewModel() {
        return mViewModel;
    }

}
