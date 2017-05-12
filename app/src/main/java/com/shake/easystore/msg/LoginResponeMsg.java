package com.shake.easystore.msg;

/**
 * Created by shake on 17-5-12.
 * 登录响应信息
 */
public class LoginResponeMsg<User> extends BaseResponeMsg {
    private String token;
    private User data;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

}
