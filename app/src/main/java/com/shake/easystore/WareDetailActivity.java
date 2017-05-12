package com.shake.easystore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.bean.Wares;
import com.shake.easystore.utils.CartProvider;
import com.shake.easystore.weiget.ShopToolbar;

import java.io.Serializable;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import dmax.dialog.SpotsDialog;

public class WareDetailActivity extends Activity {


    @ViewInject(R.id.webView)
    private WebView mWebView;

    @ViewInject(R.id.toolbar)
    private ShopToolbar mToolbar;

    //加载对话框
    private SpotsDialog mSpotsDialog;

    //商品的实体类
    private Wares mWare;

    //商品存储的工具类
    private CartProvider mCartProvider;

    //native和web交互的桥梁
    private WebAppInterface mAppInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ware_detail);

        ViewUtils.inject(this);

        //初始化ToolBar
        initToolBar();

        //显示对话框
        mSpotsDialog = new SpotsDialog(this);
        mSpotsDialog.show();

        //从natite那里获取数据
        Serializable serializable = getIntent().getSerializableExtra(Contants.WARE);
        if (serializable == null) {
            finish();
        } else {
            mWare = (Wares) serializable;
        }

        //获取存储工具类
        mCartProvider = CartProvider.getInstance(this);

        //初始化WebView
        initWebView();
    }


    /**
     * 初始化WebView
     */
    private void initWebView() {

        //初始化WebSetting的相关操作
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setAppCacheEnabled(true);

        //WebView设置URL
        mWebView.loadUrl(Contants.API.WARES_DETAIL);

        //创建交互接口并添加
        mAppInterface = new WebAppInterface(this);
        mWebView.addJavascriptInterface(mAppInterface, "appInterface");

        //设置WebViewClient
        mWebView.setWebViewClient(new WC());

    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {

        //点击回退按钮
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置分享
        mToolbar.setRightButtonText("分享");
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示分享
                showShare();
            }
        });

    }

    /**
     * 显示分享
     */
    private void showShare() {
        ShareSDK.initSDK(this);


        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享");

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.hodogame.com/");

        // text是分享文本，所有平台都需要这个字段
        oks.setText(mWare.getName());

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(mWare.getImgUrl());

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.hodogame.com/");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mWare.getName());

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.hodogame.com/");

        // 启动分享GUI
        oks.show(this);
    }


    /**
     * 停止分享
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }

    public class WC extends WebViewClient{

        /**
         * 当页面加载完成的时候。显示详情，并且让加载框消失
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if(mSpotsDialog != null && mSpotsDialog.isShowing()){
                //让加载框消失
                mSpotsDialog.dismiss();
            }

            //显示详情，因为显示详情要在确保网页加载完成之后调用
            mAppInterface.showDetail();

        }
    }



    /**
     * native和web交互的接口
     */
    class WebAppInterface {

        private Context mContext;

        public WebAppInterface(Context context) {
            this.mContext = context;
        }


        /**
         * 安卓调用Web的方法
         */
        @JavascriptInterface
        public void showDetail() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:showDetail(" + mWare.getId() + ")");
                }
            });
        }


        /**
         * Web调安卓的方法，立即购买
         *
         * @param id
         */
        @JavascriptInterface
        public void buy(long id) {
            //将数据存储起来
            mCartProvider.put(mWare);
            Toast.makeText(mContext, "已添加到购物车!", Toast.LENGTH_SHORT).show();
        }


        /**
         * 添加到收藏
         *
         * @param id
         */
        @JavascriptInterface
        public void addFavorites(long id) {

        }


    }


}
