package com.shake.easystore.http;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.shake.easystore.LoginActivity;
import com.shake.easystore.utils.UserLocalData;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import dmax.dialog.SpotsDialog;

/**
 * Created by shake on 17-5-4.
 * 这是能显示 loading 对话框的Callback
 * <p>
 * 修改记录 ： 添加实现父类的 onTokenError 方法
 */
public abstract class SpotsCallBack<T> extends BaseCallback<T> {

    private Context mContext;

    //loading对话框
    private SpotsDialog mDialog;

    public SpotsCallBack(Context context) {
        this.mContext = context;
        initSpotsDialog();
    }

    /**
     * 初始化loading对话框
     */
    private void initSpotsDialog() {
        mDialog = new SpotsDialog(mContext, "拼命加载中...");
    }

    /**
     * 显示对话框
     */
    public void showDialog() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    /**
     * 销毁对话框
     */
    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    /**
     * 在请求网络之前显示对话框
     *
     * @param request
     */
    @Override
    public void onRequestBefore(Request request) {
        showDialog();
    }

    /**
     * 在请求失败的时候关闭对话框
     *
     * @param request
     * @param e
     */
    @Override
    public void onFailure(Request request, IOException e) {
        dismissDialog();
    }

    /**
     * 在请求有响应的时候关闭对话框
     *
     * @param response
     */
    @Override
    public void onResponse(Response response) {
        dismissDialog();
    }

    /**
     * 当登录失效的时候，就会回调这个方法。即会重新跳转到 LoginActivity
     *
     * @param response
     * @param code
     */
    @Override
    public void onTokenError(Response response, int code) {
        //提示
        Toast.makeText(mContext, "登录失效，请重新登录", Toast.LENGTH_SHORT).show();

        //重新回到登录页面
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);

        //清除用户数据
        UserLocalData.clearUserAndToken();
    }
}
