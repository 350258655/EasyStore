package com.shake.easystore.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shake.easystore.R;
import com.shake.easystore.bean.ShoppingCart;
import com.shake.easystore.bean.Wares;
import com.shake.easystore.utils.CartProvider;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shake on 17-5-11.
 * 优化后热点Fragment的适配器
 * 和老的Fragment吐司的内容不一样，所以可以根据吐司看在项目中是使用了那个Fragment
 */
public class HotspotAdapter extends BaseAdapter<Wares> {


    CartProvider mCartProvider;

    private Context mContext;

    public HotspotAdapter(Context context, List<Wares> datas) {
        super(context, R.layout.template_hot_wares,datas);
        this.mContext = context;
        mCartProvider = CartProvider.getInstance(mContext);
    }

    @Override
    public void bindData(BaseViewHolder holder, final Wares wares, int position) {
        //找到View
        TextView textTitle = holder.findView(R.id.text_title);
        TextView textPrice = holder.findView(R.id.text_price);
        ImageView mImageView = holder.findView(R.id.drawee_view);
        Button btn_tobuy = holder.findView(R.id.btn_tobuy);


        //给View设置值
        textTitle.setText(wares.getName());
        textPrice.setText("￥" + wares.getPrice());
        //用Picasso加载图片
        Picasso.with(mContext).load(wares.getImgUrl()).into(mImageView);

        /**
         * 因为商品列表在GridView显示状态的时候，那个布局中不存在这个按钮，所以得进行判空处理
         */
        if(btn_tobuy != null){
            //设置为可见
            btn_tobuy.setVisibility(View.VISIBLE);
            //设置点击事件
            btn_tobuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取购物车数据
                    ShoppingCart cart = getShoppingCartData(wares);
                    mCartProvider.put(cart);
                    Toast.makeText(mContext, "已经添加到购物车！", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }


    /**
     * 获取ShoppingCart。因为父类没法强转成子类，所以只能一个个set
     *
     * @param wares
     * @return
     */
    public ShoppingCart getShoppingCartData(Wares wares) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(wares.getId());
        cart.setDescription(wares.getDescription());
        cart.setImgUrl(wares.getImgUrl());
        cart.setName(wares.getName());
        cart.setPrice(wares.getPrice());
        return cart;
    }

    /**
     * 更新布局文件，当在商品列表中，切换显示方式的时候可以用到
     * @param layoutId
     */
    public void refreshLayout(int layoutId){
        this.mLayoutResId = layoutId;
        //更新
        //notifyItemRangeChanged(0,getDatas().size());
        //TODO 这里有点bug，切换的时候会出现一些奇奇怪怪的布局
        notifyDataSetChanged();
    }

}
