package com.shake.easystore.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.shake.easystore.R;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.weiget.NumberAddSubView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shake on 17-5-9.
 * 购物车适配器
 */
public class CartAdapter extends BaseAdapter<ShoppingCart> {

    private Context mContext;

    public CartAdapter(Context context, List<ShoppingCart> datas) {
        super(context, R.layout.template_cart, datas);
        this.mContext = context;
    }

    @Override
    public void bindData(BaseViewHolder holder, ShoppingCart cart, int position) {

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
        checkbox_card.setChecked(true);
        numberAddSubView.setValue(cart.getCount());

        //设置点击事件
        numberAddSubView.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
            @Override
            public void onButtonAddClick(View view, int value) {

            }

            @Override
            public void onButtonSubClick(View view, int value) {

            }
        });

    }
}
