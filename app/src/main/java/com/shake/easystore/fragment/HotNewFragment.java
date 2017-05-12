package com.shake.easystore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.Contants;
import com.shake.easystore.R;
import com.shake.easystore.WareDetailActivity;
import com.shake.easystore.adapter.BaseAdapter;
import com.shake.easystore.adapter.HotspotAdapter;
import com.shake.easystore.bean.Page;
import com.shake.easystore.bean.Wares;
import com.shake.easystore.utils.Pager;

import java.util.List;

/**
 * Created by shake on 17-5-11.
 * 这是经过优化的Fragment
 */
public class HotNewFragment extends Fragment implements Pager.OnPageListener<Wares> {
    @ViewInject(R.id.recyclerview_hot)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout mRefreshLaout;

    //适配器
    private HotspotAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        ViewUtils.inject(this, view);

        initPager();

        return view;
    }

    /**
     * 初始化pager并且请求数据
     */
    private void initPager() {

        Pager pager = Pager.newBuilder()
                .setUrl(Contants.API.WARES_HOT)
                .setLoadMore(true)
                .setOnPageListener(this)
                .setPageSize(20)
                .setRefreshLayout(mRefreshLaout)
                .build(getContext(), new TypeToken<Page<Wares>>() {
                }.getType());

        pager.request();
    }

    @Override
    public void load(List<Wares> datas, int totalPage, int totalCount) {

        if(mAdapter == null){
            mAdapter = new HotspotAdapter(getContext(),datas);

            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            //设置点击事件
            mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //去详情页面
                    goDetail(position);
                }
            });

        }

    }

    /**
     * 跳转到详情页面
     */
    private void goDetail(int position) {
        Wares wares = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), WareDetailActivity.class);
        intent.putExtra(Contants.WARE,wares);
        startActivity(intent);
    }

    @Override
    public void refresh(List<Wares> datas, int totalPage, int totalCount) {
        //调用Adapter的刷新数据方法
        mAdapter.refreshData(datas);
        //滑动到第一位
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Wares> datas, int totalPage, int totalCount) {
        //调用Adapter的加载更多数据的方法
        mAdapter.loadMoreData(datas);
    }
}
