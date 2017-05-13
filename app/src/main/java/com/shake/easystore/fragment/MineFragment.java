package com.shake.easystore.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.shake.easystore.Contants;
import com.shake.easystore.LoginActivity;
import com.shake.easystore.MainActivity;
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
     * 隐藏搜索框
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            ShopToolbar toolbar = (ShopToolbar) mainActivity.findViewById(R.id.shopToolbar);
            toolbar.hideSearchView();
        }

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
            mTxtUserName.setText("点击登录");
            btn_logout.setVisibility(View.GONE);
        }
    }
}
