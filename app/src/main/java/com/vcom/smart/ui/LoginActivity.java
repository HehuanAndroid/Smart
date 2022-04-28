package com.vcom.smart.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.LocaleList;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivityLoginBinding;
import com.vcom.smart.model.User;
import com.vcom.smart.uivm.LoginVM;
import com.vcom.smart.utils.GsonUtil;
import com.vcom.smart.utils.SPUtil;

import java.util.Locale;

public class LoginActivity extends BaseMvvmActivity<LoginVM, ActivityLoginBinding> implements LoginVM.LoginVmCallBack {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);
        if (!TextUtils.isEmpty(SPUtil.getValue(this, "user_param"))) {
            User user = GsonUtil.getInstance().json2Bean(SPUtil.getValue(this, "user_param"), User.class);
            getViewModel().setUserName(user.getUserName());
        }

        //banlap: 底部文本部分颜色调整
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.green));
        ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.green));

        SpannableStringBuilder builder = new SpannableStringBuilder(getViewDataBind().tvUserDocument2.getText().toString());
        SpannableStringBuilder builder2 = new SpannableStringBuilder(getViewDataBind().tvUserDocument4.getText().toString());

        ClickableSpan userAgreementClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showTreaty("user");
            }
        };
        ClickableSpan privacyPolicyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showTreaty("private");
            }
        };

        int indexUserAgreement = getString(R.string.login_agreement_title_2).indexOf(getString(R.string.login_agreement_title_2));
        int indexPrivacyPolicy = getString(R.string.login_agreement_title_4).indexOf(getString(R.string.login_agreement_title_4));

        //banlap: 设置span到指定文本中
        builder.setSpan(greenSpan, indexUserAgreement, getString(R.string.login_agreement_title_2).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder2.setSpan(greenSpan2,indexPrivacyPolicy, getString(R.string.login_agreement_title_4).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setSpan(userAgreementClickableSpan, indexUserAgreement, getString(R.string.login_agreement_title_2).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder2.setSpan(privacyPolicyClickableSpan,indexPrivacyPolicy, getString(R.string.login_agreement_title_4).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        getViewDataBind().tvUserDocument2.setText(builder);
        getViewDataBind().tvUserDocument4.setText(builder2);
        //banlap: 配置到tvUserDocument中 (这个在setSpan()之后设置)
        getViewDataBind().tvUserDocument2.setMovementMethod(LinkMovementMethod.getInstance());
        getViewDataBind().tvUserDocument4.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    protected void initDatum() {
        changeLanguage();
    }

    @Override
    public void startLogin() {
        getViewDataBind().loginCommit.setClickable(false);
        getViewDataBind().loginCommit.setBackgroundResource(R.drawable.shape_radius_gray);
        getViewDataBind().loginCommit.setTextColor(Color.GRAY);

    }

    @Override
    public void loginSuccess() {
        Toast.makeText(getApplication(),getString(R.string.toast_login_success),Toast.LENGTH_SHORT).show();
        Intent goMain = new Intent(this, MainActivity.class);
        startActivity(goMain);
        //banlap: 当在app退出账号重新登录后，点击返回键不再返回到登录界面
        finish();
    }

    @Override
    public void loginFailure() {
        Toast.makeText(getApplication(),getString(R.string.toast_login_error),Toast.LENGTH_SHORT).show();
        getViewDataBind().loginCommit.setClickable(true);
        getViewDataBind().loginCommit.setBackgroundResource(R.drawable.shape_radius_green);
        getViewDataBind().loginCommit.setTextColor(Color.WHITE);
    }

    /*
     * banlap: 跳转用户协议和隐私政策 （当前代码不使用）
     * */
    @Override
    public void showTreaty(String tag){
        Intent intent = new Intent(this, DocActivity.class);
        intent.putExtra("webTag", tag);
        startActivity(intent);
    }


    /*
    * banlap: 语言设置 跟随系统语言
    * */
    public void changeLanguage(){
        //String localeLanguage = Locale.getDefault().getLanguage();      //获取当前系统语言;
        //Toast.makeText(this, localeLanguage, Toast.LENGTH_SHORT).show();

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //banlap: 系统语言首选项语言
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        //banlap: 设置成系统语言
        config.setLocale(locale);
    }

    @Override
    public void goAppHelp() {
        Intent intent = new Intent(this, AppHelpActivity.class);
        intent.putExtra("Advance", false);
        startActivity(intent);
    }

    //banlap: 输入框点击空白处收回键盘 处理触摸事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
        View v = getCurrentFocus();
        if (isShouldHideInput(v, ev)) {
            hideSoftInput(v.getWindowToken());
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}