package com.shake.easystore.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.shake.easystore.R;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.utils.CartProvider;
import com.shake.easystore.weiget.NumberAddSubView;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;

/**
 * Created by shake on 17-5-9.
 * 购物车适配器
 */
public class CartAdapter extends BaseAdapter<ShoppingCart> {

    private Context mContext;

    //Item数目改变的监听器
    private OnItemNumChangeListener mOnItemNumChangeListener;


    public CartAdapter(Context context, List<ShoppingCart> datas) {
        super(context, R.layout.template_cart, datas);
        this.mContext = context;
    }

    @Override
    public void bindData(BaseViewHolder holder, final ShoppingCart cart, int position) {

        //找到View
        TextView txt_card_price = holder.findView(R.id.txt_card_price);
        TextView txt_card_title = holder.findView(R.id.txt_card_title);
        ImageView imgview_card = holder.findView(R.id.imgview_card);
        CheckBox checkbox_card = holder.findView(R.id.checkbox_card);
        NumberAddSubView numberAddSubView = holder.findView(R.id.num_control);

        //给View赋值
        txt_card_title.setText(cart.getName());
        txt_card_price.setText("￥" + cart.getPrice());
        Picasso.with(mContext).load(cart.getImgUrl()).into(imgview_card);
        //看是否是被选中了
        checkbox_card.setChecked(cart.isChecked());
        numberAddSubView.setValue(cart.getCount());

        //设置点击事件
        numberAddSubView.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
            @Override
            public void onButtonClickResult(View view, int value) {
                //更新数量
                cart.setCount(value);
                //回调给Fragment
                mOnItemNumChangeListener.onResult(value,cart);
            }
        });

    }


    /**
     * 获取购物车商品的总价格
     */
    public float getTotalPrice() {

        float sum = 0;

        Log.i("TAG", "CartAdapter，getTotalPrice，获取总价格的时候数据的长度: " + mListDatas.size());
        if (mListDatas != null && mListDatas.size() > 0) {

            for (ShoppingCart shoppingCart : mListDatas) {
                //假如该项是被选中的，计算价格
                if (shoppingCart.isChecked()) {
                    sum = sum + (shoppingCart.getCount() * shoppingCart.getPrice());
                }
            }

        }

        Log.i("TAG", "CartAdapter，getTotalPrice，获取的总价格是: " + sum);
        return sum;
    }


    /**
     * 购物车商品全选或反选
     *
     * @param isChecked
     */
    public void checkAllOrNull(boolean isChecked) {
        if (mListDatas != null && mListDatas.size() > 0) {
            int i = 0;
            for (ShoppingCart listData : mListDatas) {
                //选择全选
                listData.setIsChecked(isChecked);

                //更新
                notifyItemChanged(i);
                i++;
            }
        }
    }


    /**
     * RecyclerView中每一项被触发之后应该做的事情，即点击之后CheckBox的改变
     *
     * @param position
     */
    public void itemClickAction(int position) {
        //获取被选中的那一项
        ShoppingCart cart = mListDatas.get(position);
        //CheckBox要反选
        cart.setIsChecked(!cart.isChecked());
        //刷新数据
        notifyItemChanged(position);
    }


    /**
     * 获取被选中项目的数量，是为了能动态改变那个全选的按钮，假如全都选中，那么全选应该选中。否则则不显示
     *
     * @return
     */
    public int getCheckNum() {

        int checkNum = 0;

        if (mListDatas != null) {

            for (ShoppingCart cart : mListDatas) {
                if (cart.isChecked()) {
                    checkNum = checkNum + 1;
                }
            }
        }
        return checkNum;
    }

    /**
     * 删除购物车中的item
     */
    public void deleteCard(){

        //先拿到操作储存的工具类
        CartProvider cartProvider = CartProvider.getInstance(mContext);

        //不能遍历List去执行删除操作，因为List的数目一改变就会乱。可以用迭代器
        if(mListDatas != null && mListDatas.size() > 0){
            //获取迭代器
            Iterator iterator = mListDatas.iterator();

            while (iterator.hasNext()){
                //获取那个Item
                ShoppingCart cart = (ShoppingCart) iterator.next();
                //判断这个Item是否被选中
                if(cart.isChecked()){
                    //拿到这个cart对应的位置
                    int position = mListDatas.indexOf(cart);
                    //删除这个cart
                    cartProvider.delete(cart);
                    //迭代器中也删除这个item
                    iterator.remove();
                    //更新数据
                    notifyItemRemoved(position);
                }
            }

        }

    }





    /**
     * 设置Item数目改变的监听事件
     */
    public interface OnItemNumChangeListener {
        void onResult(int value,ShoppingCart cart);
    }

    /**
     * 暴露项目数量改变的接口
     *
     * @param listener
     */
    public void setOnItemNumChangeListener(OnItemNumChangeListener listener) {
        this.mOnItemNumChangeListener = listener;
    }


}
