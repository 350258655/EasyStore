package com.shake.easystore.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.Contants;
import com.shake.easystore.R;
import com.shake.easystore.adapter.BaseAdapter;
import com.shake.easystore.adapter.BaseViewHolder;
import com.shake.easystore.adapter.decoration.DividerItemDecoration;
import com.shake.easystore.bean.Page;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.bean.Wares;
import com.shake.easystore.http.BaseCallback;
import com.shake.easystore.http.OkHttpHelper;
import com.shake.easystore.utils.CartProvider;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

/**
 * Created by shake on 17-5-2.
 *
 * 这是还没有经过优化的HotFragment
 *
 * <p/>
 * 热点Fragment
 * <p/>
 * 本Fragment根据 MaterialRefreshLayout 可区分为三种状态：正常状态，下拉刷新状态，上拉加载状态
 * <p/>
 * 正常状态：刚进入本页，会去请求数据，然后加载在 RecyclerView 上
 * <p/>
 * 下拉刷新状态 ：当下拉刷新的时候，先请求数据，然后删除 RecyclerView 上原来的数据，再把新数据添加进去
 * <p/>
 * 上拉加载状态 ： 当上拉加载更多数据的时候，在原来的位置上添加更多的数据进去
 */
public class HotFragment extends Fragment {


    @ViewInject(R.id.recyclerview_hot)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout mRefreshLayout;

    private int currPage = 1;
    private int totalPage = 1;
    private int pageSize = 10;

    //初始化状态
    private static final int STATE_NORMAL = 0;
    //下拉状态
    private static final int STATE_PULLDOWN = 1;
    //上拉状态
    private static final int STATE_PULLUP = 2;
    //当前状态
    private int currentState = STATE_NORMAL;

    //装载请求数据的容器
    List<Wares> mWares;

    //存储商品信息的工具类
    CartProvider provider;


    private BaseAdapter<Wares> adapter;

    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hot, container, false);

        // 用XUtils绑定
        ViewUtils.inject(this, view);

        //初始化工具类
        provider = CartProvider.getInstance(getContext());

        //初始化RecycleView
        initRecycleView();

        //初始化下拉刷新组件
        initRefreshLayout();

        //加载数据到RecycleView上
        requestData();

        return view;
    }

    /**
     * 初始化下拉刷新组件
     */
    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(true);

        /**
         * 下拉刷新：假如在同一个position，后台返回的数据有更新了，那么下拉刷新的含义就是删除原来的数据，在相同位置替换上新的数据
         * 上拉加载 ：就是在尾部添加更多的数据
         */
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                //当前属于下拉刷新状态
                currentState = STATE_PULLDOWN;
                //下拉刷新之后，要在第一页
                currPage = 1;
                //下拉刷新完成请求数据
                requestData();

            }

            //加载更多数据的回调
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if (currPage <= totalPage) {
                    currPage++;
                    //加载更多
                    currentState = STATE_PULLUP;
                    //请求网络数据
                    requestData();

                } else {
                    mRefreshLayout.finishRefreshLoadMore();
                    Toast.makeText(HotFragment.this.getContext(), "没有更多数据了!", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    /**
     * 初始化RecycleView的操作
     */
    private void initRecycleView() {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 加载数据到RecycleView中
     */
    private void requestData() {
        String url = Contants.API.WARES_HOT + "?curPage=" + currPage + "&pageSize=" + pageSize;

        httpHelper.get(url, new BaseCallback<Page<Wares>>() {

            @Override
            public void onRequestBefore(Request request) {
            }

            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) {
            }

            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {
                //获取到该页的数据
                mWares = waresPage.getList();
                currPage = waresPage.getCurrentPage();
                totalPage = waresPage.getTotalPage();

                loadRecycleViewDatas();
            }

            @Override
            public void onError(Response response, int code, Exception e) {
            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });

    }

    /**
     * 将根据请求数据返回的结果的不同状态，将数据装载在RecycleView上
     */
    private void loadRecycleViewDatas() {

        if (adapter == null) {
            adapter = new BaseAdapter<Wares>(this.getContext(), R.layout.template_hot_wares, mWares) {
                @Override
                public void bindData(BaseViewHolder holder, final Wares wares, int position) {
                    //找到View
                    TextView textTitle = holder.findView(R.id.text_title);
                    TextView textPrice = holder.findView(R.id.text_price);
                    ImageView mImageView = holder.findView(R.id.drawee_view);
                    Button btn_tobuy = holder.findView(R.id.btn_tobuy);

                    //给View设置值
                    textTitle.setText(wares.getName());
                    textPrice.setText("￥" + wares.getPrice());
                    //用Picasso加载图片
                    Picasso.with(getContext()).load(wares.getImgUrl()).into(mImageView);

                    //设置点击事件
                    btn_tobuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //获取购物车数据
                            ShoppingCart cart = getShoppingCartData(wares);
                            provider.put(cart);
                            Toast.makeText(getContext(), "已添加到购物车", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            };

            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //Toast.makeText(getContext(), "哪个位置被点击了:" + position, Toast.LENGTH_SHORT).show();
                }
            });

        }


        switch (currentState) {
            case STATE_NORMAL:
                mRecyclerView.setAdapter(adapter);
                break;

            //下拉刷新状态：删除原来的数据，替换新的数据
            case STATE_PULLDOWN:
                //先清除数据
                adapter.clearData();
                //再添加数据
                adapter.addData(mWares);
                //滑动到第一位
                mRecyclerView.scrollToPosition(0);
                //结束转动效果
                mRefreshLayout.finishRefresh();
                break;

            //加载更多数据
            case STATE_PULLUP:
                //当前位置
                //将数据添加到特定位置上
                adapter.addData(adapter.getDatas().size(), mWares);
                //滑动到特定位置
                //结束加载
                mRefreshLayout.finishRefreshLoadMore();
                break;
        }

    }

    /**
     * 获取ShoppingCart。因为父类没法强转成子类，所以只能一个个set
     *
     * @param wares
     * @return
     */
    public ShoppingCart getShoppingCartData(Wares wares) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(wares.getId());
        cart.setDescription(wares.getDescription());
        cart.setImgUrl(wares.getImgUrl());
        cart.setName(wares.getName());
        cart.setPrice(wares.getPrice());
        return cart;
    }


}
