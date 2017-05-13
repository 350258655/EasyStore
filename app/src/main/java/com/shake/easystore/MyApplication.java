package com.shake.easystore;

import android.app.Application;

import com.shake.easystore.utils.IntentUtils;
import com.shake.easystore.utils.UserLocalData;

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
    }
}
