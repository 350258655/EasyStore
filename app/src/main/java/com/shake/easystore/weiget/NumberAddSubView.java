package com.shake.easystore.weiget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shake.easystore.R;


/**
 * Created by shake on 17-5-9.
 * 购物车加减View
 */
public class NumberAddSubView extends LinearLayout implements View.OnClickListener {

    private LayoutInflater mLayoutInflater;
    private Button btnadd;
    private TextView textnum;
    private Button btnsub;

    //TextView的值
    private int mValue;
    //最小值
    private int minValue;
    //最大值
    private int maxValue;

    //回调接口实例
    private OnButtonClickListener mOnButtonClickListener;

    public NumberAddSubView(Context context) {
        this(context, null);
    }

    public NumberAddSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mLayoutInflater = LayoutInflater.from(context);
        //初始化Views
        initViews();
        //初始化自定义属性
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberAddSubView);

            //获取控件的值
            mValue = typedArray.getInt(R.styleable.NumberAddSubView_value, 0);
            //设置Value
            setValue(mValue);

            //获取最大值
            maxValue = typedArray.getInt(R.styleable.NumberAddSubView_maxValue, 0);
            //设置最大值
            setMaxValue(maxValue);

            //获取最小值
            minValue = typedArray.getInt(R.styleable.NumberAddSubView_minValue, 0);
            //设置最小值
            setMinValue(minValue);

            //获取添加按钮的样式
            Drawable drawableBtnAdd = typedArray.getDrawable(R.styleable.NumberAddSubView_btnAddBackground);
            if (drawableBtnAdd != null) {
                setButtonAddBackground(drawableBtnAdd);
            }

            //获取减按钮样式
            Drawable drawableBtnSub = typedArray.getDrawable(R.styleable.NumberAddSubView_btnSubBackground);
            if (drawableBtnSub != null) {
                setButtonSubBackgroud(drawableBtnSub);
            }

            //获取TextView样式
            Drawable drawableTextView = typedArray.getDrawable(R.styleable.NumberAddSubView_textViewBackground);
            if (drawableTextView != null) {
                setTexViewtBackground(drawableTextView);
            }

            //关闭资源
            typedArray.recycle();
        }

    }


    private void initViews() {

        View view = mLayoutInflater.inflate(R.layout.add_sub_view, this, true);
        this.btnsub = (Button) view.findViewById(R.id.btn_sub);
        this.textnum = (TextView) view.findViewById(R.id.text_num);
        this.btnadd = (Button) view.findViewById(R.id.btn_add);
        btnadd.setOnClickListener(this);
        btnsub.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            addNum();

        }else if(v.getId() == R.id.btn_sub){
            subNum();
        }
        if(mOnButtonClickListener != null){
            mOnButtonClickListener.onButtonClickResult(v,mValue);
        }
    }

    /**
     * 减少商品数量
     */
    private void subNum() {
        if(mValue > minValue){
            mValue = mValue - 1;
        }
        textnum.setText(mValue+"");
    }


    /**
     * 增加商品数量
     */
    private void addNum() {
        if (mValue < maxValue) {
            mValue = mValue + 1;
        }
        textnum.setText(mValue + "");
    }


    /**
     * 设置TextView的值
     *
     * @param value
     */
    public void setValue(int value) {
        textnum.setText(value + "");
        mValue = value;
    }

    /**
     * 设置最大值
     *
     * @param maxValue
     */
    private void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * 设置最小值
     *
     * @param minValue
     */
    private void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * 设置添加按钮的样式
     *
     * @param drawableBtnAdd
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setButtonAddBackground(Drawable drawableBtnAdd) {
        btnadd.setBackground(drawableBtnAdd);
    }

    /**
     * 设置减按钮的样式
     *
     * @param drawableBtnSub
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setButtonSubBackgroud(Drawable drawableBtnSub) {
        btnsub.setBackground(drawableBtnSub);
    }

    /**
     * 设置TextView的样式
     *
     * @param drawableTextView
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setTexViewtBackground(Drawable drawableTextView) {
        textnum.setBackground(drawableTextView);
    }


    /**
     * 按钮点击的回调接口
     */
    public interface OnButtonClickListener {
        void onButtonClickResult(View view, int value);
    }

    /**
     * 暴露接口
     *
     * @param listener
     */
    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.mOnButtonClickListener = listener;
    }


}
