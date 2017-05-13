package com.shake.easystore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shake.easystore.bean.User;
import com.shake.easystore.utils.IntentUtils;
import com.shake.easystore.utils.UserLocalData;

public class BaseActivity extends AppCompatActivity {

    /**
     * 经过重载的startActivity方法
     *
     * @param intent
     * @param isNeedLogin
     */
    public void startActivity(Intent intent, boolean isNeedLogin) {

        //假如需要登录
        if(isNeedLogin){
            User user = UserLocalData.getUser();
            //假如user不为空，证明当前已经是登录状态
            if(user != null){
                super.startActivity(intent);
            }else {
                //当前是未登录状态，需要先跳转到登录界面

                //先保存当前的Intent,记录它是要跳转到哪个Activity
                IntentUtils.putIntent(intent);
                //再跳转到LoginActivity
                Intent loginIntent = new Intent(this, LoginActivity.class);
                super.startActivity(loginIntent);
            }
        }else {
            //假如不需要先登录，那么直接调用父类的跳转方法
            super.startActivity(intent);
        }


    }


}
