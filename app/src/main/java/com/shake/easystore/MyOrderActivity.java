package com.shake.easystore;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.adapter.MyOrderAdapter;
import com.shake.easystore.adapter.decoration.CardViewtemDecortion;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.utils.LocalDataUtils;
import com.shake.easystore.weiget.ShopToolbar;

import java.util.List;

/**
 * Created by shake on 17-5-23.
 * 我的订单的Activity
 */
public class MyOrderActivity extends BaseActivity {


    public static final int STATUS_SUCCESS = 1; //支付成功的订单
    public static final int STATUS_PAY_FAIL = -2; //支付失败的订单
    public static final int STATUS_PAY_WAIT = 0; //：待支付的订单
    private int status = STATUS_SUCCESS;

    @ViewInject(R.id.toolbar)
    private ShopToolbar mToolbar;


    @ViewInject(R.id.tab_layout)
    private TabLayout mTablayout;


    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerview;

    private MyOrderAdapter mAdapter;

    LocalDataUtils utils;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ViewUtils.inject(this);

        //初始化存储订单工具类
        utils = LocalDataUtils.getInstance(this);

        //初始化Toolbar
        initToolBar();

        //初始化TAB
        initTab();

        //获取数据，默认是全部的数据
        getDatas(STATUS_SUCCESS);


        //初始化事件
        initEvent();
    }

    private void initEvent() {

        //设置Adapter的监听时间
        mAdapter.setBuyListener(new MyOrderAdapter.BuyListener() {
            @Override
            public void onBuyAgain(ShoppingCart cart, int position) {
                //再次购买
                buyAgain(cart, position);
            }

            @Override
            public void onBuyContinue(ShoppingCart cart, int position) {

            }
        });

    }


    /**
     * TODO 再次购买
     *
     * @param cart
     * @param position
     */
    private void buyAgain(ShoppingCart cart, int position) {
        //设置订单号
        cart.setOrderNum(String.valueOf(System.currentTimeMillis()));



    }

    /**
     * 获取数据
     *
     * @param status
     */
    private void getDatas(int status) {

        List<ShoppingCart> mCarts = null;

        switch (status) {
            case STATUS_SUCCESS:
                //获取支付成功的数据
                mCarts = utils.getCarts(Contants.SUCCESS_ORDER);
                break;

            case STATUS_PAY_WAIT:
                //获取取消支付的数据
                mCarts = utils.getCarts(Contants.CANCLE_ORDER);
                break;
            case STATUS_PAY_FAIL:
                //获取支付失败的数据
                mCarts = utils.getCarts(Contants.FAILD_ORDER);
                break;
        }

        Log.i("TAG", "MyOrderActivity，获取订单数量: " + mCarts.size());

        //显示数据
        showDatas(mCarts);

    }

    /**
     * 显示数据
     *
     * @param mCarts
     */
    private void showDatas(List<ShoppingCart> mCarts) {
        if (mAdapter == null) {
            mAdapter = new MyOrderAdapter(this, mCarts);
            //设置当前状态
            mAdapter.setCurrentState(status);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview.addItemDecoration(new CardViewtemDecortion());
        } else {
            mAdapter.refreshData(mCarts);
            mAdapter.setCurrentState(status);
            mRecyclerview.setAdapter(mAdapter);
        }

    }


    /**
     * 初始化TAB
     */
    private void initTab() {

        TabLayout.Tab tab = mTablayout.newTab();
        tab.setText("支付成功");
        tab.setTag(STATUS_SUCCESS);
        mTablayout.addTab(tab);

        tab = mTablayout.newTab();
        tab.setText("待支付");
        tab.setTag(STATUS_PAY_WAIT);
        mTablayout.addTab(tab);

        tab = mTablayout.newTab();
        tab.setText("支付失败");
        tab.setTag(STATUS_PAY_FAIL);
        mTablayout.addTab(tab);

        //设置事件
        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                status = (int) tab.getTag();
                //再次去初始化数据
                getDatas(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * 初始化ToolBar的返回事件
     */
    private void initToolBar() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
