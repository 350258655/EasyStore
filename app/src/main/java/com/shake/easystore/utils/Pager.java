package com.shake.easystore.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.shake.easystore.bean.Page;
import com.shake.easystore.http.OkHttpHelper;
import com.shake.easystore.http.SpotsCallBack;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shake on 17-5-10.
 * TODO 改天再来看看这个Builder设计模式具体是怎么搞的
 * <p/>
 * 分页的工具类，即封装了加载数据，上拉加载更多，下拉刷新数据等
 * 不动的东西，则放在Pager中。
 * 第一，Pager的构造方法中，会去初始化OkHttpHelper和RefreshLayout
 * 第二，外面调用构建Builder，并且用Builder设置好一些相关配置，并且构建出Pager实例
 * 第三，外部调用Pager的requestData方法，然后在Pager内部，请求完数据之后就会对数据进行展示
 * 第四，根据三种状态显示数据，并且把状态回调给外部监听
 */
public class Pager {

    private OkHttpHelper httpHelper;

    //初始化状态
    private static final int STATE_NORMAL = 0;
    //下拉状态
    private static final int STATE_PULLDOWN = 1;
    //上拉状态
    private static final int STATE_PULLUP = 2;
    //当前状态
    private int currentState = STATE_NORMAL;


    private static Builder builder;


    private Pager() {
        //1、创建OkHttpHelper
        httpHelper = OkHttpHelper.getInstance();
        //2、初始化RefreshLayout
        initRefreshLayout();
    }

    /**
     * 提供给外面的，添加参数用的
     *
     * @param key
     * @param value
     */
    public void addParam(String key, Object value) {
        builder.params.put(key, value);
    }


    /**
     * 请求数据
     */
    public void request() {
        requestData();
    }


    /**
     * 请求数据，内部调用
     */
    private void requestData() {
        //获取URL
        String url = buildUrl();
        //请求网络
        httpHelper.get(url, new RequestCallBack(builder.mContext));
    }

    /**
     * 构建URL
     *
     * @return
     */
    private String buildUrl() {
        return builder.url + "?" + buildUrlParams();
    }

    /**
     * 构建Url的参数，其实就是构建curPage和pageSize两个参数
     *
     * @return
     */
    private String buildUrlParams() {

        HashMap<String, Object> map = builder.params;

        map.put("curPage", builder.pageIndex);
        map.put("pageSize", builder.pageSize);


        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }


    /**
     * 初始化下拉刷新组件
     */
    private void initRefreshLayout() {
        builder.mRefreshLayout.setLoadMore(builder.canLoadMore);

        /**
         * 下拉刷新：假如在同一个position，后台返回的数据有更新了，那么下拉刷新的含义就是删除原来的数据，在相同位置替换上新的数据
         * 上拉加载 ：就是在尾部添加更多的数据
         */
        builder.mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                //当前属于下拉刷新状态
                currentState = STATE_PULLDOWN;
                //下拉刷新之后，要在第一页
                builder.pageIndex = 1;
                //下拉刷新完成请求数据
                requestData();

            }

