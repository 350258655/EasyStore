//package com.shake.easystore.adapter;
//
//import android.content.Context;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.shake.easystore.R;
//import com.shake.easystore.bean.Wares;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
///**
// * Created by shake on 2017/5/7 0007.
// * 分类页面，二级菜单的Fragment
// */
//public class WaresAdapter extends BaseAdapter<com.shake.easystore.bean.Wares> {
//
//    private Context mContext;
//
//    public WaresAdapter(Context context, List<Wares> datas) {
//        super(context, R.layout.template_grid_wares, datas);
//        this.mContext = context;
//    }
//
//
//
//    @Override
//    public void bindData(BaseViewHolder holder, com.shake.easystore.bean.Wares wares, int position) {
//        ImageView imageView = holder.findView(R.id.imageView);
//        TextView textPrice = holder.findView(R.id.text_price);
//        TextView textTitle = holder.findView(R.id.text_title);
//
//
//        textPrice.setText("￥" + wares.getPrice());
//        textTitle.setText(wares.getName());
//        //加载图片
//        Picasso.with(mContext).load(wares.getImgUrl()).into(imageView);
//    }
//}
