package com.vcom.smart.fragment;

import android.content.Intent;
import android.widget.Toast;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseMvvmFragment;
import com.vcom.smart.databinding.FragmentMeBinding;
import com.vcom.smart.fvm.MeFVM;
import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.ui.AppHelpActivity;
import com.vcom.smart.ui.DocActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

/**
 * @Author Lzz
 * @Date 2020/10/27 18:44
 */
public class MeFragment extends BaseMvvmFragment<MeFVM, FragmentMeBinding> implements MeFVM.MeFvmCallBack {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void afterCreate() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initDatum() {
        getViewDataBind().setVm(getViewModel());
        EventBus.getDefault().register(this);
        getViewModel().setCallBack(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VcomSingleton.getInstance().getLoginUser().isEmpty()){
            return;
        }
        getViewDataBind().mainFragmentUserName.setText(VcomSingleton.getInstance().getLoginUser().getUserName());
        getViewDataBind().mainFragmentUserId.setText(String.format(getString(R.string.user_id), VcomSingleton.getInstance().getLoginUser().getUserId()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.msgCode == MessageEvent.loginSuccess) {
            getViewDataBind().mainFragmentUserName.setText(VcomSingleton.getInstance().getLoginUser().getUserName());
            getViewDataBind().mainFragmentUserId.setText(String.format(getString(R.string.user_id), VcomSingleton.getInstance().getLoginUser().getUserId()));
        }
    }

    @Override
    public void viewGoWeb(String tag) {
        Intent intent = new Intent(getActivity(), DocActivity.class);
        intent.putExtra("webTag", tag);
        startActivity(intent);
    }

    @Override
    public void viewGoAppHelp(){
        Intent intent = new Intent(getActivity(), AppHelpActivity.class);
        intent.putExtra("Advance", true);
        startActivity(intent);
    }

}
