package com.shake.easystore;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.adapter.HotspotAdapter;
import com.shake.easystore.adapter.decoration.DividerItemDecoration;
import com.shake.easystore.bean.Page;
import com.shake.easystore.bean.Wares;
import com.shake.easystore.utils.Pager;
import com.shake.easystore.weiget.ShopToolbar;

import java.util.List;

/**
 * 商品列表的Activity
 */
public class WareListActivity extends Activity implements TabLayout.OnTabSelectedListener,Pager.OnPageListener<Wares>{


    @ViewInject(R.id.tab_layout)
    private TabLayout mTablayout;

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerview_wares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLayout;

    @ViewInject(R.id.shoplist_shopToolbar)
    private ShopToolbar mToolbar;


    /**
     * TabLayout的三种Tag
     */
    public static final int TAG_DEFAULT=0;
    public static final int TAG_SALE=1;
    public static final int TAG_PRICE=2;


    /**
     * 商品列表的显示状态
     */
    public static final int ACTION_LIST=1;
    public static final int ACTION_GIRD=2;

    //这是商品的ID，从onCreate中获取到
    private long campaignId = 0;

    //排序规则，是默认，按销量还是按价格
    private int orderBy = 0;

    private Pager pager;

    //这里用到的Adapter和热点Adapter是一样的
    private HotspotAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ware_list);
        ViewUtils.inject(this);
        //从HomeFragment传递进来
        campaignId=getIntent().getLongExtra(Contants.COMPAINGAIN_ID,0);

        //初始化TabLayout
        initTabLayout();

        //初始化ToolBar
        initToolBar();

        //请求数据
        requestData();

    }


    /**
     * 请求数据
     */
    private void requestData() {

        /**
         * 因为此接口被菜鸟窝限制了，只能修改成为热门商品中的接口
         */
//        pager= Pager.newBuilder().setUrl(Contants.API.WARES_CAMPAIN_LIST)
//                .putParams("campaignId", campaignId)
//                .putParams("orderBy", orderBy)
//                .setRefreshLayout(mRefreshLayout)
//                .setLoadMore(true)
//                .setOnPageListener(this)
//                .build(this,new TypeToken<Page<Wares>>(){}.getType());

         pager= Pager.newBuilder().setUrl(Contants.API.WARES_HOT)
                .setRefreshLayout(mRefreshLayout)
                .setLoadMore(true)
                .setOnPageListener(this)
                .build(this,new TypeToken<Page<Wares>>(){}.getType());

        pager.request();
    }


    /**
     * 初始化ToolBar
     */
    private void initToolBar() {

        //设置ToolBar上面返回按钮的事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //结束这个Activity
                finish();
            }
        });


        //设置右边的icon
        mToolbar.setRightButtonIcon(R.drawable.icon_grid_32);
        //设置当前商品列表的显示状态
        mToolbar.getRightButton().setTag(ACTION_LIST);
        //设置右边按钮的监听事件
        mToolbar.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变商品列表的显示状态
                changeShapShowStyle(v);
            }
        });

    }


    /**
     * 改变商品列表的显示状态
     */
    private void changeShapShowStyle(View view) {
        //获取状态
        int action = (int) view.getTag();

        if(action == ACTION_LIST){
            //改为GridView的形式
            mToolbar.setRightButtonIcon(R.drawable.icon_list_32);
            mToolbar.getRightButton().setTag(ACTION_GIRD);
            //更新布局
            mAdapter.refreshLayout(R.layout.template_grid_wares);

            mRecyclerview_wares.setLayoutManager(new GridLayoutManager(this,2));
        }else if(action == ACTION_GIRD){
            //改为ListView的形式
            mToolbar.setRightButtonIcon(R.drawable.icon_grid_32);
            mToolbar.getRightButton().setTag(ACTION_LIST);
            mAdapter.refreshLayout(R.layout.template_hot_wares);
            mRecyclerview_wares.setLayoutManager(new LinearLayoutManager(this));
        }


    }

    /**
     * 初始化TabLayout
     */
    private void initTabLayout() {

        TabLayout.Tab tab = mTablayout.newTab();

        tab.setText("默认");
        tab.setTag(TAG_DEFAULT);
        mTablayout.addTab(tab);

        tab = mTablayout.newTab();
        tab.setText("价格");
        tab.setTag(TAG_PRICE);
        mTablayout.addTab(tab);

        tab = mTablayout.newTab();
        tab.setText("销量");
        tab.setTag(TAG_SALE);
        mTablayout.addTab(tab);

        //设置监听事件
        mTablayout.setOnTabSelectedListener(this);
    }


    /**
     * 以下三个方法，是当切换TAB的时候的监听器
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {


        /**
         * TODO 因为此接口被菜鸟窝限制了，只能修改成为热门商品中的接口
         */
//        orderBy = (int) tab.getTag();
//
//        //重新加入参数
//        pager.addParam("orderBy", orderBy);

        //再次请求网络
        pager.request();

    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * 以下三个方法，是OnPageListener的回调方法
     * @param datas
     * @param totalPage
     * @param totalCount
     */
    @Override
    public void load(List<Wares> datas, int totalPage, int totalCount) {
        if(mAdapter == null){
            mAdapter = new HotspotAdapter(this,datas);
            mRecyclerview_wares.setAdapter(mAdapter);
            mRecyclerview_wares.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview_wares.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mRecyclerview_wares.setItemAnimator(new DefaultItemAnimator());
        }else {
            //否则就只是刷新数据
            mAdapter.refreshData(datas);
        }

    }

    @Override
    public void refresh(List<Wares> datas, int totalPage, int totalCount) {
        mAdapter.refreshData(datas);
        mRecyclerview_wares.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Wares> datas, int totalPage, int totalCount) {
        mAdapter.loadMoreData(datas);
    }
}
