package com.shake.easystore.msg;

import java.io.Serializable;

/**
 * Created by shake on 17-5-12.
 * 返回信息的封装类
 */
public class BaseResponeMsg implements Serializable {

    public final static int STATUS_SUCCESS = 1;
    public final static int STATUS_ERROR = 0;

    protected int currentState = STATUS_SUCCESS;
    protected  String message;

    public int getStatus() {
        return currentState;
    }

    public void setStatus(int status) {
        this.currentState = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
