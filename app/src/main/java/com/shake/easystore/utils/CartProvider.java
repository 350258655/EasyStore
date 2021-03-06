package com.shake.easystore.utils;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.reflect.TypeToken;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.bean.Wares;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shake on 17-5-9.
 * 购物车数据的存储工具类。购物车的数据则用一个 ShoppingCart 类封装起来
 * 存储分两层，用户直接接触到的是SparseArray，而底层则是将SparseArray中的数据存储到SP中。SP和SparseArray始终保持同步
 */
public class CartProvider {


    private SparseArray<ShoppingCart> mSparseArrayDatas;

    private Context mContext;

    public static final String CART_JSON = "cart_json";


    private static CartProvider mCartProvider;

    private CartProvider(Context context) {
        this.mContext = context;
        //创建SparseArray
        mSparseArrayDatas = new SparseArray<>(10);
        //把SP中的数据，添加到这个SparseArray中。。。要添加到SparseArray中，后续增删改查才能在这个SparseArray的基础上进行
        spToSparse();
    }


    /**
     * 以单例模式来创建CartProvider，保证全局只有一个 CartProvider，这样才能保证数据得到同步
     *
     * @param context
     * @return
     */
    public static CartProvider getInstance(Context context) {
        if (mCartProvider == null) {
            mCartProvider = new CartProvider(context.getApplicationContext());
        }
        return mCartProvider;
    }


    /**
     * 提供给外面的更新方法
     *
     * @param cart
     */
    public void upDate(ShoppingCart cart) {
        mSparseArrayDatas.put(cart.getId().intValue(), cart);
        commit();
    }

    /**
     * 提供给外面的删除方法
     *
     * @param cart
     */
    public void delete(ShoppingCart cart) {
        mSparseArrayDatas.delete(cart.getId().intValue());
        commit();
    }

    /**
     * 提供给外面的添加方法
     *
     * @param cart
     */
    public void put(ShoppingCart cart) {
        //先根据要添加的cart去集合中查找，看是否能拿到数据
        ShoppingCart temp = mSparseArrayDatas.get(cart.getId().intValue());
        //假如购物车中有该项，那么数量加1
        if (temp != null) {
            temp.setCount(temp.getCount() + 1);
        } else {
            //假如购物车没有，则添加该项
            temp = cart;
            temp.setCount(1);
        }
        //存到 SparseArray中
        mSparseArrayDatas.put(temp.getId().intValue(), temp);

        commit();
    }

    /**
     * 提供给外面的添加方法，直接添加的是Wares，在这里转换为ShoppingCart再添加
     *
     * @param wares
     */
    public void put(Wares wares) {
        ShoppingCart cart = getShoppingCartData(wares);
        put(cart);
    }


    /**
     * 提供给外面的查询所有的方法。因为一开始有把SP中的数据同步到SparseArray中，但是在后面的
     */
    public List<ShoppingCart> getAllData() {
        return sparseArrayToList();
    }


    /**
     * 将 SparseArray 中的数据存到 SP中
     */
    private void commit() {
        //将 SparseArray 转存到 List集合中
        List<ShoppingCart> carts = sparseArrayToList();
        //存到SP中
        PreferencesUtils.putString(mContext, CART_JSON, JSONUtil.toJSON(carts));
    }


    /**
     * 将 SparseArray 转存到 List集合中
     *
     * @return
     */
    private List<ShoppingCart> sparseArrayToList() {
        int size = mSparseArrayDatas.size();
        List<ShoppingCart> carts = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            carts.add(mSparseArrayDatas.valueAt(i));
        }
        return carts;
    }


    /**
     * 把SP中的数据，添加到这个SparseArray中
     */
    private void spToSparse() {
        //先从SP中拿数据
        List<ShoppingCart> carts = getDataFromSP();

        //再将转存到 SparseArray中
        if (carts != null && carts.size() > 0) {
            for (ShoppingCart cart : carts) {
                mSparseArrayDatas.put(cart.getId().intValue(), cart);
            }
        }

    }


    /**
     * 从 SP中取数据
     *
     * @return
     */
    private List<ShoppingCart> getDataFromSP() {
        //从SP中取得json数据
        String json = PreferencesUtils.getString(mContext, CART_JSON);

        //将json数据转换为 List<ShoppingCart>
        List<ShoppingCart> carts = null;
        if (json != null) {
            carts = JSONUtil.fromJson(json, new TypeToken<List<ShoppingCart>>() {
            }.getType());
        }

        return carts;
    }


    /**
     * 获取ShoppingCart。因为父类没法强转成子类，所以只能一个个set
     *
     * @param wares
     * @return
     */
    public ShoppingCart getShoppingCartData(Wares wares) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(wares.getId());
        cart.setDescription(wares.getDescription());
        cart.setImgUrl(wares.getImgUrl());
        cart.setName(wares.getName());
        cart.setPrice(wares.getPrice());
        return cart;
    }


}
