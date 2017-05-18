package com.shake.easystore.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.shake.easystore.R;

/**
 * Created by shake on 17-5-17.
 * 倒计时的View
 */
public class CountTimerView extends CountDownTimer {

    public static final int TIME_COUNT = 61000;//时间防止从59s开始显示（以倒计时60s为例子）
    private TextView btn;
    private int endStrRid;

    /**
     * @param millisInFuture    倒计时总时间（如60S，120s等）
     * @param countDownInterval 渐变时间（每次倒计1s）
     */
    public CountTimerView(long millisInFuture, long countDownInterval, TextView btn, int endStrRid) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
        this.endStrRid = endStrRid;
    }

    public CountTimerView(TextView btn, int endStrRid) {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = endStrRid;
    }

    public CountTimerView(TextView btn) {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = R.string.smssdk_resend_identify_code;
    }


    /**
     * 计时过程显示
     *
     * @param millisUntilFinished
     */
    @Override
    public void onTick(long millisUntilFinished) {
        btn.setEnabled(false);
        btn.setText(millisUntilFinished / 1000 + " 秒后可重新发送");
    }


    /**
     * 计时完成的时候触发
     */
    @Override
    public void onFinish() {
        btn.setText(endStrRid);
        btn.setEnabled(true);
    }
}
