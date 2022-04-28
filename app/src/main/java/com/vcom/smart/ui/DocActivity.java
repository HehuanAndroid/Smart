package com.vcom.smart.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vcom.smart.R;
import com.vcom.smart.VcomApp;
import com.vcom.smart.base.BaseActivity;
import com.vcom.smart.databinding.ActivityDocBinding;

import java.util.Locale;

public class DocActivity extends BaseActivity<ActivityDocBinding> {

    private final static String USER_AGREEMENT = "foreign/UserAgreement";
    private final static String PRIVATE_AGREEMENT = "foreign/PrivacyPolicy";
    private final static String HELP = "foreign/HelpCenter";
    private final static String ABOUT = "foreign/AboutUs";

    private final static String USER_AGREEMENT_EN = "foreign/UserAgreementForEN";
    private final static String PRIVATE_AGREEMENT_EN = "foreign/PrivacyPolicyForEN";
    private final static String HELP_EN = "foreign/HelpCenterForEN";
    private final static String ABOUT_EN = "foreign/AboutUsForEN";


    private String loadUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_doc;
    }

    @Override
    protected void afterInit() {

    }

    @Override
    protected void initViews() {
        webViewSetting();
        Log.e("test", loadUrl);
        getViewDataBind().docWebView.loadUrl(loadUrl);
        getViewDataBind().ivViewBack.setOnClickListener(v->{ finish();});
    }

    @Override
    protected void initDatum() {

        if (getIntent().getExtras() == null) {
            finish();
        }

        String tag = getIntent().getStringExtra("webTag");
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        //banlap: 获取当前系统语言;
        String localeLanguage = Locale.getDefault().getLanguage();
        switch (tag) {
            case "user":
                getViewDataBind().tvUserTitle.setText(getString(R.string.user_user_agreement));
                //banlap: 切换语言时, 变更链接
                if (localeLanguage.equals("en")) {
                    loadUrl = VcomApp.BASE_URL + USER_AGREEMENT_EN;
                } else {
                    loadUrl = VcomApp.BASE_URL + USER_AGREEMENT;
                }
                break;
            case "private":
                getViewDataBind().tvUserTitle.setText(getString(R.string.user_privacy_policy));
                //banlap: 切换语言时, 变更链接
                if (localeLanguage.equals("en")) {
                    loadUrl = VcomApp.BASE_URL + PRIVATE_AGREEMENT_EN;
                } else {
                    loadUrl = VcomApp.BASE_URL + PRIVATE_AGREEMENT;
                }
                break;
            case "help":
                getViewDataBind().tvUserTitle.setText(getString(R.string.user_help_center));
                //banlap: 切换语言时, 变更链接
                if (localeLanguage.equals("en")) {
                    loadUrl = VcomApp.BASE_URL + HELP_EN;
                } else {
                    loadUrl = VcomApp.BASE_URL + HELP;
                }
                break;
            case "about":
                getViewDataBind().tvUserTitle.setText(getString(R.string.user_about_vcom));
                //banlap: 切换语言时, 变更链接
                if (localeLanguage.equals("en")) {
                    loadUrl = VcomApp.BASE_URL + ABOUT_EN;
                } else {
                    loadUrl = VcomApp.BASE_URL + ABOUT;
                }
                break;
        }

       // Log.v("*************************GetURL","URL="+VcomApp.BASE_URL);
        System.out.println("gggggggggggggggggggggg:"+loadUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getViewDataBind().docWebView.clearCache(true);
        getViewDataBind().docWebView.clearFormData();
        getViewDataBind().docWebView.clearMatches();
        getViewDataBind().docWebView.clearSslPreferences();
        getViewDataBind().docWebView.clearDisappearingChildren();
        getViewDataBind().docWebView.clearHistory();
        getViewDataBind().docWebView.clearAnimation();
        getViewDataBind().docWebView.loadUrl("about:blank");
        getViewDataBind().docWebView.removeAllViews();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void webViewSetting() {

        final WebSettings webSettings = getViewDataBind().docWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);  //不使用缓存，只从网络获取数据
        webSettings.setDomStorageEnabled(true);      //开启 DOM storage API 功能
        webSettings.setUseWideViewPort(true);        //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);   //缩放至屏幕的大小
        webSettings.setSupportZoom(true);            //支持缩放，默认为true。是下面那个的前提
        webSettings.setBuiltInZoomControls(true);    //设置内置的缩放控件
        webSettings.setDefaultFontSize(32);          //设置 WebView 字体的大小，默认大小为 16
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        getViewDataBind().docWebView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        getViewDataBind().pbProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        getViewDataBind().pbProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        // 注意：super句话一定要删除，或者注释掉，否则又走handler.cancel()默认的不支持https的了。
                        // super.onReceivedSslError(view, handler, error);
                        // handler.cancel(); // Android默认的处理方式
                        // handler.handleMessage(Message msg); // 进行其他处理
                        handler.proceed(); // 接受所有网站的证书
                        if (error.getPrimaryError() == SslError.SSL_INVALID) {// 校验过程遇到了bug
                            handler.proceed();
                        } else {
                            handler.cancel();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                        }
                    }
                });

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//允许js 弹窗
        getViewDataBind().docWebView.addJavascriptInterface(this, "H5Interface");
    }

}