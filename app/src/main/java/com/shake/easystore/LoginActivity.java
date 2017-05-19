package com.shake.easystore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.bean.User;
import com.shake.easystore.http.OkHttpHelper;
import com.shake.easystore.http.SpotsCallBack;
import com.shake.easystore.msg.LoginResponeMsg;
import com.shake.easystore.utils.DESUtil;
import com.shake.easystore.utils.IntentUtils;
import com.shake.easystore.utils.UserLocalData;
import com.shake.easystore.weiget.ClearEditText;
import com.shake.easystore.weiget.ShopToolbar;
import com.squareup.okhttp.Response;

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


    @ViewInject(R.id.txt_toRegister)
    private TextView txt_toRegister;


    OkHttpHelper mOkHttpHelper = OkHttpHelper.getInstance();

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


        //去注册
        txt_toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
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
        mOkHttpHelper.post(Contants.API.LOGIN,map, new SpotsCallBack<LoginResponeMsg<User>>(this) {
            @Override
            public void onSuccess(Response response, LoginResponeMsg<User> responeMsg) {

                //保存Token 并且 保存用户对象
                UserLocalData.putUserAndToken(responeMsg.getData(),responeMsg.getToken());
                //获取Intent
                Intent intent = IntentUtils.getIntent();

                //假如当前的Intent是空的，那么证明它是直接从某个地方跳转到登录Activity的，那么直接结束掉当前Activity就会
                //自动返回了
                if(intent == null){
                    //回调数据
                    setResult(Contants.REQUEST_CODE);
                    //结束这个Activity，回到"我的" 界面
                    finish();
                }else {
                    //假如是Intent不为空，证明是从某个地方A(假如A要到B，但是能进B的前提是已经登录了)被截断；
                    //那么应该跳转到B
                    startActivity(intent);
                    //清除Intent
                    IntentUtils.clearIntent();
                    //结束当前Activity
                    finish();
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });



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
