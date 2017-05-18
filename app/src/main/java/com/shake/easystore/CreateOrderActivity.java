package com.shake.easystore;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.adapter.WareOrderAdapter;
import com.shake.easystore.adapter.layoutmanager.FullyLinearLayoutManager;
import com.shake.easystore.http.OkHttpHelper;
import com.shake.easystore.utils.CartProvider;
import com.shake.easystore.weiget.ShopToolbar;

import java.util.HashMap;
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

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();


    private CartProvider mCartProvider;

    private WareOrderAdapter mAdapter;

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

        //获取CartProvider数据存储类
        mCartProvider = CartProvider.getInstance(this);

        //从数据存储类中获取数据，并且提供给适配器
        mAdapter = new WareOrderAdapter(this, mCartProvider.getAllData());

        //设置RecycleView
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {

        Log.i("TAG", "CreateOrderActivity，被选中的渠道: "+v.getTag().toString());
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
            if(entry.getKey().equals(paychannel)){
                boolean isCheck = rb.isChecked();
                rb.setChecked(!isCheck);
            }else {
                //没被选中的就反选
                rb.setChecked(false);
            }

        }
    }
}
