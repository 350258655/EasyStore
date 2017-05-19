package com.shake.easystore.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.shake.easystore.utils.UserLocalData;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by shake on 17-5-4.
 * OkHttp的封装类
 * OkHttp本身的请求过程是：
 * 1、获取OkHttpClient对象(一般以单例形式)；
 * 2、构建Request对象
 * 3、以Request对象作为参数，调用OkHttpClient对象的请求网络的方法。
 * <p>
 * 封装的思想：
 * 1、在OkHttpHelper的构造方法中去实例化 OkHttpClient，当然，这个OkHttpHelper也是一个单例
 * 2、对外提供get、和post方法。当外面请求数据的时候，首先会根据get和post去创建对应不同的Reuqest对象
 * 3、获取Request对象之后，就开始去请求数据。根据外部传进来的回调接口，会对应在不同时间段去调用这些接口的方法
 * <p>
 * 修改记录 ： 在请求数据的时候，加上一个Token。登录的时候需要用到。假如是post，那么直接加在要post的数据里面；假如是
 * get，那么就拼接在URL中
 * 在响应结果的时候，当请求数据返回的请求码是401，402，403的时候，回调onTokenError方法
 */
public class OkHttpHelper {

    public static final int TOKEN_MISSING = 401;// token 丢失
    public static final int TOKEN_ERROR = 402; // token 错误
    public static final int TOKEN_EXPIRE = 403; // token 过期

    //封装类实例
    private static OkHttpHelper mInstance;

    static {
        mInstance = new OkHttpHelper();
    }

    // OkHttpClient实例
    private static OkHttpClient okHttpClient;

    private Gson mGson;

    private static Handler mHandler;


    private OkHttpHelper() {
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);

