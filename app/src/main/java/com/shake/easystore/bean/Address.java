package com.shake.easystore.bean;

import java.io.Serializable;

/**
 * 收货地址
 */
public class Address implements Serializable{

    private String id;
    private String name;
    private String address;
    private String phone;
    private String detailAddress;

    private Boolean isDefault = false;

    public Address() {
    }
    public Address(String name, String address, String phone, String detailAddress,boolean isDefault) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
    }
    public Address(String id,String name, String address, String phone, String detailAddress,boolean isDefault) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }


    @Override
    public String toString() {
        return "ID是多少："+id+"，名字是："+name+",号码是："+phone+"，地址是："+address+"，是不是默认的："+isDefault;
    }
}
