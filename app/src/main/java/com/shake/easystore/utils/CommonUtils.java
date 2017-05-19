package com.shake.easystore.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shake on 17-5-18.
 * 普通的工具类
 */
public class CommonUtils {


    /**
     * 检查手机号码和区号等
     *
     * @param phone
     * @param code
     */
    public static void checkPhoneNum(String phone, String code,Context context) {

        if(code.startsWith("+")){
            code = code.substring(1);
        }

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(code == "86"){
            if(phone.length() != 11){
                Toast.makeText(context, "手机号码长度不对", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            Toast.makeText(context, "您输入的手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }

    }

}
