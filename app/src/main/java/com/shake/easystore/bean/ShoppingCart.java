package com.shake.easystore.bean;

/**
 * Created by shake on 17-5-9.
 * 购物车数据的封装类
 */
public class ShoppingCart extends Wares {
    private int count;
    private boolean isChecked = true;

    private String orderNum;

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

    /**
     * 获取订单号
     *
     * @return
     */
    public String getOrderNum() {
        return orderNum;
    }

    /**
     * 设置订单号
     * @param orderNum
     */
    public void setOrderNum(String orderNum){
        this.orderNum = orderNum;
    }
}