        mGson = new Gson();
        mHandler = new Handler(Looper.myLooper());

    }


    public static OkHttpHelper getInstance() {
        return mInstance;
    }

    /**
     * 对外提供的get方法，这是不需要拼接参数的接口
     *
     * @param url
     * @param callback
     */
    public void get(String url, BaseCallback callback) {
        get(url, null, callback);
    }


    /**
     * get方法的重载方法，假如有需要在get上拼接参数的，需要调用这个方法传递一些参数进来
     *
     * @param url
     * @param params
     * @param callback
     */
    public void get(String url, Map<String, Object> params, BaseCallback callback) {
        //1，构建Request对象
        Request request = buildGetRequest(url, params);
        //2、去请求数据
        doRequest(request, callback);
    }


    /**
     * 创建一个Get方式的Request
     *
     * @param url
     * @param params
     * @return
     */
    private Request buildGetRequest(String url, Map<String, Object> params) {
        return buildRequest(url, params, HttpMethodType.GET);
    }

    /**
     * 创建一个Post方式的Reuqest
     *
     * @param url
     * @param params
     * @return
     */
    private Request buildPostRequest(String url, Map<String, Object> params) {
        return buildRequest(url, params, HttpMethodType.POST);
    }


    /**
     * 对外提供的post方法
     *
     * @param url
     * @param params
     * @param callback
     */
    public void post(String url, Map<String, Object> params, BaseCallback callback) {
        //第一步，获取Request对象
        Request request = buildPostRequest(url, params);
        //第二步，去请求数据v
        doRequest(request, callback);
    }

    /**
     * 构建一个Request对象
     *
     * @param url
     * @param params
     * @param methodType
     * @return
     */
    private Request buildRequest(String url, Map<String, Object> params, HttpMethodType methodType) {
        //封装Request对象
        Request.Builder builder = new Request.Builder();
        builder.url(url);

        if (methodType == HttpMethodType.GET) {
            //拼接URL参数
            url = buildUrlParams(url, params);
            //重新设置url
            builder.url(url);
            builder.get();
        } else if (methodType == HttpMethodType.POST) {
            RequestBody body = buildFormData(params);
            builder.post(body);
        }
        return builder.build();
    }

    /**
     * 拼接get请求的时候URL的参数
     *
     * @param url
     * @param params
     * @return
     */
    private String buildUrlParams(String url, Map<String, Object> params) {
        //先对Params进行判空处理
        if (params == null) {
            params = new HashMap<>(1);
        }

        //添加token
        String token = UserLocalData.getToken();
        if (!TextUtils.isEmpty(token)) {
            params.put("token", token);
        }

        //叠加参数
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }

        //裁剪参数
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }

        //把参数往URL上拼接
        if (url.indexOf("?") > 0) {
            url = url + "&" + s;
        } else {
            url = url + "?" + s;
        }

        return url;
    }


    /**
     * 构建要post的数据
     *
     * @param params
     * @return
     */
    private RequestBody buildFormData(Map<String, Object> params) {

        FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();

        if (params != null) {

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                encodingBuilder.add(entry.getKey(),entry.getValue()==null?"":entry.getValue().toString());
            }

            /**
             * post的时候，要增加一个token，登录的时候需要用到
             */
            String token = UserLocalData.getToken();
            if (!TextUtils.isEmpty(token)) {
                encodingBuilder.add("token", token);
            }

        }
        return encodingBuilder.build();
    }


    enum HttpMethodType {
        GET,
        POST
    }

    /**
     * 请求网络数据的过程
     *
     * @param request
     * @param callback
     */
    public void doRequest(final Request request, final BaseCallback callback) {

        /**
         * 在请求网络之前调用，可以让用户弹起一些对话框之类的
         */
        callback.onRequestBefore(request);


        /**
         * 去请求网络
         */
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //请求失败的回调
                callbackFailure(callback, request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {

                //请求成功，关闭对话框
                callbackResponse(callback, response);

                if (response.isSuccessful()) {
                    //获取请求结果
                    String resultStr = response.body().string();
                    //假如外面 要求返回结果类型是字符串，那么就不需要做类型转换，直接返回结果
                    if (callback.mType == String.class) {
                        callbackSuccess(callback, response, resultStr);
                    } else {
                        try {
                            //假如外面对返回类型有特殊需求，那么用gson转成特定的类型
                            Object obj = mGson.fromJson(resultStr, callback.mType);
                            //然后返回对应的类型
                            callbackSuccess(callback, response, obj);
                        } catch (JsonParseException e) {
                            //若发生解析错误则返回这个
                            callback.onError(response, response.code(), e);
                        }

                    }

                } else if (response.code() == TOKEN_ERROR || response.code() == TOKEN_EXPIRE || response.code() == TOKEN_MISSING) {
                    //回调Token失效的接口
                    callbackTokenError(callback, response);
                } else {
                    //假如不是成功的，就返回请求失败
                    callbackError(callback, response, null);
                }

            }
        });


    }


    /**
     * 请求有响应，关闭对话框。。。这里有点迷糊。。。
     *
     * @param callback
     * @param response
     */
    private void callbackResponse(final BaseCallback callback, final Response response) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }

    /**
     * 请求成功的回调
     *
     * @param callback
     * @param response
     * @param obj
     */
    private void callbackSuccess(final BaseCallback callback, final Response response, final Object obj) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //调用我们自己定义的 BaseCallback的方法
                callback.onSuccess(response, obj);
            }
        });
    }



    /**
     * 回调Token失效
     *
     * @param callback
     * @param response
     */
    private void callbackTokenError(final BaseCallback callback, final Response response) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //调用我们自己定义的 BaseCallback的方法
                callback.onTokenError(response,response.code());
            }
        });
    }


    /**
     * 请求失败的回调
     *
     * @param callback
     * @param request
     * @param e
     */
    private void callbackFailure(final BaseCallback callback, final Request request, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //调用我们自己定义的 BaseCallback的方法
                callback.onFailure(request, e);
            }
        });
    }

    /**
     * 请求错误的回调
     *
     * @param callback
     * @param response
     * @param e
     */
    private void callbackError(final BaseCallback callback, final Response response, final IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //调用我们自己定义的 BaseCallback的方法
                callback.onError(response, response.code(), e);
            }
        });
    }


}
