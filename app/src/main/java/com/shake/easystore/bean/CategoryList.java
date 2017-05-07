package com.shake.easystore.bean;

/**
 * Created by shake on 2017/5/7 0007.
 * 一级菜单数据的bean类
 */
public class CategoryList extends BaseBean {

    private String name;
    public CategoryList() {
    }

    public CategoryList(String name) {

        this.name = name;
    }

    public CategoryList(long id ,String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
