package com.shake.easystore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.shake.easystore.Contants;
import com.shake.easystore.R;
import com.shake.easystore.WareListActivity;
import com.shake.easystore.adapter.HomeCategoryAdapter;
import com.shake.easystore.adapter.decoration.CardViewtemDecortion;
import com.shake.easystore.bean.Banner;
import com.shake.easystore.bean.Campaign;
import com.shake.easystore.bean.HomeCampaign;
import com.shake.easystore.http.BaseCallback;
import com.shake.easystore.http.OkHttpHelper;
import com.shake.easystore.http.SpotsCallBack;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;


/**
 * Created by shake on 17-5-2.
 */
public class HomeFragment extends Fragment {

    private SliderLayout mSliderLayout;

    private RecyclerView mRecyclerView;

    private HomeCategoryAdapter mAdapter;


    private Gson mGson = new Gson();

    private List<Banner> mBanners;

    //获取OkHttpHelper实例
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /**
         * 轮播图初始化第一步，找到SliderLayout
         */
        mSliderLayout = (SliderLayout) view.findViewById(R.id.slider);


        //下载轮播图的数据
        requestImages();


        //初始化RecycleView
        initRecycleView(view);


        return view;
    }


    private void requestImages() {
        String url = "http://112.124.22.238:8081/course_api/banner/query?type=1";

        httpHelper.get(url, new SpotsCallBack<List<Banner>>(this.getContext()) {

            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                mBanners = banners;
                initSlider();

            }

            @Override
            public void onError(Response response, int code, Exception e) {
            }
        });

    }

    /**
     * 初始化RecycleView
     */
    private void initRecycleView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_home);

        httpHelper.get(Contants.API.CAMPAIGN_HOME, new BaseCallback<List<HomeCampaign>>() {
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
            public void onSuccess(Response response, List<HomeCampaign> homeCampaigns) {
                loadRecycleViewDatas(homeCampaigns);
            }


            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });


    }

    /**
     * 装载数据到RecycleViews中
     *
     * @param homeCampaigns
     */
    private void loadRecycleViewDatas(final List<HomeCampaign> homeCampaigns) {

        /**
         * 废弃原本传统的做法
         */
        mAdapter = new HomeCategoryAdapter(homeCampaigns, this.getContext());

        //设置监听事件，点击进入到商品列表
        mAdapter.setOnCampClickListener(new HomeCategoryAdapter.OnCampClickListener() {
            @Override
            public void onClick(View view, Campaign campaign) {

                Intent intent = new Intent(getContext(), WareListActivity.class);
                intent.putExtra(Contants.COMPAINGAIN_ID, campaign.getId());
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new CardViewtemDecortion());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

    }

    /**
     * 初始化轮播图
     */
    private void initSlider() {

        /**
         * 修改 轮播图初始化第二步：循环往 SliderLayout 中添加 TextSliderView
         */
        if (mBanners != null) {
            for (Banner banner : mBanners) {
                TextSliderView textSliderView = new TextSliderView(this.getActivity());
                textSliderView.image(banner.getImgUrl());
                textSliderView.description(banner.getName());
                mSliderLayout.addSlider(textSliderView);
            }
        }


        /**
         * 修改 轮播图初始化第三步，设置各种效果以及监听事件
         */
        //设置转场时间
        mSliderLayout.setDuration(3000);
        //设置转场动画
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());

    }


}
