package com.shake.easystore.utils;

import com.shake.easystore.bean.ShoppingCart;

import java.util.List;

/**
 * Created by shake on 17-5-22.
 * 存储一些静态数据
 */
public class StaticDataUtils {

    private static StaticDataUtils mInstance;

    //存储购物车中 被选中的那些Cart
    private static List<ShoppingCart> mList;

    //存储单个Cart
    private static ShoppingCart mCart;


    private StaticDataUtils() {
    }


    public static void init() {
        if (mInstance == null) {
            mInstance = new StaticDataUtils();
        }
    }

    /**
     * 存储购物车中被选中的Carts的数据
     *
     * @param carts
     */
    public static void putShopCarts(List<ShoppingCart> carts) {
        mList = carts;
    }

    /**
     * 获取购物车中被选中的Carts的数据
     *
     * @return
     */
    public static List<ShoppingCart> getShopCarts() {
        return mList;
    }

    /**
     * 清除购物车中被选中的Carts的数据
     */
    public static void clearShopCarts() {
        mList = null;
    }


    /**
     * 存储单个cart
     *
     * @param cart
     */
    public static void putShopCart(ShoppingCart cart) {
        mCart = cart;
    }

    /**
     * 获取单个Cart
     *
     * @return
     */
    public static ShoppingCart getShopCart() {
        return mCart;
    }

    /**
     * 清除Cart
     */
    public static void clearShopCart() {
        mCart = null;
    }




}
