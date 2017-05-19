package com.shake.easystore.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shake.easystore.R;
import com.shake.easystore.bean.Address;

import java.util.List;

/**
 * Created by shake on 17-5-18.
 */
public class AddressAdapter extends BaseAdapter<Address> {

    //各种回调监听接口
    private DefaultAddressListener mDefaultAddressListener;
    private EditOnClickListener mEditOnClickListener;


    public AddressAdapter(Context context, List<Address> datas) {
        super(context, R.layout.template_address, datas);
    }

    @Override
    public void bindData(BaseViewHolder holder, final Address address, final int position) {

        //找到控件
        TextView txt_name = holder.findView(R.id.txt_name);
        TextView txt_phone = holder.findView(R.id.txt_phone);
        TextView txt_address = holder.findView(R.id.txt_address);
        TextView txt_edit = holder.findView(R.id.txt_edit);
        TextView txt_del = holder.findView(R.id.txt_del);
        CheckBox checkBox = holder.findView(R.id.cb_is_defualt);

        Log.i("TAG", "AddressAdapter，bindData，获得地址数据是：" + address.toString());

        if (address == null) {
            Log.i("TAG", "AddressAdapter，bindData: 获得的地址是空的");
            return;
        }

        //给控件设值
        txt_name.setText(address.getName());
        txt_phone.setText(address.getPhone());
        txt_address.setText(address.getAddress());


        //设置事件
        //设置有关CheckBox的点击事件
        setDefaultCheck(checkBox, address);
        //设置编辑的监听事件
        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditOnClickListener.onClick(address, position);
            }
        });

        //设置删除按钮的监听事件
        txt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除收货地址
                deleteAddress(address, position);

            }
        });


    }

    /**
     * 删除收货地址
     *
     * @param address
     * @param position
     */
    private void deleteAddress(Address address, int position) {

        //TODO 先删除ListDatas中的数据
        //TODO 再同步删除数据库中的数据



    }

    private void setDefaultCheck(CheckBox checkBox, final Address address) {
        //看该地址是否是默认地址
        boolean isDefault = address.getIsDefault();
        checkBox.setChecked(isDefault);

        if (isDefault) {
            checkBox.setText("默认地址");
            checkBox.setClickable(false);
        } else {

            //当状态是未选中的情况下才允许点击，点击之后要回调给外面去更改状态，所以需要一个监听器把状态回调给外面
            checkBox.setClickable(true);
            //状态是未选中
            checkBox.setChecked(false);

            //CheckBox在未选中的情况下，点击监听事件，因为未选中的时候，点击就会变成选中的
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //假如点击之后被选中了
                    if (isChecked && mDefaultAddressListener != null) {
                        //回调给外面
                        mDefaultAddressListener.setDefault(address);
                    }

                }
            });


        }
    }


    /**
     * 设置默认地址的监听器
     */
    public interface DefaultAddressListener {
        void setDefault(Address address);
    }


    /**
     * 设置编辑的监听器
     */
    public interface EditOnClickListener {
        void onClick(Address address, int position);
    }


    /**
     * 暴露默认地址的监听器接口
     *
     * @param listener
     */
    public void setDefaultAddressListener(DefaultAddressListener listener) {
        this.mDefaultAddressListener = listener;
    }

    /**
     * 暴露编辑按钮的监听器接口
     *
     * @param listener
     */
    public void setEditOnClickListener(EditOnClickListener listener) {
        this.mEditOnClickListener = listener;
    }


}
