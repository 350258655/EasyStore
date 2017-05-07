package com.shake.easystore.http;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import dmax.dialog.SpotsDialog;

/**
 * Created by shake on 17-5-4.
 * 这是能显示 loading 对话框的Callback
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

}
