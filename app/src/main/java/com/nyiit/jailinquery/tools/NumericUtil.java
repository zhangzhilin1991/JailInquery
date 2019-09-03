package com.nyiit.jailinquery.tools;

import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumericUtil {

    private static final String TAG = NumericUtil.class.getName();

    /**
     * 从字符串中提取数字
     * @param text
     * @return
     */
    public static String extractNumeric(String text) {
        //正则表达式，用于匹配非数字串，+号用于匹配出多个非数字串
            String regEx="[^0-9]+";
            Pattern pattern = Pattern.compile(regEx);
            Matcher m = pattern.matcher(text);
            //用定义好的正则表达式拆分字符串，，把字符串中的数字留出来
            //String[] cs = pattern.split(text);
            //System.out.println(Arrays.toString(cs));
            //String num = cs.toString();
            //LogUtil.d(TAG, "id: " + num);
            //if (cs == null || cs.length == 0) {
            //    return null;
            //}
            String num = m.replaceAll("").trim();
            //LogUtil.d(TAG, "num: " + num);
            return num;
    }

    public static String extractNumericFormText(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }

        char[] wz = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九', 'x'};
        char[] sz = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'X'};

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< text.length(); i++) {
            for (int j= 0; j < wz.length; j++) {
                if (text.charAt(i) == wz[j]) {
                    builder.append(sz[j]);
                }
            }
        }
        return builder.toString();
    }
}
