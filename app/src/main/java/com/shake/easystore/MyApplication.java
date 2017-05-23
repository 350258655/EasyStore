package com.shake.easystore;

import android.app.Application;

import com.shake.easystore.utils.IntentUtils;
import com.shake.easystore.utils.StaticDataUtils;
import com.shake.easystore.utils.UserLocalData;

import cn.smssdk.SMSSDK;

/**
 * Created by shake on 2017/5/13 0013.
 * 入口Application
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化用户数据工具类
        UserLocalData.init(this);

        //存储Intent的工具类
        IntentUtils.init();

        //静态变量工具类
        StaticDataUtils.init();

        //初始化短信SDK
        SMSSDK.initSDK(this, "1dd61e48a0200", "08e4a8e566ca77442e4aaa70d9b3a861");
    }
}
