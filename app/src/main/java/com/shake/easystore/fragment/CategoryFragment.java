package com.shake.easystore.fragment;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.Contants;
import com.shake.easystore.R;
import com.shake.easystore.adapter.BaseAdapter;
import com.shake.easystore.adapter.BaseViewHolder;

import com.shake.easystore.adapter.decoration.DividerGridItemDecoration;
import com.shake.easystore.adapter.decoration.DividerItemDecoration;
import com.shake.easystore.bean.Banner;
import com.shake.easystore.bean.CategoryList;
import com.shake.easystore.bean.Page;
import com.shake.easystore.bean.Wares;
import com.shake.easystore.http.BaseCallback;
import com.shake.easystore.http.OkHttpHelper;
import com.shake.easystore.http.SpotsCallBack;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

/**
 * Created by shake on 17-5-2.
 * 商品分类
 */
public class CategoryFragment extends Fragment {


    @ViewInject(R.id.recyclerview_category)
    private RecyclerView mRecyclerview_category;

    @ViewInject(R.id.recyclerview_wares)
    private RecyclerView mRecyclerview_wares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLaout;

    @ViewInject(R.id.slider_category)
    private SliderLayout mSliderLayout;

    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();

    private long category_id=0;
    private int currPage=1;
    private int totalPage=1;
    private int pageSize=10;

    //初始化状态
    private static final int STATE_NORMAL=0;
    //下拉状态
    private  static final int STATE_PULLDOWN=1;
    //上拉状态
    private  static final int STATE_PULLUP=2;
    //当前状态
    private int currentState = STATE_NORMAL;

    //二级菜单的适配器
   // BaseAdapter<Wares> mWaresBaseAdapter;
    //二级菜单的适配器
    BaseAdapter<Wares> mWaresBaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ViewUtils.inject(this, view);

        //获取一级菜单数据
        requestFirstCategoryData();

        //获取广告轮播图的数据
        requestBannerData();

        //初始化下拉刷新控件
        initRefreshLayout();

