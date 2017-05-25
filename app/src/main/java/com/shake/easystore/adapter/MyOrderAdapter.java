package com.shake.easystore.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shake.easystore.R;
import com.shake.easystore.bean.ShoppingCart;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shake on 17-5-23.
 * 我的订单的Adapter
 */
public class MyOrderAdapter extends BaseAdapter<ShoppingCart> {

    public static final int STATUS_SUCCESS = 1; //支付成功的订单
    public static final int STATUS_PAY_FAIL = -2; //支付失败的订单
    public static final int STATUS_PAY_WAIT = 0; //：待支付的订单

    private int currentState = STATUS_SUCCESS; //当前的状态
    private Context mContext;

    private BuyListener mBuyListener;


    public MyOrderAdapter(Context context, List<ShoppingCart> datas) {
        super(context, R.layout.template_my_orders, datas);
        this.mContext = context;
    }

    @Override
    public void bindData(BaseViewHolder holder, final ShoppingCart cart, final int position) {

        //先进行一个判空处理，因为有可能没有数据
        if (mListDatas.size() <= 0 || mListDatas == null) {
            return;
        }

        //找到View
        ImageView imageView = holder.findView(R.id.drawee_view);
        TextView text_title = holder.findView(R.id.text_title);
        TextView text_order = holder.findView(R.id.text_order);
        Button btn_tobuy = holder.findView(R.id.btn_tobuy);

        //给View赋值
        Picasso.with(mContext).load(cart.getImgUrl()).into(imageView);
        text_title.setText(cart.getName());
        text_order.setText("订单号：" + cart.getOrderNum());
        if (currentState == STATUS_SUCCESS) {
            btn_tobuy.setText("再次购买");
        } else {
            btn_tobuy.setText("继续支付");
        }

        //设置事件
        btn_tobuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState == STATUS_SUCCESS) {
                    //TODO 再次购买的话，支付完成之后要回到本页面，然后数据应该加1
                    if(mBuyListener != null){
                        mBuyListener.onBuyAgain(cart,position);
                    }

                } else {
                    //TODO 继续支付的话，支付完成之后要回到本页面，然后数据减1，该数据会跑到支付完成的列表项中
                    if(mBuyListener != null){
                        mBuyListener.onBuyContinue(cart,position);
                    }
                }

            }
        });
    }

    /**
     * 设置当前状态，提供给外面调用
     *
     * @param state
     */
    public void setCurrentState(int state) {
        this.currentState = state;
    }


    /**
     * 再次购买 和 继续购买 的监听器
     */
    public interface BuyListener {
        void onBuyAgain(ShoppingCart cart, int position);

        void onBuyContinue(ShoppingCart cart, int position);
    }


    /**
     * 暴露接口
     *
     * @param listener
     */
    public void setBuyListener(BuyListener listener) {
        mBuyListener = listener;
    }


}
