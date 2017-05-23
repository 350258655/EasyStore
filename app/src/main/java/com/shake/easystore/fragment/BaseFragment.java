package com.shake.easystore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.shake.easystore.LoginActivity;
import com.shake.easystore.bean.User;
import com.shake.easystore.utils.IntentUtils;
import com.shake.easystore.utils.UserLocalData;

/**
 * Created by shake on 2017/5/13 0013.
 * 这是Fragment的基类
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, container, savedInstanceState);
        ViewUtils.inject(this, view);
        //初始化的操作放到这里来
        init();

        return view;
    }


    /**
     * 提供给子类去实现的初始化方法
     */
    protected abstract void init();


    /**
     * 创建View的抽象方法，需要子类去实现
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    /**
     * 经过重载的startActivity方法
     *
     * @param intent
     * @param isNeedLogin
     */
    public void startActivity(Intent intent, boolean isNeedLogin) {

        //假如需要登录
        if (isNeedLogin) {
            User user = UserLocalData.getUser();
            //假如user不为空，证明当前已经是登录状态
            if (user != null) {
                super.startActivity(intent);
            } else {
                //当前是未登录状态，需要先跳转到登录界面

                //先保存当前的Intent,记录它是要跳转到哪个Activity
                IntentUtils.putIntent(intent);
                //再跳转到LoginActivity
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                super.startActivity(loginIntent);
            }
        } else {
            //假如不需要先登录，那么直接调用父类的跳转方法
            super.startActivity(intent);
        }


    }


    /**
     * 经过重载的startActivityForResult方法
     *
     * @param intent
     * @param isNeedLogin
     * @param requestCode
     */
    public void startActivityForResult(Intent intent, boolean isNeedLogin, int requestCode) {
        //假如需要登录
        if (isNeedLogin) {
            User user = UserLocalData.getUser();
            //假如user不为空，证明当前已经是登录状态
            if (user != null) {
                super.startActivityForResult(intent, requestCode);
            } else {
                //当前是未登录状态，需要先跳转到登录界面

                //先保存当前的Intent,记录它是要跳转到哪个Activity
                IntentUtils.putIntent(intent);
                //再跳转到LoginActivity
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                super.startActivityForResult(loginIntent, requestCode);
            }
        } else {
            //假如不需要先登录，那么直接调用父类的跳转方法
            super.startActivityForResult(intent, requestCode);
        }

    }
}
