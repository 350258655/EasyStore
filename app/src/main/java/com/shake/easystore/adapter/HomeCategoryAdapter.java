package com.shake.easystore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shake.easystore.R;
import com.shake.easystore.bean.Campaign;
import com.shake.easystore.bean.HomeCampaign;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shake on 17-5-4.
 * 首页RecycleView的适配器。实现步骤
 * 1、先确定好每一项的布局的元素，即封装好ViewHolder(确定元素)
 * 2、在 onCreateViewHolder 回调方法中，实例化ViewHolder，即给它具体的布局(确定布局)
 * 3、在 onBindViewHolder 中，将布局中的元素和具体数据进行绑定(绑定数据)
 */
public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    private static int VIEW_TYPE_L = 0;
    private static int VIEW_TYPE_R = 1;

    private LayoutInflater mInflater;

    private List<HomeCampaign> mDatas;

    private Context mContext;

    /**
     * 回调接口的设置。第二步，创建接口实例
     */
    private OnCampClickListener mListener;

    public HomeCategoryAdapter(List<HomeCampaign> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }


    /**
     * 创建ViewHolder,并且return出去
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mInflater = LayoutInflater.from(parent.getContext());
        if(viewType == VIEW_TYPE_R){

            return  new ViewHolder(mInflater.inflate(R.layout.template_home_cardview2,parent,false));
        }

        return  new ViewHolder(mInflater.inflate(R.layout.template_home_cardview,parent,false));
    }


    /**
     * 绑定ViewHolder，即将数据和ViewHolder上的控件绑定
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //获取并绑定数据
        HomeCampaign campaign = mDatas.get(position);

        holder.textTitle.setText(campaign.getTitle());

        //去加载图片
        Picasso.with(mContext).load(campaign.getCpOne().getImgUrl()).into(holder.imageViewBig);
        Picasso.with(mContext).load(campaign.getCpTwo().getImgUrl()).into(holder.imageViewSmallTop);
        Picasso.with(mContext).load(campaign.getCpThree().getImgUrl()).into(holder.imageViewSmallBottom);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 重写ViewType
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {

        if(position % 2==0){
            return  VIEW_TYPE_R;
        }
        else return VIEW_TYPE_L;
    }

    /**
     * 一个ViewHolder。其实就是每一项的封装吧
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textTitle;
        ImageView imageViewBig;
        ImageView imageViewSmallTop;
        ImageView imageViewSmallBottom;

        public ViewHolder(View itemView) {
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            imageViewBig = (ImageView) itemView.findViewById(R.id.imgview_big);
            imageViewSmallTop = (ImageView) itemView.findViewById(R.id.imgview_small_top);
            imageViewSmallBottom = (ImageView) itemView.findViewById(R.id.imgview_small_bottom);

            //这几个View都要设置点击事件
            imageViewBig.setOnClickListener(this);
            imageViewSmallTop.setOnClickListener(this);
            imageViewSmallBottom.setOnClickListener(this);

        }

        /**
         * 假如是点击了大图片，就回调大图片bean给外面；假如是点击了小图片，就回调小图片bean给外面
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            //获取被点击的那一整个部位
            HomeCampaign homeCampaign = mDatas.get(getLayoutPosition());

            //回调各个部位给外面
            if(mListener != null){
                switch (v.getId()) {
                    case R.id.imgview_big:
                        mListener.onClick(v,homeCampaign.getCpOne());
                        break;

                    case R.id.imgview_small_top:
                        mListener.onClick(v,homeCampaign.getCpTwo());
                        break;

                    case R.id.imgview_small_bottom:
                        mListener.onClick(v,homeCampaign.getCpThree());
                        break;
                }
            }

        }
    }

    /**
     * 回调接口的设置。第一步，创建回调接口和方法，因为需要辨别出具体是哪个 Campaign 的操作，所以需要回调给外面知道
     */
    public interface OnCampClickListener {
        void onClick(View view, Campaign campaign);
    }

    /**
     * 回调接口的设置。第三步，暴露接口给外面调用
     *
     * @param listener
     */
    public void setOnCampClickListener(OnCampClickListener listener) {
        this.mListener = listener;
    }


}
