package com.shake.easystore.utils;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.shake.easystore.bean.ShoppingCart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shake on 17-5-23.
 * 一些全局变量可以存在这里
 */
public class LocalDataUtils {

    private static LocalDataUtils mInstance;

    private Context mContext;

    private LocalDataUtils(Context context) {
        this.mContext = context;
    }


    public static LocalDataUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocalDataUtils(context.getApplicationContext());
        }
        return mInstance;
    }


    /**
     * 获取存储在SP中的数据
     *
     * @param key
     * @return
     */
    public List<ShoppingCart> getCarts(String key) {
        //从SP中取得json数据
        String json = PreferencesUtils.getString(mContext, key);
        //将json数据转换为 List<ShoppingCart>
        List<ShoppingCart> carts = new ArrayList<>();
        if (json != null) {
            carts = JSONUtil.fromJson(json, new TypeToken<List<ShoppingCart>>() {
            }.getType());
        }

        return carts;
    }


    /**
     * 将订单数据存储在SP中
     *
     * @param carts
     * @param key
     */
    public void putCarts(List<ShoppingCart> carts, String key) {
        //先获取本地数据
        List<ShoppingCart> mDatas = getCarts(key);


        //叠加数据，为空的时候，必须先实例化一个订单
        if(mDatas == null || mDatas.size() <= 0){
            mDatas = new ArrayList<>();
        }
        mDatas.addAll(carts);


        //存储在SP中
        PreferencesUtils.putString(mContext, key, JSONUtil.toJSON(mDatas));

    }


}
