package com.shake.easystore;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.adapter.WareOrderAdapter;
import com.shake.easystore.adapter.layoutmanager.FullyLinearLayoutManager;
import com.shake.easystore.alipay.PartnerConfig;
import com.shake.easystore.alipay.PayResult;
import com.shake.easystore.alipay.SignUtils;
import com.shake.easystore.bean.Address;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.db.Dao;
import com.shake.easystore.http.OkHttpHelper;
import com.shake.easystore.http.SpotsCallBack;
import com.shake.easystore.utils.StaticDataUtils;
import com.shake.easystore.weiget.ShopToolbar;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";


    @ViewInject(R.id.txt_order)
    private TextView txtOrder;

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;


    @ViewInject(R.id.rl_alipay)
    private RelativeLayout mLayoutAlipay;

    @ViewInject(R.id.rl_wechat)
    private RelativeLayout mLayoutWechat;

    @ViewInject(R.id.rb_alipay)
    private RadioButton mRbAlipay;

    @ViewInject(R.id.rb_webchat)
    private RadioButton mRbWechat;

    @ViewInject(R.id.btn_createOrder)
    private Button mBtnCreateOrder;

    @ViewInject(R.id.txt_total)
    private TextView mTxtTotal;

    @ViewInject(R.id.toolbar)
    private ShopToolbar mToolbar;

    //收件人姓名
    @ViewInject(R.id.txt_name)
    private TextView txt_name;

    //收件人地址
    @ViewInject(R.id.txt_address)
    private TextView txt_address;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();


    private WareOrderAdapter mAdapter;


    private boolean hasAddress;

    //商品价格
    private float amount;

    private String payChannel = CHANNEL_ALIPAY;

    /**
     * 存放渠道和对应的RadioButton
     */
    private HashMap<String, RadioButton> channels = new HashMap<>(3);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ViewUtils.inject(this);

        //显示数据
        showData();

        //初始化Toolbar上的点击返回按钮
        initToolbarEvent();

        //初始化支付渠道
        initChannels();

        //初始化事件，其实就一个下单的按钮
        initEvent();
    }


    /**
     * 初始化下单事件
     */
    private void initEvent() {

        //去下单
        mBtnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //先判断有没有收货地址，有收货地址才能下单
                if(!hasAddress){
                    Toast.makeText(CreateOrderActivity.this, "请先添加收货地址！", Toast.LENGTH_SHORT).show();
                    return;
                }

                //去下单
                loginAndcreateOrder();
            }
        });
    }

    /**
     * 初始化Toolbar上的点击返回按钮
     */
    private void initToolbarEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化支付渠道
     */
    private void initChannels() {
        channels.put(CHANNEL_ALIPAY, mRbAlipay);
        channels.put(CHANNEL_WECHAT, mRbWechat);

        mLayoutAlipay.setOnClickListener(this);
        mLayoutWechat.setOnClickListener(this);

        //获取并且显示总价格
        amount = mAdapter.getTotalPrice();
        mTxtTotal.setText("应付款： ￥" + amount);
    }


    /**
     * 显示数据
     */
    private void showData() {

        //从全局数据中获取
        List<ShoppingCart> carts = StaticDataUtils.getShopCarts();

        //从数据存储类中获取数据，并且提供给适配器。并且设置Adapter
        mAdapter = new WareOrderAdapter(this,carts);
        //设置RecycleView
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);


        //显示收货地址信息
        Dao.getDao(this).query(new Dao.OnCompleteListener() {
            @Override
            public void onDone(List<Address> list) {
                if (list.size() > 0 && list != null) {
                    Address address = list.get(0);
                    txt_name.setText(address.getName());
                    txt_address.setText(address.getAddress() + "," + address.getDetailAddress() + "," + address.getPhone());
                    hasAddress = true;
                } else {
                    txt_name.setText("暂时没有收货地址信息");
                    txt_address.setText("暂时没有收货地址信息");
                    hasAddress = false;
                }

            }
        });


    }

    @Override
    public void onClick(View v) {
        Log.i("TAG", "CreateOrderActivity，被选中的渠道: " + v.getTag().toString());
        //选择支付的渠道
        selecePayChannel(v.getTag().toString());
    }


    /**
     * 选择支付的渠道
     *
     * @param paychannel
     */
    private void selecePayChannel(String paychannel) {
        for (Map.Entry<String, RadioButton> entry : channels.entrySet()) {

            payChannel = paychannel;
            RadioButton rb = entry.getValue();
            //被选中的那个就反选
            if (entry.getKey().equals(paychannel)) {
                boolean isCheck = rb.isChecked();
                rb.setChecked(!isCheck);
            } else {
                //没被选中的就反选
                rb.setChecked(false);
            }

        }
    }


    /**
     * 创建订单
     */
    private void loginAndcreateOrder() {

        //先登录
        okHttpHelper.get(Contants.API.HD_LOGIN, new SpotsCallBack<String>(this) {
            @Override
            public void onSuccess(Response response, String s) {
                Log.i("TAG", "CreateOrderActivity,浩动登录返回的信息: " + s);
                //再创建订单
                createOrder(s);

            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    /**
     * 到浩动后台创建订单
     *
     * @param s
     */
    private void createOrder(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (json.getString("code").equals("1")) {
                //获取access_token
                String access_token = json.getJSONObject("result").getString("access_token");

                String url = Contants.API.HD_CREATE_ORDER +
                        "&access_token=" + URLEncoder.encode(access_token, "utf-8") +
                        "&amount=" + URLEncoder.encode("0.01", "utf-8") +
                        "&notifyurl=" + URLEncoder.encode("http://callback", "utf-8") +
                        "&exorderno=" + URLEncoder.encode("外部订单", "utf-8") +
                        "&player=" + URLEncoder.encode("角色", "utf-8") +
                        "&server=" + URLEncoder.encode("区服", "utf-8") +
                        "&remark=" + URLEncoder.encode("产品名称", "utf-8") +
                        "&desc=" + URLEncoder.encode("产品描述", "utf-8") +
                        "&extend_info=" + URLEncoder.encode("扩展信息", "utf-8");

                okHttpHelper.get(url, new SpotsCallBack<String>(CreateOrderActivity.this) {

                    @Override
                    public void onSuccess(Response response, String s) {
                        Log.i("TAG", "CreateOrderActivity,浩动下单返回的信息: " + s);
                        //去支付
                        gotoPay(s);
                    }

                    @Override
                    public void onError(Response response, int code, Exception e) {

                    }
                });


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 去支付
     *
     * @param s
     */
    private void gotoPay(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (json.getString("code").equals("1")) {
                //获取订单号
                String orderNum = json.getJSONObject("result").getString("order_no");

                if (payChannel == CHANNEL_ALIPAY) {
                    //支付宝支付
                    goAliPay(orderNum);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付宝支付
     *
     * @param orderNum
     */
    private void goAliPay(String orderNum) {
        //获取支付宝订单信息
        String orderInfo = getAliPayOrderInfo(orderNum);

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";


        //异步支付
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(CreateOrderActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }


    /**
     * 异步支付过程
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        JSONObject resultJson = new JSONObject();
                        //支付宝成功支付成功返回
                        //TODO 回调结果
                        setResult(Contants.RESULT_SUCCESS);

                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            //TODO 回调结果

                        } else if(TextUtils.equals(resultStatus, "6001")) {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            //TODO 回调结果,用户取消订单
                            setResult(Contants.RESULT_CANCLE);
                        }else {
                            setResult(Contants.RESULT_FAILD);
                        }
                    }
                    break;
                }
                case 2: {
                    //TODO 回调结果
                    setResult(Contants.RESULT_FAILD);
                    break;
                }
                default:
                    break;
            }

            //支付过程结束后，关闭这个Activity
            finish();

        }


    };


    /**
     * 获取支付宝的订单信息
     *
     * @param orderNum
     */
    private String getAliPayOrderInfo(String orderNum) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + "2088111008275155" + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + "ddlegame@ddle.cn" + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + "研发测试" + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + "研发测试" + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + "0.01" + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://tiger.hodogame.com/alipayquicknotify.php" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";


        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";


        Log.i("TAG", "CreateOrderActivity，getAliPayOrderInfo，生成支付宝支付信息: " + orderInfo);

        return orderInfo;

    }


    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, PartnerConfig.RSA_PRIVATE);
    }


}
