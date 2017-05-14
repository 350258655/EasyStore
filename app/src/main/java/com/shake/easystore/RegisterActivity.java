package com.shake.easystore;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //打开注册页面
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
// 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");

                    Log.i("TAG", "获取到的国家: "+country+",获取到的手机："+phone);

                    // 提交用户信息（此方法可以不调用）
                   // registerUser(country, phone);
                }
            }
        });
        registerPage.show(this);
    }
}
