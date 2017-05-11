package com.shake.easystore.http;

import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by shake on 17-5-4.
 * 回调接口类，应用泛型，可以回调对应类型的数据。其实定义成一个接口也是可以的
 */
public abstract class BaseCallback<T> {

    public Type mType;

    /**
     * 将 T 转换为Type类型
     *
     * @param subclass
     * @return
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public BaseCallback() {
        mType = getSuperclassTypeParameter(getClass());
    }


    /**
     * 在请求网络之前调用的方法
     *
     * @param request
     */
    public abstract void onRequestBefore(Request request);

    /**
     * 在请求网络失败的时候调用的方法
     *
     * @param request
     * @param e
     */
    public abstract void onFailure(Request request, IOException e);


    /**
     * 请求成功时调用此方法，用于关闭loading对话框
     *
     * @param response
     */
    public abstract void onResponse(Response response);


    /**
     * 在请求网络成功的时候调用的方法。返回泛型，用户就不需要再从Response中去解析
     *
     * @param response
     */
    public abstract void onSuccess(Response response, T t);

    /**
     * 在请求网络返回发生错误的时候调用的方法
     *
     * @param response
     * @param code
     * @param e
     */
    public abstract void onError(Response response, int code, Exception e);

}
