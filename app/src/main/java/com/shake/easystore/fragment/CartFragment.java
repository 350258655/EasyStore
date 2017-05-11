package com.shake.easystore.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.MainActivity;
import com.shake.easystore.R;
import com.shake.easystore.adapter.BaseAdapter;
import com.shake.easystore.adapter.CartAdapter;
import com.shake.easystore.adapter.decoration.DividerItemDecoration;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.utils.CartProvider;
import com.shake.easystore.weiget.ShopToolbar;

import java.util.List;

/**
 * Created by shake on 17-5-2.
 * 购物车Fragment
 */
public class CartFragment extends Fragment {

    @ViewInject(R.id.recyclerview_cart)
    private RecyclerView mRecyclerViewCart;

    @ViewInject(R.id.checkbox_all)
    private CheckBox mCheckBoxAll;

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

    //ToolBar
    private ShopToolbar mShopToolbar;

    //购物车在编辑状态
    public static final String ACTION_EDIT = "edit";
    //购物车在完成状态
    public static final String ACTION_CAMPLATE = "complete";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        ViewUtils.inject(this, view);

        mCartProvider = CartProvider.getInstance(getContext());

        //初始化数据
        initData();

        //初始化事件
        initEvent();

        return view;
    }

    /**
     * 显示数据
     */
    private void initData() {
        //获取购物车数据
        List<ShoppingCart> carts = mCartProvider.getAllData();

        //初始化Adapter
        mCartAdapter = new CartAdapter(getContext(), carts);

        //设置RecyclerView的状态
        mRecyclerViewCart.setAdapter(mCartAdapter);
        mRecyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewCart.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        //Adapter中的每一项要默认全选的
        mCartAdapter.checkAllOrNull(true);

        //下方显示总价格
        showTotalPrice();

    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //设置CheckBox的点击事件
        mCheckBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变购物车上每一项的选中状态
                mCartAdapter.checkAllOrNull(mCheckBoxAll.isChecked());
                //获取并显示合计的价格
                showTotalPrice();
            }
        });


        //设置Adapter的每个Item点击事件
        mCartAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //触发后的行为，是选中，还是未选中
                mCartAdapter.itemClickAction(position);
                //改变合计价格
                showTotalPrice();
                //获取被选中项目的数量
                int checkNum = mCartAdapter.getCheckNum();
                //根据被选中项目的数量，来确定全选按钮要不要选中
                if (checkNum == mCartAdapter.getItemCount()) {
                    mCheckBoxAll.setChecked(true);
                } else {
                    mCheckBoxAll.setChecked(false);
                }

            }
        });



        //设置Adapter中每一项数量的监听回调
        mCartAdapter.setOnItemNumChangeListener(new CartAdapter.OnItemNumChangeListener() {
            @Override
            public void onResult(int value,ShoppingCart cart) {
                //更新数据到内存中
                mCartProvider.upDate(cart);
                //更新合计价格
                showTotalPrice();
            }
        });


        //删除按钮的监听事件
        mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用Adapter中的删除方法
                mCartAdapter.deleteCard();
                //删除完成后，Adapter中的全部项目要默认选中，因为在编辑状态，所有Item是默认不选中的。
                //现在删除完成回来，要更新回来每个Item都选择的状态
                //mCartAdapter.checkAllOrNull(true);
                //更新合计价格
                showTotalPrice();
            }
        });

    }


    /**
     * 显示购物车合计价格
     */
    private void showTotalPrice() {
        float totalPrice = mCartAdapter.getTotalPrice();
        mTextTotal.setText(Html.fromHtml("合计 ￥<span style='color:#eb4f38'>" + totalPrice + "</span>"), TextView.BufferType.SPANNABLE);
    }


    /**
     * 刷新购物车数据
     */
    public void refreshCarData() {
        //先清空Adapter数据
        mCartAdapter.clearData();
        //重新获取数据
        List<ShoppingCart> datas = mCartProvider.getAllData();

        Log.i("TAG", "CartFragment,刷新数据量: " + mCartProvider.getAllData().size());
        //重新往Apdater添加数据
        mCartAdapter.addData(datas);
        //下方显示总价格
        showTotalPrice();
    }


    /**
     * 更换ToolBar。因为我们的ToolBar是写在MainActivity中的，所以我们在这个方法中获取MainActivity的引用
     * 然后根据这个引用，去调用ToolBar的方法，改变ToolBar的状态
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            //获取ToolBar对象
            mShopToolbar = (ShopToolbar) activity.findViewById(R.id.shopToolbar);
            //改变ToolBar状态
            changeToolBarState();
        }

    }

    /**
     * 改变ToolBar状态，其实就是设置一些属性
     */
    public void changeToolBarState() {
        //隐藏搜索框
        mShopToolbar.hideSearchView();
        //显示ToolBar标题
        mShopToolbar.showTitleView();
        //设置标题
        mShopToolbar.setTitle(R.string.cart);
        //设置右边按钮为可见状态
        mShopToolbar.getRightButton().setVisibility(View.VISIBLE);
        //设置右边按钮的文字
        mShopToolbar.setRightButtonText("编辑");
        //设置右边按钮的状态
        mShopToolbar.getRightButton().setTag(ACTION_EDIT);
        //设置右边按钮的点击事件
        mShopToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //假如是在编辑状态
                if (v.getTag() == ACTION_EDIT) {
                    //显示编辑状态的界面
                    showEditAction();
                } else if (v.getTag() == ACTION_CAMPLATE) {
                    //假如是在完成状态
                    showComplateAction();
                }


            }
        });
    }

    /**
     * 显示完成状态的界面
     */
    private void showComplateAction() {
        //ToolBar按钮上的名字要改变
        mShopToolbar.setRightButtonText("编辑");
        //合计的View要显示
        mTextTotal.setVisibility(View.VISIBLE);
        //去结算的按钮要显示
        mBtnOrder.setVisibility(View.VISIBLE);
        //删除的按钮要消失
        mBtnDel.setVisibility(View.GONE);
        //设置当前状态
        mShopToolbar.getRightButton().setTag(ACTION_EDIT);
        //合计的CheckBox默认选中
        mCheckBoxAll.setChecked(true);
        //适配器中每一项也默认选中
        mCartAdapter.checkAllOrNull(true);
        //显示合计的价格
        showTotalPrice();
    }

    /**
     * 显示编辑状态的界面
     */
    private void showEditAction() {
        //ToolBar按钮上的名字要改变
        mShopToolbar.setRightButtonText("完成");
        //合计的View要消失
        mTextTotal.setVisibility(View.GONE);
        //去结算的按钮要小时
        mBtnOrder.setVisibility(View.GONE);
        //删除的按钮要显示
        mBtnDel.setVisibility(View.VISIBLE);
        //设置当前状态
        mShopToolbar.getRightButton().setTag(ACTION_CAMPLATE);
        //合计的CheckBox默认不选中
        mCheckBoxAll.setChecked(false);
        //适配器中每一项也默认不选中
        mCartAdapter.checkAllOrNull(false);
    }
}
