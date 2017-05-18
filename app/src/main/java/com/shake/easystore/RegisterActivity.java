package com.shake.easystore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.weiget.ClearEditText;
import com.shake.easystore.weiget.ShopToolbar;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

/**
 * 注册的Activity。注册一直出bug，怀疑是被菜鸟窝屏蔽了接口
 */
public class RegisterActivity extends BaseActivity {

    @ViewInject(R.id.toolbar)
    private ShopToolbar mToolBar;

    @ViewInject(R.id.txt_country)
    private TextView mTxtCountry;

    @ViewInject(R.id.txtCountryCode)
    private TextView mTxtCountryCode;

    @ViewInject(R.id.edittxt_phone)
    private ClearEditText mEtxtPhone;


    @ViewInject(R.id.edit_pwd)
    private ClearEditText mEtxtPwd;

    private SmsEventHandler mEventHandler;

    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewUtils.inject(this);

        //显示区号和国家
        String[] country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        if(country != null){
            //显示 "+86"
            mTxtCountryCode.setText("+"+country[1]);
            //显示 "中国"
            mTxtCountry.setText(country[0]);
        }

        //给ToolBar的右上角的 "下一步" 按钮设置监听事件
        initToolBarEvent();

        //给短信事件注册监听器
        mEventHandler = new SmsEventHandler();
        SMSSDK.registerEventHandler(mEventHandler);

    }

    /**
     * 给ToolBar的右上角的 "下一步" 按钮设置监听事件
     */
    private void initToolBarEvent() {
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去获取手机号码，密码等
                getCode();
            }
        });

        //点击左边的按钮，结束掉这个Activity
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    /**
     * 去获取手机号码，密码等，然后通过手机号码和区号去获取验证码
     */
    private void getCode() {
        //获取手机号码，区号和密码
        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mTxtCountryCode.getText().toString().trim();
        String pwd = mEtxtPwd.getText().toString().trim();

        //检查手机号码和区号等
        checkPhoneNum(phone, code);

        //获取验证码
        SMSSDK.getVerificationCode(code, phone);
    }

    /**
     * 检查手机号码和区号等
     *
     * @param phone
     * @param code
     */
    private void checkPhoneNum(String phone, String code) {

        if(code.startsWith("+")){
            code = code.substring(1);
        }

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(RegisterActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(code == "86"){
            if(phone.length() != 11){
                Toast.makeText(RegisterActivity.this, "手机号码长度不对", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            Toast.makeText(RegisterActivity.this, "您输入的手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEventHandler);
    }


    /**
     * 跳转到验证码填写页面
     * @param data
     */
    private void afterVerificationCodeRequested(Boolean data) {

        //获取手机号码，区号和密码
        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mTxtCountryCode.getText().toString().trim();
        String pwd = mEtxtPwd.getText().toString().trim();
        if(code.startsWith("+")){
            code = code.substring(1);
        }

        //存储这些信息，然后跳转到另外的Activity
        Intent intent = new Intent(RegisterActivity.this,RegisterSecondActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("pwd",pwd);
        intent.putExtra("countryCode",code);
        startActivity(intent);
    }

    /**
     * 注册事件，在发送验证码之后
     */
    class SmsEventHandler extends EventHandler {

        @Override
        public void afterEvent(final int event, final int result, final Object data) {

            //要去在UI线程执行
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //假如是获取事件成功
                    if(result == SMSSDK.RESULT_COMPLETE){
                        //假如是成功提交了验证码
                       if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                           // 请求验证码后，跳转到验证码填写页面
                           afterVerificationCodeRequested((Boolean) data);
                        }

                    }else {
                        // 根据服务器返回的网络错误，给toast提示
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                    }


                }
            });

        }
    }


}
