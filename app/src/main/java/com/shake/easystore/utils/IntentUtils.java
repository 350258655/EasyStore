package com.shake.easystore.utils;

import android.content.Intent;

/**
 * Created by shake on 2017/5/13 0013.
 * 存取Intent的工具类
 */
public class IntentUtils {

    private static IntentUtils mInstance;
    //需要存储的Intent全局变量
    private static Intent sIntent;


    private IntentUtils() {

    }

    public static void init() {
        if (mInstance == null) {
            mInstance = new IntentUtils();
        }
    }

    /**
     * 存储Intent
     *
     * @param intent
     */
    public static void putIntent(Intent intent) {
        sIntent = intent;
    }

    /**
     * 获取Intent
     *
     * @return
     */
    public static Intent getIntent() {
        if (sIntent != null) {
            return sIntent;
        }
        return null;
    }


    /**
     * 清除Intent
     */
    public static void clearIntent() {
        sIntent = null;
    }


}
