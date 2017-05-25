package com.shake.easystore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.shake.easystore.AddressListActivity;
import com.shake.easystore.Contants;
import com.shake.easystore.LoginActivity;
import com.shake.easystore.MyOrderActivity;
import com.shake.easystore.R;
import com.shake.easystore.bean.User;
import com.shake.easystore.utils.UserLocalData;
import com.shake.easystore.weiget.ShopToolbar;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shake on 17-5-2.
 * 个人中心 Fragment
 */
public class MineFragment extends BaseFragment {

    @ViewInject(R.id.img_head)
    private CircleImageView mCircleImageView;

    @ViewInject(R.id.txt_username)
    private TextView mTxtUserName;

    @ViewInject(R.id.btn_logout)
    private Button btn_logout;


    private ShopToolbar mShopToolbar;


    /**
     * //刚进入页面的时候，也需要去查找是否有用户信息
     */
    @Override
    protected void init() {
        User user = UserLocalData.getUser();
        showUser(user);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }



    /**
     * 点击头像，和点击"点击登录"文字的时候，去登录
     *
     * @param view
     */
    @OnClick(value = {R.id.img_head, R.id.txt_username})
    public void toLogin(View view) {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivityForResult(intent, Contants.REQUEST_CODE);
    }


    /**
     * 点击收货地址，去到显示收货地址的列表
     *
     * @param view
     */
    @OnClick(R.id.txt_my_address)
    public void toAddressActivity(View view) {
        startActivity(new Intent(getContext(), AddressListActivity.class), true);
    }
    /**
     * 点击我的订单，去到我的订单页面
     *
     * @param view
     */
    @OnClick(R.id.txt_my_orders)
    public void toMyOrderActivity(View view) {
        startActivity(new Intent(getContext(), MyOrderActivity.class),true);
    }



    /**
     * 退出登录
     *
     * @param view
     */
    @OnClick(R.id.btn_logout)
    public void toLoout(View view) {
        //先清除Token数据
        UserLocalData.clearUserAndToken();
        //再重新显示界面
        showUser(null);
    }




    /**
     * 登录完成回来，显示登录的用户信息
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取用户数据
        User user = UserLocalData.getUser();
        if(user == null){
            Toast.makeText(getContext(), "登录失败", Toast.LENGTH_SHORT).show();
        }
        //显示用户信息
        showUser(user);
    }


    /**
     * 显示用户信息，并且控制 "退出登录" 按钮的隐藏与显示
     *
     * @param user
     */
    private void showUser(User user) {
        if (user != null) {
            mTxtUserName.setText(user.getUsername());
            Picasso.with(getContext()).load(user.getLogo_url()).into(mCircleImageView);
            btn_logout.setVisibility(View.VISIBLE);
        } else {
            //未登录的状态
            mTxtUserName.setText("点击登录");
            mCircleImageView.setImageResource(R.drawable.default_head);
            btn_logout.setVisibility(View.GONE);
        }
    }
}
