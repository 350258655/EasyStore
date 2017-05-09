package com.shake.easystore.bean;

/**
 * Created by shake on 17-5-9.
 * 购物车数据的封装类
 */
public class ShoppingCart extends Wares {
    private int count;
    private boolean isChecked=true;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
