package com.shake.easystore;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.utils.DESUtil;
import com.shake.easystore.weiget.ClearEditText;
import com.shake.easystore.weiget.ShopToolbar;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录的Activity
 */
public class LoginActivity extends Activity {


    @ViewInject(R.id.toolbar)
    private ShopToolbar mToolbar;

    @ViewInject(R.id.etxt_phone)
    private ClearEditText mEditTextPhone;

    @ViewInject(R.id.etxt_pwd)
    private ClearEditText mEditTextPwd;

    @ViewInject(R.id.btn_login)
    private Button btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);

        //初始化ToolBar
        initToolBar();

        //初始化事件
        initEvent();


    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //去登录
                login();
            }
        });




    }

    /**
     * 去登录
     */
    private void login() {

        //获取手机号码
        String phone = mEditTextPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(LoginActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        //获取密码
        String password = mEditTextPwd.getText().toString().trim();
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        //存储帐号密码
        Map<String,Object> map = new HashMap<>(2);
        map.put("phone",phone);
        map.put("password", DESUtil.encode(Contants.DES_KEY,password));

        //post帐号密码到服务器

    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        //点击返回按钮
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 点击返回键也要销毁Activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
