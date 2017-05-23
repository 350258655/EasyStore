package com.shake.easystore.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shake.easystore.AddressListActivity;
import com.shake.easystore.R;
import com.shake.easystore.bean.Address;
import com.shake.easystore.db.Dao;

import net.lemonsoft.lemonhello.LemonHello;
import net.lemonsoft.lemonhello.LemonHelloAction;
import net.lemonsoft.lemonhello.LemonHelloInfo;
import net.lemonsoft.lemonhello.LemonHelloView;
import net.lemonsoft.lemonhello.interfaces.LemonHelloActionDelegate;

import java.util.List;

/**
 * Created by shake on 17-5-18.
 */
public class AddressAdapter extends BaseAdapter<Address> {

    //各种回调监听接口
    private DefaultAddressListener mDefaultAddressListener;
    private EditOnClickListener mEditOnClickListener;
    private DeleteOnClickListener mDeleteOnClickListener;

    private Context mContext;


    public AddressAdapter(Context context, List<Address> datas) {
        super(context, R.layout.template_address, datas);
        this.mContext = context;
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
    private void deleteAddress(final Address address, final int position) {

        //删除的对话框
        LemonHello.getWarningHello("您确认删除这条数据吗？", "删除这条数据后会同时删除其关联的数据，并且无法撤销！")
                .addAction(new LemonHelloAction("取消", new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                        helloView.hide();
                    }
                }))
                .addAction(new LemonHelloAction("确定删除", Color.RED, new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                        helloView.hide();

                        //TODO 先删除ListDatas中的数据
                        mListDatas.remove(position);
                        notifyItemRemoved(position);
                        //TODO 再同步删除数据库中的数据
                        Dao dao = Dao.getDao(mContext);
                        dao.delete(address.getId());

                        if (mDeleteOnClickListener != null) {
                            //回调给外面，让外面的重新把第一个地址设置为默认地址
                            mDeleteOnClickListener.onClick(address, position);
                        }

                    }
                }))
                .show((AddressListActivity) mContext);

    }

    /**
     * 设置有关CheckBox的点击事件
     *
     * @param checkBox
     * @param address
     */
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
     * 设置删除的监听器
     */
    public interface DeleteOnClickListener {
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

    /**
     * 暴露删除按钮的监听器接口
     *
     * @param listener
     */
    public void setDeleteOnClickListener(DeleteOnClickListener listener) {
        this.mDeleteOnClickListener = listener;
    }


}
