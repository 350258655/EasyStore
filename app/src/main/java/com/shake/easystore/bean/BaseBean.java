package com.shake.easystore.bean;

import java.io.Serializable;

/**
 * Created by shake on 17-5-4.
 */
public class BaseBean implements Serializable {

    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
