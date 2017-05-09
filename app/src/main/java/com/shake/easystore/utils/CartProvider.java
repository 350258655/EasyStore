package com.shake.easystore.utils;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.reflect.TypeToken;
import com.shake.easystore.bean.ShoppingCart;

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

    public CartProvider(Context context) {
        this.mContext = context;
        //创建SparseArray
        mSparseArrayDatas = new SparseArray<>(10);
        //把SP中的数据，添加到这个SparseArray中。。。要添加到SparseArray中，后续增删改查才能在这个SparseArray的基础上进行
        spToSparse();
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
     * 提供给外面的查询所有的方法 ??? 理论上应该可以这样做
     */
    public List<ShoppingCart> getAll(){
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
        if(carts != null && carts.size() > 0){
            for (ShoppingCart cart : carts) {
                mSparseArrayDatas.put(cart.getId().intValue(),cart);
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


}
