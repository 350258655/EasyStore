package com.shake.easystore.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.shake.easystore.R;
import com.shake.easystore.bean.ShoppingCart;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shake on 17-5-18.
 * 订单RecycleView的适配器
 */
public class WareOrderAdapter extends BaseAdapter<ShoppingCart> {

    private Context mContext;


    public WareOrderAdapter(Context context, List<ShoppingCart> datas) {
        super(context, R.layout.template_order_wares, datas);
        this.mContext = context;
    }

    @Override
    public void bindData(BaseViewHolder holder, ShoppingCart cart, int position) {
        //找到图片
        ImageView imageView = holder.findView(R.id.drawee_view);

        //加载图片
        Picasso.with(mContext).load(cart.getImgUrl()).into(imageView);
    }


    /**
     * 获取商品总价格
     *
     * @return
     */
    public float getTotalPrice() {
        float sum = 0;
        if (mListDatas == null || mListDatas.size() <= 0) {
            return sum;
        }

        for (ShoppingCart cart : mListDatas) {
            sum += cart.getCount() * cart.getPrice();
        }
        return sum;
    }
}
