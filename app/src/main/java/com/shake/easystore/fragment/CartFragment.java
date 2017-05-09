package com.shake.easystore.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.R;
import com.shake.easystore.adapter.CartAdapter;
import com.shake.easystore.adapter.decoration.DividerItemDecoration;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.utils.CartProvider;

import java.util.List;

/**
 * Created by shake on 17-5-2.
 * 购物车Fragment
 */
public class CartFragment extends Fragment {

    @ViewInject(R.id.recyclerview_cart)
    private RecyclerView mRecyclerViewCart;

    @ViewInject(R.id.checkbox_all)
    private CheckBox mCheckBox;

    @ViewInject(R.id.text_total)
    private TextView mTextTotal;

    @ViewInject(R.id.btn_order)
    private Button mBtnOrder;

    @ViewInject(R.id.btn_del)
    private Button mBtnDel;

    //存储工具类
    private CartProvider mCartProvider;

    //适配器
    private CartAdapter mCartAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart,container,false);

        ViewUtils.inject(this, view);

        mCartProvider = new CartProvider(getContext());

        showData();

        return view;
    }

    private void showData() {
        //获取购物车数据
        List<ShoppingCart> carts = mCartProvider.getAll();

        //初始化Adapter
        mCartAdapter = new CartAdapter(getContext(),carts);

        //设置RecyclerView的状态
        mRecyclerViewCart.setAdapter(mCartAdapter);
        mRecyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewCart.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }
}
