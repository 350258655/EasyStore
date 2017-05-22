package com.shake.easystore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.shake.easystore.adapter.AddressAdapter;
import com.shake.easystore.adapter.decoration.DividerItemDecoration;
import com.shake.easystore.bean.Address;
import com.shake.easystore.db.Dao;
import com.shake.easystore.http.OkHttpHelper;
import com.shake.easystore.weiget.ShopToolbar;

import java.util.List;


public class AddressListActivity extends BaseActivity {

    @ViewInject(R.id.toolbar)
    private ShopToolbar mToolBar;

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerview;

    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();


    private AddressAdapter mAdapter;

    private List<Address> addressDatas;

    private Dao mDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ViewUtils.inject(this);

        initToolbarEvent();

        //初始化地址
        initAddress();

        //初始化编辑和删除事件
        initAddressEvent();

    }

    /**
     * 初始化删除和编辑事件
     */
    private void initAddressEvent() {

        //点击编辑收货地址
        mAdapter.setEditOnClickListener(new AddressAdapter.EditOnClickListener() {
            @Override
            public void onClick(Address address, int position) {

                Intent intent = new Intent(AddressListActivity.this,AddressAddActivity.class);
                //把地址带过来
                Bundle bundle = new Bundle();
                bundle.putSerializable(Contants.ADDRESS, address);
                intent.putExtra(Contants.BUNDLE_ADDRESS, bundle);

                //跳转到编辑的Activity
                startActivityForResult(intent,Contants.REQUEST_EDIT_CODE);

            }
        });


        //点击删除按钮
        mAdapter.setDeleteOnClickListener(new AddressAdapter.DeleteOnClickListener() {
            @Override
            public void onClick(Address address, int position) {
                //初始化视图，去重新设置一个默认的地址
                initAddress();
                //同时也要重新设置监听事件,否则会发生空指针异常
                initAddressEvent();
            }
        });

    }

    /**
     * 初始化地址。没有使用菜鸟窝接口的权限了。。。只能在本地写了
     *
     * TODO 从数据库中获取数据是一个耗时的操作
     */
    private void initAddress() {

        //构建参数，从数据库获取
        mDao = Dao.getDao(AddressListActivity.this);

        //查询数据
        mDao.query(new Dao.OnCompleteListener() {
            @Override
            public void onDone(List<Address> list) {

                //显示数据
                showAddress(list);
            }
        });


    }


    /**
     * 显示收货地址
     *
     * @param listDatas
     */
    private void showAddress(final List<Address> listDatas) {


        /**
         * 设置第一位是默认选择的
         */
        for (int i = 0; i < listDatas.size(); i++) {
            if(i == 0){
                listDatas.get(i).setIsDefault(true);
            }else {
                listDatas.get(i).setIsDefault(false);
            }
        }


        /**
         * TODO 这里暂时这么处理，牺牲性能换来暂时的安全，刷新数据总是会出错
         */
        mAdapter = new AddressAdapter(this,listDatas);

        mAdapter.setDefaultAddressListener(new AddressAdapter.DefaultAddressListener() {
            @Override
            public void setDefault(Address address) {

                //更新收货地址
                upDateAddress(listDatas,address);

            }
        });

        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

//        if(mAdapter == null){
//            mAdapter = new AddressAdapter(this,listDatas);
//
//            mAdapter.setDefaultAddressListener(new AddressAdapter.DefaultAddressListener() {
//                @Override
//                public void setDefault(Address address) {
//
//                }
//            });
//
//            mRecyclerview.setAdapter(mAdapter);
//            mRecyclerview.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
//            mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
//
//        }else {
//            //假如不为空的话，就刷新数据之后再重新设置Adapter
//            Log.i("TAG", "AddressListActivity,showAddress: 集合的长度：" + listDatas.size());
//            //mAdapter.loadMoreData(listDatas);
//            //mAdapter.refreshData(listDatas);
//            mAdapter.clearData();
//            mAdapter.addData(listDatas);
//
//            mRecyclerview.setAdapter(mAdapter);
//        }


    }

    /**
     * 更新收货地址，必须更新到数据库，下次才会
     * @param address
     */
    private void upDateAddress(List<Address> listDatas,Address address) {

        //第一，把选中的那个数据移动到数据的第一位
        listDatas.remove(address);
        listDatas.add(0, address);

        //第二，把选中那个地址设置为true，其他的都设置为false
        for (int i = 0; i < listDatas.size(); i++) {
            if(i == 0){
                listDatas.get(i).setIsDefault(true);
            }else {
                listDatas.get(i).setIsDefault(false);
            }
        }
        mAdapter.notifyDataSetChanged();

        //第三，更新到数据库
        //TODO 第一，把数据库中，所有 "IsDefault" 的值都设置为false
        mDao.upDateIsDefaultToFalse();
        //TODO 第二，把其中那个选中的地址更新上
        mDao.upDateTheDefault(address.getId());
    }


    /**
     * 初始化Toolbar以及它的事件
     */
    private void initToolbarEvent() {
        mToolBar.setRightButtonIcon(R.drawable.icon_add);

        //点击返回注销本次Activity
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //去添加收货地址
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressListActivity.this, AddressAddActivity.class);
                startActivityForResult(intent, Contants.REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initAddress();
        //同时也要重新设置监听事件
        initAddressEvent();
    }


    
    
}
