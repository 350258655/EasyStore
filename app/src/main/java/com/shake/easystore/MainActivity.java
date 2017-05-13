package com.shake.easystore;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.shake.easystore.bean.Tab;
import com.shake.easystore.fragment.CartFragment;
import com.shake.easystore.fragment.CategoryFragment;
import com.shake.easystore.fragment.HomeFragment;
import com.shake.easystore.fragment.HotNewFragment;
import com.shake.easystore.fragment.MineFragment;
import com.shake.easystore.weiget.FragmentTabHost;
import com.shake.easystore.weiget.ShopToolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {


    private LayoutInflater mInflater;
    //测试一下注释
    private FragmentTabHost mTabHost;

    //存放 底部元素 bean类的集合
    private List<Tab> mTabs = new ArrayList<>(5);

    //购物车Fragment
    private CartFragment cartFragment;

    //Toolbar
    private ShopToolbar mShopToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar();
        initTab();

    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        mShopToolbar = (ShopToolbar) findViewById(R.id.shopToolbar);
    }

    /**
     * 初始化底部菜单
     */
    private void initTab() {

        Tab tab_home = new Tab(R.string.home, R.drawable.selector_icon_home, HomeFragment.class);
        //这里使用优化之后的HotFragment即HotspotFragment
        //Tab tab_hot = new Tab(R.string.hot, R.drawable.selector_icon_hot, HotFragment.class);
        Tab tab_hot = new Tab(R.string.hot, R.drawable.selector_icon_hot, HotNewFragment.class);

        Tab tab_category = new Tab(R.string.catagory, R.drawable.selector_icon_category, CategoryFragment.class);
        Tab tab_cart = new Tab(R.string.cart, R.drawable.selector_icon_cart, CartFragment.class);
        Tab tab_mine = new Tab(R.string.mine, R.drawable.selector_icon_mine, MineFragment.class);

        mTabs.add(tab_home);
        mTabs.add(tab_hot);
        mTabs.add(tab_category);
        mTabs.add(tab_cart);
        mTabs.add(tab_mine);

        mInflater = LayoutInflater.from(this);

        /**
         * 第一步 : 初始化 FragmentTabHost
         */
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        /**
         * 第二步 : 调用 FragmentTabHost 的setup方法
         */
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        /**
         * 第三步 : 遍历集合，创建 TabSpec。并将它装载到 FragmentTabHost
         */
        for (Tab tab : mTabs) {
            //创建TabSpec
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(tab.getTitle()));
            //创建每个 TabSpec 中的View
            View view = buildIndicator(tab);
            //将View装载进 TabSpec
            tabSpec.setIndicator(view);
            //将 TabSpec 装载进 FragmentTabHost
            mTabHost.addTab(tabSpec,tab.getFragment(),null);
        }

        /**
         * 第四步 : 去除分割线，并且默认选中第一个TAB
         */
        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabHost.setCurrentTab(0);


        /**
         * 第五步 ：监听TAB切换。因为改写了TabHost，使得在APP内切换Fragment的时候，不会重复创建。
         * 所以当在其他地方点击购买商品，切换到购物车页面的时候，没有显示数据，这是因为Fragment没有重复创建，
         * 所以不会调用到更新的方法。所以在这里监听TabHost的切换，当切换到购物车页面的时候，就手动更新数据
         */
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if(tabId == getString(R.string.cart)){
                    //刷新数据
                    refreshData();
                    Log.i("TAG", "MainActivity，切换到购物车页面:");
                }else {
                    //假如是切换到其他Fragment，有关ToolBar的设置需要我们切换回来
                    //先隐藏TitleView
                    mShopToolbar.hideTitleView();
                    //隐藏右边的按钮
                    mShopToolbar.getRightButton().setVisibility(View.GONE);
                    //再显示搜索框
                    mShopToolbar.showSearchView();
                }

            }
        });

    }


    /**
     * 刷新购物车数据
     */
    private void refreshData() {

        //假如购物车Fragment还没有创建，那么就创建一个购物车Fragment，随后刷新数据
        if(cartFragment == null){
            //根据TAG获取一个Fragment实例
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.cart));
            Log.i("TAG", "MainActivity,refreshData: 购物车Fragment还没创建，拿到的fragment是否为空"+fragment);
            if(fragment != null){
                cartFragment = (CartFragment) fragment;
                //刷新购物车数据
                cartFragment.refreshCarData();
                //改变ToolBar状态
                cartFragment.changeToolBarState();
            }

        }else {
            Log.i("TAG", "MainActivity,refreshData: 购物车Fragment已经创建好了");
            //假如购物车Fragment是被创建过了，那么直接刷新数据就好
            cartFragment.refreshCarData();
            //改变ToolBar状态
            cartFragment.changeToolBarState();
        }

    }

    /**
     * 创建每个 TabSpec 中的View
     *
     * @param tab
     * @return
     */
    private View buildIndicator(Tab tab) {
        View view = mInflater.inflate(R.layout.tab_indicator, null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);
        img.setBackgroundResource(tab.getIcon());
        text.setText(tab.getTitle());
        return view;
    }
}
