package com.shake.easystore;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.shake.easystore.bean.Address;
import com.shake.easystore.db.Dao;
import com.shake.easystore.utils.CommonUtils;
import com.shake.easystore.weiget.ClearEditText;
import com.shake.easystore.weiget.ShopToolbar;

import java.util.List;

public class AddressAddActivity extends Activity {
    @ViewInject(R.id.txt_address)
    private TextView mTxtAddress;

    @ViewInject(R.id.edittxt_consignee)
    private ClearEditText mEditConsignee;

    @ViewInject(R.id.edittxt_phone)
    private ClearEditText mEditPhone;

    @ViewInject(R.id.edittxt_add)
    private ClearEditText mEditAddr;

    @ViewInject(R.id.toolbar)
    private ShopToolbar mToolBar;

    //判断当前页面是不是编辑页面(或者是更新界面)
    private boolean isEdit;

    //要进行编辑的地址
    private Address mEditaddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        ViewUtils.inject(this);

        Bundle bundle = getIntent().getBundleExtra(Contants.BUNDLE_ADDRESS);
        if (bundle != null) {
            //假如是编辑页面，那就显示编辑页面的界面
            initEditView(bundle);
            isEdit = true;
        }

        //初始化事件
        initEvent();


    }

    /**
     * 初始化编辑页面的视图
     */
    private void initEditView(Bundle bundle) {

        mEditaddress = (Address) bundle.getSerializable(Contants.ADDRESS);
        if (mEditaddress != null) {
            mEditAddr.setText(mEditaddress.getDetailAddress());
            mEditConsignee.setText(mEditaddress.getName());
            mEditPhone.setText(mEditaddress.getPhone());
            mTxtAddress.setText(mEditaddress.getAddress());
        }

        mToolBar.setTitle("编辑");

    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        mTxtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示地址选择器
                showCityPick();
            }
        });

        //设置保存按钮
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存收货地址
                saveAddress();

            }
        });
    }


    /**
     * 保存收货地址
     */
    private void saveAddress() {
        Dao dao = Dao.getDao(AddressAddActivity.this);

        if (isEdit) {
            //保存编辑的收货地址
            saveEditAddress(dao);
        } else {
            //保存新增的收货地址
            saveInsertAddress(dao);
        }


    }

    /**
     * 保存编辑的收货地址
     *
     * @param dao
     *
     */
    private void saveEditAddress(Dao dao) {

        //更新 mEditaddress 的值
        mEditaddress.setAddress(mTxtAddress.getText().toString().trim());
        mEditaddress.setName(mEditConsignee.getText().toString().trim());
        mEditaddress.setPhone(mEditPhone.getText().toString().trim());
        mEditaddress.setDetailAddress(mEditPhone.getText().toString().trim());

        dao.update(mEditaddress, new Dao.OnCompleteListener() {
            @Override
            public void onDone(List<Address> list) {
                //回调数据
                setResult(Contants.REQUEST_EDIT_CODE);
                //结束这个Activity，回到"我的" 界面
                finish();
            }
        });
    }

    /**
     * 保存新增的收货地址
     */
    private void saveInsertAddress(Dao dao) {
        //获取信息
        final String name = mEditConsignee.getText().toString().trim();
        final String phone = mEditPhone.getText().toString().trim();
        final String detailAddress = mEditAddr.getText().toString().trim();
        final String address = mTxtAddress.getText().toString().trim();

        //检查信息
        if (address == null || address.equals("")) {
            Toast.makeText(AddressAddActivity.this, "请输入收货地址！", Toast.LENGTH_SHORT).show();
            return;
        }
        CommonUtils.checkPhoneNum(phone, "86", this);

        Address addressObject = new Address(name, address, phone, detailAddress, false);


        dao.add(addressObject, new Dao.OnCompleteListener() {
            @Override
            public void onDone(List<Address> list) {

                //回调数据
                setResult(Contants.REQUEST_CODE);
                //结束这个Activity，回到"我的" 界面
                finish();
            }
        });

    }


    /**
     * 显示地址选择器
     */
    private void showCityPick() {
        CityPicker cityPicker = new CityPicker.Builder(AddressAddActivity.this)
                .textSize(18)
                .title("地址选择")
                .backgroundPop(0xa0000000)
                .titleBackgroundColor("#234Dfa")
                .titleTextColor("#000000")
                .backgroundPop(0xa0000000)
                .confirTextColor("#000000")
                .cancelTextColor("#000000")
                .province("广东省")
                .city("广州市")
                .district("天河区")
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();


        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];

                Log.i("TAG", "选择的收货地址: " + province + "," + city + "," + district + "," + code);


                mTxtAddress.setText(province + "," + city + "," + district);
            }

            @Override
            public void onCancel() {
                Toast.makeText(AddressAddActivity.this, "已取消", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //回调数据
        setResult(Contants.REQUEST_CODE);
        //结束这个Activity，回到"我的" 界面
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
