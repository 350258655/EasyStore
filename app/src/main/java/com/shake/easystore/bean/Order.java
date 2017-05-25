package com.shake.easystore.bean;

/**
 * Created by shake on 17-5-23.
 * 订单的bean类，其实就是加上一个订单编号，获取当前日期就好
 */
public class Order extends ShoppingCart {


    private String orderNum;

    public String getOrderNum() {
        orderNum = String.valueOf(System.currentTimeMillis());
        return orderNum;
    }







}