            //加载更多数据的回调
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (builder.pageIndex <= builder.totalPage) {
                    builder.pageIndex++;
                    //加载更多
                    currentState = STATE_PULLUP;
                    //请求网络数据
                    requestData();
                } else {
                    builder.mRefreshLayout.finishRefreshLoadMore();
                    Toast.makeText(builder.mContext, "没有更多数据了!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    /**
     * 显示数据
     *
     * @param datas
     * @param totalPage
     * @param totalCount
     * @param <T>
     */
    private <T> void showData(List<T> datas, int totalPage, int totalCount) {
        if (datas == null || datas.size() <= 0) {
            Toast.makeText(builder.mContext, "加载不到数据", Toast.LENGTH_LONG).show();
            return;
        }

        /**
         * 根据这三种状态对应不同的去显示数据
         */
        switch (currentState) {
            case STATE_NORMAL:

                if (builder.mOnPageListener != null) {
                    builder.mOnPageListener.load(datas, totalPage, totalCount);
                }

                break;
            case STATE_PULLDOWN:

                builder.mRefreshLayout.finishRefresh();
                if (builder.mOnPageListener != null) {
                    builder.mOnPageListener.refresh(datas, totalPage, totalCount);
                }

                break;
            case STATE_PULLUP:

                builder.mRefreshLayout.finishRefreshLoadMore();
                if (builder.mOnPageListener != null) {
                    builder.mOnPageListener.loadMore(datas, totalPage, totalCount);
                }

                break;

        }
    }


    /**
     * 创建一个Builder
     *
     * @return
     */
    public static Builder newBuilder() {
        builder = new Builder();
        return builder;
    }


    class RequestCallBack<T> extends SpotsCallBack<Page<T>> {

        public RequestCallBack(Context context) {
            super(context);
            super.mType = builder.mType;
        }

        @Override
        public void onFailure(Request request, IOException e) {
            dismissDialog();
            Toast.makeText(builder.mContext, "请求出错：" + e.getMessage(), Toast.LENGTH_LONG).show();
            failOrError();

        }

        @Override
        public void onSuccess(Response response, Page<T> page) {
            builder.pageIndex = page.getCurrentPage();
            builder.pageSize = page.getPageSize();
            builder.totalPage = page.getTotalPage();

            Log.i("TAG", "请求成功的数据: "+page.getList().size());

            //请求成功则显示数据
            showData(page.getList(), page.getTotalPage(), page.getTotalCount());

        }

        @Override
        public void onError(Response response, int code, Exception e) {
            Toast.makeText(builder.mContext, "加载数据失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            failOrError();
        }


        /**
         * 请求错误或者加载失败应该执行的
         */
        public void failOrError() {
            //假如是下拉刷新状态，或者上拉加载更多状态，就需要停止
            if (STATE_PULLDOWN == currentState) {
                builder.mRefreshLayout.finishRefresh();
            } else if (STATE_PULLUP == currentState) {
                builder.mRefreshLayout.finishRefreshLoadMore();
            }
        }

    }


    /**
     * 会变动的东西，需要外面穿参数进来。放在Builder中
     */
    public static class Builder {

        private Context mContext;
        private Type mType;
        private String url;
        private MaterialRefreshLayout mRefreshLayout;
        private boolean canLoadMore;

        private int totalPage = 1;
        private int pageIndex = 1;
        private int pageSize = 10;

        //用于装载参数
        private HashMap<String, Object> params = new HashMap<>(5);

        //回调接口实例
        private OnPageListener mOnPageListener;


        /**
         * 设置URL
         *
         * @param url
         * @return
         */
        public Builder setUrl(String url) {
            this.url = url;
            return builder;
        }

        /**
         * 设置每页的数据
         *
         * @param pageSize
         * @return
         */
        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return builder;
        }

        /**
         * 设置参数
         *
         * @param key
         * @param value
         * @return
         */
        public Builder putParams(String key, Object value) {
            params.put(key, value);
            return builder;
        }

        /**
         * 设置是否可以加载更多
         *
         * @param loadMore
         * @return
         */
        public Builder setLoadMore(boolean loadMore) {
            this.canLoadMore = loadMore;
            return builder;
        }

        /**
         * 设置MaterialRefreshLayout
         *
         * @param refreshLayout
         * @return
         */
        public Builder setRefreshLayout(MaterialRefreshLayout refreshLayout) {
            this.mRefreshLayout = refreshLayout;
            return builder;
        }

        /**
         * 设置监听器
         *
         * @param onPageListener
         * @return
         */
        public Builder setOnPageListener(OnPageListener onPageListener) {
            this.mOnPageListener = onPageListener;
            return builder;
        }

        /**
         * 构建Pager对象
         *
         * @param context
         * @param type
         * @return
         */
        public Pager build(Context context, Type type) {
            this.mType = type;
            this.mContext = context;
            //验证是否可以创建Pager对象
            verifyPager();
            return new Pager();
        }

        /**
         * 需要验证是否传入了必传的参数
         */
        private void verifyPager() {
            if (this.mContext == null)
                throw new RuntimeException("content can't be null");

            if (this.url == null || "".equals(this.url))
                throw new RuntimeException("url can't be  null");

            if (this.mRefreshLayout == null)
                throw new RuntimeException("MaterialRefreshLayout can't be  null");
        }

    }


    /**
     * 监听回调
     *
     * @param <T>
     */
    public interface OnPageListener<T> {
        void load(List<T> datas, int totalPage, int totalCount);

        void refresh(List<T> datas, int totalPage, int totalCount);

        void loadMore(List<T> datas, int totalPage, int totalCount);
    }


}
