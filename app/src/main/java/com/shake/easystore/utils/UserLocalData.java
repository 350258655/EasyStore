package com.shake.easystore.utils;

import android.content.Context;
import android.text.TextUtils;

import com.shake.easystore.Contants;
import com.shake.easystore.bean.User;

/**
 * Created by shake on 2017/5/13 0013.
 * 获取和保存用户数据的工具类
 */
public class UserLocalData {

    private static UserLocalData mInstance;

    private static Context mContext;

    private UserLocalData(Context context) {
        mContext = context;
    }

    /**
     * 初始化方法，创建UserLocalData对象
     *
     * @param context
     */
    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new UserLocalData(context.getApplicationContext());
        }
    }

    /**
     * 存User
     *
     * @param user
     */
    public static void putUser(User user) {
        String user_json = JSONUtil.toJSON(user);
        PreferencesUtils.putString(mContext, Contants.USER_JSON, user_json);
    }


    /**
     * 存Token
     *
     * @param token
     */
    public static void putToken(String token) {
        PreferencesUtils.putString(mContext, Contants.TOKEN, token);
    }


    /**
     * 获取用户
     *
     * @return
     */
    public static User getUser() {
        String user_json = PreferencesUtils.getString(mContext, Contants.USER_JSON);
        if (!TextUtils.isEmpty(user_json)) {
            return JSONUtil.fromJson(user_json, User.class);
        }
        return null;
    }


    /**
     * 获取Token
     *
     * @return
     */
    public static String getToken() {
        return PreferencesUtils.getString(mContext, Contants.TOKEN);
    }


    /**
     * 清除用户数据
     */
    public static void clearUser() {
        PreferencesUtils.putString(mContext, Contants.USER_JSON, "");
    }

    /**
     * 清除Token
     */
    public static void clearToken() {
        PreferencesUtils.putString(mContext, Contants.TOKEN, "");
    }


    /**
     * 存用户和Token
     *
     * @param user
     * @param token
     */
    public static void putUserAndToken(User user, String token) {
        putUser(user);
        putToken(token);
    }


    /**
     * 清除用户和Token
     *
     */
    public static void clearUserAndToken() {
        clearUser();
        clearToken();
    }


}
