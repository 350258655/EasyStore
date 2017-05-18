package com.shake.easystore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
import com.shake.easystore.utils.CountTimerView;
import com.shake.easystore.utils.DESUtil;
import com.shake.easystore.utils.UserLocalData;
import com.shake.easystore.weiget.ClearEditText;
import com.shake.easystore.weiget.ShopToolbar;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import dmax.dialog.SpotsDialog;

public class RegisterSecondActivity extends Activity {

    @ViewInject(R.id.toolbar)
    private ShopToolbar mToolBar;

    @ViewInject(R.id.txtTip)
    private TextView mTxtTip;

    @ViewInject(R.id.btn_reSend)
    private Button mBtnResend;

    @ViewInject(R.id.edittxt_code)
    private ClearEditText mEtCode;

    private String phone;
    private String pwd;
    private String countryCode;

    //倒计时的View
    private CountTimerView countTimerView;

    //事件处理器
    private SDKEventHandler mSDKEventHandler;

    //显示对话框
    private SpotsDialog mDialog;

    //网络处理类
    private OkHttpHelper mOkHttpHelper = OkHttpHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second);
        ViewUtils.inject(this);
        //初始化Toolbar的点击事件，即点击右上角的完成按钮
        initToolBarEvent();

        //获取从上个界面获取的数据并显示
        initDataAndView();

        //注册事件
        mSDKEventHandler = new SDKEventHandler();
        SMSSDK.registerEventHandler(mSDKEventHandler);

        //初始化对话框
        mDialog = new SpotsDialog(this,"正在校验验证码");

        //初始化重新发送按钮事件
        initReSendBtnEvent();
    }


    /**
     * 初始化重新发送按钮事件
     */
    private void initReSendBtnEvent() {
        //获取验证码
        SMSSDK.getVerificationCode("+"+countryCode, phone);
        countTimerView.start();

        //显示对话框
        mDialog.show();
    }


    /**
     * 获取从上个界面获取的数据并显示
     */
    private void initDataAndView() {
        //获取数据
        phone = getIntent().getStringExtra("phone");
        pwd = getIntent().getStringExtra("pwd");
        countryCode = getIntent().getStringExtra("countryCode");

        //格式化电话号码，并显示
        String formatPhone = "+" + countryCode + " " + splitPhoneNum(phone);
        String text = getString(R.string.smssdk_send_mobile_detail) + formatPhone;
        mTxtTip.setText(Html.fromHtml(text));

        //开始显示倒计时的View
        countTimerView = new CountTimerView(mBtnResend);
        countTimerView.start();

    }

    /**
     * 裁剪手机号码
     *
     * @param phone
     * @return
     */
    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        for (int i = 4, len = builder.length(); i < len; i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();
        return builder.toString();
    }

    /**
     * 初始化Toolbar的点击事件，即点击右上角的完成按钮
     */
    private void initToolBarEvent() {

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交验证码
                submitCode();
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
     * 提交验证码
     */
    private void submitCode() {
        //获取编辑框中的验证码
        String code = mEtCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(RegisterSecondActivity.this, R.string.smssdk_write_identify_code, Toast.LENGTH_SHORT).show();
            return;
        }

        //提交验证码
        SMSSDK.submitVerificationCode(countryCode, phone, code);

        Log.i("TAG", "RegisterSecondActivity。提交验证码的信息: "+countryCode+",手机号码："+phone+"，验证码："+code);
        //显示对话框
        mDialog.show();
    }


    /**
     * 注册
     */
    private void doRegister() {
        Map<String,String> params = new HashMap<>(2);
        params.put("phone",phone);
        params.put("password", DESUtil.encode(Contants.DES_KEY, pwd));

        mOkHttpHelper.post(Contants.API.REG, params, new SpotsCallBack<LoginResponeMsg<User>>(this) {

            @Override
            public void onSuccess(Response response, LoginResponeMsg<User> userLoginResponeMsg) {

                //让对话框消失
                if(mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();
                }

                //假如注册失败
                if(userLoginResponeMsg.getStatus() == LoginResponeMsg.STATUS_ERROR){
                    Toast.makeText(RegisterSecondActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                //保存用户和Token
                UserLocalData.putUserAndToken(userLoginResponeMsg.getData(), userLoginResponeMsg.getToken());

                //回到Home页面并且结束本Activity
                startActivity(new Intent(RegisterSecondActivity.this,MainActivity.class));
                finish();

            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            //假如是Token验证失败呢？
            @Override
            public void onTokenError(Response response, int code) {
                super.onTokenError(response, code);

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mSDKEventHandler);
    }

    class SDKEventHandler extends EventHandler{
        @Override
        public void afterEvent(final int event, final int result, final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //让对话框消失
                    if(mDialog != null && mDialog.isShowing()){
                        mDialog.dismiss();
                    }

                    if(result == SMSSDK.RESULT_COMPLETE){

                        //假如提交的验证码 验证成功
                        if(result == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){

                            //去注册
                            doRegister();

                            //让对话框消失
                            mDialog.setMessage("正在提交注册信息");
                            mDialog.show();
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
                                Toast.makeText(RegisterSecondActivity.this, des, Toast.LENGTH_SHORT).show();
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
