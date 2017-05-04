package com.shake.easystore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.shake.easystore.fragment.HotFragment;
import com.shake.easystore.fragment.MineFragment;
import com.shake.easystore.weiget.FragmentTabHost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private LayoutInflater mInflater;

    private FragmentTabHost mTabHost;

    //存放 底部元素 bean类的集合
    private List<Tab> mTabs = new ArrayList<>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTab();

    }

    /**
     * 初始化底部菜单
     */
    private void initTab() {

        Tab tab_home = new Tab(R.string.home, R.drawable.selector_icon_home, HomeFragment.class);
        Tab tab_hot = new Tab(R.string.hot, R.drawable.selector_icon_hot, HotFragment.class);
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
