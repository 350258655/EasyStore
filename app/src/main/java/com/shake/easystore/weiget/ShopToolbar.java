package com.shake.easystore.weiget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shake.easystore.R;


/**
 * Created by shake on 17-5-2.
 */
public class ShopToolbar extends Toolbar {


    private LayoutInflater mInflater;
    //自定义的Toolbar
    private View mView;

    private TextView mTextView;
    private EditText mSearchView;
    private Button mRightButton;

    public ShopToolbar(Context context) {
        this(context, null);
    }

    public ShopToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShopToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化布局
        initView();
        //设置左边距
        setContentInsetsRelative(10, 10);

        if (attrs != null) {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                    R.styleable.ShopToolbar, defStyleAttr, 0);

            //是否显示搜索框
            boolean isShowSearchView = a.getBoolean(R.styleable.ShopToolbar_isShowSearchView, false);
            //假如要显示搜索框，那就屏蔽标题，否则显示标题
            if (isShowSearchView) {
                showSearchView();
                hideTitleView();
            } else {
                showTitleView();
            }

            //获取右边按钮的文字
            CharSequence rightButtonText = a.getText(R.styleable.ShopToolbar_rightButtonText);
            if(rightButtonText !=null){
                setRightButtonText(rightButtonText);
            }


            //回收资源
            a.recycle();
        }

    }

    /**
     * 初始化布局
     */
    private void initView() {

        if (mView == null) {
            mInflater = LayoutInflater.from(getContext());
            mView = mInflater.inflate(R.layout.toolbar, null);

            mTextView = (TextView) mView.findViewById(R.id.toolbar_title);
            mSearchView = (EditText) mView.findViewById(R.id.toolbar_searchview);
            mRightButton = (Button) mView.findViewById(R.id.toolbar_rightButton);

            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
            //添加View
            addView(mView, lp);
        }
    }


    @Override
    public void setTitle(int resId) {
        setTitle(getContext().getText(resId));
    }


    @Override
    public void setTitle(CharSequence title) {

        /**
         * 子类构造器会默认先执行父类构造器的方法，在Toolbar的构造方法中，会执行 setText方法并对其赋值，但是现在在执行
         * setTitle 的时候，那些View什么的还没被初始化呢，所以这里应该先初始化View。也就是说，这里的逻辑的执行顺序，比本类
         * 的构造方法还早
         */
        initView();

        if (mTextView != null) {
            mTextView.setText(title);
            //让标题显示出来
            showTitleView();
        }

    }

    /**
     * 显示标题
     */
    public void showTitleView() {
        if (mTextView != null) {
            mTextView.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏标题
     */
    public void hideTitleView() {
        if (mTextView != null) {
            mTextView.setVisibility(GONE);
        }
    }


    /**
     * 显示搜索框
     */
    public void showSearchView() {
        if (mSearchView != null) {
            mSearchView.setVisibility(VISIBLE);
        }
    }


    /**
     * 隐藏搜索框
     */
    public void hideSearchView() {
        if (mSearchView != null) {
            mSearchView.setVisibility(GONE);
        }
    }

    /**
     * 获取右边的按钮
     *
     * @return
     */
    public Button getRightButton() {
        return mRightButton;
    }


    /**
     * 设置右边按钮的文字
     *
     * @param text
     */
    public void setRightButtonText(CharSequence text) {
        mRightButton.setText(text);
        mRightButton.setVisibility(VISIBLE);
    }


    /**
     * 设置右边按钮的图片样式
     *
     * @param icon
     */
    public void setRightButtonIcon(int icon) {
        setRightButtonIcon(getResources().getDrawable(icon));
    }

    /**
     * 设置右边按钮的图片样式
     *
     * @param icon
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setRightButtonIcon(Drawable icon) {

        if (mRightButton != null) {
            mRightButton.setBackground(icon);
            mRightButton.setVisibility(VISIBLE);
        }

    }

    /**
     * 设置 Toolbar 右边图标的监听事件
     *
     * @param li
     */
    public void setRightButtonOnClickListener(OnClickListener li) {
        mRightButton.setOnClickListener(li);
    }

}
