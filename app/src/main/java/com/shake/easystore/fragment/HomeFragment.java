package com.shake.easystore.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.shake.easystore.R;

/**
 * Created by shake on 17-5-2.
 */
public class HomeFragment extends Fragment {

    private SliderLayout mSliderLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        /**
         * 第一步，找到SliderLayout
         */
        mSliderLayout = (SliderLayout) view.findViewById(R.id.slider);

        //初始化轮播图
        initSlider();

        return view;
    }

    /**
     * 初始化轮播图
     */
    private void initSlider() {

        /**
         * 第二步，创建 TextSliderView
         */
        TextSliderView textSliderView1 = new TextSliderView(this.getActivity());
        textSliderView1.image("http://m.360buyimg.com/mobilecms/s300x98_jfs/t2416/102/20949846/13425/a3027ebc/55e6d1b9Ne6fd6d8f.jpg");
        textSliderView1.description("新品推荐");

        TextSliderView textSliderView2 = new TextSliderView(this.getActivity());
        textSliderView2.image("http://m.360buyimg.com/mobilecms/s300x98_jfs/t1507/64/486775407/55927/d72d78cb/558d2fbaNb3c2f349.jpg");
        textSliderView2.description("时尚男装");

        TextSliderView textSliderView3 = new TextSliderView(this.getActivity());
        textSliderView3.image("http://m.360buyimg.com/mobilecms/s300x98_jfs/t1363/77/1381395719/60705/ce91ad5c/55dd271aN49efd216.jpg");
        textSliderView3.description("家电秒杀");

        /**
         * 第三步，将 TextSliderView 添加到 SliderLayout 中
         */
        mSliderLayout.addSlider(textSliderView1);
        mSliderLayout.addSlider(textSliderView2);
        mSliderLayout.addSlider(textSliderView3);


        /**
         * 第四步，设置各种效果以及监听事件
         */
        //设置转场时间
        mSliderLayout.setDuration(3000);
        //设置转场动画
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        textSliderView1.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(HomeFragment.this.getActivity(), "新品推荐", Toast.LENGTH_SHORT).show();
            }
        });

        textSliderView2.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(HomeFragment.this.getActivity(), "时尚男装", Toast.LENGTH_SHORT).show();
            }
        });

        textSliderView3.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(HomeFragment.this.getActivity(), "家电秒杀", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