        return view;
    }


    /**
     * 初始化下拉刷新控件
     */
    private void initRefreshLayout() {
        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                //刷新数据
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if (currPage <= totalPage) {
                    //加载更多数据，不过提供的API的数据很有限，一般只有一页，没得加载更多
                    loadMoreData();
                } else {
                    mRefreshLaout.finishRefreshLoadMore();
                }


            }
        });
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        currPage = ++ currPage;
        currentState = STATE_PULLUP;
        //请求二级菜单数据
        requestSecondCategoryData(category_id);
    }


    /**
     * 刷新数据
     */
    private void refreshData() {
        //显示第一页数据
        currPage = 1;
        currentState = STATE_PULLDOWN;
        //重新请求二级菜单数据
        requestSecondCategoryData(category_id);
    }


    /**
     * 获取广告轮播图的数据
     */
    private void requestBannerData() {

        String url = Contants.API.BANNER + "?type=1";

        mHttpHelper.get(url, new SpotsCallBack<List<Banner>>(this.getContext()) {

            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                //显示广告轮播图数据
                showBannerData(banners);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }


    /**
     * 显示广告轮播图数据
     *
     * @param banners
     */
    private void showBannerData(List<Banner> banners) {

        if(banners != null){
            for (Banner banner : banners) {

                DefaultSliderView sliderView = new DefaultSliderView(this.getActivity());
                sliderView.image(banner.getImgUrl());
                sliderView.description(banner.getName());
                sliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(sliderView);
            }
        }

        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setDuration(3000);

    }

    /**
     * 请求一级菜单数据
     */
    private void requestFirstCategoryData() {

        mHttpHelper.get(Contants.API.CATEGORY_LIST, new SpotsCallBack<List<CategoryList>>(this.getContext()) {

            @Override
            public void onSuccess(Response response, List<CategoryList> categoryLists) {

                //获取一级菜单数据
                showFirstCategoryData(categoryLists);

                if (categoryLists != null && categoryLists.size() > 0) {
                    //请求二级菜单的第一项
                    CategoryList categoryList = categoryLists.get(0);

                    category_id = categoryList.getId();

                    //请求二级菜单
                    requestSecondCategoryData(category_id);
                }


            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }


    /**
     * 请求二级菜单
     * @param category_id
     */
    private void requestSecondCategoryData(long category_id) {

        String url = Contants.API.WARES_LIST+"?categoryId="+category_id+"&curPage="+currPage+"&pageSize="+pageSize;

        mHttpHelper.get(url, new BaseCallback<Page<Wares>>() {
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

                //当前页数
                currPage = waresPage.getCurrentPage();
                //全部页数
                totalPage = waresPage.getTotalPage();

                //显示二级菜单数据
                showSecondCategoryData(waresPage.getList());
            }


            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    /**
     * 显示二级菜单数据
     * @param waresList
     */
    private void showSecondCategoryData(final List<Wares> waresList) {
        switch (currentState){
            case STATE_NORMAL:
                if(mWaresBaseAdapter == null){
                    mWaresBaseAdapter = new BaseAdapter<Wares>(this.getContext(),R.layout.template_grid_wares,waresList) {
                        @Override
                        public void bindData(BaseViewHolder holder, Wares wares, int position) {
                            ImageView imageView = holder.findView(R.id.imageView);
                            TextView textPrice = holder.findView(R.id.text_price);
                            TextView textTitle = holder.findView(R.id.text_title);


                            textPrice.setText("￥"+waresList.get(position).getPrice());
                            textTitle.setText(waresList.get(position).getName());
                            //加载图片
                            Picasso.with(getContext()).load(waresList.get(position).getImgUrl()).into(imageView);
                        }
                    };


                } else {
                    //假如Adapter存在的话，则要先删除数据，再重新添加，否则都是那些数据
                    mWaresBaseAdapter.clearData();
                    mWaresBaseAdapter.addData(waresList);
                }
                break;


            case STATE_PULLDOWN:
                //先清除数据
                mWaresBaseAdapter.clearData();
                //再添加数据
                mWaresBaseAdapter.addData(waresList);
                //滑动到第一位
                mRecyclerview_wares.scrollToPosition(0);
                //停止刷新
                mRefreshLaout.finishRefresh();
                break;


            case STATE_PULLUP :
                //在末尾位置添加数据
                mWaresBaseAdapter.addData(mWaresBaseAdapter.getDatas().size(), waresList);
                mRefreshLaout.finishRefreshLoadMore();
                break;
        }

        /**
         * TODO 这样每次都要去重新设置Adapter，很明显不是明智做法，相当于前面的notify操作都作废了，暂时不知道奔溃原因
         */
        mRecyclerview_wares.setAdapter(mWaresBaseAdapter);
        mRecyclerview_wares.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerview_wares.addItemDecoration(new DividerGridItemDecoration(getContext()));

    }

    /**
     * 显示一级菜单数据
     */
    private void showFirstCategoryData(final List<CategoryList> categoryLists) {

        /**
         * 一级菜单显示数据
         */
        BaseAdapter<CategoryList> adapter = new BaseAdapter<CategoryList>(this.getContext(), R.layout.template_single_text, categoryLists) {
            @Override
            public void bindData(BaseViewHolder holder, CategoryList categoryList, int position) {
                TextView textView = holder.findView(R.id.textView);
                textView.setText(categoryList.getName());
            }
        };
        mRecyclerview_category.setAdapter(adapter);
        mRecyclerview_category.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerview_category.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));


        /**
         * 设置一级菜单的点击事件，对应请求二级菜单的数据
         */
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoryList categoryList = categoryLists.get(position);

                category_id = categoryList.getId();
                currPage=1;
                currentState=STATE_NORMAL;


                //请求二级菜单的数据
                requestSecondCategoryData(category_id);

            }
        });

    }
}
